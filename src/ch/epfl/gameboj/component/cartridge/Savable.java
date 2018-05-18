package ch.epfl.gameboj.component.cartridge;

public interface Savable {

    abstract byte[] save(); 
    abstract void load(byte[] file);
    
}
