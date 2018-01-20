package compilation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 
 * @author Benjamin Aparicio & Pascal Nguyen
 *
 */
public class TableSymbole {

	private LinkedList<Map<String, Symbole>> pile;

	public void initVariables() {
		pile = new LinkedList<Map<String, Symbole>>();
		pile.add(new HashMap<String, Symbole>());
	}

	// Methode de debut de bloque
	public void debutBloc() {
		// Correspond à l'ajout d'une MAP vide dans la pile
		pile.add(new HashMap<String, Symbole>());
	}

	// Methode fin de bloc
	public void finBloc() {
		pile.removeLast();
	}

	// Création d'un nouveau symbole
	public Symbole newSymbole(Noeud n, int slot) throws CompilationException {
		Symbole symbole = null;

		if (pile.getLast().get(n.getIdentifiant()) != null) {
			// Gestion des erreurs
			throw new CompilationException("Erreur : Variable " + n.getIdentifiant() + " déjà déclarée.", n.getLine(),
					n.getColumn());
		} else {
			symbole = new Symbole(slot);
			pile.getLast().put(n.getIdentifiant(), symbole);
		}

		return symbole;
	}

	// Recherche d'un symbole en commençant par le bloc courant
	public Symbole rechercheSymbole(Noeud n) throws CompilationException {
		Symbole symbole = null;

		for (int i = pile.size() - 1; i >= 0; --i) {
			symbole = pile.get(i).get(n.getIdentifiant());

			if (symbole != null) {
				break;
			}
		}

		// Gestion des erreurs
		if (symbole == null) {
			throw new CompilationException("Erreur : Variable non déclarée.", n.getLine(), n.getColumn());
		}

		return symbole;
	}
}
