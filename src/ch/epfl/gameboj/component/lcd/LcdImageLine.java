package ch.epfl.gameboj.component.lcd;
import java.util.Objects;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.bits.BitVector;
import ch.epfl.gameboj.bits.Bits;

/**
 * Représente une ligne d'image Game Boy.
 * @author Riand Andre
 * @author Michel François
 */
public final class LcdImageLine {

    private final BitVector msb;
    private final BitVector lsb;
    private final BitVector opac;
    private final static int NB_COLORS = 4;

    /**
     * Construit une ligne d'image Game Boy.
     * @param msb contient les bits de poids fort
     * @param lsb contient les bits de poids faible
     * @param opacity contient le degré d'opacité
     * @throws IllegalArgumentException si les 3 BitVectors passés en arguments n'ont pas la meme longueur.
     */
    public LcdImageLine(BitVector msb, BitVector lsb, BitVector opacity) {
        Preconditions.checkArgument(msb.size() == lsb.size() && lsb.size() == opacity.size());
        this.msb = msb;
        this.lsb = lsb;
        opac = opacity;
    }

    /**
     * @return la longueur, en pixels, de la ligne
     */
    public int size() {
        return msb.size(); //pourrait etre lsb.size ou opac.size aussi
    }   

    /**
     * @return le vecteur des bits de poids fort
     */
    public BitVector msb() {
        return msb;
    }

    /**
     * @return le vecteur des bits de poids faible
     */
    public BitVector lsb() {
        return lsb;
    }

    /**
     * @return le vecteur des bits d'opacité
     */
    public BitVector opacity() {
        return opac;
    }

    /**
     * Décale la ligne d'un nombre de pixels donné, en préservant sa longueur, en utilisant la convention qu'une distance positive 
     * représente un décalage à gauche, une distance négative un décalage à droite (shift).
     * @param nb : nombre de décalages unitaires à faire
     * @return la ligne décalée, de meme longueur.
     */
    public LcdImageLine shift(int nb) {
        return new LcdImageLine(msb.shift(nb), lsb.shift(nb), opac.shift(nb));
    }

    /**
     * Fait l'extraction de l'extension infinie par enroulement, à partir d'un pixel donné, une ligne de longueur donnée. 
     * @param index : index à partir duquel l'extraction commence
     * @param l : longueur de l'extraction
     * @return l'extraction décrite ci-dessus
     */
    public LcdImageLine extractWrapped(int index, int l) {
        return new LcdImageLine(msb.extractWrapped(index, l), lsb.extractWrapped(index, l), opac.extractWrapped(index, l));
    }
    
    /**
     * Transforme les couleurs de la ligne en fonction d'une palette, donnée sous la forme d'un octet encodé, qui, à chaque couleur, en associe une autre. 
     * @param map : palette de transformation des couleurs
     * @return Une nouvelle ligne, avec les couleurs tranformées.
     * @throws IllegalArgumentException si "map" a plus de 8 bits (ce qui correspondrait à une palette incorrecte).
     */
    public LcdImageLine mapColors(int map) {
        Preconditions.checkBits8(map);
        if(map == 0b11_10_01_00) {return this;}
        BitVector nMsb = new BitVector(this.size());
        BitVector nLsb = new BitVector(this.size());
        for (int i = 0 ; i < NB_COLORS ; i++) {
            BitVector a = Bits.test(i,0) ? lsb : lsb.not(); 
            BitVector b = Bits.test(i,1) ? msb : msb.not();
            BitVector mask = a.and(b);
            nMsb = Bits.test(map, 2*i+1) ? nMsb.or(mask) : nMsb;
            nLsb = Bits.test(map, 2*i) ? nLsb.or(mask) : nLsb;
        }
        return new LcdImageLine(nMsb, nLsb, this.opac);
    }

    /**
     * Compose deux lignes de longueur identique pour en obtenir une nouvelle de même longueur dont les pixels sont ceux de la ligne du dessus, pour ceux qui sont opaques, et ceux de la ligne du dessous sinon.
     * @param that : Deuxieme ligne utilisée pour la composition
     * @return Nouvelle ligne selon la description ci-dessus.
     * @throws IllegalArgumentException si les deux lignes n'ont pas la meme taille (longueur/ nombre de pixels).
     */
    public LcdImageLine below(LcdImageLine that) {
        return below(that, that.opac);
    }

    /**
     * Compose deux lignes de longueur identique pour en obtenir une nouvelle de même longueur en utilisant un vecteur d'opacité passé en argument pour effectuer la composition.
     * @param that : Deuxieme ligne utilisée pour la composition
     * @param opac : Vecteur d'opacité
     * @return Nouvelle ligne selon la description ci-dessus.
     * @throws IllegalArgumentException si les deux lignes n'ont pas la meme taille (longueur/ nombre de pixels) ou si le BitVector en argument n'a pas la meme taille que les lignes.
     */
    public LcdImageLine below(LcdImageLine that, BitVector opac) {
        Preconditions.checkArgument(this.size() == that.size());
        Preconditions.checkArgument(this.size() == opac.size());
        BitVector nMsb = opac.and(that.msb).or(opac.not().and(msb));
        BitVector nLsb = opac.and(that.lsb).or(opac.not().and(lsb));
        BitVector nOpac = opac.or(this.opac);
        return new LcdImageLine(nMsb, nLsb, nOpac);
    }

    /**
     * Compose deux lignes de longueur identique pour en obtenir une nouvelle de même longueur dont les "cut" premiers pixels sont ceux de la première, les autres ceux de la seconde. 
     * @param cut : Détermine jusqu'à quel'indice la nouvelle ligne aura le meme pixel que la premiere ligne. 
     * @param that : Deuxieme ligne utilisée pour la composition
     * @return Nouvelle ligne selon la description ci-dessus.
     * @throws IllegalArgumentException si les deux lignes n'ont pas la meme taille (longueur/ nombre de pixels) ou si l'argument "cut" n'est pas valide
     */
    public LcdImageLine join(LcdImageLine that, int cut) {
        BitVector mask = (new BitVector(this.size(), true).shift(cut));
        BitVector nMsb = msb.and(mask.not()).or(that.msb.and(mask));
        BitVector nLsb = lsb.and(mask.not()).or(that.lsb.and(mask));
        BitVector nOpac = opac.and(mask.not()).or(that.opac.and(mask));        
        return new LcdImageLine(nMsb, nLsb, nOpac);
    }
    
    @Override
    public boolean equals(Object that) {
        if (!(that instanceof LcdImageLine))
            return false;
        LcdImageLine thatImageLine = (LcdImageLine)that;
        return this.msb.equals(thatImageLine.msb) &&
               this.lsb.equals(thatImageLine.lsb) &&
               this.opac.equals(thatImageLine.opac);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(msb, lsb, opac);
    }
    
    /**
     * Bâtisseur d'une ligne d'image GameBoy
     * @author Riand Andre
     * @author Michel François
     */
    public final static class Builder {
        
        private BitVector.Builder msbLine;
        private BitVector.Builder lsbLine;
        
        /**
         * Construit la ligne avec une longuer size et met la couleur à 0.
         * @param size : taille de la ligne.
         * @throws IllegalArgumentException si la taille du vecteur est négative ou n'est pas un multiple de 32.
         */
        public Builder(int size) {
            msbLine = new BitVector.Builder(size);
            lsbLine = new BitVector.Builder(size);
        }

        /**
         * Définir la valeur des octets de poids fort et de poids faible de la ligne, à un index donné.
         * @param index auquel la valeur 8 bits est modifiée.
         * @param value est la nouvelle valeur 8 bit de la ligne.
         * @return
         */
        public Builder setBytes(int index, int highValue, int lowValue) {
            if (msbLine == null)
                throw new IllegalStateException();
            msbLine.setByte(index, highValue);
            lsbLine.setByte(index, lowValue);
            return this;
        }
        
        /**
         * Retourne la ligne construite. Les pixels de couleur 0 sont transparents, les autres opaques.
         * @return la ligne construite.
         * @throws IllegalStateException si on appelle la méthode après avoir appelé la méthode build.
         */
        public LcdImageLine build() {      
            if (msbLine == null)
                throw new IllegalStateException();
            BitVector msb = msbLine.build();
            BitVector lsb = lsbLine.build();
            BitVector opacity = msb.or(lsb);
            msbLine = null;
            return new LcdImageLine(msb, lsb, opacity);        
        }
    }
}
