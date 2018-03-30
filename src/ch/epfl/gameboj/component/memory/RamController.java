package ch.epfl.gameboj.component.memory;

import ch.epfl.gameboj.component.Component;

import java.util.Objects;

import ch.epfl.gameboj.Preconditions;


public class RamController implements Component {
    
    private Ram ram;
    private int startAddress;
    private int endAddress;
    
    public RamController(Ram ram, int startAddress, int endAddress) {
        Objects.requireNonNull(ram);
        Preconditions.checkBits16(startAddress);
        Preconditions.checkBits16(endAddress);
        Preconditions.checkArgument(startAddress <= endAddress && endAddress - startAddress <= ram.size());
        this.ram = ram;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
    }
    
    public RamController(Ram ram, int startAddress) {
        this(ram, startAddress, startAddress + ram.size());
    }
    
    public int read(int address) {
        Preconditions.checkBits16(address);
        if (!isAccessible(address))
            return NO_DATA;
        return ram.read(address-startAddress);
    }

    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);
        if (isAccessible(address))
            ram.write(address-startAddress, data);  
    }
    
    /**
     * Vérifie que l'adresse donnée est accessible (valide) pour l'objet en question.
     * @param address : adresse à vérifier.
     * @return True si l'adresse est valide, False sinon.
     */
    private boolean isAccessible(int address) {
        if (address < startAddress || address >= endAddress)
            return false;
        return true;
    }

}
