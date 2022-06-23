package me.adswt518.dfa.toyparser;

public class BrInst extends Instruction {
    private final Instruction condition;
    private final String trueLabel;
    private final String falseLabel;

    public BrInst(Instruction condition, String trueLabel, String falseLabel) {
        this.condition = condition;
        this.trueLabel = trueLabel;
        this.falseLabel = falseLabel;
    }

    public Instruction getCondition() {
        return condition;
    }

    public String getTrueLabel() {
        return trueLabel;
    }

    public String getFalseLabel() {
        return falseLabel;
    }

    @Override
    public String toString() {
        return "br " + condition + " " + trueLabel + " " + falseLabel;
    }
}
