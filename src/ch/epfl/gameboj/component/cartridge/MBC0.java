package ch.epfl.gameboj.component.cartridge;

import java.util.Objects;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Rom;

public final class MBC0 implements Component {
    
    private Rom rom;

    public MBC0(Rom rom) {
        if (Objects.isNull(rom))
            throw new NullPointerException();
        if (rom.size() != 32768)            
            throw new IllegalArgumentException();
        else
            this.rom = rom;
    }

    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);
        if (address >= 0 && address > rom.size())
            return rom.read(address);
        else
            return NO_DATA;
    }

    @Override
    public void write(int address, int data) {}
}
