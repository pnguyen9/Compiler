package compilation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Pascal Nguyen
 *
 */

public class FileParser {

	private String filePath;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public List<String> getFileContent() throws CompilationException {
		List<String> fileContent = new ArrayList<String>();

		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
			StringBuilder stringBuilder = new StringBuilder();
			String line = bufferedReader.readLine();

			while (line != null) {
				stringBuilder.append(line);
				stringBuilder.append(System.lineSeparator());
				fileContent.add(line);
				line = bufferedReader.readLine();
			}

			bufferedReader.close();
		} catch (IOException e) {
			throw new CompilationException("Erreur : Le fichier spécifié n'existe pas.");
		}

		return fileContent;
	}

	public void createFileWithContent(String filePath, List<String> content) throws CompilationException {
		try {
			String fileContent = "";
			FileWriter fileWriter = new FileWriter(filePath);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			for (String line : content) {
				fileContent += line + '\n';
			}

			bufferedWriter.write(fileContent);
			bufferedWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			throw new CompilationException("Erreur : La création du fichier compilé a échoué.");
		}
	}

}
