package smu.smc.jiaming.lsss;

import java.util.Arrays;

public class LSSSShares {
    private Vector vector;
    private String[] map;

    public LSSSShares(Vector vector, String[] map){
        this.vector = vector;
        this.map = map;
    }

    public String toString(){
        int len = this.vector.getLen();
        String res = "{\n";
        for(int i = 0; i < len; i++){
            res += "    " + this.vector.getValue(i) + "  -- " + this.map[i] + " " + i + "\n";
        }
        res += "}";
        return res;
    }

    public LSSSShares extract(String[] attrs){
        int[] res = new int[this.map.length];
        int index = 0;
        int start = 0;
        int temp = 0;
        for(int i = 0; i < attrs.length; i++){
            start = 0;
            while(start < this.map.length) {
                temp = Arrays.binarySearch(this.map, start, this.map.length, attrs[i]);
                if (temp >= 0){
                    res[index++] = temp;
                    start = temp + 1;
                }else
                    break;
            }
        }
        res = Arrays.copyOf(res, index);

        Vector vector = this.vector.extract(res);
        String[] map = new String[res.length];
        for(int i = 0; i < res.length; i++){
            map[i] = this.map[res[i]];
        }
        return new LSSSShares(vector, map);
    }

    public int recover(Vector lambda){
        return this.vector.transform().mul1(lambda);
    }
}
