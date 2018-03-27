package ch.epfl.gameboj.component.memory;

import java.util.Objects;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cartridge.Cartridge;

public final class BootRomController implements Component {

    Cartridge cartridge;
    boolean bootRomDisabled = false;
    Rom bootRom = new Rom(BootRom.DATA);

    public BootRomController(Cartridge cartridge) {
        Objects.requireNonNull(cartridge);
        this.cartridge = cartridge;
    }

    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);
        if (!bootRomDisabled && address >= AddressMap.BOOT_ROM_START && address < AddressMap.BOOT_ROM_END) {
            return bootRom.read(address - AddressMap.BOOT_ROM_START);
        } else {
            return cartridge.read(address);
        }
    }

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
