package NeuralNetwork;

import HelperClasses.Pair;
import org.ejml.simple.SimpleMatrix;

public abstract class Genotype {
    public abstract Pair<SimpleMatrix[], SimpleMatrix[]> toPhenotype();
}
