package mocks;

import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
public class GenericObjectContainingObject {
	private final Object object;

	public GenericObjectContainingObject(Object object) {
		this.object = object;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		GenericObjectContainingObject that = (GenericObjectContainingObject) o;
		return Objects.equals(object, that.object);
	}
}
