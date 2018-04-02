package ch.epfl.gameboj.component.cartridge;

import java.util.Objects;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Rom;

public final class MBC0 implements Component {
    
    private Rom rom;
    
    private static final int ROM_SIZE = 0x8000; //taille de la mémoire morte

    public MBC0(Rom rom) {
        Objects.requireNonNull(rom);
        Preconditions.checkArgument(rom.size() == ROM_SIZE);
        this.rom = rom;
    }

    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);
        if (address < rom.size())
            return rom.read(address);
        else
            return NO_DATA;
    }

    @Override
    public void write(int address, int data) {}
}
