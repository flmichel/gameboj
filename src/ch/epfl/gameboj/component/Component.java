package ch.epfl.gameboj.component;

import ch.epfl.gameboj.Bus;

public interface Component {

    public static final int NO_DATA = 0x100;
    
    int read(int address);
    
    void write(int address, int data);
    
    /**
     * Attache le composant au bus donné.
     * @param bus : Bus auquel il faut attacher le composant.
     */   
    default void attachTo(Bus bus) {
        bus.attach(this);
    }
    
}
