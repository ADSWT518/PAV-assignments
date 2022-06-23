package me.adswt518.dfa.toyparser;

public class AssignInst extends Instruction {
    private final Variable left;
    private final Instruction right;

    public AssignInst(Variable left, Instruction right) {
        this.left = left;
        this.right = right;
    }

    public Variable getLeft() {
        return left;
    }

    public Instruction getRight() {
        return right;
    }

    @Override
    public String toString() {
        return left + " = " + right;
    }
}