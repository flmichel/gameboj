package ch.epfl.gameboj.component.cpu;

import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.Register;
import ch.epfl.gameboj.component.Clocked;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cpu.Opcode.Kind;

public final class Cpu implements Component, Clocked {

    private Bus bus;
    private long nextNonIdleCycle;
    private static final Opcode[] DIRECT_OPCODE_TABLE =
            buildOpcodeTable(Opcode.Kind.DIRECT);

    private enum Reg implements Register {
        A, F, B, C, D, E, H, L
    }

    private enum Reg16 implements Register {
        AF, BC, DE, HL
    }

    @Override
    public void cycle(long cycle) {
        if (cycle == nextNonIdleCycle) return;
        //...

    }

    private static Opcode[] buildOpcodeTable(Kind direct) {
        for (Opcode o: Opcode.values()) {
            if (o.kind == direct) {
                DIRECT_OPCODE_TABLE[0] = o;  // [?]
            }

        }
        return null;
    }

    private void dispatch(int bit) {
        switch (bit) {

//        case NOP: {
//        } break;
//        case LD_R8_HLR: {
//        } break;
//        case LD_A_HLRU: {
//        } break;
//        case LD_A_N8R: {
//        } break;
//        case LD_A_CR: {
//        } break;
//        case LD_A_N16R: {
//        } break;
//        case LD_A_BCR: {
//        } break;
//        case LD_A_DER: {
//        } break;
//        case LD_R8_N8: {
//        } break;
//        case LD_R16SP_N16: {
//        } break;
//        case POP_R16: {
//        } break;
//        case LD_HLR_R8: {
//        } break;
//        case LD_HLRU_A: {
//        } break;
//        case LD_N8R_A: {
//        } break;
//        case LD_CR_A: {
//        } break;
//        case LD_N16R_A: {
//        } break;
//        case LD_BCR_A: {
//        } break;
//        case LD_DER_A: {
//        } break;
//        case LD_HLR_N8: {
//        } break;
//        case LD_N16R_SP: {
//        } break;
//        case LD_R8_R8: {
//        } break;
//        case LD_SP_HL: {
//        } break;
//        case PUSH_R16: {
//        } break;


        }
    }

    public void attachTo(Bus bus) {
        this.bus = bus;
    }

    @Override
    public int read(int address) {
        return NO_DATA;
    }

    @Override
    public void write(int address, int data) {
        // TODO Auto-generated method stub        
    }

    public int[] _testGetPcSpAFBCDEHL() {
        int[] tab = new int[10];


        return tab;
    }


    // METHODES PRIVEES POUR LE BUS

    private int read8(int address){
        return bus.read(address);
    }

    private int read8AtHl(){
        return bus.read(0b10);  // HL ???
    }

    private int read8AfterOpcode(){
        return bus.read(+1); // PC+1?
    }

    private int read16(int address){  // 16 ??
        return bus.read(address);
    }

    private int read16AfterOpcode(){
        return bus.read(0+1);  // PC+1? 16 ??

    }

    private void write8(int address, int v){
        Preconditions.checkBits8(v);
        bus.write(address, v);
    }

    private void write16(int address, int v){
        Preconditions.checkBits16(v);
        bus.write(address, v);
    }

    private void write8AtHl(int v){
        Preconditions.checkBits8(v);
        bus.write(0b10, v);             // HL ?
    }

    private void push16(int v){
        Preconditions.checkBits16(v);
        int newAddress = +2;            // SP ??  
        bus.write(newAddress, v);
    }

    private int pop16(){
        int SP = 0;
        int temp = bus.read(SP);
        SP += 2;
        
        return temp;

    }
    
    // GESTION DES PAIRES DE REGISTRES
    
    private int reg16(Reg16 r) {
        return r.index();
    }
    
    private void setReg16(Reg16 r, int newV) {
        if (r.name() == "AF") {
            
        } else {
            
        }
    }
    
    private void setReg16SP(Reg16 r, int newV) {
        
    }

    // Extraction de paramètres
    
    private Reg extractReg(Opcode opcode, int startBit) {
        return null;      
    }
    
    private Reg16 extractReg16(Opcode opcode) {
        return null;       
    }
    
    private int extractHlIncrement(Opcode opcode) {
        return 0;      
    }


}
