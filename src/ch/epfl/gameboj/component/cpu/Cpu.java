package ch.epfl.gameboj.component.cpu;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.Register;
import ch.epfl.gameboj.RegisterFile;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.Clocked;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cpu.Alu.*;
import ch.epfl.gameboj.component.cpu.Opcode.Kind;

public final class Cpu implements Component, Clocked {

    private Bus bus;
    private long nextNonIdleCycle = 0;
    private static final Opcode[] DIRECT_OPCODE_TABLE = buildOpcodeTable(Opcode.Kind.DIRECT);
    private static final Opcode[] PREFIXED_OPCODE_TABLE = buildOpcodeTable(Opcode.Kind.PREFIXED);
    private int PC = 0;
    private int SP;

    private enum Reg implements Register {
        A, F, B, C, D, E, H, L
    }
    RegisterFile<Reg> registerFile = new RegisterFile<>(Reg.values());    
    
    private enum Reg16 implements Register {
        AF, BC, DE, HL
    }
    
    private enum FlagSrc {
        V0, V1, ALU, CPU
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
    
    public void cycle(long cycle) {
        System.out.println(SP);
        if (cycle == nextNonIdleCycle) {
            int opcodeValue = read8(PC);
            Opcode opcode = DIRECT_OPCODE_TABLE[opcodeValue];           
            dispatch(opcode);
            PC += opcode.totalBytes;
            nextNonIdleCycle += opcode.cycles;
        }
    }

    private void dispatch(Opcode opcode) {

        switch (opcode.family) {

        case NOP: {
        } break;
        case LD_R8_HLR: {
            int data = read8AtHl();
            Reg reg = extractReg(opcode, 3);
            registerFile.set(reg, data);   
        } break;
        case LD_A_HLRU: {
            int data = read8AtHl(); 
            registerFile.set(Reg.A, data);
            setReg16(Reg16.HL, reg16(Reg16.HL) + extractHlIncrement(opcode));
        } break;
        case LD_A_N8R: {
            int address = AddressMap.REGS_START + read8AfterOpcode();
            registerFile.set(Reg.A, read8(address));   
        } break;
        case LD_A_CR: {
            int address = AddressMap.REGS_START + registerFile.get(Reg.C);
            registerFile.set(Reg.A, read8(address));   
        } break;
        case LD_A_N16R: {
            registerFile.set(Reg.A, read8(read16AfterOpcode()));
        } break;
        case LD_A_BCR: {
            registerFile.set(Reg.A, read8(reg16(Reg16.BC)));
        } break;
        case LD_A_DER: {
            registerFile.set(Reg.A, read8(reg16(Reg16.DE)));
        } break;
        case LD_R8_N8: {
            int data = read8AfterOpcode();
            Reg reg = extractReg(opcode, 3);
            registerFile.set(reg, data);
        } break;
        case LD_R16SP_N16: {
            int data = read16AfterOpcode();
            Reg16 reg16 = extractReg16(opcode);
            setReg16SP(reg16, data);
        } break;
        case POP_R16: {
            Reg16 reg16 = extractReg16(opcode);
            setReg16(reg16, pop16());
        } break;
        case LD_HLR_R8: {
            Reg reg = extractReg(opcode, 0);
            write8AtHl(registerFile.get(reg));
        } break;
        case LD_HLRU_A: {
            write8AtHl(registerFile.get(Reg.A));
            setReg16(Reg16.HL, reg16(Reg16.HL) + extractHlIncrement(opcode));
        } break;
        case LD_N8R_A: {
            int address = AddressMap.REGS_START + read8AfterOpcode();
            write8(address, registerFile.get(Reg.A));
        } break;
        case LD_CR_A: {
            int address = AddressMap.REGS_START + registerFile.get(Reg.C);
            write8(address, registerFile.get(Reg.A));
        } break;
        case LD_N16R_A: {
            write8(read16AfterOpcode(), registerFile.get(Reg.A));
        } break;
        case LD_BCR_A: {
            write8(reg16(Reg16.BC), registerFile.get(Reg.A));
        } break;
        case LD_DER_A: {
            write8(reg16(Reg16.DE), registerFile.get(Reg.A));
        } break;
        case LD_HLR_N8: {
            int data = read8AfterOpcode();
            write8AtHl(data);
        } break;
        case LD_N16R_SP: {
            write16(read16AfterOpcode(), SP);
        } break;
        case LD_R8_R8: {
            Reg regH = extractReg(opcode, 3);
            Reg regL = extractReg(opcode, 0);
            if (registerFile.get(regH) != registerFile.get(regL))
                registerFile.set(regH, registerFile.get(regL));
        } break;
        case LD_SP_HL: {
            SP = reg16(Reg16.HL);
        } break;
        case PUSH_R16: {
            Reg16 reg16 = extractReg16(opcode);
            push16(reg16(reg16));
        } break;
        
        // Add
        case ADD_A_R8: {
        } break;
        case ADD_A_N8: {
        } break;
        case ADD_A_HLR: {
        } break;
        case INC_R8: {
            Reg reg = extractReg(opcode, 3);
            int vf = Alu.add(registerFile.get(reg), 1);
            setRegFromAlu(reg, vf);
            combineAluFlags(vf, FlagSrc.ALU, FlagSrc.V0, FlagSrc.ALU, FlagSrc.CPU);
        } break;
        case INC_HLR: {
            int vf = Alu.add(read8AtHl(), 1);
            int v = Alu.unpackValue(vf);
            write8AtHl(v);
            combineAluFlags(vf, FlagSrc.ALU, FlagSrc.V0, FlagSrc.ALU, FlagSrc.CPU);
        } break;
        case INC_R16SP: {
            Reg16 reg16 = extractReg16(opcode);
            int vf = Alu.add16H(reg16(reg16), 1);
            int v = Alu.unpackValue(vf);
            setReg16SP(reg16, v);
        } break;
        case ADD_HL_R16SP: {
            Reg16 reg16 = extractReg16(opcode);
            int vf = Alu.add16H(reg16(Reg16.HL), reg16(reg16));
            int v = Alu.unpackFlags(vf);
            setReg16(Reg16.HL, v);
            combineAluFlags(vf, FlagSrc.CPU, FlagSrc.V0, FlagSrc.ALU, FlagSrc.ALU);
        } break;
        case LD_HLSP_S8: {
        } break;

        // Subtract
        case SUB_A_R8: {
        } break;
        case SUB_A_N8: {
        } break;
        case SUB_A_HLR: {
        } break;
        case DEC_R8: {
        } break;
        case DEC_HLR: {
        } break;
        case CP_A_R8: {
        } break;
        case CP_A_N8: {
        } break;
        case CP_A_HLR: {
        } break;
        case DEC_R16SP: {
        } break;

        // And, or, xor, complement
        case AND_A_N8: {
        } break;
        case AND_A_R8: {
        } break;
        case AND_A_HLR: {
        } break;
        case OR_A_R8: {
        } break;
        case OR_A_N8: {
        } break;
        case OR_A_HLR: {
        } break;
        case XOR_A_R8: {
        } break;
        case XOR_A_N8: {
        } break;
        case XOR_A_HLR: {
        } break;
        case CPL: {
        } break;

        // Rotate, shift
        case ROTCA: {
        } break;
        case ROTA: {
        } break;
        case ROTC_R8: {
        } break;
        case ROT_R8: {
        } break;
        case ROTC_HLR: {
        } break;
        case ROT_HLR: {
        } break;
        case SWAP_R8: {
        } break;
        case SWAP_HLR: {
        } break;
        case SLA_R8: {
        } break;
        case SRA_R8: {
        } break;
        case SRL_R8: {
        } break;
        case SLA_HLR: {
        } break;
        case SRA_HLR: {
        } break;
        case SRL_HLR: {
        } break;

        // Bit test and set
        case BIT_U3_R8: {
        } break;
        case BIT_U3_HLR: {
        } break;
        case CHG_U3_R8: {
        } break;
        case CHG_U3_HLR: {
        } break;

        // Misc. ALU
        case DAA: {
        } break;
        case SCCF: {
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
        tab[0] = PC;
        tab[1] = SP;
        for (int i = 2; i < tab.length; i++) {
            tab[i] = registerFile.get(Reg.values()[i-2]);
        }
        return tab;
    }


    // METHODES PRIVEES POUR LE BUS

    private int read8(int address) {
        return bus.read(address);
    }

    private int read8AtHl() {
        return bus.read(reg16(Reg16.HL));
    }

    private int read8AfterOpcode() {
        return bus.read(PC+1);
    }

    private int read16(int address) {  
        return Bits.make16(bus.read(address+1), bus.read(address));
    }

    private int read16AfterOpcode() {
        return read16(PC+1);

    }

    private void write8(int address, int v) {
        bus.write(address, v);
    }

    private void write16(int address, int v) {
        write8(address, Bits.clip(8, v));
        write8(Bits.clip(16, address + 1), Bits.extract(v, 8, 8));
    }

    private void write8AtHl(int v){
        bus.write(reg16(Reg16.HL), v);
    }

    private void push16(int v) {
        SP -= 2; 
        SP = Bits.clip(16, SP);
        write16(SP, v);
    }

    private int pop16() {
        int val = read16(SP);
        SP += 2;
        SP = Bits.clip(16, SP);
        return val;

    }
    
    // GESTION DES PAIRES DE REGISTRES
    
    private int reg16(Reg16 r) {
        int h = registerFile.get(Reg.values()[r.index() * 2]);
        int l = registerFile.get(Reg.values()[r.index() * 2 + 1]);
        return Bits.make16(h, l);
    }
    
    private void setReg16(Reg16 r, int newV) {
        Preconditions.checkBits16(newV);
        if (r.name() == "AF")
            newV = newV & 0xFFF0;
        registerFile.set(Reg.values()[r.index() * 2], Bits.extract(newV, 8, 8));
        registerFile.set(Reg.values()[r.index() * 2 + 1], Bits.clip(8, newV));
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
        return (Bits.test(opcode.encoding, 4)) ? -1 : 1;
    }
    
    private RotDir rotDirection(int v) {
        if (Bits.test(v, 3)) return RotDir.LEFT;
        return RotDir.RIGHT;
    }
    
    private int extractBit(int v) {
        return Bits.extract(v, 3, 3); 
    }
    
    private boolean bitResSet(int v) {
        return Bits.test(v, 6);   
    }

    // Gestion des fanions
    
    private void setRegFromAlu(Reg r, int vf) {
        registerFile.set(r, Alu.unpackValue(vf));
    }
    
    private void setFlags(int valueFlags) {
        registerFile.set(Reg.F, Alu.unpackFlags(valueFlags));
    }
    
    private void setRegFlags(Reg r, int vf) {
        setRegFromAlu(r, vf);
        setFlags(vf);
    }
    
    private void write8AtHlAndSetFlags(int vf) {
        write8AtHl(Alu.unpackValue(vf));
        setFlags(vf);
    }
    
    private void combineAluFlags(int vf, FlagSrc z, FlagSrc n, FlagSrc h, FlagSrc c) {
        FlagSrc[] flags = {z, n, h, c};
        boolean[] tab = new boolean[4];
        for (int i = 0; i < flags.length; i++) {
            if (flags[i].name() == "V1")
                tab[i] = true;
            else if (flags[i].name() == "ALU")
                tab[i] = Bits.test(vf, 7 - i); //On choisit l'index correspondant au bon fanions dans vf
            else if (flags[i].name() == "CPU");
                tab[i] = Bits.test(registerFile.get(Reg.F), 7 - i); //On choisit l'index correspondant au bon fanions dans le registre F.
        }
        int newValue = Alu.maskZNHC(tab[0], tab[1], tab[2], tab[3]);
        registerFile.set(Reg.F, newValue);
    }

}
