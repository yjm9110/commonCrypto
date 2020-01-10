package smu.smc.jiaming.policy;

import smu.smc.jiaming.policy.nodes.LeafNode;
import smu.smc.jiaming.policy.nodes.Node;
import smu.smc.jiaming.policy.nodes.NonLeafNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;

public class PolicyTree {
    private Node root;
    private ArrayList<Node> nodelist;

    public PolicyTree() {
    }

    public void initTree(String policy) {
        this.nodelist = this.toNodesList(policy);
        this.root = this.buildTree(this.nodelist);
    }

    public Node getRoot() {
        return this.root;
    }

    public ArrayList<Node> getNodelist() {
        return this.nodelist;
    }

    public Node buildTree(ArrayList<Node> list) {
        Stack<Node> leafStack = new Stack();
        Stack<NonLeafNode> nodeStack = new Stack();
        Node prenode = null;
        Node tempnode = null;
        NonLeafNode branchNode = null;

        for(int i = 0; i < list.size(); ++i) {
            tempnode = (Node)list.get(i);
            if (tempnode.getOrder() == 3) {
                nodeStack.add((NonLeafNode)tempnode);
            } else if (tempnode.getOrder() > 0 && tempnode.getOrder() != 3) {
                if (i == 0) {
                    return null;
                }

                if (tempnode.getOrder() != 4) {
                    if (prenode == null) {
                        nodeStack.add((NonLeafNode)tempnode);
                    } else if (prenode != null && tempnode.getOrder() != 4) {
                        if (nodeStack.isEmpty()) {
                            leafStack.add(prenode);
                            nodeStack.add((NonLeafNode)tempnode);
                        } else {
                            switch(((NonLeafNode)nodeStack.peek()).getOrder()) {
                                case 1:
                                    leafStack.add(prenode);
                                    nodeStack.add((NonLeafNode)tempnode);
                                    break;
                                case 2:
                                    if (((Node)leafStack.peek()).getOrder() > 0) {
                                        branchNode = (NonLeafNode)leafStack.peek();
                                        branchNode.setChildNode(prenode);
                                        leafStack.pop();
                                        leafStack.add(branchNode);
                                        nodeStack.pop();
                                    } else {
                                        branchNode = (NonLeafNode)nodeStack.peek();
                                        branchNode.setChildNode(prenode);
                                        branchNode.setChildNode((Node)leafStack.peek());
                                        leafStack.pop();
                                        leafStack.add(branchNode);
                                        nodeStack.pop();
                                    }

                                    nodeStack.add((NonLeafNode)tempnode);
                                    break;
                                case 3:
                                    leafStack.add(prenode);
                                    nodeStack.add((NonLeafNode)tempnode);
                                case 4:
                            }
                        }
                    }
                } else {
                    Node flag = null;
                    if (prenode == null && ((Node)leafStack.peek()).getOrder() > 0) {
                        flag = (Node)leafStack.pop();
                    } else {
                        flag = prenode;
                    }

                    if (((NonLeafNode)nodeStack.peek()).getOrder() == 2) {
                        if (((Node)leafStack.peek()).getOrder() > 0) {
                            branchNode = (NonLeafNode)leafStack.peek();
                            branchNode.setChildNode((Node)flag);
                            leafStack.pop();
                            nodeStack.pop();
                        } else {
                            branchNode = (NonLeafNode)nodeStack.peek();
                            branchNode.setChildNode((Node)flag);
                            branchNode.setChildNode((Node)leafStack.peek());
                            leafStack.pop();
                            nodeStack.pop();
                        }

                        flag = branchNode;
                    }

                    NonLeafNode temp = null;
                    if (leafStack.isEmpty()) {
                        leafStack.add(flag);
                        nodeStack.pop();
                    } else {
                        while(nodeStack.peek() != null && ((NonLeafNode)nodeStack.peek()).getOrder() != 3) {
                            temp = (NonLeafNode)nodeStack.peek();
                            temp.setChildNode((Node)flag);
                            temp.setChildNode((Node)leafStack.peek());
                            flag = temp;
                            nodeStack.pop();
                            leafStack.pop();
                        }

                        nodeStack.pop();
                        if (!nodeStack.isEmpty() && ((NonLeafNode)nodeStack.peek()).getOrder() != 3 && ((NonLeafNode)nodeStack.peek()).getOrder() != 1) {
                            temp = (NonLeafNode)nodeStack.peek();
                            temp.setChildNode((Node)leafStack.peek());
                            temp.setChildNode((Node)flag);
                            nodeStack.pop();
                            leafStack.pop();
                            leafStack.add(temp);
                        } else {
                            leafStack.add(flag);
                        }
                    }

                    prenode = null;
                }
            } else if (tempnode.getOrder() == 0) {
                prenode = (LeafNode)tempnode;
            }
        }

        Node res = null;
        if (tempnode != null && tempnode.getOrder() == 0) {
            leafStack.add(tempnode);
        }

        if (nodeStack.isEmpty()) {
            res = (Node)leafStack.pop();
            if (leafStack.isEmpty()) {
                this.root = res;
            } else {
                this.root = null;
            }

            return this.root;
        } else {
            branchNode = null;

            while(!leafStack.isEmpty()) {
                if (nodeStack.isEmpty()) {
                    this.root = (Node)leafStack.pop();
                    break;
                }

                branchNode = (NonLeafNode)nodeStack.peek();
                branchNode.setChildNode((Node)leafStack.pop());
                branchNode.setChildNode((Node)leafStack.pop());
                nodeStack.pop();
                if (leafStack.isEmpty()) {
                    this.root = branchNode;
                    break;
                }

                leafStack.add(branchNode);
            }

            return this.root;
        }
    }

    public boolean compute(ArrayList<String> list) {
        return this.root.computeNode(list);
    }

    public boolean compute(String[] array) {
        return this.root.computeNode(new ArrayList(Arrays.asList(array)));
    }

    private ArrayList<Node> toNodesList(String str) {
        ArrayList<Node> res = new ArrayList();
        char[] temp = str.trim().toCharArray();
        String leaf_str = "";
        Node tempnode = null;
        String tempitem = null;
        char[] var12 = temp;
        int var11 = temp.length;

        for(int var10 = 0; var10 < var11; ++var10) {
            char c = var12[var10];
            if (NonLeafNode.isOperator(c)) {
                if (!"".equals(leaf_str)) {
                    tempitem = leaf_str.trim();

                    tempnode = new LeafNode(tempitem);
                    res.add(tempnode);
                    leaf_str = "";
                }

                tempnode = new NonLeafNode(c);
                res.add(tempnode);
            } else {
                leaf_str = leaf_str + c;
            }
        }

        if (!"".equals(leaf_str)) {
            tempitem = leaf_str.trim();

            tempnode = new LeafNode(tempitem);
            res.add(tempnode);
        }

        return res;
    }

    private ArrayList<Node> toNodesList(ArrayList<String> list) {
        ArrayList<Node> res = new ArrayList();
        Node tempnode = null;

        for(Iterator var5 = list.iterator(); var5.hasNext(); res.add(tempnode)) {
            String item = (String)var5.next();
            if (NonLeafNode.isOperator(item)) {
                tempnode = new NonLeafNode(item.charAt(0));
            } else {
                tempnode = new LeafNode(item);
            }
        }

        return res;
    }

    public ArrayList<String> getAttrList() {
        ArrayList<String> res = new ArrayList();

        String atemp;
        for(Iterator var6 = this.nodelist.iterator(); var6.hasNext(); res.add(atemp)) {
            Node node = (Node)var6.next();
            if (node.getOrder() > 0) {
                NonLeafNode ntemp = (NonLeafNode)node;
                atemp = String.valueOf(ntemp.getValue());
            } else {
                LeafNode ltemp = (LeafNode)node;
                atemp = ltemp.getValue();
            }
        }

        return res;
    }

    public String toString() {
        return this.root.toString();
    }

    public static void main(String[] args) {
        String policy1 = "(attr1&attr2|attr3)|(attr4&attr5&(attr6|attr7))|attr8";
        policy1 = "attr1&attr2&(attr3|(attr4&attr5|(attr6|attr7)))";
        ArrayList<String> list = new ArrayList();

        for(int i = 0; i < 20; ++i) {
            list.add("attr" + (i+1));
        }

        ArrayList<String> userlist = new ArrayList();
        userlist.add(list.get(0));
        userlist.add(list.get(1));
        userlist.add(list.get(3));
        userlist.add(list.get(5));
        PolicyTree t = null;
        PolicyTree p = new PolicyTree();
        p.initTree(policy1);
        System.out.println(p);
        System.out.println(p.compute(userlist));
        t = p;
        p = new PolicyTree();
        p.initTree(t.toString());
        System.out.println(p.toString());
        System.out.println(p.compute(userlist));
    }
}
