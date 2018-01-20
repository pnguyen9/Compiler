package compilation;

public class CompilationException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public CompilationException(String message) {
		super(message);
	}

	public CompilationException(String message, int line, int column) {
		super(message + " At line: " + line + "; column: " + column);
	}

}
