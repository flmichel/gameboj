package ch.epfl.gameboj.component.cpu;

import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.Preconditions;

public final class Alu {
    
    private Alu() {}
    
    public enum Flag implements Bit {
        UNUSED_0, UNUSED_1, UNUSED_2, UNUSED_3, C, H, N, Z
    }

    public enum RotDir {
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
        return Bits.clip(8, valueFlags);
    }
    
    public static int add(int l, int r, boolean c0) {
        Preconditions.checkBits8(l);
        Preconditions.checkBits8(r);
        
        boolean z = false, h = false, c = false;
        int value = l+r;
        int valueBits4 = Bits.clip(4, l) + Bits.clip(4, r);
        
        if (c0) {
            value++;
            valueBits4++;
        }
        
        if (Bits.test(valueBits4, 4)) h = true;
        if (Bits.test(value, 8)) {
            c = true;
            value = Bits.set(value, 8, false);
        }
        if (value == 0) z = true;

        return packValueZNHC(value, z, false, h, c);
    }
    
    public static int add(int l, int r) {
       return add(l, r, false);
    }
    private static int add16(int l, int r, boolean low) {
        Preconditions.checkBits16(l);
        Preconditions.checkBits16(r);
        
        boolean c = false;
        int lowB = add(Bits.clip(8, r), Bits.clip(8, l));
        
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
        int borrow = 0;
        if (b0) borrow = 1;
        boolean c = l - borrow < r;
        boolean h = Bits.clip(4, l) - borrow < Bits.clip(4, r);
        int value = Bits.clip(8, l - r - borrow);
        boolean z = (value == 0);
        
        return packValueZNHC(value, z, true, h, c);    
    }
    
    public static int sub(int l, int r) {
        return sub(l, r, false);
    }
    
    public static int bcdAdjust(int v, boolean n, boolean h, boolean c) {
        Preconditions.checkBits8(v);
        boolean fixL = h || (!n && Bits.clip(4, v) > 9);
        boolean fixH = c || (!n && v > 0x99);
        int fix = 0;
        if (fixL) fix += 0x06;
        if (fixH) fix += 0x60;
        int val;
        if (n) val = v - fix;
        else val = v + fix;
        
        return packValueZNHC(val, val == 0, n, false, fixH);
    }
    
    public static int and(int l, int r) {
        Preconditions.checkBits8(l);
        Preconditions.checkBits8(r);
        int val = l & r;
        return packValueZNHC(val, val == 0, false, true, false);
    }
    
    public static int or(int l, int r) {
        Preconditions.checkBits8(l);
        Preconditions.checkBits8(r);
        int val = l | r;
        return packValueZNHC(val, val == 0, false, false, false);
    }
    
    public static int xor(int l, int r) {
        Preconditions.checkBits8(l);
        Preconditions.checkBits8(r);
        int val = l ^ r;
        return packValueZNHC(val, val == 0, false, false, false);
    }
    
    public static int shiftLeft(int v) {
        Preconditions.checkBits8(v);
        int val = v << 1;
        val = Bits.clip(8, val);
        return packValueZNHC(val, val == 0, false, false, Bits.test(v, 7));
    }
    
    public static int shiftRightA(int v) {
        Preconditions.checkBits8(v);
        int val = (byte)v >> 1;
        val = Bits.clip(8, val);
        return packValueZNHC(val, val == 0, false, false, Bits.test(v, 0));
    }
    
    public static int shiftRightL(int v) {
        Preconditions.checkBits8(v);
        int val = v >>> 1;
        val = Bits.clip(8, val);
        return packValueZNHC(val, val == 0, false, false, Bits.test(v, 0)); 
    }
  
    public static int rotate(RotDir d, int v) {
        Preconditions.checkBits8(v);
        int dir, index;
        if (d.name() == "LEFT") {
            dir = 1;
            index = 0;
        } else {
            dir = -1;
            index = 7;
        }
        v = Bits.rotate(8, v, dir);
        return packValueZNHC(v, v == 0, false, false, Bits.test(v, index));
    }
    
    public static int rotate(RotDir d, int v, boolean c) {
        Preconditions.checkBits8(v);
        int dir;
        if (d.name() == "LEFT") dir = 1;
        else dir = -1;
        
        if (c) v = Bits.set(v, 8, true);
        v = Bits.rotate(9, v, dir);
        int val = Bits.clip(8, v);
        return packValueZNHC(val, val == 0, false, false, Bits.test(v, 8));
      
    }
    
    public static int swap(int v) {
        Preconditions.checkBits8(v);
        v = Bits.reverse8(v);
        return packValueZNHC(v, v == 0, false, false, false);
    }
    
    public static int testBit(int v, int bitIndex) {
        Preconditions.checkBits8(v);
        if (bitIndex < 0 | bitIndex >= 8)
            throw new IndexOutOfBoundsException();
        return packValueZNHC(0, Bits.test(v, bitIndex), false, false, false);     
    }
    
}
