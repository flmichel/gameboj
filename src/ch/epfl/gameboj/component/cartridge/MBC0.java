package ch.epfl.gameboj.component.cartridge;

import java.util.Objects;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Rom;

/**
 * Représente un contrôleur de banque mémoire de type 0, c'est-à-dire doté uniquement d'une mémoire morte de 32 768 octets.
 * @author Riand Andre
 * @author Michel François
 */
public final class MBC0 implements Component {

    private final Rom rom;

    private static final int ROM_SIZE = 0x8000; //taille de la mémoire morte

    /**
     * Construit un contrôleur de type 0 pour la mémoire donnée.
     * @param rom mémoire donnée pour le contrôleur
     * @throws NullPointerException si la mémoire est nulle.
     * @throws IllegalArgumentException si la mémoire ne contient pas exactement 32 768 octets.
     */
    public MBC0(Rom rom) {
        Objects.requireNonNull(rom);
        Preconditions.checkArgument(rom.size() == ROM_SIZE);
        this.rom = rom;
    }

    /**
     * @throws IllegalArgumentException si address ne peut pas s'écrire avec 16 bits.
     */
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
