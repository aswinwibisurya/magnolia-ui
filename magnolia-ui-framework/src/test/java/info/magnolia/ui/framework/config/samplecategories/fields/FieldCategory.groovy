/**
 * This file Copyright (c) 2014 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This file is dual-licensed under both the Magnolia
 * Network Agreement and the GNU General public static  License.
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
 * public static  License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General public static  License, Version 3 along with this program;
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
package info.magnolia.ui.framework.config.samplecategories.fields
import info.magnolia.repository.RepositoryConstants
import info.magnolia.ui.form.config.*
import info.magnolia.ui.form.field.converter.BaseIdentifierToPathConverter

/**
 * Field category that demonstrates how a {@link TabBuilder} can be extended with new
 * methods without modifying itself.
 */
@Category(value = TabBuilder.class)
public class FieldCategory {

    public DateFieldBuilder date(String name) {
        return addField(this, new DateFieldBuilder(name));
    }

    public BasicUploadFieldBuilder basicUpload(String name) {
        return addField(this, new BasicUploadFieldBuilder(name).binaryNodeName(name));
    }

    public TextFieldBuilder text(String name) {
        return addField(this, new TextFieldBuilder(name));
    }

    public LinkFieldBuilder link(String name) {
        return addField(this, new LinkFieldBuilder(name));
    }

    public LinkFieldBuilder pageLink(String name) {
        return addField(this, addField(this,
                new LinkFieldBuilder(name).
                        appName("pages").
                        targetWorkspace(RepositoryConstants.WEBSITE).
                        identifierToPathConverter(new BaseIdentifierToPathConverter())));
    }

    public SelectFieldBuilder select(String name) {
        return addField(this, new SelectFieldBuilder(name));
    }

    public HiddenFieldBuilder hidden(String name) {
        return addField(this, new HiddenFieldBuilder(name));
    }

    public CheckboxFieldBuilder checkbox(String name) {
        return addField(this, new CheckboxFieldBuilder(name));
    }

    public OptionGroupFieldBuilder optionGroup(String name) {
        return addField(this, new OptionGroupFieldBuilder(name));
    }

    public PasswordFieldBuilder password(String name) {
        return addField(this, new PasswordFieldBuilder(name));
    }

    public StaticFieldBuilder staticField(String name) {
        return addField(this, new StaticFieldBuilder(name));
    }

    public RichTextFieldBuilder richText(String name) {
        return addField(this, new RichTextFieldBuilder(name));
    }

    public TwinColSelectFieldBuilder twinColSelect(String name) {
        return addField(this, new TwinColSelectFieldBuilder(name));
    }

    public MultiValueFieldBuilder multi(String name) {
        return addField(this, new MultiValueFieldBuilder(name));
    }

    public BasicTextCodeFieldBuilder basicTextCode( String name) {
        return addField(this, new BasicTextCodeFieldBuilder(name));
    }

    public SwitchableFieldBuilder switchable( String name) {
        return addField(this, new SwitchableFieldBuilder(name));
    }

    private static <T extends AbstractFieldBuilder> T addField(TabBuilder tabBuilder, T fieldBuilder) {
        tabBuilder.definition().addField(fieldBuilder.definition());
        return fieldBuilder;
    }
}