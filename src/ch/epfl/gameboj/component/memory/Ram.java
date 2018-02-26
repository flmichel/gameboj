package ch.epfl.gameboj.component.memory;

import java.lang.Byte;
import ch.epfl.gameboj.Preconditions;


final public class Ram {
            
    private byte[] data;
    
    public Ram(int size) {
        Preconditions.checkArgument(size >= 0);
        data = new byte[size];
    }
    
    public int size() {
        return data.length;
    }
    
    public int read(int index) {
        if (index < 0 || index >= data.length)
            throw new IndexOutOfBoundsException();
        return Byte.toUnsignedInt(data[index]);
    }
    
    public void write(int index, int value) {
        if (index < 0 || index >= data.length)
            throw new IndexOutOfBoundsException();
        data[index] = (byte) Preconditions.checkBits8(value);

    }
}
