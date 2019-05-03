package parser;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private Text text;
    @JsonIgnore
    private Node parent = null;
    private List<Node> children = new ArrayList<>();

    public Node(String text) {
        this.text = new Text(text);
    }



    public Text getText() {
        return text;
    }

    public void setText(String text) {
        this.text = new Text(text);
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public void addChild(Node child) {
        child.parent = this;
        this.children.add(child);
    }
    public  Node deleteChild(){
       return this.children.remove(children.size()-1);
    }

    public void addChild(String data) {
        Node newChild = new Node(data);
        this.addChild(newChild);
    }

    public void addChildren(List<Node> children) {
        for (Node child : children) {
            child.setParent(this);
        }
        this.children.addAll(children);
    }
    public boolean haveChildren(){
        if(children.size()>0)
            return true;
        return false;
    }

}
