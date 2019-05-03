package parser;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Stack;

public class Parser {
    private int index = 0;
    private ArrayList<Token> tokenList;
    private Node root = null, currentNode = null;
    private Stack<Node> previousNodes = new Stack<>(); // to store the parent
    boolean yoyo=false;

    public Parser(ArrayList<Token> tokenList) {
        try {
            PrintStream fOut = new PrintStream("parser_output.txt");
            System.setOut(fOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.tokenList = tokenList;
        program();
    }

    private void matchValue(Token expectedToken) throws TypeOrValueException {
        Token nextToken = tokenList.get(index);
        if (nextToken.equals(expectedToken)) {
            //System.out.println(nextToken);
            index++;

        } else {
            throw new TypeOrValueException(expectedToken.getValue());
        }

    }

    private void matchType(Token expectedToken) throws TypeOrValueException {
        Token nextToken = tokenList.get(index);
        if (expectedToken.getType().equalsIgnoreCase(nextToken.getType())) {
            //System.out.println(nextToken);
            index++;
        } else {
            throw new TypeOrValueException(expectedToken.getType());
        }
    }


    private void program() {
        System.out.println("program is Found");
        root = new Node("program");
        previousNodes.push(root);
        stmtSequence();
    }

    private void stmtSequence() {
        System.out.println("stmt-seq is Found");
        Node stmtSeq = new Node("stmt-seq");
        statement();
        while (tokenList.size() > index && tokenList.get(index).getValue().equalsIgnoreCase(";")) {
            try {
                matchValue(new Token(";"));
            } catch (TypeOrValueException e) {
                e.printExpectedToken();
            }
            //System.out.println("Statement End");
            if (index < tokenList.size()) {
                if (tokenList.get(index).getValue().equalsIgnoreCase("until") ||
                        tokenList.get(index).getValue().equalsIgnoreCase("end")) {
//                    previousNodes.pop();
                    return;
                }

                statement();
            }
        }

    }

    private void statement() {
        if (tokenList.get(index).getValue().equals("read"))
            readStmt();
        else if (tokenList.get(index).getValue().equals("write"))
            writeStmt();
        else if (tokenList.get(index).getValue().equals("repeat"))
            repeatStmt();
        else if (tokenList.get(index).getValue().equals("if"))
            ifStmt();
        else if (tokenList.get(index).getType().equals("inId"))
            assignStmt();
        else
            ;//TODO Handle Errors

    }

    private void ifStmt() {
        System.out.println("ifStmt is Found");
        Node ifStmt = new Node("ifStmt");
        currentNode = previousNodes.peek();
        currentNode.addChild(ifStmt);
        previousNodes.push(ifStmt);
        try {
            matchValue(new Token("if"));
            System.out.println("if");
            matchValue(new Token("("));
            exp();
            matchValue(new Token(")"));
            matchValue(new Token("then"));
            System.out.println("then");
            stmtSequence();
            if (tokenList.get(index).getValue().equals("else")) {
                Node elseNode = new Node("else");
                currentNode = previousNodes.peek();
                currentNode.addChild(elseNode);
                previousNodes.push(elseNode);
                matchValue(new Token("else"));
                System.out.println("else");
                stmtSequence();
                previousNodes.pop();
            }
            matchValue(new Token("end"));
            System.out.println("end");
            previousNodes.pop();
        } catch (TypeOrValueException e) {
            e.printExpectedToken();
        }

    }

    private void repeatStmt() {
        try {
            System.out.println("repeatStmt is Found");
            Node repeatStmt = new Node("repeatStmt");
            currentNode = previousNodes.peek();
            currentNode.addChild(repeatStmt);
            previousNodes.push(repeatStmt);
            matchValue(new Token("repeat"));
            System.out.println("repeat");
            stmtSequence();
            matchValue(new Token("until"));
            System.out.println("until");
            exp();
            previousNodes.pop();
        } catch (TypeOrValueException e) {
            e.printStackTrace();
        }
    }

    private void assignStmt() {
        try {
            System.out.println("assignStmt is Found");
            Node assignStmt = new Node("assignStmt");
            currentNode = previousNodes.peek();
            currentNode.addChild(assignStmt);
            previousNodes.push(assignStmt);
            previousNodes.peek().addChild(tokenList.get(index).getValue());
            matchType(new Token("inId", ""));
            matchValue(new Token(":="));
            exp();
            previousNodes.pop();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    private void readStmt() {
        try {
            System.out.println("readStmt is Found");
            Node readStmt = new Node("readStmt");
            currentNode = previousNodes.peek();
            currentNode.addChild(readStmt);
            previousNodes.push(readStmt);
            matchValue(new Token("read"));
            System.out.println("read");
            previousNodes.peek().addChild(tokenList.get(index).getValue());
            matchType(new Token("inId", ""));
            previousNodes.pop();
        } catch (TypeOrValueException e) {
            e.printExpectedToken();
        }

    }

    private void writeStmt() {
        try {
            System.out.println("writeStmt is Found");
            Node writeStmt = new Node("writeStmt");
            currentNode = previousNodes.peek();
            currentNode.addChild(writeStmt);
            previousNodes.push(writeStmt);
            matchValue(new Token("write"));
            System.out.println("write");
            exp();
            previousNodes.pop();
        } catch (TypeOrValueException e) {
            e.printExpectedToken();
        }

    }

    private void exp() {
        System.out.println("exp is Found");
        simpleExp();
        while (tokenList.get(index).getValue().equals("<") ||
                tokenList.get(index).getValue().equals("=")) {
            compOp();
            simpleExp();
        }
    }

    private void compOp() {
        changeChildrenOrder();
        if (tokenList.get(index).getValue().equals("=")) {
            try {
                matchValue(new Token("="));
            } catch (TypeOrValueException e) {
                e.printExpectedToken();
            }
        } else if (tokenList.get(index).getValue().equals("<")) {
            try {
                matchValue(new Token("<"));
            } catch (TypeOrValueException e) {
                e.printExpectedToken();
            }
        } else
            //TODO Handle Errors
            return;

    }

    private void simpleExp() {
        term();
        while (tokenList.get(index).getValue().equals("+") ||
                tokenList.get(index).getValue().equals("-")) {
            addOp();
            term();
        }

    }

    private void addOp() {
        try {

            changeChildrenOrder();
            if (tokenList.get(index).getValue().equals("+"))
                matchValue(new Token("+"));
            else if (tokenList.get(index).getValue().equals("-"))
                matchValue(new Token("-"));
            else
                matchValue(new Token("addOp"));
        } catch (TypeOrValueException e) {
            e.printExpectedToken();
        }
    }

    private void term() {
        factor();
        while (tokenList.get(index).getValue().equals("/") ||
                tokenList.get(index).getValue().equals("*")) {
            mulOp();
            factor();
        }
    }

    private void mulOp() {
        try {
            changeChildrenOrder();
            if (tokenList.get(index).getValue().equals("*"))
                matchValue(new Token("*"));
            else if (tokenList.get(index).getValue().equals("/"))
                matchValue(new Token("/"));
            else
                matchValue(new Token("mulOp"));
        } catch (TypeOrValueException e) {
            e.printExpectedToken();
        }

    }

    private void changeChildrenOrder() {
        Node plusMinus = new Node(tokenList.get(index).getValue());
        currentNode = previousNodes.peek();
        Node deleted = currentNode.deleteChild();
        currentNode.addChild(plusMinus);
        plusMinus.addChild(deleted);
        previousNodes.push(plusMinus);
        yoyo = true;
    }

    private void factor() {
        try {

            if (tokenList.get(index).getValue().equals("(")) {
                matchValue(tokenList.get(index));
                exp();
                matchValue(new Token(")"));
            } else if (tokenList.get(index).getType().equals("inNum")) {
                Node n= new Node(tokenList.get(index).getValue());
                previousNodes.peek().addChild(n);
                matchType(new Token("inNum", ""));
            } else if (tokenList.get(index).getType().equals("inId")) {
                Node n= new Node(tokenList.get(index).getValue());
                previousNodes.peek().addChild(n);
                matchType(new Token("inId", ""));
            } else
                matchValue(new Token("( or Number or identifier"));
        } catch (TypeOrValueException e) {
            e.printExpectedToken();
        }
        if(yoyo){
            previousNodes.pop();
            yoyo=false;
        }
    }

    public Node getRoot() {
        return root;
    }
}
