package ActivationFunction;

import org.ejml.simple.SimpleMatrix;

public class ReluFunction implements ActivationFunction{
    public double apply(double input){
        return Math.max(0, input);
    }
    public SimpleMatrix apply(SimpleMatrix input){
        int numRows = input.numRows();
        int numCols = input.numCols();
        SimpleMatrix B = new SimpleMatrix(numRows, numCols);
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                double val = input.get(i, j);
                B.set(i, j, Math.max(0,val));
            }
        }
        return B;
    }
}
