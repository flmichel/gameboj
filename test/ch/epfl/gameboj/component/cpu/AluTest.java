package ch.epfl.gameboj.component.cpu;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AluTest {

    @Test
    void MaskZNHCtest() {       
        assertEquals(0b10100000, Alu.maskZNHC(true, false, true, false));
    }

    @Test
    void unpackValueTest() {       
        assertEquals(0b01001000, 
                Alu.unpackValue(0b0100100011111111));
    }

    @Test
    void addRetenue0Test() {       
        assertEquals(0b10100_0010_0000, 
                Alu.add(0b111, 0b1101, false));
    }

    @Test
    void addRetenue1Test() {       
        assertEquals(0b10101_0010_0000, 
                Alu.add(0b111, 0b1101, true));
    }

    @Test
    void add2argsTest() {       
        assertEquals(0b10100_0010_0000, 
                Alu.add(0b111, 0b1101));
    }

    @Test
    void add16LTest() {       
        assertEquals(0xE633_3_0, 
                Alu.add16L(0xC298, 0x239B));
    }

    @Test
    void add16HTestOverflow() {       
        assertEquals(0xBF2A_1_0, 
                Alu.add16H(0xFC81, 0xC2A9));
    }

    @Test
    void add16HTestNormal() {       
        assertEquals(0xCF2A_0_0, 
                Alu.add16H(0x0C81, 0xC2A9));
    }

    @Test
    void subEmprunt0Test() {       
        assertEquals(0b011111010_0111_0000, 
                Alu.sub(0b111, 0b1101, false));
    }

    @Test
    void subEmprunt1Test() {       
        assertEquals(0b011111001_0111_0000, 
                Alu.sub(0b111, 0b1101, true));
    }

    @Test
    void subEmprunt2ArgsTest() {       
        assertEquals(0b011111010_0111_0000, 
                Alu.sub(0b111, 0b1101));
    }

    @Test
    void bcdAdjustTest() {
        assertEquals(0b10010100_0000_0000, Alu.bcdAdjust(142, false, false, false));
        assertEquals(0x09_40,Alu.bcdAdjust(0x0F, true, true, false));
        assertEquals(0x73_00, Alu.bcdAdjust(0x6D, false, false, false));
    }

    @Test
    void andTest() {       
        assertEquals(0b0101_0010_0000, 
                Alu.and(0b111, 0b1101));
        assertEquals(0b0_1010_0000, 
                Alu.and(0b101, 0b010));
    }

    @Test
    void orTest() {       
        assertEquals(0b1111_0000_0000, 
                Alu.or(0b111, 0b1101));
        assertEquals(0b0_1000_0000, 
                Alu.or(0b0, 0b00));
    }

    @Test
    void xorTest() {       
        assertEquals(0b1010_0000_0000, 
                Alu.xor(0b111, 0b1101));
        assertEquals(0b0_1000_0000, 
                Alu.xor(0b111, 0b111));
    }

    @Test
    void shiftLeftTest() {       
        assertEquals(0b101010_0000_0000, 
                Alu.shiftLeft(0b10101));
        assertEquals(0b11101010_0001_0000, 
                Alu.shiftLeft(0b1111_0101));
        assertEquals(0b0_1001_0000, 
                Alu.shiftLeft(0b10000000));
    }

    @Test
    void shiftRightATest() {       
        assertEquals(0b01010_0001_0000, 
                Alu.shiftRightA(0b10101));
        assertEquals(0b0_1000_0000, 
                Alu.shiftRightA(0b0));
        assertEquals(0b1100_0000_0000_0000, 
                Alu.shiftRightA(0b1000_0000));
    }

    @Test
    void shiftRightLTest() {       
        assertEquals(0b01010_0001_0000, 
                Alu.shiftRightL(0b10101));
        assertEquals(0b0_1000_0000, 
                Alu.shiftRightL(0b0));
        assertEquals(0b100_0000_0000_0000, 
                Alu.shiftRightL(0b1000_0000));
    }

    @Test
    void rotateTest() {
        assertEquals(0b01010101_0000_0000, 
                Alu.rotate(Alu.RotDir.RIGHT, 0b10101010));
        assertEquals(0b01010101_0001_0000, 
                Alu.rotate(Alu.RotDir.LEFT, 0b10101010));
        assertEquals(0b0_1000_0000, 
                Alu.rotate(Alu.RotDir.LEFT, 0b0));
    }

    //    @Test
    //    void rotateWithBooleanTest() {
    //        assertEquals(0b01010101_0000_0000, 
    //                Alu.rotate(Alu.RotDir.RIGHT, 0b10101010));
    //        assertEquals(0b01010101_0001_0000, 
    //                Alu.rotate(Alu.RotDir.LEFT, 0b10101010));
    //        assertEquals(0b0_1000_0000, 
    //                Alu.rotate(Alu.RotDir.LEFT, 0b0));
    //    }

    @Test
    void swapTest() {   
        assertEquals(0x1000, Alu.swap(0x01));
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
