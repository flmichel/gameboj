package ch.epfl.gameboj.component.memory;

import java.util.Arrays;
import java.util.Objects;
import java.lang.Byte;

/**
 * Une mémoire morte.
 * @author Riand Andre
 * @author Michel François
 */
public final class Rom {

    private byte[] data;

    /**
     * Construit une mémoire morte dont le contenu et la taille sont ceux du tableau d'octets donné en argument.
     * @param data tableau d'octets dont le contenu et la taille sont à copier
     * @throws NullPointerException si le tableau "data" est null.
     */
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
     * Lit l'octet se trouvant à l'index donné, sous la forme d'une valeur comprise entre 0 et FF (en base 16).
     * @param index : index compris entre 0 (inclus) et la taille de la Rom (exclus).
     * @return L'octet se trouvant à l'index donné.
     * @throws IndexOutOfBoundsException si l'index est invalide.
     */
    public int read(int index) {
        Objects.checkIndex(index, this.size());
        return Byte.toUnsignedInt(data[index]);
    }
}
