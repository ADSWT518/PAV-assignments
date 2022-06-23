package me.adswt518.dfa.toyparser;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;

public class Block {
    private final String label;
    private final List<Instruction> instructions;
    private BitSet IN;
    private BitSet OUT;
    private BitSet def;
    private BitSet use;
    private List<Block> prev;
    private List<Block> next;
    // 接下来两个成员变量用于未定义变量检测
    private BitSet undefVarBefore;
    private BitSet undefVarAfter;

    public Block(String label, List<Instruction> instructions) {
        this.label = label;
        this.instructions = instructions;
        if (!Objects.equals(label, "ENTRY") && !Objects.equals(label, "EXIT")) {
            IN = new BitSet();
            OUT = new BitSet();
            def = new BitSet();
            use = new BitSet();
            prev = new ArrayList<>();
            next = new ArrayList<>();
            undefVarBefore = new BitSet();
            undefVarAfter = new BitSet();
        }
        switch (label) {
            case "ENTRY" -> {
                OUT = new BitSet();
                next = new ArrayList<>();
                undefVarAfter = new BitSet();
            }
            case "EXIT" -> {
                IN = new BitSet();
                prev = new ArrayList<>();
                undefVarBefore = new BitSet();
            }
        }
    }

    public String getLabel() {
        return label;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public List<Instruction> getAssignBrInsts() {
        // 这里其实只有最后一个指令不是 assign，前面的指令都是 assign
        // branch 也要算进来，因为 condition 里面也可能有 use
        if (instructions == null || instructions.size() == 0) {
            return null;
        }
        List<Instruction> assignBrInsts = new java.util.ArrayList<>();
        for (Instruction inst : instructions) {
            if (inst instanceof AssignInst || inst instanceof BrInst) {
                assignBrInsts.add(inst);
            }
        }
        return assignBrInsts;
    }

    public BitSet getIN() {
        return IN;
    }

    public void setIN(BitSet IN) {
        this.IN = IN;
    }

    public BitSet getOUT() {
        return OUT;
    }

    public void setOUT(BitSet OUT) {
        this.OUT = OUT;
    }

    public BitSet getDef() {
        return def;
    }

    public void setDef(BitSet def) {
        this.def = def;
    }

    public BitSet getUse() {
        return use;
    }

    public void setUse(BitSet use) {
        this.use = use;
    }

    public List<Block> getPrev() {
        return prev;
    }

    public void setPrev(List<Block> prev) {
        this.prev = prev;
    }

    public List<Block> getNext() {
        return next;
    }

    public void setNext(List<Block> next) {
        this.next = next;
    }

    public void addPrev(Block prev) {
        this.prev.add(prev);
    }

    public void addNext(Block next) {
        this.next.add(next);
    }

    public BitSet getUndefVarBefore() {
        return undefVarBefore;
    }

    public void setUndefVarBefore(BitSet undefVarBefore) {
        this.undefVarBefore = undefVarBefore;
    }

    public BitSet getUndefVarAfter() {
        return undefVarAfter;
    }

    public void setUndefVarAfter(BitSet undefVarAfter) {
        this.undefVarAfter = undefVarAfter;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("\tBlock ").append(label).append("\n");
        sb.append("\t\tIN: ").append(IN).append("\n");
        sb.append("\t\tOUT: ").append(OUT).append("\n");
        sb.append("\t\tdef: ").append(def).append("\n");
        sb.append("\t\tuse: ").append(use).append("\n");
        sb.append("\t\tinstructions: ").append(instructions).append("\n");
        sb.append("\t\tprev: [");
        printPrevNext(sb, prev);
        sb.append("]\n");
        sb.append("\t\tnext: [");
        printPrevNext(sb, next);
        sb.append("]");
        return sb.toString();
    }

    private void printPrevNext(StringBuilder sb, List<Block> prevNext) {
        if (prevNext != null) {
            for (Block prev : prevNext) {
                sb.append(prev.getLabel()).append(" ");
            }
        }
        if (sb.charAt(sb.length() - 1) == ' ') {
            sb.deleteCharAt(sb.length() - 1);
        }
    }
}
