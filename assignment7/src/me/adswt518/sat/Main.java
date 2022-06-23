package me.adswt518.sat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

enum algorithm {Naive, DPLL};

public class Main {

    public static void main(String[] args) {
        if (!(args.length == 2 || args.length == 3) || !(args[0].equals("naive") || args[0].equals("dpll"))) {
            System.out.println("Usage: java me.adswt518.sat.Main <naive|dpll> <input_file> [-V]");
            System.out.println("\t-V: show the SAT problem information");
            System.exit(1);
        }
        int varCount = 0;
        int clauseCount = 0;
        SAT sat = new SAT();
        try {
            BufferedReader inputFile = new BufferedReader(new FileReader(args[1]));
            String line;
            line = inputFile.readLine();
            varCount = Integer.parseInt(line.split(" ")[2]);
            clauseCount = Integer.parseInt(line.split(" ")[3]);
            sat.setVarCount(varCount);
            sat.setClauseCount(clauseCount);
            while ((line = inputFile.readLine()) != null) {
                Clause clause = new Clause();
                for (String s : line.split(" ")) {
                    int var = Integer.parseInt(s);
                    if (var == 0) {
                        break;
                    }
                    Variable v = new Variable(Math.abs(var), var > 0);
                    clause.addVariable(v);
                }
                sat.addClause(clause);
            }
            inputFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String s : args) {
            if (s.equals("-V")) {
                System.out.println(sat);
            }
        }
        switch (args[0]) {
            case "naive" -> System.out.println("Solvable: " + sat.solve(algorithm.Naive));
            case "dpll" -> System.out.println("Solvable: " + sat.solve(algorithm.DPLL));
        }
        System.out.println("One of solutions: " + sat.getSolution());
    }
}
