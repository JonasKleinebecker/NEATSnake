package NeuralNetwork;

import ActivationFunction.ActivationFunction;
import HelperClasses.Pair;
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

        SimpleMatrix output = input;
        for(int i = 0; i < weights.length; i++){
            output = weights[i].mult(output);
            for(int j = 0; j < output.getNumCols(); j++) {
                output.setColumn(j, output.getColumn(j).plus(biases[i]));
            }
            output = activationFunctions[i].apply(output);
        }
        return output;
    }
}
