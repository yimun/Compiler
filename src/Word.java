
import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import javax.swing.text.Position;


/**
 * ����
 * @author Linwei
 *
 */
public class Word {
	
	public static final int KEEP_WORD = 1;
	public static final int ID = 7;
	public static final int NUM = 8;
	public static final int ARTH_OPERA = 9;
	public static final int LOGI_OPERA = 13;
	public static final int RELOP = 15; // ��ϵ�����
	public static final int SPLIT = 16;

	public static final List<String> KeepWords = Arrays.asList( "int", "if", "then", "else", "while", "do" );
	public static final List<String> ArthOpera = Arrays.asList( "+", "-", "*", "/" );
	public static final List<String> LogiOpera = Arrays.asList( "&", "|" );
	public static final List<String> Relop = Arrays.asList( "<", ">", "<=", ">=", "!=", "==" );
	public static final List<String> Split = Arrays.asList( "{", "}", ";", "(", ")", "=" );
	
	public String symbol; // ���ʷ���
	public int code;  // ���ʱ���
	public String sign; // ���Ƿ�
	public String value; // ����ֵ
	
	public Point pos;
	
	public Word(String symbol, int code, String sign, String value,Point pos){
		this.symbol = symbol;
		this.code = code;
		this.sign = sign;
		this.value = value;
		this.pos = pos;
	}
	
	public static Word buildKeepOrID(String value,Point pos){
		int index;
		if((index = KeepWords.indexOf(value)) != -1){
			return new Word(value, KEEP_WORD + index, value, value, pos);
		}else{
			return new Word("identifier", ID, "ID", value, pos);
		}
	}
	
	public static Word buildNum(String value, Point pos){
		return new Word("number", NUM, "NUM", value, pos);
	}
	
	public static Word buildOperaOfSplit(String value,Point pos) {
		int index;
		if((index = ArthOpera.indexOf(value)) != -1){
			return new Word(value, ARTH_OPERA + index, value, value, pos);
		}else if((index = LogiOpera.indexOf(value)) != -1){
			return new Word(value, LOGI_OPERA + index, value, value, pos);
		}else if((index = Split.indexOf(value)) != -1){
			return new Word(value, SPLIT + index, value, value, pos);
		}
		return null;
	}
	
	public static Word buildRelop(String value, Point pos) {
		int index;
		if((index = Relop.indexOf(value)) != -1){
			return new Word(value, RELOP + index, "relop", value, pos);
		}else if((index = Split.indexOf(value)) != -1){
			return new Word(value, SPLIT + index, value, value, pos);
		}
		return null;
	}
	
	@Override
	public String toString(){
		return String.format("%10s %10d %10s %10s", symbol, code, sign, value);
	}
}
