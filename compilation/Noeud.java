package compilation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Benjamin Aparicio & Pascal Nguyen
 *
 */
public class Noeud {

	public static enum Category {
		NOEUD_OUT("out"), //
		NOEUD_ID("ident"), //
		NOEUD_VAL("val"), //
		NOEUD_IF("if"), //
		NOEUD_ELSE("else"), //
		NOEUD_FOR("for"), //
		NOEUD_WHILE("while"), //
		NOEUD_DO("do"), //
		NOEUD_BREAK("break"), //
		NOEUD_CONTINUE("continue"), //
		NOEUD_RETURN("return"), //
		NOEUD_INT("int"), //
		NOEUD_VOID("void"), //
		NOEUD_OPEN_PAR("("), //
		NOEUD_CLOSE_PAR(")"), //
		NOEUD_OPEN_ACO("{"), //
		NOEUD_CLOSE_ACO("}"), //
		NOEUD_OPEN_CRO("["), //
		NOEUD_CLOSE_CRO("]"), //
		NOEUD_PV(";"), //
		NOEUD_PLUS("+"), //
		NOEUD_MINUS("-"), //
		NOEUD_MINUS_UN("-"), //
		NOEUD_STAR("*"), //
		NOEUD_DIV("/"), //
		NOEUD_PERCENTAGE("%"), //
		NOEUD_DEG("=="), //
		NOEUD_DIF("!="), //
		NOEUD_BEL("<"), //
		NOEUD_ABO(">"), //
		NOEUD_BELOQ("<="), //
		NOEUD_ABOOQ(">="), //
		NOEUD_AND("&&"), //
		NOEUD_OR("||"), //
		NOEUD_NOT("!"), //
		//NOEUD_POINT("&"), //
		NOEUD_AT("@"), //
		NOEUD_ASSIGN("="), //
		NOEUD_BLOCK(null), //
		NOEUD_VARDEC(null), //
		NOEUD_VARREF(null), //
		NOEUD_AFFEC(null), //
		NOEUD_LOOP(null);

		private String name;

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

	public String getIdentifiant() {
		return identifiant;
	}

	public void setIdentifiant(String identifiant) {
		this.identifiant = identifiant;
	}

	private Integer value;
	private List<Noeud> children;
	private int line;
	private int column;
	private int slot;
	private String comment = null;

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public Noeud(Category category, String identifiant, Integer value, List<Noeud> children, int line, int column) {
		this.category = category;
		this.identifiant = identifiant;
		this.value = value;

		if (children == null) {
			this.children = new ArrayList<Noeud>();
		} else {
			this.children = children;
		}

		this.line = line;
		this.column = column;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Noeud> getChildren() {
		return children;
	}

	public Noeud getChild(int enfantIndice) {
		return children.get(enfantIndice);
	}

	public void setChildren(List<Noeud> children) {
		this.children = children;
	}

	public Integer getValue() {
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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
				retrievedCategory = Category.NOEUD_VAL;
			} else {
				retrievedCategory = Category.NOEUD_ID;
			}
		}

		return retrievedCategory;
	}

	@Override
	public String toString() {
		String token = "Token category: " + category //
				+ " ; identifiant: " + identifiant //
				+ " ; value: " + value + " ; children: "; //

		for (Noeud child : children) {
			token += child.getNoeudStringName() + " ";
		}

		return token;
	}

	public String getNoeudStringName() {
		String name;

		switch (category) {
		case NOEUD_ID:
			name = identifiant;
			break;
		case NOEUD_VAL:
			name = "" + value;
			break;
		default:
			name = category.toString();
		}

		return name;
	}

	public void showNoeudsRecursively() {
		List<Noeud> noeuds = new ArrayList<Noeud>();
		noeuds.add(this);

		while (!noeuds.isEmpty()) {
			Noeud temp = noeuds.remove(0);
			temp.showNoeudAndChildren();
			noeuds.addAll(temp.getChildren());
		}
	}

	public void showNoeudAndChildren() {
		System.out.print("Noeud: ");

		showNoeud();

		System.out.print(" ; enfants: ");

		for (Noeud child : children) {
			child.showNoeud();
			System.out.print(" ");
		}

		System.out.println();
	}

	public void showNoeud() {
		switch (category) {
		case NOEUD_ID:
			System.out.print(identifiant);
			break;
		case NOEUD_VAL:
			System.out.print(value);
			break;
		default://
			switch (category) {
			case NOEUD_BLOCK:
				System.out.print("block");
				break;
			case NOEUD_VARDEC:
				System.out.print("vardec");
				break;
			case NOEUD_VARREF:
				System.out.print("varrec");
				break;
			case NOEUD_AFFEC:
				System.out.print("affec");
				break;
			case NOEUD_LOOP:
				System.out.print("loop");
				break;
			default:
				System.out.print(category);
			}
			;
		}
	}

}