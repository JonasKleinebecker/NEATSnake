package ActivationFunction;

import org.ejml.simple.SimpleMatrix;

public class IdentityFunction implements ActivationFunction{
    public double apply(double input){
        return input;
    }
    public SimpleMatrix apply(SimpleMatrix input){
        return input;
    }
}
