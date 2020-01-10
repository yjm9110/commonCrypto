package smu.smc.jiaming.lsss;

import java.util.Arrays;

public class LSSSMatrix {
    private Matrix matrix;
    private String[] map;

    public LSSSMatrix(String attr){
        this.matrix = Matrix.ONE;
        this.map = new String[]{attr};
    }

    public LSSSMatrix(Matrix matrix, String[] map) {
        this.matrix = matrix;
        this.map = map;
    }

    public String[] getMap() {
        return map;
    }

    public LSSSMatrix or(LSSSMatrix p){
        Matrix a = this.matrix;
        Matrix b = p.matrix;
        int cols = a.getCols() + b.getCols() - 1;
        int rows = a.getRows() + b.getRows();

        Matrix res = new Matrix(rows, cols);
        String[] resmap = new String[rows];
        int temp;
        for(int i = 0; i < rows; i++) {
            if(i < a.getRows()){
                resmap[i] = this.map[i];
                for(int j = 0; j < a.getCols(); j++)
                    res.setValue(i, j, a.getValue(i, j));
            }else{
                temp = i - a.getRows();
                resmap[i] = p.map[temp];
                res.setValue(i, 0, b.getValue(temp, 0));
                for(int j = a.getCols(); j < cols; j++)
                    res.setValue(i, j, b.getValue(temp, j-a.getCols() + 1));
            }
        }
        return new LSSSMatrix(res, resmap);
    }

    public LSSSMatrix and(LSSSMatrix p){
        Matrix a = this.matrix;
        Matrix b = p.matrix;
        int cols = a.getCols() + b.getCols();
        int rows = a.getRows() + b.getRows();

        Matrix res = new Matrix(rows, cols);
        String[] resmap = new String[rows];
        int temp;
        for(int i = 0; i < rows; i++) {
            if(i < a.getRows()){
                resmap[i] = this.map[i];
                res.setValue(i, 0, a.getValue(i, 0));
                for(int j = 1; j <= a.getCols(); j++) {
                    res.setValue(i, j, a.getValue(i, j-1));
                }

            }else{
                temp = i - a.getRows();
                resmap[i] = p.map[temp];
                res.setValue(i, 1, b.getValue(temp, 0));
                for(int j = a.getCols()+1; j < cols; j++)
                    res.setValue(i, j, b.getValue(temp, j-a.getCols()));
            }
        }
        return new LSSSMatrix(res, resmap);
    }

    public LSSSShares genShareVector(Vector vector){
        Vector res = this.matrix.mul(vector);
        return new LSSSShares(res, this.map);
    }

    public LSSSMatrix extract(String[] attrs){
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

        Matrix matrix = this.matrix.extract(res);
        String[] map = new String[res.length];
        for(int i = 0; i < res.length; i++){
            map[i] = this.map[res[i]];
        }
        return new LSSSMatrix(matrix, map);
    }

    public int recover(LSSSShares shares){
        Matrix tm = this.matrix.transform();
        System.out.println(tm);
        Vector v = new Vector(tm.getRows(), false);
        v.setValue(0, 1);
        Vector rv = tm.GaussianElimination(v);
        if(!tm.mul(rv).equals(v))
            return 0;
        int res = shares.recover(rv);
        return res;
    }



    public String toString(){
        int cols = this.matrix.getCols();
        int rows = this.matrix.getRows();
        String res = "{\n";
        for(int i = 0; i < rows; i++){
            res += "    ";
            for(int j = 0; j < cols; j++){
                res += this.matrix.getValue(i, j) + ", ";
            }
            res += "  -- " + this.map[i] + " " + i + "\n";
        }
        res += "}";
        return res;
    }


}
