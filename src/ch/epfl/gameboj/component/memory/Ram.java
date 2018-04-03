package ch.epfl.gameboj.component.memory;

import java.lang.Byte;
import java.util.Objects;

import ch.epfl.gameboj.Preconditions;

/**
 * Une mémoire vive.
 * @author Riand Andre
 *
 */
final public class Ram {
            
    private byte[] data;
    
    /**
     * Construit une nouvelle mémoire vive de taille donnée (en octets). 
     * @param size taille de la mémoire vive
     * @throws IllegalArgumentException si "size" est strictement négative.
     */
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
     * Lit l'octet se trouvant à l'index donné, sous la forme d'une valeur comprise entre 0 et FF (en base 16). 
     * @param index : index compris entre 0 (inclus) et la taille de la Ram (exclus).
     * @return L'octet se trouvant à l'index donné.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    public int read(int index) {
        Objects.checkIndex(index, data.length);
        return Byte.toUnsignedInt(data[index]);
    }
    
    /**
     * Modifie le contenu de la mémoire à l'index donné pour qu'il soit égal à la valeur donnée.
     * @param index : index compris entre 0 (inclus) et la taille de la Ram (exclus).
     * @param value : valeur à écrire dans la mémoire.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     * @throws IllegalArgumentException si la valeur n'est pas une valeur 8 bits.
     */
    public void write(int index, int value) {
        Objects.checkIndex(index, data.length);
        data[index] = (byte) Preconditions.checkBits8(value);

    }
}
