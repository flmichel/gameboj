package ch.epfl.gameboj.component.memory;

import java.lang.Byte;
import static ch.epfl.gameboj.Preconditions.checkBits8;


final public class Ram {
            
    private byte[] data;
    
    public Ram(int size) {
        if (size < 0)
            throw new IllegalArgumentException();
        else
            data = new byte[size];
    }
    
    public int size() {
        return data.length;
    }
    
    public int read(int index) {
        checkBits8(data[index]);
        return Byte.toUnsignedInt(data[index]);
    }
    
    public void write(int index, int value) {
        if (index < 0 && index >= data.length)
            throw new IndexOutOfBoundsException();
        checkBits8(value);
        data[index] = (byte) value;

    }
}
