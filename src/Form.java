/**
 * ËÄÔªÊ½
 *
 * @author Linwei
 */
public class Form {
	
	public String operate;
	public String op1;
    public String op2;
    public String result;
    public int line;

    public Form(String operate,String op1,String op2,String result, int line){
        this.operate = operate;
        this.op1 = op1;
        this.op2 = op2;
        this.result = result;
        this.line = line;
    }

    @Override
    public String toString(){
        return String.format("%3d: %5s %5s %5s %5s",line, operate,op1,op2,result);
    }

}
