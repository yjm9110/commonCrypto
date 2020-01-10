package smu.smc.jiaming.lsss;

public class Vector {
    // true - row vector; false - col vector
    private boolean flag;
    private int len;
    private int[] vector;

    public Vector(int len, boolean flag){
        this.len = len;
        this.flag = flag;
        this.vector = new int[len];
    }

    public Vector(boolean flag, int[] vector){
        this.len = vector.length;
        this.flag = flag;
        this.vector = vector;
    }

    public Vector(int len){
        this(len, true);
    }

    public int getValue(int i){
        return this.vector[i];
    }

    public void setValue(int i, int value){
        this.vector[i] = value;
    }

    public boolean isFlag() {
        return flag;
    }

    public int getLen() {
        return len;
    }

    public Vector transform(){
        Vector res = new Vector(this.len, !this.flag);
        for(int i = 0; i < this.len; i++)
            res.vector[i] = this.vector[i];
        return res;
    }

    public Vector add(Vector a){
        if(this.flag != a.flag || this.len != a.len)
            return null;
        Vector res = new Vector(this.len, this.flag);
        for(int i = 0; i < this.len; i++)
            res.vector[i] = this.vector[i] + a.vector[i];
        return res;

    }

    public Vector sub(Vector a){
        if(this.flag != a.flag || this.len != a.len)
            return null;
        Vector res = new Vector(this.len, this.flag);
        for(int i = 0; i < this.len; i++)
            res.vector[i] = this.vector[i] - a.vector[i];
        return res;
    }

    public Vector mul(Matrix a){
        if(this.len != a.getRows() || !this.flag)
            return null;
        Vector res = new Vector(a.getCols(), this.flag);
        int temp;
        for(int i = 0; i < a.getCols(); i++){
            temp = 0;
            for(int j = 0; j < this.len; j++)
                temp += this.vector[j]*a.getValue(j, i);
            res.vector[i] = temp;
        }
        return res;
    }

    public Matrix mul(Vector a){
        if(this.flag || !a.flag)
            return null;
        Matrix res = new Matrix(this.len, a.len);
        for(int i = 0; i < this.len; i++){
            for(int j = 0 ; j < this.len ; j++){
                res.setValue(i, j, this.vector[i]*a.vector[j]);
            }
        }
        return res;
    }

    public int mul1(Vector a){
        if(!this.flag || a.flag || this.len != a.len)
            return 0;
        int res = 0;
        for(int i = 0; i < this.len; i++)
                res += this.vector[i] * a.vector[i];
        return res;
    }

    public Vector extract(int[] indexes) {
        Vector res = new Vector(indexes.length, this.flag);
        for(int i = 0; i < indexes.length; i++){
            res.setValue(i, this.getValue(indexes[i]));
        }
        return res;
    }

    public String toString(){
        int len = this.getLen();
        String res = "{";
        for(int i = 0; i < len; i++){
            res += " " + this.getValue(i) + ", ";
        }
        res += "} -- " + this.isFlag();
        return res;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(this == obj)
            return true;
        if(! (obj instanceof Vector))
            return false;
        if(((Vector)obj).isFlag() != this.isFlag())
            return false;
        if(((Vector)obj).len != this.len)
            return false;
        if(((Vector)obj).vector == this.vector)
            return true;
        if(((Vector)obj).vector == null)
            return false;
        for(int i = 0; i < this.len; i++){
            if(((Vector)obj).vector[i] != this.vector[i])
                return false;
        }
        return true;
    }
}
