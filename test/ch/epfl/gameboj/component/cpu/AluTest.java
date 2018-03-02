package ch.epfl.gameboj.component.cpu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class AluTest {
    
    @Test
    void swapTest() {   
        assertEquals(0x4000, Alu.swap(0x02));
    }
    
    @Test
    void swapTestZeroValue() {   
        assertEquals(0b1000_0000, Alu.swap(0x00));
    }
    
    @Test
    void testBitTest() {   
        assertEquals(0b0010_0000, Alu.testBit(0xFF, 5));
        assertEquals(0b1010_0000, Alu.testBit(0x00, 5));
    }
    
    @Test
    void testBitTestBadIndex() {   
        assertThrows(IndexOutOfBoundsException.class, () -> {Alu.testBit(0, -1);});
        assertThrows(IndexOutOfBoundsException.class, () -> {Alu.testBit(0, 8);});
    }    
}
