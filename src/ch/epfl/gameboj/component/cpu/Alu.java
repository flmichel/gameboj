package ch.epfl.gameboj.component.cpu;

import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.Preconditions;

public final class Alu {
    
    private Alu() {}
    
    enum Flag implements Bit {
        UNUSED_0, UNUSED_1, UNUSED_2, UNUSED_3, Z, N, H, C
    }

    enum RotDir {
        LEFT, RIGHT
    }
    
    private static int packValueZNHC(int v, boolean z, boolean n, boolean h, boolean c) {
        return v << 8 | maskZNHC(z, n, h, c);
    }
    
    static public int maskZNHC(boolean z, boolean n, boolean h, boolean c) {
        int mask = 0;
        if (z) mask += Flag.Z.mask();
        if (n) mask += Flag.N.mask();
        if (h) mask += Flag.H.mask();
        if (c) mask += Flag.C.mask();
        return mask;
    }
    
    public static int unpackValue(int valueFlags) {
        Preconditions.checkBits16(valueFlags);
        return Bits.extract(valueFlags, 8, 8);
    }
    
    public static int unpackFlags(int valueFlags) {
        Preconditions.checkBits16(valueFlags);
        return Bits.clip(valueFlags, 8);
    }
    
    public static int add(int l, int r, boolean c0) {
        Preconditions.checkBits8(l);
        Preconditions.checkBits8(r);
        
        boolean z = false, h = false, c = false;
        int value = l+r;
        int valueBits4 = Bits.clip(l, 4) + Bits.clip(r, 4);
        
        if (c0) {
            value++;
            valueBits4++;
        }
        
        if (value == 0) z = true;
        if (Bits.test(valueBits4, 4)) h = true;
        if (Bits.test(value, 8)) {
            c = true;
            value = Bits.set(value, 8, false);
        }

        return packValueZNHC(value, z, false, h, c);
    }
    
    public static int add(int l, int r) {
       return add(l, r, false);
    }
    private static int add16(int l, int r, boolean low) {
        Preconditions.checkBits16(l);
        Preconditions.checkBits16(r);
        
        boolean c = false;
        int lowB = add(Bits.clip(l, 8), Bits.clip(l, 8));
        
        if (Bits.test(lowB, Flag.C.index())) c = true;
        
        int highB = add(Bits.extract(l, 8, 8), Bits.extract(r, 8, 8), c);
        
        int flagB;
        if (low) flagB = lowB;
        else flagB = highB;
                
        int flags = Bits.set(unpackFlags(flagB), Flag.Z.index(), false);
        
        return Bits.make16(unpackValue(highB), unpackValue(lowB)) << 8 | flags;
    }
    public static int add16L(int l, int r) {
        return add16(l, r, true);
    }
    
    public static int add16H(int l, int r) {
        return add16(l, r, false);
    }
    
    public static int sub(int l, int r, boolean b0) {
        Preconditions.checkBits8(l);
        Preconditions.checkBits8(r);
        int value = l-r;
        
    }
    
    public static int sub(int l, int r) {
        
    }
    
    public static int bcdAdjust(int v, boolean n, boolean h, boolean c) {
        
    }
    
    public static int and(int l, int r) {
        
    }
    
    public static int or(int l, int r) {
        
    }
    
    public static int xor(int l, int r) {
        
    }
    
    public static int shiftLeft(int v) {
        
    }
    
    public static int shiftRightA(int v) {
        
    }
    
    public static int shiftRightL(int v) {
        
    }
    
    public static int rotate(RotDir d, int v) {
        
    }
    
    public static int rotate(RotDir d, int v, boolean c) {
        
    }
    
    public static int swap(int v) {
        
    }
    
    public static int testBit(int v, int bitIndex) {
        
    }
}
