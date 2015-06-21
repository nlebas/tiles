package org.apache.tiles.test.freemarker;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class FreemarkerTest extends SeleniumLikeTestBase {

    // Composite Definition with Inner Configured Definition with no Type Test
    @Test
    public void testCompositeDefinitionWithInnerConfiguredDefinitionNoType() throws IOException {

        clickAndWait("ftl/testinsertdefinition_composite_tags_includes_configured_notype.ftl");
        assertTextPresent("This is a composite definition with tags.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a configured inner definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Composite Definition with Inner Configured Definition Test
    @Test
    public void testCompositeDefinitionWithInnerConfiguredDefinition() throws IOException {

        clickAndWait("ftl/testinsertdefinition_composite_tags_includes_configured.ftl");
        assertTextPresent("This is a composite definition with tags.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a configured inner definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Composite Definition with Inner Configured Definition with no Type Test
    @Test
    public void testCompositeDefinitionWithInnerDefinitionNoType() throws IOException {

        clickAndWait("ftl/testinsertdefinition_composite_tags_notype.ftl");
        assertTextPresent("This is a composite definition with tags.");
        assertTextPresent("This is the header");
        assertTextPresent("This is an inner definition with tags.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Composite Definition with Inner Definition Test
    @Test
    public void testCompositeDefinitionWithInnerDefinition() throws IOException {

        clickAndWait("ftl/testinsertdefinition_composite_tags.ftl");
        assertTextPresent("This is a composite definition with tags.");
        assertTextPresent("This is the header");
        assertTextPresent("This is an inner definition with tags.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Composite Definition Test
    @Test
    public void testConfiguredCompositeDefinition() throws IOException {

        clickAndWait("ftl/testinsertdefinition_composite.ftl");
        assertTextPresent("This is a configured composite definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a configured inner definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition Attribute Preparer Test
    @Test
    public void testConfiguredDefinitionAttributePreparer() throws IOException {

        clickAndWait("ftl/testinsertdefinition_attribute_preparer.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is the value added by the AttributeViewPreparer");

    }

    // Configured Definition Attribute Roles Tags Test
    @Test
    public void testConfiguredDefinitionAttributeRolesTags() throws IOException {

        clickAndWait("ftl/testinsertdefinition_attribute_roles_tags.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextNotPresent("This is a body");

    }

    // Configured Definition Attribute Roles Test
    @Test
    public void testConfiguredDefinitionAttributeRoles() throws IOException {

        clickAndWait("ftl/testinsertdefinition_attribute_roles.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextNotPresent("This is a body");

    }

    // Configured Definition Cascaded List Test
    @Test
    public void testConfiguredDefinitionCascadedList() throws IOException {

        clickAndWait("ftl/testinsertdefinition_cascaded_list.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("Single attribute \"stringTest\" value: This is a string ");
        assertTextPresent("valueOne");
        assertTextPresent("valueTwo");
        assertTextPresent("valueThree");

    }

    // Configured Definition Cascaded Overridden Test
    @Test
    public void testConfiguredDefinitionCascadedOverridden() throws IOException {

        clickAndWait("ftl/testinsertdefinition_cascaded_overridden.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");
        assertTextPresent("This is a configured inner definition.");
        assertTextPresent("This is the header");

    }

    // Configured Definition Cascaded Template Test
    @Test
    public void testConfiguredDefinitionCascadedTemplate() throws IOException {

        clickAndWait("ftl/testinsertdefinition_cascaded_template.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");

    }

    // Configured Definition Cascaded Test
    @Test
    public void testConfiguredDefinitionCascaded() throws IOException {

        clickAndWait("ftl/testinsertdefinition_cascaded.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition with Default Values Test
    @Test
    public void testConfiguredDefinitionDefaultValues() throws IOException {

        clickAndWait("ftl/testinsertdefinition_defaultvalues.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the alternate header");
        assertTextPresent("This is the default body in the tag.");

    }

    // Configured Definition EL with Single Evaluation Test
    @Test
    public void testConfiguredDefinitionELSingleEval() throws IOException {

        clickAndWait("ftl/testinsertdefinition_el_singleeval.ftl");
        assertTextPresent("This is a configured definition.");
        assertTextPresent("This is the header");
        assertTextPresent("${requestScope.doNotShow}");

    }

    // Configured Definition EL Test
    @Test
    public void testConfiguredDefinitionEL() throws IOException {

        clickAndWait("ftl/testinsertdefinition_el.ftl");
        assertTextPresent("This is a configured composite definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a configured inner definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition Test
    @Test
    public void testConfiguredDefinitionException() throws IOException {
        
        try {
            clickAndWait("ftl/testinsertdefinition_exception.ftl");
            assertTrue("Exception expected but not thrown", false);
        }
        catch(ArithmeticException e) {
            assertTextNotPresent("This is a body");
        }
    }

    // Configured Definition with Flush Test
    @Test
    public void testConfiguredDefinitionFlush() throws IOException {
        clickAndWait("ftl/testinsertdefinition_flush.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition Ignore Test
    @Test
    public void testConfiguredDefinitionIgnore() throws IOException {

        clickAndWait("ftl/testinsertdefinition_ignore.ftl");
        assertTextPresent("This is the title.");
        assertTextNotPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition with Inline Content Test
    @Test
    public void testConfiguredDefinitionInline() throws IOException {

        clickAndWait("ftl/testinsertdefinition_inline.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is an inline content");

    }

    // Configured Definition MVEL Test
    @Test
    public void testConfiguredDefinitionMVEL() throws IOException {

        clickAndWait("ftl/testinsertdefinition_mvel.ftl");
        assertTextPresent("This is a configured composite definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a configured inner definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition OGNL Test
    @Test
    public void testConfiguredDefinitionOGNL() throws IOException {

        clickAndWait("ftl/testinsertdefinition_ognl.ftl");
        assertTextPresent("This is a configured composite definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a configured inner definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition Old Format Test
    @Test
    public void testConfiguredDefinitionOldFormat() throws IOException {

        clickAndWait("ftl/testinsertdefinition_old.ftl");
        assertTextPresent("This is a definition configured in 1.1 format.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition with Open BodyTest
    @Test
    public void testConfiguredDefinitionOpenBody() throws IOException {

        clickAndWait("ftl/testinsertdefinition_openbody.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");
        assertTextPresent("This is a customized context");

    }

    // Configured Definition with Overridden Content Test
    @Test
    public void testConfiguredDefinitionOverrideAndNot() throws IOException {

        clickAndWait("ftl/testinsertdefinition_override_and_not.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is an overridden content");
        assertTextPresent("This is a body");

    }

    // Configured Definition with Overridden Template Test
    @Test
    public void testConfiguredDefinitionOverrideTemplate() throws IOException {

        clickAndWait("ftl/testinsertdefinition_override_template.ftl");
        assertTextPresent("This is the overridden template.");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition with Overridden Content Test
    @Test
    public void testConfiguredDefinitionOverride() throws IOException {

        clickAndWait("ftl/testinsertdefinition_override.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is an overridden content");

    }

    // Configured Definition Regular Expression Test
    @Test
    public void testConfiguredDefinitionRegexp() throws IOException {

        clickAndWait("ftl/testinsertdefinition_regexp.ftl");
        assertTextPresent("This is layout one.");
        assertTextPresent("This is layout two.");
        assertTextPresent("This definition has a message: Hello.");
        assertTextPresent("This definition has a message: Bye.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition Reversed Test
    @Test
    public void testConfiguredDefinitionReversed() throws IOException {

        clickAndWait("ftl/testinsertdefinition_reversed.ftl");
        assertTextPresent(".eltit eht si sihT");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition Role Tag Test
    @Test
    public void testConfiguredDefinitionRoleTag() throws IOException {

        clickAndWait("ftl/testinsertdefinition_role_tag.ftl");
        assertTextPresent("This definition appears.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");
        assertTextNotPresent("This definition does not appear.");

    }

    // Configured Definition Role Test
    @Test
    public void testConfiguredDefinitionRole() throws IOException {

        clickAndWait("ftl/testinsertdefinition_role.ftl");
        assertTextPresent("This definition appears.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");
        assertTextNotPresent("This definition does not appear.");

    }

    // Configured Definition Test
    @Test
    public void testConfiguredDefinition() throws IOException {

        clickAndWait("ftl/testinsertdefinition.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition Wildcard Test
    @Test
    public void testConfiguredDefinitionWildcard() throws IOException {

        clickAndWait("ftl/testinsertdefinition_wildcard.ftl");
        assertTextPresent("This is layout one.");
        assertTextPresent("This is layout two.");
        assertTextPresent("This definition has a message: Hello.");
        assertTextPresent("This definition has a message: Bye.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition with Configured Preparer Test
    @Test
    public void testConfiguredDefinitionWithConfiguredPreparer() throws IOException {

        clickAndWait("ftl/testinsertdefinition_preparer_configured.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is the value added by the ViewPreparer");

    }

    // Configured Definition with Preparer Test
    @Test
    public void testConfiguredDefinitionWithPreparer() throws IOException {

        clickAndWait("ftl/testinsertdefinition_preparer.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is the value added by the ViewPreparer");

    }

    // Configured Nested Definition Test
    @Test
    public void testConfiguredNestedDefinition() throws IOException {

        clickAndWait("ftl/testinsertnesteddefinition.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a nested definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Nested List Definition Test
    @Test
    public void testConfiguredNestedListDefinition() throws IOException {

        clickAndWait("ftl/testinsertnestedlistdefinition.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a nested definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Definition Tag Test
    @Test
    public void testDefinitionTagExtend() throws IOException {

        clickAndWait("ftl/testdef_extend.ftl");
        assertTextPresent("This is an overridden title");
        assertTextPresent("This is the header");
        assertTextPresent("This is an overridden content");

    }

    // Definition Tag List Inherit Test
    @Test
    public void testDefinitionTagListInherit() throws IOException {

        clickAndWait("ftl/testdef_list_inherit.ftl");
        assertTextPresent("Single attribute \"stringTest\" value: This is a string ");
        assertTextPresent("valueOne");
        assertTextPresent("valueTwo");
        assertTextPresent("valueThree");
        assertTextPresent("valueFour");

    }

    // Definition Tag with Preparer Test
    @Test
    public void testDefinitionTagPreparer() throws IOException {

        clickAndWait("ftl/testdef_preparer.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is the value added by the ViewPreparer");

    }

    // Definition Tag Test
    @Test
    public void testDefinitionTag() throws IOException {

        clickAndWait("ftl/testdef.ftl");
        assertTextPresent("This is the title");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Import Attribute Tag with no Name Specified Test
    @Test
    public void testImportAttributeTagAll() throws IOException {

        clickAndWait("ftl/testimportattribute_all.ftl");
        assertTextPresent("One");
        assertTextPresent("Two");
        assertTextPresent("Three");

    }

    // Import Attribute Tag Inherit Test
    @Test
    public void testImportAttributeTagInherit() throws IOException {

        clickAndWait("ftl/testimportattribute_inherit.ftl");
        assertTextPresent("Single attribute \"stringTest\" value: This is a string ");
        assertTextPresent("valueOne");
        assertTextPresent("valueTwo");
        assertTextPresent("valueThree");
        assertTextPresent("valueFour");

    }

    // Import Attribute Tag Test
    @Test
    public void testImportAttributeTag() throws IOException {

        clickAndWait("ftl/testimportattribute.ftl");
        assertTextPresent("Single attribute \"stringTest\" value: This is a string ");
        assertTextPresent("valueOne");
        assertTextPresent("valueTwo");
        assertTextPresent("valueThree");

    }

    // Put List Cascaded Tag Test
    @Test
    public void testPutListCascadedTag() throws IOException {

        clickAndWait("ftl/testputlist_cascaded.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("Single attribute \"stringTest\" value: This is a string ");
        assertTextPresent("valueOne");
        assertTextPresent("valueTwo");
        assertTextPresent("valueThree");

    }

    // Put List Tag Inherit Test
    @Test
    public void testPutListTagInherit() throws IOException {

        clickAndWait("ftl/testputlist_inherit.ftl");
        assertTextPresent("Single attribute \"stringTest\" value: This is a string ");
        assertTextPresent("valueOne");
        assertTextPresent("valueTwo");
        assertTextPresent("valueThree");
        assertTextPresent("valueFour");

    }

    // Put List Tag Test
    @Test
    public void testPutListTag() throws IOException {

        clickAndWait("ftl/testputlist.ftl");
        assertTextPresent("Single attribute \"stringTest\" value: This is a string ");
        assertTextPresent("valueOne");
        assertTextPresent("valueTwo");
        assertTextPresent("valueThree");

    }

    // Put Tag Cascaded Overridden Test
    @Test
    public void testPutTagCascadedOverridden() throws IOException {

        clickAndWait("ftl/testput_cascaded_overridden.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");
        assertTextPresent("This is a configured inner definition.");
        assertTextPresent("This is the header");

    }

    // Put Tag Cascaded Template Test
    @Test
    public void testPutTagCascadedTemplate() throws IOException {

        clickAndWait("ftl/testput_cascaded_template.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");

    }

    // Put Tag Cascaded Test
    @Test
    public void testPutTagCascaded() throws IOException {

        clickAndWait("ftl/testput_cascaded.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Put Tag with Flush Test
    @Test
    public void testPutTagFlush() throws IOException {

        clickAndWait("ftl/testput_flush.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Put Tag with Nested Definition Test
    @Test
    public void testPutTagNestedDefinition() throws IOException {

        clickAndWait("ftl/testinsertnesteddefinition_tags.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a nested definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Put Tag with Nested List Definition Test
    @Test
    public void testPutTagNestedListDefinition() throws IOException {

        clickAndWait("ftl/testinsertnestedlistdefinition_tags.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a nested definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Put Tag Reversed Test
    @Test
    public void testPutTagReversed() throws IOException {

        clickAndWait("ftl/testput_reversed.ftl");
        assertTextPresent(".eltit eht si sihT");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Put Tag Test
    @Test
    public void testPutTag() throws IOException {

        clickAndWait("ftl/testput.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Put Tag using EL with Single Evaluation Test
    @Test
    public void testPutTagWithELSingleEval() throws IOException {

        clickAndWait("ftl/testput_el_singleeval.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("${requestScope.doNotShow}");

    }

    // Put Tag using EL Test
    @Test
    public void testPutTagWithEL() throws IOException {

        clickAndWait("ftl/testput_el.ftl");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("Body Content defined by and el");

    }

    // setCurrentContainer Tag Test
    @Test
    public void testSetCurrentContainerTag() throws IOException {

        clickAndWait("ftl/testsetcurrentcontainer.ftl");
        assertTextPresent("This definition is from an alternate container.");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

}
