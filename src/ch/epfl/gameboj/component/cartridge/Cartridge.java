package ch.epfl.gameboj.component.cartridge;

import java.io.File;

import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Rom;

public final class Cartridge implements Component {
    
    private MBC0 controller;
    private Rom rom;

    
    private Cartridge(MBC0 controller) {
        this.controller = controller;

    }
    
    @Override
    public int read(int address) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void write(int address, int data) {
        // TODO Auto-generated method stub
        
    }

}
