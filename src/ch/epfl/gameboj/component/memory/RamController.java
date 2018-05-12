package ch.epfl.gameboj.component.memory;

import ch.epfl.gameboj.component.Component;

import java.util.Objects;

import ch.epfl.gameboj.Preconditions;

/**
 * Un composant contrôlant l'accès à une mémoire vive.
 * @author Riand Andre
 * @author Michel François
 */
public final class RamController implements Component {

    private final Ram ram;
    private final int startAddress;
    private final int endAddress;

    /**
     * Construit un contrôleur pour la mémoire vive donnée, accessible entre l'adresse startAddress (inclue) et endAddress (exclue).
     * @param ram mémoire vive
     * @param startAddress adresse initiale
     * @param endAddress adresse de fin
     * @throws NullPointerException si la mémoire donnée est nulle
     * @throws IllegalArgumentException si l'une des deux adresses n'est pas une valeur 16 bits, ou si l'intervalle qu'elles décrivent a une taille négative ou supérieure à celle de la mémoire
     */
    public RamController(Ram ram, int startAddress, int endAddress) {
        Objects.requireNonNull(ram);
        Preconditions.checkBits16(startAddress);
        Preconditions.checkBits16(endAddress);
        Preconditions.checkArgument(startAddress <= endAddress && endAddress - startAddress <= ram.size());
        this.ram = ram;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
    }

    /**
     * Appelle le premier constructeur en lui passant une adresse de fin telle que la totalité de la mémoire vive soit accessible au travers du contrôleur.
     * @param ram mémoire vive
     * @param startAddress adresse initiale
     */
    public RamController(Ram ram, int startAddress) {
        this(ram, startAddress, startAddress + ram.size());
    }

    /**
     * @throws IllegalArgumentException si address ne peut pas s'écrire avec 16 bits.
     */
    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);
        if (!isAccessible(address))
            return NO_DATA;
        return ram.read(address-startAddress);
    }

    /**
     * @throws IllegalArgumentException si address ne peut pas s'écrire avec 16 bits ou data ne peut pas s'écrire avec 8 bits.
     */
    @Override
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
        return !(address < startAddress || address >= endAddress);
    }

}
