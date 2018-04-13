package ch.epfl.gameboj.bits;

import java.util.ArrayList;
import java.util.Objects;

import ch.epfl.gameboj.Preconditions;
/**
 * Représente un vecteur de bits
 * @author Riand Andre
 * @author Michel François
 */
public final class BitVector {

    private final int size;
    private final int[] vect;

    //Constructeurs

    /**
     * Construit un vecteur de Bits.
     * @param taille : taille du vecteur
     * @param v : valeur des Bits
     */
    public BitVector (int taille, boolean v) {
        this(checkAndFill(taille,v));
    }

    /**
     * Construit un vecteur de Bits nuls
     * @param taille : taille du vecteur
     */
    public BitVector (int taille) {
        this(taille, false);
    }

    private BitVector (int[] elements) {
        size = elements.length;
        vect = elements;
    }

    private static int[] checkAndFill (int taille, boolean v) {
        Preconditions.checkArgument(taille >= 0 && (taille % Integer.SIZE == 0));
        int val = 0;
        if(v) val = Integer.MAX_VALUE;
        int[] temp = new int[taille/Integer.SIZE];
        for (int i = 0 ; i < temp.length ; i++) {
            temp[i]= val;
        }
        return temp;         
    }

    //Méthodes publiques

    /**
     * Retourne la taille du vecteur de bits
     * @return la taille du vecteur de bits
     */
    public int size() { 
        return size;
    }

    /**
     * Détermine si le bit d'index donné est vrai ou faux.
     * @param index : index à tester
     * @return true si le bit vaut 1 et false si le vaut 0
     */
    public boolean testBit (int index) {
        return Bits.test(index / Integer.SIZE , index % Integer.SIZE);
    }

    /**
     * Retourne le complement du vecteur de bits
     * @return le complement du vecteur de bits
     */
    public BitVector not () {
        int[] tab = new int[size];
        for (int i = 0 ; i < size ; i++) {
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
    public BitVector and (BitVector that) {
        Preconditions.checkArgument(that.size() == this.size);
        int[] tab = new int[size];
        for (int i = 0 ; i < size ; i++) {
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
    public BitVector or (BitVector that) {
        Preconditions.checkArgument(that.size() == this.size);
        int[] tab = new int[size];
        for (int i = 0 ; i < size ; i++) {
            tab[i] = vect[i] | that.vect[i];
        }
        return new BitVector(tab);
    }

    public BitVector extractZeroExtended (int start, int size) {
        int[] tab = new int[size];

        return new BitVector(tab);
    }

    private BitVector extract (int start, int size, boolean type) {
        int[] tab = new int[size/Integer.SIZE];
        for (int i = 0 ; i < tab.length ; i++) {
            
        }        
        return new BitVector(tab);
    }

    private int InfiniteElem (int index, boolean type) {
        if(type) {
            return vect[index % size]; 
        } else {            
            if(index >= 0 && index < size) {
                return vect[index];               
            } else {return 0;}       
        }
    }
}
