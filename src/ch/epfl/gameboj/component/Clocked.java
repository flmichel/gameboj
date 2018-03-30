package ch.epfl.gameboj.component;

public interface Clocked {

    /**
     * Fait évoluer un composant en exécutant toutes les opérations qu'il doit exécuter durant le cycle d'index donné en argument.
     * @param cycle : index repérant le cycle à exécuter.
     */
    void cycle(long cycle);

}
