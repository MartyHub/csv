package org.sweet.csv.editor;

public class EnumEditor<E extends Enum<E>> extends SafePropertyEditor<E> {

	private final Class<E> type;

	public EnumEditor(Class<E> type) {
		this.type = type;
	}

	@Override
	protected String doGetAsText(E value) {
		return value.name();
	}

	@Override
	protected E fromText(String text) {
		return Enum.valueOf(type, text);
	}
}
