package ch.epfl.gameboj.component.memory;

import java.util.Arrays;
import java.util.Objects;
import java.lang.Byte;


final public class Rom {
            
    private byte[] data;
    
    public Rom(byte[] data) {
        Objects.requireNonNull(data);
        this.data = Arrays.copyOf(data, data.length);
    }
    
    public int size() {
        return data.length;
    }
    
    public int read(int index) {
        if (index < 0 || index > 0xff)
            throw new IndexOutOfBoundsException();
        return Byte.toUnsignedInt(data[index]);
    }
}
