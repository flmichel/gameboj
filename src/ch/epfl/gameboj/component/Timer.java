package ch.epfl.gameboj.component;

import java.util.Objects;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.cpu.Cpu.Interrupt;

public final class Timer implements Component, Clocked {

    private Cpu cpu;
    private int mainCounter;
    private int TIMA;
    private int TMA;
    private int TAC;
    private static final int BITS8_MAX_VALUE = 0xFF;

    public Timer (Cpu cpu) {
        Objects.requireNonNull(cpu);
        this.cpu = cpu;
    }

    @Override
    public void cycle(long cycle) {
        boolean status = state();
        mainCounter = Bits.clip(16, mainCounter + 4);
        incIfChange(status);
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
            boolean status = state();
            mainCounter = data;
            incIfChange(status);
        } break;

        case AddressMap.REG_TIMA: {          
            TIMA = data;
        } break;

        case AddressMap.REG_TMA: {
            TMA = data;
        } break;

        case AddressMap.REG_TAC: {
            boolean status = state();
            TAC = data;
            incIfChange(status);
        } break;
        default : {} //ne fait rien si l addresse n'est pas une des listées ci-dessus;  
        }
    }

    private int bitToUse () {
        int r;

        switch (Bits.extract(TAC, 0, 2)) {

        case 0b00 : r = 9; break;
        case 0b01 : r = 3; break;
        case 0b10 : r = 5; break;
        default : r = 7;

        }
        return r;
    }

    private boolean state() {       
        return Bits.test(TAC, 2) && Bits.test(mainCounter, bitToUse()) ;
    }

    private void incIfChange(boolean b) {
        if (b && !state()) {
            if (TIMA != BITS8_MAX_VALUE) TIMA++;
            else {
                cpu.requestInterrupt(Interrupt.TIMER);
                TIMA = TMA;
            }
        }
    }
}

