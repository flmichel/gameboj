package ch.epfl.gameboj.component.lcd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.bits.BitVector;
import ch.epfl.gameboj.bits.Bits;
/**
 * Une image dans l'écran du Game Boy.
 * @author Riand Andre
 * @author Michel François
 */
public final class LcdImage {

    private final int width;
    private final int height;
    private final List<LcdImageLine> lines;

    /**
     * Construit une image Game Boy.
     * @param width représente la largeur de l'image.
     * @param height représente la hauteur de l'image.
     * @param lines contient la liste de toutes les lignes de l'image.
     * @throws IllegalArgumentException si la largeur de l'image est négative ou si 
     * la hauteur est différente du nombre de line (de lines).
     */
    public LcdImage(int width, int height, List<LcdImageLine> lines) {
        Preconditions.checkArgument(width > 0 && height == lines.size());
        this.width = width;
        this.height = height;
        this.lines = new ArrayList<>(lines);           
    }

    /**
     * Retourne la largeur de l'image.
     * @return la largeur de l'image.
     */
    public int width() {
        return width;
    }

    /**
     * Retourne la hauteur de l'image.
     * @return la hauteur de l'image.
     */
    public int height() {
        return height;
    }

    /**
     * Retourne un entier composé de 2 bits représentant la couleur à l'index (x,y) donné.
     * @param x corresond à l'axe horizontal de l'image.
     * @param y corresond à l'axe vertical de l'image.
     * @return un entier composé de 2 bits représentant la couleur à l'index (x,y) donné.
     */
    public int get(int x, int y) {
        Preconditions.checkArgument(x < width && x >= 0 && y < height && y >= 0);
        LcdImageLine line = lines.get(y);
        int color = 0;
        color = Bits.set(color, 0, line.lsb().testBit(x));
        color = Bits.set(color, 1, line.msb().testBit(x));
        return color;
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof LcdImage))
            return false;
        LcdImage thatImage = (LcdImage)that;
        if (this.height != thatImage.height || this.width != thatImage.width)
            return false;
        return this.lines.equals(thatImage.lines);
    }

    @Override
    public int hashCode() {
        return this.lines.hashCode();
    }

    /**
     * Bâtisseur d'image GameBoy
     * @author Riand Andre
     * @author Michel François
     */
    public final static class Builder {

        private List<LcdImageLine> linesList;

        /**
         * Construit le bâtisseur d'image et tous ses pixels ont la couleur 0.
         * @param width représente la largeur de l'image.
         * @param height représente la hauteur de l'image.
         */
        public Builder(int width, int height) {
            BitVector ZeroLine = new BitVector(width);
            linesList = new ArrayList<>(Collections.nCopies(height, new LcdImageLine(ZeroLine, ZeroLine, ZeroLine)));
        }

        /**
         * Modifie une ligne de l'image à l'index donné.
         * @param index auquel la ligne va être remplacée.
         * @param line est la ligne qui modifie l'image.
         * @return le bâtisseur avec la ligne d'index "index" modifiée.
         * @throws IllegalStateException si on appelle la méthode après avoir appelé la méthode build.

         */
        public Builder setLine(int index, LcdImageLine line) {
            if (linesList == null)
                throw new IllegalStateException();
            linesList.set(index, line);
            return this;
        }

        /**
         * Retourne l'image construite.
         * @return l'image construite.
         * @throws IllegalStateException si on appelle la méthode après avoir appelé la méthode build.
         */
        public LcdImage build() {
            if (linesList == null)
                throw new IllegalStateException();
            int height = linesList.size();
            int width = linesList.get(0).size();
            LcdImage image = new LcdImage(width, height, linesList);
            linesList = null;
            return image;
        }
    }
}
