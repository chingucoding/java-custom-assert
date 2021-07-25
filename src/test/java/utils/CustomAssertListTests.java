package utils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import mocks.GenericObjectContainingObject;
import mocks.PrimitiveOnlyObject;
import mocks.TreeNodeObject;

import static org.junit.jupiter.api.Assertions.fail;
import static utils.CustomAssert.assertObjectsEqual;
import static utils.CustomAssertTests.generateNestedObjects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CustomAssertListTests {

	@Test
	public void listOfPrimitivesWithSameValuesAreEqual() {
		assertObjectsEqual(List.of('A', 'B', 'C'), List.of('A', 'B', 'C'));
	}

	@Test
	public void listOfDifferentSizesAreNotEqual() {
		try {
			assertObjectsEqual(List.of('A', 'B', 'C'), List.of('A', 'B', 'C', 'D'));
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '' to be of size 3 but was of size 4."
			assertTrue(e.getMessage().contains("3") && e.getMessage().contains("4"),
					"Message should contain both sizes.");
		}
	}

	@Test
	public void listOfPrimitivesWithDifferentValuesAreNotEqual() {
		try {
			assertObjectsEqual(List.of(1, 2, 3), List.of(1, 2, 4));
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '[2]' to be '3' but was '4'."
			assertTrue(e.getMessage().contains("[2]"), "Message should contain path");
			assertTrue(e.getMessage().contains("4") && e.getMessage().contains("3"),
					"Message should contain both values.");
		}
	}

	@Test
	public void listOfPrimitiveOnlyObjectsWithSameValuesAreEqual() {
		final var expected = new ArrayList<PrimitiveOnlyObject>();
		expected.add(new PrimitiveOnlyObject(1, 2, false, 'A'));
		expected.add(new PrimitiveOnlyObject(1, 2, false, 'A'));

		final var actual = new ArrayList<PrimitiveOnlyObject>();
		actual.add(new PrimitiveOnlyObject(1, 2, false, 'A'));
		actual.add(new PrimitiveOnlyObject(1, 2, false, 'A'));
		assertObjectsEqual(expected, actual);
	}

	@Test
	public void listOfPrimitiveOnlyObjectsWithDifferentValuesAreNotEqual() {
		try {
			assertObjectsEqual(
					Arrays.asList(new PrimitiveOnlyObject(1, 2, false, 'A'),
							new PrimitiveOnlyObject(1, 2, false, 'A')),
					Arrays.asList(new PrimitiveOnlyObject(1, 2, false, 'A'),
							new PrimitiveOnlyObject(1, 2, false, 'B')));
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '[1].charValue' to be 'A' but was 'B'."
			assertTrue(e.getMessage().contains("[1].charValue"), "Message should contain path");
			assertTrue(e.getMessage().contains("A") && e.getMessage().contains("B"),
					"Message should contain both values.");
		}
	}

	@Test
	public void listOfNestedObjectsWithSameValuesAreEqual() {
		assertObjectsEqual(
				Arrays.asList(generateNestedObjects(3, new TreeNodeObject("Some text 0")),
						generateNestedObjects(2, new TreeNodeObject("Some text 1"))),
				Arrays.asList(generateNestedObjects(3, new TreeNodeObject("Some text 0")),
						generateNestedObjects(2, new TreeNodeObject("Some text 1"))));
	}

	@Test
	public void listOfNestedObjectsWithDifferentValuesTopLevelPropertiesAreNotEqual() {
		try {
			assertObjectsEqual(
					Arrays.asList(generateNestedObjects(3, new TreeNodeObject("Some text 0")),
							generateNestedObjects(2, new TreeNodeObject("Some text 1"))),
					Arrays.asList(generateNestedObjects(3, new TreeNodeObject("Some text 0")),
							generateNestedObjects(2, new TreeNodeObject("Some text 2"))));
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '[1].name' to be 'Some text 1' but was 'Some text 2'."
			assertTrue(e.getMessage().contains("[1].name"), "Message should contain path");
			assertTrue(e.getMessage().contains("Some text 1") && e.getMessage().contains("Some text 2"),
					"Message should contain both values.");
		}
	}

	@Test
	public void listOfNestedObjectsWithDifferentValuesInNestingAreNotEqual() {
		try {
			assertObjectsEqual(
					Arrays.asList(generateNestedObjects(3, new TreeNodeObject("Some text 0")),
							generateNestedObjects(2, new TreeNodeObject("Some text 1"))),
					Arrays.asList(generateNestedObjects(3, new TreeNodeObject("Some text 0")),
							generateNestedObjects(3, new TreeNodeObject("Some text 1"))));
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
	public void objectContainingDifferentSizedListsAreNotEqual() {
		try {
			assertObjectsEqual(new GenericObjectContainingObject(List.of('A', 'B', 'C')),
					new GenericObjectContainingObject(List.of('A', 'B', 'C', 'D')));
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '.object' to be of size 3 but was of size 4."
			assertTrue(e.getMessage().contains(".object"), "Message should contain path");
			assertTrue(e.getMessage().contains("3") && e.getMessage().contains("4"),
					"Message should contain both sizes.");
		}
	}

	@Test
	public void objectContainingListWithSamePrimitiveValuesAreEqual() {
		assertObjectsEqual(new GenericObjectContainingObject(List.of(1, 2, 3)),
				new GenericObjectContainingObject(List.of(1, 2, 3)));
	}

	@Test
	public void objectContainingListWithDifferentPrimitiveValuesAreNotEqual() {
		try {
			assertObjectsEqual(new GenericObjectContainingObject(List.of(1, 2, 3)),
					new GenericObjectContainingObject(List.of(1, 2, 4)));
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '.object[2]' to be '3' but was '4'."
			assertTrue(e.getMessage().contains(".object[2]"), "Message should contain path");
			assertTrue(e.getMessage().contains("3") && e.getMessage().contains("4"),
					"Message should contain both values.");
		}
	}

	@Test
	public void objectContainingListWithSameObjectValuesAreEqual() {
		assertObjectsEqual(
				new GenericObjectContainingObject(
						Collections.singletonList(new PrimitiveOnlyObject(1, 2, false, 'A'))),
				new GenericObjectContainingObject(
						Collections.singletonList(new PrimitiveOnlyObject(1, 2, false, 'A'))));
	}

	@Test
	public void objectContainingListWithDifferentObjectValuesAreNotEqual() {
		try {
			assertObjectsEqual(
					new GenericObjectContainingObject(
							Collections.singletonList(new PrimitiveOnlyObject(1, 2, false, 'A'))),
					new GenericObjectContainingObject(
							Collections.singletonList(new PrimitiveOnlyObject(3, 4, false, 'B'))));
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is:
			// "Expected '.object[0].intValue' to be '1' but was '3'.
			// Expected '.object[0].doubleValue' to be '2.0' but was '4.0'.
			// Expected '.object[0].charValue' to be 'A' but was 'B'."
			assertTrue(e.getMessage().contains("object[0].intValue"), "Message should contain path");
			assertTrue(e.getMessage().contains("1") && e.getMessage().contains("2"),
					"Message should contain both values.");
		}
	}
}