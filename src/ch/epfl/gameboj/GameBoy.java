package ch.epfl.gameboj;

import java.util.Objects;

import ch.epfl.gameboj.component.Joypad;
import ch.epfl.gameboj.component.Timer;
import ch.epfl.gameboj.component.cartridge.Cartridge;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.lcd.LcdController;
import ch.epfl.gameboj.component.memory.BootRomController;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;

/**
 * Modélise un Game Boy, avec tous ces composants.
 * @author Riand Andre
 * @author Michel François
 */
public final class GameBoy {

    private final Bus bus;
    private final Ram workRam;
    private final Cpu cpu;
    private long cyclesNb = 0;
    private Timer minuteur;
    private LcdController lcdC;
    private Joypad joypad;

    /**
     * Nombre de cycles par secondes
     */
    public static final long NB_CYCLES_P_SECOND = (long) Math.pow(2, 20);

    /**
     * Nombre de cycles par nanosecondes
     */
    public static final double NB_CYCLES_P_NANOSECOND = (double) ((NB_CYCLES_P_SECOND) / 1e9);

    /**
     * Construit un Game Boy avec la cartouche (cartridge) donnée, en créant tous les composants nécessaires pour le bon fonctionnement de celui-ci (Processeur, Ram...) et les attachant à un bus commum.
     * @param cartridge la cartouche du Game Boy.
     * @throws NullPointerException si la cartouche passée en argument vaut null.
     */
    public GameBoy(Cartridge cartridge) {
        Objects.requireNonNull(cartridge);
        workRam = new Ram(AddressMap.WORK_RAM_SIZE);
        cpu = new Cpu();
        bus = new Bus();
        cpu.attachTo(bus);
        bus.attach(new RamController(workRam, AddressMap.WORK_RAM_START));
        bus.attach(new RamController(workRam, AddressMap.ECHO_RAM_START, AddressMap.ECHO_RAM_END));
        bus.attach(new BootRomController(cartridge));
        minuteur = new Timer(cpu) ;
        bus.attach(minuteur);
        lcdC = new LcdController(cpu);
        lcdC.attachTo(bus); 
        joypad = new Joypad(cpu);
        bus.attach(joypad);
    }

    /**
     * Retourne le bus du Game Boy.
     * @return le bus du Game Boy.
     */
    public Bus bus() {
        return bus;
    }

    /**
     * Retourne le processeur du Game Boy.
     * @return le processeur du Game Boy.
     */
    public Cpu cpu() {
        return cpu;
    }

    /**
     * Retourne le nombre de cycles déjà simulés.
     * @return le nombre de cycles déjà simulés.
     */
    public long cycles() {
        return cyclesNb;
    }

    /**
     * Retourne le minuteur du Game Boy.
     * @return le minuteur du Game Boy.
     */
    public Timer timer() {
        return minuteur;
    }

    /**
     * Retourne le controleur LCD du Game Boy
     * @return le controleur LCD du Game Boy
     */
    public LcdController lcdController() {
        return lcdC;
    }

    /**
     * Retourne le clavier du Game Boy
     * @return le clavier du Game Boy
     */
    public Joypad joypad() {
        return joypad;
    }

    /**
     * Simule le fonctionnement du GameBoy jusqu'au cycle donné moins 1.
     * @param cycle : la méthode simule jusqu'à ce nombre représentant un cycle.
     * @throws IllegalArgumentException si un nombre (strictement) supérieur de cycles a déjà été simulé.
     */
    public void runUntil(long cycle) {
        Preconditions.checkArgument(cycles() <= cycle);        

        while (cycles() < cycle) {
            minuteur.cycle(cycles());
            lcdC.cycle(cycles());
            cpu.cycle(cycles());
            cyclesNb++;        
        }
    }
}
