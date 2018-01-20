package compilation;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Benjamin Aparicio & Pascal Nguyen
 *
 */
public class Token {

	public final static Token TOKEN_END = new Token(Category.TOKEN_END, null, null, 0, 0);

	public static enum Category {
		TOKEN_OUT("out"), //
		TOKEN_ID("ident"), //
		TOKEN_VAL("val"), //
		TOKEN_IF("if"), //
		TOKEN_ELSE("else"), //
		TOKEN_FOR("for"), //
		TOKEN_WHILE("while"), //
		TOKEN_DO("do"), //
		TOKEN_BREAK("break"), //
		TOKEN_CONTINUE("continue"), //
		TOKEN_RETURN("return"), //
		TOKEN_INT("int"), //
		TOKEN_VOID("void"), //
		TOKEN_OPEN_PAR("("), //
		TOKEN_CLOSE_PAR(")"), //
		TOKEN_OPEN_ACO("{"), //
		TOKEN_CLOSE_ACO("}"), //
		TOKEN_OPEN_CRO("["), //
		TOKEN_CLOSE_CRO("]"), //
		TOKEN_PV(";"), //
		TOKEN_PLUS("+"), //
		TOKEN_MINUS("-"), //
		TOKEN_STAR("*"), //
		TOKEN_DIV("/"), //
		TOKEN_PERCENTAGE("%"), //
		TOKEN_DEG("=="), //
		TOKEN_DIF("!="), //
		TOKEN_BEL("<"), //
		TOKEN_ABO(">"), //
		TOKEN_BELOQ("<="), //
		TOKEN_ABOOQ(">="), //
		TOKEN_AND("&&"), //
		TOKEN_OR("||"), //
		TOKEN_NOT("!"), //
		// TOKEN_POINT("&"), //
		TOKEN_AT("@"), //
		TOKEN_ASSIGN("="), //
		TOKEN_END("");

		private final String name;

		Category(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	private static Map<String, Category> categories;

	private Category category;
	private String identifiant;
	private Integer value;
	private int line;
	private int column;
	private String comment = null;

	public Token(Category category, String identifiant, Integer value, int line, int column) {
		this.category = category;
		this.identifiant = identifiant;
		this.value = value;
		this.line = line;
		this.column = column;
	}

	/**
	 * Returns a Category enum for a given string.
	 * 
	 * @param category
	 *            The string to be checked
	 * @return See description
	 */
	public static Category getCategoryFromString(String category) {
		// Init the static categories map if it's not initialised yet
		if (categories == null) {
			categories = new HashMap<String, Category>();

			for (Category cat : Category.values()) {
				categories.put(cat.toString(), cat);
			}
		}

		Category retrievedCategory = categories.get(category);

		// If no category was found for the given string,
		// we check if it's numeric or alphanumeric
		if (retrievedCategory == null) {
			if (StringUtils.isNumeric(category)) {
				retrievedCategory = Category.TOKEN_VAL;
			} else if (StringUtils.isAlphanumeric(category)) {
				retrievedCategory = Category.TOKEN_ID;
			}
		}

		return retrievedCategory;
	}

	public String getIdentifiant() {
		return identifiant;
	}

	public void setIdentifiant(String identifiant) {
		this.identifiant = identifiant;
	}

	public int getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		String token = "Token category: " + category //
				+ " ; identifiant: " + identifiant //
				+ " ; value: " + value //
				+ " ; line: " + line //
				+ " ; column: " + column;
		return token;
	}

	public String getTokenStringName() {
		String name;

		switch (category) {
		case TOKEN_ID:
			name = identifiant;
			break;
		case TOKEN_VAL:
			name = "" + value;
			break;
		default:
			name = category.toString();
		}

		return name;
	}

}
