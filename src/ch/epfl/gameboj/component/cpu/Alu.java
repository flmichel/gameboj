package ch.epfl.gameboj.component.cpu;

import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.bits.Bit;

import java.util.Objects;

import ch.epfl.gameboj.Preconditions;

public final class Alu {

    private Alu() {}

    public enum Flag implements Bit {
        UNUSED_0, UNUSED_1, UNUSED_2, UNUSED_3, C, H, N, Z
    }

    public enum RotDir {
        LEFT, RIGHT
    }

    private static int packValueZNHC(int v, boolean z, boolean n, boolean h, boolean c) {
        return v << 8 | maskZNHC(z, n, h, c);
    }

    /**
     * Retourne une valeur dont les bits correspondant aux différents fanions valent 1 si et seulement si l'argument correspondant est vrai.
     * @param z : valeur du fanion z (true si 1, false si 0).
     * @param n : valeur du fanion n (true si 1, false si 0).
     * @param h : valeur du fanion h (true si 1, false si 0).
     * @param c : valeur du fanion c (true si 1, false si 0).
     * @return entier suivant la description ci-dessus.
     */
    public static int maskZNHC(boolean z, boolean n, boolean h, boolean c) {
        int mask = 0;
        if (z) mask += Flag.Z.mask();
        if (n) mask += Flag.N.mask();
        if (h) mask += Flag.H.mask();
        if (c) mask += Flag.C.mask();
        return mask;
    }

    /**
     * Retourne la valeur contenue dans le paquet valeur/fanion donné.
     * @param valueFlags : entier correspondant à un paquet valeur/fanion.
     * @return entier correspondant à la valeur contenue dans le paquet valeur/fanion donné.
     */
    public static int unpackValue(int valueFlags) {
        return Bits.extract(valueFlags, 8, 16);
    }

    /**
     * Retourne les fanions contenus dans le paquet valeur/fanion donné.
     * @param valueFlags : entier correspondant à un paquet valeur/fanion.
     * @return entier correspondant au fanion contenu dans le paquet valeur/fanion donné.
     */
    public static int unpackFlags(int valueFlags) {
        return Bits.clip(8, valueFlags);
    }

    /**
     * Retourne la somme des deux valeurs 8 bits données et du bit de retenue initial c0, et les fanions Z0HC.
     * @param l : premiere valeur à additionner.
     * @param r : deuxieme valeur à additionner.
     * @param c0 : retenue initiale.
     * @return somme des arguments, et fanions correspondants.
     */
    public static int add(int l, int r, boolean c0) {
        Preconditions.checkBits8(l);
        Preconditions.checkBits8(r);

        boolean z = false, h = false, c = false;
        int value = l+r;
        int valueBits4 = Bits.clip(4, l) + Bits.clip(4, r);

        if (c0) {
            value++;
            valueBits4++;
        }

        if (Bits.test(valueBits4, 4)) h = true;
        if (Bits.test(value, 8)) {
            c = true;
            value = Bits.set(value, 8, false);
        }
        if (value == 0) z = true;

        return packValueZNHC(value, z, false, h, c);
    }

    /**
     * Retourne la somme des deux valeurs 8 bits données, et les fanions Z0HC.
     * @param l : premiere valeur à additionner.
     * @param r : deuxieme valeur à additionner.
     * @return somme des arguments, et fanions correspondants.
     */
    public static int add(int l, int r) {
        return add(l, r, false);
    }

    private static int add16(int l, int r, boolean low) {
        Preconditions.checkBits16(l);
        Preconditions.checkBits16(r);

        boolean c = false;
        int lowB = add(Bits.clip(8, r), Bits.clip(8, l));

        if (Bits.test(lowB, Flag.C.index())) c = true;

        int highB = add(Bits.extract(l, 8, 8), Bits.extract(r, 8, 8), c);

        int flagB;
        if (low) flagB = lowB;
        else flagB = highB;

        int flags = Bits.set(unpackFlags(flagB), Flag.Z.index(), false);

        return Bits.make16(unpackValue(highB), unpackValue(lowB)) << 8 | flags;
    }

    /**
     * Retourne la somme des deux valeurs 16 bits données et les fanions 00HC, où H et C sont les fanions correspondant à l'addition des 8 bits de poids faible.
     * @param l : premiere valeur à additionner.
     * @param r : deuxieme valeur à additionner.
     * @return somme des arguments, et fanions correspondants.
     */
    public static int add16L(int l, int r) {
        return add16(l, r, true);
    }

    /**
     * Retourne la somme des deux valeurs 16 bits données et les fanions 00HC, où H et C sont les fanions correspondant à l'addition des 8 bits de poids forts.
     * @param l : premiere valeur à additionner.
     * @param r : deuxieme valeur à additionner.
     * @return somme des arguments, et fanions correspondants.
     */
    public static int add16H(int l, int r) {
        return add16(l, r, false);
    }

    /**
     * Retourne la différence des valeurs de 8 bits données et du bit d'emprunt initial b0, et les fanions Z1HC.
     * @param l : valeur initiale
     * @param r : valeur à soustraire à "l"
     * @param b0 : emprunt initial
     * @return la différence des arguments, et fanions correspondants.
     */
    public static int sub(int l, int r, boolean b0) {
        Preconditions.checkBits8(l);
        Preconditions.checkBits8(r);
        int borrow = 0;
        if (b0) borrow = 1;
        boolean c = l - borrow < r;
        boolean h = Bits.clip(4, l) - borrow < Bits.clip(4, r);
        int value = Bits.clip(8, l - r - borrow);
        boolean z = (value == 0);

        return packValueZNHC(value, z, true, h, c);    
    }

    /**
     * Retourne la différence des valeurs de 8 bits données, et les fanions Z1HC.
     * @param l : valeur initiale
     * @param r : valeur à soustraire à "l"
     * @return la différence des arguments, et fanions correspondants.
     */
    public static int sub(int l, int r) {
        return sub(l, r, false);
    }

    /**
     * Ajuste la valeur 8 bits donnée en argument afin qu'elle soit au format DCB.
     * @param v : entier à ajuster.
     * @param n : fanion n
     * @param h : fanion h
     * @param c : fanion c
     * @return la valeur donnée en format DCB.
     */
    public static int bcdAdjust(int v, boolean n, boolean h, boolean c) {
        Preconditions.checkBits8(v);
        boolean fixL = h || (!n && Bits.clip(4, v) > 9);
        boolean fixH = c || (!n && v > 0x99);
        int fix = 0;
        if (fixL) fix += 0x06;
        if (fixH) fix += 0x60;
        int val;
        if (n) val = v - fix;
        else val = v + fix;

        return packValueZNHC(Bits.clip(8, val), Bits.clip(8, val) == 0, n, false, fixH);
    }

    /**
     * Retourne le « et » bit à bit des deux valeurs 8 bits données et les fanions Z010.
     * @param l : entier à utiliser
     * @param r : entier à utiliser
     * @return entier dont les bits proviennent du "et" entre les bits de "l" et "r", et fanions correspondants.
     */
    public static int and(int l, int r) {
        Preconditions.checkBits8(l);
        Preconditions.checkBits8(r);
        int val = l & r;
        return packValueZNHC(val, val == 0, false, true, false);
    }

    /**
     * Retourne le « ou inclusif » bit à bit des deux valeurs 8 bits données et les fanions Z000.
     * @param l : entier à utiliser
     * @param r : entier à utiliser
     * @return entier dont les bits proviennent du "ou inclusif" entre les bits de "l" et "r", et fanions correspondants.
     */
    public static int or(int l, int r) {
        Preconditions.checkBits8(l);
        Preconditions.checkBits8(r);
        int val = l | r;
        return packValueZNHC(val, val == 0, false, false, false);
    }

    /**
     * Retourne le « ou exclusif » bit à bit des deux valeurs 8 bits données et les fanions Z000.
     * @param l : entier à utiliser
     * @param r : entier à utiliser
     * @return entier dont les bits proviennent du "ou exclusif" entre les bits de "l" et "r", et fanions correspondants.
     */
    public static int xor(int l, int r) {
        Preconditions.checkBits8(l);
        Preconditions.checkBits8(r);
        int val = l ^ r;
        return packValueZNHC(val, val == 0, false, false, false);
    }

    /**
     * Retourne la valeur 8 bits donnée décalée à gauche d'un bit, et les fanions Z00C où le fanion C contient le bit éjecté par le décalage (c'est-à-dire que C est vrai si et seulement si le bit en question valait 1).
     * @param v : entier à effectuer le décalage
     * @return entier apres décalage, et fanions correspondants.
     */
    public static int shiftLeft(int v) {
        Preconditions.checkBits8(v);
        int val = v << 1;
        val = Bits.clip(8, val);
        return packValueZNHC(val, val == 0, false, false, Bits.test(v, 7));
    }

    /**
     * Retourne la valeur 8 bits donnée décalée droite d'un bit, de manière arithmétique, et les fanions Z00C où le fanion C contient le bit éjecté par le décalage (c'est-à-dire que C est vrai si et seulement si le bit en question valait 1).
     * @param v : entier à effectuer le décalage
     * @return entier apres décalage, et fanions correspondants.
     */
    public static int shiftRightA(int v) {
        Preconditions.checkBits8(v);
        int val = (byte)v >> 1;
        val = Bits.clip(8, val);
        return packValueZNHC(val, val == 0, false, false, Bits.test(v, 0));
    }

    /**
     * Retourne la valeur 8 bits donnée décalée droite d'un bit, de manière logique, et les fanions Z00C où le fanion C contient le bit éjecté par le décalage (c'est-à-dire que C est vrai si et seulement si le bit en question valait 1).
     * @param v : entier à effectuer le décalage
     * @return entier apres décalage, et fanions correspondants.
     */
    public static int shiftRightL(int v) {
        Preconditions.checkBits8(v);
        int val = v >>> 1;
        val = Bits.clip(8, val);
        return packValueZNHC(val, val == 0, false, false, Bits.test(v, 0)); 
    }

    /**
     * Retourne la rotation de la valeur 8 bits donnée, d'une distance de un bit dans la direction donnée, et les fanions Z00C où C contient le bit qui est passé d'une extrémité à l'autre lors de la rotation.
     * @param d : direction de rotation
     * @param v : entier à subir la rotation
     * @return entier apres rotation, et fanions correspondants.
     */
    public static int rotate(RotDir d, int v) {
        Preconditions.checkBits8(v);
        int dir, index;
        if (d.name() == "LEFT") {
            dir = 1;
            index = 0;
        } else {
            dir = -1;
            index = 7;
        }
        v = Bits.rotate(8, v, dir);
        return packValueZNHC(v, v == 0, false, false, Bits.test(v, index));
    }

    /**
     * Retourne la rotation à travers la retenue, dans la direction donnée, de la combinaison de la valeur 8 bits et du fanion de retenue donnés, ainsi que les fanions Z00C. Cette opération consiste à construire une valeur 9 bits à partir de la retenue et de la valeur 8 bits, la faire tourner dans la direction donnée, puis retourner les 8 bits de poids faible comme résultat, et le bit de poids le plus fort comme nouvelle retenue (fanion C).
     * @param d : direction de la rotation
     * @param v : entier à subir la rotation
     * @param c : fanion c
     * @return Entier suivant le procédé expliqué ci-dessus, et fanions correspondants.
     */
    public static int rotate(RotDir d, int v, boolean c) {
        Preconditions.checkBits8(v);
        int dir;
        if (d.name() == "LEFT") dir = 1;
        else dir = -1;

        if (c) v = Bits.set(v, 8, true);
        v = Bits.rotate(9, v, dir);
        int val = Bits.clip(8, v);
        return packValueZNHC(val, val == 0, false, false, Bits.test(v, 8));

    }

    /**
     * Retourne la valeur obtenue en échangeant les 4 bits de poids faible et de poids fort de la valeur 8 bits donnée, et les fanions Z000.
     * @param v : entier à manipuler.
     * @return entier apres manipulation et fanions correspondants.
     */
    public static int swap(int v) {
        Preconditions.checkBits8(v);
        v = Bits.clip(4, v) << 4 | Bits.extract(v, 4, 4);
        return packValueZNHC(v, v == 0, false, false, false);
    }

    /**
     * Retourne la valeur 0 et les fanions Z010 où Z est vrai si et seulement si le bit d'index donné de la valeur 8 bits donnée vaut 0 ; en plus de la validation de la valeur 8 bits reçue, cette méthode valide l'index reçu et lève IndexOutOfBoundsException s'il n'est pas compris entre 0 et 7.
     * @param v : valeur 8 bits
     * @param bitIndex : index compris entre 0 et 7 (inclus).
     * @return 0 si la condition décrite ci-dessus est remplie, et fanions correspondants.
     */
    public static int testBit(int v, int bitIndex) {
        Preconditions.checkBits8(v);
//        if (bitIndex < 0 | bitIndex >= 8)
//            throw new IndexOutOfBoundsException();
        Objects.checkIndex(bitIndex, 8);
        return packValueZNHC(0, !Bits.test(v, bitIndex), false, true, false);     
    } 
}
