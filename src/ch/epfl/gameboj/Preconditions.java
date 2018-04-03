package ch.epfl.gameboj;

/**
 * Assure la validité des arguments vérifiant des conditions pré-determinées.
 * @author Riand Andre
 * @author Michel François
 */
public interface Preconditions {

    /**
     * Vérifie si la condition passée en argument est remplie.
     * @param b : boolean qui représente la condition à remplir.
     * @throws IllegalArgumentException si la condition n'est pas remplie.
     */
    static public void checkArgument(boolean b) {
        if (!b)
            throw new IllegalArgumentException();
    }

    /**
     * Vérifie si l'entier passé en argument peut s'écrire avec 8 bits.
     * @param v : entier à vérifier
     * @return v : l'argument lui-meme, si celui-ci peut s'écrire avec 8 bits.
     * @throws IllegalArgumentException si l'entier ne peut pas s'écrire avec 8 bits.
     */
    static public int checkBits8(int v) {
        if (v >= 0 && v <= 0xFF)
            return v;
        else
            throw new IllegalArgumentException();
    }

    /**
     * Vérifie si l'entier passé en argument peut s'écrire avec 16 bits.
     * @param v : entier à vérifier
     * @return v : l'argument lui-meme, si celui-ci peut s'écrire avec 16 bits.
     * @throws IllegalArgumentException si l'entier ne peut pas s'écrire avec 16 bits.
     */
    static public int checkBits16(int v) {
        if (v >= 0 && v <= 0xFFFF)
            return v;
        else
            throw new IllegalArgumentException();
    }
}
