package ch.epfl.gameboj.component.cartridge;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Rom;

public final class Cartridge implements Component {
    private Component bankController;
    
    private Cartridge(Component bankController) {
        this.bankController = bankController;
        
    }
    
    public Cartridge ofFile(File romFile) throws IOException {
        try(InputStream stream = new BufferedInputStream(new FileInputStream(romFile));) {
        if (stream.readAllBytes()[0x147] != 0)
            throw new IllegalArgumentException();
        byte[] data = stream.readAllBytes();
        MBC0 bankController = new MBC0(new Rom(data));
        return new Cartridge(bankController);
        }
    }
    
    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);
        return bankController.read(address);

    }

    @Override
    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);
        bankController.write(address, data);
    }

}
