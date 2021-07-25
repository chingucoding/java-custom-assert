package utils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import mocks.GenericObjectContainingObject;
import mocks.PrimitiveOnlyObject;
import mocks.TreeNodeObject;

import static org.junit.jupiter.api.Assertions.fail;
import static utils.CustomAssert.assertObjectsEqual;
import static utils.CustomAssertTests.generateNestedObjects;

public class CustomAssertArrayTests {
	@Test
	public void arrayOfPrimitivesWithSameValuesAreEqual() {
		assertObjectsEqual(new char[] { 'A', 'B', 'C' }, new char[] { 'A', 'B', 'C' });
	}

	@Test
	public void arrayOfDifferentSizesAreNotEqual() {
		try {
			assertObjectsEqual(new char[] { 'A', 'B', 'C' }, new char[] { 'A', 'B' });
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '' to be of length 3 but was of length 2."
			assertTrue(e.getMessage().contains("3") && e.getMessage().contains("2"),
					"Message should contain both sizes.");
		}
	}

	@Test
	public void arrayOfPrimitivesWithDifferentValuesAreNotEqual() {
		try {
			assertObjectsEqual(new int[] { 1, 2, 3 }, new int[] { 1, 2, 4 });
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '[2]' to be '3' but was '4'."
			assertTrue(e.getMessage().contains("[2]"), "Message should contain path");
			assertTrue(e.getMessage().contains("4") && e.getMessage().contains("3"),
					"Message should contain both values.");
		}
	}

	@Test
	public void arrayOfPrimitiveOnlyObjectsWithSameValuesAreEqual() {
		assertObjectsEqual(
				new PrimitiveOnlyObject[] { new PrimitiveOnlyObject(1, 2, false, 'A'),
						new PrimitiveOnlyObject(1, 2, false, 'A') },
				new PrimitiveOnlyObject[] { new PrimitiveOnlyObject(1, 2, false, 'A'),
						new PrimitiveOnlyObject(1, 2, false, 'A') });
	}

	@Test
	public void arrayOfPrimitiveOnlyObjectsWithDifferentValuesAreNotEqual() {
		try {
			assertObjectsEqual(
					new PrimitiveOnlyObject[] { new PrimitiveOnlyObject(1, 2, false, 'A'),
							new PrimitiveOnlyObject(1, 2, false, 'A') },
					new PrimitiveOnlyObject[] { new PrimitiveOnlyObject(1, 2, false, 'A'),
							new PrimitiveOnlyObject(1, 2, false, 'B') });
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '[1].charValue' to be 'A' but was 'B'."
			assertTrue(e.getMessage().contains(".charValue"), "Message should contain path");
			assertTrue(e.getMessage().contains("A") && e.getMessage().contains("B"),
					"Message should contain both values.");
		}
	}

	@Test
	public void arrayOfNestedObjectsWithSameValuesAreEqual() {
		assertObjectsEqual(
				new TreeNodeObject[] { generateNestedObjects(3, new TreeNodeObject("Some text 0")),
						generateNestedObjects(2, new TreeNodeObject("Some text 1")) },
				new TreeNodeObject[] { generateNestedObjects(3, new TreeNodeObject("Some text 0")),
						generateNestedObjects(2, new TreeNodeObject("Some text 1")) });
	}

	@Test
	public void arraysOfNestedObjectsWithDifferentValuesOnTopLevelPropertiesAreNotEqual() {
		try {
			assertObjectsEqual(
					new TreeNodeObject[] { generateNestedObjects(3, new TreeNodeObject("Some text 0")),
							generateNestedObjects(2, new TreeNodeObject("Some text 1")) },
					new TreeNodeObject[] { generateNestedObjects(3, new TreeNodeObject("Some text 0")),
							generateNestedObjects(2, new TreeNodeObject("Some text 2")) });
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '[1].name' to be 'Some text 1' but was 'Some text 2'."
			assertTrue(e.getMessage().contains("[1].name"), "Message should contain path");
			assertTrue(e.getMessage().contains("Some text 1") && e.getMessage().contains("Some text 2"),
					"Message should contain both values.");
		}
	}

	@Test
	public void ArrayOfNestedObjectsWithDifferentValuesInNestingAreNotEqual() {
		try {
			assertObjectsEqual(
					new TreeNodeObject[] { generateNestedObjects(3, new TreeNodeObject("Some text 0")),
							generateNestedObjects(2, new TreeNodeObject("Some text 1")) },
					new TreeNodeObject[] { generateNestedObjects(3, new TreeNodeObject("Some text 0")),
							generateNestedObjects(3, new TreeNodeObject("Some text 1")) });
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '[1].child.child.child.' to be null but was not null."
			assertTrue(e.getMessage().contains("[1].child.child.child"), "Message should contain path");
			assertTrue(
					e.getMessage().contains("null")
							&& e.getMessage().indexOf("null") < e.getMessage().indexOf("not null"),
					"Message should contain both values.");
		}
	}

	@Test
	public void objectContainingDifferentSizedArraysAreNotEqual() {
		try {
			assertObjectsEqual(new GenericObjectContainingObject(new char[] { 'A', 'B', 'C' }),
					new GenericObjectContainingObject(new char[] { 'A', 'B', 'C', 'D' }));
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '.object' to be of length 3 but was of length 4."
			assertTrue(e.getMessage().contains(".object"), "Message should contain path");
			assertTrue(e.getMessage().contains("3") && e.getMessage().contains("4"),
					"Message should contain both sizes.");
		}
	}

	@Test
	public void objectContainingArrayWithSamePrimitiveValuesAreEqual() {
		assertObjectsEqual(new GenericObjectContainingObject(new int[] { 1, 2, 3 }),
				new GenericObjectContainingObject(new int[] { 1, 2, 3 }));
	}

	@Test
	public void objectContainingArrayWithDifferentPrimitiveValuesAreNotEqual() {
		try {
			assertObjectsEqual(new GenericObjectContainingObject(new int[] { 1, 2, 3 }),
					new GenericObjectContainingObject(new int[] { 1, 2, 4 }));
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '.object[2]' to be '3' but was '4'."
			assertTrue(e.getMessage().contains(".object[2]"), "Message should contain path");
			assertTrue(e.getMessage().contains("3") && e.getMessage().contains("4"),
					"Message should contain both values.");
		}
	}

	@Test
	public void objectContainingArrayWithSameObjectValuesAreEqual() {
		assertObjectsEqual(
				new GenericObjectContainingObject(
						new PrimitiveOnlyObject[] { new PrimitiveOnlyObject(1, 2, false, 'A') }),
				new GenericObjectContainingObject(
						new PrimitiveOnlyObject[] { new PrimitiveOnlyObject(1, 2, false, 'A') }));
	}

	@Test
	public void objectContainingArrayWithDifferentObjectValuesAreNotEqual() {
		try {
			assertObjectsEqual(
					new GenericObjectContainingObject(
							new PrimitiveOnlyObject[] { new PrimitiveOnlyObject(1, 2, false, 'A') }),
					new GenericObjectContainingObject(
							new PrimitiveOnlyObject[] { new PrimitiveOnlyObject(3, 4, false, 'B') }));
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is:
			// "Expected '.object[0].intValue' to be '1' but was '3'.
			//  Expected '.object[0].doubleValue' to be '2.0' but was '4.0'.
			//  Expected '.object[0].charValue' to be 'A' but was 'B'."
			assertTrue(e.getMessage().contains("object[0].intValue"), "Message should contain path");
			assertTrue(e.getMessage().contains("1") && e.getMessage().contains("3"),
					"Message should contain both values.");
		}
	}
}
