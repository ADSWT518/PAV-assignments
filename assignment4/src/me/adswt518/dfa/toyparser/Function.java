package me.adswt518.dfa.toyparser;

import java.util.List;

public class Function {

    public enum Type {
        I32("i32"),
        I1("i1");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private final Type returnType;
    private final String name;
    private final List<Block> blocks;

    public Function(Type returnType, String name, List<Block> blocks) {
        this.returnType = returnType;
        this.name = name;
        this.blocks = blocks;
    }

    public Type getReturnType() {
        return returnType;
    }

    public String getName() {
        return name;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public Block getBlock(String label) {
        for (Block block : blocks) {
            if (block.getLabel().equals(label)) {
                return block;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Function{" +
                "returnType=" + returnType +
                ", name='" + name + '\'' +
                ", blocks=" + blocks +
                '}';
    }

    public Block getEntryBlock() {
        return blocks.get(0);
    }

    public Block getExitBlock() {
        return blocks.get(blocks.size() - 1);
    }

    public Block getBlock(int index) {
        return blocks.get(index);
    }

    public int getBlockCount() {
        return blocks.size();
    }
}
