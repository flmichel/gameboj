package ch.epfl.gameboj.component.memory;

import ch.epfl.gameboj.component.Component;
import static ch.epfl.gameboj.Preconditions.checkBits16;
import static ch.epfl.gameboj.Preconditions.checkBits8;


public class RamController implements Component {
    
    private Ram ram;
    private int startAddress;
    private int endAddress;
    
    RamController(Ram ram, int startAddress, int endAddress) {
        if (ram == null)
            throw new NullPointerException();
        checkBits16(startAddress);
        checkBits16(endAddress);
        if (endAddress == 0)
            endAddress = ram.size();
        else if ((ram.size() < endAddress) || 0 > endAddress - startAddress)
            throw new IllegalArgumentException();
        ram = this.ram;
        startAddress = this.startAddress;
        endAddress = this.endAddress;
    }
    
    RamController(Ram ram, int startAddress) {
        this(ram, startAddress, 0);
    }
    
    public int read(int address) {
        checkBits16(address);
        if (!isAccessible(address))
            return NO_DATA;
        return ram.read(address);
    }

    public void write(int address, int data) {
        checkBits16(address);
        checkBits8(data);
        if (isAccessible(address))
            ram.write(address, data);  
    }
    
    private boolean isAccessible(int address) {
        if (address < startAddress || address >= endAddress)
            return false;
        return true;
    }

}
