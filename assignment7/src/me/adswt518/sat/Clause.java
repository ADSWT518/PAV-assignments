package me.adswt518.sat;

import java.util.Vector;

public class Clause {
    private final Vector<Variable> variables;

    public Clause() {
        variables = new Vector<>();
    }

    public Clause(Vector<Variable> vars) {
        variables = vars;
    }

    public Vector<Variable> getVariables() {
        return variables;
    }

    public void addVariable(Variable variable) {
        variables.add(variable);
    }

    public void removeVariable(Variable variable) { variables.remove(variable); }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Variable variable : variables) {
            sb.append(variable.toString());
            if (variables.indexOf(variable) != variables.size() - 1) {
                sb.append(" \\/ ");
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Clause clause) {
            return variables.equals(clause.getVariables());
        }
        return false;
    }
}
