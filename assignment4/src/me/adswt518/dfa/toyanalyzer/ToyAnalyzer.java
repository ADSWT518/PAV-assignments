package me.adswt518.dfa.toyanalyzer;

import me.adswt518.dfa.toyparser.*;

import java.util.*;

public record ToyAnalyzer(List<Variable> variableList, Function function, boolean isLVA) {

    // 参数中 isLVA = true 时，代表此时正在进行活跃变量分析（第 1 题），否则是正在进行未定义变量检测（第 2 题）

    public void analyze() {
        List<Block> revBlocks = new ArrayList<>(function.getBlocks());
        Collections.reverse(revBlocks); // 活跃变量分析是逆向的
        analyzeNext(function.getBlocks());
        analyzePrev(function.getBlocks());
        analyzeDefUse(revBlocks);
        liveVariableAnalysis(revBlocks);
    }

    public void UndefVariableCheck() {
        analyze();
        List<Block> blocks = function.getBlocks();
        BitSet undefV = function.getBlock("ENTRY").getOUT(); // 未定义的变量
        function.getBlock("ENTRY").setUndefVarAfter(undefV);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Block block : blocks.subList(1, blocks.size() - 1)) {
                BitSet undefVBef = new BitSet();
                for (Block prev : block.getPrev()) {
                    undefVBef.or(prev.getUndefVarAfter());
                }
                if (!undefVBef.equals(block.getUndefVarBefore())) {
                    changed = true;
                }
                block.setUndefVarBefore(undefVBef);
                BitSet undefVAfter = new BitSet();
                undefVAfter.or(undefVBef);
                undefVAfter.andNot(block.getDef());
                if (!undefVAfter.equals(block.getUndefVarAfter())) {
                    changed = true;
                }
                block.setUndefVarAfter(undefVAfter);
            }
        }
        System.out.println("未定义的变量：" + printBitSet(undefV));

        List<Block> usedBlock = new ArrayList<>(); // 使用了未定义的变量的基本块
        for (Block block : blocks.subList(1, blocks.size() - 1)) {
            if (block.getUndefVarBefore().intersects(block.getUse())) {
                usedBlock.add(block);
            }
        }
        List<Instruction> usedInst = new ArrayList<>();
        for (Block block : usedBlock) {
            for (Instruction instruction : block.getInstructions()) {
                if (getUsedInst(instruction, undefV)) {
                    usedInst.add(instruction);
                }
            }
        }

        System.out.println("使用了未定义的变量的指令：" + usedInst);
        printUndefVar(blocks);
    }

    private void analyzeNext(List<Block> blocks) {
        Block entryBlock = blocks.get(0);
        entryBlock.setNext(List.of(blocks.get(1)));
        for (Block block : blocks.subList(1, blocks.size() - 1)) {
            List<Instruction> instructions = block.getInstructions();
            Instruction finalInstruction = instructions.get(instructions.size() - 1);
            switch (finalInstruction.getClass().getSimpleName()) {
                case "JmpInst" -> {
                    JmpInst jmpInst = (JmpInst) finalInstruction;
                    block.addNext(function.getBlock(jmpInst.getLabel()));
                    // debugger 会在这里栈溢出，因为它尝试帮我展开 next，引起无限递归
                    // 所以好像最开始在 next 中保存 label 的方法好像好一些。。。
                }
                case "BrInst" -> {
                    BrInst brInst = (BrInst) finalInstruction;
                    block.addNext(function.getBlock(brInst.getTrueLabel()));
                    block.addNext(function.getBlock(brInst.getFalseLabel()));
                }
                case "RetInst" -> block.addNext(function.getBlock("EXIT"));
            }
        }
    }

    private void analyzePrev(List<Block> blocks) {
        Block exitBlock = blocks.get(blocks.size() - 1);
        exitBlock.setPrev(List.of(blocks.get(blocks.size() - 2)));
        for (Block block : blocks.subList(0, blocks.size() - 1)) {
            for (Block next : block.getNext()) {
                if (next.getLabel().equals("EXIT")) {
                    continue;
                }
                next.addPrev(block);
            }
        }
    }

    private void analyzeDefUse(List<Block> revBlocks) {
        for (Block block : revBlocks.subList(1, revBlocks.size() - 1)) {
            List<Instruction> assignBrInsts = block.getAssignBrInsts();
            List<Instruction> revInsts = new ArrayList<>(assignBrInsts);
            Collections.reverse(revInsts);
            BitSet def = block.getDef();
            BitSet use = block.getUse();
            for (Instruction assignBrInst : revInsts) {
                analyzeDefUseInst(assignBrInst, def, use);
            }
            block.setDef(def);
            block.setUse(use);
        }
    }

    private void analyzeDefUseInst(Instruction instruction, BitSet def, BitSet use) {
        if (instruction instanceof AssignInst assignInst) {
            Variable left = assignInst.getLeft();
            int leftIndex = variableList.indexOf(left);
            if (leftIndex != -1) {
                def.set(leftIndex);
                use.clear(leftIndex);
            }
            Instruction right = assignInst.getRight();
            analyzeDefUseInst(right, def, use);
        } else if (instruction instanceof BinInst binInst) {
            Instruction left = binInst.getLeft();
            analyzeDefUseInst(left, def, use);
            Instruction right = binInst.getRight();
            analyzeDefUseInst(right, def, use);
        } else if (instruction instanceof CmpInst cmpInst) {
            Instruction left = cmpInst.getLeft();
            analyzeDefUseInst(left, def, use);
            Instruction right = cmpInst.getRight();
            analyzeDefUseInst(right, def, use);
        } else if (instruction instanceof Variable variable) {
            int index = variableList.indexOf(variable);
            if (index != -1) {
                use.set(index);
                if (isLVA) {
                    // 只有进行活跃变量分析时才会清除 def
                    // 在进行未定义变量检测时，保留前面的 def，告诉后面的 block，该变量在这里定义了
                    def.clear(index);
                }
            }
        } else if (instruction instanceof BrInst brInst) {
            Instruction condition = brInst.getCondition();
            analyzeDefUseInst(condition, def, use);
        }
    }

    private void liveVariableAnalysis(List<Block> revBlocks) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Block block : revBlocks.subList(1, revBlocks.size() - 1)) {
                BitSet OUT = new BitSet();
                for (Block next : block.getNext()) {
                    OUT.or(next.getIN());
                }
                if (!OUT.equals(block.getOUT())) {
                    changed = true;
                }
                block.setOUT(OUT);
                BitSet IN = new BitSet();
                IN.or(block.getOUT());
                IN.andNot(block.getDef());
                IN.or(block.getUse());
                if (!IN.equals(block.getIN())) {
                    changed = true;
                }
                block.setIN(IN);
            }
            BitSet OUT = new BitSet();
            for (Block next : revBlocks.get(revBlocks.size() - 1).getNext()) {
                OUT.or(next.getIN());
            }
            if (!OUT.equals(revBlocks.get(revBlocks.size() - 1).getOUT())) {
                changed = true;
            }
            revBlocks.get(revBlocks.size() - 1).setOUT(OUT);
        }
    }

    public void printLVAResult(List<Block> blocks) {
        for (Block block : blocks) {
            System.out.println(block.getLabel());
            System.out.println("\tIN: " + printBitSet(block.getIN()));
            System.out.println("\tOUT: " + printBitSet(block.getOUT()));
            System.out.println("\tDEF: " + printBitSet(block.getDef()));
            System.out.println("\tUSE: " + printBitSet(block.getUse()));
        }
    }

    private String printBitSet(BitSet bitSet) {
        if (bitSet == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
            sb.append(variableList.get(i).getName()).append(" ");
        }
        if (sb.charAt(sb.length() - 1) == ' ') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }

    private void printUndefVar(List<Block> blocks) {
        for (Block block : blocks) {
            System.out.println(block.getLabel());
            System.out.println("\tundefVarBefore: " + printBitSet(block.getUndefVarBefore()));
            System.out.println("\tundefVarAfter: " + printBitSet(block.getUndefVarAfter()));
            System.out.println("\tDEF: " + printBitSet(block.getDef()));
            System.out.println("\tUSE: " + printBitSet(block.getUse()));
        }
    }

    private boolean getUsedInst(Instruction instruction, BitSet undefV) {
        // 返回该未定义变量是否在该指令中被使用
        switch (instruction.getClass().getSimpleName()) {
            case "AssignInst" -> {
                Instruction right = ((AssignInst) instruction).getRight();
                return getUsedInst(right, undefV);
            }
            case "BinInst" -> {
                Instruction left = ((BinInst) instruction).getLeft();
                Instruction right = ((BinInst) instruction).getRight();
                return getUsedInst(left, undefV) || getUsedInst(right, undefV);
            }
            case "BrInst" -> {
                Instruction condition = ((BrInst) instruction).getCondition();
                return getUsedInst(condition, undefV);
            }
            case "CmpInst" -> {
                Instruction left = ((CmpInst) instruction).getLeft();
                Instruction right = ((CmpInst) instruction).getRight();
                return getUsedInst(left, undefV) || getUsedInst(right, undefV);
            }
            case "Variable" -> {
                int index = variableList.indexOf((Variable) instruction);
                if (index != -1) {
                    if (undefV.get(index)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
