package compilation;

import compilation.Noeud.Category;

/**
 * 
 * @author Benjamin Aparicio && Pascal Nguyen
 *
 */
public class AnalyseSemantique {

	private int nbVar;
	private TableSymbole tableSymbole;

	public TableSymbole getTableSymbole() {
		return tableSymbole;
	}

	public AnalyseSemantique(TableSymbole tableSymbole) {
		this.tableSymbole = tableSymbole;
	}

	public void sem(Noeud N) throws CompilationException {
		if (N == null) {
			return;
		}

		// Parcours de l'arbre
		if (N.getCategory().equals(Category.NOEUD_BLOCK)) {
			tableSymbole.debutBloc();
			for (Noeud E : N.getChildren()) {
				sem(E);
			}
			tableSymbole.finBloc();
		} else {
			for (Noeud E : N.getChildren()) {
				sem(E);
			}
		}

		// On test pour certaines catégories
		if (N.getCategory().equals(Category.NOEUD_VARDEC)) {
			Symbole symbole = tableSymbole.newSymbole(N, nbVar++);
			N.setSlot(symbole.getSlot());
		}
		if (N.getCategory().equals(Category.NOEUD_ID) || N.getCategory().equals(Category.NOEUD_VARREF)
				|| N.getCategory().equals(Category.NOEUD_AFFEC)) {
			Symbole symbole = tableSymbole.rechercheSymbole(N);
			N.setSlot(symbole.getSlot());
		}
	}

	public void semLoop(Noeud N) throws CompilationException {
		if (N == null) {
			return;
		}

		if (N.getCategory().equals(Category.NOEUD_LOOP)) {
			return;
		} else if (N.getCategory().equals(Category.NOEUD_BREAK) || N.getCategory().equals(Category.NOEUD_CONTINUE)) {
			throw new CompilationException("Erreur: Appel à break ou continue en dehors d'une boucle. ", N.getLine(),
					N.getColumn());
		}

		for (Noeud E : N.getChildren()) {
			semLoop(E);
		}
	}

	public int getNbVar() {
		return nbVar;
	}

	public void initVariables() {
		nbVar = 0;
		tableSymbole.initVariables();
	}

}
