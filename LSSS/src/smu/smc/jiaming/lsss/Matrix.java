package smu.smc.jiaming.lsss;

public class Matrix {
    public static Matrix ONE;
    static {
        ONE = new Matrix(1, 1);
        ONE.matrix[0][0] = 1;
    }

    private int rows;
    private int cols;
    private int[][] matrix;

    public Matrix(int rows, int cols, int init){
        this.rows = rows;
        this.cols = cols;
        this.matrix = new int[rows][cols];
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++)
                this.matrix[i][j] = init;
        }
    }

    public Matrix(int rows, int cols){
        this.rows = rows;
        this.cols = cols;
        this.matrix = new int[rows][cols];
    }

    public int getValue(int i, int j){
        return matrix[i][j];
    }

    public void setValue(int i, int j, int value){
        this.matrix[i][j] = value;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Matrix add(Matrix a){
        if(this.rows != a.rows || this.cols != a.cols)
            return null;
        Matrix res = new Matrix(this.rows, this.cols);
        for(int i = 0; i < this.rows; i++){
            for(int j = 0; j < this.cols; j++)
                res.matrix[i][j] = this.matrix[i][j]+a.matrix[i][j];
        }
        return res;
    }

    public Matrix sub(Matrix a){
        if(this.rows != a.rows || this.cols != a.cols)
            return null;
        Matrix res = new Matrix(this.rows, this.cols);
        for(int i = 0; i < this.rows; i++){
            for(int j = 0; j < this.cols; j++)
                res.matrix[i][j] = this.matrix[i][j] - a.matrix[i][j];
        }
        return res;
    }

    public Matrix mul(Matrix a){
        if(this.cols != a.rows)
            return null;
        Matrix res = new Matrix(this.rows, a.cols);
        int temp;
        for(int i = 0; i < this.rows; i++){
            for(int j = 0; j < a.cols; j++){
                temp = 0;
                for(int k = 0; k < this.cols; k++){
                    temp += this.matrix[i][k]*a.matrix[k][j];
                }
                res.matrix[i][j] = temp;
            }
        }
        return res;
    }

    public Vector mul(Vector a){
        if(this.cols != a.getLen() || a.isFlag())
            return null;
        Vector res = new Vector(this.rows, false);
        int temp;
        for(int i = 0; i < this.rows; i++){
            temp = 0;
            for(int j = 0; j < this.cols; j++)
                temp += this.matrix[i][j]*a.getValue(j);
            res.setValue(i, temp);
        }
        return res;
    }

    public Matrix extract(int[] rows){
        Matrix res = new Matrix(rows.length, this.cols);
        for(int i = 0; i < res.rows; i++){
            for(int j = 0; j < res.cols; j++){
                if(rows[i] >= this.rows || rows[i] < 0)
                    return null;
                res.matrix[i][j] = this.matrix[rows[i]][j];
            }
        }
        return res;
    }

    public Vector GaussianElimination(Vector res){
        if(this.rows != res.getLen() || res.isFlag())
            return null;
        Matrix tm = new Matrix(this.rows, this.cols+1);
        for(int i = 0 ;i < this.rows; i++){
            for(int j = 0; j < this.cols; j++){
                tm.setValue(i, j, this.getValue(i, j));
            }
            tm.setValue(i, this.getCols(), res.getValue(i));
        }
        int max, maxI;
        for(int i = 0; i < this.cols; i++) {
            if (i >= this.rows)
                break;
            max = tm.getValue(i, i);
            maxI = i;
            for (int j = i; j < this.rows; j++) {
                if (max < tm.getValue(j, i) && tm.getValue(j, i) != 0) {
                    max = tm.getValue(j, i);
                    maxI = j;
                }
            }
            if (i != maxI)
                tm.switchRow(i, maxI);
            for( int j = 0; j < this.rows; j++){
                tm.performRow(i, j);
            }
        }
        Vector resv = new Vector(this.cols, false);
        for(int i = 0; i < this.cols; i++){
            if(i < this.rows)
                resv.setValue(i, tm.getValue(i, tm.cols-1));
        }
        return resv;
    }

    private boolean switchRow(int row1, int row2){
        if(row1 < 0 || row1 >= this.rows || row2 < 0 || row2 >= this.rows )
            return false;
        int tmp;
        for(int i = 0; i < this.cols; i++){
            tmp = this.getValue(row1, i);
            this.setValue(row1, i, this.getValue(row2, i));
            this.setValue(row2, i, tmp);
        }
        return true;
    }

    private boolean performRow(int fix, int target){
        if(fix < 0 || fix >= this.rows || target < 0 || target >= this.rows )
            return false;
        int tmp;
        if(fix == target){
            tmp = this.matrix[fix][fix];
            if(tmp != 0){
                for (int i = 0; i < this.cols; i++) {
                    this.matrix[fix][i] = this.matrix[fix][i] / tmp;
                }
            }
        }else{
            tmp = this.matrix[fix][fix];
            if(tmp != 0){
                tmp = this.matrix[target][fix] / tmp;
                if(tmp != 0) {
                    for (int i = 0; i < this.cols; i++) {
                        this.matrix[target][i] -= this.matrix[fix][i] * tmp;
                    }
                }
            }

        }
        return true;
    }

    public Matrix transform(){
        Matrix res = new Matrix(this.cols, this.rows);
        for(int i = 0; i < this.rows; i++){
            for(int j = 0; j < this.cols; j++)
                res.matrix[j][i] = this.matrix[i][j];
        }
        return res;
    }

    public String toString(){
        int cols = this.getCols();
        int rows = this.getRows();
        String res = "{\n";
        for(int i = 0; i < rows; i++){
            res += "    ";
            for(int j = 0; j < cols; j++){
                res += this.getValue(i, j) + ", ";
            }
            res += "\n";
        }
        res += "}";
        return res;
    }
}
