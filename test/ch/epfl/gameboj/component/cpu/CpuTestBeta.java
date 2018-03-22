package ch.epfl.gameboj.component.cpu;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.component.memory.Ram;
import ch.epfl.gameboj.component.memory.RamController;

    public class CpuTestBeta {

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
/*
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
       */ 
//      //*******************TEST STEP 4**************************
        
        
        
        
//      //Add
//      
//      @Test
//      void ADD_A_R8WorksE() {
//          Cpu c = new Cpu();
//          Ram r = new Ram(0xffff -1);
//          Bus b = connect(c, r);
//          b.write(0,  Opcode.LD_A_N8.encoding);
//          b.write(1,  25);
//          b.write(2, Opcode.LD_C_N8.encoding);
//          b.write(3, 45);
//          b.write(5,  Opcode.ADD_A_C.encoding);
//          cycleCpu(c, 6);
//          assertArrayEquals(new int [] {6, 0, 70, 0b0100000, 0, 45, 0, 0, 0, 0}, c._testGetPcSpAFBCDEHL());
//      }
//      
//      @Test 
//      void ADD_A_N8WorksE() {
//          Cpu c = new Cpu();
//          Ram r = new Ram(0xffff -1);
//          Bus b = connect(c, r);
//          b.write(0,  Opcode.LD_A_N8.encoding);
//          b.write(1,  68);
//          b.write(2,  Opcode.ADD_A_N8.encoding);
//          b.write(3,  2);
//          cycleCpu(c, 6);
//          assertArrayEquals(new int [] {6, 0, 70, 0b0000, 0, 0, 0, 0, 0, 0}, c._testGetPcSpAFBCDEHL());        
//      }
//      
//      
//      @Test
//      void ADD_A_HLRWorksE() {
//          Cpu c = new Cpu();
//          Ram r = new Ram(0xffff-1);
//          Bus b = connect(c, r);
//          b.write(0,  Opcode.LD_L_N8.encoding);
//          b.write(1,  120);
//          b.write(2,  Opcode.LD_A_N8.encoding);
//          b.write(3,  4);
//          b.write(120, 43);
//          b.write(4,  Opcode.ADD_A_HLR.encoding);
//          cycleCpu(c, 7);
//          assertArrayEquals(new int [] {6, 0, 47, 0b0000000, 0, 0, 0, 0, 0, 120}, c._testGetPcSpAFBCDEHL());
//      }
//      
//      @Test
//      void INC_R8WorksE() {
//          Cpu c = new Cpu();
//          Ram r = new Ram(0xffff-1);
//          Bus b = connect(c, r);
//          b.write(0,  Opcode.LD_E_N8.encoding);
//          b.write(1,  44);
//          b.write(2,  Opcode.INC_E.encoding);
//          cycleCpu(c, 3);
//          assertArrayEquals(new int [] {3, 0, 0, 0b00000000, 0, 0, 0, 45, 0, 0}, c._testGetPcSpAFBCDEHL());  
//          }
//      @Test 
//      void ADD_A_R8WorksB() {
//          Cpu c= new Cpu();
//          Ram r= new Ram(0xFFFF-1);
//          Bus bus= connect(c,r);
//          bus.write(0,Opcode.LD_A_N8.encoding);
//          bus.write(1,40);
//          bus.write(2, Opcode.LD_B_N8.encoding);
//          bus.write(3, 2);
//          bus.write(5,  Opcode.ADD_A_B.encoding);
//          bus.write(6, Opcode.ADD_A_A.encoding);
//          cycleCpu(c,7);
//          assertArrayEquals(new int[] {7,0,84,0b000100000,2,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//      }
//      
//      @Test
//      void ADD_A_N8WorksB() {
//          Cpu c= new Cpu();
//          Ram r= new Ram(0xFFFF-1);
//          Bus bus= connect(c,r);
//          bus.write(0, Opcode.LD_A_N8.encoding);
//          bus.write(1, 17);
//          bus.write(2, Opcode.ADD_A_N8.encoding);
//          bus.write(3, 13);
//          cycleCpu(c,4);
//          assertArrayEquals(new int[] {4,0,30,0b0000000,0,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//      }
//      
//      @Test
//      void ADD_A_HLRWorksB() {
//          Cpu c= new Cpu();
//          Ram r= new Ram(0xFFFF-1);
//          Bus bus= connect(c,r);
//          bus.write(0, Opcode.LD_L_N8.encoding);
//          bus.write(1, 42);
//          bus.write(2, Opcode.LD_H_N8.encoding);
//          bus.write(3, 0);
//          bus.write(4, Opcode.LD_A_N8.encoding);
//          bus.write(5, 10);
//          bus.write(6, Opcode.ADD_A_HLR.encoding);
//          bus.write(42, 3);
//          cycleCpu(c,10);
//          assertArrayEquals(new int[] {9,0,13,0,0,0,0,0,0,42},c._testGetPcSpAFBCDEHL());
//      }
//      
//      @Test
//      void INC_R8WorksB() {
//          Cpu c= new Cpu();
//          Ram r= new Ram(0xFFFF-1);
//          Bus bus= connect(c,r);
//          bus.write(0, Opcode.LD_H_N8.encoding);
//          bus.write(1, 3);
//          bus.write(2,Opcode.INC_H.encoding);
//          cycleCpu(c,3);
//          assertArrayEquals(new int[] {3,0,0,0b0000,0,0,0,0,4,0},c._testGetPcSpAFBCDEHL());
//      }
//      
//
//      
//      
//      
//      //sub
//      
//      
//
//      
//      //And, or, xor, complement
//      
//      @Test
//      void AND_A_N8WorksB() {
//          Cpu c= new Cpu();
//          Ram r= new Ram(0xFFFF-1);
//          Bus bus= connect(c,r);
//          bus.write(0,Opcode.LD_A_N8.encoding);
//          bus.write(1,0b10001101);
//          bus.write(2, Opcode.LD_B_N8.encoding);
//          bus.write(3, 0b10100001);
//          bus.write(4, Opcode.AND_A_B.encoding);
//          cycleCpu(c,5);
//          assertArrayEquals(new int[] {5,0,0b10000001,0b0100000,0b10100001,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//      }
//      
//      @Test
//      void AND_A_R8WorksB() {
//          Cpu c= new Cpu();
//          Ram r= new Ram(0xFFFF-1);
//          Bus bus= connect(c,r);
//          bus.write(0,Opcode.LD_A_N8.encoding);
//          bus.write(1,0b10001101);
//          bus.write(2, Opcode.AND_A_N8.encoding);
//          bus.write(3, 0b10100001);
//          cycleCpu(c,5);
//          assertArrayEquals(new int[] {5,0,0b10000001,0b0100000,0b0,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//      }
//      
//      @Test
//      void OR_A_N8WorksB() {
//          Cpu c= new Cpu();
//          Ram r= new Ram(0xFFFF-1);
//          Bus bus= connect(c,r);
//          bus.write(0,Opcode.LD_A_N8.encoding);
//          bus.write(1,0b10001101);
//          bus.write(2, Opcode.LD_B_N8.encoding);
//          bus.write(3, 0b10100001);
//          bus.write(4, Opcode.OR_A_B.encoding);
//          cycleCpu(c,5);
//          assertArrayEquals(new int[] {5,0,0b10101101,0b0000000,0b10100001,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//      }
//      
//      @Test
//      void OR_A_R8WorksB() {
//          Cpu c= new Cpu();
//          Ram r= new Ram(0xFFFF-1);
//          Bus bus= connect(c,r);
//          bus.write(0,Opcode.LD_A_N8.encoding);
//          bus.write(1,0b10001101);
//          bus.write(2, Opcode.OR_A_N8.encoding);
//          bus.write(3, 0b10100001);
//          cycleCpu(c,5);
//          assertArrayEquals(new int[] {5,0,0b10101101,0b0000000,0b0,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//      }
//      
//      @Test
//      void XOR_A_N8WorksB() {
//          Cpu c= new Cpu();
//          Ram r= new Ram(0xFFFF-1);
//          Bus bus= connect(c,r);
//          bus.write(0,Opcode.LD_A_N8.encoding);
//          bus.write(1,0b10001101);
//          bus.write(2, Opcode.LD_B_N8.encoding);
//          bus.write(3, 0b10100001);
//          bus.write(4, Opcode.XOR_A_B.encoding);
//          cycleCpu(c,5);
//          assertArrayEquals(new int[] {5,0,0b00101100,0b0000000,0b10100001,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//      }
//      
//      @Test
//      void XOR_A_R8WorksB() {
//          Cpu c= new Cpu();
//          Ram r= new Ram(0xFFFF-1);
//          Bus bus= connect(c,r);
//          bus.write(0,Opcode.LD_A_N8.encoding);
//          bus.write(1,0b10001101);
//          bus.write(2, Opcode.XOR_A_N8.encoding);
//          bus.write(3, 0b10100001);
//          cycleCpu(c,5);
//          assertArrayEquals(new int[] {5,0,0b00101100,0b0000000,0b0,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//      }
//      
//      @Test
//      void AND_A_HLRWorksB() {
//          Cpu c = new Cpu();
//          Ram r = new Ram(0xffff-1);
//          Bus bus = connect(c, r);
//          bus.write(0,  Opcode.LD_L_N8.encoding);
//          bus.write(1,  120);
//          bus.write(2,  Opcode.LD_A_N8.encoding);
//          bus.write(3,  0b10001101);
//          bus.write(120, 0b10100001);
//          bus.write(4,  Opcode.AND_A_HLR.encoding);
//          cycleCpu(c, 7);
//          assertArrayEquals(new int[] {6,0,0b10000001,0b0100000,0b0,0,0,0,0,120},c._testGetPcSpAFBCDEHL());
//          
//      }
//      
//      @Test
//      void OR_A_HLRWorksB() {
//          Cpu c = new Cpu();
//          Ram r = new Ram(0xffff-1);
//          Bus bus = connect(c, r);
//          bus.write(0,  Opcode.LD_L_N8.encoding);
//          bus.write(1,  120);
//          bus.write(2,  Opcode.LD_A_N8.encoding);
//          bus.write(3,  0b10001101);
//          bus.write(120, 0b10100001);
//          bus.write(4,  Opcode.OR_A_HLR.encoding);
//          cycleCpu(c, 7);
//          assertArrayEquals(new int[] {6,0,0b10101101,0b0000000,0b0,0,0,0,0,120},c._testGetPcSpAFBCDEHL());
//          
//      }
//      
//      @Test
//      void XOR_A_HLRWorksB() {
//          Cpu c = new Cpu();
//          Ram r = new Ram(0xffff-1);
//          Bus bus = connect(c, r);
//          bus.write(0,  Opcode.LD_L_N8.encoding);
//          bus.write(1,  120);
//          bus.write(2,  Opcode.LD_A_N8.encoding);
//          bus.write(3,  0b10001101);
//          bus.write(120, 0b10100001);
//          bus.write(4,  Opcode.XOR_A_HLR.encoding);
//          cycleCpu(c, 7);
//          assertArrayEquals(new int[] {6,0,0b00101100,0b000000,0b0,0,0,0,0,120},c._testGetPcSpAFBCDEHL());
//          
//      }
//      
//
//      
//      //Rotate,shift
//      @Test
//      void RLCAWorksE() {
//          Cpu c = new Cpu();
//          Ram r = new Ram(0xffff-1);
//          Bus b = connect(c, r);
//          b.write(0, Opcode.LD_A_N8.encoding);
//          b.write(1,  0b01111111);
//          b.write(2,  0b00000111);
//          cycleCpu(c, 4);
//          assertArrayEquals(new int [] {4, 0, 0b11111110, 0b0000000, 0, 0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
//          
//      }
//
//
//
//
//      @Test
//      void SWAP_R8WorksB() { 
//          Cpu c = new Cpu();
//          Ram r = new Ram(0xffff-1);
//          Bus bus = connect(c, r);
//          
//          bus.write(0,  Opcode.LD_A_N8.encoding);
//          bus.write(1,  0b10001101);
//          bus.write(2, 0xCB);
//          bus.write(3, Opcode.SWAP_A.encoding);
//          
//          cycleCpu(c, 4);
//          assertArrayEquals(new int[] {4,0,0b11011000,0b0000000,0b0,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//
//      }
//      @Test
//      void SWAP_HLWorksB() { //a voir quand ca marchera avec r8 (0xCB a ajouter)
//          Cpu c = new Cpu();
//          Ram r = new Ram(0xffff-1);
//          Bus bus = connect(c, r);
//          
//          bus.write(0,  Opcode.LD_L_N8.encoding);
//          bus.write(1,  0b1000_1101);
//          bus.write(2,  Opcode.LD_H_N8.encoding);
//          bus.write(3,  0b1000_1101);
//          bus.write(0b1000_1101_1000_1101, 0b11110000);
//          bus.write(4, 0xCB);
//          bus.write(5, Opcode.SWAP_HLR.encoding);
//          
//          cycleCpu(c, 6);
//          assertArrayEquals(new int[] {6,0,0,0,0,0,0,0,0b10001101,0b10001101},c._testGetPcSpAFBCDEHL());
//          assertEquals(bus.read(0b1000_1101_1000_1101),0b00001111);
//          
//      }
//      
//      
//      
//      
//
//      
//    @Test
//    void CPLWorksB() {
//        Cpu c = new Cpu();
//        Ram r = new Ram(0xffff-1);
//        Bus bus = connect(c, r);
//        bus.write(0,  Opcode.LD_A_N8.encoding);
//        bus.write(1,  0b0001101);
//        bus.write(2,Opcode.CPL.encoding);
//        
//        cycleCpu(c, 4);
//        assertArrayEquals(new int [] {4, 0, 0b11110010, 0b1100000, 0, 0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
//        
//    }
//    
//    @Test
//    void SUB_A_R8B() { 
//        Cpu c= new Cpu();
//        Ram r= new Ram(0xFFFF-1);
//        Bus bus= connect(c,r);
//        bus.write(0,Opcode.LD_A_N8.encoding);
//        bus.write(1,40);
//        bus.write(2, Opcode.LD_B_N8.encoding);
//        bus.write(3, 2);
//        bus.write(5, Opcode.SUB_A_B.encoding);
//        cycleCpu(c,7);
//        assertArrayEquals(new int[] {7,0,38,0b01000000,2,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//    }
//    
//    @Test
//    void SUB_A_HLRB() { 
//        Cpu c= new Cpu();
//        Ram r= new Ram(0xFFFF-1);
//        Bus bus= connect(c,r);
//        bus.write(20, 77);
//        bus.write(0, Opcode.LD_H_N8.encoding); 
//        bus.write(1, 0);
//        bus.write(2, Opcode.LD_L_N8.encoding); 
//        bus.write(3, 20);
//        bus.write(4,Opcode.LD_A_N8.encoding);
//        bus.write(5, 79);
//        bus.write(6, Opcode.SUB_A_HLR.encoding); 
//        cycleCpu(c, 7);
//        assertArrayEquals(new int[] {7, 0, 2, 0b01000000, 0, 0, 0, 0, 0, 20}, c._testGetPcSpAFBCDEHL());
//    }
//      
//  @Test
//  void SLA_R8WorksB() {
//    Cpu c = new Cpu();
//    Ram r = new Ram(0xffff-1);
//    Bus bus = connect(c, r);
//    
//    bus.write(0,  Opcode.LD_A_N8.encoding);
//    bus.write(1,  0b10001101);
//    bus.write(2, 0xCB);
//    bus.write(3, Opcode.SLA_A.encoding);
//    cycleCpu(c, 4);
//    assertArrayEquals(new int[] {4,0,0b00011010,0b010000,0b0,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//    
//  }
//
//  @Test
//  void SLA_HLRWorksB() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(0xffff-1);
//      Bus bus = connect(c, r);
//      bus.write(20, 0b10001101);
//    bus.write(0, Opcode.LD_H_N8.encoding); 
//    bus.write(1, 0);
//    bus.write(2, Opcode.LD_L_N8.encoding); 
//    bus.write(3, 20);
//
//    bus.write(4, 0xCB);
//    bus.write(5, Opcode.SLA_HLR.encoding); 
//    cycleCpu(c, 6);
//    assertArrayEquals(new int[] {6, 0, 0, 0b00010000, 0, 0, 0, 0, 0, 20}, c._testGetPcSpAFBCDEHL());
//
//  }
//  
//  @Test
//  void RRATestEasy() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_A_N8.encoding);
//      b.write(1, 0b00000001);
//      b.write(2, Opcode.RRA.encoding);
//      cycleCpu(c, 3);
//      assertArrayEquals(
//              new int[] { 3, 0, 0b00000000, 0b00010000, 0, 0, 0, 0, 0, 0 },
//              c._testGetPcSpAFBCDEHL());
//  }
//
//  // Clemence
//  @Test
//  void SLA_R8() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(200);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_B_N8.encoding);
//      b.write(1, 0b11011010);
//      b.write(2, 0xCB);
//      b.write(3, Opcode.SLA_B.encoding);
//      cycleCpu(c, Opcode.LD_B_N8.cycles + Opcode.SLA_B.cycles);
//      assertArrayEquals(
//              new int[] { 4, 0, 0, 0b10000, 0b10110100, 0, 0, 0, 0, 0 },
//              c._testGetPcSpAFBCDEHL());
//
//  }
//
//  // Clemence
//  @Test
//  void SRA_R8() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(200);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_D_N8.encoding);
//      b.write(1, 0b11011010);
//      b.write(2, 0xCB);
//      b.write(3, Opcode.SRA_D.encoding);
//      cycleCpu(c, Opcode.LD_D_N8.cycles + Opcode.SRA_D.cycles);
//      assertArrayEquals(new int[] { 4, 0, 0, 0, 0, 0, 0b11101101, 0, 0, 0 },
//              c._testGetPcSpAFBCDEHL());
//  }
//
//  // Clemence
//  @Test
//  void SRL_R8() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(200);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_C_N8.encoding);
//      b.write(1, 0b11011010);
//      b.write(2, 0xCB);
//      b.write(3, Opcode.SRL_C.encoding);
//      cycleCpu(c, Opcode.LD_C_N8.cycles + Opcode.SRL_D.cycles);
//      assertArrayEquals(new int[] { 4, 0, 0, 0, 0, 0b01101101, 0, 0, 0, 0 },
//              c._testGetPcSpAFBCDEHL());
//  }
//
//  // Clemence
//  @Test
//  void BIT_U3_R8() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(200);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_C_N8.encoding);
//      b.write(1, 0b11011010);
//      b.write(2, 0xCB);
//      b.write(3, Opcode.BIT_2_C.encoding);
//      cycleCpu(c, Opcode.LD_C_N8.cycles + Opcode.BIT_2_C.cycles);
//      assertArrayEquals(
//              new int[] { 4, 0, 0, 0b10100000, 0, 0b11011010, 0, 0, 0, 0 },
//              c._testGetPcSpAFBCDEHL());
//  }
//
//  // Clemence
//  @Test
//  void CHG_U3_R8_SET() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(200);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_E_N8.encoding);
//      b.write(1, 0b11011010);
//      b.write(2, 0xCB);
//      b.write(3, Opcode.SET_2_E.encoding);
//      cycleCpu(c, Opcode.LD_E_N8.cycles + Opcode.SET_2_E.cycles);
//      assertArrayEquals(new int[] { 4, 0, 0, 0, 0, 0, 0, 0b11011110, 0, 0 },
//              c._testGetPcSpAFBCDEHL());
//  }
//
//  // Clemence
//  @Test
//  void CHG_U3_R8_RES() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(200);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_D_N8.encoding);
//      b.write(1, 0b11011010);
//      b.write(2, 0xCB);
//      b.write(3, Opcode.RES_3_D.encoding);
//      cycleCpu(c, Opcode.LD_D_N8.cycles + Opcode.RES_3_D.cycles);
//      assertArrayEquals(new int[] { 4, 0, 0, 0, 0, 0, 0b11010010, 0, 0, 0 },
//              c._testGetPcSpAFBCDEHL());
//  }
//  
////TEST POUR LES ADD/ROTATION
//
//  @Test
//  void AND_A_N8_RandomValueWorks() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_A_N8.encoding);
//      b.write(1, 0b11011011);
//      b.write(2, Opcode.AND_A_N8.encoding);
//      b.write(3, 0b10101010);
//      cycleCpu(c, 4);
//      assertArrayEquals(
//              new int[] { 4, 0, 0b10001010, 0b00100000, 0, 0, 0, 0, 0, 0 },
//              c._testGetPcSpAFBCDEHL());
//  }
//
//  @Test
//  void AND_A_N8_FanionWorks() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_A_N8.encoding);
//      b.write(1, 0b00000000);
//      b.write(2, Opcode.AND_A_N8.encoding);
//      b.write(3, 0b11111111);
//      cycleCpu(c, 4);
//      assertArrayEquals(
//              new int[] { 4, 0, 0b00000000, 0b10100000, 0, 0, 0, 0, 0, 0 },
//              c._testGetPcSpAFBCDEHL());
//  }
//
//  @Test
//  void AND_A_R8_RandomValueWorks() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_A_N8.encoding);
//      b.write(1, 0b10101010);
//      b.write(3, Opcode.LD_D_N8.encoding);
//      b.write(4, 0b11001100);
//      b.write(5, Opcode.AND_A_D.encoding);
//      cycleCpu(c, 6);
//      assertArrayEquals(new int[] { 6, 0, 0b10001000, 0b00100000, 0, 0,
//              0b11001100, 0, 0, 0 }, c._testGetPcSpAFBCDEHL());
//  }
//
//  @Test
//  void AND_A_HLR_RandomValueWorks() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_A_N8.encoding);
//      b.write(1, 0b10101010);
//      b.write(2, Opcode.LD_H_N8.encoding);
//      b.write(3, 0b1);
//      b.write(4, Opcode.LD_L_N8.encoding);
//      b.write(5, 0b00000000);
//      b.write(0b100000000, 0b11001100);
//      b.write(6, Opcode.AND_A_HLR.encoding);
//      cycleCpu(c, 8);
//      assertArrayEquals(
//              new int[] { 7, 0, 0b10001000, 0b00100000, 0, 0, 0, 0, 0b1, 0 },
//              c._testGetPcSpAFBCDEHL());
//  }
//
//  @Test
//  void ADD_A_N8_RandomValueWorks() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_A_N8.encoding);
//      b.write(1, 0b10101010);
//      b.write(2, Opcode.ADD_A_N8.encoding);
//      b.write(3, 0b00001100);
//      cycleCpu(c, 4);
//      assertArrayEquals(
//              new int[] { 4, 0, 0b10110110, 0b00100000, 0, 0, 0, 0, 0, 0 },
//              c._testGetPcSpAFBCDEHL());
//  }
//
//  @Test
//  void ADD_A_N8_FanionWorks() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_A_N8.encoding);
//      b.write(1, 0b10101010);
//      b.write(2, Opcode.ADD_A_N8.encoding);
//      b.write(3, 0b10001100);
//      cycleCpu(c, 4);
//      assertArrayEquals(
//              new int[] { 4, 0, 0b00110110, 0b00110000, 0, 0, 0, 0, 0, 0 },
//              c._testGetPcSpAFBCDEHL());
//  }
//
//  @Test
//  void ADD_A_R8_RandomValueWorks() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_A_N8.encoding);
//      b.write(1, 0b10101010);
//      b.write(2, Opcode.LD_D_N8.encoding);
//      b.write(3, 0b00110011);
//      b.write(4, Opcode.ADD_A_D.encoding);
//      b.write(5, 0b10001100);
//      cycleCpu(c, 6);
//      assertArrayEquals(new int[] { 6, 0, 0b11011101, 0b00000000, 0, 0,
//              0b00110011, 0, 0, 0 }, c._testGetPcSpAFBCDEHL());
//  }
//
//  @Test
//  void RLCATestBorder() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_A_N8.encoding);
//      b.write(1, 0b10000000);
//      b.write(2, Opcode.RLCA.encoding);
//      cycleCpu(c, 3);
//      assertArrayEquals(
//              new int[] { 3, 0, 0b00000001, 0b00010000, 0, 0, 0, 0, 0, 0 },
//              c._testGetPcSpAFBCDEHL());
//  }
//
//  @Test
//  void RLCATestEasy() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_A_N8.encoding);
//      b.write(1, 0b01000010);
//      b.write(2, Opcode.RLCA.encoding);
//      cycleCpu(c, 3);
//      assertArrayEquals(
//              new int[] { 3, 0, 0b10000100, 0b00000000, 0, 0, 0, 0, 0, 0 },
//              c._testGetPcSpAFBCDEHL());
//  }
//
//  @Test
//  void RRCATestEasy() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_A_N8.encoding);
//      b.write(1, 0b10101010);
//      b.write(2, Opcode.RRCA.encoding);
//      cycleCpu(c, 3);
//      assertArrayEquals(
//              new int[] { 3, 0, 0b01010101, 0b00000000, 0, 0, 0, 0, 0, 0 },
//              c._testGetPcSpAFBCDEHL());
//  }
//
//  @Test
//  void RRCATestBorder() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_A_N8.encoding);
//      b.write(1, 0b00000001);
//      b.write(2, Opcode.RRCA.encoding);
//      cycleCpu(c, 3);
//      assertArrayEquals(
//              new int[] { 3, 0, 0b10000000, 0b00010000, 0, 0, 0, 0, 0, 0 },
//              c._testGetPcSpAFBCDEHL());
//  }
//
//  @Test
//  void RRATestBorder() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      // ajouter les fanions
//      b.write(0, Opcode.LD_A_N8.encoding);
//      b.write(1, 0b10101010);
//      b.write(2, Opcode.RRA.encoding);
//      cycleCpu(c, 3);
//      assertArrayEquals(
//              new int[] { 3, 0, 0b01010101, 0b00000000, 0, 0, 0, 0, 0, 0 },
//              c._testGetPcSpAFBCDEHL());
//  }
//
//  @Test
//  void RRC_RTestEasy() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_A_N8.encoding);
//      b.write(1, 0b01010101);
//      b.write(2, 0xCB);
//      b.write(3, Opcode.RRC_A.encoding);
//      cycleCpu(c, 4);
//      assertArrayEquals(
//              new int[] { 4, 0, 0b10101010, 0b00010000, 0, 0, 0, 0, 0, 0 },
//              c._testGetPcSpAFBCDEHL());
//  }
//
//  @Test 
//  void RLC_HLR() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_H_N8.encoding);
//      b.write(1, 0b00000001);
//      b.write(2, Opcode.LD_L_N8.encoding);
//      b.write(3, 0b00000001);
//      b.write(4, Opcode.LD_HLR_N8.encoding);
//      b.write(5, 0b01010101);//////
//      b.write(6, 0XCB);
//      b.write(7, Opcode.RLC_HLR.encoding);
//      cycleCpu(c, 8);
//      assertArrayEquals(new int[] { 8, 0, 0, 0, 0, 0, 0, 0, 1, 0b00000001 },
//              c._testGetPcSpAFBCDEHL());
//      assertEquals(0b10101010, b.read(0b100000001), 0.1);
//  }
//
//  @Test 
//  void RRC_HLR() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_H_N8.encoding);
//      b.write(1, 0b00000001);
//      b.write(2, Opcode.LD_L_N8.encoding);
//      b.write(3, 0b00000001);
//      b.write(4, Opcode.LD_HLR_N8.encoding);
//      b.write(5, 0b01010101);
//      b.write(6,0xCB);
//      b.write(7, Opcode.RRC_HLR.encoding);
//      cycleCpu(c, 8);
//      assertArrayEquals(
//              new int[] { 8, 0, 0, 0b00010000, 0, 0, 0, 0, 1, 0b00000001 },
//              c._testGetPcSpAFBCDEHL());
//      assertEquals(0b10101010, b.read(0b100000001), 0.1);
//  }
//
//  @Test 
//  void RR_HLR() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_H_N8.encoding);
//      b.write(1, 0b00000001);
//      b.write(2, Opcode.LD_L_N8.encoding);
//      b.write(3, 0b00000001);
//      b.write(4, Opcode.LD_HLR_N8.encoding);
//      b.write(5, 0b01010101);
//      b.write(6, 0xCB);
//      b.write(7, Opcode.RR_HLR.encoding);
//      cycleCpu(c, 8);
//      assertArrayEquals(
//              new int[] { 8, 0, 0, 0b00010000, 0, 0, 0, 0, 1, 0b00000001 },
//              c._testGetPcSpAFBCDEHL());
//      assertEquals(0b00101010, b.read(0b100000001), 0.1);
//  }
//
//  @Test // ne fonctionne passssssssssssssssssssssssssssssss
//  void RL_HLR() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_H_N8.encoding);
//      b.write(1, 0b00000001);
//      b.write(2, Opcode.LD_L_N8.encoding);
//      b.write(3, 0b00000001);
//      b.write(4, Opcode.LD_HLR_N8.encoding);
//      b.write(5, 0b01010101);
//      b.write(6, 0xCB);
//      b.write(7, Opcode.RL_HLR.encoding);
//      cycleCpu(c, 8);
//      assertArrayEquals(
//              new int[] { 8, 0, 0, 0, 0, 0, 0, 0, 1, 0b00000001 },
//              c._testGetPcSpAFBCDEHL());
//      assertEquals(0b10101010, b.read(0b100000001), 0.1);
//  }
//
//
//
//  @Test
//  void SWAP_RTest() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_A_N8.encoding);
//      b.write(1, 0b11110000);
//      b.write(2, 0xCB);
//      b.write(3, Opcode.SWAP_A.encoding);
//      cycleCpu(c, 4);
//      assertArrayEquals(
//              new int[] { 4, 0, 0b00001111, 0b00000000, 0, 0, 0, 0, 0, 0 },
//              c._testGetPcSpAFBCDEHL());
//  }
//
//  @Test 
//  void SWAP_HLRTest() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(65535);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_H_N8.encoding);
//      b.write(1, 0b00000001);
//      b.write(2, Opcode.LD_L_N8.encoding);
//      b.write(3, 0b00000001);
//      b.write(4, Opcode.LD_HLR_N8.encoding);
//      b.write(5, 0b11110000);
//      b.write(6, 0xCB);
//      b.write(7, Opcode.SWAP_HLR.encoding);
//      cycleCpu(c, 8);
//      assertArrayEquals(
//              new int[] { 8, 0, 0, 0, 0, 0, 0, 0, 1, 0b00000001 },
//              c._testGetPcSpAFBCDEHL());
//      assertEquals(0b00001111, b.read(0b100000001), 0.1);
//  }
//
//  @Test
//   public void ADD_A_R8Works() {
//       Cpu c = new Cpu();
//       Ram r = new Ram(0xffff -1);
//       Bus b = connect(c, r);
//       b.write(0,  Opcode.LD_A_N8.encoding);
//       b.write(1,  25);
//       b.write(2, Opcode.LD_C_N8.encoding);
//       b.write(3, 45);
//       b.write(5,  Opcode.ADD_A_C.encoding);
//       cycleCpu(c, 6);
//       assertArrayEquals(new int [] {6, 0, 70, 0b0100000, 0, 45, 0, 0, 0, 0}, c._testGetPcSpAFBCDEHL());
//   }
//   @Test
//      public void ADD_A_R8_Works() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(10);
//      Bus b = connect(c, r);
//      b.write(0, Opcode.LD_B_N8.encoding);
//      b.write(1, 25);
//      b.write(2, Opcode.LD_A_N8.encoding);
//      b.write(3, 5);
//      b.write(4, Opcode.ADD_A_B.encoding);
//      cycleCpu(c, 6);
//      assertArrayEquals(new int[] { 6, 0, 30, 0, 25, 0, 0, 0, 0, 0 }, c._testGetPcSpAFBCDEHL());
//      
//      }
//      
//      @Test
//      public void ADD_SUB_A_N8RWorks() {
//          Cpu c= new Cpu();
//          Ram r= new Ram(0xFFFF-1);
//          Bus bus= connect(c,r);
//          bus.write(11+0xFF00,3);
//          bus.write(0, Opcode.LD_A_N8R.encoding);
//          bus.write(1, 11);
//          bus.write(2, Opcode.ADD_A_N8.encoding);
//          bus.write(3, 3);
//          bus.write(4, Opcode.SUB_A_N8.encoding);
//          bus.write(5, 2);
//
//          cycleCpu(c,6);
////        System.out.print(Arrays.toString(c._testGetPcSpAFBCDEHL()) );
//          assertArrayEquals(new int[] {6,0,4,64,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
//          }
//   
//   @Test 
//   public void ADD_A_N8Works() {
//       Cpu c = new Cpu();
//       Ram r = new Ram(0xffff -1);
//       Bus b = connect(c, r);
//       b.write(0,  Opcode.LD_A_N8.encoding);
//       b.write(1,  68);
//       b.write(2,  Opcode.ADD_A_N8.encoding);
//       b.write(3,  2);
//       cycleCpu(c, 6);
//       assertArrayEquals(new int [] {6, 0, 70, 0b0000, 0, 0, 0, 0, 0, 0}, c._testGetPcSpAFBCDEHL());        
//   }
//   @Test 
//   public  void ADD_A_R8WorksF() {
//        Cpu c= new Cpu();
//        Ram r= new Ram(0xFFFF-1);
//        Bus bus= connect(c,r);
//        bus.write(0,Opcode.LD_A_N8.encoding);
//        bus.write(1,40);
//        bus.write(2, Opcode.LD_B_N8.encoding);
//        bus.write(3, 2);
//        bus.write(5,  Opcode.ADD_A_B.encoding);
//        bus.write(6, Opcode.ADD_A_A.encoding);
//        cycleCpu(c,7);
//        assertArrayEquals(new int[] {7,0,84,0b000100000,2,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//    }
//  @Test
//  public void ADD_SUB_A_N8RWokrs() {
//  Cpu c= new Cpu();
//  Ram r= new Ram(0xFFFF-1);
//  Bus bus= connect(c,r);
//  bus.write(11+0xFF00,3);
//  bus.write(0, Opcode.LD_A_N8R.encoding);
//  bus.write(1, 11);
//  
//  bus.write(2, Opcode.ADD_A_N8.encoding);
//  bus.write(3, 3);
//  
//  bus.write(4, Opcode.SUB_A_N8.encoding);
//  bus.write(5, 2);
//  
//  cycleCpu(c,7);
//  System.out.print(Arrays.toString(c._testGetPcSpAFBCDEHL()) );
//  assertArrayEquals(new int[] {6,0,4,64,0,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//  
//  }
//   @Test
//  public  void ADD_A_HLRWorks() {
//       Cpu c = new Cpu();
//       Ram r = new Ram(0xffff-1);
//       Bus b = connect(c, r);
//       b.write(0,  Opcode.LD_L_N8.encoding);
//       b.write(1,  120);
//       b.write(2,  Opcode.LD_A_N8.encoding);
//       b.write(3,  4);
//       b.write(120, 43);
//       b.write(4,  Opcode.ADD_A_HLR.encoding);
//       cycleCpu(c, 7);
//       assertArrayEquals(new int [] {6, 0, 47, 0b0000000, 0, 0, 0, 0, 0, 120}, c._testGetPcSpAFBCDEHL());
//   }
//   @Test
//   public void ADD_A_N8WorksF() {
//       Cpu c= new Cpu();
//       Ram r= new Ram(0xFFFF-1);
//       Bus bus= connect(c,r);
//       bus.write(0, Opcode.LD_A_N8.encoding);
//       bus.write(1, 17);
//       bus.write(2, Opcode.ADD_A_N8.encoding);
//       bus.write(3, 13);
//       cycleCpu(c,4);
//       assertArrayEquals(new int[] {4,0,30,0b0000000,0,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//   }
//   
//   @Test
//  public void ADD_A_HLRWorksF() {
//       Cpu c= new Cpu();
//       Ram r= new Ram(0xFFFF-1);
//       Bus bus= connect(c,r);
//       bus.write(0, Opcode.LD_L_N8.encoding);
//       bus.write(1, 42);
//       bus.write(2, Opcode.LD_H_N8.encoding);
//       bus.write(3, 0);
//       bus.write(4, Opcode.LD_A_N8.encoding);
//       bus.write(5, 10);
//       bus.write(6, Opcode.ADD_A_HLR.encoding);
//       bus.write(42, 3);
//       cycleCpu(c,10);
//       assertArrayEquals(new int[] {9,0,13,0,0,0,0,0,0,42},c._testGetPcSpAFBCDEHL());
//   }
//   
//   @Test
//  public  void INC_R8Works() {
//       Cpu c = new Cpu();
//       Ram r = new Ram(0xffff-1);
//       Bus b = connect(c, r);
//       b.write(0,  Opcode.LD_E_N8.encoding);
//       b.write(1,  44);
//       b.write(2,  Opcode.INC_E.encoding);
//       cycleCpu(c, 3);
//       assertArrayEquals(new int [] {3, 0, 0, 0b00000000, 0, 0, 0, 45, 0, 0}, c._testGetPcSpAFBCDEHL());  
//       }
//
//   
//
//   
//   @Test
//   public void INC_R8WorksF() {
//       Cpu c= new Cpu();
//       Ram r= new Ram(0xFFFF-1);
//       Bus bus= connect(c,r);
//       bus.write(0, Opcode.LD_H_N8.encoding);
//       bus.write(1, 3);
//       bus.write(2,Opcode.INC_H.encoding);
//       cycleCpu(c,3);
//       assertArrayEquals(new int[] {3,0,0,0b0000,0,0,0,0,4,0},c._testGetPcSpAFBCDEHL());
//   }
//   
//
//   
//   
//   
//   //sub
//   
//   
//
//   
//   //And, or, xor, complement
//   
//   @Test
//   public void AND_A_N8Works() {
//       Cpu c= new Cpu();
//       Ram r= new Ram(0xFFFF-1);
//       Bus bus= connect(c,r);
//       bus.write(0,Opcode.LD_A_N8.encoding);
//       bus.write(1,0b10001101);
//       bus.write(2, Opcode.LD_B_N8.encoding);
//       bus.write(3, 0b10100001);
//       bus.write(4, Opcode.AND_A_B.encoding);
//       cycleCpu(c,5);
//       assertArrayEquals(new int[] {5,0,0b10000001,0b0100000,0b10100001,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//   }
//   
//  
//      @Test
//      public void AND_A_R8WorksA() {
//          Cpu c = new Cpu();
//          Ram r = new Ram(100);
//          Bus bus = connect(c,r);
//          
//      
//          bus.write(0, Opcode.LD_B_N8.encoding);
//          bus.write(1, 0b1010_0111);
//          bus.write(2, Opcode.LD_A_N8.encoding);
//          bus.write(3, 0b1101_0110);
//          bus.write(4, Opcode.AND_A_B.encoding);
//          cycleCpu(c,8);
////        System.out.println(Arrays.toString(c._testGetPcSpAFBCDEHL()) );
//          // PC, SP, A, F, B, C, D, E, H, L
//          assertArrayEquals(new int[] {8, 0, 0b1000_0110, 0b0010_0000, 0b1010_0111, 0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
//      }
//      
//      
//   @Test
//   public void AND_A_R8Works() {
//       Cpu c= new Cpu();
//       Ram r= new Ram(0xFFFF-1);
//       Bus bus= connect(c,r);
//       bus.write(0,Opcode.LD_A_N8.encoding);
//       bus.write(1,0b10001101);
//       bus.write(2, Opcode.AND_A_N8.encoding);
//       bus.write(3, 0b10100001);
//       cycleCpu(c,5);
//       assertArrayEquals(new int[] {5,0,0b10000001,0b0100000,0b0,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//   }
//   
//   @Test
//  public  void OR_A_N8Works() {
//       Cpu c= new Cpu();
//       Ram r= new Ram(0xFFFF-1);
//       Bus bus= connect(c,r);
//       bus.write(0,Opcode.LD_A_N8.encoding);
//       bus.write(1,0b10001101);
//       bus.write(2, Opcode.LD_B_N8.encoding);
//       bus.write(3, 0b10100001);
//       bus.write(4, Opcode.OR_A_B.encoding);
//       cycleCpu(c,5);
//       assertArrayEquals(new int[] {5,0,0b10101101,0b0000000,0b10100001,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//   }
//   
//   @Test
//  public void OR_A_R8Works() {
//       Cpu c= new Cpu();
//       Ram r= new Ram(0xFFFF-1);
//       Bus bus= connect(c,r);
//       bus.write(0,Opcode.LD_A_N8.encoding);
//       bus.write(1,0b10001101);
//       bus.write(2, Opcode.OR_A_N8.encoding);
//       bus.write(3, 0b10100001);
//       cycleCpu(c,5);
//       assertArrayEquals(new int[] {5,0,0b10101101,0b0000000,0b0,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//   }
//   
//   @Test
//  public void XOR_A_N8Works() {
//       Cpu c= new Cpu();
//       Ram r= new Ram(0xFFFF-1);
//       Bus bus= connect(c,r);
//       bus.write(0,Opcode.LD_A_N8.encoding);
//       bus.write(1,0b10001101);
//       bus.write(2, Opcode.LD_B_N8.encoding);
//       bus.write(3, 0b10100001);
//       bus.write(4, Opcode.XOR_A_B.encoding);
//       cycleCpu(c,5);
//       assertArrayEquals(new int[] {5,0,0b00101100,0b0000000,0b10100001,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//   }
//      
//      @Test
//      public void XOR_A_R8WorksA() {
//          Cpu c = new Cpu();
//          Ram r = new Ram(100);
//          Bus bus = connect(c,r);
//          
//      
//          bus.write(0, Opcode.LD_B_N8.encoding);
//          bus.write(1, 0b1010_0111);
//          bus.write(2, Opcode.LD_A_N8.encoding);
//          bus.write(3, 0b1101_0110);
//          bus.write(4, Opcode.XOR_A_B.encoding);
//          cycleCpu(c,8);
////        System.out.println(Arrays.toString(c._testGetPcSpAFBCDEHL()) );
//          // PC, SP, A, F, B, C, D, E, H, L
//          assertArrayEquals(new int[] {8, 0, 0b0111_0001, 0b0000_0000, 0b1010_0111, 0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
//      }
//   
//   @Test
//  public void XOR_A_R8Works() {
//       Cpu c= new Cpu();
//       Ram r= new Ram(0xFFFF-1);
//       Bus bus= connect(c,r);
//       bus.write(0,Opcode.LD_A_N8.encoding);
//       bus.write(1,0b10001101);
//       bus.write(2, Opcode.XOR_A_N8.encoding);
//       bus.write(3, 0b10100001);
//       cycleCpu(c,5);
//       assertArrayEquals(new int[] {5,0,0b00101100,0b0000000,0b0,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//   }
//   
//   @Test
// public  void XOR_A_HLRWorks() {
//       Cpu c = new Cpu();
//       Ram r = new Ram(0xffff-1);
//       Bus bus = connect(c, r);
//       bus.write(0,  Opcode.LD_L_N8.encoding);
//       bus.write(1,  120);
//       bus.write(2,  Opcode.LD_A_N8.encoding);
//       bus.write(3,  0b10001101);
//       bus.write(120, 0b10100001);
//       bus.write(4,  Opcode.XOR_A_HLR.encoding);
//       cycleCpu(c, 7);
//       assertArrayEquals(new int[] {6,0,0b00101100,0b000000,0b0,0,0,0,0,120},c._testGetPcSpAFBCDEHL());
//       
//   }
//
//
//   
//
//   
//
//   
//   //Rotate,shift
//   @Test
// public  void RLCAWorks() {
//       Cpu c = new Cpu();
//       Ram r = new Ram(0xffff-1);
//       Bus b = connect(c, r);
//       b.write(0, Opcode.LD_A_N8.encoding);
//       b.write(1,  0b01111111);
//       b.write(2,  0b00000111);
//       cycleCpu(c, 4);
//       assertArrayEquals(new int [] {4, 0, 0b11111110, 0b0000000, 0, 0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
//       
//   }
//
//
//
//
//   @Test
//public   void SWAP_R8Works() { 
//       Cpu c = new Cpu();
//       Ram r = new Ram(0xffff-1);
//       Bus bus = connect(c, r);
//       
//       bus.write(0,  Opcode.LD_A_N8.encoding);
//       bus.write(1,  0b10001101);
//       bus.write(2, 0xCB);
//       bus.write(3, Opcode.SWAP_A.encoding);
//       
//       cycleCpu(c, 4);
//       assertArrayEquals(new int[] {4,0,0b11011000,0b0000000,0b0,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//
//   }
//   @Test
//public   void SWAP_HLWorks() { //a voir quand ca marchera avec r8 (0xCB a ajouter)
//       Cpu c = new Cpu();
//       Ram r = new Ram(0xffff-1);
//       Bus bus = connect(c, r);
//       
//       bus.write(0,  Opcode.LD_L_N8.encoding);
//       bus.write(1,  0b1000_1101);
//       bus.write(2,  Opcode.LD_H_N8.encoding);
//       bus.write(3,  0b1000_1101);
//       bus.write(0b1000_1101_1000_1101, 0b11110000);
//       bus.write(4, 0xCB);
//       bus.write(5, Opcode.SWAP_HLR.encoding);
//       
//       cycleCpu(c, 6);
//       assertArrayEquals(new int[] {6,0,0,0,0,0,0,0,0b10001101,0b10001101},c._testGetPcSpAFBCDEHL());
//       assertEquals(bus.read(0b1000_1101_1000_1101),0b00001111);
//       
//   }
//   
//   
//   
//   
//
//   
// @Test
//public   void CPLWorks() {
//     Cpu c = new Cpu();
//     Ram r = new Ram(0xffff-1);
//     Bus bus = connect(c, r);
//     bus.write(0,  Opcode.LD_A_N8.encoding);
//     bus.write(1,  0b0001101);
//     bus.write(2,Opcode.CPL.encoding);
//     
//     cycleCpu(c, 4);
//     assertArrayEquals(new int [] {4, 0, 0b11110010, 0b1100000, 0, 0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
//     
// }
//  @Test
//  public void CPLWorksA() {
//      Cpu c= new Cpu();
//      Ram r= new Ram(0xFFFF-1);
//      Bus bus= connect(c,r);
//      bus.write(0, Opcode.LD_A_N8.encoding);
//      bus.write(1, 0b1010_1100);
//      bus.write(2, Opcode.CPL.encoding);
//      
//      cycleCpu(c,4);
////    System.out.print(Arrays.toString(c._testGetPcSpAFBCDEHL()) );
//      assertArrayEquals(new int[] {4, 0, 0b0101_0011,0b0110_0000, 0, 0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
//  }
// 
// @Test
//public   void SUB_A_R8() { 
//     Cpu c= new Cpu();
//     Ram r= new Ram(0xFFFF-1);
//     Bus bus= connect(c,r);
//     bus.write(0,Opcode.LD_A_N8.encoding);
//     bus.write(1,40);
//     bus.write(2, Opcode.LD_B_N8.encoding);
//     bus.write(3, 2);
//     bus.write(5, Opcode.SUB_A_B.encoding);
//     cycleCpu(c,7);
//     assertArrayEquals(new int[] {7,0,38,0b01000000,2,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
// }
// 
// @Test
// public   void SUB_A_HLR() { 
//     Cpu c= new Cpu();
//     Ram r= new Ram(0xFFFF-1);
//     Bus bus= connect(c,r);
//     bus.write(20, 77);
//     bus.write(0, Opcode.LD_H_N8.encoding); 
//     bus.write(1, 0);
//     bus.write(2, Opcode.LD_L_N8.encoding); 
//     bus.write(3, 20);
//     bus.write(4,Opcode.LD_A_N8.encoding);
//     bus.write(5, 79);
//     bus.write(6, Opcode.SUB_A_HLR.encoding); 
//     cycleCpu(c, 7);
//     assertArrayEquals(new int[] {7, 0, 2, 0b01000000, 0, 0, 0, 0, 0, 20}, c._testGetPcSpAFBCDEHL());
// }
//   
//@Test
//public void SLA_R8Works() {
// Cpu c = new Cpu();
// Ram r = new Ram(0xffff-1);
// Bus bus = connect(c, r);
// 
// bus.write(0,  Opcode.LD_A_N8.encoding);
// bus.write(1,  0b10001101);
// bus.write(2, 0xCB);
// bus.write(3, Opcode.SLA_A.encoding);
// cycleCpu(c, 4);
// assertArrayEquals(new int[] {4,0,0b00011010,0b010000,0b0,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
// 
//}
//
//@Test
//public void SLA_HLRWorks() {
//   Cpu c = new Cpu();
//   Ram r = new Ram(0xffff-1);
//   Bus bus = connect(c, r);
//   bus.write(20, 0b10001101);
// bus.write(0, Opcode.LD_H_N8.encoding); 
// bus.write(1, 0);
// bus.write(2, Opcode.LD_L_N8.encoding); 
// bus.write(3, 20);
//
// bus.write(4, 0xCB);
// bus.write(5, Opcode.SLA_HLR.encoding); 
// cycleCpu(c, 6);
// assertArrayEquals(new int[] {6, 0, 0, 0b00010000, 0, 0, 0, 0, 0, 20}, c._testGetPcSpAFBCDEHL());
//
//}  
//  
//  @Test
//  public void CP_A_R8_Works() {
//  Cpu c = new Cpu();
//  Ram r = new Ram(0xFFFF-1);
//  Bus b = connect(c,r);
//  
//  
//  b.write(0, Opcode.LD_A_N8.encoding);
//  b.write(1, 0b1111);
//  b.write(2, Opcode.LD_B_N8.encoding);
//  b.write(3, 0b1111);
//  b.write(4, Opcode.CP_A_B.encoding);
//  
//  cycleCpu(c, 10);
//  assertArrayEquals(new int[] {10,0,0b1111,0b1100_0000,0b1111,0,0,0,0,0},c._testGetPcSpAFBCDEHL());
//  }
//  
//  @Test
//  public void AND_A_R8_Works() {
//  Cpu c = new Cpu();
//  Ram r = new Ram(0xFFFF-1);
//  Bus b = connect(c,r);
//  
//  b.write(0, Opcode.LD_A_N8.encoding);
//  b.write(1, 0b1010_0011);
//  b.write(2, Opcode.AND_A_N8.encoding);
//  b.write(3, 0b1100_1011);
//  
//  cycleCpu(c, 4);
//  assertArrayEquals(new int[] { 4 ,0 ,0b1000_0011 ,0b0010_0000 ,0 ,0 ,0 ,0 ,0 ,0 },c._testGetPcSpAFBCDEHL());
//  }
//  @Test
//  public void AND_A_N8WorksA() {
//      Cpu c= new Cpu();
//      Ram r= new Ram(0xFFFF-1);
//      Bus bus= connect(c,r);
//      bus.write(0, Opcode.LD_A_N8.encoding);
//      bus.write(1, 0b1010_1100);
//      bus.write(2, Opcode.AND_A_N8.encoding);
//      bus.write(3, 0b0011_1010);
//      
//      cycleCpu(c,4);
////    System.out.print(Arrays.toString(c._testGetPcSpAFBCDEHL()) );
//      assertArrayEquals(new int[] {4,0,0b0010_1000,0b0010_0000,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
//  }
//  @Test
//  public void AND_A_HLRWorksA() {
//      Cpu c= new Cpu();
//      Ram r= new Ram(0xFFFF-1);
//      Bus bus= connect(c,r);
//      
//      bus.write(0, Opcode.LD_H_N8.encoding);
//      bus.write(1, 0b1000_0110);
//      bus.write(2, Opcode.LD_L_N8.encoding);
//      bus.write(3, 0b1010_0110);
//      bus.write(4, Opcode.LD_A_N8.encoding);
//      bus.write(5, 0b1110_0101);
//      bus.write(0b1000_0110_1010_0110, 0b0100_0100);
//      bus.write(6, Opcode.AND_A_HLR.encoding);
//      
//      cycleCpu(c,10);
////    System.out.print(Arrays.toString(c._testGetPcSpAFBCDEHL()) );
//      assertArrayEquals(new int[] {9,0,0b0100_0100,0b0010_0000,0,0,0,0,0b1000_0110,0b1010_0110}, c._testGetPcSpAFBCDEHL());
//  }
//  
//  @Test
// public void AND_A_HLRWorks() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(0xffff-1);
//      Bus bus = connect(c, r);
//      bus.write(0,  Opcode.LD_L_N8.encoding);
//      bus.write(1,  120);
//      bus.write(2,  Opcode.LD_A_N8.encoding);
//      bus.write(3,  0b10001101);
//      bus.write(120, 0b10100001);
//      bus.write(4,  Opcode.AND_A_HLR.encoding);
//      cycleCpu(c, 7);
//      assertArrayEquals(new int[] {6,0,0b10000001,0b0100000,0b0,0,0,0,0,120},c._testGetPcSpAFBCDEHL());
//      
//  }
//  
//  @Test
//  public void CPL_Works() {
//  Cpu c = new Cpu();
//  Ram r = new Ram(0xFFFF-1);
//  Bus b = connect(c,r);
//  
//  b.write(0, Opcode.LD_A_N8.encoding);
//  b.write(1, 0b1010_0011);
//  b.write(2, Opcode.CPL.encoding);
//  
//  cycleCpu(c, 3);
//  assertArrayEquals(new int[] { 3 ,0 ,0b0101_1100 ,0b0110_0000 ,0 ,0 ,0 ,0 ,0 ,0 },c._testGetPcSpAFBCDEHL());
//  }
//  
//  @Test
//  public void ROTA_Works() {
//  Cpu c = new Cpu();
//  Ram r = new Ram(0xFFFF-1);
//  Bus b = connect(c,r);
//  
//  b.write(0, Opcode.LD_A_N8.encoding);
//  b.write(1, 0b1010_0011);
//  b.write(2, Opcode.RLCA.encoding);
//  
//  cycleCpu(c, 3);
//  assertArrayEquals(new int[] { 3 ,0 ,0b0100_0111 ,0b0001_0000 ,0 ,0 ,0 ,0 ,0 ,0 },c._testGetPcSpAFBCDEHL());
//  }
//  
//  @Test
//  public void SWAP_R8_Works() {
//  Cpu c = new Cpu();
//  Ram r = new Ram(10);
//  Bus b = connect(c, r);
//  b.write(0, Opcode.LD_B_N8.encoding);
//  b.write(1, 0b1010_1100);
//  b.write(2, 0xCB);
//  b.write(3, Opcode.SWAP_B.encoding);
//  
//  cycleCpu(c, 4);
//  assertArrayEquals(new int[] { 4, 0, 0, 0, 0b1100_1010, 0, 0, 0, 0, 0 }, c._testGetPcSpAFBCDEHL());
//  
//  }
//  
//  @Test
//  public void SRA_Works() {
//  Cpu c = new Cpu();
//  Ram r = new Ram(10);
//  Bus b = connect(c, r);
//  b.write(0, Opcode.LD_B_N8.encoding);
//  b.write(1, 0b1010_1101);
//  b.write(2, 0xCB);
//  b.write(3, Opcode.SRA_B.encoding);
//  
//  cycleCpu(c, 4);
//  assertArrayEquals(new int[] { 4, 0, 0, 0b0001_0000, 0b1101_0110, 0, 0, 0, 0, 0 }, c._testGetPcSpAFBCDEHL());    
//  }
//  
//
//  
//  @Test
//  public void BIT_Works() {
//  Cpu c = new Cpu();
//  Ram r = new Ram(10);
//  Bus b = connect(c, r);
//  b.write(0, Opcode.LD_B_N8.encoding);
//  b.write(1, 0b1010_1101);
//  b.write(2, 0xCB);
//  b.write(3, Opcode.BIT_6_B.encoding);
//  
//  cycleCpu(c, 4);
//  assertArrayEquals(new int[] { 4, 0, 0, 0b1010_0000, 0b1010_1101, 0, 0, 0, 0, 0 }, c._testGetPcSpAFBCDEHL());  
//  }
//  
//  @Test
//    public  void SCF_Works() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(0xFFFF-1);
//      Bus b = connect(c, r);
//      
//      b.write(0, Opcode.SCF.encoding);
//          
//      cycleCpu(c, 1);
//      assertArrayEquals(new int[] { 1, 0, 0, 0b0001_0000, 0, 0, 0, 0, 0, 0 }, c._testGetPcSpAFBCDEHL());  
//     }
//    
//     @Test
//     public void CCF_Works() {
//      Cpu c = new Cpu();
//      Ram r = new Ram(0xFFFF-1);
//      Bus b = connect(c, r);
//      
//      b.write(0, Opcode.SCF.encoding);
//      b.write(1, Opcode.CCF.encoding);
//          
//      cycleCpu(c, 2);
//      assertArrayEquals(new int[] { 2, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, c._testGetPcSpAFBCDEHL());  
//     }
//     
//  
//      
//      
//      
//      
//      @Test
//      public void OR_A_N8WorksA() {
//          Cpu c= new Cpu();
//          Ram r= new Ram(0xFFFF-1);
//          Bus bus= connect(c,r);
//          bus.write(0, Opcode.LD_A_N8.encoding);
//          bus.write(1, 0b1110_1100);
//          bus.write(2, Opcode.OR_A_N8.encoding);
//          bus.write(3, 0b0011_1010);
//          
//          cycleCpu(c,4);
////        System.out.print(Arrays.toString(c._testGetPcSpAFBCDEHL()) );
//          assertArrayEquals(new int[] {4,0,0b1111_1110,0b0000_0000,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
//      }
//       
//       @Test
//   public   void OR_A_HLRWorks() {
//           Cpu c = new Cpu();
//           Ram r = new Ram(0xffff-1);
//           Bus bus = connect(c, r);
//           bus.write(0,  Opcode.LD_L_N8.encoding);
//           bus.write(1,  120);
//           bus.write(2,  Opcode.LD_A_N8.encoding);
//           bus.write(3,  0b10001101);
//           bus.write(120, 0b10100001);
//           bus.write(4,  Opcode.OR_A_HLR.encoding);
//           cycleCpu(c, 7);
//           assertArrayEquals(new int[] {6,0,0b10101101,0b0000000,0b0,0,0,0,0,120},c._testGetPcSpAFBCDEHL());
//           
//       }
//      
//      @Test
//      public void XOR_A_N8WorksA() {
//          Cpu c= new Cpu();
//          Ram r= new Ram(0xFFFF-1);
//          Bus bus= connect(c,r);
//          bus.write(0, Opcode.LD_A_N8.encoding);
//          bus.write(1, 0b1110_1100);
//          bus.write(2, Opcode.XOR_A_N8.encoding);
//          bus.write(3, 0b0011_1010);
//          
//          cycleCpu(c,4);
////        System.out.print(Arrays.toString(c._testGetPcSpAFBCDEHL()) );
//          assertArrayEquals(new int[] {4,0,0b1101_0110,0b0000_0000,0,0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
//      }
//      @Test
//      public void XOR_A_HLRWorksA() {
//          Cpu c= new Cpu();
//          Ram r= new Ram(0xFFFF-1);
//          Bus bus= connect(c,r);
//          
//          bus.write(0, Opcode.LD_H_N8.encoding);
//          bus.write(1, 0b1000_0110);
//          bus.write(2, Opcode.LD_L_N8.encoding);
//          bus.write(3, 0b1010_0110);
//          bus.write(4, Opcode.LD_A_N8.encoding);
//          bus.write(5, 0b1000_0101);
//          bus.write(0b1000_0110_1010_0110, 0b0101_0100);
//          bus.write(6, Opcode.XOR_A_HLR.encoding);
//          
//          cycleCpu(c,10);
////        System.out.print(Arrays.toString(c._testGetPcSpAFBCDEHL()) );
//          assertArrayEquals(new int[] {9,0,0b1101_0001,0b0000_0000,0,0,0,0,0b1000_0110,0b1010_0110}, c._testGetPcSpAFBCDEHL());
//      }
//  
//      
//  
//      
//      @Test
//      public void OR_A_R8WorksA() {
//          Cpu c = new Cpu();
//          Ram r = new Ram(100);
//          Bus bus = connect(c,r);
//          
//      
//          bus.write(0, Opcode.LD_B_N8.encoding);
//          bus.write(1, 0b1010_0111);
//          bus.write(2, Opcode.LD_A_N8.encoding);
//          bus.write(3, 0b1101_0110);
//          bus.write(4, Opcode.OR_A_B.encoding);
//          cycleCpu(c,8);
////        System.out.println(Arrays.toString(c._testGetPcSpAFBCDEHL()) );
//          // PC, SP, A, F, B, C, D, E, H, L
//          assertArrayEquals(new int[] {8, 0, 0b1111_0111, 0b0000_0000, 0b1010_0111, 0,0,0,0,0}, c._testGetPcSpAFBCDEHL());
//      }
//      
//      @Test
//      public void OR_A_HLRWorksA() {
//          Cpu c= new Cpu();
//          Ram r= new Ram(0xFFFF-1);
//          Bus bus= connect(c,r);
//          
//          bus.write(0, Opcode.LD_H_N8.encoding);
//          bus.write(1, 0b1000_0110);
//          bus.write(2, Opcode.LD_L_N8.encoding);
//          bus.write(3, 0b1010_0110);
//          bus.write(4, Opcode.LD_A_N8.encoding);
//          bus.write(5, 0b1110_0101);
//          bus.write(0b1000_0110_1010_0110, 0b0101_0100);
//          bus.write(6, Opcode.OR_A_HLR.encoding);
//          
//          cycleCpu(c,10);
////        System.out.print(Arrays.toString(c._testGetPcSpAFBCDEHL()) );
//          assertArrayEquals(new int[] {9,0,0b1111_0101,0b0000_0000,0,0,0,0,0b1000_0110,0b1010_0110}, c._testGetPcSpAFBCDEHL());
//      }
//      
//          
//
//  // ce test est peut être foireux au niveau des fanions CPU
//  
//      @Test
//         public void SET_R8_Works() {
//          Cpu c = new Cpu();
//          Ram r = new Ram(0xFFFF-1);
//          Bus b = connect(c, r);
//          b.write(0, Opcode.LD_B_N8.encoding);
//          b.write(1, 0b1010_1101);
//          b.write(2, 0xCB);
//          b.write(3, Opcode.SET_6_B.encoding);
//          
//          cycleCpu(c, 4);
//          assertArrayEquals(new int[] { 4, 0, 0, 0, 0b1110_1101, 0, 0, 0, 0, 0 }, c._testGetPcSpAFBCDEHL());  
//         }
//        
//         @Test
//         public void SET_HLR_Works() {
//          Cpu c = new Cpu();
//          Ram r = new Ram(0xFFFF-1);
//          Bus b = connect(c, r);
//          b.write(0, Opcode.LD_H_N8.encoding);
//          b.write(1, 0b1000_0110);
//          b.write(2, Opcode.LD_L_N8.encoding);
//          b.write(3, 0b1010_0110);
//          b.write(0b1000_0110_1010_0110, 0b1001_1110);
//          b.write(4, 0xCB);
//          b.write(5, Opcode.SET_6_HLR.encoding);
//          b.write(6, Opcode.LD_E_HLR.encoding);
//          
//          cycleCpu(c, 10);
//          assertArrayEquals(new int[] { 7, 0, 0, 0, 0, 0, 0, 0b1101_1110, 0b1000_0110, 0b1010_0110 }, c._testGetPcSpAFBCDEHL());  
//         }
//        
//         @Test
//         public void RES_R8_Works() {
//          Cpu c = new Cpu();
//          Ram r = new Ram(0xFFFF-1);
//          Bus b = connect(c, r);
//          b.write(0, Opcode.LD_B_N8.encoding);
//          b.write(1, 0b1010_1101);
//          b.write(2, 0xCB);
//          b.write(3, Opcode.RES_7_B.encoding);
//          
//          cycleCpu(c, 4);
//          assertArrayEquals(new int[] { 4, 0, 0, 0, 0b0010_1101, 0, 0, 0, 0, 0 }, c._testGetPcSpAFBCDEHL());  
//         }
//         @Test
//         public void RES_HLR_Works() {
//          Cpu c = new Cpu();
//          Ram r = new Ram(0xFFFF-1);
//          Bus b = connect(c, r);
//          b.write(0, Opcode.LD_H_N8.encoding);
//          b.write(1, 0b1000_0110);
//          b.write(2, Opcode.LD_L_N8.encoding);
//          b.write(3, 0b1010_0110);
//          b.write(0b1000_0110_1010_0110, 0b1001_1110);
//          b.write(4, 0xCB);
//          b.write(5, Opcode.RES_7_HLR.encoding);
//          b.write(6, Opcode.LD_E_HLR.encoding);
//          
//          cycleCpu(c, 10);
//          assertArrayEquals(new int[] { 7, 0, 0, 0, 0, 0, 0, 0b0001_1110, 0b1000_0110, 0b1010_0110 }, c._testGetPcSpAFBCDEHL());  
//         }
        
        @Test
        void JP_HLWorksE() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(0,  Opcode.LD_HL_N16.encoding);
            b.write(1,  0);
            b.write(2,  0x42);
            b.write(3, Opcode.JP_HL.encoding);
            cycleCpu(c, 6);
            assertArrayEquals(new int [] {0x4202, 0, 0, 0, 0, 0, 0, 0, 0x42, 0}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        void JP_N16WorksE() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(0,  0b11000011);
            b.write(1,  0x55);
            b.write(2,  0x33);
            cycleCpu(c,  3);
            assertArrayEquals(new int [] {0x3355, 0, 0, 0, 0, 0, 0, 0, 0, 0}, c._testGetPcSpAFBCDEHL());
        }

        @Test
        void JP_CC_N16WorksE() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(0, Opcode.LD_A_N8.encoding);
            b.write(1,  0b10);
            b.write(2,  Opcode.LD_C_N8.encoding);
            b.write(3, 0b10);
            b.write(4, Opcode.SUB_A_C.encoding);
            b.write(5,  0b11001010); // CC = 01
            b.write(6,  0x25);
            b.write(7,  0x0F);
            cycleCpu(c, 7);
            assertArrayEquals(new int [] {0x0F25, 0, 0, 0b11000000, 0, 0b10, 0, 0, 0, 0}, c._testGetPcSpAFBCDEHL());
        }
        @Test
        public void RST_U3WorksB() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            System.out.println("TEST RST");
            System.out.println("SP avant = " + c._testGetPcSpAFBCDEHL()[1]);
            b.write(0,  Opcode.LD_SP_N16.encoding);
            b.write(1,  0x13);
            b.write(2,  0x0);
            cycleCpu(c,3);
            System.out.println("SP milieu = " + c._testGetPcSpAFBCDEHL()[1]);
            b.write(3, Opcode.RST_4.encoding);
            cycleCpu(c,4);
            System.out.println("SP après = " + c._testGetPcSpAFBCDEHL()[1]);
            assertArrayEquals(new int [] {32, 0x11, 0, 0, 0, 0, 0, 0, 0, 0}, c._testGetPcSpAFBCDEHL());
            
            

            System.out.println();
        }
        
        
    /*    @Test
        public void EDI_WorksB() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            b.write(0, Opcode.EI.encoding);
            
            System.out.println(c.getIME());
            b.write(1, Opcode.DI.encoding);
            cycleCpu(c,4);
            System.out.println(c.getIME());
            
        }*/
        
        @Test
        public void readAndWriteWorksB() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            c.write(AddressMap.HIGH_RAM_START+3, 0x13);
            assertEquals(0x13,c.read(AddressMap.HIGH_RAM_START+3),0.001);
        }

        @Test
        void CALL_N16WorksE() {
            Cpu c = new Cpu();
            Ram r = new Ram(65535);
            Bus b = connect(c, r);
            System.out.println("SP avant = " + c._testGetPcSpAFBCDEHL()[1]);
            b.write(0,  Opcode.LD_SP_N16.encoding);
            b.write(1,  0x13);
            b.write(2,  0x0);
            cycleCpu(c,3);
            System.out.println("SP milieu = " + c._testGetPcSpAFBCDEHL()[1]);
            b.write(3,  Opcode.CALL_N16.encoding);
            b.write(4,  0x10);
            b.write(5,  0x12);
            cycleCpu(c, 4);
            System.out.println("SP après = " + c._testGetPcSpAFBCDEHL()[1]);
            assertArrayEquals(new int [] {0x1210, 0x11, 0, 0, 0, 0, 0, 0, 0, 0}, c._testGetPcSpAFBCDEHL());
            assertEquals(0x06, b.read(0x11), 0.1);
        }
}

