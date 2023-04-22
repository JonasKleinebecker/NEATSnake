package ActivationFunction;

import org.ejml.simple.SimpleMatrix;

public interface ActivationFunction {
    public double apply(double input);
    public SimpleMatrix apply(SimpleMatrix input);
}
