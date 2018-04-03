package ch.epfl.gameboj.component.memory;

import java.util.Objects;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cartridge.Cartridge;

/**
 * Un contrôleur de la mémoire morte de démarrage. 
 * @author Riand Andre
 * @author Michel François
 */
public final class BootRomController implements Component {

    private Cartridge cartridge;
    private boolean bootRomDisabled = false;
    private Rom bootRom = new Rom(BootRom.DATA);

    /**
     * Construit un contrôleur de mémoire de démarrage auquel la cartouche donnée est attachée.
     * @param cartridge cartouche à attacher.
     * @throws NullPointerException si la cartouche est nulle.
     */
    public BootRomController(Cartridge cartridge) {
        Objects.requireNonNull(cartridge);
        this.cartridge = cartridge;
    }

    /**
     * @throws IllegalArgumentException si address ne peut pas s'écrire avec 16 bits.
     */
    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);
        if (!bootRomDisabled && address >= AddressMap.BOOT_ROM_START && address < AddressMap.BOOT_ROM_END) {
            return bootRom.read(address - AddressMap.BOOT_ROM_START);
        } else {
            return cartridge.read(address);
        }
    }

    /**
     * @throws IllegalArgumentException si address ne peut pas s'écrire avec 16 bits ou data ne peut pas s'écrire avec 8 bits.
     */
    @Override
    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);
        if (!bootRomDisabled && address == AddressMap.REG_BOOT_ROM_DISABLE) {
            bootRomDisabled = true;
        }
        cartridge.write(address, data);
    }

}
