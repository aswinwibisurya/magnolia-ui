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
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import info.magnolia.context.MgnlContext;
import info.magnolia.test.mock.MockContext;
import info.magnolia.test.mock.jcr.MockSession;
import info.magnolia.test.mock.jcr.MockValue;
import info.magnolia.ui.model.ModelConstants;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.Value;
import javax.jcr.ValueFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.vaadin.data.Property;

/**
 * Main test class for {@link JcrPropertyAdapter}.
 */
public class JcrPropertyAdapterTest {

    private final String workspaceName = "workspace";

    private final String propertyName = "property";

    private final String numericPropertyName = "numericProperty";

    private final String booleanPropertyName = "booleanProperty";

    private final String propertyValue = "value";

    private final int intValue = 42;

    private final boolean booleanValue = true;

    private MockSession session;

    @Before
    public void setUp() {
        session = new MockSession(workspaceName);
        MockContext ctx = new MockContext();
        ctx.addSession(workspaceName, session);
        MgnlContext.setInstance(ctx);

        final ValueFactory valueFactory = mock(ValueFactory.class);
        Answer<Value> valueFactoryAnswer = new Answer<Value>() {

            @Override
            public Value answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return new MockValue(args[0]);
            }
        };
        when(valueFactory.createValue(anyString())).thenAnswer(valueFactoryAnswer);
        when(valueFactory.createValue(anyInt())).thenAnswer(valueFactoryAnswer);
        when(valueFactory.createValue(anyDouble())).thenAnswer(valueFactoryAnswer);
        when(valueFactory.createValue(anyBoolean())).thenAnswer(valueFactoryAnswer);
        // add more parameter types here if necessary for tests

        session.setValueFactory(valueFactory);
    }

    @After
    public void tearDown() {
        MgnlContext.setInstance(null);
    }

    @Test
    public void testGetItemProperty() throws Exception {
        // GIVEN
        Node node = session.getRootNode();
        node.setProperty(propertyName, propertyValue);

        JcrPropertyAdapter adapter = new JcrPropertyAdapter(node.getProperty(propertyName));

        // WHEN
        Property nameProperty = adapter.getItemProperty(ModelConstants.JCR_NAME);
        Property valueProperty = adapter.getItemProperty(JcrPropertyAdapter.VALUE_PROPERTY);
        Property typeProperty = adapter.getItemProperty(JcrPropertyAdapter.TYPE_PROPERTY);

        // THEN
        assertEquals(propertyName, nameProperty.getValue());
        assertEquals(propertyValue, valueProperty.getValue());
        assertEquals(PropertyType.nameFromValue(PropertyType.STRING), typeProperty.getValue());
        assertNotSame(nameProperty, adapter.getItemProperty(ModelConstants.JCR_NAME));
    }

    @Test
    public void testUpdatePropertyName() throws Exception {
        // GIVEN
        Node node = session.getRootNode();
        node.setProperty(propertyName, propertyValue);
        JcrPropertyAdapter adapter = new JcrPropertyAdapter(node.getProperty(propertyName));
        String newJcrName = "propertyRenamed";

        // WHEN
        adapter.getItemProperty(ModelConstants.JCR_NAME).setValue(newJcrName);
        adapter.updateProperties();

        // THEN
        assertFalse(node.hasProperty(propertyName));
        assertTrue(node.hasProperty(newJcrName));
    }

    @Test
    public void testUpdatePropertyNameToSameName() throws Exception {
        // GIVEN
        Node node = session.getRootNode();
        node.setProperty(propertyName, propertyValue);
        JcrPropertyAdapter adapter = new JcrPropertyAdapter(node.getProperty(propertyName));
        String newJcrName = propertyName;

        // WHEN
        adapter.getItemProperty(ModelConstants.JCR_NAME).setValue(newJcrName);
        adapter.updateProperties();

        // THEN
        assertTrue(node.hasProperty(propertyName));
    }

    @Test
    public void testUpdatePropertyNameWhenAlreadyExisting() throws Exception {
        // GIVEN
        String existingName = "existingName";
        Node node = session.getRootNode();
        node.setProperty(existingName, "42");
        node.setProperty(propertyName, propertyValue);
        long propertyCount = node.getProperties().getSize();
        JcrPropertyAdapter adapter = new JcrPropertyAdapter(node.getProperty(propertyName));

        // WHEN
        adapter.getItemProperty(ModelConstants.JCR_NAME).setValue(existingName);
        adapter.updateProperties();

        // THEN
        assertTrue(node.hasProperty(existingName));
        assertEquals(propertyCount, node.getProperties().getSize());
    }

    @Test
    public void testUpdatePropertyValue() throws Exception {
        // GIVEN
        Node node = session.getRootNode();
        node.setProperty(propertyName, propertyValue);
        JcrPropertyAdapter adapter = new JcrPropertyAdapter(node.getProperty(propertyName));
        String newValue = "valueChanged";

        // WHEN
        adapter.getItemProperty(JcrPropertyAdapter.VALUE_PROPERTY).setValue(newValue);
        adapter.updateProperties();

        // THEN
        assertTrue(node.hasProperty(propertyName));
        assertEquals(newValue, node.getProperty(propertyName).getString());
    }

    @Ignore("http://jira.magnolia-cms.com/browse/MGNLUI-485")
    @Test
    public void testUpdatePropertyValueKeepsPropertyType() throws Exception {
        // GIVEN
        Node node = session.getRootNode();
        node.setProperty(numericPropertyName, intValue);
        node.setProperty(booleanPropertyName, booleanValue);
        JcrPropertyAdapter numericAdapter = new JcrPropertyAdapter(node.getProperty(numericPropertyName));
        JcrPropertyAdapter booleanAdapter = new JcrPropertyAdapter(node.getProperty(booleanPropertyName));
        int numericType = numericAdapter.getProperty().getType();
        int booleanType = booleanAdapter.getProperty().getType();
        String newIntValue = "43";
        String newBooleanValue = "false";

        // WHEN
        numericAdapter.getItemProperty(JcrPropertyAdapter.VALUE_PROPERTY).setValue(newIntValue);
        numericAdapter.updateProperties();

        booleanAdapter.getItemProperty(JcrPropertyAdapter.VALUE_PROPERTY).setValue(newBooleanValue);
        booleanAdapter.updateProperties();

        // THEN
        assertTrue(node.hasProperty(numericPropertyName));
        assertEquals(newIntValue, node.getProperty(numericPropertyName).getString());
        assertEquals(numericType, node.getProperty(numericPropertyName).getType());

        assertTrue(node.hasProperty(booleanPropertyName));
        assertEquals(newBooleanValue, node.getProperty(booleanPropertyName).getString());
        assertEquals(booleanType, node.getProperty(booleanPropertyName).getType());
    }

    @Test
    public void testUpdatePropertyType() throws Exception {
        // GIVEN
        Node node = session.getRootNode();
        node.setProperty(propertyName, propertyValue);
        JcrPropertyAdapter adapter = new JcrPropertyAdapter(node.getProperty(propertyName));
        String newType = PropertyType.TYPENAME_DOUBLE;

        // WHEN
        adapter.getItemProperty(JcrPropertyAdapter.TYPE_PROPERTY).setValue(newType);
        adapter.updateProperties();

        // THEN
        assertTrue(node.hasProperty(propertyName));
        assertEquals(PropertyType.valueFromName(newType), node.getProperty(propertyName).getType());
    }

}
