package ch.epfl.gameboj;

import ch.epfl.gameboj.Preconditions;

import java.util.ArrayList;
import java.util.Objects;

import ch.epfl.gameboj.component.Component;

public final class Bus {

    private ArrayList<Component> components = new ArrayList<Component>();

    public void attach(Component component) {
        Objects.requireNonNull(component);
        components.add(component);
    }
    
    public int read(int address) {
        Preconditions.checkBits16(address);
        for (Component component : components) {
            if (component.read(address) != Component.NO_DATA)
                return component.read(address);        
        }
        return 0xff;
    }
    
    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);
        for (Component component : components) {
            component.write(address, data);
        }
    }
}
