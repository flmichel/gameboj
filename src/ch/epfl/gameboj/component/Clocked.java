package ch.epfl.gameboj.component;

/**
 * Représente un composant piloté par l'horloge du système
 * @author Riand Andre
 * @author Michel François
 */
public interface Clocked {

    /**
     * Fait évoluer un composant en exécutant toutes les opérations qu'il doit exécuter durant le cycle d'index donné en argument.
     * @param cycle : index repérant le cycle à exécuter.
     */
    void cycle(long cycle);

}
