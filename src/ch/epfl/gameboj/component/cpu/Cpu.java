package ch.epfl.gameboj.component.cpu;

import ch.epfl.gameboj.AddressMap;
import ch.epfl.gameboj.Bus;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.Register;
import ch.epfl.gameboj.RegisterFile;
import ch.epfl.gameboj.bits.Bit;
import ch.epfl.gameboj.bits.Bits;
import ch.epfl.gameboj.component.Clocked;
import ch.epfl.gameboj.component.Component;
import ch.epfl.gameboj.component.cpu.Alu.Flag;
import ch.epfl.gameboj.component.cpu.Alu.RotDir;
import ch.epfl.gameboj.component.cpu.Opcode.Kind;
import ch.epfl.gameboj.component.memory.Ram;

/**
 * Le processeur du Game Boy
 * @author Riand Andre
 * @author Michel François
 */
public final class Cpu implements Component, Clocked {

    private Bus bus;
    private long nextNonIdleCycle = 0;
    private static final Opcode[] DIRECT_OPCODE_TABLE = buildOpcodeTable(Opcode.Kind.DIRECT);
    private static final Opcode[] PREFIXED_OPCODE_TABLE = buildOpcodeTable(Opcode.Kind.PREFIXED);
    private int PC = 0;
    private int nextPC = 0;
    private int SP = 0;
    private boolean IME;
    private int IE;
    private int IF;
    private boolean addTotalByte;
    private Ram highRam = new Ram(AddressMap.HIGH_RAM_SIZE);

    private enum Reg implements Register {
        A, F, B, C, D, E, H, L
    }

    private final RegisterFile<Reg> registerFile = new RegisterFile<>(Reg.values());    

    private enum Reg16 implements Register {
        AF, BC, DE, HL
    }

    private enum FlagSrc {
        V0, V1, ALU, CPU
    }

    /**
     * Interruptions
     */
    public enum Interrupt implements Bit {
        VBLANK, LCD_STAT, TIMER, SERIAL, JOYPAD
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

    private boolean ieAndIfDiffZero() {
        int reg = IE & IF;
        return Bits.clip(5, reg) != 0;
    }

    @Override
    public void cycle(long cycle) {
        if (nextNonIdleCycle == Long.MAX_VALUE) {
            if (ieAndIfDiffZero())
                nextNonIdleCycle = cycle;
        }       
        if (cycle == nextNonIdleCycle) {
            reallyCycle();
        }
    }

    /**
     * Regarde si les interruptions sont activées (c'est-à-dire si IME est vrai) et si une interruption est en attente, auquel cas elle la gère; sinon, elle exécute normalement la prochaine instruction.
     */
    private void reallyCycle() {
        
        if (IME && ieAndIfDiffZero()) {
            IME = false;
            int i = Integer.lowestOneBit(IE & IF);
            IF -= i;
            push16(PC);
            PC = AddressMap.INTERRUPTS[Integer.numberOfTrailingZeros(i)];
            nextNonIdleCycle += 5;
        }
        else {            
            int opcodeValue = read8(PC);
            Opcode opcode = opcodeValue == 0xCB ? PREFIXED_OPCODE_TABLE[read8AfterOpcode()] : DIRECT_OPCODE_TABLE[opcodeValue];
            nextPC = PC + opcode.totalBytes;
            addTotalByte = true;
            dispatch(opcode);
            if (addTotalByte)
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
            boolean c = combineBit3AndC(opcode);
            Reg reg = extractReg(opcode, 0);
            int vf = Alu.add(registerFile.get(Reg.A), registerFile.get(reg), c);
            setRegFlags(Reg.A, vf);
        } break;
        case ADD_A_N8: {
            boolean c = combineBit3AndC(opcode);
            int n8 = read8AfterOpcode();
            int vf = Alu.add(registerFile.get(Reg.A), n8, c);
            setRegFlags(Reg.A, vf);
        } break;
        case ADD_A_HLR: {
            boolean c = combineBit3AndC(opcode);
            int hl = read8AtHl();
            int vf = Alu.add(registerFile.get(Reg.A), hl, c);
            setRegFlags(Reg.A, vf);
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
            int vf = reg16.name() == "AF" ? Alu.add16H(SP, 1) : Alu.add16H(reg16(reg16), 1);
            int v = Alu.unpackValue(vf);
            setReg16SP(reg16, v);
        } break;
        case ADD_HL_R16SP: {
            Reg16 reg16 = extractReg16(opcode);
            int vf = reg16.name() == "AF" ? Alu.add16H(reg16(Reg16.HL), SP) : Alu.add16H(reg16(Reg16.HL), reg16(reg16));
            int v = Alu.unpackValue(vf);
            setReg16(Reg16.HL, v);
            combineAluFlags(vf, FlagSrc.CPU, FlagSrc.V0, FlagSrc.ALU, FlagSrc.ALU);
        } break;
        case LD_HLSP_S8: {
            boolean addSP = !Bits.test(opcode.encoding, 4);
            int v = read8AfterOpcode();
            v = Bits.clip(16, Bits.signExtend8(v));
            int vf = Alu.add16L(SP, v);
            int result = Alu.unpackValue(vf);
            if (addSP) SP = result;
            else setReg16(Reg16.HL, result);
            combineAluFlags(vf, FlagSrc.V0, FlagSrc.V0, FlagSrc.ALU, FlagSrc.ALU);
        } break;

        // Subtract
        case SUB_A_R8: {
            boolean c = combineBit3AndC(opcode);
            Reg reg = extractReg(opcode, 0);
            int vf = Alu.sub(registerFile.get(Reg.A), registerFile.get(reg), c);
            setRegFlags(Reg.A, vf);
        } break;
        case SUB_A_N8: {
            boolean c = combineBit3AndC(opcode);
            int n8 = read8AfterOpcode();
            int vf = Alu.sub(registerFile.get(Reg.A), n8, c);
            setRegFlags(Reg.A, vf);
        } break;
        case SUB_A_HLR: {
            boolean c = combineBit3AndC(opcode);
            int hl = read8AtHl();
            int vf = Alu.sub(registerFile.get(Reg.A), hl, c);
            setRegFlags(Reg.A, vf);
        } break;
        case DEC_R8: {
            Reg reg = extractReg(opcode, 3);
            int vf = Alu.sub(registerFile.get(reg), 1);
            setRegFromAlu(reg, vf);
            combineAluFlags(vf, FlagSrc.ALU, FlagSrc.V1, FlagSrc.ALU, FlagSrc.CPU);
        } break;
        case DEC_HLR: {
            int vf = Alu.sub(read8AtHl(), 1);
            int v = Alu.unpackValue(vf);
            write8AtHl(v);
            combineAluFlags(vf, FlagSrc.ALU, FlagSrc.V1, FlagSrc.ALU, FlagSrc.CPU);
        } break;
        case CP_A_R8: {
            Reg reg = extractReg(opcode, 0);
            int vf = Alu.sub(registerFile.get(Reg.A), registerFile.get(reg));
            setFlags(vf);
        } break;
        case CP_A_N8: {
            int n8 = read8AfterOpcode();
            int vf = Alu.sub(registerFile.get(Reg.A), n8);
            setFlags(vf);
        } break;
        case CP_A_HLR: {
            int hl = read8AtHl();
            int vf = Alu.sub(registerFile.get(Reg.A), hl);
            setFlags(vf);
        } break;
        case DEC_R16SP: {          
            Reg16 reg16 = extractReg16(opcode);
            int v = reg16.name() == "AF" ? Bits.clip(16, SP - 1) : Bits.clip(16, reg16(reg16) - 1);
            setReg16SP(reg16, v);
        } break;

        // And, or, xor, complement
        case AND_A_N8: {
            int n8 = read8AfterOpcode();
            int vf = Alu.and(registerFile.get(Reg.A), n8);
            setRegFlags(Reg.A, vf);
        } break;
        case AND_A_R8: {
            Reg reg = extractReg(opcode, 0);
            int vf = Alu.and(registerFile.get(Reg.A), registerFile.get(reg));
            setRegFlags(Reg.A, vf);
        } break;
        case AND_A_HLR: {
            int hl = read8AtHl();
            int vf = Alu.and(registerFile.get(Reg.A), hl);
            setRegFlags(Reg.A, vf);
        } break;
        case OR_A_R8: {
            Reg reg = extractReg(opcode, 0);
            int vf = Alu.or(registerFile.get(Reg.A), registerFile.get(reg));
            setRegFlags(Reg.A, vf);
        } break;
        case OR_A_N8: {
            int n8 = read8AfterOpcode();
            int vf = Alu.or(registerFile.get(Reg.A), n8);
            setRegFlags(Reg.A, vf);
        } break;
        case OR_A_HLR: {
            int hl = read8AtHl();
            int vf = Alu.or(registerFile.get(Reg.A), hl);
            setRegFlags(Reg.A, vf);
        } break;
        case XOR_A_R8: {
            Reg reg = extractReg(opcode, 0);
            int vf = Alu.xor(registerFile.get(Reg.A), registerFile.get(reg));
            setRegFlags(Reg.A, vf);
        } break;
        case XOR_A_N8: {
            int n8 = read8AfterOpcode();
            int vf = Alu.xor(registerFile.get(Reg.A), n8);
            setRegFlags(Reg.A, vf);
        } break;
        case XOR_A_HLR: {
            int hl = read8AtHl();
            int vf = Alu.xor(registerFile.get(Reg.A), hl);
            setRegFlags(Reg.A, vf);
        } break;
        case CPL: {
            int v = Bits.complement8(registerFile.get(Reg.A));
            registerFile.set(Reg.A, v);
            combineAluFlags(0, FlagSrc.CPU, FlagSrc.V1, FlagSrc.V1, FlagSrc.CPU);
        } break;

        // Rotate, shift
        case ROTCA: {
            RotDir dir = rotDirection(opcode);
            int vf = Alu.rotate(dir, registerFile.get(Reg.A));
            setRegFromAlu(Reg.A, vf);
            combineAluFlags(vf, FlagSrc.V0, FlagSrc.V0, FlagSrc.V0, FlagSrc.ALU);
        } break;
        case ROTA: {
            RotDir dir = rotDirection(opcode);
            boolean c = registerFile.testBit(Reg.F, Flag.C);
            int vf = Alu.rotate(dir, registerFile.get(Reg.A), c);
            setRegFromAlu(Reg.A, vf);
            combineAluFlags(vf, FlagSrc.V0, FlagSrc.V0, FlagSrc.V0, FlagSrc.ALU);
        } break;
        case ROTC_R8: {
            RotDir dir = rotDirection(opcode);
            Reg reg = extractReg(opcode, 0);
            int vf = Alu.rotate(dir, registerFile.get(reg));
            setRegFlags(reg, vf);
        } break;
        case ROT_R8: {
            RotDir dir = rotDirection(opcode);
            boolean c = registerFile.testBit(Reg.F, Flag.C);
            Reg reg = extractReg(opcode, 0);
            int vf = Alu.rotate(dir, registerFile.get(reg), c);
            setRegFlags(reg, vf);
        } break;
        case ROTC_HLR: {
            RotDir dir = rotDirection(opcode);
            int vf = Alu.rotate(dir, read8AtHl());
            write8AtHlAndSetFlags(vf);
        } break;
        case ROT_HLR: {
            RotDir dir = rotDirection(opcode);
            boolean c = registerFile.testBit(Reg.F, Flag.C);
            int vf = Alu.rotate(dir, read8AtHl(), c);
            write8AtHlAndSetFlags(vf);
        } break;
        case SWAP_R8: {
            Reg reg = extractReg(opcode, 0);
            int vf = Alu.swap(registerFile.get(reg));
            setRegFlags(reg, vf);
        } break;
        case SWAP_HLR: {
            int vf = Alu.swap(read8AtHl());
            write8AtHlAndSetFlags(vf);
        } break;
        case SLA_R8: {
            Reg reg = extractReg(opcode, 0);
            int vf = Alu.shiftLeft(registerFile.get(reg));
            setRegFlags(reg, vf);
        } break;
        case SRA_R8: {
            Reg reg = extractReg(opcode, 0);
            int vf = Alu.shiftRightA(registerFile.get(reg));
            setRegFlags(reg, vf);
        } break;
        case SRL_R8: {
            Reg reg = extractReg(opcode, 0);
            int vf = Alu.shiftRightL(registerFile.get(reg));
            setRegFlags(reg, vf);
        } break;
        case SLA_HLR: {
            int vf = Alu.shiftLeft(read8AtHl());
            write8AtHlAndSetFlags(vf);
        } break;
        case SRA_HLR: {
            int vf = Alu.shiftRightA(read8AtHl());
            write8AtHlAndSetFlags(vf);
        } break;
        case SRL_HLR: {
            int vf = Alu.shiftRightL(read8AtHl());
            write8AtHlAndSetFlags(vf);
        } break;

        // Bit test and set
        case BIT_U3_R8: {
            int index = extractIndex(opcode);
            Reg reg = extractReg(opcode, 0);
            int vf = Alu.testBit(registerFile.get(reg), index);
            combineAluFlags(vf, FlagSrc.ALU, FlagSrc.V0, FlagSrc.V1, FlagSrc.CPU);
        } break;
        case BIT_U3_HLR: {
            int index = extractIndex(opcode);
            int vf = Alu.testBit(read8AtHl(), index);
            combineAluFlags(vf, FlagSrc.ALU, FlagSrc.V0, FlagSrc.V1, FlagSrc.CPU);
        } break;
        case CHG_U3_R8: {
            Boolean set = bitResSet(opcode);
            int index = extractIndex(opcode);
            Reg reg = extractReg(opcode, 0);
            int regValue = registerFile.get(reg);
            if (set) regValue |= Bits.mask(index);
            else regValue &= ~Bits.mask(index);
            registerFile.set(reg, regValue);
        } break;
        case CHG_U3_HLR: {
            Boolean set = bitResSet(opcode);
            int index = extractIndex(opcode);
            int HlValue = read8AtHl();
            if (set) HlValue |= Bits.mask(index);
            else HlValue &= ~Bits.mask(index);
            write8AtHl(HlValue);
        } break;

        // Misc. ALU
        case DAA: {
            int regAValue = registerFile.get(Reg.A);
            int regFValue = registerFile.get(Reg.F);
            boolean n = Bits.test(regFValue, Flag.N);
            boolean h = Bits.test(regFValue, Flag.H);
            boolean c = Bits.test(regFValue, Flag.C);

            int vf = Alu.bcdAdjust(regAValue, n,  h, c);
            setRegFromAlu(Reg.A, vf);
            combineAluFlags(vf, FlagSrc.ALU, FlagSrc.CPU, FlagSrc.V0, FlagSrc.ALU);
        } break;
        case SCCF: {
            boolean c = combineBit3AndC(opcode);
            if (c) combineAluFlags(0, FlagSrc.CPU, FlagSrc.V0, FlagSrc.V0, FlagSrc.V0);               
            else combineAluFlags(0, FlagSrc.CPU, FlagSrc.V0, FlagSrc.V0, FlagSrc.V1);
        } break;

        // Jumps
        case JP_HL: {
            PC = reg16(Reg16.HL);
            addTotalByte = false;
        } break;
        case JP_N16: {
            PC = read16AfterOpcode();
            addTotalByte = false;
        } break;
        case JP_CC_N16: {
            if (checkCondition(opcode)) {
                PC = read16AfterOpcode();
                addTotalByte = false;
                nextNonIdleCycle += opcode.additionalCycles;
            }   
        } break;
        case JR_E8: {
            int e8 = Bits.signExtend8(read8AfterOpcode());
            PC = Bits.clip(16, nextPC + e8);
            addTotalByte = false;
        } break;
        case JR_CC_E8: {
            if (checkCondition(opcode)) {
                int e8 = Bits.signExtend8(read8AfterOpcode());
                PC = Bits.clip(16, nextPC + e8);
                addTotalByte = false;
                nextNonIdleCycle += opcode.additionalCycles;
            }
        } break;

        // Calls and returns
        case CALL_N16: {
            push16(nextPC);
            PC = read16AfterOpcode();
            addTotalByte = false;
        } break;
        case CALL_CC_N16: {
            if (checkCondition(opcode)) {
                push16(nextPC);
                PC = read16AfterOpcode();
                addTotalByte = false;
                nextNonIdleCycle += opcode.additionalCycles;
            }
        } break;
        case RST_U3: {
            push16(nextPC);
            PC = AddressMap.RESETS[extractIndex(opcode)];
            addTotalByte = false;
        } break;
        case RET: {
            PC = pop16();
            addTotalByte = false;
        } break;
        case RET_CC: {
            if (checkCondition(opcode)) {
                PC = pop16();
                addTotalByte = false;
                nextNonIdleCycle += opcode.additionalCycles;
            }
        } break;

        // Interrupts
        case EDI: {
            IME = Bits.test(opcode.encoding, 3);
        } break;
        case RETI: {
            IME = true;
            PC = pop16();
            addTotalByte = false;
        } break;

        // Misc control
        case HALT: {
            nextNonIdleCycle = Long.MAX_VALUE;
        } break;
        case STOP:
            throw new Error("STOP is not implemented");
        default: 
            throw new IllegalArgumentException();
        }        
    }

    /**
     * @throws NullPointerException si le bus est null.
     */
    @Override
    public void attachTo(Bus bus) {
        this.bus = bus;
        bus.attach(this);
    }

    /**
     * Lève l'interruption donnée, c'est-à-dire met à 1 le bit correspondant dans le registre IF.
     * @param i : interruption à lever.
     */
    public void requestInterrupt(Interrupt i) {
        IF = Bits.set(IF, i.index(), true);
    }

    /**
     * @throws IllegalArgumentException si address ne peut pas s'écrire avec 16 bits.
     */
    @Override
    public int read(int address) {
        Preconditions.checkBits16(address);
        if (address == AddressMap.REG_IE)
            return IE;
        if (address == AddressMap.REG_IF)
            return IF;
        if (address >= AddressMap.HIGH_RAM_START && address < AddressMap.HIGH_RAM_END)
            return highRam.read(address - AddressMap.HIGH_RAM_START);
        else
            return NO_DATA;
    }

    /**
     * @throws IllegalArgumentException si address ne peut pas s'écrire avec 16 bits ou data ne peut pas s'écrire avec 8 bits.
     */
    @Override
    public void write(int address, int data) {
        Preconditions.checkBits16(address);
        Preconditions.checkBits8(data);
        if (address == AddressMap.REG_IE)
            IE = data;
        if (address == AddressMap.REG_IF)
            IF = data;
        if (address >= AddressMap.HIGH_RAM_START && address < AddressMap.HIGH_RAM_END)
            highRam.write(address - AddressMap.HIGH_RAM_START, data);
    }

    /**
     * Retourne un tableau contenant, dans l'ordre, la valeur des registres PC, SP, A, F, B, C, D, E, H et L.
     * @return un tableau tel que décrit ci-dessus.
     */
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
        write8(reg16(Reg16.HL),v);
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
        if (r == Reg16.AF)
            newV = newV & 0xFFF0;
        registerFile.set(Reg.values()[r.index() * 2], Bits.extract(newV, 8, 8));
        registerFile.set(Reg.values()[r.index() * 2 + 1], Bits.clip(8, newV));
    }


    private void setReg16SP(Reg16 r, int newV) {
        if (r == Reg16.AF)
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

    private RotDir rotDirection(Opcode opcode) {
        if (Bits.test(opcode.encoding, 3)) return RotDir.RIGHT;
        return RotDir.LEFT;
    }

    private int extractIndex(Opcode opcode) {
        return Bits.extract(opcode.encoding, 3, 3); 
    }

    private boolean bitResSet(Opcode opcode) {
        return Bits.test(opcode.encoding, 6);   
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
        Flag[] flagList = Flag.values();
        //On détermine la valeure de chaque fanion selon la valeur de FlagSrc.
        for (int i = 0; i < flags.length; i++) {
            int index = 7 - i; //On choisit l'index correspondant au bon fanions
            switch (flags[i].name()) {
            case ("V1") : registerFile.setBit(Reg.F, flagList[index], true); break;
            case ("V0") : registerFile.setBit(Reg.F, flagList[index], false); break;
            case ("ALU") : {
                boolean flag = Bits.test(vf, index);
                registerFile.setBit(Reg.F, flagList[index], flag);
            } break;
            }
        }
    }

    private boolean combineBit3AndC(Opcode opcode) {
        return Bits.test(opcode.encoding, 3) && registerFile.testBit(Reg.F, Flag.C);

    }

    private boolean checkCondition(Opcode opcode) {
        int cc = Bits.extract(opcode.encoding, 3, 2);
        switch (cc) {     
        case (0b00) : return !registerFile.testBit(Reg.F, Flag.Z) ? true : false;
        case (0b01) : return registerFile.testBit(Reg.F, Flag.Z) ? true : false;
        case (0b10) : return !registerFile.testBit(Reg.F, Flag.C) ? true : false;
        default : return registerFile.testBit(Reg.F, Flag.C) ? true : false;
        }
    }   
}