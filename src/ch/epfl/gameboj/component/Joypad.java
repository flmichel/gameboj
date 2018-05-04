package ch.epfl.gameboj.component;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.cpu.Cpu.Interrupt;

/**
 * Représente un clavier
 * @author Riand Andre
 * @author Michel François
 */
public class Joypad implements Component {

    private Cpu cpu;
    private int line0;
    private int line1;

    public enum Key implements Bit{
        RIGHT, LEFT, UP, DOWN, A, B, SELECT, START;
    }
    
    private enum Tab implements Bit {
        COL0, COL1, COL2, COL3, ROW0, ROW1;
    }

    /**
     * Construit un clavier associé à un processeur (cpu)
     * @param cpu : processeur du Game Boy auquel appartient ce clavier
     */
    public Joypad(Cpu cpu) {
        this.cpu = cpu;
    }


    /**
     * @throws IllegalArgumentException si address ne peut pas s'écrire avec 16 bits.
     */
    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);
        if (address == AddressMap.REG_P1) {
            return 0; //combin line0 et line1
        } else return NO_DATA;
    }

    /**
     * @throws IllegalArgumentException si address ne peut pas s'écrire avec 16 bits ou data ne peut pas s'écrire avec 8 bits.
     */
    @Override
    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);

        if (address == AddressMap.REG_P1) {

        }
    }

    /**
     * Simule la pression d'une touche
     * @param K : touche concernée
     */
    public void keyPressed(Key K) {        
        int line = K.index() < 4 ? line0 : line1;
        keyAction(line, K, false);
        cpu.requestInterrupt(Interrupt.JOYPAD);
    }

    /**
     * Simule le relâchement d'une touche
     * @param K : touche concernée
     */
    public void keyReleased(Key K) {
        int line = K.index() < 4 ? line0 : line1;
        keyAction(line, K, true);
    }

    private void keyAction(int line, Key K, boolean newValue) {
        Bits.set(line, K.index() % 4, newValue);
    }
}
