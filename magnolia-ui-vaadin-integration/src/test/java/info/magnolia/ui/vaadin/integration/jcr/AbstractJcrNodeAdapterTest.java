/**
 * This file Copyright (c) 2012 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This file is dual-licensed under both the Magnolia
 * Network Agreement and the GNU General Public License.
 * You may elect to use one or the other of these licenses.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or MNA you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the Magnolia Network Agreement (MNA), this file
 * and the accompanying materials are made available under the
 * terms of the MNA which accompanies this distribution, and
 * is available at http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.ui.vaadin.integration.jcr;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import info.magnolia.context.MgnlContext;
import info.magnolia.test.mock.MockContext;
import info.magnolia.test.mock.jcr.MockNode;
import info.magnolia.test.mock.jcr.MockSession;

import java.util.Collection;

import javax.jcr.Node;
import javax.jcr.PropertyType;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.vaadin.data.Property;


public class AbstractJcrNodeAdapterTest {

    private static final String WORKSPACE_NAME = "workspace";

    private MockSession session;

    @Before
    public void setUp() {
        session = new MockSession(WORKSPACE_NAME);
        MockContext ctx = new MockContext();
        ctx.addSession(WORKSPACE_NAME, session);
        MgnlContext.setInstance(ctx);
    }

    @After
    public void tearDown() {
        MgnlContext.setInstance(null);
    }

    @Test
    public void testSetCommonAttributes() throws Exception {
        // GIVEN
        String nodeName = "nodeName";
        Node testNode = session.getRootNode().addNode(nodeName);

        // WHEN
        DummyJcrNodeAdapter adapter = new DummyJcrNodeAdapter(testNode);

        // THEN
        assertEquals(testNode.getIdentifier(), adapter.getNodeIdentifier());
        assertEquals(testNode.getIdentifier(), ((Node) adapter.getJcrItem()).getIdentifier());
        assertEquals(testNode.getPrimaryNodeType().getName(), adapter.getPrimaryNodeTypeName());
    }

    @Test
    public void testGetItemProperty_ExistingProperty() throws Exception {
        // GIVEN
        final Node underlyingNode = session.getRootNode().addNode("underlying");
        final String propertyName = "TEST";
        final String propertyValue = "value";
        underlyingNode.setProperty(propertyName, propertyValue);
        final DummyJcrNodeAdapter item = new DummyJcrNodeAdapter(underlyingNode);

        // WHEN
        final Property prop = item.getItemProperty(propertyName);

        // THEN
        assertEquals(propertyValue, prop.getValue());

    }

    @Test
    public void testGetItemProperty_NewProperty() throws Exception {
        // GIVEN
        final Node underlyingNode = session.getRootNode().addNode("underlying");
        final String propertyName = "TEST";
        final String propertyValue = "value";
        underlyingNode.setProperty(propertyName, propertyValue);
        final DummyJcrNodeAdapter item = new DummyJcrNodeAdapter(underlyingNode);

        // WHEN
        final Property prop = item.getItemProperty(propertyName + "_1");

        // THEN
        assertEquals(true, prop == null);
    }

    @Test
    public void testGetItemProperty_NewProperty_Add() throws Exception {
        // GIVEN
        final Node underlyingNode = session.getRootNode().addNode("underlying");
        final String propertyName = "TEST";
        final String propertyValue = "value";
        final DummyJcrNodeAdapter item = new DummyJcrNodeAdapter(underlyingNode);
        Property property = DefaultPropertyUtil.newDefaultProperty(propertyName, PropertyType.TYPENAME_STRING, propertyValue);
        item.addItemProperty(propertyName, property);

        // WHEN
        final Property prop = item.getItemProperty(propertyName);

        // THEN
        assertEquals(propertyValue, prop.getValue().toString());
    }

    @Test
    public void testValueChangeEvent_PropertyExist() throws Exception {
        // GIVEN
        Node underlyingNode = session.getRootNode().addNode("underlying");
        String propertyName = "TEST";
        String propertyValue = "value";
        javax.jcr.Property jcrProperty = underlyingNode.setProperty(propertyName, propertyValue);
        DummyJcrNodeAdapter item = new DummyJcrNodeAdapter(underlyingNode);

        // WHEN
        Property nodeProperty = item.getItemProperty(propertyName);
        nodeProperty.setValue("newValue");

        // THEN
        assertFalse(item.getChangedProperties().isEmpty());
        assertTrue(item.getChangedProperties().containsKey(propertyName));
        assertEquals(nodeProperty, item.getChangedProperties().get(propertyName));
        assertEquals("newValue", item.getChangedProperties().get(propertyName).getValue());
    }

    @Test
    public void testValueChangeEvent_PropertyDoNotExist() throws Exception {
        // GIVEN
        Node underlyingNode = session.getRootNode().addNode("underlying");
        String propertyName = "TEST";
        DummyJcrNodeAdapter item = new DummyJcrNodeAdapter(underlyingNode);

        // WHEN
        Property itemProperty = DefaultPropertyUtil.newDefaultProperty(propertyName, PropertyType.TYPENAME_STRING, propertyName);
        item.addItemProperty(propertyName, itemProperty);
        itemProperty.setValue("newValue");

        // THEN
        assertFalse(item.getChangedProperties().isEmpty());
        assertTrue(item.getChangedProperties().containsKey(propertyName));
        assertEquals(itemProperty, item.getChangedProperties().get(propertyName));
        assertEquals("newValue", item.getChangedProperties().get(propertyName).getValue());
    }

    @Test
    public void testUpdateProperties() throws Exception {
        // GIVEN
        final Node underlyingNode = session.getRootNode().addNode("underlying");
        final String propertyName = "TEST";
        final String propertyValue = "value";
        final DummyJcrNodeAdapter item = new DummyJcrNodeAdapter(underlyingNode);
        Property property = DefaultPropertyUtil.newDefaultProperty(propertyName, PropertyType.TYPENAME_STRING, propertyValue);
        item.getChangedProperties().put(propertyName, property);

        // WHEN
        item.updateProperties(underlyingNode);

        // THEN
        assertEquals(propertyValue, underlyingNode.getProperty(propertyName).getString());
    }

    @Test
    public void testUpdateProperties_JcrName_Existing() throws Exception {

        // spy hooks for session move
        final MockSession session = spy(this.session);
        final Node root = new MockNode(session);
        doReturn(root).when(session).getRootNode();

        MockContext ctx = (MockContext) MgnlContext.getInstance();
        ctx.addSession(WORKSPACE_NAME, session);

        // mocking rename operation
        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String srcAbsPath = (String) invocation.getArguments()[0];
                String dstAbsPath = (String) invocation.getArguments()[1];
                session.removeItem(srcAbsPath);
                String dstRelPath = StringUtils.substringAfter(dstAbsPath, root.getPath());
                root.addNode(dstRelPath);
                return null;
            }
        }).when(session).move(anyString(), anyString());

        // GIVEN
        String existingName = "existingName";
        String subNodeName = "subNode";
        Node node = session.getRootNode();
        node.setProperty(existingName, "42");
        Node subNode = node.addNode(subNodeName);
        long nodeCount = node.getNodes().getSize();
        long propertyCount = node.getProperties().getSize();
        DummyJcrNodeAdapter adapter = new DummyJcrNodeAdapter(subNode);

        // WHEN
        adapter.getItemProperty(JcrItemAdapter.JCR_NAME).setValue(existingName);
        adapter.updateProperties();

        // THEN
        assertTrue(node.hasProperty(existingName));
        assertFalse(node.hasNode(existingName));
        assertFalse(node.hasNode(subNodeName));
        assertEquals(nodeCount, node.getNodes().getSize());
        assertEquals(propertyCount, node.getProperties().getSize());
    }

    /**
     * Dummy implementation of the Abstract class.
     */
    public class DummyJcrNodeAdapter extends AbstractJcrNodeAdapter {

        public DummyJcrNodeAdapter(Node jcrNode) {
            super(jcrNode);
        }

        @Override
        public Collection< ? > getItemPropertyIds() {
            return null;
        }

        @Override
        public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
            return false;
        }

    }

}
