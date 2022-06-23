package me.adswt518.dfa.toyparser;

public class CmpInst extends Instruction {

    public enum Type {
        EQ("eq"),
        NE("ne"),
        LT("lt"),
        LE("le"),
        GT("gt"),
        GE("ge");

        private String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private Type type;
    private Instruction left;
    private Instruction right;

    public CmpInst(Type type, Instruction left, Instruction right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }

    public Type getType() {
        return type;
    }

    public Instruction getLeft() {
        return left;
    }

    public Instruction getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "cmp " + type.getName() + " " + left + " " + right;
    }
}
