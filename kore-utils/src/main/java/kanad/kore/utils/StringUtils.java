package kanad.kore.utils;

public class StringUtils {

	public static boolean isValidString (String inpString) {
		return inpString != null && !inpString.isEmpty();
	}
	
	
	/**
	 * 
	 * @param fieldName
	 * @return the method name corresponding to the given field name as per the JavaBeans naming convention.
	 * Current implementation doesn't completely adhere to JavaBeans conventions for e.g. for boolean, method name starts with is<FieldName>. 
	 * Refer: 
	 * https://en.wikipedia.org/wiki/JavaBeans
	 * TODO: it would be better to pass fieldName as Field rather than String so as to build the method name completely as per conventions.
	 */
	public static String getMethodName(String fieldName){
		//Can't use replace; it replaces all matching chars in the string not just 0th one. 
		StringBuffer sb = new StringBuffer(fieldName);
		char firstChar = fieldName.charAt(0);
		sb.deleteCharAt(0);
		sb.insert(0, Character.toUpperCase(firstChar));
		sb.insert(0,"get");
		return sb.toString();
	}
}
