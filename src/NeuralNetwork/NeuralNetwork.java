package NeuralNetwork;

import NeuralNetwork.ActivationFunction.ActivationFunction;
import org.ejml.simple.SimpleMatrix;

public class NeuralNetwork {
    private Genotype genotype;
    private SimpleMatrix[] weights;
    private SimpleMatrix[] biases;
    private ActivationFunction[] activationFunctions;

    public NeuralNetwork(Genotype genotype){
        this.genotype = genotype;
    }

    public SimpleMatrix[] getWeights(){
        return weights;
    }

    public SimpleMatrix calculate(SimpleMatrix input){
        weights = genotype.toPhenotype();
        SimpleMatrix output = input;
        for(int i = 0; i < weights.length; i++){
            output = weights[i].mult(output).plus(biases[i]);
            output = activationFunctions[i].apply(output);
        }
        return output;
    }
}
