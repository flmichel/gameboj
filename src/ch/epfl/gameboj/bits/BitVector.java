package ch.epfl.gameboj.bits;

import java.util.ArrayList;

import ch.epfl.gameboj.Preconditions;

public final class BitVector {

    private final int length;
   // private final ArrayList<Integer> vect;
    
    public BitVector (int taille, boolean v) {
        Preconditions.checkArgument(taille >= 0 && (taille % Integer.SIZE == 0));
        int val = 0;
        if(v) val = 1;
        for (int i = 0 ; i < taille/Integer.SIZE ; i++) {
            
//            vect.add(val);
        }
        
        
        length = taille;
    }
    
}
