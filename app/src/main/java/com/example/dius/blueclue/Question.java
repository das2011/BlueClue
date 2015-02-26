package com.example.dius.blueclue;

/**
 * Created by elgaby on 26/02/15.
 */
public class Question {
    public enum Operator { PLUS, TIMES }

    private int operand1;
    private int operand2;
    private Operator operator;

    public int getOperand1() {
        return operand1;
    }

    public void setOperand1(int operand1) {
        this.operand1 = operand1;
    }

    public int getOperand2() {
        return operand2;
    }

    public void setOperand2(int operand2) {
        this.operand2 = operand2;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }
}
