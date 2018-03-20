package ch.epfl.gameboj.component.cpu;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;

public class FibonacciTest {

    private byte[] tab = new byte[] {
            (byte)0x31, (byte)0xFF, (byte)0xFF, (byte)0x3E,
            (byte)0x0B, (byte)0xCD, (byte)0x0A, (byte)0x00,
            (byte)0x76, (byte)0x00, (byte)0xFE, (byte)0x02,
            (byte)0xD8, (byte)0xC5, (byte)0x3D, (byte)0x47,
            (byte)0xCD, (byte)0x0A, (byte)0x00, (byte)0x4F,
            (byte)0x78, (byte)0x3D, (byte)0xCD, (byte)0x0A,
            (byte)0x00, (byte)0x81, (byte)0xC1, (byte)0xC9,
          };
    
    private Bus connect(Cpu cpu, Ram ram) {
        RamController rc = new RamController(ram, 0);
        Bus b = new Bus();
        cpu.attachTo(b);
        rc.attachTo(b);
        return b;
    }
    
    @Test
    void Fibo() {
        Cpu c= new Cpu();
        Ram r= new Ram(0xFFFF-1);
        Bus bus= connect(c,r);
        for (int i = 0; i < tab.length; i++) {
            bus.write(i, Byte.toUnsignedInt(tab[i]));
        }
        int count = 0;
        while (c._testGetPcSpAFBCDEHL()[0] != 8) {
            c.cycle(count);
            count++;
        }
        assertEquals(89, c._testGetPcSpAFBCDEHL()[2]);
    }
}
