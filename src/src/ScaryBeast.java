package src;

public class ScaryBeast extends Beasts{
    private Object varA;
    private Object varB;
    private int varC;
    private int varD;

    public ScaryBeast(String name){
        super(name);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    /*@Override
    public boolean equals(Object obj) {
        if (ScaryBeast.name == UnknownBeast.name)
            return true;
        else return false;
    }
*/

    @Override
    public int hashCode() {
        final int p = 31;
        int result = 1;
        result = p * result + varC;
        result = p * result + varD;
        return result;
    }

    @Override
    public String toString() {
        return super.getName();
    }
}

