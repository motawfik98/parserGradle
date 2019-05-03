package parser;

public class Token {
   private String type;
   private String value;


    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }
    public Token(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        if(type==TinyScanner.States.inAssign.toString())
            type="Special Character";
            return value +" : " + type;
        }
    public boolean equals(Token t) {
        if(t.getValue().equalsIgnoreCase(this.getValue()))
            return true;
        return  false;
    }
}
