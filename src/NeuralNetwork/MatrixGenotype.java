package NeuralNetwork;

import HelperClasses.Pair;
import org.ejml.simple.SimpleMatrix;

// Genotype for when no Genotype-Representation is needed
public class MatrixGenotype extends Genotype{
    private SimpleMatrix[] weights;
    private SimpleMatrix[] biases;
    public MatrixGenotype(SimpleMatrix[] weights, SimpleMatrix[] biases){
        this.weights = weights;
        this.biases = biases;
    }
    public void setWeights(SimpleMatrix[] weights){
        this.weights = weights;
    }
    public void setBiases(SimpleMatrix[] biases){
        this.biases = biases;
    }
    public Pair<SimpleMatrix[],SimpleMatrix[]> toPhenotype(){
        return new Pair<SimpleMatrix[],SimpleMatrix[]>(weights, biases);
    }
}
