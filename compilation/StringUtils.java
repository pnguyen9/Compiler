package compilation;

/**
 * 
 * @author Pascal Nguyen
 *
 */
public class StringUtils {

	public static boolean isAlphanumeric(String string) {
		boolean isAlphanumeric = false;

		for (char character : string.toCharArray()) {
			isAlphanumeric = Character.isLetterOrDigit(character);
			if (!isAlphanumeric) {
				break;
			}
		}

		return isAlphanumeric;
	}

	public static boolean isNumeric(String string) {
		boolean isAlphanumeric = false;

		for (char character : string.toCharArray()) {
			isAlphanumeric = Character.isDigit(character);
			if (!isAlphanumeric) {
				break;
			}
		}

		return isAlphanumeric;
	}

	public static boolean hasNoAlphanumeric(String string) {
		boolean hasNoAlphanumeric = false;

		for (char character : string.toCharArray()) {
			hasNoAlphanumeric = !Character.isLetterOrDigit(character);
			if (!hasNoAlphanumeric) {
				break;
			}
		}

		return hasNoAlphanumeric;
	}

	public static boolean isBlankString(String string) {
		return string.trim().length() < 1;
	}

	/**
	 * Returns true if the given string is equal to "//", whick marks the beginning
	 * of a commment
	 * 
	 * @param string
	 * @return See description
	 */
	public static boolean isCommentStart(String string) {
		return string.equals("//");
	}

}
