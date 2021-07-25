package mocks;

@SuppressWarnings("ClassCanBeRecord")
public class PrimitiveOnlyObject {
	private final int intValue;
	private final double doubleValue;
	private final boolean booleanValue;
	private final char charValue;

	public PrimitiveOnlyObject(int intValue, double doubleValue, boolean booleanValue, char charValue) {
		this.intValue = intValue;
		this.doubleValue = doubleValue;
		this.booleanValue = booleanValue;
		this.charValue = charValue;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PrimitiveOnlyObject that = (PrimitiveOnlyObject) o;
		return intValue == that.intValue && doubleValue == that.doubleValue && booleanValue == that.booleanValue
				&& charValue == that.charValue;
	}
}
