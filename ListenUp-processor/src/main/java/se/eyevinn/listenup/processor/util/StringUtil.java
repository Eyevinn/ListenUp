package se.eyevinn.listenup.processor.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 
 * @author Mattias Selin
 *
 */
public class StringUtil {
	private static Locale LOCALE = Locale.ENGLISH;

	public static IStringJoiner joiner(String separator) {
		return new StringJoiner(separator);
	}
	
	public static String join(String separator, Object ... objects) {
		StringBuilder stringBuilder = new StringBuilder();
		boolean first = true;
		for(Object object : objects) {
			if(first) {
				first = false;
			} else {
				stringBuilder.append(separator);
			}
			stringBuilder.append(object);
		}
		return stringBuilder.toString();
	}
	
	public interface IStringJoiner {
		IStringJoiner add(Object object);
	}
	
	private static class StringJoiner implements IStringJoiner {
		private final String separator;
		private final List<String> objects = new ArrayList<>();
		
		public StringJoiner(String separator) {
			this.separator = separator;
		}

		@Override
		public IStringJoiner add(Object object) {
			objects.add(String.valueOf(object));
			return this;
		}
		
		@Override
		public String toString() {
			return join(separator, objects.toArray());
		}
	}

	public static String capitalize(String name) {
		if(name != null && name.length() > 0) {
			return name.substring(0, 1).toUpperCase(LOCALE)+name.substring(1);
		} else {
			return name;
		}
	}
}
