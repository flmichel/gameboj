package ch.epfl.gameboj.bits;

import java.util.Objects;

import ch.epfl.gameboj.Preconditions;

/**
 * Représente un ensemble de bits.
 * @author Riand Andre
 * @author Michel François
 */
public final class Bits {

    private Bits() {} //Le constructeur fermé rend la classe non instanciable.

    /**
     * Retourne un entier int dont seul le bit d'index donné vaut 1. 
     * @param index compris entre 0 (inclus) et 32 (exclus).
     * @return un entier int dont seul le bit d'index donné vaut 1.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    public static int mask(int index) {
        Objects.checkIndex(index, Integer.SIZE);
        return 0b1 << index;
    }

    /**
     * Retourne vrai si et seulement si le bit d'index donné de l'entier donné vaut 1.
     * @param bits est l'entier à évaluer.
     * @param index compris entre 0 (inclus) et 32 (exclus) qui correspond au bit qu'il faut "regarder".
     * @return true si et seulement si le bit d'index donné de "bits" vaut 1.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    public static boolean test(int bits, int index) {
        Objects.checkIndex(index, Integer.SIZE);
        return (mask(index) & bits) != 0;
    }

    /**
     * Retourne vrai si et seulement si le bit d'index (donné par bit) de l'entier "bits" vaut 1.
     * @param bits est l'entier à évaluer.
     * @param bit est un entier. On obtient l'index à tester grace à ce bit.
     * @return vrai si et seulement si le bit d'index donné de bits vaut 1.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    public static boolean test(int bits, Bit bit) {
        return test(bits, bit.index());
    }

    /**
     * Retourne une valeur dont tous les bits sont égaux à ceux de l'entier donné, sauf celui d'index donné, qui est égal à newValue (1 si newValue est true, 0 si false).
     * @param bits est l'entier (int) utilisé.
     * @param index compris entre 0 (inclus) et 32 (exclus).
     * @param newValue est un boolean qui définit si le bit doit passer à 1 ou 0.
     * @return une valeur dont tous les bits sont égaux à ceux de bits, sauf celui d'index donné, qui est égal à newValue.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    public static int set(int bits, int index, boolean newValue) {
        Objects.checkIndex(index, Integer.SIZE);
        if (newValue)
            return bits | (0x1 << index);
        return bits & ~(0x1 << index);
    }

    /**
     * Retourne une valeur dont les "size" (argument) bits de poids faible sont égaux à ceux de "bits" (argument), les autres valant 0.
     * @param size : nombre de bits de poids faible à "garder".
     * @param bits : entier duquel on enlèvera des bits de poids fort.  
     * @return Un nouveau entier, qui correspond aux "size" bits de poids faible de "bits".
     * @throws IllegalArgumentException si "size" n'est pas compris entre 0 (inclus) et 32 (inclus).
     */
    public static int clip(int size, int bits) {
        Preconditions.checkArgument(size >= 0 && size <= Integer.SIZE);
        if (size == Integer.SIZE)
            return bits;
        return ((0b1 << size) - 1) & bits;
    }

    /**
     * Retourne une valeur dont les "size" bits de poids faible sont égaux à ceux de "bits" allant de l'index "start" (inclus) à l'index (start + size) (exclus). 
     * @param bits : entier qui sera "tronqué".
     * @param start : index du premier bit (à gauche) du nouveau entier.
     * @param size : taille du nouveau entier.
     * @return Un nouveau entier, crée à partir de l'entier "bits", en le tronquant.
     * @throws IndexOutOfBoundsException si "start" et "size" ne désignent pas une plage de bits valide.
     */
    public static int extract(int bits, int start, int size) {
        Objects.checkFromIndexSize(start, size, Integer.SIZE);
        return clip(size, bits >>> start);
    }

    /**
     * Retourne une valeur dont les "size" bits de poids faible sont ceux de "bits" mais auxquels une rotation de la distance donnée a été appliquée ; si la distance est positive, la rotation se fait vers la gauche, sinon elle se fait vers la droite. 
     * @param size : représente le nombre de bits qui seront affectés par la rotation.
     * @param bits : entier à manipuler.
     * @param distance : détermine la direction et quantité de la rotation. Par quantité, on entend le nombre de rotations unitaires. 
     * @return un entier crée suivant la description ci-dessus.
     * @throws IllegalArgumentException si "size" n'est pas compris entre 0 (exclus) et 32 (inclus), ou si la valeur donnée n'est pas une valeur de "size" bits.
     */
    public static int rotate(int size, int bits, int distance) {
        Preconditions.checkArgument(size > 0 && size <= Integer.SIZE);
        Preconditions.checkArgument((bits >>> size == 0) || size == Integer.SIZE);
        distance = Math.floorMod(distance, size);
        bits = (bits << distance) | (bits >>> (size - distance));
        return clip(size, bits);
    }

    /**
     * étend le signe de la valeur 8 bits donnée, c'est-à-dire copie le bit d'index 7 dans les bits d'index 8 à 31 de la valeur retournée.
     * @param b : entier dont on veut étendre le signe.
     * @return l'entier en question, signé.
     * @throws IllegalArgumentException si la valeur donnée n'est pas une valeur de 8 bits.
     */
    public static int signExtend8(int b) {
        Preconditions.checkBits8(b);
        b = (byte)b;
        return (int)b;
    }

    /**
     * Retourne une valeur égale à celle donnée, si ce n'est que les 8 bits de poids faible ont été renversés, c'est-à-dire que les bits d'index 0 et 7 ont été échangés, de même que ceux d'index 1 et 6, 2 et 5, et 3 et 4.
     * @param b : entier à manipuler. 
     * @return un entier suivant la manipulation décrite ci-dessus.
     * @throws IllegalArgumentException si la valeur donnée n'est pas une valeur de 8 bits.
     */
    public static int reverse8(int b) {
        Preconditions.checkBits8(b);
        int[] list = new int[] {
                0x00, 0x80, 0x40, 0xC0, 0x20, 0xA0, 0x60, 0xE0,
                0x10, 0x90, 0x50, 0xD0, 0x30, 0xB0, 0x70, 0xF0,
                0x08, 0x88, 0x48, 0xC8, 0x28, 0xA8, 0x68, 0xE8,
                0x18, 0x98, 0x58, 0xD8, 0x38, 0xB8, 0x78, 0xF8,
                0x04, 0x84, 0x44, 0xC4, 0x24, 0xA4, 0x64, 0xE4,
                0x14, 0x94, 0x54, 0xD4, 0x34, 0xB4, 0x74, 0xF4,
                0x0C, 0x8C, 0x4C, 0xCC, 0x2C, 0xAC, 0x6C, 0xEC,
                0x1C, 0x9C, 0x5C, 0xDC, 0x3C, 0xBC, 0x7C, 0xFC,
                0x02, 0x82, 0x42, 0xC2, 0x22, 0xA2, 0x62, 0xE2,
                0x12, 0x92, 0x52, 0xD2, 0x32, 0xB2, 0x72, 0xF2,
                0x0A, 0x8A, 0x4A, 0xCA, 0x2A, 0xAA, 0x6A, 0xEA,
                0x1A, 0x9A, 0x5A, 0xDA, 0x3A, 0xBA, 0x7A, 0xFA,
                0x06, 0x86, 0x46, 0xC6, 0x26, 0xA6, 0x66, 0xE6,
                0x16, 0x96, 0x56, 0xD6, 0x36, 0xB6, 0x76, 0xF6,
                0x0E, 0x8E, 0x4E, 0xCE, 0x2E, 0xAE, 0x6E, 0xEE,
                0x1E, 0x9E, 0x5E, 0xDE, 0x3E, 0xBE, 0x7E, 0xFE,
                0x01, 0x81, 0x41, 0xC1, 0x21, 0xA1, 0x61, 0xE1,
                0x11, 0x91, 0x51, 0xD1, 0x31, 0xB1, 0x71, 0xF1,
                0x09, 0x89, 0x49, 0xC9, 0x29, 0xA9, 0x69, 0xE9,
                0x19, 0x99, 0x59, 0xD9, 0x39, 0xB9, 0x79, 0xF9,
                0x05, 0x85, 0x45, 0xC5, 0x25, 0xA5, 0x65, 0xE5,
                0x15, 0x95, 0x55, 0xD5, 0x35, 0xB5, 0x75, 0xF5,
                0x0D, 0x8D, 0x4D, 0xCD, 0x2D, 0xAD, 0x6D, 0xED,
                0x1D, 0x9D, 0x5D, 0xDD, 0x3D, 0xBD, 0x7D, 0xFD,
                0x03, 0x83, 0x43, 0xC3, 0x23, 0xA3, 0x63, 0xE3,
                0x13, 0x93, 0x53, 0xD3, 0x33, 0xB3, 0x73, 0xF3,
                0x0B, 0x8B, 0x4B, 0xCB, 0x2B, 0xAB, 0x6B, 0xEB,
                0x1B, 0x9B, 0x5B, 0xDB, 0x3B, 0xBB, 0x7B, 0xFB,
                0x07, 0x87, 0x47, 0xC7, 0x27, 0xA7, 0x67, 0xE7,
                0x17, 0x97, 0x57, 0xD7, 0x37, 0xB7, 0x77, 0xF7,
                0x0F, 0x8F, 0x4F, 0xCF, 0x2F, 0xAF, 0x6F, 0xEF,
                0x1F, 0x9F, 0x5F, 0xDF, 0x3F, 0xBF, 0x7F, 0xFF,
        };
        return list[b];     
    }

    /**
     * Retourne une valeur égale à celle donnée, si ce n'est que les 8 bits de poids faible ont été inversés bit à bit, c'est-à-dire que les 0 et les 1 ont été échangés.
     * @param b : entier à manipuler.
     * @return un entier suivant la manipulation décrite ci-dessus.
     * @throws IllegalArgumentException si la valeur donnée n'est pas une valeur de 8 bits.
     */
    public static int complement8(int b) {
        Preconditions.checkBits8(b);
        return b ^ 0xFF;
    }

    /**
     * Retourne une valeur 16 bits dont les 8 bits de poids forts sont les 8 bits de poids faible de highB, et dont les 8 bits de poids faible sont ceux de lowB.
     * @param highB : entier qui donnera les 8 bits de poids forts de celui qui sera retourné.
     * @param lowB : entier qui donnera les 8 bits de poids faible de celui qui sera retourné.
     * @return "Combinaison" des deux arguments, comme décrit ci-dessus.
     * @throws IllegalArgumentException si l'une des deux valeurs données n'est pas une valeur de 8 bits.
     */
    public static int make16(int highB, int lowB) {
        Preconditions.checkBits8(highB);
        Preconditions.checkBits8(lowB);
        return highB << 8 | lowB;
    }
}
