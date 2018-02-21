package ch.epfl.gameboj;

public interface Preconditions {

    static public void checkArgument(boolean b) {
        if (!b)
            throw new IllegalArgumentException();
    }
    static public int checkBits8(int v) {
        if (v >= 0 && v <= 0xff)
            return v;
        else
            throw new IllegalArgumentException();
    }
    static public int checkBits16(int v) {
        if (v >= 0 && v <= 0xffff)
            return v;
        else
            throw new IllegalArgumentException();
    }
}
