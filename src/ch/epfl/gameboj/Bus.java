package ch.epfl.gameboj;

import ch.epfl.gameboj.Preconditions;

import java.util.ArrayList;
import java.util.Objects;

import ch.epfl.gameboj.component.Component;

public final class Bus {

    private final static int VALUE_TO_RETURN = 0xFF;
    private ArrayList<Component> components = new ArrayList<Component>();

    /**
     * Attache le composant donné au bus, ou lève l'exception NullPointerException si le composant vaut null.
     * @param component : Composant qu'il faut attacher au bus.
     */
    public void attach(Component component) {
        Objects.requireNonNull(component);
        components.add(component);
    }
    /**
     * Retourne la valeur stockée à l'adresse donnée si au moins un des composants attaché au bus possède une valeur à cette adresse, ou FF (en base 16) sinon; lève l'exception IllegalArgumentException si l'adresse n'est pas une valeur 16 bits.
     * @param address : valeur qui repère où lire dans la mémoire. 
     * @return valeur stockée à l'adresse donnée.
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
     * écrit la valeur "data" à l'adresse donnée dans tous les composants connectés au bus ; lève l'exception IllegalArgumentException si l'adresse n'est pas une valeur 16 bits ou si la donnée n'est pas une valeur 8 bits.
     * @param address : valeur qui repère où écrire dans la mémoire.
     * @param data : valeur à écrire à l'adresse donnée.
     */   
    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);
        for (Component component : components) {
            component.write(address, data);
        }
    }
}
