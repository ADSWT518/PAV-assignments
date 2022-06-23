package me.adswt518.dfa;

import me.adswt518.dfa.toyanalyzer.ToyAnalyzer;
import me.adswt518.dfa.toyparser.Function;
import me.adswt518.dfa.toyparser.ToyParser;
import me.adswt518.dfa.toyparser.Variable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        StringBuilder input = new StringBuilder();
        try {
            BufferedReader inputFile = new BufferedReader(new FileReader(args[0]));
            String line;
            while ((line = inputFile.readLine()) != null) {
                input.append(line).append("\n");
            }
            inputFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ToyParser toyParser = new ToyParser(input.toString());
        Function function = toyParser.parse();
        System.out.println(function.toString());
        List<Variable> variableList = toyParser.getVariableList();

        ToyAnalyzer toyAnalyzer = new ToyAnalyzer(variableList, function, true);
        toyAnalyzer.analyze();
        toyAnalyzer.printLVAResult(function.getBlocks());

        ToyAnalyzer undefVariableAnalyzer = new ToyAnalyzer(variableList, function, false);
        undefVariableAnalyzer.UndefVariableCheck();

    }
}
