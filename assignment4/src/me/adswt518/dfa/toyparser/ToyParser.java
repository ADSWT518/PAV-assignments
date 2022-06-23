package me.adswt518.dfa.toyparser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ToyParser {
    private final char[] input;
    private int pos;
    private List<Variable> variableList;  // used for numbering variables for BitSet

    public ToyParser(String source) {
        this.input = source.toCharArray();
        this.pos = 0;
        this.variableList = new ArrayList<>();
    }

    public Function parse() {
        Function function = parseFunction();
        variableList = variableList.stream().distinct().collect(Collectors.toList());
        return function;
    }

    public List<Variable> getVariableList() {
        return variableList;
    }

    private Function parseFunction() {
        if (startsWith("define")) {
            if (!parseKeyword().equals("define")) {
                throw new RuntimeException("Expected define");
            }
            consumeWhitespace();
            for (Function.Type type : Function.Type.values()) {
                if (startsWith(type.getName())) {
                    if (!parseKeyword().equals(type.getName())) {
                        throw new RuntimeException("Expected " + type.getName());
                    }
                    consumeWhitespace();
                    String name = parseName();
                    if (consumeChar() != '(') {
                        throw new RuntimeException("Expected (");
                    }
                    if (consumeChar() != ')') {
                        throw new RuntimeException("Expected )");
                    }
                    consumeWhitespace();
                    if (consumeChar() != '{') {
                        throw new RuntimeException("Expected {");
                    }
                    consumeWhitespace();
                    List<Block> blocks = new ArrayList<>();
                    blocks.add(new Block("ENTRY", null));
                    while (!startsWith("}")) {
                        blocks.add(parseBlock());
                        consumeWhitespace();
                    }
                    blocks.add(new Block("EXIT", null));
                    if (consumeChar() != '}') {
                        throw new RuntimeException("Expected }");
                    }
                    return new Function(type, name, blocks);
                }
            }
            return null;
        }
        return null;
    }

    private Block parseBlock() {
        String label = parseLabel();
        if (consumeChar() != ':') {
            throw new RuntimeException("Expected :");
        }
        consumeWhitespace();
        List<Instruction> instructions = new ArrayList<>();
        while (!startsWith("jmp") && !startsWith("br") && !startsWith("ret")) {
            instructions.add(parseAssignInst());
        }
        instructions.add(parseFinalInstruction());
        return new Block(label, instructions);
    }

    private Instruction parseFinalInstruction() {
        if (startsWith("jmp")) {
            return parseJmpInst();
        } else if (startsWith("br")) {
            return parseBrInst();
        } else if (startsWith("ret")) {
            return parseRetInst();
        } else {
            throw new RuntimeException("Expected jmp, br or ret");
        }
    }

    private Instruction parseAssignInst() {
        Variable left = parseVariable();
        consumeWhitespace();
        if (consumeChar() != '=') {
            throw new RuntimeException("Expected =");
        }
        consumeWhitespace();
        Instruction right = parseInstruction();
        consumeWhitespace();
        return new AssignInst(left, right);
    }

    private Instruction parseInstruction() {
        for (BinInst.Type type : BinInst.Type.values()) {
            if (startsWith(type.getName())) {
                return parseBinInst(type);
            }
        }
        for (CmpInst.Type type : CmpInst.Type.values()) {
            if (startsWith(type.getName())) {
                return parseCmpInst(type);
            }
        }
        return parseVariable();
    }

    private Instruction parseBinInst(BinInst.Type type) {
        String name = type.getName();
        for (char c : name.toCharArray()) {
            if (consumeChar() != c) {
                throw new RuntimeException("Expected " + name);
            }
        }
        consumeWhitespace();
        Instruction left = parseInstruction();
        consumeWhitespace();
        Instruction right = parseInstruction();
        return new BinInst(type, left, right);
    }

    private Instruction parseCmpInst(CmpInst.Type type) {
        String name = type.getName();
        for (char c : name.toCharArray()) {
            if (consumeChar() != c) {
                throw new RuntimeException("Expected " + name);
            }
        }
        consumeWhitespace();
        Instruction left = parseInstruction();
        consumeWhitespace();
        Instruction right = parseInstruction();
        return new CmpInst(type, left, right);
    }

    private Instruction parseJmpInst() {
        if (startsWith("jmp")) {
            if (!parseKeyword().equals("jmp")) {
                throw new RuntimeException("Expected jmp");
            }
            consumeWhitespace();
            String label = parseLabel();
            consumeWhitespace();
            return new JmpInst(label);
        }
        return null;
    }

    private Instruction parseBrInst() {
        if (startsWith("br")) {
            if (!parseKeyword().equals("br")) {
                throw new RuntimeException("Expected br");
            }
            consumeWhitespace();
            Instruction condition = parseInstruction();
            consumeWhitespace();
            String trueLabel = parseLabel();
            consumeWhitespace();
            String falseLabel = parseLabel();
            consumeWhitespace();
            return new BrInst(condition, trueLabel, falseLabel);
        }
        return null;
    }

    private Instruction parseRetInst() {
        if (startsWith("ret")) {
            if (!parseKeyword().equals("ret")) {
                throw new RuntimeException("Expected ret");
            }
            consumeWhitespace();
            Variable value = parseVariable();
            consumeWhitespace();
            return new RetInst(value);
        }
        return null;
    }

    private Variable parseVariable() {
        return new Variable(parseName());
    }

    private String parseKeyword() {
        return consumeWhile(Character::isLetterOrDigit);
    }

    private String parseName() {
        if (nextChar() == '@') {
            consumeChar();
            return '@' + consumeWhile(Character::isLetterOrDigit);
        }
        if (nextChar() == '%') {
            consumeChar();
            String varName = '%' + consumeWhile(Character::isLetterOrDigit);
            Variable var = new Variable(varName);
            variableList.add(var);
            return varName;
        }
        return consumeWhile(Character::isDigit);
    }

    private String parseLabel() {
        return consumeWhile(Character::isLetterOrDigit);
    }

    private char consumeChar() {
        return input[pos++];
    }

    private boolean startsWith(String s) {
        return new String(input, pos, input.length - pos).startsWith(s);
    }

    private char nextChar() {
        return input[pos];
    }

    private boolean isEOF() {
        return pos >= input.length;
    }

    private String consumeWhile(Predicate<Character> predicate) {
        StringBuilder sb = new StringBuilder();
        while (!isEOF() && predicate.test(input[pos])) {
            sb.append(consumeChar());
        }
        return sb.toString();
    }

    private void consumeWhitespace() {
        consumeWhile(Character::isWhitespace);
    }
}
