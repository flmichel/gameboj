package ch.epfl.gameboj.component.lcd;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.Register;
import ch.epfl.gameboj.RegisterFile;
import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.Clocked;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.cpu.Cpu.Interrupt;
import ch.epfl.gameboj.component.memory.Ram;

public class LcdController implements Component, Clocked {

    public static final int LCD_WIDTH = 160;
    public static final int LCD_HEIGHT = 144;
    public static final int NB_CYCLES_LINE = 114;
    public static final int NB_CYCLES_MODE0 = 51;
    public static final int NB_CYCLES_MODE1 = 1140;
    public static final int NB_CYCLES_MODE2 = 20;
    public static final int NB_CYCLES_MODE3 = 43;
    public static final int NB_CYCLES_LCD = 17556;
    public static final int ENTER_MODE2 = 0;
    public static final int ENTER_MODE3 = 20;
    public static final int ENTER_MODE0 = 43;


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
            reallyCycle(cycle);
        }       
    }

    private void reallyCycle(long cycle) {
        if(registerFile.testBit(Reg.LCDC, RegLCDC.LCD_STATUS)) {

            int r = (int) cycle % NB_CYCLES_LINE;
            int line = ((int) cycle % NB_CYCLES_LCD) / NB_CYCLES_LINE; //division entiere

            if (line < LCD_HEIGHT) {
                switch (r) {

                case ENTER_MODE2 : {
                    registerFile.setBit(Reg.STAT, RegSTAT.MODE0, true);
                    registerFile.setBit(Reg.STAT, RegSTAT.MODE1, false);
                    
                    if(registerFile.testBit(Reg.STAT, RegSTAT.INT_MODE2)) {
                        cpu.requestInterrupt(Interrupt.LCD_STAT);
                    }
                    registerFile.set(Reg.LY, line+1);
                    if (registerFile.get(Reg.LY) == registerFile.get(Reg.LYC)) {
                        registerFile.setBit(Reg.STAT, RegSTAT.LYC_EQ_LY, true);
                        if(registerFile.testBit(Reg.STAT, RegSTAT.INT_LYC)) {
                            cpu.requestInterrupt(Interrupt.LCD_STAT);
                        }
                    } else {
                        registerFile.setBit(Reg.STAT, RegSTAT.LYC_EQ_LY, false);
                    }
                    nextNonIdleCycle += NB_CYCLES_MODE2;  
                } break;

                case ENTER_MODE3 : {
                    registerFile.setBit(Reg.STAT, RegSTAT.MODE0, true);
                    registerFile.setBit(Reg.STAT, RegSTAT.MODE1, true);
                    //dessinera la ligne ici
                    nextNonIdleCycle += NB_CYCLES_MODE3;  
                } break;

                case ENTER_MODE0 : {
                    registerFile.setBit(Reg.STAT, RegSTAT.MODE0, false);
                    registerFile.setBit(Reg.STAT, RegSTAT.MODE1, false);
                    if(registerFile.testBit(Reg.STAT, RegSTAT.INT_MODE0)) {
                        cpu.requestInterrupt(Interrupt.LCD_STAT);
                    }
                    nextNonIdleCycle += NB_CYCLES_MODE0;  
                } break;
                }
            } else {
                registerFile.setBit(Reg.STAT, RegSTAT.MODE0, false);
                registerFile.setBit(Reg.STAT, RegSTAT.MODE1, true);
                cpu.requestInterrupt(Interrupt.VBLANK);
                if(registerFile.testBit(Reg.STAT, RegSTAT.INT_MODE1)) {
                    cpu.requestInterrupt(Interrupt.LCD_STAT);
                }
                nextNonIdleCycle += NB_CYCLES_MODE1; 
            }
        }
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
        if (address >= AddressMap.REGS_LCDC_START && address < AddressMap.REGS_LCDC_END) {
            Reg reg = Reg.values()[address - AddressMap.REGS_LCDC_START];
            if (reg == Reg.STAT) {
                final int maskReadOnly = RegSTAT.MODE0.mask() | RegSTAT.MODE1.mask() | RegSTAT.LYC_EQ_LY.mask();
                final int mask = ~maskReadOnly;
                data &= mask;
            }
            registerFile.set(reg , data);
            if (reg == Reg.LY || reg == Reg.LY) {
                updateStatelycEqLy();
            }
            if (reg == Reg.LCDC && !Bits.test(data, RegLCDC.LCD_STATUS.index())) {
                registerFile.setBit(Reg.STAT, RegSTAT.MODE0, false);
                registerFile.setBit(Reg.STAT, RegSTAT.MODE1, false);
                registerFile.set(Reg.LY, 0);
                nextNonIdleCycle = Long.MAX_VALUE;
            }
        }
    }

    private void updateStatelycEqLy() {
        final Boolean lycEqLy = registerFile.get(Reg.LY) == registerFile.get(Reg.LYC);
        registerFile.setBit(Reg.STAT, RegSTAT.LYC_EQ_LY, lycEqLy);
        if (registerFile.testBit(Reg.STAT, RegSTAT.INT_LYC) && lycEqLy) {
            cpu.requestInterrupt(Interrupt.LCD_STAT);
        }
    }
}
