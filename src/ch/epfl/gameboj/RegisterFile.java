package ch.epfl.gameboj;

import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.bits.Bits;

public final class RegisterFile<E extends Register> {
    
    E[] allRegs;
    byte[] values;
    
    public RegisterFile(E[] allRegs) {
        this.allRegs = allRegs;
        values = new byte[allRegs.length];
    }
    
    public int get(E reg) {
        return Byte.toUnsignedInt(values[reg.index()]);
    }
    
    public void set(E reg, int newValue) {
        Preconditions.checkBits8(newValue);
        values[reg.index()] = (byte) newValue;
    }
    
    public boolean testBit(E reg, Bit b) {
        return Bits.test(values[reg.index()], b);
    }
    
    public void setBit(E reg, Bit bit, boolean newValue) {
        Bits.set(values[reg.index()], bit.index(), newValue);
    }
}
