package ch.epfl.gameboj;

import ch.epfl.gameboj.component.cartridge.Cartridge;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;

import java.util.Objects;

public class GameBoy {

    private Bus bus;
    private Ram workRam;
    private Cpu cpu;
    private long cyclesNb = 0;

    public GameBoy(Cartridge cartridge) {
        Objects.requireNonNull(cartridge);
        workRam = new Ram(AddressMap.WORK_RAM_SIZE);
        cpu = new Cpu();
        bus = new Bus();
        bus.attach(new RamController(workRam, AddressMap.WORK_RAM_START));
        bus.attach(new RamController(workRam, AddressMap.ECHO_RAM_START, AddressMap.ECHO_RAM_END));
        cpu.attachTo(bus);
//        BootRomController brc = new BootRomController (cartridge);
//        bus.attach(brc);
//        Timer minuteur = new Timer () ;
//        bus.attach(minuteur);
    }

    public Bus bus() {
        return bus;
    }

    public Cpu cpu() {
        return cpu;
    }

    public long cycles() {
        return cyclesNb;
    }

    public void runUntil(long cycle) {
       Preconditions.checkArgument(cycles() <= cycle);        
       
       if (cycles() < cycle) {
            for (long i = cycles() ; i < cycle ; i++) {
                //timer.cycle(i);
                cpu.cycle(i);
                cyclesNb++;
            }
        }
    }

}
