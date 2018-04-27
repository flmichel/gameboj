package ch.epfl.gameboj.component.lcd;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.Register;
import ch.epfl.gameboj.RegisterFile;
import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.component.Clocked;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.memory.Ram;

public class LcdController implements Component, Clocked {

    public static final int LCD_WIDTH = 160;
    public static final int LCD_HEIGHT = 144;

    private long nextNonIdleCycle = 0;
    private long lcdOnCycle;

    public final Cpu cpu;
    private Ram videoRam = new Ram(AddressMap.VIDEO_RAM_SIZE);

    private enum Reg implements Register {
        LCDC, STAT, SCY, SCX, LY, LYC, DMA, BGP, OBP0, OBP1, WY, WX;
    }

    private enum RegLCDC implements Bit {
        BG, OBJ, OBJ_SIZE, BG_AREA, TILE_SOURCE, WIN, WIN_AREA, LCD_STATUS;
    }

    private enum RegSTAT implements Bit {
        MODE0, MODE1, LYC_EQ_LY, INT_MODE0, INT_MODE1, INT_MODE2, INT_LYC, UNUSED;
    }

    RegisterFile<Reg> registerFile = new RegisterFile<>(Reg.values());    


    public LcdController(Cpu cpu) {
        this.cpu = cpu;
    }

    @Override
    public void cycle(long cycle) {
        if ((nextNonIdleCycle == Long.MAX_VALUE) && registerFile.testBit(Reg.LCDC, RegLCDC.LCD_STATUS)) {
            lcdOnCycle = cycle;
            nextNonIdleCycle = cycle;
        }       
        if (cycle == nextNonIdleCycle) {
            reallyCycle();
        }       
    }

    private void reallyCycle() {

    }

    /**
     * @throws IllegalArgumentException si address ne peut pas s'écrire avec 16 bits.
     */
    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);
        if (address >= AddressMap.VIDEO_RAM_START && address < AddressMap.VIDEO_RAM_END)
            return videoRam.read(address - AddressMap.HIGH_RAM_START);
        if (address >= AddressMap.REGS_LCDC_START && address < AddressMap.REGS_LCDC_END)
            return registerFile.get(Reg.values()[address - AddressMap.REGS_LCDC_START]);
        else
            return NO_DATA;
    }

    /**
     * @throws IllegalArgumentException si address ne peut pas s'écrire avec 16 bits ou data ne peut pas s'écrire avec 8 bits.
     */
    @Override
    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data); 
        if (address >= AddressMap.VIDEO_RAM_START && address < AddressMap.VIDEO_RAM_END)
            videoRam.write(address - AddressMap.HIGH_RAM_START, data);
        if (address >= AddressMap.REGS_LCDC_START && address < AddressMap.REGS_LCDC_END)
            registerFile.set(Reg.values()[address - AddressMap.REGS_LCDC_START], data);
    }

}
