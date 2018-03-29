package ch.epfl.gameboj;

public interface Preconditions {
    
    
    /**
     * Vérifie si la condition passée en argument est remplie.
     * @param b : boolean qui représente la condition à remplir.
     */
    static public void checkArgument(boolean b) {
        if (!b)
            throw new IllegalArgumentException();
    }
    
    /**
     * Vérifie si l'entier passé en argument peut s'écrire avec 8 bits, lance une exception dans le cas contraire.
     * @param v : entier à vérifier
     * @return v : l'argument lui-meme, si celui-ci peut s'écrire avec 8 bits.
     */
    static public int checkBits8(int v) {
        if (v >= 0 && v <= 0xff)
            return v;
        else
            throw new IllegalArgumentException();
    }
    
    /**
     * Vérifie si l'entier passé en argument peut s'écrire avec 16 bits, lance une exception dans le cas contraire.
     * @param v : entier à vérifier
     * @return v : l'argument lui-meme, si celui-ci peut s'écrire avec 16 bits.
     */
    static public int checkBits16(int v) {
        if (v >= 0 && v <= 0xffff)
            return v;
        else
            throw new IllegalArgumentException();
    }
}
