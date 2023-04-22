package UnitTests;

import ActivationFunction.*;
import HelperClasses.MatrixOperator;
import org.ejml.data.DenseD2Matrix64F;
import org.ejml.data.Matrix;
import org.ejml.simple.SimpleMatrix;
import NeuralNetwork.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class NeuralNetworkTest {
    private NeuralNetwork neuralNetwork;
    private MatrixOperator matrixConverter;

    @BeforeEach
    public void setUp() {
        // Initialize neural network and matrix converter
        neuralNetwork = null;
        matrixConverter = new MatrixOperator();
    }

    @ParameterizedTest
    @MethodSource("neuralNetworkProvider")
    public void testCalculate(NeuralNetwork neuralNetwork, double[][] input, double[][] expectedOutput) {
        // Set neural network for testing
        this.neuralNetwork = neuralNetwork;

        // Calculate actual output using the neural network
        double[][] actualOutput = matrixConverter.matrix2Array(neuralNetwork.calculate(new SimpleMatrix(input)));

        // Check that the actual output is equal to the expected output
        for (int i = 0; i < actualOutput.length; i++) {
            assertArrayEquals(actualOutput[i], expectedOutput[i], 1e-6);
        }
    }

    private static Stream<Arguments> neuralNetworkProvider() {
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
        NeuralNetwork neuralNetwork = new NeuralNetwork(genotype, activationFunctions);

        // Define input and expected output for testing
        double[][] input1 = new double[][]{{0.5}, {0.6}};
        double[][] expectedOutput1 = new double[][]{{103.4}};

        // Edge case: test input of all zeros
        double[][] input2 = new double[][]{{0}, {0}};
        double[][] expectedOutput2 = new double[][]{{5.4}};

        // Edge case: test input of negative numbers
        double[][] input3 = new double[][]{{-1}, {1}};
        double[][] expectedOutput3 = new double[][]{{29.4}};

        // Edge case: test input of large numbers
        double[][] input4 = new double[][]{{1000000}, {2000000}};
        double[][] expectedOutput4 = new double[][]{{276000005.4}};


        // Edge case: compute all in one
        double[][] input5 = new double[][]{{0.5, 0, -1, 1000000}, {0.6, 0, 1, 2000000}};
        double[][] expectedOutput5 = new double[][]{{103.4, 5.4, 29.4, 276000005.4}};

        return Stream.of(
                Arguments.of(neuralNetwork, input1, expectedOutput1),
                Arguments.of(neuralNetwork, input2, expectedOutput2),
                Arguments.of(neuralNetwork, input3, expectedOutput3),
                Arguments.of(neuralNetwork, input4, expectedOutput4),
                Arguments.of(neuralNetwork, input5, expectedOutput5)
        );
    }
}
