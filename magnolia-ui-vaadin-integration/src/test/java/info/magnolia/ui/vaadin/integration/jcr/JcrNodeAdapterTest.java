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

import info.magnolia.context.MgnlContext;
import info.magnolia.test.mock.MockContext;
import info.magnolia.test.mock.jcr.MockSession;

import javax.jcr.Node;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;

/**
 * Main test class for {@link JcrNodeAdapter}.
 */
public class JcrNodeAdapterTest {

    private final String workspaceName = "workspace";
    private final String nodeName = "node";
    private final String propertyName = "property";
    private final String propertyValue = "value";
    private final String modified = "_modified";
    private MockSession session;

    @Before
    public void setUp() {
        session = new MockSession(workspaceName);
        MockContext ctx = new MockContext();
        ctx.addSession(workspaceName, session);
        MgnlContext.setInstance(ctx);
    }

    @After
    public void tearDown() {
        MgnlContext.setInstance(null);
    }

    @Test
    public void testAddItemProperty() throws Exception {
        // GIVEN
        final Node underlyingNode = session.getRootNode().addNode("underlying");
        DefaultProperty property = new DefaultProperty(propertyName, propertyValue);
        final JcrNodeAdapter item = new JcrNodeAdapter(underlyingNode);

        // WHEN
        final boolean b = item.addItemProperty(propertyName, property);

        // THEN
        assertEquals(true, b);
        assertEquals(property.getValue().toString(), item.getItemProperty(propertyName).getValue().toString());
    }

    @Test
    public void testGetItemProperty_New() throws Exception {
        // GIVEN
        Node node = session.getRootNode().addNode(nodeName);
        JcrNodeAdapter adapter = new JcrNodeAdapter(node);

        // WHEN
        Property property = adapter.getItemProperty(propertyName);

        // THEN
        assertEquals(null, property);
    }

    @Test
    public void testGetItemProperty_NewCreate() throws Exception {
        // GIVEN
        Node node = session.getRootNode().addNode(nodeName);
        JcrNodeAdapter adapter = new JcrNodeAdapter(node);

        // WHEN
        Property property = DefaultPropertyUtil.newDefaultProperty(propertyName, null, "");
        adapter.addItemProperty(propertyName, property);

        // THEN
        assertEquals("", property.getValue().toString());
        assertSame(property, adapter.getItemProperty(propertyName));
    }

    @Test
    public void testGetItemProperty_Existing() throws Exception {
        // GIVEN
        Node node = session.getRootNode().addNode(nodeName);
        node.setProperty(propertyName, propertyValue);

        JcrNodeAdapter adapter = new JcrNodeAdapter(node);

        // WHEN
        Property property = adapter.getItemProperty(propertyName);

        // THEN
        assertEquals(propertyValue, property.getValue());
        assertNotSame(property, adapter.getItemProperty(propertyName));
    }

    @Test
    public void testGetItemProperty_Modified() throws Exception {
        // GIVEN
        Node node = session.getRootNode().addNode(nodeName);
        JcrNodeAdapter adapter = new JcrNodeAdapter(node);
        // Get and modify property
        Property propertyInitial = DefaultPropertyUtil.newDefaultProperty(propertyName, null, propertyValue);
        adapter.addItemProperty(propertyName, propertyInitial);

        // WHEN get property again
        Property property = adapter.getItemProperty(propertyName);

        // THEN
        assertEquals(propertyValue, property.getValue());
        // Should be the same property
        assertSame(property, propertyInitial);
    }

    @Test
    public void testProperty_ListenerUnique() throws Exception {
        // GIVEN
        Node node = session.getRootNode().addNode(nodeName);
        node.setProperty(propertyName, propertyValue);
        JcrNodeAdapter adapter = new JcrNodeAdapter(node);
        // Get property: add listener
        DefaultProperty propertyInitial = (DefaultProperty) adapter.getItemProperty(propertyName);
        // Modify property --> add listener
        propertyInitial.setValue(propertyValue);

        // WHEN Modify property --> add listener
        propertyInitial.setValue(propertyValue);

        // THEN
        assertEquals(1, propertyInitial.getListeners(ValueChangeEvent.class).size());
        assertEquals(true, propertyInitial.getListeners(ValueChangeEvent.class).contains(adapter));
    }

    @Test
    public void testProperty_ListenerUniqueCreated() throws Exception {
        // GIVEN
        Node node = session.getRootNode().addNode(nodeName);
        JcrNodeAdapter adapter = new JcrNodeAdapter(node);
        // Get property: add listener
        DefaultProperty propertyInitial = DefaultPropertyUtil.newDefaultProperty(propertyName, null, propertyValue);
        adapter.addItemProperty(propertyName, propertyInitial);
        // Modify property --> add listener
        propertyInitial.setValue(propertyValue);

        // WHEN Modify property --> add listener
        propertyInitial.setValue(propertyValue);

        // THEN
        assertEquals(1, propertyInitial.getListeners(ValueChangeEvent.class).size());
        assertEquals(true, propertyInitial.getListeners(ValueChangeEvent.class).contains(adapter));
    }

    @Test
    public void testRemoveItemProperty_New_NotModified() throws Exception {
        // GIVEN
        Node node = session.getRootNode().addNode(nodeName);

        JcrNodeAdapter adapter = new JcrNodeAdapter(node);
        // Create a new property
        DefaultProperty property = DefaultPropertyUtil.newDefaultProperty(propertyName, null, "");
        adapter.addItemProperty(propertyName, property);
        assertNotNull(property);

        // WHEN
        // Remove the property --> boolean true.
        boolean res = adapter.removeItemProperty(propertyName);

        // THEN
        assertEquals(true, res);
        assertEquals(0, adapter.getItemPropertyIds().size());
    }

    @Test
    public void testRemoveItemProperty_New_Modified() throws Exception {
        // GIVEN
        Node node = session.getRootNode().addNode(nodeName);
        node.setProperty(propertyName, "");
        JcrNodeAdapter adapter = new JcrNodeAdapter(node);
        // Create a new property
        DefaultProperty property = (DefaultProperty) adapter.getItemProperty(propertyName);
        property.setValue("newValue");
        assertNotNull(property);

        // WHEN
        // Remove the property --> boolean true.
        boolean res = adapter.removeItemProperty(propertyName);

        // THEN
        assertEquals(true, res);
        assertEquals(0, adapter.getItemPropertyIds().size());
    }

    @Test
    public void testRemoveItemProperty_Modified() throws Exception {
        // GIVEN
        Node node = session.getRootNode().addNode(nodeName);
        node.setProperty(propertyName, propertyValue);
        JcrNodeAdapter adapter = new JcrNodeAdapter(node);
        // Create a new property
        DefaultProperty property = (DefaultProperty) adapter.getItemProperty(propertyName);
        assertNotNull(property);
        // Modify the new property
        property.setValue(propertyValue);
        assertEquals(propertyValue, ((DefaultProperty) adapter.getItemProperty(propertyName)).getValue().toString());

        // WHEN
        // Remove the property --> boolean true.
        boolean res = adapter.removeItemProperty(propertyName);

        // THEN
        assertEquals(true, res);
        assertEquals(0, adapter.getItemPropertyIds().size());
    }

    @Test
    public void testRemoveItemProperty_Existing() throws Exception {
        // GIVEN
        // Create a empty node
        String nodeName = "nodeName";
        String id = "propertyName";
        String value = "value";
        Node node = session.getRootNode().addNode(nodeName);
        // Add a property (JCR)
        node.setProperty(id, value);
        JcrNodeAdapter adapter = new JcrNodeAdapter(node);
        assertEquals(true, adapter.getNode().hasProperty(id));

        // WHEN
        // Remove the property --> boolean true.
        boolean res = adapter.removeItemProperty(id);

        // THEN
        assertEquals(true, res);
        assertEquals(false, adapter.getNode().hasProperty(id));
    }

    @Test
    public void testRemoveItemProperty_NonExisting() throws Exception {
        // GIVEN
        // Create a empty node
        String nodeName = "nodeName";
        String id = "propertyName";
        String value = "value";
        Node node = session.getRootNode().addNode(nodeName);
        // Add a property (JCR)
        node.setProperty(id, value);
        JcrNodeAdapter adapter = new JcrNodeAdapter(node);
        assertEquals(true, adapter.getNode().hasProperty(id));

        // WHEN
        // Remove the property --> boolean true.
        boolean res = adapter.removeItemProperty(id + "_1");

        // THEN
        assertEquals(false, res);
        assertEquals(true, adapter.getNode().hasProperty(id));
    }

    @Test
    public void testGetNode_NewProperty() throws Exception {
        // GIVEN
        Node node = session.getRootNode().addNode(nodeName);
        JcrNodeAdapter adapter = new JcrNodeAdapter(node);
        // Add a new property (Vaadin)
        Property property = DefaultPropertyUtil.newDefaultProperty(propertyName, null, "");
        adapter.addItemProperty(propertyName, property);
        property.setValue(propertyValue);

        // WHEN
        Node res = adapter.getNode();

        // THEN
        // should have the created property
        assertEquals(true, res.hasProperty(propertyName));
        assertEquals(propertyValue, res.getProperty(propertyName).getString());
        assertSame(node, res);
    }

    @Test
    public void testGetNode_ExistingProperty() throws Exception {
        // GIVEN
        String id_2 = "propertyName_2";
        String value_2 = "value_2";
        Node node = session.getRootNode().addNode(nodeName);
        // Add two property (JCR)
        javax.jcr.Property property1 = node.setProperty(propertyName, propertyValue);
        node.setProperty(id_2, value_2);
        JcrNodeAdapter adapter = new JcrNodeAdapter(node);

        // Modify one JCR property
        Property property = adapter.getItemProperty(propertyName);
        property.setValue(propertyValue + modified);

        // WHEN
        // getNode.
        Node res = adapter.getNode();

        // THEN
        // should have the created property
        assertEquals(true, res.hasProperty(propertyName));
        assertEquals(propertyValue + modified, res.getProperty(propertyName).getString());
        assertSame(node, res);
        assertSame(property1, res.getProperty(propertyName));
        assertEquals(value_2, res.getProperty(id_2).getString());
    }

    @Test
    public void testGetNode_MixedProperty() throws Exception {
        // GIVEN
        String id_2 = "propertyName_2";
        String value_2 = "value_2";
        String id_3 = "propertyName_3";
        String value_3 = "value_3";
        Node node = session.getRootNode().addNode(nodeName);
        // Add two property (JCR)
        node.setProperty(propertyName, propertyValue);
        node.setProperty(id_2, value_2);
        JcrNodeAdapter adapter = new JcrNodeAdapter(node);
        // Create a new Vaadin property
        Property newProperty = DefaultPropertyUtil.newDefaultProperty(id_3, null, "");
        adapter.addItemProperty(id_3, newProperty);

        // WHEN
        // Modify one JCR and Vaadin property.
        newProperty.setValue(value_3);
        Property jcrProperty = adapter.getItemProperty(propertyName);
        jcrProperty.setValue(propertyValue + modified);
        Node res = adapter.getNode();

        // THEN
        // Vaadin property should be created
        assertEquals(true, res.hasProperty(id_3));
        assertEquals(value_3, res.getProperty(id_3).getString());
        // Modified property should be stored.
        assertEquals(propertyValue + modified, res.getProperty(propertyName).getString());
        // The other JCR property should stay unmodified.
        assertEquals(value_2, res.getProperty(id_2).getString());
    }

    @Test
    public void testGetNode_MixedProperty_FlagSaveInfo_False() throws Exception {
        // GIVEN
        String id_2 = "propertyName_2";
        String value_2 = "value_2";
        String id_3 = "propertyName_3";
        String value_3 = "value_3";
        Node node = session.getRootNode().addNode(nodeName);
        // Add two property (JCR)
        node.setProperty(propertyName, propertyValue);
        node.setProperty(id_2, value_2);
        JcrNodeAdapter adapter = new JcrNodeAdapter(node);
        // Create a new Vaadin property
        DefaultProperty newProperty = DefaultPropertyUtil.newDefaultProperty(id_3, null, "");
        newProperty.setReadOnly(true);
        adapter.addItemProperty(id_3, newProperty);

        // WHEN
        // Modify one JCR and Vaadin property.
        newProperty.setValue(value_3);
        DefaultProperty jcrProperty = (DefaultProperty) adapter.getItemProperty(propertyName);
        jcrProperty.setValue(propertyValue + modified);
        jcrProperty.setReadOnly(true);
        Node res = adapter.getNode();

        // THEN
        // Vaadin property should not be created
        assertEquals(false, res.hasProperty(id_3));
        // Modified property should stay unmodified.
        assertEquals(propertyValue, res.getProperty(propertyName).getString());
        // The other JCR property should stay unmodified.
        assertEquals(value_2, res.getProperty(id_2).getString());
    }

    @Test
    public void testGetNode_ExistingPropertyRemoved() throws Exception {
        // GIVEN
        String id_2 = "propertyName_2";
        String value_2 = "value_2";
        String id_3 = "propertyName_3";
        String value_3 = "value_3";
        Node node = session.getRootNode().addNode(nodeName);
        // Add three property (JCR)
        node.setProperty(propertyName, propertyValue);
        node.setProperty(id_2, value_2);
        node.setProperty(id_3, value_3);
        JcrNodeAdapter adapter = new JcrNodeAdapter(node);
        // Modify two JCR property.
        adapter.getItemProperty(propertyName).setValue(propertyValue + modified);
        adapter.getItemProperty(id_2).setValue(value_2 + modified);

        // WHEN
        // Remove one modified JCR property
        adapter.removeItemProperty(id_2);

        // THEN
        // getNode should have
        Node res = adapter.getNode();
        // 2 previous JCR property (one modified, one not)
        assertEquals(true, res.hasProperty(propertyName));
        assertEquals(false, res.hasProperty(id_2));
        assertEquals(true, res.hasProperty(id_3));
        assertEquals(propertyValue + modified, res.getProperty(propertyName).getString());
        assertEquals(value_3, res.getProperty(id_3).getString());
    }

    @Test
    public void testGetNode_MixedPropertyRemoved() throws Exception {
        // GIVEN
        Node node = session.getRootNode().addNode(nodeName);
        // Add three property (JCR)
        node.setProperty("id_1", "value_1");
        node.setProperty("id_2", "value_2");
        node.setProperty("id_3", "value_3");
        JcrNodeAdapter adapter = new JcrNodeAdapter(node);
        // Modify two JCR property.
        adapter.getItemProperty("id_1").setValue("value_1_Modify");
        adapter.getItemProperty("id_2").setValue("value_2_Modify");
        // Create two 2 Vaadin property
        Property newProperty_1 = DefaultPropertyUtil.newDefaultProperty("id_4", null, "");
        adapter.addItemProperty("id_4", newProperty_1);
        Property newProperty_2 = DefaultPropertyUtil.newDefaultProperty("id_5", null, "");
        adapter.addItemProperty("id_5", newProperty_2);
        // Modify two Vaadin property.
        newProperty_1.setValue("value_4");
        newProperty_2.setValue("value_5");

        // WHEN
        // Remove one modified JCR property and one Vaadin modified.
        adapter.removeItemProperty("id_4");
        adapter.removeItemProperty("id_1");

        // THEN
        // getNode should have
        Node res = adapter.getNode();
        // 2 previous JCR property (one modified, one not)
        assertEquals(false, res.hasProperty("id_1"));
        assertEquals(true, res.hasProperty("id_2"));
        assertEquals(true, res.hasProperty("id_3"));
        assertEquals(false, res.hasProperty("id_4"));
        assertEquals(true, res.hasProperty("id_5"));

        assertEquals("value_2_Modify", res.getProperty("id_2").getString());
        assertEquals("value_3", res.getProperty("id_3").getString());
        assertEquals("value_5", res.getProperty("id_5").getString());
        // one new property
    }

    @Test
    public void testGetNode_NewPropertyRemoved() throws Exception {
        // GIVEN
        Node node = session.getRootNode().addNode(nodeName);
        JcrNodeAdapter adapter = new JcrNodeAdapter(node);
        // Add two three property (Vaadin)
        Property newProperty_1 = DefaultPropertyUtil.newDefaultProperty("id_4", null, "");
        adapter.addItemProperty("id_4", newProperty_1);
        Property newProperty_2 = DefaultPropertyUtil.newDefaultProperty("id_5", null, "");
        adapter.addItemProperty("id_5", newProperty_2);

        adapter.getItemProperty("id_6");
        // Modify two Vaadin property.
        newProperty_1.setValue("value_4");
        newProperty_2.setValue("value_5");

        // WHEN
        // Remove one
        adapter.removeItemProperty("id_5");

        // THEN
        Node res = adapter.getNode();
        // should have only the created&set property not removed
        assertEquals(true, res.hasProperty("id_4"));
        assertEquals(false, res.hasProperty("id_5"));
        // not part of the result. New Vaadin property not changed
        assertEquals(false, res.hasProperty("id_6"));
        assertEquals("value_4", res.getProperty("id_4").getString());
    }
}
