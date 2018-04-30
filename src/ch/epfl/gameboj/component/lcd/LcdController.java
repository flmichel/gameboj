package ch.epfl.gameboj.component.lcd;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.Register;
import ch.epfl.gameboj.RegisterFile;
import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.Clocked;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.LcdImage;

import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.cpu.Cpu.Interrupt;
import ch.epfl.gameboj.component.memory.Ram;
/**
 * Représente un contrôleur LCD
 * @author Riand Andre
 * @author Michel François
 */
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
    public static final int ENTER_MODE0 = 63;
    public static final int BACKGROUND_IMAGE_SIZE = 256;
    public static final int TILE_SIZE_IN_MEMORY = 16;


    private long nextNonIdleCycle = 0;

    private final Cpu cpu;
    private Ram videoRam = new Ram(AddressMap.VIDEO_RAM_SIZE);
    
    private LcdImage.Builder nextImageBuilder;
    private LcdImage currentImage;
    private boolean firstLine = true;

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

    /**
     * Construit un contrôleur LCD associé à un processeur (cpu)
     * @param cpu : processeur du Game Boy auquel appartient ce contrôleur LCD
     */
    public LcdController(Cpu cpu) {
        this.cpu = cpu;
    }
    
    public LcdImage currentImage() {
        return currentImage;
    }

    @Override
    public void cycle(long cycle) {
        if ((nextNonIdleCycle == Long.MAX_VALUE) && registerFile.testBit(Reg.LCDC, RegLCDC.LCD_STATUS)) {
            nextNonIdleCycle = cycle;
        }       
        if (cycle == nextNonIdleCycle) {
            reallyCycle(cycle);
        }
    }

    private void reallyCycle(long cycle) {

        int r = (int) cycle % NB_CYCLES_LINE;
        int lineIndex = ((int) cycle % NB_CYCLES_LCD) / NB_CYCLES_LINE;
        if (lineIndex < LCD_HEIGHT) {
            switch (r) {    
                case ENTER_MODE2 : {
                    if (firstLine) {
                        nextImageBuilder = new LcdImage.Builder(LCD_WIDTH, LCD_HEIGHT);
                        firstLine = false;
                    }
                    setMode(2);             
                    needInterrupt(RegSTAT.INT_MODE2);
                    LycEqLyAndSetLy(lineIndex);
                    nextNonIdleCycle += NB_CYCLES_MODE2;  
                } break;
    
                case ENTER_MODE3 : {
                    LcdImageLine line = computeLine(lineIndex);
                    nextImageBuilder.setLine(lineIndex, line);
                    setMode(3);
                    nextNonIdleCycle += NB_CYCLES_MODE3;  
                } break;
    
                case ENTER_MODE0 : {
                    setMode(0);
                    needInterrupt(RegSTAT.INT_MODE0);
                    nextNonIdleCycle += NB_CYCLES_MODE0;  
                } break;
            }
        } else {
            currentImage = nextImageBuilder.build();
            firstLine = true;
            LycEqLyAndSetLy(lineIndex);
            setMode(1);
            cpu.requestInterrupt(Interrupt.VBLANK);
            needInterrupt(RegSTAT.INT_MODE1);
            nextNonIdleCycle += NB_CYCLES_MODE1; 
        }
    }


    /**
     * @throws IllegalArgumentException si address ne peut pas s'écrire avec 16 bits.
     */
    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);
        if (address >= AddressMap.VIDEO_RAM_START && address < AddressMap.VIDEO_RAM_END)
            return videoRam.read(address - AddressMap.VIDEO_RAM_START);
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
        if (address >= AddressMap.VIDEO_RAM_START && address < AddressMap.VIDEO_RAM_END) {
            videoRam.write(address - AddressMap.VIDEO_RAM_START, data);
            //System.out.println("data : " + Integer.toBinaryString(data) + " at address : " + Integer.toHexString(address));
        }
        if (address >= AddressMap.REGS_LCDC_START && address < AddressMap.REGS_LCDC_END) {
            Reg reg = Reg.values()[address - AddressMap.REGS_LCDC_START];
            if (reg == Reg.STAT) {
                final int maskReadOnly = RegSTAT.MODE0.mask() | RegSTAT.MODE1.mask() | RegSTAT.LYC_EQ_LY.mask();
                final int mask = ~maskReadOnly;
                data &= mask;
            }
            registerFile.set(reg, data);
            if (reg == Reg.LYC)
                updateStateLycEqLy();            
            if (reg == Reg.LCDC && !Bits.test(data, RegLCDC.LCD_STATUS.index())) {
                setMode(0);
                registerFile.set(Reg.LY, 0);
                nextNonIdleCycle = Long.MAX_VALUE;
            }
        }
    }

    private void updateStateLycEqLy() {
        final Boolean lycEqLy = registerFile.get(Reg.LY) == registerFile.get(Reg.LYC);
        registerFile.setBit(Reg.STAT, RegSTAT.LYC_EQ_LY, lycEqLy);
        if (registerFile.testBit(Reg.STAT, RegSTAT.INT_LYC) && lycEqLy) {
            cpu.requestInterrupt(Interrupt.LCD_STAT);
        }
    }

    private void LycEqLyAndSetLy(int line) {
        registerFile.set(Reg.LY, line);
        updateStateLycEqLy();
    }

    private void setMode(int i) {
        Preconditions.checkArgument(i >= 0 && i < 4);
        registerFile.setBit(Reg.STAT, RegSTAT.MODE0, Bits.test(i, 0));
        registerFile.setBit(Reg.STAT, RegSTAT.MODE1, Bits.test(i, 1));
    }

    private void needInterrupt(RegSTAT mode) {
        if (mode.index() >= RegSTAT.INT_MODE0.index() && mode.index() <= RegSTAT.INT_MODE2.index()) {
            if (registerFile.testBit(Reg.STAT, mode)) {
                cpu.requestInterrupt(Interrupt.LCD_STAT);
            }
        }
    }
    
    private LcdImageLine computeLine(int indexLine) {
        LcdImageLine.Builder lineBuilder = new LcdImageLine.Builder(BACKGROUND_IMAGE_SIZE);
        final int tileSourceIndex = registerFile.testBit(Reg.LCDC, RegLCDC.TILE_SOURCE) ? 1 : 0;
        final int versionUsed = registerFile.testBit(Reg.LCDC, RegLCDC.BG_AREA) ? 1 : 0;
        final int Scx = registerFile.get(Reg.SCX);
        final int Scy = registerFile.get(Reg.SCY);
        final int indexY = (indexLine + Scy) % BACKGROUND_IMAGE_SIZE;
        
        final int tileIndexY = indexY / 8;
        final int tileLineIndex = indexY % 8; // NOMBREs MAGIQUE
        
        for (int i = 0; i < Integer.SIZE; i++) {
            int numberOfTheTile = read(AddressMap.BG_DISPLAY_DATA[versionUsed] + tileIndexY * Integer.SIZE + i);
            final int address;
            if (numberOfTheTile >= 0x80)
                address = AddressMap.TILE_SOURCE[1] + (numberOfTheTile * TILE_SIZE_IN_MEMORY + tileLineIndex * 2);
            else if (tileSourceIndex == 0) {
                address = 0x9000 + numberOfTheTile * TILE_SIZE_IN_MEMORY + tileLineIndex * 2;
            } else {
                address = AddressMap.TILE_SOURCE[1] + numberOfTheTile * TILE_SIZE_IN_MEMORY + tileLineIndex * 2;
            }
                
            //System.out.println(Integer.toHexString(indexY));
            
            int lsb = read(address);
            int msb = read(address + 1);
            lineBuilder.setBytes(i, msb, lsb);
        }
        LcdImageLine line = lineBuilder.build();
        line = line.extractWrapped(Scx, LCD_WIDTH);
        return line;
    }
}
