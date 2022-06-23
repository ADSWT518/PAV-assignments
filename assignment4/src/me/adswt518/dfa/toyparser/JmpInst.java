package me.adswt518.dfa.toyparser;

public class JmpInst extends Instruction {
    private String label;

    public JmpInst(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "jmp " + label;
    }
}
