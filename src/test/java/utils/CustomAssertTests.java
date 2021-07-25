package utils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import mocks.PrimitiveOnlyRecord;
import mocks.PrimitiveOnlyObject;
import mocks.TreeNodeObject;

import static org.junit.jupiter.api.Assertions.fail;
import static utils.CustomAssert.assertObjectsEqual;

public class CustomAssertTests {

    @Test
    public void verifyNullComparedToActualThrows() {
        try {
            assertObjectsEqual(null, new Object());
            fail("Should have thrown an exception");
        } catch (AssertionError e) {
            // Message is: "Expected element was null but actual element was not null."
            assertTrue(
                    e.getMessage().contains("was null")
                            && e.getMessage().indexOf("was null") < e.getMessage().indexOf("was not null"),
                    "Message should explain that null was expected but non-null was given.");
        }
    }

    @Test
    public void verifyActualComparedToNullThrows() {
        try {
            assertObjectsEqual(new Object(), null);
            fail("Should have thrown an exception");
        } catch (AssertionError e) {
            // Message is: "Expected element was not null but actual element was null."
            assertTrue(
                    e.getMessage().contains("was not null")
                            && e.getMessage().indexOf("was not null") < e.getMessage().indexOf("was null"),
                    "Message should explain that non-null was expected but null was given.");
        }
    }

    @Test
    public void verifyTwoNullsAreEqual() {
        assertObjectsEqual(null, null);
    }

    @Test
    public void differentTypesAreNotEqual() {
        try {
            assertObjectsEqual("", new Object());
            fail("Should have thrown an exception");
        } catch (AssertionError e) {
            // Message is: "Expected element to be of type 'java.lang.String' but was of
            // type 'java.lang.Object'."
            assertTrue(e.getMessage().contains("java.lang.String") && e.getMessage().contains("java.lang.Object"),
                    "Message should contain both types.");
        }
    }

    @Test
    public void primitiveOnlyObjectsWithSameValuesAreEqual() {
        assertObjectsEqual(new PrimitiveOnlyObject(1, 2, false, 'A'), new PrimitiveOnlyObject(1, 2, false, 'A'));
    }

    @Test
    public void differentPrimitiveOnlyObjectsWithDifferentValuesAreNotEqual() {
        try {
            assertObjectsEqual(new PrimitiveOnlyObject(1, 2, false, 'A'),
                    new PrimitiveOnlyObject(1, 2, false, 'B'));
            fail("Should have thrown an exception");
        } catch (AssertionError e) {
            // Message is: "Expected '.charValue' to be 'A' but was 'B'."
            assertTrue(e.getMessage().contains(".charValue"), "Message should contain path");
            assertTrue(e.getMessage().contains("A") && e.getMessage().contains("B"),
                    "Message should contain both values.");
        }
    }

    @Test
    public void nestedObjectsWithSameValuesAreEqualOnlyLevelOne() {
        assertObjectsEqual(new TreeNodeObject("Some text"), new TreeNodeObject("Some text"));
    }

    @Test
    public void nestedObjectsWithSameValuesAreEqualMultipleLevels() {
        var firstElement = new TreeNodeObject("Some text");
        generateNestedObjects(3, firstElement);

        var secondElement = new TreeNodeObject("Some text");
        generateNestedObjects(3, secondElement);
        assertObjectsEqual(firstElement, secondElement);
    }

    @Test
    public void nestedObjectsWithDifferentNestingLevelsFail() {
        var expectedElement = new TreeNodeObject("Some text");
        generateNestedObjects(3, expectedElement);

        var actualElement = new TreeNodeObject("Some text");
        generateNestedObjects(2, actualElement);
        try {
            assertObjectsEqual(expectedElement, actualElement);
            fail("Should have thrown an exception");
        } catch (AssertionError e) {
            assertTrue(e.getMessage().contains(".child.child.child"), "Message should contain path.");
            assertTrue(e.getMessage().contains("not null"), "Message should contain information about missing object.");
        }
    }


    @Test
    public void recordsWithSameValuesAreEqual() {
        assertObjectsEqual(new PrimitiveOnlyRecord(1,2,false,'A'), new PrimitiveOnlyRecord(1,2,false,'A'));
    }

    @Test
    public void recordsWithDifferentValuesAreNotEqual() {
        try {
            assertObjectsEqual(new PrimitiveOnlyRecord(1,2,false,'A'),
                    new PrimitiveOnlyRecord(1,2,false,'B'));
            fail("Should have thrown an exception");
        } catch (AssertionError e) {
            // Message is: "Expected '.charValue' to be 'A' but was 'B'."
            assertTrue(e.getMessage().contains(".charValue"), "Message should contain path.");
            assertTrue(e.getMessage().contains("A") && e.getMessage().contains("B"),
                    "Message should contain both values.");
        }
    }

    @Test
    public void allErrorsAreBeingReported() {
        var expectedElement = new TreeNodeObject("Some text");
        generateNestedObjects(3, expectedElement);

        var actualElement = new TreeNodeObject("Other text");
        generateNestedObjects(2, actualElement);

        actualElement.getChild().setName("non-element");

        try {
            assertObjectsEqual(expectedElement, actualElement);
            fail("Should have thrown an exception");
        } catch (AssertionError e) {
            // Message is:
            // "Expected '.child.child.child' to be not null but was null.
            // Expected '.child.name' to be 'element' but was 'non-element'.
            // Expected '.name' to be 'Some text' but was 'Other text'."
            assertTrue(e.getMessage().contains(".name"), "Message should contain path for top level name mismatch.");
            assertTrue(e.getMessage().contains("Some text") && e.getMessage().contains("Other text"),
                    "Message should contain both values for top level mismatch.");
            assertTrue(e.getMessage().contains(".child.name"),
                    "Message should contain path for nested level name mismatch.");
            assertTrue(e.getMessage().contains("element") && e.getMessage().contains("non-element"),
                    "Message should contain both values for nested level mismatch.");
            assertTrue(e.getMessage().contains(".child.child.child"),
                    "Message should contain path for different levels.");
            assertTrue(e.getMessage().contains("not null") && e.getMessage().contains("null"),
                    "Message should contain information about missing object on different path levels.");
        }
    }

    public static TreeNodeObject generateNestedObjects(int depth, TreeNodeObject parent) {
        if (depth > 0) {
            parent.setChild(new TreeNodeObject("element"));
            generateNestedObjects(depth - 1, parent.getChild());
        }
        return parent;
    }
}
