package ch.epfl.gameboj.component.memory;

import java.util.Arrays;
import java.util.Objects;
import java.lang.Byte;


final public class Rom {
            
    private byte[] data;
    
    public Rom(byte[] data) {
        Objects.requireNonNull(data);
        this.data = Arrays.copyOf(data, data.length);
    }
    
    /**
     * Retourne la taille, en octets, de l'objet (Rom dans le cas présent).
     * @return La taille, en octets, de la Rom.
     */
    public int size() {
        return data.length;
    }
    
    /**
     * Lit l'octet se trouvant à l'index donné, sous la forme d'une valeur comprise entre 0 et FF (en base 16), ou lève l'exception IndexOutOfBoundsException si l'index est invalide.
     * @param index : index compris entre 0 (inclus) et la taille de la Rom (exclus).
     * @return L'octet se trouvant à l'index donné.
     */
    public int read(int index) {
//        if (index < 0 || index > this.size())
//            throw new IndexOutOfBoundsException();
        Objects.checkIndex(index, this.size());
        return Byte.toUnsignedInt(data[index]);
    }
}
