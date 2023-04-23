package NeuralNetwork;

import ActivationFunction.ActivationFunction;
import HelperClasses.Pair;
import Genotypes.Genotype;
import org.ejml.simple.SimpleMatrix;

public class NeuralNetwork {
    private Genotype genotype;
    private SimpleMatrix[] weights;
    private SimpleMatrix[] biases;
    private ActivationFunction[] activationFunctions;

    public NeuralNetwork(Genotype genotype, ActivationFunction[] activationFunctions){
        this.genotype = genotype;
        this.activationFunctions = activationFunctions;
    }

    public SimpleMatrix[] getWeights(){
        return weights;
    }

    public SimpleMatrix calculate(SimpleMatrix input){
        //update weights and biases
        Pair<SimpleMatrix[], SimpleMatrix[]> phenotype = genotype.toPhenotype();
        weights = phenotype.getFirst();
        biases = phenotype.getSecond();

        if(weights.length != biases.length || weights.length != activationFunctions.length){
            throw new IllegalArgumentException("The number of weight layers, bias layers and activation functions must be equal");
        }

        SimpleMatrix output = input;

        for(int i = 0; i < weights.length; i++){
            if(i == 0){
                if(weights[i].getNumCols() != output.getNumRows()){
                    throw new IllegalArgumentException("The number of inputs must be equal to the number of neurons in the first layer");
                }
            } else {
                if(weights[i].getNumCols() != output.getNumRows()){
                    throw new IllegalArgumentException("The number of neurons layer " + i + " must be equal to the number of rows in the weight layer " + (i-1));
                }
            }

            output = weights[i].mult(output);

            if(output.getNumRows() != biases[i].getNumRows()){
                throw new IllegalArgumentException("The number of neurons in layer " + i + " must be equal to the number of rows in the bias layer " + i);
            }

            for(int j = 0; j < output.getNumCols(); j++) {
                output.setColumn(j, output.getColumn(j).plus(biases[i]));
            }
            output = activationFunctions[i].apply(output);
        }
        return output;
    }
}
