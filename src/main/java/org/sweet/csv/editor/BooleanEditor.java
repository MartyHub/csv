package org.sweet.csv.editor;

public class BooleanEditor extends SafePropertyEditor<Boolean> {

	@Override
	protected String doGetAsText(Boolean value) {
		if (value != null) {
			return value.toString();
		}

		return null;
	}

	@Override
	protected Boolean fromText(String text) {
		return "1".equals(text) || "yes".equalsIgnoreCase(text) || "true".equalsIgnoreCase(text);
	}
}
