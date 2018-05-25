package ch.epfl.gameboj.component.lcd;

import java.util.Arrays;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.Register;
import ch.epfl.gameboj.RegisterFile;
import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.bits.BitVector;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.Clocked;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cpu.Cpu;
import ch.epfl.gameboj.component.cpu.Cpu.Interrupt;
import ch.epfl.gameboj.component.lcd.LcdImageLine.Builder;
import ch.epfl.gameboj.component.memory.Ram;
/**
 * Représente un contrôleur LCD
 * @author Riand Andre
 * @author Michel François
 */
public final class LcdController implements Component, Clocked {

    public static final int LCD_WIDTH = 160;
    public static final int LCD_HEIGHT = 144;
    
    private static final int NB_CYCLES_MODE0 = 51;
    private static final int NB_CYCLES_MODE2 = 20;
    private static final int NB_CYCLES_MODE3 = 43;
    private static final int NB_CYCLES_LINE = NB_CYCLES_MODE0 + NB_CYCLES_MODE2 + NB_CYCLES_MODE3;
    private static final int NB_CYCLES_LCD = 17556;
    private static final int ENTER_MODE2 = 0;
    private static final int ENTER_MODE3 = NB_CYCLES_MODE2;
    private static final int ENTER_MODE0 = NB_CYCLES_MODE2 + NB_CYCLES_MODE3;

    private static final int IMAGE_SIZE = 256;
    private static final int PIXEL_PER_TILE_LINE = 8;
    private static final int TILE_SIZE_IN_MEMORY = 16;
    private static final int NUMBER_OF_TILE_ACCESSIBLE = 256;
    private static final int WX_START = 7;

    private static final int SPRITE_SIZE_IN_MEMORY = 4;
    private static final int SMALL_SPRITE_HEIGHT = PIXEL_PER_TILE_LINE;
    private static final int BIG_SPRITE_HEIGHT = 2 * PIXEL_PER_TILE_LINE;
    private static final int MAX_SPRITE_PER_LINE = 10;
    private static final int START_SPRITE_X = PIXEL_PER_TILE_LINE;
    private static final int START_SPRITE_Y = BIG_SPRITE_HEIGHT;

    private int winY;
    private long lcdOnCycle;
    private long nextNonIdleCycle = Long.MAX_VALUE;
    
    private Bus bus;
    private final Cpu cpu;
    
    private final Ram videoRam = new Ram(AddressMap.VIDEO_RAM_SIZE);
    private final Ram oamRam = new Ram(AddressMap.OAM_RAM_SIZE);
    private int sourceAddress;
    private int copyIndex = oamRam.size();

    private LcdImage.Builder nextImageBuilder;
    private LcdImage currentImage;

    private enum Reg implements Register {
        LCDC, STAT, SCY, SCX, LY, LYC, DMA, BGP, OBP0, OBP1, WY, WX;
    }

    private enum LCDC implements Bit {
        BG, OBJ, OBJ_SIZE, BG_AREA, TILE_SOURCE, WIN, WIN_AREA, LCD_STATUS;
    }

    private enum STAT implements Bit {
        MODE0, MODE1, LYC_EQ_LY, INT_MODE0, INT_MODE1, INT_MODE2, INT_LYC, UNUSED;
    }

    private enum Sprite implements Bit {
        Y, X, TILE_INDEX, FEATURES, 
    }

    private enum SpriteFeatures implements Bit {
        UNUSED_0, UNUSED_1, UNUSED_2, UNUSED_3, PALETTE, FLIP_H, FLIP_V, BEHIND_BG
    }
    
    private enum TypeOfLine {
        SPRITE, BG_OR_WIN
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
        if(copyIndex < oamRam.size()) {
            // copie le prochain octet vers la mémoire d'attributs d'objets
            oamRam.write(copyIndex, bus.read(sourceAddress));
            copyIndex++;
            sourceAddress++;
        }
        if (nextNonIdleCycle == Long.MAX_VALUE && registerFile.testBit(Reg.LCDC, LCDC.LCD_STATUS)) {
            nextNonIdleCycle = cycle;
            lcdOnCycle = cycle;
        }       
        if (cycle == nextNonIdleCycle) {
            reallyCycle(cycle);
        }
    }

    private void reallyCycle(long cycle) {
        final int cycleInLine = (int) ((cycle - lcdOnCycle) % NB_CYCLES_LINE);
        final int lineIndex = ((int) (cycle - lcdOnCycle) % NB_CYCLES_LCD) / NB_CYCLES_LINE;
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
            return oamRam.read(address - AddressMap.OAM_START);
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
        if (address >= AddressMap.OAM_START && address < AddressMap.OAM_END) {
            oamRam.write(address - AddressMap.OAM_START, data);
        }
        if (address >= AddressMap.REGS_LCDC_START && address < AddressMap.REGS_LCDC_END) {
            final Reg reg = Reg.values()[address - AddressMap.REGS_LCDC_START];

            if (reg == Reg.STAT) {
                final int maskReadOnly = STAT.MODE0.mask() | STAT.MODE1.mask() | STAT.LYC_EQ_LY.mask();
                final int mask = ~maskReadOnly;
                data &= mask;
            }
            registerFile.set(reg, data);

            switch (reg) {
            case LYC: {
                updateStateLycEqLy();            
            } break;
            case LCDC: {
                if (!Bits.test(data, LCDC.LCD_STATUS.index())) {
                    setMode(0);
                    registerFile.set(Reg.LY, 0);
                    nextNonIdleCycle = Long.MAX_VALUE;
                }          
            } break;
            case DMA: {
                copyIndex = 0;
                sourceAddress = data << 8;           
            } break;
            default:
                break;
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
        registerFile.setBit(Reg.STAT, STAT.LYC_EQ_LY, lycEqLy);
        if (registerFile.testBit(Reg.STAT, STAT.INT_LYC) && lycEqLy) {
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
            if (registerFile.testBit(Reg.STAT, STAT.values()[STAT.INT_MODE0.index() + mode]))
                cpu.requestInterrupt(Interrupt.LCD_STAT);
        }
        registerFile.setBit(Reg.STAT, STAT.MODE0, Bits.test(mode, 0));
        registerFile.setBit(Reg.STAT, STAT.MODE1, Bits.test(mode, 1));
    }

    private LcdImageLine computeLine(int indexLine) {

        LcdImageLine line = new LcdImageLine.Builder(LCD_WIDTH).build();
        if (registerFile.testBit(Reg.LCDC, LCDC.BG)) // Background
            line = computeBgLine(indexLine);
        final int realWX = Math.max(registerFile.get(Reg.WX) - WX_START, 0); 
        if (registerFile.testBit(Reg.LCDC, LCDC.WIN) && realWX < LCD_WIDTH && indexLine >= registerFile.get(Reg.WY)) { // Window
            line = line.join(computeWinLine(realWX), realWX);
            winY = (winY + 1) % IMAGE_SIZE;
        }
        if (registerFile.testBit(Reg.LCDC, LCDC.OBJ)) { // Sprites
            line = computeSpriteLine(indexLine, line);
        }
        return line;
    }

    private LcdImageLine computeBgLine(int indexLine) {
        final int startAddress = registerFile.testBit(Reg.LCDC, LCDC.BG_AREA) ? AddressMap.BG_DISPLAY_DATA[1] : AddressMap.BG_DISPLAY_DATA[0];
        final int Scx = registerFile.get(Reg.SCX);
        final int Scy = registerFile.get(Reg.SCY);
        final int indexY = (indexLine + Scy) % IMAGE_SIZE;
        return computeBgWinLine(startAddress, indexY).extractWrapped(Scx, LCD_WIDTH);
    }

    private LcdImageLine computeWinLine(int realXW) {
        final int startAddress = registerFile.testBit(Reg.LCDC, LCDC.WIN_AREA) ? AddressMap.BG_DISPLAY_DATA[1] : AddressMap.BG_DISPLAY_DATA[0];
        return computeBgWinLine(startAddress, winY).extractWrapped(-realXW, LCD_WIDTH);
    }

    private LcdImageLine computeSpriteLine(int indexLine, LcdImageLine line) {
        final int[] sprites = spritesIntersectingLine(indexLine);
        BitVector lineOpacity = line.opacity();
         
        for (int i = sprites.length - 1; i >= 0; i--) {
            final int features = spriteValue(sprites[i], Sprite.FEATURES);
            boolean behind = Bits.test(features, SpriteFeatures.BEHIND_BG);
            LcdImageLine spriteLine = spriteLine(sprites[i], indexLine, features);
            if (behind)
                line = spriteLine.below(line, lineOpacity.or(spriteLine.opacity().not()));          
            else
                line = line.below(spriteLine);   
        }
        return line;
    }

    private LcdImageLine computeBgWinLine(int startAddress, int indexY) {
        final Builder lineBuilder = new LcdImageLine.Builder(IMAGE_SIZE);
        final int tileIndexY = indexY / PIXEL_PER_TILE_LINE;
        final int tileLineIndex = indexY % PIXEL_PER_TILE_LINE;

        for (int i = 0; i < Integer.SIZE; i++) {
            final int numberOfTheTile = read(startAddress + tileIndexY * Integer.SIZE + i);
            final int address = getAddress(numberOfTheTile, tileLineIndex, TypeOfLine.BG_OR_WIN);
            final int lsb = Bits.reverse8(read(address));
            final int msb = Bits.reverse8(read(address + 1));
            lineBuilder.setBytes(i, msb, lsb);
        }
        return lineBuilder.build().mapColors(registerFile.get(Reg.BGP));
    }

    private int[] spritesIntersectingLine(int lineIndex) {
        final int spriteHeight = registerFile.testBit(Reg.LCDC, LCDC.OBJ_SIZE) ? BIG_SPRITE_HEIGHT : SMALL_SPRITE_HEIGHT;
        final int[] sprites = new int[MAX_SPRITE_PER_LINE];
        int spriteCount = 0;
        int spriteIndex = 0;
        while (spriteCount < MAX_SPRITE_PER_LINE && spriteIndex < AddressMap.OAM_RAM_SIZE / SPRITE_SIZE_IN_MEMORY) {
            int positionY = spriteValue(spriteIndex, Sprite.Y);
            if (positionY > lineIndex - spriteHeight && positionY <= lineIndex) {
                int positionX = spriteValue(spriteIndex, Sprite.X);
                sprites[spriteCount] = Bits.make16(positionX + START_SPRITE_X, spriteIndex);
                spriteCount++;
            }
            spriteIndex++;    
        }
        Arrays.sort(sprites, 0, spriteCount);
        final int[] spritesIndexes = new int[spriteCount];
        for (int i = 0; i < spriteCount; i++) {
            spritesIndexes[i] = Bits.clip(8, sprites[i]);
        }
        return spritesIndexes;        
    }

    private LcdImageLine spriteLine(int spriteIndex, int lineIndex, int features) {
        final int numberOfTheTile = spriteValue(spriteIndex, Sprite.TILE_INDEX);
        final int tileLineIndex = lineIndex - spriteValue(spriteIndex, Sprite.Y);
        final int xShift = spriteValue(spriteIndex, Sprite.X);

        final Reg OBP = Bits.test(features, SpriteFeatures.PALETTE) ? Reg.OBP1 : Reg.OBP0;
        final int palette = registerFile.get(OBP);
        final int spriteHeight = registerFile.testBit(Reg.LCDC, LCDC.OBJ_SIZE) ? BIG_SPRITE_HEIGHT : SMALL_SPRITE_HEIGHT;
        final int realTileLineIndex = Bits.test(features, SpriteFeatures.FLIP_V) ? spriteHeight - tileLineIndex - 1 : tileLineIndex;

        final Builder spriteLine = new LcdImageLine.Builder(LCD_WIDTH);
        final int address = getAddress(numberOfTheTile, realTileLineIndex, TypeOfLine.SPRITE);
        int lsb = read(address);
        int msb = read(address + 1);
        if (!Bits.test(features, SpriteFeatures.FLIP_H)) {
            lsb = Bits.reverse8(lsb);
            msb = Bits.reverse8(msb);
        }
        spriteLine.setBytes(0, msb, lsb);
        return spriteLine.build().shift(xShift).mapColors(palette);
    }

    private int spriteValue(int index, Sprite value) {
        int spriteValue = oamRam.read(index * SPRITE_SIZE_IN_MEMORY + value.index());
        if (value == Sprite.Y)
            spriteValue -= START_SPRITE_Y;
        if (value == Sprite.X)
            spriteValue -= START_SPRITE_X;
        return spriteValue;
    }

    private int getAddress(int numberOfTheTile, int tileLineIndex, TypeOfLine type) {
        final int begin;
        if (registerFile.testBit(Reg.LCDC, LCDC.TILE_SOURCE) || numberOfTheTile >= NUMBER_OF_TILE_ACCESSIBLE / 2 || type == TypeOfLine.SPRITE)
            begin = AddressMap.TILE_SOURCE[1];
        else
            begin = AddressMap.TILE_SOURCE[1] + NUMBER_OF_TILE_ACCESSIBLE * TILE_SIZE_IN_MEMORY;
        return begin + numberOfTheTile * TILE_SIZE_IN_MEMORY + tileLineIndex * 2;
    }
}