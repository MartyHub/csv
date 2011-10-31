package org.sweet.csv.editor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateEditor extends SafePropertyEditor<Date> {

	public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private final String pattern;

	private final SimpleDateFormat format;

	public DateEditor(String pattern) {
		this.pattern = pattern == null ? DEFAULT_PATTERN : pattern;
		this.format = new SimpleDateFormat(this.pattern);
	}

	@Override
	protected String doGetAsText(Date value) {
		return format.format(value);
	}

	@Override
	protected Date fromText(String text) {
		try {
			return format.parse(text);
		} catch (ParseException pe) {
			throw new IllegalArgumentException("Could not parse date <" + text
					+ "> with pattern <" + pattern + ">", pe);
		}
	}
}
