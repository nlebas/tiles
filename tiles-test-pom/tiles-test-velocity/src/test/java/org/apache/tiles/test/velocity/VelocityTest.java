package org.apache.tiles.test.velocity;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class VelocityTest extends SeleniumLikeTestBase {

    // Composite Definition with Inner Configured Definition with no Type Test
    @Test
    public void testCompositeDefinitionWithInnerConfiguredDefinitionNoType() throws IOException {

        clickAndWait("velocity/testinsertdefinition_composite_tags_includes_configured_notype.vm");
        assertTextPresent("This is a composite definition with tags.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a configured inner definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Composite Definition with Inner Configured Definition Test
    @Test
    public void testCompositeDefinitionWithInnerConfiguredDefinition() throws IOException {

        clickAndWait("velocity/testinsertdefinition_composite_tags_includes_configured.vm");
        assertTextPresent("This is a composite definition with tags.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a configured inner definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Composite Definition with Inner Configured Definition with no Type Test
    @Test
    public void testCompositeDefinitionWithInnerDefinitionNoType() throws IOException {

        clickAndWait("velocity/testinsertdefinition_composite_tags_notype.vm");
        assertTextPresent("This is a composite definition with tags.");
        assertTextPresent("This is the header");
        assertTextPresent("This is an inner definition with tags.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Composite Definition with Inner Definition Test
    @Test
    public void testCompositeDefinitionWithInnerDefinition() throws IOException {

        clickAndWait("velocity/testinsertdefinition_composite_tags.vm");
        assertTextPresent("This is a composite definition with tags.");
        assertTextPresent("This is the header");
        assertTextPresent("This is an inner definition with tags.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Composite Definition Test
    @Test
    public void testConfiguredCompositeDefinition() throws IOException {

        clickAndWait("velocity/testinsertdefinition_composite.vm");
        assertTextPresent("This is a configured composite definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a configured inner definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition Attribute Preparer Test
    @Test
    public void testConfiguredDefinitionAttributePreparer() throws IOException {

        clickAndWait("velocity/testinsertdefinition_attribute_preparer.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is the value added by the AttributeViewPreparer");

    }

    // Configured Definition Attribute Roles Tags Test
    @Test
    public void testConfiguredDefinitionAttributeRolesTags() throws IOException {

        clickAndWait("velocity/testinsertdefinition_attribute_roles_tags.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextNotPresent("This is a body");

    }

    // Configured Definition Attribute Roles Test
    @Test
    public void testConfiguredDefinitionAttributeRoles() throws IOException {

        clickAndWait("velocity/testinsertdefinition_attribute_roles.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextNotPresent("This is a body");

    }

    // Configured Definition Cascaded List Test
    @Test
    public void testConfiguredDefinitionCascadedList() throws IOException {

        clickAndWait("velocity/testinsertdefinition_cascaded_list.vm");
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

        clickAndWait("velocity/testinsertdefinition_cascaded_overridden.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");
        assertTextPresent("This is a configured inner definition.");
        assertTextPresent("This is the header");

    }

    // Configured Definition Cascaded Template Test
    @Test
    public void testConfiguredDefinitionCascadedTemplate() throws IOException {

        clickAndWait("velocity/testinsertdefinition_cascaded_template.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");

    }

    // Configured Definition Cascaded Test
    @Test
    public void testConfiguredDefinitionCascaded() throws IOException {

        clickAndWait("velocity/testinsertdefinition_cascaded.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition with Default Values Test
    @Test
    public void testConfiguredDefinitionDefaultValues() throws IOException {

        clickAndWait("velocity/testinsertdefinition_defaultvalues.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the alternate header");
        assertTextPresent("This is the default body in the tag.");

    }

    // Configured Definition EL with Single Evaluation Test
    @Test
    public void testConfiguredDefinitionELSingleEval() throws IOException {

        clickAndWait("velocity/testinsertdefinition_el_singleeval.vm");
        assertTextPresent("This is a configured definition.");
        assertTextPresent("This is the header");
        assertTextPresent("${requestScope.doNotShow}");

    }

    // Configured Definition EL Test
    @Test
    public void testConfiguredDefinitionEL() throws IOException {

        clickAndWait("velocity/testinsertdefinition_el.vm");
        assertTextPresent("This is a configured composite definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a configured inner definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition Test
    // TODO
//    @Test
//    public void testConfiguredDefinitionException() throws IOException {
//        
//        try {
//            clickAndWait("velocity/testinsertdefinition_exception.vm");
//            assertTrue("Exception expected but not thrown", false);
//        }
//        catch(ArithmeticException e) {
//            assertTextNotPresent("This is a body");
//        }
//    }

    // Configured Definition with Flush Test
    @Test
    public void testConfiguredDefinitionFlush() throws IOException {
        clickAndWait("velocity/testinsertdefinition_flush.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition Ignore Test
    @Test
    public void testConfiguredDefinitionIgnore() throws IOException {

        clickAndWait("velocity/testinsertdefinition_ignore.vm");
        assertTextPresent("This is the title.");
        assertTextNotPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition with Inline Content Test
    @Test
    public void testConfiguredDefinitionInline() throws IOException {

        clickAndWait("velocity/testinsertdefinition_inline.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is an inline content");

    }

    // Configured Definition MVEL Test
    @Test
    public void testConfiguredDefinitionMVEL() throws IOException {

        clickAndWait("velocity/testinsertdefinition_mvel.vm");
        assertTextPresent("This is a configured composite definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a configured inner definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition OGNL Test
    @Test
    public void testConfiguredDefinitionOGNL() throws IOException {

        clickAndWait("velocity/testinsertdefinition_ognl.vm");
        assertTextPresent("This is a configured composite definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a configured inner definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition Old Format Test
    @Test
    public void testConfiguredDefinitionOldFormat() throws IOException {

        clickAndWait("velocity/testinsertdefinition_old.vm");
        assertTextPresent("This is a definition configured in 1.1 format.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition with Open BodyTest
    @Test
    public void testConfiguredDefinitionOpenBody() throws IOException {

        clickAndWait("velocity/testinsertdefinition_openbody.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");
        assertTextPresent("This is a customized context");

    }

    // Configured Definition with Overridden Content Test
    @Test
    public void testConfiguredDefinitionOverrideAndNot() throws IOException {

        clickAndWait("velocity/testinsertdefinition_override_and_not.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is an overridden content");
        assertTextPresent("This is a body");

    }

    // Configured Definition with Overridden Template Test
    @Test
    public void testConfiguredDefinitionOverrideTemplate() throws IOException {

        clickAndWait("velocity/testinsertdefinition_override_template.vm");
        assertTextPresent("This is the overridden template.");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition with Overridden Content Test
    @Test
    public void testConfiguredDefinitionOverride() throws IOException {

        clickAndWait("velocity/testinsertdefinition_override.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is an overridden content");

    }

    // Configured Definition Regular Expression Test
    // TODO
//    @Test
//    public void testConfiguredDefinitionRegexp() throws IOException {
//
//        clickAndWait("velocity/testinsertdefinition_regexp.vm");
//        assertTextPresent("This is layout one.");
//        assertTextPresent("This is layout two.");
//        assertTextPresent("This definition has a message: Hello.");
//        assertTextPresent("This definition has a message: Bye.");
//        assertTextPresent("This is the header");
//        assertTextPresent("This is a body");
//
//    }

    // Configured Definition Reversed Test
    @Test
    public void testConfiguredDefinitionReversed() throws IOException {

        clickAndWait("velocity/testinsertdefinition_reversed.vm");
        assertTextPresent(".eltit eht si sihT");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition Role Tag Test
    @Test
    public void testConfiguredDefinitionRoleTag() throws IOException {

        clickAndWait("velocity/testinsertdefinition_role_tag.vm");
        assertTextPresent("This definition appears.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");
        assertTextNotPresent("This definition does not appear.");

    }

    // Configured Definition Role Test
    @Test
    public void testConfiguredDefinitionRole() throws IOException {

        clickAndWait("velocity/testinsertdefinition_role.vm");
        assertTextPresent("This definition appears.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");
        assertTextNotPresent("This definition does not appear.");

    }

    // Configured Definition Test
    @Test
    public void testConfiguredDefinition() throws IOException {

        clickAndWait("velocity/testinsertdefinition.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Definition Wildcard Test
    // TODO
//    @Test
//    public void testConfiguredDefinitionWildcard() throws IOException {
//
//        clickAndWait("velocity/testinsertdefinition_wildcard.vm");
//        assertTextPresent("This is layout one.");
//        assertTextPresent("This is layout two.");
//        assertTextPresent("This definition has a message: Hello.");
//        assertTextPresent("This definition has a message: Bye.");
//        assertTextPresent("This is the header");
//        assertTextPresent("This is a body");
//
//    }

    // Configured Definition with Configured Preparer Test
    @Test
    public void testConfiguredDefinitionWithConfiguredPreparer() throws IOException {

        clickAndWait("velocity/testinsertdefinition_preparer_configured.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is the value added by the ViewPreparer");

    }

    // Configured Definition with Preparer Test
    @Test
    public void testConfiguredDefinitionWithPreparer() throws IOException {

        clickAndWait("velocity/testinsertdefinition_preparer.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is the value added by the ViewPreparer");

    }

    // Configured Nested Definition Test
    @Test
    public void testConfiguredNestedDefinition() throws IOException {

        clickAndWait("velocity/testinsertnesteddefinition.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a nested definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Configured Nested List Definition Test
    @Test
    public void testConfiguredNestedListDefinition() throws IOException {

        clickAndWait("velocity/testinsertnestedlistdefinition.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a nested definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Definition Tag Test
    @Test
    public void testDefinitionTagExtend() throws IOException {

        clickAndWait("velocity/testdef_extend.vm");
        assertTextPresent("This is an overridden title");
        assertTextPresent("This is the header");
        assertTextPresent("This is an overridden content");

    }

    // Definition Tag List Inherit Test
    @Test
    public void testDefinitionTagListInherit() throws IOException {

        clickAndWait("velocity/testdef_list_inherit.vm");
        assertTextPresent("Single attribute \"stringTest\" value: This is a string ");
        assertTextPresent("valueOne");
        assertTextPresent("valueTwo");
        assertTextPresent("valueThree");
        assertTextPresent("valueFour");

    }

    // Definition Tag with Preparer Test
    @Test
    public void testDefinitionTagPreparer() throws IOException {

        clickAndWait("velocity/testdef_preparer.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is the value added by the ViewPreparer");

    }

    // Definition Tag Test
    @Test
    public void testDefinitionTag() throws IOException {

        clickAndWait("velocity/testdef.vm");
        assertTextPresent("This is the title");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Import Attribute Tag with no Name Specified Test
    @Test
    public void testImportAttributeTagAll() throws IOException {

        clickAndWait("velocity/testimportattribute_all.vm");
        assertTextPresent("One");
        assertTextPresent("Two");
        assertTextPresent("Three");

    }

    // Import Attribute Tag Inherit Test
    @Test
    public void testImportAttributeTagInherit() throws IOException {

        clickAndWait("velocity/testimportattribute_inherit.vm");
        assertTextPresent("Single attribute \"stringTest\" value: This is a string ");
        assertTextPresent("valueOne");
        assertTextPresent("valueTwo");
        assertTextPresent("valueThree");
        assertTextPresent("valueFour");

    }

    // Import Attribute Tag Test
    @Test
    public void testImportAttributeTag() throws IOException {

        clickAndWait("velocity/testimportattribute.vm");
        assertTextPresent("Single attribute \"stringTest\" value: This is a string ");
        assertTextPresent("valueOne");
        assertTextPresent("valueTwo");
        assertTextPresent("valueThree");

    }

    // Put List Cascaded Tag Test
    @Test
    public void testPutListCascadedTag() throws IOException {

        clickAndWait("velocity/testputlist_cascaded.vm");
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

        clickAndWait("velocity/testputlist_inherit.vm");
        assertTextPresent("Single attribute \"stringTest\" value: This is a string ");
        assertTextPresent("valueOne");
        assertTextPresent("valueTwo");
        assertTextPresent("valueThree");
        assertTextPresent("valueFour");

    }

    // Put List Tag Test
    @Test
    public void testPutListTag() throws IOException {

        clickAndWait("velocity/testputlist.vm");
        assertTextPresent("Single attribute \"stringTest\" value: This is a string ");
        assertTextPresent("valueOne");
        assertTextPresent("valueTwo");
        assertTextPresent("valueThree");

    }

    // Put Tag Cascaded Overridden Test
    @Test
    public void testPutTagCascadedOverridden() throws IOException {

        clickAndWait("velocity/testput_cascaded_overridden.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");
        assertTextPresent("This is a configured inner definition.");
        assertTextPresent("This is the header");

    }

    // Put Tag Cascaded Template Test
    @Test
    public void testPutTagCascadedTemplate() throws IOException {

        clickAndWait("velocity/testput_cascaded_template.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");

    }

    // Put Tag Cascaded Test
    @Test
    public void testPutTagCascaded() throws IOException {

        clickAndWait("velocity/testput_cascaded.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Put Tag with Flush Test
    @Test
    public void testPutTagFlush() throws IOException {

        clickAndWait("velocity/testput_flush.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Put Tag with Nested Definition Test
    @Test
    public void testPutTagNestedDefinition() throws IOException {

        clickAndWait("velocity/testinsertnesteddefinition_tags.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a nested definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Put Tag with Nested List Definition Test
    @Test
    public void testPutTagNestedListDefinition() throws IOException {

        clickAndWait("velocity/testinsertnestedlistdefinition_tags.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a nested definition.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Put Tag Reversed Test
    @Test
    public void testPutTagReversed() throws IOException {

        clickAndWait("velocity/testput_reversed.vm");
        assertTextPresent(".eltit eht si sihT");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Put Tag Test
    @Test
    public void testPutTag() throws IOException {

        clickAndWait("velocity/testput.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("This is a body");

    }

    // Put Tag using EL with Single Evaluation Test
    @Test
    public void testPutTagWithELSingleEval() throws IOException {

        clickAndWait("velocity/testput_el_singleeval.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("${requestScope.doNotShow}");

    }

    // Put Tag using EL Test
    @Test
    public void testPutTagWithEL() throws IOException {

        clickAndWait("velocity/testput_el.vm");
        assertTextPresent("This is the title.");
        assertTextPresent("This is the header");
        assertTextPresent("Body Content defined by and el");

    }

    // setCurrentContainer Tag Test
    // TODO
//    @Test
//    public void testSetCurrentContainerTag() throws IOException {
//
//        clickAndWait("velocity/testsetcurrentcontainer.vm");
//        assertTextPresent("This definition is from an alternate container.");
//        assertTextPresent("This is the title.");
//        assertTextPresent("This is the header");
//        assertTextPresent("This is a body");
//
//    }

}
