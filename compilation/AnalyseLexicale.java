package compilation;

import java.util.LinkedList;
import java.util.List;

import compilation.Token.Category;

public class AnalyseLexicale {

	private List<Token> tokenList;

	public void loadTokensFromCode(List<String> codeLines) {
		tokenList = new LinkedList<Token>();

		// We loop on each code line
		for (int i = 0; i < codeLines.size(); ++i) {
			String currentCodeLine = codeLines.get(i);

			if (StringUtils.isBlankString(currentCodeLine)) {
				// We ignore empty lines
				continue;
			}

			char[] codesArray = currentCodeLine.toCharArray();

			int column = 0;
			String temp = "";

			// We loop on each character on the current code line
			for (int j = 0; j < currentCodeLine.length(); ++j) {
				if (StringUtils.isBlankString("" + codesArray[j])) {
					if (!StringUtils.isBlankString(temp)) {
						Token token = null;

						if (StringUtils.hasNoAlphanumeric(temp)) {
							// Create a token for non-alphanumeric strings
							Category category = Token.getCategoryFromString(temp);
							token = new Token(category, null, null, i, column);
						} else {
							// Create a token for identifiant, value and keyword
							Category category = Token.getCategoryFromString(temp);
							token = createNewAlphanumericTokenFromParameters(category, temp, i, column);
						}

						tokenList.add(token);
						temp = "";
					}

					// We ignore blank characters
					continue;
				}

				if (StringUtils.isBlankString(temp)) {
					// Init the temp string if it's empty
					column = j;
					temp = "" + codesArray[j];
				} else if (StringUtils.isCommentStart(temp + codesArray[j])) {
					// Since we found a "//" comment beginning, we retrieve the remaining of the
					// line as a comment
					temp = "";
					++j;

					while (j < codesArray.length) {
						temp += codesArray[j++];
					}

					// We record the comment into the last created token
					Token lastToken = ((LinkedList<Token>) tokenList).getLast();
					String lastTokenComment = lastToken.getComment() == null ? "" : lastToken.getComment();
					lastToken.setComment(lastTokenComment + ";" + temp);
					temp = "";

					break; // The line is over so the loop is over
				} else if (StringUtils.hasNoAlphanumeric(temp) && StringUtils.isAlphanumeric("" + codesArray[j])) {
					// Create a token for non-alphanumeric strings
					Category category = Token.getCategoryFromString(temp);
					Token token = new Token(category, null, null, i, column);

					tokenList.add(token);

					column = j;
					temp = "" + codesArray[j];
				} else if (StringUtils.hasNoAlphanumeric(temp)) {
					// We check if the current string isn't already a valid non-alphanumeric
					if (StringUtils.hasNoAlphanumeric(temp + codesArray[j])) {
						// We check if it's a 2 char non-alphanumeric
						Category category = Token.getCategoryFromString(temp + codesArray[j]);

						if (category != null) {
							temp += codesArray[j];
							continue;
						}
					}

					// Otherwise, it's a 1 char non-alphanumeric
					Category category = Token.getCategoryFromString(temp);

					if (category == null) {
						temp += codesArray[j];
						continue;
					}

					Token token = new Token(category, null, null, i, column);

					tokenList.add(token);

					column = j;
					temp = "" + codesArray[j];
				} else if (StringUtils.isAlphanumeric(temp) && !StringUtils.isAlphanumeric("" + codesArray[j])) {
					// Create a token for identifiant, a value or a keyword
					Category category = Token.getCategoryFromString(temp);
					Token token = createNewAlphanumericTokenFromParameters(category, temp, i, column);

					tokenList.add(token);

					column = j;
					temp = "" + codesArray[j];
				} else {
					// We keep checking the next char until we can identify the token
					temp += codesArray[j];
				}
			}

			// We check if temp is blank
			// Basically if the line was ended with a comment
			if (StringUtils.isBlankString(temp)) {
				// We go to the next line
				continue;
			}

			// The last token is handled outside the column loop
			Token token = null;

			if (StringUtils.hasNoAlphanumeric(temp)) {
				// Create a token for non-alphanumeric strings
				Category category = Token.getCategoryFromString(temp);
				token = new Token(category, null, null, i, column);
			} else {
				// Create a token for identifiant, a value or a keyword
				Category category = Token.getCategoryFromString(temp);
				token = createNewAlphanumericTokenFromParameters(category, temp, i, column);
			}

			tokenList.add(token);
		}
	}

	private Token createNewAlphanumericTokenFromParameters(Category category, String value, int line, int column) {
		Token token = null;

		if (category.equals(Category.TOKEN_ID)) {
			// Create an identifiant token
			token = new Token(category, value, null, line, column);
		} else if (category.equals(Category.TOKEN_VAL)) {
			// Create a value token
			token = new Token(category, null, Integer.parseInt(value), line, column);
		} else {
			// Create a keyword token (int, if, break, return, etc.)
			token = new Token(category, null, null, line, column);
		}

		return token;
	}

	/**
	 * Remove the next element of the token list and return it.
	 * 
	 * @return see description
	 */
	public Token next() {
		Token token = Token.TOKEN_END;

		if (!tokenList.isEmpty()) {
			token = tokenList.remove(0);
		}

		return token;
	}

	/**
	 * Return the next element of the token list.
	 * 
	 * @return see description
	 */
	public Token look() {
		Token token = Token.TOKEN_END;

		if (!tokenList.isEmpty()) {
			token = tokenList.get(0);
		}

		return token;
	}

	/**
	 * Return the element after the next element of the token list.
	 * 
	 * @return see description
	 */
	public Token lookNext() {
		Token token = Token.TOKEN_END;

		if (!tokenList.isEmpty() && tokenList.size() > 1) {
			token = tokenList.get(1);
		}

		return token;
	}

	public void displayTokens() {
		for (Token token : tokenList) {
			System.out.println(token);
		}
	}

	public void displayTokensAsString() {
		for (Token token : tokenList) {
			System.out.println(token.getTokenStringName());
		}
	}

	public void displayTokensAsCode() {
		for (Token token : tokenList) {
			System.out.print(token.getTokenStringName());
		}
	}

}
