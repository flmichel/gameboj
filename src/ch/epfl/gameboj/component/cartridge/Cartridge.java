package ch.epfl.gameboj.component.cartridge;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Rom;

/**
 * Représente une cartouche
 * @author Riand Andre
 * @author Michel François
 */
public final class Cartridge implements Component {
    private final Component bankController;
    private static final int NB_RAM_TYPE = 4;

    
    private static final int SOFTWARE_TYPE = 0x147; //Position de l’octet qui donne le type de MBC dans l’en-tête de la cartouche
    private static final int EXTERNAL_RAM_SIZE_TYPE = 0x149;
    private static final int[] ramSizeMap = {0, 2048, 8192, 32768};

    private Cartridge(Component bankController) {
        this.bankController = bankController;
    }

    /**
     * Retourne une cartouche dont la mémoire morte contient les octets du fichier donné. 
     * @param romFile fichier à lire.
     * @return Une cartouche tel que décrit ci-dessus.
     * @throws IOException en cas d'erreur d'entrée-sortie, y compris si le fichier donné n'existe pas.
     * @throws IllegalArgumentException si le fichier en question ne contient pas une valeur de 0 à 3 à la position 147 (en base 16).
     * @throws IllegalArgumentException si le fichier en question est de type 3 et ne contient pas une valeur de 0 à 3 à la position 149 (en base 16).
     */
    public static Cartridge ofFile(File romFile) throws IOException {
        try(InputStream stream = new BufferedInputStream(new FileInputStream(romFile))) {
            final byte[] data = stream.readAllBytes();

            final int cartridgeType = data[SOFTWARE_TYPE];
            Preconditions.checkArgument(cartridgeType >= 0 && cartridgeType < NB_RAM_TYPE);
            final Component bc;
            if (cartridgeType > 0) {
                int ramSize = 0;
                if (cartridgeType == 3) {
                    final int ramType = data[EXTERNAL_RAM_SIZE_TYPE];
                    Preconditions.checkArgument(ramType >= 0 && ramType < NB_RAM_TYPE);
                    ramSize = ramSizeMap[ramType];
                }
                bc = new MBC1(new Rom(data), ramSize);
            }
            else {
                bc = new MBC0(new Rom(data));
            }
            return new Cartridge(bc);
        }
    }

    /**
     * @throws IllegalArgumentException si address ne peut pas s'écrire avec 16 bits.
     */
    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);
        return bankController.read(address);
    }

    /**
     * @throws IllegalArgumentException si address ne peut pas s'écrire avec 16 bits ou data ne peut pas s'écrire avec 8 bits.
     */
    @Override
    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);
        bankController.write(address, data);
    }

}
