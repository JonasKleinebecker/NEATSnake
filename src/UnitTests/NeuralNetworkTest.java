package UnitTests;

import ActivationFunction.*;
import HelperClasses.MatrixOperator;
import org.ejml.data.DenseD2Matrix64F;
import org.ejml.data.Matrix;
import org.ejml.simple.SimpleMatrix;
import NeuralNetwork.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NeuralNetworkTest {
    private NeuralNetwork neuralNetwork;
    private MatrixOperator matrixConverter;

    @BeforeEach
    public void setUpNoGenotypeNoActivationFunctionNeuralNetwork() {
        // Set up a test genotype with 2 input nodes, 3 hidden nodes, and 1 output node
        SimpleMatrix[] weights = new SimpleMatrix[]{
                new SimpleMatrix(new double[][]{{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}}),
                new SimpleMatrix(new double[][]{{7.0, 8.0, 9.0}})
        };
        SimpleMatrix[] biases = new SimpleMatrix[]{
                new SimpleMatrix(new double[][]{{0.1}, {0.2}, {0.3}}),
                new SimpleMatrix(new double[][]{{0.4}})
        };
        Genotype genotype = new MatrixGenotype(weights, biases);

        // Set up a test neural network with identity activation functions
        ActivationFunction[] activationFunctions = new ActivationFunction[]{
                new IdentityFunction(),
                new IdentityFunction()
        };
        neuralNetwork = new NeuralNetwork(genotype, activationFunctions);

        //needed to convert SimpleMatrix to double[][]
        matrixConverter = new MatrixOperator();

    }

    @Test
    public void testCalculate() {
        // Test input
        SimpleMatrix input = new SimpleMatrix(new double[][]{{0.5}, {0.6}});

        // Expected output based on test weights and biases
        double[][] expectedOutput = new double[][]{{103.4}};

        // Calculate actual output using the neural network
        double[][] actualOutput = matrixConverter.matrix2Array(neuralNetwork.calculate(input));

        // Check that the actual output is equal to the expected output
        for (int i = 0; i < actualOutput.length; i++){
            assertArrayEquals(actualOutput[i], expectedOutput[i], 1e-6);
        }
    }
}
