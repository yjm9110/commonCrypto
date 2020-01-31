import java.util.*;

public class Policy {
    public static Node convert(String p, boolean isK){
        p = p.trim();
        if(isK){
            int j = 0;
            while(p.charAt(j) == '(' && p.charAt(p.length()-1-j) == ')'){
                j++;
            }
            if(j > 0)
                p = p.substring(j, (p.length() - j));
        }
        return convert(p);
    }
    public static Node convert(String p){
        Node left;
        Node right;
        String tmp = "";
        boolean isK = false;
        char opt = 0, opt1 = 0;
        int l=0;
        Stack<Node> chs = new Stack<>();
        Node res = null;
        for(int i = 0; i < p.length(); i++){
            char t = p.charAt(i);
            if(t == '('){
                l++;
                tmp += t;
                isK = true;
            }else if(t == ')') {
                l--;
                tmp += t;
            }else if( l < 0){
                System.err.println("Error ')' at index " + i + " ---> " + p);
                return null;
            }else if(l == 0){
                 if(t == '+' || t == '|'){
                     // a |
                     if(opt == 0){
                         chs.push(convert(tmp,isK));
                         isK = false;
                     // a | b |  // a & b |
                     }else{
                         chs.push(new NNode(opt, chs.pop(), convert(tmp,isK)));
                         isK = false;
                         if(opt1 != 0){
                             right = chs.pop();
                             left = chs.pop();
                             chs.push(new NNode( opt1, left, right));
                             opt1 = 0;
                         }
                    }
                    tmp="";
                    opt = t;
                 }else if(t == '&' || t == '*'){
                     chs.push(convert(tmp,isK));
                     isK = false;
                     tmp = "";
                    // a &
                    if(opt == 0){

                    // a | b &
                    }else if(opt == '+' || opt == '|'){
                        opt1 = opt;
                    // a & b &
                    }else if(opt == '&' || opt == '*'){
                        right = chs.pop();
                        left = chs.pop();
                        chs.push(new NNode( opt, left, right));
                    }
                    opt = t;
                }else {
                    tmp += t;
                }
            }else {
                tmp += t;
            }
            if(i == p.length() - 1){
                // a
                if(opt == 0){
                    res = new LNode(tmp.trim());
                // a | b & c
                }else if(opt1 != 0){
                    chs.push(new NNode(opt, chs.pop(), convert(tmp,isK)));
                    isK = false;
                    right = chs.pop();
                    left = chs.pop();
                    res = new NNode(opt1, left, right);
                // a | b
                }else{
                    res = new NNode(opt, chs.pop(), convert(tmp,isK));
                    isK = false;
                }
            }
        }
        if(l > 0){
            System.err.println("Missing ')'");
            return null;
        }
        if(!chs.empty()){
            System.err.println("Something Wrong !!");
        }

        // set index
        l = 0;
        res.index = l++;
        Queue<Node> q = new LinkedList<>();
        q.add(res);
        Node s;
        while(q.size() > 0){
            s =  q.poll();
            s.index = l++;
            if(s instanceof NNode){
                q.offer(((NNode) s).left);
                q.offer(((NNode) s).right);
            }
        }

        return res;
    }

    public static class Node{
        protected int degree;
        protected int index;
    }
    public static class NNode extends Node{
        private char v;
        private Node left;
        private Node right;

        public NNode(char v, Node left, Node right) {
            this.v = v;
            this.left = left;
            this.right = right;
            if(v == '+' || v == '|')
                this.degree = 1;
            else
                this.degree = 2;

        }
        public String toString(){
            String res = "";
            if(this.left.degree < this.degree)
                res += "(" + this.left.toString() + ")";
            else
                res += this.left.toString();
            if(this.right.degree < this.degree)
                res += " " + this.v + " (" + this.right.toString() + ")";
            else
                res += " " + this.v + " " + this.right.toString();
            return res;
        }
    }
    public static class LNode extends Node{
        private String v;

        public LNode(String v) {
            this.v = v;
            this.degree = 3;
        }
        public String toString(){
            return this.v + "-" + this.index;
        }
    }

    public static void main(String[] args){
        String p = "(a1 | (a2 & ( a3 | a4 ))) & a5 &(s3 |r5)";
        p = "(a1 |(a2&a3&a4))&a5";
        Node r = convert(p);
        System.out.println(r);
    }
}
