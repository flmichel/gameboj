package ch.epfl.gameboj.component;

import java.util.List;

import ch.epfl.gameboj.component.lcd.LcdImageLine;

public final class LcdImage {
    
    private final int width;
    private final int height;
    private final List<LcdImageLine> lines;

    public LcdImage(int width, int height, List<LcdImageLine> lines) {
        this.width = width;
        this.height = height;
        this.lines = lines;
               
    }
}
