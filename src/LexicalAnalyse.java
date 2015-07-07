import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Linwei
 *
 * 词法分析器
 */
public class LexicalAnalyse {

	public static final char END = '#';

	private ArrayList<Word> mWordList = new ArrayList<>();
	private BufferedReader mSource;
	private int mLineNum;
	private int mLineSize;
	private int mIndex;
	private String mLineBuf;
	private boolean mIsEof;
	private boolean mIsErr;
	private int mStage;
	
	private String mTemp;

	public LexicalAnalyse(String inFile) {
		try {
			mSource = new BufferedReader(new FileReader(inFile));
		} catch (FileNotFoundException e) {
			System.out.println("没有找到文件");
		}
		mWordList.clear();
		mLineNum = 0;
		mLineSize = 0;
		mIndex = 0;
		mStage = 0;
		mIsEof = false;
		mIsErr = false;
	}
	
	// DFA
	public void analyse() throws IOException{
		char ch;
		reset();
		ch = next();
		while(!mIsEof && !mIsErr){
			if(0 == mStage){
				if(isBlank(ch)){
					ch = next();
					continue;
				}else if(isDigit(ch)){
					mTemp += ch;
					mStage = 3;
				}else if(isLetter(ch)){
					mTemp += ch;
					mStage = 1;
				}else if(isOperaOrSplit(ch)){
					mTemp += ch;
					mWordList.add(Word.buildOperaOfSplit(mTemp, new Point(mIndex,mLineNum)));
					reset();
				}else if(isRelop(ch)){
					mTemp += ch;
					mStage = 6;
				}else{
					mIsErr = true;
				}
			}else if(1 == mStage){
				if(isDigit(ch) || isLetter(ch)){
					mTemp += ch;
				}else{ // 标示符或关键字
					mWordList.add(Word.buildKeepOrID(mTemp, new Point(mIndex,mLineNum)));
					reset();
					back();
				}
			}else if(3 == mStage){
				if(isDigit(ch)){
					mTemp += ch;
				}else{  // 数字
					mWordList.add(Word.buildNum(mTemp, new Point(mIndex,mLineNum)));
					reset();
					back();
				}
			}else if(6 == mStage){
				if(isEqual(ch)){
					mTemp += ch;
					mWordList.add(Word.buildRelop(mTemp, new Point(mIndex,mLineNum)));
					reset();
				}else{
					mWordList.add(Word.buildRelop(mTemp, new Point(mIndex,mLineNum)));
					reset();
					back();
				}
			}
			ch = next();
		}
	}
	
	public ArrayList<Word> getWords(){
		return this.mWordList;
	}

	boolean isLetter(char ch) {
		return (ch >= 'a' && ch <= 'z') || ch >= 'A' && ch <= 'Z';
	}

	boolean isDigit(char ch) {
		return ch >= '0' && ch <= '9';
	}

	boolean isBlank(char ch) {
		return ch == ' ' || ch == 9 || ch == '\n';
	}
	
	boolean isOperaOrSplit(char ch){
		String pattern = "+-*/&|(){};";
		return pattern.indexOf(ch) != -1;
	}
	
	boolean isRelop(char ch){
		String pattern = "<>!=";
		return pattern.indexOf(ch) != -1;
	}
	
	boolean isEqual(char ch){
		return ch == '=';
	}
	
	void reset(){
		mStage = 0;
		mTemp = "";
	}
	
	private void back(){
		mIndex --;
	}

	/**
	 * 获取下一个字符
	 * @return
	 * @throws IOException
	 */
	private char next() throws IOException {
		char ch = '\0';
		if (mIndex < mLineSize) {
			ch = mLineBuf.charAt(mIndex++);
		} else {
			if ((mLineBuf = mSource.readLine()) != null) {
				mLineNum++;
				mLineSize = mLineBuf.length();
				System.out.println(String.format("%d:%s", mLineNum, mLineBuf));
				mIndex = 0;
				ch = mLineBuf.charAt(mIndex++);
			} else {
				mIsEof = true;
			}
		}
		if(ch == END){
			mIsEof = true;
		}
		return ch;
	}

}
