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
    
    private static final int FIRST_ACTIVE_LINE_INDEX = 4;

    private final Cpu cpu;
    private int line0;
    private int line1;
    private int activeLines;

    /**
     * Représente une touche (un bouton)
     * @author Riand Andre
     * @author Michel François
     */
    public enum Key implements Bit {
        RIGHT, LEFT, UP, DOWN, A, B, SELECT, START;
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
            return Bits.complement8(getP1());
        }
        return NO_DATA;
    }

    /**
     * @throws IllegalArgumentException si address ne peut pas s'écrire avec 16 bits ou data ne peut pas s'écrire avec 8 bits.
     */
    @Override
    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);
        if (address == AddressMap.REG_P1) {
            activeLines = Bits.extract(Bits.complement8(data), FIRST_ACTIVE_LINE_INDEX, 2);
        }
    }

    /**
     * Simule la pression d'une touche
     * @param K : touche concernée
     */
    public void keyPressed(Key K) {
        final int oldP1 = getP1();
        keyAction(K, true);
        if (oldP1 != getP1())
            cpu.requestInterrupt(Interrupt.JOYPAD);
    }

    /**
     * Simule le relâchement d'une touche
     * @param K : touche concernée
     */
    public void keyReleased(Key K) {
        keyAction(K, false);
    }

    private void keyAction(Key K, boolean newValue) {
        final int firstPart = Key.values().length / 2;
        if (K.index() < firstPart)
            line0 = Bits.set(line0, K.index(), newValue);
        else
            line1 = Bits.set(line1, K.index() % firstPart, newValue);
    }

    private int getP1() {
        return activeLines << FIRST_ACTIVE_LINE_INDEX | stateBits();
    }
    
    private int stateBits() {
        final int activeLine0 = Bits.test(activeLines, 0) ? line0 : 0;
        final int activeLine1 = Bits.test(activeLines, 1) ? line1 : 0;
        return activeLine0 | activeLine1;
    }
}
