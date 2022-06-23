package me.adswt518.dfa.toyparser;

public class BinInst extends Instruction {

    public enum Type {
        ADD("add"),
        SUB("sub"),
        MUL("mul"),
        DIV("div"),
        MOD("mod");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private final Type type;
    private final Instruction left;
    private final Instruction right;

    public BinInst(Type type, Instruction left, Instruction right) {
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
        return type.getName() + " " + left + " " + right;
    }
}