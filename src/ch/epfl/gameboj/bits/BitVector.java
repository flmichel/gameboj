package ch.epfl.gameboj.bits;

import java.util.Arrays;

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
     * @param size : taille du vecteur
     * @param v : valeur des Bits (pour false tout les bits sont initialisé à zéro et true à un)
     */
    public BitVector(int size, boolean v) {
        this(checkAndFill(size, v));
    }

    /**
     * Construit un vecteur de Bits nuls
     * @param size : taille du vecteur
     */
    public BitVector(int size) {
        this(size, false);
    }

    private BitVector(int[] elements) {
        vect = elements;
    }

    private static int[] checkAndFill(int size, boolean v) {
        Preconditions.checkArgument(size >= 0 && (size % Integer.SIZE == 0));
        final int val = v ? -1 : 0; //-1 en complément à 2 est composé de 32 1 en binaire.
        final int[] tab = new int[size/Integer.SIZE];
        Arrays.fill(tab, val);
        return tab;         
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
        return Bits.test(vect[index / Integer.SIZE] , index % Integer.SIZE);
    }

    /**
     * Retourne le complement du vecteur de bits
     * @return le complement du vecteur de bits
     */
    public BitVector not() {
        final int[] tab = new int[vect.length];
        for (int i = 0 ; i < vect.length ; i++) {
            tab[i] = ~vect[i];
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

    private enum Extract {ZERO_EXTENDED, WARPED};

    public BitVector extractZeroExtended(int start, int size) {
        return extract(start, size, Extract.ZERO_EXTENDED);
    }
    
    public BitVector extractWrapped(int start, int size) {
        return extract(start, size, Extract.WARPED);
    }
    
    public BitVector shift(int distance) {
        return extractZeroExtended(-distance, vect.length * Integer.SIZE);
    }

    private BitVector extract(int start, int size, Extract type ) {
        Preconditions.checkArgument(size > 0 && (size % Integer.SIZE == 0));
        final int[] tab = new int[size/32];
       
        for (int i = 0 ; i < tab.length ; i++) {
           tab[i] = getExtractedValue(i, start, type);
        }
        return new BitVector(tab);
    }
  
    private int getExtractedValue(int index, int start, Extract type) {
        final int i = Math.floorDiv(start, Integer.SIZE) + index;
        final int start32 = Math.floorDiv(start, Integer.SIZE) * Integer.SIZE;
        int a = 0;
        if (start % Integer.SIZE == 0) {
            if (i >= 0 && i < vect.length) {
                a = vect[i];
            }
            else if (type == Extract.WARPED)
                a = vect[i % vect.length];
        } else {
            int cut = Math.floorMod(start, Integer.SIZE);
            a = Bits.clip(cut, getExtractedValue(index + 1, start32, type)) << Integer.SIZE - cut;
            a |= Bits.extract(getExtractedValue(index, start32, type), cut, Integer.SIZE - cut);
        }
        return a;
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof BitVector))
            return false;
        return Arrays.equals(this.vect, ((BitVector)that).vect);
    } 

    @Override
    public int hashCode() {
        
        
        return Arrays.hashCode(vect);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = vect.length - 1; i >= 0; i--) {
            sb.append(String.format("%32s", Integer.toBinaryString(vect[i])).replace(' ', '0'));
        }
        return sb.toString();
    }

    public final static class Builder {

        private int[] tab;
        
        public Builder(int vectSize) {
            tab = checkAndFill(vectSize, false);
        }
        
        public Builder setByte(int index, int value) {
            if (tab == null)
                throw new IllegalStateException();
            Preconditions.checkArgument(index >= 0 && index < tab.length * 4);
            final int shift = (index % 4) * Byte.SIZE;
            final int i = index/4;
            final int mask = 0b11111111 << shift; // Biggest value of a byte.
            final int inverseMask = ~mask;
            tab[i] &= inverseMask; // We clear the part with we want to set.
            tab[i] |= value << shift;
            return this;
        }
        
        public BitVector build() {
            if (tab == null)
                throw new IllegalStateException();
            BitVector vector = new BitVector(tab);
            tab = null;
            return vector;
        }
    }
}
