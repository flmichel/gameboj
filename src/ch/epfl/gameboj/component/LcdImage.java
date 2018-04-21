package ch.epfl.gameboj.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.bits.BitVector;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.lcd.LcdImageLine;

public final class LcdImage {
    
    private final int width;
    private final int height;
    private final List<LcdImageLine> lines;

    public LcdImage(int width, int height, List<LcdImageLine> lines) {
        Preconditions.checkArgument(height > 0 && height == lines.size());
        this.width = width;
        this.height = height;
        this.lines = lines;           
    }
    
    public int widht() {
        return width;
    }
    
    public int height() {
        return height;
    }
    
    public int get(int x, int y) {
        Preconditions.checkArgument(x < width && x >= 0 && y < height && y >= 0);
        LcdImageLine line = lines.get(y);
        int color = 0;
        color = Bits.set(color, 0, line.lsb().testBit(x));
        color = Bits.set(color, 1, line.msb().testBit(x));
        return color;
    }
    
    public final static class Builder {

        private final List<LcdImageLine> linesList;
        
        /**
         * Construit un vecteurs initialisé à zéro et de taille "vectSize"
         * @param vectSize : taille du vecteur
         * @throws IllegalArgumentException si la taille du vecteur est négative ou n'est pas un multiple de 32.
         */
        public Builder(int width, int height) {
            BitVector line = new BitVector(width);
            linesList = new ArrayList<>(Collections.nCopies(height, 0));
        }

        public Builder setLine(int index, int value) {
            
        }
        
        public BitVector build() {
        }
    }
}
