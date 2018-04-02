package ch.epfl.gameboj.component.memory;

import java.lang.Byte;
import java.util.Objects;

import ch.epfl.gameboj.Preconditions;


final public class Ram {
            
    private byte[] data;
    
    public Ram(int size) {
        Preconditions.checkArgument(size >= 0);
        data = new byte[size];
    }
    
    /**
     * Retourne la taille, en octets, de l'objet (Ram dans le cas présent).
     * @return La taille, en octets, de la Ram.
     */
    public int size() {
        return data.length;
    }
    
    /**
     * Lit l'octet se trouvant à l'index donné, sous la forme d'une valeur comprise entre 0 et FF (en base 16), ou lève l'exception IndexOutOfBoundsException si l'index est invalide.
     * @param index : index compris entre 0 (inclus) et la taille de la Ram (exclus).
     * @return L'octet se trouvant à l'index donné.
     */
    public int read(int index) {
        Objects.checkIndex(index, data.length);
        return Byte.toUnsignedInt(data[index]);
    }
    
    /**
     * Modifie le contenu de la mémoire à l'index donné pour qu'il soit égal à la valeur donnée ; lève l'exception IndexOutOfBoundsException si l'index est invalide, et l'exception IllegalArgumentException si la valeur n'est pas une valeur 8 bits.
     * @param index : index compris entre 0 (inclus) et la taille de la Ram (exclus).
     * @param value : valeur à écrire dans la mémoire.
     */
    public void write(int index, int value) {
        Objects.checkIndex(index, data.length);
        data[index] = (byte) Preconditions.checkBits8(value);

    }
}
