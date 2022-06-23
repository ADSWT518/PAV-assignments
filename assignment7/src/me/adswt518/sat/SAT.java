package me.adswt518.sat;

import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

public class SAT {
    private int varCount;
    private int clauseCount;

    private Vector<Clause> clauses;
    private Vector<Variable> solution;
    private final Vector<Integer> varIds;

    public SAT( ) {
        varCount = 0;
        clauseCount = 0;
        clauses = new Vector<>();
        solution = new Vector<>();
        varIds = new Vector<>();
    }

    public void addClause(Clause clause) {
        clauses.add(clause);
        for (Variable var : clause.getVariables()) {
            if (varIds.contains(var.getId())) {
                continue;
            }
            varIds.add(var.getId());
        }
    }

    public int getVarCount() {
        return varCount;
    }

    public int getClauseCount() {
        return clauseCount;
    }

    public Vector<Clause> getClauses() {
        return clauses;
    }

    public Vector<Variable> getSolution() {
        return solution;
    }

    public void setVarCount(int varCount) {
        this.varCount = varCount;
    }

    public void setClauseCount(int clauseCount) {
        this.clauseCount = clauseCount;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SAT: \n");
        sb.append("varCount: ").append(varCount).append("\n");
        sb.append("clauseCount: ").append(clauseCount).append("\n");
        sb.append("clauses: \n");
        for (Clause clause : clauses) {
            sb.append("\t").append(clause.toString()).append("\n");
        }
        return sb.toString();
    }

    public boolean solve(algorithm alg) {
        return switch (alg) {
            case Naive -> solveNaive(this.clauses, new Vector<>());
            case DPLL -> solveDPLL(this.clauses, new Vector<>());
        };
    }

    private boolean solveNaive(Vector<Clause> clauses, Vector<Variable> solution) {
        if (clauses.size() == 0) {
            this.solution = solution;
            return true;
        }
        for (Clause clause : clauses) {
            if (clause.getVariables().size() == 0) {
                return false;
            }
        }
        int firstVarId = clauses.get(0).getVariables().get(0).getId();
        Variable varT = new Variable(firstVarId, true);
        Variable varF = new Variable(firstVarId, false);
        Vector<Clause> clausesT = assignLit(clauses, varT);
        Vector<Clause> clausesF = assignLit(clauses, varF);
        Vector<Variable> solutionT = new Vector<>();
        Vector<Variable> solutionF = new Vector<>();
        for (Variable var : solution) {
            solutionT.add(new Variable(var.getId(), var.getTrueOrFalse()));
            solutionF.add(new Variable(var.getId(), var.getTrueOrFalse()));
        }
        solutionT.add(varT);
        solutionF.add(varF);
        return solveNaive(clausesT, solutionT) || solveNaive(clausesF, solutionF);
    }

    private Vector<Clause> assignLit(Vector<Clause> clauses, Variable var) {
        // deep copy
        Vector<Clause> newClauses = new Vector<>();
        for (Clause clause : clauses) {
            Vector<Variable> newVars = new Vector<>();
            for (Variable v : clause.getVariables()) {
                newVars.add(new Variable(v.getId(), v.getTrueOrFalse()));
            }
            newClauses.add(new Clause(newVars));
        }
        // assign
        for (Iterator<Clause> it = newClauses.iterator(); it.hasNext();) {
            Clause newClause = it.next();
            if (newClause.getVariables().contains(var)) {
                it.remove();
            }
            if (newClause.getVariables().contains(var.getNegated())) {
                newClause.removeVariable(var.getNegated());
            }
        }
        return newClauses;
    }

    private boolean solveDPLL(Vector<Clause> clauses, Vector<Variable> solution) {
        // unit propagation
        Vector<Clause> clausesAfterUniProp = unitPropagation(clauses, solution);
        while (true) {
            Vector<Clause> tmp = unitPropagation(clausesAfterUniProp, solution);
            if (clausesAfterUniProp == tmp) {
                break;
            }
            clausesAfterUniProp = tmp;
        }
        // System.out.println("clauses after unit propagation: " + clausesAfterUniProp);
        // System.out.println("solution after unit propagation: " + solution);
        // naive solver
        return solveNaive(clausesAfterUniProp, solution);
    }

    private Vector<Clause> unitPropagation(Vector<Clause> clauses, Vector<Variable> solution) {
        for (Clause clause : clauses) {
            if (clause.getVariables().size() == 1) {
                Variable forcedLit = clause.getVariables().get(0);
                solution.add(forcedLit);
                clauses = assignLit(clauses, forcedLit);
            }
        }
        return clauses;
    }
}
