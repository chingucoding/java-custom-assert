package utils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import mocks.GenericObjectContainingObject;
import mocks.PrimitiveOnlyObject;
import mocks.TreeNodeObject;

import static org.junit.jupiter.api.Assertions.fail;
import static utils.CustomAssert.assertObjectsEqual;
import static utils.CustomAssertTests.generateNestedObjects;

import java.util.HashSet;
import java.util.List;

public class CustomAssertSetTests {

	@Test
	public void setOfPrimitivesWithSameValuesAreEqual() {
		assertObjectsEqual(new HashSet<>(List.of('A', 'B', 'C')),
				new HashSet<>(List.of('A', 'B', 'C')));
	}

	@Test
	public void setOfDifferentSizesAreNotEqual() {
		try {
			assertObjectsEqual(new HashSet<>(List.of('A', 'B', 'C')),
					new HashSet<>(List.of('A', 'B', 'C', 'D')));
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '' to be of size 3 but was of size 4."
			assertTrue(e.getMessage().contains("3") && e.getMessage().contains("4"),
					"Message should contain both sizes.");
		}
	}

	@Test
	public void setOfPrimitivesWithDifferentValuesAreNotEqual() {
		try {
			assertObjectsEqual(new HashSet<>(List.of(1, 2, 3)), new HashSet<>(List.of(1, 2, 4)));
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '' to contain same items, however 2 items were different: [3, 4]."
			assertTrue(e.getMessage().contains("2"), "Message should contain number of different elements");
			assertTrue(e.getMessage().contains("3") && e.getMessage().contains("4"),
					"Message should contain both values.");
		}
	}

	@Test
	public void setOfPrimitiveOnlyObjectsWithSameValuesAreEqual() {
		final var expected = new HashSet<PrimitiveOnlyObject>();
		expected.add(new PrimitiveOnlyObject(1, 2, false, 'A'));
		expected.add(new PrimitiveOnlyObject(1, 2, false, 'A'));

		final var actual = new HashSet<PrimitiveOnlyObject>();
		actual.add(new PrimitiveOnlyObject(1, 2, false, 'A'));
		actual.add(new PrimitiveOnlyObject(1, 2, false, 'A'));
		assertObjectsEqual(expected, actual);
	}

	@Test
	public void setOfPrimitiveOnlyObjectsWithDifferentValuesAreNotEqual() {
		try {
			assertObjectsEqual(
					new HashSet<>(List.of(new PrimitiveOnlyObject(1, 2, false, 'A'),
							new PrimitiveOnlyObject(1, 2, false, 'A'))),
					new HashSet<>(List.of(new PrimitiveOnlyObject(1, 2, false, 'A'),
							new PrimitiveOnlyObject(1, 2, false, 'B'))));
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '' to contain same items, however 1 items were different: [mocks.PrimitiveOnlyObject@80169cf]."
			assertTrue(e.getMessage().contains("1 items"), "Message should correctly identify the number of missing items");
		}
	}

	@Test
	public void setOfNestedObjectsWithSameValuesAreEqual() {
		assertObjectsEqual(
				new HashSet<>(List.of(generateNestedObjects(3, new TreeNodeObject("Some text 0")),
						generateNestedObjects(2, new TreeNodeObject("Some text 1")))),
				new HashSet<>(List.of(generateNestedObjects(3, new TreeNodeObject("Some text 0")),
						generateNestedObjects(2, new TreeNodeObject("Some text 1")))));
	}

	@Test
	public void setOfNestedObjectsWithDifferentValuesTopLevelPropertiesAreNotEqual() {
		try {
			assertObjectsEqual(
					new HashSet<>(List.of(generateNestedObjects(3, new TreeNodeObject("Some text 0")),
							generateNestedObjects(2, new TreeNodeObject("Some text 1")))),
					new HashSet<>(List.of(generateNestedObjects(3, new TreeNodeObject("Some text 0")),
							generateNestedObjects(2, new TreeNodeObject("Some text 2")))));
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '' to contain same items, however 2 items were different: [mocks.TreeNodeObject@73d4cc9e, mocks.TreeNodeObject@5427c60c]."
			assertTrue(e.getMessage().contains("2 items"), "Message should correctly identify the number of missing items");
		}
	}

	@Test
	public void setOfNestedObjectsWithDifferentValuesInNestingAreNotEqual() {
		try {
			assertObjectsEqual(
					new HashSet<>(List.of(generateNestedObjects(3, new TreeNodeObject("Some text 0")),
							generateNestedObjects(2, new TreeNodeObject("Some text 1")))),
					new HashSet<>(List.of(generateNestedObjects(3, new TreeNodeObject("Some text 0")),
							generateNestedObjects(3, new TreeNodeObject("Some text 1")))));
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '' to contain same items, however 2 items were different: [mocks.TreeNodeObject@73d4cc9e, mocks.TreeNodeObject@5427c60c]."
			assertTrue(e.getMessage().contains("2 items"), "Message should correctly identify the number of missing items");
		}
	}

	@Test
	public void objectContainingDifferentSizedSetsAreNotEqual() {
		try {
			assertObjectsEqual(new GenericObjectContainingObject(new HashSet<>(List.of('A', 'B', 'C'))),
					new GenericObjectContainingObject(new HashSet<>(List.of('A', 'B', 'C', 'D'))));
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '.object' to be of size 3 but was of size 4."
			assertTrue(e.getMessage().contains(".object"), "Message should contain path");
			assertTrue(e.getMessage().contains("3") && e.getMessage().contains("4"),
					"Message should contain both sizes.");
		}
	}

	@Test
	public void objectContainingSetWithSamePrimitiveValuesAreEqual() {
		assertObjectsEqual(new GenericObjectContainingObject(new HashSet<>(List.of(1, 2, 3))),
				new GenericObjectContainingObject(new HashSet<>(List.of(1, 2, 3))));
	}

	@Test
	public void objectContainingSetWithDifferentPrimitiveValuesAreNotEqual() {
		try {
			assertObjectsEqual(new GenericObjectContainingObject(new HashSet<>(List.of(1, 2, 3))),
					new GenericObjectContainingObject(new HashSet<>(List.of(1, 2, 4))));
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '.object' to contain same items, however 2 items were different: [3, 4]."
			assertTrue(e.getMessage().contains(".object"), "Message should contain path");
			assertTrue(e.getMessage().contains("3") && e.getMessage().contains("4"),
					"Message should contain both values.");
		}
	}

	@Test
	public void objectContainingSetWithSameObjectValuesAreEqual() {
		assertObjectsEqual(
				new GenericObjectContainingObject(
						new HashSet<>(List.of(new PrimitiveOnlyObject(1, 2, false, 'A')))),
				new GenericObjectContainingObject(
						new HashSet<>(List.of(new PrimitiveOnlyObject(1, 2, false, 'A')))));
	}

	@Test
	public void objectContainingSetWithDifferentObjectValuesAreNotEqual() {
		try {
			assertObjectsEqual(
					new GenericObjectContainingObject(
							new HashSet<>(List.of(new PrimitiveOnlyObject(1, 2, false, 'A')))),
					new GenericObjectContainingObject(
							new HashSet<>(List.of(new PrimitiveOnlyObject(3, 4, false, 'B')))));
			fail("Should have thrown an exception");
		} catch (AssertionError e) {
			// Message is: "Expected '.object' to contain same items, however 2 items were different: [mocks.PrimitiveOnlyObject@382db087, mocks.PrimitiveOnlyObject@73d4cc9e]."
			assertTrue(e.getMessage().contains(".object"), "Message should contain path");
			assertTrue(e.getMessage().contains("2"),
					"Message should contain both values.");
		}
	}
}
