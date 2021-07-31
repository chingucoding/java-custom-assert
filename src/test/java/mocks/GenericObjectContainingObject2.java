package mocks;

import java.util.Objects;

public class GenericObjectContainingObject2 {
	private final Object object;

	public GenericObjectContainingObject2(Object object) {
		this.object = object;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
			GenericObjectContainingObject2 that = (GenericObjectContainingObject2) o;
		return Objects.equals(object, that.object);
	}
}
