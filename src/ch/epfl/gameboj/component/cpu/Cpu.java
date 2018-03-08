package ch.epfl.gameboj.component.cpu;

import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.Register;
import ch.epfl.gameboj.RegisterFile;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.Clocked;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cpu.Opcode.Kind;

public final class Cpu implements Component, Clocked {

    private Bus bus;
    private long nextNonIdleCycle;
    private static final Opcode[] DIRECT_OPCODE_TABLE = buildOpcodeTable(Opcode.Kind.DIRECT);
    private int PC;
    private int SP;

    private enum Reg implements Register {
        A, F, B, C, D, E, H, L
    }
    RegisterFile<Reg> registerFile = new RegisterFile<>(Reg.values());    
    
    private enum Reg16 implements Register {
        AF, BC, DE, HL
    }

    public void cycle(long cycle) {
        if (cycle == nextNonIdleCycle) return;
        else dispatch();
    }

    private static Opcode[] buildOpcodeTable(Kind direct) {
        Opcode a[] = new Opcode[256];
        for (Opcode o: Opcode.values()) {
            if (o.kind == direct) {
                a[o.encoding] = o;
            }
        }
        return a;
    }

    private void dispatch() {
        int opcodeValue = read8(PC);
        Opcode opcode = DIRECT_OPCODE_TABLE[opcodeValue];
        switch (opcode.family) {

        case NOP: {
        } break;
        case LD_R8_HLR: {
            for (int i = 0; i < Reg.values().length; i++) {
                write
            }
        } break;
        case LD_A_HLRU: {
        } break;
        case LD_A_N8R: {
        } break;
        case LD_A_CR: {
        } break;
        case LD_A_N16R: {
        } break;
        case LD_A_BCR: {
        } break;
        case LD_A_DER: {
        } break;
        case LD_R8_N8: {
        } break;
        case LD_R16SP_N16: {
        } break;
        case POP_R16: {
        } break;
        case LD_HLR_R8: {
        } break;
        case LD_HLRU_A: {
        } break;
        case LD_N8R_A: {
        } break;
        case LD_CR_A: {
        } break;
        case LD_N16R_A: {
        } break;
        case LD_BCR_A: {
        } break;
        case LD_DER_A: {
        } break;
        case LD_HLR_N8: {
        } break;
        case LD_N16R_SP: {
        } break;
        case LD_R8_R8: {
        } break;
        case LD_SP_HL: {
        } break;
        case PUSH_R16: {
        } break;
        default: 
            throw new IllegalArgumentException();
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
    public void write(int address, int data) {}

    public int[] _testGetPcSpAFBCDEHL() {
        int[] tab = new int[10];
        return tab;
    }


    // METHODES PRIVEES POUR LE BUS

    private int read8(int address) {
        return bus.read(address);
    }

    private int read8AtHl(){
        return bus.read(reg16(Reg16.HL));
    }

    private int read8AfterOpcode(){
        return bus.read(PC+1);
    }

    private int read16(int address){  
        return Bits.make16(bus.read(address+1), bus.read(address));
    }

    private int read16AfterOpcode(){
        return read16(PC+1);

    }

    private void write8(int address, int v){
        bus.write(address, v);
    }

    private void write16(int address, int v){
        write8(address, Bits.clip(8, v));
        write8(address, Bits.extract(v, 8, 8));
    }

    private void write8AtHl(int v){
        bus.write(reg16(Reg16.HL), v);             // HL ?
    }

    private void push16(int v){
        SP -= 2;             
        write16(SP, v);
    }

    private int pop16(){
        int val = read16(SP);
        SP += 2;
        return val;

    }
    
    // GESTION DES PAIRES DE REGISTRES
    
    private int reg16(Reg16 r) {
        int h = registerFile.get(Reg.values()[r.index() * 2 + 1]);
        int l = registerFile.get(Reg.values()[r.index() * 2]);
        return Bits.make16(h, l);
    }
    
    private void setReg16(Reg16 r, int newV) {
        Preconditions.checkBits16(newV);
        if (r.name() == "AF")
            newV = newV & 0xFFF0;
        registerFile.set(Reg.values()[r.index() * 2 + 1], Bits.extract(newV, 8, 8));
        registerFile.set(Reg.values()[r.index() * 2], Bits.clip(8, newV));
    }
    
    private void setReg16SP(Reg16 r, int newV) {
        if (r.name() == "AF")
            SP = newV;
        else
            setReg16(r, newV);       
    }

    // Extraction de paramètres
    
    private Reg extractReg(Opcode opcode, int startBit) {
        int a = Bits.extract(opcode.encoding, startBit, 3);
        if (a < 6) return Reg.values()[a+2];
        else if (a == 7) return Reg.A;
        else return null;
    }
    
    private Reg16 extractReg16(Opcode opcode) {
        int a = Bits.extract(opcode.encoding, 4, 2);
        a = Bits.clip(2, a+1);
        return Reg16.values()[a];
    }
    
    private int extractHlIncrement(Opcode opcode) {
        return (Bits.test(opcode.encoding, 4)) ? 1 : -1;
    }


}
