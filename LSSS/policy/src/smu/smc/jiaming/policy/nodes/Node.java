package smu.smc.jiaming.policy.nodes;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class Node {
    public static final Comparator<Node> DEEP_COM = new Comparator<Node>() {
        public int compare(Node arg0, Node arg1) {
            if (arg0.getOrder() > arg1.getOrder()) {
                return 1;
            } else {
                return arg0.getOrder() < arg0.getOrder() ? -1 : 0;
            }
        }
    };
    protected int order = 0;

    public Node() {
    }

    public abstract boolean computeNode(ArrayList<String> var1);

    public abstract boolean computeNode(ArrayList<String> var1, Integer var2);

    public abstract String toString();

    public int getOrder() {
        return this.order;
    }
}
