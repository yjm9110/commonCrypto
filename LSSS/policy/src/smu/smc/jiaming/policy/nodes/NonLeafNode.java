package smu.smc.jiaming.policy.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class NonLeafNode extends Node {
    public static final char AND1 = '*';
    public static final char AND2 = '&';
    public static final char OR1 = '+';
    public static final char OR2 = '|';
    public static final char LEFT = '(';
    public static final char RIGHT = ')';
    private static HashMap<Character, Integer> ORDERMAP = new HashMap();

    static {
        ORDERMAP.put('*', 2);
        ORDERMAP.put('&', 2);
        ORDERMAP.put('+', 1);
        ORDERMAP.put('|', 1);
        ORDERMAP.put('(', 3);
        ORDERMAP.put(')', 4);
    }

    private char value;
    private boolean isSort = false;
    private ArrayList<Node> childNodes = new ArrayList();

    public NonLeafNode(char value) {
        this.value = value;
        this.order = (Integer) ORDERMAP.get(this.value);
    }

    public void setChildNode(Node child) {
        this.childNodes.add(child);

    }

    public void setChildNodes(ArrayList<Node> childnodes) {
        Iterator var3 = childnodes.iterator();

        while (var3.hasNext()) {
            Node node = (Node) var3.next();
            this.childNodes.add(node);
        }

    }

    public char getValue() {
        return this.value;
    }

    public void setValue(char value) {
        this.value = value;
        this.order = (Integer) ORDERMAP.get(this.value);
    }

    public ArrayList<Node> getChildNodes() {
        return this.childNodes;
    }

    public boolean computeNode(ArrayList<String> list) {
        boolean res = false;
        Node node;
        Iterator var4;
        if (this.getOrder() == 2) {
            res = true;
            var4 = this.childNodes.iterator();

            while (var4.hasNext()) {
                node = (Node) var4.next();
                if (!node.computeNode(list)) {
                    res = false;
                    break;
                }
            }
        } else if (this.getOrder() == 1) {
            res = false;
            this.sortNodeList();
            var4 = this.childNodes.iterator();

            while (var4.hasNext()) {
                node = (Node) var4.next();
                if (node.computeNode(list)) {
                    res = true;
                    break;
                }
            }
        }

        return res;
    }

    public boolean computeNode(ArrayList<String> list, Integer b) {
        System.out.println("compute " + (b = b + 1) + " times!");
        boolean res = false;
        Node node;
        Iterator var5;
        if (this.getOrder() == 2) {
            res = true;
            var5 = this.childNodes.iterator();

            while (var5.hasNext()) {
                node = (Node) var5.next();
                if (!node.computeNode(list, b)) {
                    res = false;
                    break;
                }
            }
        } else if (this.getOrder() == 1) {
            res = false;
            this.sortNodeList();
            var5 = this.childNodes.iterator();

            while (var5.hasNext()) {
                node = (Node) var5.next();
                if (node.computeNode(list, b)) {
                    res = true;
                    break;
                }
            }
        }

        return res;
    }

    private ArrayList<Node> sortNodeList() {
        if (this.isSort) {
            return this.childNodes;
        } else {
            Collections.sort(this.childNodes, Node.DEEP_COM);
            this.isSort = true;
            return this.childNodes;
        }
    }

    public static boolean isOperator(char c) {
        Integer v = (Integer) ORDERMAP.get(c);
        return v != null;
    }

    public static boolean isOperator(String item) {
        if (item.length() > 1) {
            return false;
        } else {
            char c = item.charAt(0);
            return isOperator(c);
        }
    }

    public String toString() {
        String self = "";
        if (this.getOrder() > 2) {
            return self + this.getValue();
        } else {
            if (this.getOrder() == 1) {
                self = "+";
            }

            if (this.getOrder() == 2) {
                self = "*";
            }

            String res = "";

            for (int i = 0; i < this.childNodes.size(); ++i) {
                Node temp = this.childNodes.get(i);
                if (i == 0) {
                    res = res + temp.toString();
                } else if (this.getOrder() > temp.getOrder() && temp.getOrder() > 0) {
                    res = res + self + "(" + temp.toString() + ")";
                } else {
                    res = res + self + temp.toString();
                }
            }

            return res;
        }
    }
}