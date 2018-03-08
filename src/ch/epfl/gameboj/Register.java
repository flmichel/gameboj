package ch.epfl.gameboj;

public interface Register {

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
}
