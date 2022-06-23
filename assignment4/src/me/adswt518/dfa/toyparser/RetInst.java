package me.adswt518.dfa.toyparser;

public class RetInst extends Instruction {
    private final Variable retVar;

    public RetInst(Variable retVar) {
        this.retVar = retVar;
    }

    public Variable getRetVar() {
        return retVar;
    }

    @Override
    public String toString() {
        return "ret " + retVar;
    }
}
