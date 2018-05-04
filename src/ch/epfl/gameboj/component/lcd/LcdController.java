package ch.epfl.gameboj.component.lcd;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Bus;
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
    public static final int NB_CYCLES_MODE2 = 20;
    public static final int NB_CYCLES_MODE3 = 43;
    public static final int NB_CYCLES_LCD = 17556;
    public static final int ENTER_MODE2 = 0;
    public static final int ENTER_MODE3 = 20;
    public static final int ENTER_MODE0 = 63;

    public static final int IMAGE_SIZE = 256;
    public static final int TILE_SIZE_IN_MEMORY = 16;
    public static final int NUMBER_OF_TILE_ACCESSIBLE = 256;
    public static final int PIXEL_PER_TILE_LINE = 8;
    public static final int NUMBER_OF_TILES_PER_LINE = 32;
    public static final int NUMBER_OF_TILES = NUMBER_OF_TILES_PER_LINE * NUMBER_OF_TILES_PER_LINE;
    public static final int WX_START = 7;
    public static final int WIN_MAX_SIZE = 160;


    private int winY;
    private long lcdOnCycle;
    private long nextNonIdleCycle = Long.MAX_VALUE;
    //private boolean dmaOn;
    private int sourceAddress;
    private int copyIndex = Integer.MAX_VALUE; //meilleure option ?

    private Bus bus;
    private final Cpu cpu;
    private Ram videoRam = new Ram(AddressMap.VIDEO_RAM_SIZE);
    private Ram oam = new Ram(AddressMap.OAM_RAM_SIZE);

    private LcdImage.Builder nextImageBuilder;
    private LcdImage currentImage;

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

    /**
     * Retourne l'image courante de l'écran
     * @return l'image courante de l'écran
     */
    public LcdImage currentImage() {
        if (currentImage == null) return new LcdImage.Builder(LCD_WIDTH, LCD_HEIGHT).build();
        return currentImage;
    }

    @Override
    public void cycle(long cycle) {
        if (nextNonIdleCycle == Long.MAX_VALUE && registerFile.testBit(Reg.LCDC, RegLCDC.LCD_STATUS)) {
            nextNonIdleCycle = cycle;
            lcdOnCycle = cycle;
        }       
        if (cycle == nextNonIdleCycle) {
            reallyCycle(cycle);
        }
//        if(copyIndex < oam.size()) {
//            // copie le prochain octet vers la mémoire d'attributs d'objets
//            oam.write(AddressMap.OAM_START + copyIndex, bus.read(sourceAddress));
//            copyIndex++;
//            sourceAddress++;
//        }
    }

    private void reallyCycle(long cycle) {
        int cycleInLine = (int) ((cycle - lcdOnCycle) % NB_CYCLES_LINE);
        int lineIndex = ((int) (cycle - lcdOnCycle) % NB_CYCLES_LCD) / NB_CYCLES_LINE;
        if (lineIndex < LCD_HEIGHT) {
            switch (cycleInLine) {    
            case ENTER_MODE2 : {
                if (lineIndex == 0) {
                    winY = 0;
                    nextImageBuilder = new LcdImage.Builder(LCD_WIDTH, LCD_HEIGHT);
                }
                setMode(2);             
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
                nextNonIdleCycle += NB_CYCLES_MODE0;  
            } break;
            }
        } else {
            if (lineIndex == LCD_HEIGHT) {
                currentImage = nextImageBuilder.build();
                setMode(1);
                cpu.requestInterrupt(Interrupt.VBLANK);
            }
            LycEqLyAndSetLy(lineIndex);
            nextNonIdleCycle += NB_CYCLES_LINE;
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
        if (address >= AddressMap.OAM_START && address < AddressMap.OAM_END) 
            return oam.read(address - AddressMap.OAM_START);
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
            if (reg == Reg.DMA) {
                copyIndex = 0;
                registerFile.set(Reg.DMA, data); //Nombre magique ?
                sourceAddress = data << 8;
            }
        }
    }

    /**
     * @throws NullPointerException si le bus est null.
     */
    @Override
    public void attachTo(Bus bus) {
        this.bus = bus;
        bus.attach(this);
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

    private void setMode(int mode) {
        Preconditions.checkArgument(mode >= 0 && mode < 4);
        if (mode < 3) {
            if (registerFile.testBit(Reg.STAT, RegSTAT.values()[RegSTAT.INT_MODE0.index() + mode]))
                cpu.requestInterrupt(Interrupt.LCD_STAT);
        }
        registerFile.setBit(Reg.STAT, RegSTAT.MODE0, Bits.test(mode, 0));
        registerFile.setBit(Reg.STAT, RegSTAT.MODE1, Bits.test(mode, 1));
    }

    private LcdImageLine computeLine(int indexLine) {

        LcdImageLine line = computeBgLine(indexLine);
        final int realWX = registerFile.get(Reg.WX) - WX_START;
        if (registerFile.testBit(Reg.LCDC, RegLCDC.WIN) && (realWX >= 0 && realWX < WIN_MAX_SIZE) && indexLine >= registerFile.get(Reg.WY)) {
            winY = (winY + 1) % IMAGE_SIZE;
            line = line.join(computeWinLine(realWX), realWX);
        }   
        return line.mapColors(registerFile.get(Reg.BGP));
    }

    private LcdImageLine computeBgLine(int indexLine) {
        final int startAddress = registerFile.testBit(Reg.LCDC, RegLCDC.BG_AREA) ? AddressMap.BG_DISPLAY_DATA[1] : AddressMap.BG_DISPLAY_DATA[0];
        final int Scx = registerFile.get(Reg.SCX);
        final int Scy = registerFile.get(Reg.SCY);
        final int indexY = (indexLine + Scy) % IMAGE_SIZE;
        return computeBgWinLine(startAddress, indexY).extractWrapped(Scx, LCD_WIDTH);
    }

    private LcdImageLine computeWinLine(int realXW) {
        final int startAddress = registerFile.testBit(Reg.LCDC, RegLCDC.WIN_AREA) ? AddressMap.BG_DISPLAY_DATA[1] : AddressMap.BG_DISPLAY_DATA[0];
        return computeBgWinLine(startAddress, winY).extractWrapped(realXW, LCD_WIDTH);
    }

    private LcdImageLine computeBgWinLine(int startAddress, int indexY) {
        LcdImageLine.Builder lineBuilder = new LcdImageLine.Builder(IMAGE_SIZE);
        final int tileIndexY = indexY / PIXEL_PER_TILE_LINE;
        final int tileLineIndex = indexY % PIXEL_PER_TILE_LINE;

        for (int i = 0; i < Integer.SIZE; i++) {
            final int numberOfTheTile = read(startAddress + tileIndexY * Integer.SIZE + i);
            final int address = getAddress(numberOfTheTile, tileLineIndex);
            final int lsb = Bits.reverse8(read(address));
            final int msb = Bits.reverse8(read(address + 1));
            lineBuilder.setBytes(i, msb, lsb);
        }
        return lineBuilder.build();
    }

    private int getAddress(int numberOfTheTile, int tileLineIndex) {
        final int begin;
        if (registerFile.testBit(Reg.LCDC, RegLCDC.TILE_SOURCE) || numberOfTheTile >= NUMBER_OF_TILE_ACCESSIBLE / 2)
            begin = AddressMap.TILE_SOURCE[1];
        else
            begin = AddressMap.TILE_SOURCE[1] + NUMBER_OF_TILE_ACCESSIBLE * TILE_SIZE_IN_MEMORY;
        return begin + numberOfTheTile * TILE_SIZE_IN_MEMORY + tileLineIndex * 2;
    }
}