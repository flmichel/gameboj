package ch.epfl.gameboj.bits;

public interface Bit {

    /**
     * Automatiquement fournie par le type énuméré.
     */
    int ordinal();
    
    /**
     * Même methode que ordinal mais le nom est plus parlant.
     * @return la même valeur que la méthode ordinal
     */
    default int index() {
        return ordinal();
    }
    
    /**
     * Retourne le masque correspondant au bit.
     * @return le masque correspondant au bit
     */
    default int mask() {
        return 0b1 << index();
    }
}
