package ch.epfl.gameboj;

import java.io.File;
import java.io.IOException;

import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cartridge.Cartridge;
import ch.epfl.gameboj.component.cpu.Cpu;

public final class DebugMain {
    public static void main(String[] args) throws IOException {
        for (int i = 1; i < args.length; i++) {
            File romFile = new File(args[i]);
            long cycles = Long.parseLong(args[0]);

            GameBoy gb = new GameBoy(Cartridge.ofFile(romFile));
            Component printer = new DebugPrintComponent();
            printer.attachTo(gb.bus());
            while (gb.cycles() < cycles) {
                long nextCycles = Math.min(gb.cycles() + 17556, cycles);
                gb.runUntil(nextCycles);
                gb.cpu().requestInterrupt(Cpu.Interrupt.VBLANK);
            }
        }
    }
}