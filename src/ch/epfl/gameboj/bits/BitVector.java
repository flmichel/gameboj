package ch.epfl.gameboj.bits;

import ch.epfl.gameboj.Preconditions;
/**
 * Représente un vecteur de bits
 * @author Riand Andre
 * @author Michel François
 */
public final class BitVector {

    private final int[] vect;

    //Constructeurs

    /**
     * Construit un vecteur de Bits.
     * @param taille : taille du vecteur
     * @param v : valeur des Bits (pour false tout les bits sont initialisé à zéro et true à un)
     */
    public BitVector(int taille, boolean v) {
        this(checkAndFill(taille, v));
    }

    /**
     * Construit un vecteur de Bits nuls
     * @param taille : taille du vecteur
     */
    public BitVector(int taille) {
        this(taille, false);
    }

    private BitVector(int[] elements) {
        vect = elements;
    }

    private static int[] checkAndFill(int taille, boolean v) {
        Preconditions.checkArgument(taille >= 0 && (taille % Integer.SIZE == 0));
        final int val = v ? Integer.MAX_VALUE : 0;
        final int[] temp = new int[taille/Integer.SIZE];
        for (int i = 0 ; i < temp.length ; i++) {
            temp[i] = val;
        }
        return temp;         
    }

    //Méthodes publiques

    /**
     * Retourne la taille du vecteur de bits
     * @return la taille du vecteur de bits
     */
    public int size() { 
        return vect.length * Integer.SIZE;
    }

    /**
     * Détermine si le bit d'index donné est vrai ou faux.
     * @param index : index à tester
     * @return true si le bit vaut 1 et false si le vaut 0
     */
    public boolean testBit(int index) {
        return Bits.test(index / Integer.SIZE , index % Integer.SIZE);
    }

    /**
     * Retourne le complement du vecteur de bits
     * @return le complement du vecteur de bits
     */
    public BitVector not() {
        final int[] tab = new int[vect.length];
        for (int i = 0 ; i < vect.length ; i++) {
            tab[i] = vect[i] ^ Integer.MAX_VALUE;
        }
        return new BitVector(tab);
    }

    /**
     * Calcule la conjonction de deux vecteurs de bits
     * @param that : un vecteur de bits
     * @return la conjonction bit à bit de "this" et "that".
     * * @throws IllegalArgumentException si les deux vecteurs de bits n'ont pas la meme taille
     */
    public BitVector and(BitVector that) {
        Preconditions.checkArgument(that.size() == this.vect.length * Integer.SIZE);
        final int[] tab = new int[vect.length];
        for (int i = 0 ; i < vect.length ; i++) {
            tab[i] = vect[i] & that.vect[i];
        }
        return new BitVector(tab);
    }

    /**
     * Calcule la disjonction de deux vecteurs de bits
     * @param that : un vecteur de bits
     * @return la disjonction bit à bit de "this" et "that".
     * @throws IllegalArgumentException si les deux vecteurs de bits n'ont pas la meme taille
     */
    public BitVector or(BitVector that) {
        Preconditions.checkArgument(that.size() == this.vect.length * Integer.SIZE);
        final int[] tab = new int[vect.length];
        for (int i = 0; i < vect.length; i++) {
            tab[i] = vect[i] | that.vect[i];
        }
        return new BitVector(tab);
    }

    public BitVector extractZeroExtended(int start, int size) {
        return extract(start, size, false);
    }
    
    public BitVector extractWrapped(int start, int size) {
        return extract(start, size, true);
    }
    
    public BitVector shift(int distance) {
        return extractZeroExtended(distance, vect.length * Integer.SIZE);
    }

    private BitVector extract(int start, int size, boolean type) {
        Preconditions.checkArgument(size > 0);
        int tabLength = vect.length;
        final int[] tab = new int[tabLength];
        
        for (int i = 0 ; i < tabLength ; i++) {
           tab[i] = getExtractedValue(i, start, type);
        }        
        return new BitVector(tab);
    }

    private int getExtractedValue(int index, int start, boolean type) {
        int number = 0;
        int cut = Math.floorMod(start, Integer.SIZE);
        if (index > -1 && index < vect.length) {
            int i = Math.floorDiv(start, Integer.SIZE) + index;
            if (index != vect.length * Integer.SIZE)
                number = Bits.clip(cut, vect[i+1]);
            if (index != -1) 
                number |= Bits.extract(vect[i], cut, Integer.SIZE-cut) << cut;
        }
        if (type && (index < 0 || index > vect.length - 1)) {
            int i = (Math.floorDiv(start, 32) + index) % vect.length;
            int afterI = i == vect.length ? 0 : i+1;
            number |= Bits.extract(vect[i], cut, 32-cut) << cut;
            number = Bits.clip(cut, vect[afterI]);
        }
        return number;
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof BitVector))
            return false;
        final BitVector thatVec = (BitVector)that;
        if (this.vect.length!= thatVec.vect.length)
            return false;
        for (int i = 0; i < vect.length; i++) {
            if (this.vect[i] != thatVec.vect[i])
                return false;
        }  
        return true;
    } 

    @Override
    public int hashCode() {
        return vect.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int val : vect) {
            sb.append(Integer.toBinaryString(val));
        }
        return sb.toString();
    }

    public final static class Builder {

        private final int[] tab;
        private final BitVector vector = null;
        
        public Builder(int vectSize) {
            tab = checkAndFill(vectSize, false);
        }
        
        public Builder setByte(int index, int value) {
            Preconditions.checkArgument(index >= 0 && index < tab.length * 4);
            tab[index/4] = value << (index % 4) * Byte.SIZE;
            return this;
        }
        
        public BitVector build() {
            if (vector != null)
                throw new IllegalStateException();
        return new BitVector(tab);
        }
    }
}
