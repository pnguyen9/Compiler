package compilation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * @author Pascal Nguyen & Benjamin Aparicio
 *
 */
public class Compilation {

	private final static String DEFAULT_SOURCE_DIRECTORY = "./code/";
	private final static String OUTPUT_FILE_SUFFIX = "_compiled";

	private Scanner scanner = new Scanner(System.in);
	private FileParser fileParser = new FileParser();

	private AnalyseLexicale analyseLexicale;
	private AnalyseSyntaxique analyseSyntaxique;
	private AnalyseSemantique analyseSemantique;

	private String sourceDirectory;
	private List<String> codeLines;
	private List<String> compiledCodeLines;

	private final static String JUMP_LABEL = "jump_";

	private enum JumpLabels {
		IF_LABEL_ELSE("_if_else"), //
		IF_LABEL_END("_if_end"), //
		LOOP_LABEL_START("_loop_start"), //
		LOOP_LABEL_END("_loop_end"), //
		LOOP_LABEL_FOR_FIRST_ITERATION("_loop_for_first_iteration");

		private JumpLabels(String label) {
			this.label = label;
		}

		private String label;

		@Override
		public String toString() {
			return label;
		}
	}

	private int labelIndice = 0;
	private LinkedList<LoopLabels> loopLabelsList;

	class LoopLabels {

		private String labelStart;
		private String labelEnd;

		public LoopLabels(String labelStart, String labelEnd) {
			this.labelStart = labelStart;
			this.labelEnd = labelEnd;
		}

		public String getLabelStart() {
			return labelStart;
		}

		public String getLabelEnd() {
			return labelEnd;
		}

	}

	public Compilation(AnalyseLexicale analyseLexicale, AnalyseSyntaxique analyseSyntaxique,
			AnalyseSemantique analyseSemantique) {
		this(DEFAULT_SOURCE_DIRECTORY, analyseLexicale, analyseSyntaxique, analyseSemantique);
	}

	public Compilation(String sourceDirectory, AnalyseLexicale analyseLexicale, AnalyseSyntaxique analyseSyntaxique,
			AnalyseSemantique analyseSemantique) {
		this.sourceDirectory = sourceDirectory;
		this.analyseLexicale = analyseLexicale;
		this.analyseSyntaxique = analyseSyntaxique;
		this.analyseSemantique = analyseSemantique;
	}

	public void launchCompiler() {
		String fileName = "";
		while (true) {
			if (StringUtils.isBlankString(fileName)) {
				System.out.print("Saisir le nom du fichier à compiler : ");
				fileName = scanner.nextLine();
			} else {
				try {
					loadCodeFromFile(fileName);

					analyseLexicale.loadTokensFromCode(codeLines);

					analyseSyntaxique.generateTree();
					Noeud arbre = analyseSyntaxique.getArbre();

					if (arbre != null) {
						analyseSemantique.initVariables();
						analyseSemantique.sem(arbre);
						analyseSemantique.semLoop(arbre);

						generateCompiledCode(arbre);
						saveCompiledCodeIntoCompiledFile();
					} else {
						System.err.println("Erreur: le fichier spécifié est vide.");
					}
				} catch (CompilationException ce) {
					System.err.println(ce.getMessage());
					System.err.println();
				} finally {
					fileName = "";
				}
			}
		}
	}

	public static void main(String[] args) {
		AnalyseLexicale analyseLexicale = new AnalyseLexicale();
		AnalyseSyntaxique analyseSyntaxique = new AnalyseSyntaxique(analyseLexicale);
		AnalyseSemantique analyseSemantique = new AnalyseSemantique(new TableSymbole());
		Compilation compilation = new Compilation(analyseLexicale, analyseSyntaxique, analyseSemantique);
		compilation.launchCompiler();
	}

	public List<String> getCodeLines() {
		return codeLines;
	}

	public void loadCodeFromFile(String relativePath) throws CompilationException {
		fileParser.setFilePath(sourceDirectory + relativePath);
		codeLines = fileParser.getFileContent();
	}

	public void saveCompiledCodeIntoCompiledFile() throws CompilationException {
		String compiledFileName = fileParser.getFilePath() + OUTPUT_FILE_SUFFIX;
		fileParser.createFileWithContent(compiledFileName, compiledCodeLines);
		System.out.println("Le fichier " + fileParser.getFilePath() + " a été compilé avec succès.");
		System.out.println("Le code compilé a été enregistré dans le fichier suivant : ");
		System.out.println(compiledFileName);
	}

	public void generateCompiledCode(Noeud arbre) throws CompilationException {
		compiledCodeLines = new ArrayList<String>();
		compiledCodeLines.add(".start");

		compiledCodeLines.add("; Variables declarations");
		for (int i = 0; i < analyseSemantique.getNbVar(); ++i) {
			compiledCodeLines.add("push.i 21400868");
		}

		loopLabelsList = new LinkedList<LoopLabels>();
		genCode(arbre);
		compiledCodeLines.add(".halt");
	}

	private void genCode(Noeud noeud) throws CompilationException {
		// Push de la valeur du noeud
		if (noeud.getCategory().equals(Noeud.Category.NOEUD_VAL)) {
			compiledCodeLines.add("push.i " + noeud.getValue());
		}
		// Valeur de la variable référencée
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_ID)) {
			compiledCodeLines.add("get " + noeud.getSlot());
		}
		// addition
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_PLUS)) {
			genCode(noeud.getChild(0));
			genCode(noeud.getChild(1));
			compiledCodeLines.add("add.i");
		}
		// soustraction
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_MINUS)) {
			genCode(noeud.getChild(0));
			genCode(noeud.getChild(1));
			compiledCodeLines.add("sub.i");
		}
		// minus unaire
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_MINUS_UN)) {
			compiledCodeLines.add("push.i 0");
			genCode(noeud.getChild(0));
			compiledCodeLines.add("sub.i");
		}
		// !
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_NOT)) {
			genCode(noeud.getChild(0));
			compiledCodeLines.add("not");
		}
		// multiplication
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_STAR)) {
			genCode(noeud.getChild(0));
			genCode(noeud.getChild(1));
			compiledCodeLines.add("mul.i");
		}
		// division
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_DIV)) {
			genCode(noeud.getChild(0));
			genCode(noeud.getChild(1));
			compiledCodeLines.add("div.i");
		}
		// modulo
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_PERCENTAGE)) {
			genCode(noeud.getChild(0));
			genCode(noeud.getChild(1));
			compiledCodeLines.add("mod.i");
		}
		// and
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_AND)) {
			genCode(noeud.getChild(0));
			genCode(noeud.getChild(1));
			compiledCodeLines.add("and");
		}
		// or
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_OR)) {
			genCode(noeud.getChild(0));
			genCode(noeud.getChild(1));
			compiledCodeLines.add("or");
		}
		// ==
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_DEG)) {
			genCode(noeud.getChild(0));
			genCode(noeud.getChild(1));
			compiledCodeLines.add("cmpeq.i");
		}
		// <=
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_BELOQ)) {
			genCode(noeud.getChild(0));
			genCode(noeud.getChild(1));
			compiledCodeLines.add("cmple.i");
		}
		// <
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_BEL)) {
			genCode(noeud.getChild(0));
			genCode(noeud.getChild(1));
			compiledCodeLines.add("cmplt.i");
		}
		// >=
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_ABOOQ)) {
			genCode(noeud.getChild(0));
			genCode(noeud.getChild(1));
			compiledCodeLines.add("cmpge.i");
		}
		// >
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_ABO)) {
			genCode(noeud.getChild(0));
			genCode(noeud.getChild(1));
			compiledCodeLines.add("cmpgt.i");
		}
		// !=
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_DIF)) {
			genCode(noeud.getChild(0));
			genCode(noeud.getChild(1));
			compiledCodeLines.add("cmpne.i");
		}
		// block
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_BLOCK)) {
			for (Noeud child : noeud.getChildren()) {
				genCode(child);
			}
		}
		// Déclaration
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_VARDEC)) {
			compiledCodeLines.add("; Variable declaration: " + noeud.getIdentifiant());
			// DO NOTHING
		}
		// Référence
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_VARREF)) {
			compiledCodeLines.add("get " + noeud.getSlot());
		}
		// Affectation
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_AFFEC)) {
			genCode(noeud.getChild(0));
			compiledCodeLines.add("set " + noeud.getSlot());
		}
		// if
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_IF)) {
			compiledCodeLines.add("; If start");
			String generatedLabel = generateLabel();
			String ifElseLabel = generatedLabel + JumpLabels.IF_LABEL_ELSE;
			String ifEndLabel = null;

			// S'il y a un else, on crée un label pour sauter à travers le else
			if (noeud.getChildren().size() > 2) {
				ifEndLabel = generatedLabel + JumpLabels.IF_LABEL_END;
			}

			genCode(noeud.getChild(0));
			compiledCodeLines.add("jumpf " + ifElseLabel);
			genCode(noeud.getChild(1));

			if (ifEndLabel != null) {
				compiledCodeLines.add("jump " + ifEndLabel);
			}

			compiledCodeLines.add("." + ifElseLabel);
			compiledCodeLines.add("; If else");

			if (ifEndLabel != null) {
				genCode(noeud.getChild(2));
				compiledCodeLines.add("." + ifEndLabel);
			}
			compiledCodeLines.add("; If end");
		}
		// boucles
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_LOOP)) {
			compiledCodeLines.add("; Loop start");
			String generatedLabel = generateLabel();
			String forFirstIterationLabel = null;

			if (noeud.getChildren().size() > 1) {
				compiledCodeLines.add("; Starting for loop");
				forFirstIterationLabel = generatedLabel + JumpLabels.LOOP_LABEL_FOR_FIRST_ITERATION;
			}

			String loopStartLabel = generatedLabel + JumpLabels.LOOP_LABEL_START;
			String loopEndLabel = generatedLabel + JumpLabels.LOOP_LABEL_END;
			LoopLabels loopLabels = new LoopLabels(loopStartLabel, loopEndLabel);
			loopLabelsList.add(loopLabels);

			if (noeud.getChildren().size() > 1) {
				compiledCodeLines.add("jump " + forFirstIterationLabel);
			}

			compiledCodeLines.add("." + loopStartLabel);

			genCode(noeud.getChild(0));
			if (noeud.getChildren().size() > 1) {
				compiledCodeLines.add("." + forFirstIterationLabel);
				genCode(noeud.getChild(1));
			}

			compiledCodeLines.add("jump " + loopStartLabel);
			compiledCodeLines.add("." + loopEndLabel);
			loopLabelsList.removeLast();
			compiledCodeLines.add("; Loop end");
		}
		// break
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_BREAK)) {
			compiledCodeLines.add("jump " + loopLabelsList.getLast().getLabelEnd());
		}
		// continue
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_CONTINUE)) {
			compiledCodeLines.add("jump " + loopLabelsList.getLast().getLabelStart());
		}
		// out
		else if (noeud.getCategory().equals(Noeud.Category.NOEUD_OUT)) {
			genCode(noeud.getChild(0));
			compiledCodeLines.add("out.i");
		}
		// Sinon : erreur
		else {
			throw new CompilationException("Erreur : Syntaxe non connue.", noeud.getLine(), noeud.getColumn());
		}

		if (noeud.getComment() != null) {
			compiledCodeLines.add(noeud.getComment());
		}
	}

	private String generateLabel() {
		return JUMP_LABEL + labelIndice++;
	}

	public void displayCodeLines() {
		for (String line : codeLines) {
			System.out.println(line);
		}
	}

}