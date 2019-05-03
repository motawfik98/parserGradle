package parser;

public class TypeOrValueException extends Exception {
    private String expectedToken;
    public void printExpectedToken() {
         System.out.println(expectedToken);
    }
    public TypeOrValueException(String expectedToken) {
        this.expectedToken=expectedToken+" was expected";
    }
}
