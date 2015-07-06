import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 递归下降语法分析器
 *
 * @author Linwei
 */
public class GrammarAnalyse {

    private ArrayList<Word> mWords;
    private int mIndex;
    private Word mSym = null;

    private HashMap<String, Integer> mIDTable = new HashMap<>();
    private ArrayList<Form> mForms = new ArrayList<>();

    private int mTcount;

    public GrammarAnalyse(ArrayList<Word> list) {
        this.mWords = list;
        mIndex = 0;
        mTcount = 1;
    }

    public void analyse() {
        mSym = getSym();
        P();
        System.out.println("OK!");
    }

    public ArrayList<Form> getForms(){
        return this.mForms;
    }

    // P →{DS}
    private void P() {
        if (symEqual("{")) {
            mSym = getSym();
            D();
            S();
            if (!symEqual("}")) {
                error("missing } after " + mSym.value);
            }
        } else {
            error("missing { before " + mSym.value);
        }
    }

    // D →int ID ;{int ID;}
    private void D() {
        if (symEqual("int")) {
            mSym = getSym();
            insert(mSym.value);
            mSym = getSym();
            if (!symEqual(";")) {
                error("missing ;");
            }
            mSym = getSym();
            D();
        }
    }

    // S→if (B) then S [else S ] | while (B) do S | { L } | ID=E
    private void S() {
        if (symEqual("if")) { // if (B) then S [else S ]
            mSym = getSym();
            if (!symEqual("(")) {
                error("missing ( before" + mSym.value);
            }
            mSym = getSym();
            B();
            if (!symEqual(")")) {
                error("missing ) after" + mSym.value);
            }
            mSym = getSym();
            if (!symEqual("then")) {
                error("missing then after" + mSym.value);
            }
            mSym = getSym();
            S();
            if (symEqual("else")) {
                mSym = getSym();
                S();
            }
        } else if (symEqual("while")) { // while (B) do S
            mSym = getSym();
            if (!symEqual("(")) {
                error("missing ( before" + mSym.value);
            }
            mSym = getSym();
            B();
            if (!symEqual(")")) {
                error("missing ) after" + mSym.value);
            }

            mSym = getSym();
            if (!symEqual("do")) {
                error("missing then after" + mSym.value);
            }
            mSym = getSym();
            S();
        } else if (symEqual("{")) { //{ L }
            mSym = getSym();
            L();
            if (!symEqual("}")) {
                error("missing } after " + mSym.value);
            }
            mSym = getSym();
        } else if (mSym.sign.equals("ID")) { //ID=E
            lookup(mSym.value);
            String id = mSym.value;
            mSym = getSym();
            if (!symEqual("=")) {
                error("missing = before " + mSym.value);
            }
            mSym = getSym();
            emit("=", E(), "", id);
        } else {
            error("error in S");
        }

    }

    private boolean firstS() {
        return symEqual("if") || symEqual("while") || symEqual("{") || mSym.sign.equals("ID");
    }

    // L→SL’
    private void L() {
        if (firstS()) {
            S();
            LL();
        } else {
            error("can't match first L");
        }
    }

    private boolean followL() {
        return symEqual("}");
    }

    // L’ →; L | null
    private void LL() {
        if (symEqual(";")) {
            mSym = getSym();
            L();
        } else {
            if (!followLL()) {
                error("LL missing ; follow " + mSym.value);
            }
        }
    }

    private boolean followLL() {
        return followL();
    }

    // B→T’ {|T’}
    private void B() {
        if (firstTT()) {
            TT();
            if (symEqual("|")) {
                TT();
            }
        } else {
            error("can't match first TT");
        }
    }

    private boolean firstTT() {
        return firstFF();
    }

    // T’ →F’ {&F’ }
    private void TT() {
        if (firstFF()) {
            FF();
            if (symEqual("&")) {
                FF();
            }
        } else {
            error("can't match first FF");
        }
    }

    private boolean firstFF() {
        return mSym.sign.equals("ID");
    }

    // F’ →ID relop ID | ID
    private String FF() {
        String express = "";
        if (!mSym.sign.equals("ID")) {
            error("error in FF1");
        }
        express += mSym.value;
        mSym = getSym();
        if(mSym.sign.equals("relop")){
            express += mSym.value;
            mSym = getSym();
            if (!mSym.sign.equals("ID")) {
                error("error in FF2");
            }
            express += mSym.value;
        }
        mSym = getSym();
        return express;
    }

    // E→T{+T| -T} , 返回变量或临时变量名
    private String E() {
        String t1;
        String t2;
        String operate;
        String result = "";
        if (firstT()) {
            t1 = T();
            if (symEqual("+") || symEqual("-")) {
                operate = mSym.value;
                mSym = getSym();
                t2 = T();
                result = "t" + (mTcount++);
                emit(operate, t1, t2, result);
            }else{
                result = t1;
            }
        } else {
            error("can't match first T");
        }
        return result;
    }

    private boolean firstT() {
        return firstF();
    }

    // T→F{* F | /F }
    private String T() {
        String operate;
        String t1;
        String t2;
        String result = "";
        if (firstF()) {
            t1 = F();
            if (symEqual("*") || symEqual("/")) {
                operate = mSym.value;
                mSym = getSym();
                t2 = F();
                result = "t" + (mTcount++);
                emit(operate, t1, t2, result);
            }else{
                result = t1;
            }
        } else {
            error("can't match first F");
        }
        return result;
    }

    private boolean firstF() {
        return symEqual("(") || mSym.sign.equals("NUM") || mSym.sign.equals("ID");
    }

    // F→ (E) | NUM | ID
    private String F() {
        String result = "";
        if (symEqual("(")) {
            mSym = getSym();
            result = E();
            if (!symEqual(")")) {
                error("missing ) after " + mSym.value);
            }
        } else if (mSym.sign.equals("ID")) {
            lookup(mSym.value);
            result = mSym.value;
        } else if (mSym.sign.equals("NUM")) {
            result = mSym.value;
        } else {
            error("error in F");
        }
        mSym = getSym();
        return result;
    }

    private boolean symEqual(String str) {
        return mSym.value.equals(str);
    }

    private Word getSym() {
        Word sym = null;
        if (mIndex < mWords.size()) {
            sym = mWords.get(mIndex++);
            System.out.println(sym.value);
        }else{
            System.out.println("###");
        }
        return sym;
    }

    private void error(String msg) {
        System.out.println(String.format("Error:%s at %s position [%d,%d]",msg,mSym.value,
                mSym.pos.y, mSym.pos.x));
        System.exit(-1);
    }

    private void insert(String id) {
        mIDTable.put(id, null);
    }

    // 在符号表中查找标示符
    private void lookup(String id) {
        if (!mIDTable.containsKey(id)) {
            error("标示符不存在:" + id);
        }
    }

    private int lookupint(String id) {
        return mIDTable.get(id);
    }

    private void emit(String operate, String op1, String op2, String result) {
        Form form = new Form(operate, op1, op2, result);
        System.out.println(form.toString());
        mForms.add(form);
    }

}
