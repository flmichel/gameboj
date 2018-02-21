package ch.epfl.gameboj.component.memory;

import java.util.Arrays;
import java.lang.Byte;
import static ch.epfl.gameboj.Preconditions.checkBits8;


final public class Rom {
            
    private byte[] data;
    
    public Rom(byte[] data) {
        if (data.length == 0)
            throw new NullPointerException();
        else
            data = Arrays.copyOf(this.data, this.data.length);
    }
    
    public int size() {
        return data.length;
    }
    
    public int read(int index) {
        checkBits8(data[index]);
        return Byte.toUnsignedInt(data[index]);
    }
}
