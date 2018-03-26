package ch.epfl.gameboj;

import java.util.Objects;

import ch.epfl.gameboj.component.Timer;
import ch.epfl.gameboj.component.cartridge.Cartridge;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.memory.BootRomController;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;

public class GameBoy {

    private Bus bus;
    private Ram workRam;
    private Cpu cpu;
    private long cyclesNb = 0;
    private Timer minuteur;
    private BootRomController brc;

    public GameBoy(Cartridge cartridge) {
        Objects.requireNonNull(cartridge);
        workRam = new Ram(AddressMap.WORK_RAM_SIZE);
        cpu = new Cpu();
        bus = new Bus();
        bus.attach(new RamController(workRam, AddressMap.WORK_RAM_START));
        bus.attach(new RamController(workRam, AddressMap.ECHO_RAM_START, AddressMap.ECHO_RAM_END));
        cpu.attachTo(bus);
        brc = new BootRomController (cartridge);
        bus.attach(brc);
        minuteur = new Timer (cpu) ;
        bus.attach(minuteur);
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
    
    public Timer timer() {
        return minuteur;
    }

    public void runUntil(long cycle) {
       Preconditions.checkArgument(cycles() <= cycle);        
       
       if (cycles() < cycle) {
            for (long i = cycles() ; i < cycle ; i++) {
                minuteur.cycle(i);
                cpu.cycle(i);
                cyclesNb++;
            }
        }
    }

}
