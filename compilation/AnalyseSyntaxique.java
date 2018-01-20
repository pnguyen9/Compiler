package compilation;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Benjamin Aparicio & Pascal Nguyen
 *
 */

public class AnalyseSyntaxique {

	private AnalyseLexicale analyseLexicale;
	private Noeud arbre;

	public AnalyseSyntaxique(AnalyseLexicale analyseLexicale) {
		this.analyseLexicale = analyseLexicale;
	}

	private Noeud P(Token token) throws CompilationException {
		Noeud noeud = null;

		if (token.getCategory().equals(Token.Category.TOKEN_VAL)) {
			noeud = new Noeud(Noeud.Category.NOEUD_VAL, null, token.getValue(), null, token.getLine(),
					token.getColumn());
		}

		if (token.getCategory().equals(Token.Category.TOKEN_ID)) {
			noeud = new Noeud(Noeud.Category.NOEUD_ID, token.getIdentifiant(), null, null, token.getLine(),
					token.getColumn());
		}

		if (token.getCategory().equals(Token.Category.TOKEN_MINUS)) {
			Noeud tempNoeud = P(analyseLexicale.next());
			List<Noeud> ln = new ArrayList<Noeud>();
			ln.add(tempNoeud);
			noeud = new Noeud(Noeud.Category.NOEUD_MINUS_UN, null, null, ln, token.getLine(), token.getColumn());
		}

		if (token.getCategory().equals(Token.Category.TOKEN_NOT)) {
			Noeud tempNoeud = P(analyseLexicale.next());
			List<Noeud> ln = new ArrayList<Noeud>();
			ln.add(tempNoeud);
			noeud = new Noeud(Noeud.Category.NOEUD_NOT, null, null, ln, token.getLine(), token.getColumn());
		}

		if (token.getCategory().equals(Token.Category.TOKEN_OPEN_PAR)) {
			noeud = S(analyseLexicale.next());

			// Check if it's an empty parenthesis
			if (noeud == null) {
				throw new CompilationException("Erreur : Aucune expression dans la parenthèse.", token.getLine(),
						token.getColumn());
			}

			// Check if the parenthesis was closed
			Token.Category nextTokenCategory = analyseLexicale.next().getCategory();
			if (!nextTokenCategory.equals(Token.Category.TOKEN_CLOSE_PAR)) {
				throw new CompilationException("Erreur : Parenthèse ouverte non refermée.", token.getLine(),
						token.getColumn());
			}
		}

		return noeud;
	}

	private Noeud F(Token token) throws CompilationException {
		Noeud returnNoeud = null;
		Noeud noeud = P(token);

		if (noeud == null) {
			return noeud;
		}

		Token nextToken = analyseLexicale.look(); // On récup le token suivant sans avancer
		if (nextToken.getCategory().equals(Token.Category.TOKEN_STAR)) {
			analyseLexicale.next();
			Noeud noeud2 = F(analyseLexicale.next());
			List<Noeud> children = new ArrayList<Noeud>();
			children.add(noeud);
			children.add(noeud2);
			returnNoeud = new Noeud(Noeud.Category.NOEUD_STAR, null, null, children, nextToken.getLine(),
					nextToken.getColumn());
		} else if (nextToken.getCategory().equals(Token.Category.TOKEN_DIV)) {
			analyseLexicale.next();
			Noeud noeud2 = F(analyseLexicale.next());
			List<Noeud> children = new ArrayList<Noeud>();
			children.add(noeud);
			children.add(noeud2);
			returnNoeud = new Noeud(Noeud.Category.NOEUD_DIV, null, null, children, nextToken.getLine(),
					nextToken.getColumn());
		} else if (nextToken.getCategory().equals(Token.Category.TOKEN_PERCENTAGE)) {
			analyseLexicale.next();
			Noeud noeud2 = F(analyseLexicale.next());
			List<Noeud> children = new ArrayList<Noeud>();
			children.add(noeud);
			children.add(noeud2);
			returnNoeud = new Noeud(Noeud.Category.NOEUD_PERCENTAGE, null, null, children, nextToken.getLine(),
					nextToken.getColumn());
		} else {
			returnNoeud = noeud;
		}

		return returnNoeud;
	}

	private Noeud T(Token token) throws CompilationException {
		Noeud returnNoeud = null;
		Noeud noeud = F(token);

		if (noeud == null) {
			return noeud;
		}

		Token nextToken = analyseLexicale.look(); // On récup le token suivant sans avancer
		if (nextToken.getCategory().equals(Token.Category.TOKEN_PLUS)) {
			analyseLexicale.next();
			Noeud noeud2 = T(analyseLexicale.next());
			List<Noeud> children = new ArrayList<Noeud>();
			children.add(noeud);
			children.add(noeud2);
			returnNoeud = new Noeud(Noeud.Category.NOEUD_PLUS, null, null, children, nextToken.getLine(),
					nextToken.getColumn());
		} else if (nextToken.getCategory().equals(Token.Category.TOKEN_MINUS)) {
			analyseLexicale.next();
			Noeud noeud2 = E(analyseLexicale.next());
			List<Noeud> children = new ArrayList<Noeud>();
			children.add(noeud);
			children.add(noeud2);
			returnNoeud = new Noeud(Noeud.Category.NOEUD_MINUS, null, null, children, nextToken.getLine(),
					nextToken.getColumn());
		} else {
			returnNoeud = noeud;
		}

		return returnNoeud;
	}

	// C
	private Noeud C(Token token) throws CompilationException {
		Noeud returnNoeud = null;
		Noeud noeud = T(token);

		if (noeud == null) {
			return noeud;
		}

		Token nextToken = analyseLexicale.look(); // On récup le token suivant sans avancer
		// ==
		if (nextToken.getCategory().equals(Token.Category.TOKEN_DEG)) {
			analyseLexicale.next();
			Noeud noeud2 = C(analyseLexicale.next());
			List<Noeud> children = new ArrayList<Noeud>();
			children.add(noeud);
			children.add(noeud2);
			returnNoeud = new Noeud(Noeud.Category.NOEUD_DEG, null, null, children, nextToken.getLine(),
					nextToken.getColumn());
			// !=
		} else if (nextToken.getCategory().equals(Token.Category.TOKEN_DIF)) {
			analyseLexicale.next();
			Noeud noeud2 = C(analyseLexicale.next());
			List<Noeud> children = new ArrayList<Noeud>();
			children.add(noeud);
			children.add(noeud2);
			returnNoeud = new Noeud(Noeud.Category.NOEUD_DIF, null, null, children, nextToken.getLine(),
					nextToken.getColumn());
			// <
		} else if (nextToken.getCategory().equals(Token.Category.TOKEN_BEL)) {
			analyseLexicale.next();
			Noeud noeud2 = C(analyseLexicale.next());
			List<Noeud> children = new ArrayList<Noeud>();
			children.add(noeud);
			children.add(noeud2);
			returnNoeud = new Noeud(Noeud.Category.NOEUD_BEL, null, null, children, nextToken.getLine(),
					nextToken.getColumn());
			// <=
		} else if (nextToken.getCategory().equals(Token.Category.TOKEN_BELOQ)) {
			analyseLexicale.next();
			Noeud noeud2 = C(analyseLexicale.next());
			List<Noeud> children = new ArrayList<Noeud>();
			children.add(noeud);
			children.add(noeud2);
			returnNoeud = new Noeud(Noeud.Category.NOEUD_BELOQ, null, null, children, nextToken.getLine(),
					nextToken.getColumn());
			// =>
		} else if (nextToken.getCategory().equals(Token.Category.TOKEN_ABOOQ)) {
			analyseLexicale.next();
			Noeud noeud2 = C(analyseLexicale.next());
			List<Noeud> children = new ArrayList<Noeud>();
			children.add(noeud);
			children.add(noeud2);
			returnNoeud = new Noeud(Noeud.Category.NOEUD_ABOOQ, null, null, children, nextToken.getLine(),
					nextToken.getColumn());
			// >
		} else if (nextToken.getCategory().equals(Token.Category.TOKEN_ABO)) {
			analyseLexicale.next();
			Noeud noeud2 = C(analyseLexicale.next());
			List<Noeud> children = new ArrayList<Noeud>();
			children.add(noeud);
			children.add(noeud2);
			returnNoeud = new Noeud(Noeud.Category.NOEUD_ABO, null, null, children, nextToken.getLine(),
					nextToken.getColumn());
			// Other
		} else {
			returnNoeud = noeud;
		}

		return returnNoeud;
	}

	// L
	private Noeud L(Token token) throws CompilationException {
		Noeud returnNoeud = null;
		Noeud noeud = C(token);

		if (noeud == null) {
			return noeud;
		}

		Token nextToken = analyseLexicale.look(); // On récup le token suivant sans avancer
		// &&
		if (nextToken.getCategory().equals(Token.Category.TOKEN_AND)) {
			analyseLexicale.next();
			Noeud noeud2 = L(analyseLexicale.next());
			List<Noeud> children = new ArrayList<Noeud>();
			children.add(noeud);
			children.add(noeud2);
			returnNoeud = new Noeud(Noeud.Category.NOEUD_AND, null, null, children, nextToken.getLine(),
					nextToken.getColumn());
		} else {
			returnNoeud = noeud;
		}

		return returnNoeud;
	}

	// E
	private Noeud E(Token token) throws CompilationException {
		Noeud returnNoeud = null;
		Noeud noeud = L(token);

		if (noeud == null) {
			return noeud;
		}

		Token nextToken = analyseLexicale.look(); // On récup le token suivant sans avancer
		// ||
		if (nextToken.getCategory().equals(Token.Category.TOKEN_OR)) {
			analyseLexicale.next();
			Noeud noeud2 = E(analyseLexicale.next());
			List<Noeud> children = new ArrayList<Noeud>();
			children.add(noeud);
			children.add(noeud2);
			returnNoeud = new Noeud(Noeud.Category.NOEUD_OR, null, null, children, nextToken.getLine(),
					nextToken.getColumn());
		} else {
			returnNoeud = noeud;
		}

		return returnNoeud;
	}

	private Noeud A(Token token) throws CompilationException {
		analyseLexicale.next(); // On retire le =
		List<Noeud> children = new ArrayList<Noeud>();
		Noeud child = E(analyseLexicale.next());
		children.add(child);
		Noeud returnNoeud = new Noeud(Noeud.Category.NOEUD_AFFEC, token.getIdentifiant(), null, children,
				token.getLine(), token.getColumn());

		return returnNoeud;
	}

	private Noeud S(Token token) throws CompilationException {
		Noeud returnNoeud = null;

		// {
		if (token.getCategory().equals(Token.Category.TOKEN_OPEN_ACO)) {
			List<Noeud> children = new ArrayList<Noeud>();
			Token nextToken;
			Noeud child;

			do {
				nextToken = analyseLexicale.next();
				child = S(nextToken);

				if (child != null) {
					children.add(child);
				}
			} while (child != null);

			if (!nextToken.getCategory().equals(Token.Category.TOKEN_CLOSE_ACO)) {
				throw new CompilationException("Erreur : accolade ouverte ('{') mais non refermée ('}').",
						token.getLine(), token.getColumn());
			}

			returnNoeud = new Noeud(Noeud.Category.NOEUD_BLOCK, null, null, children, token.getLine(),
					token.getColumn());
		}

		Token nextToken = analyseLexicale.look(); // On récup le token suivant
		// A;
		if (token.getCategory().equals(Token.Category.TOKEN_ID)
				&& nextToken.getCategory().equals(Token.Category.TOKEN_ASSIGN)) {
			returnNoeud = A(token);

			Token secondNextToken = analyseLexicale.next();
			if (!secondNextToken.getCategory().equals(Token.Category.TOKEN_PV)) {
				throw new CompilationException("Erreur : ligne non terminée par un point virgule (';').",
						token.getLine(), token.getColumn());
			} else {
				returnNoeud.setComment(secondNextToken.getComment());
			}
		}

		// E;
		if (returnNoeud == null) {
			returnNoeud = E(token);
		}

		// if
		if (token.getCategory().equals(Token.Category.TOKEN_IF)) {
			if (!analyseLexicale.next().getCategory().equals(Token.Category.TOKEN_OPEN_PAR)) {
				throw new CompilationException("Erreur : début de if sans parenthèse ouvrante ('(')", token.getLine(),
						token.getColumn());
			}

			List<Noeud> children = new ArrayList<Noeud>();
			Noeud E = E(analyseLexicale.next());

			Token.Category nextTokenCategory = analyseLexicale.next().getCategory();
			if (!nextTokenCategory.equals(Token.Category.TOKEN_CLOSE_PAR)) {
				throw new CompilationException("Erreur : début de if sans parenthèse fermante (')')", token.getLine(),
						token.getColumn());
			}

			Noeud S1 = S(analyseLexicale.next());
			Noeud S2 = null;

			if (analyseLexicale.look().getCategory().equals(Token.Category.TOKEN_ELSE)) {
				analyseLexicale.next();
				S2 = S(analyseLexicale.next());
			}

			children.add(E);
			children.add(S1);

			if (S2 != null) {
				children.add(S2);
			}

			returnNoeud = new Noeud(Noeud.Category.NOEUD_IF, null, null, children, token.getLine(), token.getColumn());
		}

		// while
		if (token.getCategory().equals(Token.Category.TOKEN_WHILE)) {
			if (!analyseLexicale.next().getCategory().equals(Token.Category.TOKEN_OPEN_PAR)) {
				throw new CompilationException("Erreur : début de while sans parenthèse ouvrante ('(')",
						token.getLine(), token.getColumn());
			}

			List<Noeud> children = new ArrayList<Noeud>();
			List<Noeud> ifChildren = new ArrayList<Noeud>();

			Noeud E = E(analyseLexicale.next());
			ifChildren.add(E);

			if (!analyseLexicale.next().getCategory().equals(Token.Category.TOKEN_CLOSE_PAR)) {
				throw new CompilationException("Erreur : début de while sans parenthèse fermante (')')",
						token.getLine(), token.getColumn());
			}

			Noeud S = S(analyseLexicale.next());
			ifChildren.add(S);

			Noeud breakNode = new Noeud(Noeud.Category.NOEUD_BREAK, null, null, null, token.getLine(),
					token.getColumn());
			ifChildren.add(breakNode);

			Noeud ifNode = new Noeud(Noeud.Category.NOEUD_IF, null, null, ifChildren, token.getLine(),
					token.getColumn());
			children.add(ifNode);

			returnNoeud = new Noeud(Noeud.Category.NOEUD_LOOP, null, null, children, token.getLine(),
					token.getColumn());
		}

		// do while
		if (token.getCategory().equals(Token.Category.TOKEN_DO)) {
			List<Noeud> blockChildren = new ArrayList<Noeud>();

			Noeud S = S(analyseLexicale.next());
			blockChildren.add(S);

			if (!analyseLexicale.next().getCategory().equals(Token.Category.TOKEN_WHILE)) {
				throw new CompilationException("Erreur : fin de do while sans while. ", token.getLine(),
						token.getColumn());
			}

			if (!analyseLexicale.next().getCategory().equals(Token.Category.TOKEN_OPEN_PAR)) {
				throw new CompilationException("Erreur : fin de do while sans parenthèse ouvrante ('('). ",
						token.getLine(), token.getColumn());
			}

			List<Noeud> ifChildren = new ArrayList<Noeud>();

			Noeud E = E(analyseLexicale.next());
			ifChildren.add(E);

			Noeud continueNode = new Noeud(Noeud.Category.NOEUD_CONTINUE, null, null, null, token.getLine(),
					token.getColumn());
			ifChildren.add(continueNode);

			Noeud breakNode = new Noeud(Noeud.Category.NOEUD_BREAK, null, null, null, token.getLine(),
					token.getColumn());
			ifChildren.add(breakNode);

			Noeud ifNode = new Noeud(Noeud.Category.NOEUD_IF, null, null, ifChildren, token.getLine(),
					token.getColumn());

			blockChildren.add(ifNode);

			Noeud block = new Noeud(Noeud.Category.NOEUD_BLOCK, null, null, blockChildren, token.getLine(),
					token.getColumn());

			if (!analyseLexicale.next().getCategory().equals(Token.Category.TOKEN_CLOSE_PAR)) {
				throw new CompilationException("Erreur : fin de do while sans parenthèse fermante (')'). ",
						token.getLine(), token.getColumn());
			}

			List<Noeud> children = new ArrayList<Noeud>();
			children.add(block);

			returnNoeud = new Noeud(Noeud.Category.NOEUD_LOOP, null, null, children, token.getLine(),
					token.getColumn());
		}

		// for
		if (token.getCategory().equals(Token.Category.TOKEN_FOR)) {
			if (!analyseLexicale.next().getCategory().equals(Token.Category.TOKEN_OPEN_PAR)) {
				throw new CompilationException("Erreur : début de for sans parenthèse ouvrante ('('). ",
						token.getLine(), token.getColumn());
			}

			Noeud A1 = A(analyseLexicale.next());
			List<Noeud> blockChildren = new ArrayList<Noeud>();
			blockChildren.add(A1);

			if (!analyseLexicale.next().getCategory().equals(Token.Category.TOKEN_PV)) {
				throw new CompilationException("Erreur : point virgule manquant dans la déclaration du for (';'). ",
						token.getLine(), token.getColumn());
			}

			Noeud E = E(analyseLexicale.next());
			List<Noeud> ifChildren = new ArrayList<Noeud>();
			ifChildren.add(E);

			if (!analyseLexicale.next().getCategory().equals(Token.Category.TOKEN_PV)) {
				throw new CompilationException("Erreur : point virgule manquant dans la déclaration du for (';'). ",
						token.getLine(), token.getColumn());
			}

			Noeud A2 = A(analyseLexicale.next());
			List<Noeud> loopChildren = new ArrayList<Noeud>();
			loopChildren.add(A2);

			if (!analyseLexicale.next().getCategory().equals(Token.Category.TOKEN_CLOSE_PAR)) {
				throw new CompilationException("Erreur : début de for sans parenthèse fermante (')'). ",
						token.getLine(), token.getColumn());
			}

			Noeud S = S(analyseLexicale.next());
			ifChildren.add(S);

			Noeud breakNode = new Noeud(Noeud.Category.NOEUD_BREAK, null, null, null, token.getLine(),
					token.getColumn());
			ifChildren.add(breakNode);

			Noeud ifNode = new Noeud(Noeud.Category.NOEUD_IF, null, null, ifChildren, token.getLine(),
					token.getColumn());
			loopChildren.add(ifNode);

			Noeud loopNode = new Noeud(Noeud.Category.NOEUD_LOOP, null, null, loopChildren, token.getLine(),
					token.getColumn());
			blockChildren.add(loopNode);

			returnNoeud = new Noeud(Noeud.Category.NOEUD_BLOCK, null, null, blockChildren, token.getLine(),
					token.getColumn());
		}

		// break
		if (token.getCategory().equals(Token.Category.TOKEN_BREAK)) {
			returnNoeud = new Noeud(Noeud.Category.NOEUD_BREAK, null, null, null, token.getLine(), token.getColumn());

			Token secondNextToken = analyseLexicale.next();
			String comment;
			if (!secondNextToken.getCategory().equals(Token.Category.TOKEN_PV)) {
				throw new CompilationException("Erreur : ligne non terminée par un point virgule (';').",
						token.getLine(), token.getColumn());
			} else {
				comment = secondNextToken.getComment();
			}

			returnNoeud = new Noeud(Noeud.Category.NOEUD_BREAK, null, null, null, token.getLine(), token.getColumn());
			returnNoeud.setComment(comment);
		}

		// continue
		if (token.getCategory().equals(Token.Category.TOKEN_CONTINUE)) {
			Token secondNextToken = analyseLexicale.next();
			String comment;
			if (!secondNextToken.getCategory().equals(Token.Category.TOKEN_PV)) {
				throw new CompilationException("Erreur : ligne non terminée par un point virgule (';').",
						token.getLine(), token.getColumn());
			} else {
				comment = secondNextToken.getComment();
			}

			returnNoeud = new Noeud(Noeud.Category.NOEUD_CONTINUE, null, null, null, token.getLine(), token.getColumn());
			returnNoeud.setComment(comment);
		}

		// int
		if (token.getCategory().equals(Token.Category.TOKEN_INT)) {
			Token identToken = analyseLexicale.next();
			Token.Category nextTokenCategory = identToken.getCategory();
			Token secondNextToken = analyseLexicale.next();
			String comment;
			if (!nextTokenCategory.equals(Token.Category.TOKEN_ID)
					|| !secondNextToken.getCategory().equals(Token.Category.TOKEN_PV)) {
				throw new CompilationException("Erreur : syntaxe déclaration variable incorrecte.", token.getLine(),
						token.getColumn());
			} else {
				comment = secondNextToken.getComment();
			}

			returnNoeud = new Noeud(Noeud.Category.NOEUD_VARDEC, identToken.getIdentifiant(), null, null,
					token.getLine(), token.getColumn());
			returnNoeud.setComment(comment);
		}

		// out
		if (token.getCategory().equals(Token.Category.TOKEN_OUT)) {
			List<Noeud> children = new ArrayList<Noeud>();

			Noeud child = E(analyseLexicale.next());
			children.add(child);

			Token secondNextToken = analyseLexicale.next();
			String comment;
			if (!secondNextToken.getCategory().equals(Token.Category.TOKEN_PV)) {
				throw new CompilationException("Erreur : ligne non terminée par un point virgule (';').",
						token.getLine(), token.getColumn());
			} else {
				comment = secondNextToken.getComment();
			}

			returnNoeud = new Noeud(Noeud.Category.NOEUD_OUT, null, null, children, token.getLine(), token.getColumn());
			returnNoeud.setComment(comment);
		}

		return returnNoeud;
	}

	public void generateTree() throws CompilationException {
		arbre = S(analyseLexicale.next());
		Token token = analyseLexicale.next();
		if (!token.equals(Token.TOKEN_END)) {
			throw new CompilationException("Erreur : Problème de syntaxe en fin de fichier.", token.getLine(),
					token.getColumn());
		}
		checkForNoeudNull(arbre);
	}

	public Noeud getArbre() {
		return arbre;
	}

	private void checkForNoeudNull(Noeud arbre) throws CompilationException {
		List<Noeud> noeuds = new ArrayList<Noeud>();
		noeuds.add(arbre);

		for (int i = 0; i < noeuds.size(); ++i) {
			Noeud temp = noeuds.get(i);

			if (temp == null) {
				continue;
			}

			for (Noeud child : temp.getChildren()) {
				if (child == null) {
					throw new CompilationException("Erreur : Paramètre de droite manquant.", temp.getLine(),
							temp.getColumn());
				}
			}
		}
	}

}
