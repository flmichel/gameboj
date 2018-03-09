package ch.epfl.gameboj.component.cpu;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;

    public class CpuTest {

        private Bus connect(Cpu cpu, Ram ram) {
            RamController rc = new RamController(ram, 0);
            Bus b = new Bus();
            cpu.attachTo(b);
            rc.attachTo(b);
            return b;
        }

        private void cycleCpu(Cpu cpu, long cycles) {
            for (long c = 0; c < cycles; ++c)
                cpu.cycle(c);
        }

        @Test
        void LD_R8_HLRWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(20, 77);
            b.write(0, Opcode.LD_H_N8.encoding);
            b.write(1, 0);
            b.write(2, Opcode.LD_L_N8.encoding);
            b.write(3, 20);
            b.write(4, Opcode.LD_D_HLR.encoding);
            cycleCpu(c, 6);
            assertArrayEquals(new int[] { 5, 0, 0, 0, 0, 0, 77, 0, 0, 20 },
                    c._testGetPcSpAFBCDEHL());
        }

        @Test
        void LD_R8_HLRUWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(20, 77);
            b.write(0, Opcode.LD_H_N8.encoding);
            b.write(1, 0);
            b.write(2, Opcode.LD_L_N8.encoding);
            b.write(3, 20);
            b.write(4, Opcode.LD_A_HLRD.encoding);
            cycleCpu(c, 6);
            assertArrayEquals(new int[] { 5, 0, 77, 0, 0, 0, 0, 0, 0, 19 },
                    c._testGetPcSpAFBCDEHL());
        }

        @Test
        void LD_A_N8RWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(20 + 0xFF00, 77);
            b.write(0, Opcode.LD_A_N8R.encoding);
            b.write(1, 20);
            cycleCpu(c, 2);
            assertArrayEquals(new int[] { 2, 0, 77, 0, 0, 0, 0, 0, 0, 0 },
                    c._testGetPcSpAFBCDEHL());
        }

        @Test
        void LD_A_CRWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(55 + 0xFF00, 77);
            b.write(0, Opcode.LD_C_N8.encoding);
            b.write(1, 55);
            b.write(2, Opcode.LD_A_CR.encoding);
            cycleCpu(c, 4);
            assertArrayEquals(new int[] { 3, 0, 77, 0, 0, 55, 0, 0, 0, 0 },
                    c._testGetPcSpAFBCDEHL());
        }

        @Test
        void LD_A_N16RWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(0b0101010110101010, 77);
            b.write(0, Opcode.LD_A_N16R.encoding);
            b.write(1, 0b10101010);
            b.write(2, 0b01010101);
            cycleCpu(c, 2);
            assertArrayEquals(new int[] { 3, 0, 77, 0, 0, 0, 0, 0, 0, 0 },
                    c._testGetPcSpAFBCDEHL());
        }

        @Test
        void LD_A_BCRWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(20, 77);
            b.write(0, Opcode.LD_B_N8.encoding);
            b.write(1, 0);
            b.write(2, Opcode.LD_C_N8.encoding);
            b.write(3, 20);
            b.write(4, Opcode.LD_A_BCR.encoding);
            cycleCpu(c, 6);
            assertArrayEquals(new int[] { 5, 0, 77, 0, 0, 20, 0, 0, 0, 0 },
                    c._testGetPcSpAFBCDEHL());
        }

        @Test
        void LD_A_DERWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(20, 77);
            b.write(0, Opcode.LD_D_N8.encoding);
            b.write(1, 0);
            b.write(2, Opcode.LD_E_N8.encoding);
            b.write(3, 20);
            b.write(4, Opcode.LD_A_DER.encoding);
            cycleCpu(c, 6);
            assertArrayEquals(new int[] { 5, 0, 77, 0, 0, 0, 0, 20, 0, 0 },
                    c._testGetPcSpAFBCDEHL());
        }

        @Test
        void LD_R8_N8Works() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_B_N8.encoding);
            b.write(1, 100);
            b.write(2, Opcode.LD_C_N8.encoding);
            b.write(3, 101);
            b.write(4, Opcode.LD_D_N8.encoding);
            b.write(5, 102);
            b.write(6, Opcode.LD_E_N8.encoding);
            b.write(7, 103);
            b.write(8, Opcode.LD_H_N8.encoding);
            b.write(9, 104);
            b.write(10, Opcode.LD_L_N8.encoding);
            b.write(11, 105);
            b.write(12, Opcode.LD_A_N8.encoding);
            b.write(13, 106);
            cycleCpu(c, 14);
            assertArrayEquals(
                    new int[] { 14, 0, 106, 0, 100, 101, 102, 103, 104, 105 },
                    c._testGetPcSpAFBCDEHL());

        }

        @Test
        void LD_R16SP_N16Works() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_SP_N16.encoding);
            b.write(1, 65);
            b.write(2, 0);
            b.write(3, Opcode.LD_BC_N16.encoding);
            b.write(4, 66);
            b.write(5, 0);
            cycleCpu(c, 6);
            assertArrayEquals(new int[] { 6, 65, 0, 0, 0, 66, 0, 0, 0, 0 },
                    c._testGetPcSpAFBCDEHL());
        }

        @Test
        void POP_R16Works() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(65, 200);
            b.write(0, Opcode.LD_SP_N16.encoding);
            b.write(1, 65);
            b.write(2, 0);
            b.write(3, Opcode.POP_BC.encoding);
            cycleCpu(c, 4);
            assertArrayEquals(new int[] { 4, 67, 0, 0, 0, 200, 0, 0, 0, 0 },
                    c._testGetPcSpAFBCDEHL());
        }

        @Test
        void LD_HLR_R8Works() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_H_N8.encoding);
            b.write(1, 0);
            b.write(2, Opcode.LD_L_N8.encoding);
            b.write(3, 20);
            b.write(4, Opcode.LD_C_N8.encoding);
            b.write(5, 101);
            b.write(6, Opcode.LD_HLR_C.encoding);
            cycleCpu(c, 7);
            assertArrayEquals(new int[] { 7, 0, 0, 0, 0, 101, 0, 0, 0, 20 },
                    c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(20), 101, 0.01);
        }

        @Test
        void LD_HLRU_AWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_H_N8.encoding);
            b.write(1, 0);
            b.write(2, Opcode.LD_L_N8.encoding);
            b.write(3, 20);
            b.write(4, Opcode.LD_A_N8.encoding);
            b.write(5, 101);
            b.write(6, Opcode.LD_HLRI_A.encoding);
            cycleCpu(c, 7);
            assertArrayEquals(new int[] { 7, 0, 101, 0, 0, 0, 0, 0, 0, 21 },
                    c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(20), 101, 0.01);
        }

        @Test
        void LD_N8R_AWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(102 + 0xFF00, 66);
            b.write(0, Opcode.LD_A_N8R.encoding);
            b.write(1, 102);
            b.write(2, Opcode.LD_N8R_A.encoding);
            b.write(3, 101);
            cycleCpu(c, 4);
            assertArrayEquals(new int[] { 4, 0, 66, 0, 0, 0, 0, 0, 0, 0 },
                    c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(101 + 0xFF00), 66, 0.01);
        }

        @Test
        void LD_CR_AWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(102 + 0xFF00, 66);
            b.write(0, Opcode.LD_A_N8R.encoding);
            b.write(1, 102);
            b.write(2, Opcode.LD_C_N8.encoding);
            b.write(3, 101);
            b.write(4, Opcode.LD_CR_A.encoding);
            cycleCpu(c, 6);
            assertArrayEquals(new int[] { 5, 0, 66, 0, 0, 101, 0, 0, 0, 0 },
                    c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(101 + 0xFF00), 66, 0.01);
        }

        @Test
        void LD_BCR_A_AWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(102 + 0xFF00, 66);
            b.write(0, Opcode.LD_A_N8R.encoding);
            b.write(1, 102);
            b.write(2, Opcode.LD_C_N8.encoding);
            b.write(3, 101);
            b.write(4, Opcode.LD_BCR_A.encoding);
            cycleCpu(c, 6);
            assertArrayEquals(new int[] { 5, 0, 66, 0, 0, 101, 0, 0, 0, 0 },
                    c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(101), 66, 0.01);
        }

        @Test
        void LD_DER_A_AWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(102 + 0xFF00, 66);
            b.write(0, Opcode.LD_A_N8R.encoding);
            b.write(1, 102);
            b.write(2, Opcode.LD_E_N8.encoding);
            b.write(3, 101);
            b.write(4, Opcode.LD_DER_A.encoding);
            cycleCpu(c, 6);
            assertArrayEquals(new int[] { 5, 0, 66, 0, 0, 0, 0, 101, 0, 0 },
                    c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(101), 66, 0.01);
        }

        @Test
        void LD_HLR_N8_AWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_H_N8.encoding);
            b.write(1, 0b101);
            b.write(2, Opcode.LD_L_N8.encoding);
            b.write(3, 0b101);
            b.write(4, Opcode.LD_HLR_N8.encoding);
            b.write(5, 127);
            cycleCpu(c, 7);
            assertArrayEquals(new int[] { 6, 0, 0, 0, 0, 0, 0, 0, 5, 5 },
                    c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(0b10100000101), 127, 0.01);
        }

        @Test
        void LD_N16R_SPWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_SP_N16.encoding);
            b.write(1, 0b11111111);
            b.write(2, 0b111);
            b.write(3, Opcode.LD_N16R_SP.encoding);
            b.write(4, 0b101);
            b.write(5, 0b101);
            cycleCpu(c, 7);
            assertArrayEquals(
                    new int[] { 6, 0b11111111111, 0, 0, 0, 0, 0, 0, 0, 0 },
                    c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(0b10100000101), 0b11111111, 0.01);
            assertEquals(b.read(0b10100000110), 0b111, 0.01);
        }

        @Test
        void LD_R8_R8Works() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_H_N8.encoding);
            b.write(1, 0b101);
            b.write(2, Opcode.LD_L_H.encoding);
            cycleCpu(c, 3);
            assertArrayEquals(new int[] { 3, 0, 0, 0, 0, 0, 0, 0, 5, 5 },
                    c._testGetPcSpAFBCDEHL());
        }

        @Test
        void LD_SP_HLWorks() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_H_N8.encoding);
            b.write(1, 233);
            b.write(2, Opcode.LD_L_N8.encoding);
            b.write(3, 0b101);
            b.write(4, Opcode.LD_SP_HL.encoding);
            cycleCpu(c, 5);
            assertArrayEquals(new int[] { 5, 59653, 0, 0, 0, 0, 0, 0, 233, 5 },
                    c._testGetPcSpAFBCDEHL());
        }

        @Test
        void PUSH_R16Works() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_H_N8.encoding);
            b.write(1, 0);
            b.write(2, Opcode.LD_L_N8.encoding);
            b.write(3, 45);
            b.write(4, Opcode.LD_SP_HL.encoding);
            b.write(5, Opcode.LD_B_N8.encoding);
            b.write(6, 0);
            b.write(7, Opcode.LD_C_N8.encoding);
            b.write(8, 65);
            b.write(9, Opcode.PUSH_BC.encoding);
            cycleCpu(c, 11);
            assertArrayEquals(new int[] { 10, 43, 0, 0, 0, 65, 0, 0, 0, 45 },
                    c._testGetPcSpAFBCDEHL());
            assertEquals(b.read(43), 65, 0.01);
        }

        private Bus connect1(Cpu cpu, Ram ram) {
            RamController rc = new RamController(ram, 0);
            Bus b = new Bus();
            cpu.attachTo(b);
            rc.attachTo(b);
            return b;
        }

        private void cycleCpu1(Cpu cpu, long cycles) {
            for (long c = 0; c < cycles; ++c)
                cpu.cycle(c);
        }

        @Test
        void nopDoesNothing() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.NOP.encoding);
            cycleCpu(c, Opcode.NOP.cycles);
            assertArrayEquals(new int[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                    c._testGetPcSpAFBCDEHL());
        }

     

        @Test
        void LD_A_N8_Test() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_A_N8.encoding);
            b.write(1, 0x0A);
            b.write(2, Opcode.LD_B_A.encoding);
            b.write(3, Opcode.LD_C_B.encoding);
            cycleCpu(c, Opcode.LD_A_N8.cycles + Opcode.LD_B_A.cycles
                    + Opcode.LD_C_B.cycles);
            assertArrayEquals(new int[] { 4, 0, 0x0A, 0, 0x0A, 0x0A, 0, 0, 0, 0 },
                    c._testGetPcSpAFBCDEHL());
        }

        @Test
        void LD_B_N8_Test() {
            Cpu c = new Cpu();
            Ram r = new Ram(10);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_B_N8.encoding);
            b.write(1, 0x0B);
            cycleCpu(c, Opcode.LD_B_N8.cycles);
            assertArrayEquals(new int[] { 2, 0, 0, 0, 0x0B, 0, 0, 0, 0, 0 },
                    c._testGetPcSpAFBCDEHL());
        }
  
}
