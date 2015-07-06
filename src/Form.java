/**
 * ËÄÔªÊ½
 */
public class Form {
	
	public String operate;
	public String op1;
    public String op2;
    public String result;

    public Form(String operate,String op1,String op2,String result){
        this.operate = operate;
        this.op1 = op1;
        this.op2 = op2;
        this.result = result;
    }

    @Override
    public String toString(){
        return String.format("%5s %5s %5s %5s",operate,op1,op2,result);
    }

}
