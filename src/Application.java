import java.io.IOException;
import java.util.ArrayList;

public class Application {

	public static void main(String[] args) {
		try {
			LexicalAnalyse lexicalAnalyse = new LexicalAnalyse("main.txt");
            lexicalAnalyse.analyse();
			ArrayList<Word> words = lexicalAnalyse.getWords();
			for (Word word : words) {
				System.out.println(word.toString());
			}
            GrammarAnalyse grammarAnalyse = new GrammarAnalyse(words);
            grammarAnalyse.analyse();
            ArrayList<Form> forms = grammarAnalyse.getForms();
            for (Form form : forms){
                System.out.println(form.toString());
            }
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("·ÖÎö´íÎó");
			System.exit(-1);
		}
	}
}
