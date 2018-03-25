package ch.epfl.gameboj.component;

import java.util.Objects;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.cpu.Cpu;

public final class Timer implements Component, Clocked {

    private Cpu cpu;
    private int mainCounter;
    private int TIMA;
    private int TMA;
    private int TAC;

    public Timer (Cpu cpu) {
        Objects.requireNonNull(cpu);
        this.cpu = cpu;
    }

    @Override
    public void cycle(long cycle) {
        mainCounter++;
    }

    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);
        switch (address) {

        case AddressMap.REG_DIV: {
            return mainCounter;
        } 

        case AddressMap.REG_TIMA: {
            return TIMA;
        }

        case AddressMap.REG_TMA: {
            return TMA;
        }

        case AddressMap.REG_TAC: {
            return TAC;
        } 

        default: 
            return NO_DATA;               
        }
    }

    @Override
    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);

        switch (address) {

        case AddressMap.REG_DIV: {
            //boolean status = state();
            mainCounter = data;
            //incIfChange(status);
        } 

        case AddressMap.REG_TIMA: {
            //boolean status = state();
            TIMA = data;
            //incIfChange(status);
        }

        case AddressMap.REG_TMA: {
            TMA = data;
        }

        case AddressMap.REG_TAC: {
            boolean status = state();
            TAC = data;
            incIfChange(status);
        } 
        // default : {} ??
        }
    }

    private boolean state() {       
        return false;
    }

    private void incIfChange(boolean b) {
        if (b && !state()) {
            TIMA++;
        }
    }

}
