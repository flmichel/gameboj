package ch.epfl.gameboj.bits;

public interface Bit {

    /**
     * Automatiquement fournie par le type énuméré.
     */
    int ordinal();
    
    /**
     * Même méthode que ordinal mais le nom est plus parlant.
     * @return la même valeur que la méthode ordinal
     */
    default int index() {
        return ordinal();
    }
    
    /**
     * Retourne le masque correspondant au bit, c'est-à-dire une valeur dont seul le bit de même index que celui du récepteur vaut 1.
     * @return le masque correspondant au bit.
     */
    default int mask() {
        return 0b1 << index();
    }
}
