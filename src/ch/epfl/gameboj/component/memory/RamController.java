package ch.epfl.gameboj.component.memory;

import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.Preconditions;


public class RamController implements Component {
    
    private Ram ram;
    private int startAddress;
    private int endAddress;
    
    RamController(Ram ram, int startAddress, int endAddress) {
        if (ram == null)
            throw new NullPointerException();
        Preconditions.checkBits16(startAddress);
        Preconditions.checkBits16(endAddress);
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
        Preconditions.checkBits16(address);
        if (!isAccessible(address))
            return NO_DATA;
        return ram.read(address);
    }

    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);
        if (isAccessible(address))
            ram.write(address, data);  
    }
    
    private boolean isAccessible(int address) {
        if (address < startAddress || address >= endAddress)
            return false;
        return true;
    }

}
