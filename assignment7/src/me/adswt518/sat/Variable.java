package me.adswt518.sat;

public class Variable {
    private final int id;
    private boolean trueOrFalse;

    public Variable(int id, boolean trueOrFalse) {
        this.id = id;
        this.trueOrFalse = trueOrFalse;
    }

    public int getId() {
        return this.id;
    }

    public boolean getTrueOrFalse() {
        return this.trueOrFalse;
    }

    public void setTrueOrFalse(boolean trueOrFalse) {
        this.trueOrFalse = trueOrFalse;
    }

    public Variable getNegated() {
        return new Variable(this.id, !this.trueOrFalse);
    }

    public String toString() {
        return "(Variable " + this.id + ": " + this.trueOrFalse + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Variable other) {
            return this.id == other.id && this.trueOrFalse == other.trueOrFalse;
        }
        return false;
    }
}
