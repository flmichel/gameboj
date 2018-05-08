package ch.epfl.gameboj.component.cartridge;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.memory.Rom;

/**
 * Une cartouche
 * @author Riand Andre
 * @author Michel François;
 */
public final class Cartridge implements Component {
    private Component bankController;
    
    private static Map<Integer, Integer> ramSizeMap = Map.of(0, 0, 1, 2048, 2, 8192, 3, 32768);

    private Cartridge(Component bankController) {
        this.bankController = bankController;

    }

    /**
     * Retourne une cartouche dont la mémoire morte contient les octets du fichier donné. 
     * @param romFile fichier à lire.
     * @return Une cartouche tel que décrit ci-dessus.
     * @throws IOException en cas d'erreur d'entrée-sortie, y compris si le fichier donné n'existe pas.
     * @throws IllegalArgumentException si le fichier en question ne contient pas 0 à la position 147 (en base 16).
     */
    public static Cartridge ofFile(File romFile) throws IOException {
        try(InputStream stream = new BufferedInputStream(new FileInputStream(romFile))) {
            byte[] data = stream.readAllBytes();
            
            int cartridgeType = data[0x147];
            Preconditions.checkArgument(cartridgeType >= 0 && cartridgeType < 4); //0x147 correspond au type de la cartouche.
            Component bc;
            if (cartridgeType > 0) {
                int ramSize = 0;
                if (cartridgeType == 3) {
                    int ramType = data[0x149];
                    Preconditions.checkArgument(ramType >= 0 && ramType < 4);
                    ramSize = ramSizeMap.get(ramType);
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
