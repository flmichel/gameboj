package ch.epfl.gameboj;

import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;

public class GameBoy {
    
    private Bus bus;
    private Ram workRam;

    public GameBoy(Object cartridge) {
        workRam = new Ram(AddressMap.WORK_RAM_SIZE);
        bus.attach(new RamController(workRam, AddressMap.WORK_RAM_START));
        bus.attach(new RamController(workRam, AddressMap.ECHO_RAM_START, AddressMap.ECHO_RAM_END));
    }
    
    public Bus bus() {
        return bus;
    }
}
