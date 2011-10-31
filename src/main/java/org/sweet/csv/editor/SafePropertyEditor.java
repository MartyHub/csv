package org.sweet.csv.editor;

import java.beans.PropertyEditorSupport;

public abstract class SafePropertyEditor<T> extends PropertyEditorSupport {

	@SuppressWarnings("unchecked")
	@Override
	public String getAsText() {
		Object value = getValue();

		if (value == null) {
			return null;
		} else {
			return doGetAsText((T) value);
		}
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (text == null || "".equals(text)) {
			setValue(null);
		} else {
			T value = fromText(text);

			setValue(value);
		}
	}

	protected abstract String doGetAsText(T value);

	protected abstract T fromText(String text);
}
