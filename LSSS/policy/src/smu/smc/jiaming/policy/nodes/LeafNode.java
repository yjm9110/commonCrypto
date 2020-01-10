package smu.smc.jiaming.policy.nodes;

import java.util.ArrayList;

public class LeafNode extends Node{
    private String value = null;

    public LeafNode(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean computeNode(ArrayList<String> list) {
        return list.contains(this.value);
    }

    public boolean computeNode(ArrayList<String> list, Integer b) {
        return list.contains(this.value);
    }

    public String toString() {
        return this.value;
    }
}
