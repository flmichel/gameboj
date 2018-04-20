package ch.epfl.gameboj.component.lcd;

import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.bits.BitVector;

/**
 * Représente une ligne d'image Game Boy.
 * @author Riand Andre
 * @author Michel François
 */
public final class LcdImageLine {

    private final BitVector msb;
    private final BitVector lsb;
    private final BitVector opac;

    /**
     * Construit une ligne d'image Game Boy.
     * @param msb contient les bits de poids fort
     * @param lsb contient les bits de poids faible
     * @param opacity contient le degré d'opacité
     */
    public LcdImageLine (BitVector msb, BitVector lsb, BitVector opacity) {
        Preconditions.checkArgument(msb.size() == lsb.size() && lsb.size() == opacity.size());
        this.msb = msb;
        this.lsb = lsb;
        opac = opacity;
    }

    /**
     * @return la longueur, en pixels, de la ligne
     */
    public int size () {
        return msb.size(); //pourrait etre lsb.size ou opac.size aussi
    }   

    /**
     * @return le vecteur des bits de poids fort
     */
    public BitVector msb () {
        return msb;
    }

    /**
     * @return le vecteur des bits de poids faible
     */
    public BitVector lsb () {
        return lsb;
    }

    /**
     * @return le vecteur des bits d'opacité
     */
    public BitVector opacity () {
        return opac;
    }

    /**
     * Décale la ligne d'un nombre de pixels donné, en préservant sa longueur 
     * @param nb : nombre de décalages unitaires à faire
     * @return la ligne décalée, de meme longueur.
     */
    public LcdImageLine shift (int nb) {
        return new LcdImageLine(msb.shift(nb), lsb.shift(nb), opac.shift(nb));
    }

    /**
     * Fait l'extraction de l'extension infinie par enroulement, à partir d'un pixel donné, une ligne de longueur donnée. 
     * @param index : index à partir duquel l'extraction commence
     * @param l : longueur de l'extraction
     * @return l'extraction décrite ci-dessus
     */
    public LcdImageLine extractWrapped (int index, int l) {
        return new LcdImageLine(msb.extractWrapped(index, l), lsb.extractWrapped(index, l), opac.extractWrapped(index, l));
    }

    public LcdImageLine mapColors () {



    }

    public LcdImageLine below (LcdImageLine that) {

    }

    public LcdImageLine below (LcdImageLine that, BitVector opacity) {

    }

    /**
     * Consiste à composer deux lignes de longueur identique pour en obtenir une nouvelle de même longueur dont les "cut" premiers pixels sont ceux de la première, les autres ceux de la seconde. 
     * @param cut
     * @param that
     * @return Nouvelle ligne composée d'une partie 
     */
    public LcdImageLine join (int cut, LcdImageLine that) {
        Preconditions.checkArgument(this.size() == that.size());
        Preconditions.checkArgument(cut >= 0 && cut < this.size());
        BitVector mask = new BitVector(this.size(), true).shift(this.size()-cut);
        BitVector nMsb = msb.and(mask).or(that.msb.and(mask.not()));
        BitVector nLsb = lsb.and(mask).or(that.lsb.and(mask.not()));
        BitVector nOpac = opac.and(mask).or(that.opac.and(mask.not()));        
        return new LcdImageLine(nMsb, nLsb, nOpac);
    }
}
