package mocks;

import java.util.Objects;

public class TreeNodeObject {
	private TreeNodeObject child;
	private String name;

	public TreeNodeObject(String name) {
		this.name = name;
	}

	public void setChild(TreeNodeObject child) {
		this.child = child;
	}

	public TreeNodeObject getChild() {
		return child;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TreeNodeObject that = (TreeNodeObject) o;
		return name.equals(that.name) && (Objects.equals(child, that.child));
	}
}
