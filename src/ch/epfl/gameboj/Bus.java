package ch.epfl.gameboj;

import ch.epfl.gameboj.Preconditions;

import java.util.ArrayList;
import java.util.Objects;

import ch.epfl.gameboj.component.Component;

/**
 * Bus d'adresses et de données connectant les composants du Game Boy entre eux.
 * @author Riand Andre
 * @author Michel François
 */
public final class Bus {

    private final static int VALUE_TO_RETURN = 0xFF;
    private ArrayList<Component> components = new ArrayList<Component>();

    /**
     * Attache le composant donné au bus.
     * @param component : Composant qu'il faut attacher au bus.
     * @throws NullPointerException si le composant vaut null.
     */
    public void attach(Component component) {
        Objects.requireNonNull(component);
        components.add(component);
    }
    /**
     * Retourne la valeur stockée à l'adresse donnée si au moins un des composants attaché au bus possède une valeur à cette adresse, ou FF (en base 16) sinon.
     * @param address : valeur qui repère où lire dans la mémoire. 
     * @return valeur stockée à l'adresse donnée.
     * @throws IllegalArgumentException si l'adresse n'est pas une valeur 16 bits.
     */
    public int read(int address) {
        Preconditions.checkBits16(address);
        for (Component component : components) {
            if (component.read(address) != Component.NO_DATA)
                return component.read(address);        
        }
        return VALUE_TO_RETURN;
    }

    /**
     * écrit la valeur "data" à l'adresse donnée dans tous les composants connectés au bus.
     * @param address : valeur qui repère où écrire dans la mémoire.
     * @param data : valeur à écrire à l'adresse donnée.
     * @throws IllegalArgumentException si l'adresse n'est pas une valeur 16 bits ou si la donnée n'est pas une valeur 8 bits.
     */   
    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);
        for (Component component : components) {
            component.write(address, data);
        }
    }
}
