package ch.epfl.gameboj.component;

import ch.epfl.gameboj.Bus;

public interface Component {

    /**
     * Signale le fait qu'il n'y a aucune donnée à lire à l'adresse reçue.
     */
    public static final int NO_DATA = 0x100;
    
    /**
     * Retourne la valeur stockée à l'adresse donnée dans la condition que l'adresse soit valide.
     * @param address : valeur qui repère où lire la donnée. 
     * @return valeur stockée à l'adresse donnée.
     */
    int read(int address);
    
    /**
     * écrit la valeur "data" à l'adresse donnée dans la condition que l'adresse soit valide.
     * @param address : valeur qui repère où écrire dans la mémoire.
     * @param data : valeur à écrire à l'adresse donnée.
     */   
    void write(int address, int data);
    
    /**
     * Attache le composant au bus donné.
     * @param bus : Bus auquel il faut attacher le composant.
     * @throws NullPointerException si le bus donné vaut Null.
     */   
    default void attachTo(Bus bus) {
        bus.attach(this);
    }
    
}
