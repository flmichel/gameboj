package ch.epfl.gameboj;

import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.bits.Bits;

public final class RegisterFile<E extends Register> {
    
    E[] allRegs;
    byte[] values;
    
    public RegisterFile(E[] allRegs) {
        this.allRegs = allRegs;
        values = new byte[allRegs.length];
    }
    
    /**
     * Retourne la valeur 8 bits contenue dans le registre donné (argument reg), sous la forme d'un entier compris entre 0 (inclus) et FF (base 16) (inclus).
     * @param reg : registre à utiliser.
     * @return la valeur contenue dans le registre.
     */
    public int get(E reg) {
        return Byte.toUnsignedInt(values[reg.index()]);
    }
    
    /**
     * Modifie le contenu du registre donné pour qu'il soit égal à la valeur 8 bits donnée ; lève IllegalArgumentException si la valeur n'est pas une valeur 8 bits valide.
     * @param reg : registre à modifier le contenu.
     * @param newValue : nouvelle valeur à etre stockée dans le registre.
     */
    public void set(E reg, int newValue) {
        Preconditions.checkBits8(newValue);
        values[reg.index()] = (byte) newValue;
    }
    
    /**
     * Retourne true si et seulement si le bit donné du registre donné vaut 1.
     * @param reg : registre à analyser
     * @param b : on obtient l'index à tester grace à ce bit.
     * @return true si et seulement si le bit donné du registre donné vaut 1.
     */
    public boolean testBit(E reg, Bit b) {
        return Bits.test(values[reg.index()], b);
    }
    
    /**
     * Modifie la valeur stockée dans le registre donné pour que le bit donné ait la nouvelle valeur donnée.
     * @param reg : registre à modifier.
     * @param bit : bit à modifier.
     * @param newValue : nouvelle valeur du bit en question.
     */
    public void setBit(E reg, Bit bit, boolean newValue) {
        values[reg.index()] = (byte) Bits.set(values[reg.index()], bit.index(), newValue);
    }
}
