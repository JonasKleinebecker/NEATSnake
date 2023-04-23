package UnitTests;

import ActivationFunction.*;
import Genotypes.MatrixGenotype;
import HelperClasses.MatrixOperator;
import Genotypes.Genotype;
import org.ejml.data.DenseD2Matrix64F;
import org.ejml.simple.SimpleMatrix;
import NeuralNetwork.*;
import org.junit.jupiter.api.BeforeEach;
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
    @MethodSource({"neuralNetworkInputProvider", "neuralNetworkWeightProvider", "neuralNetworkTopologyProvider"})
    public void testCalculateForInputs(NeuralNetwork neuralNetwork, double[][] input, double[][] expectedOutput, boolean shouldFail) {
        // Set neural network for testing
        this.neuralNetwork = neuralNetwork;
        double[][] actualOutput;

        // Check that the actual output is equal to the expected output
        if(!shouldFail) {
            // Calculate actual output using the neural network
            actualOutput = matrixConverter.matrix2Array(neuralNetwork.calculate(new SimpleMatrix(input)));

            for (int i = 0; i < actualOutput.length; i++) {
                assertArrayEquals(actualOutput[i], expectedOutput[i], 1e-6);
            }
        } else {
            assertThrows(IllegalArgumentException.class, () -> {
                neuralNetwork.calculate(new SimpleMatrix(input));
            });
        }
    }

    private static Stream<Arguments> neuralNetworkTopologyProvider(){
        SimpleMatrix[] weights1 = new SimpleMatrix[]{
                new SimpleMatrix(new double[][]{{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}, {7.0, 8.0}, {9.0, 10.0}}),
                new SimpleMatrix(new double[][]{{7.0, 8.0, 9.0, 4.0, -2.0}, {1.0, 2.0, 3.0, 4.0, 5.0}})
        };
        SimpleMatrix[] biases1 = new SimpleMatrix[]{
                new SimpleMatrix(new double[][]{{0.1}, {0.2}, {0.3}, {0.4}, {0.5}}),
                new SimpleMatrix(new double[][]{{0.4}, {0.5}})
        };
        Genotype genotype1 = new MatrixGenotype(weights1, biases1);

        SimpleMatrix[] weights2 = new SimpleMatrix[]{
                new SimpleMatrix(new double[][]{{-1.0, 2.0, 3.0}}),
                new SimpleMatrix(new double[][]{{-7.0}, {8.0}}),
                new SimpleMatrix(new double[][]{{-1.0, 2.0}, {3.2, -2.0}})
        };
        SimpleMatrix[] biases2 = new SimpleMatrix[]{
                new SimpleMatrix(new double[][]{{0.1}}),
                new SimpleMatrix(new double[][]{{0.4}, {0.5}}),
                new SimpleMatrix(new double[][]{{-0.4}, {-0.5}})
        };
        Genotype genotype2 = new MatrixGenotype(weights2, biases2);

        SimpleMatrix[] weights3 = new SimpleMatrix[]{
                new SimpleMatrix(new double[][]{{1.0, 2.0}, {3.0, 4.0}}),
                new SimpleMatrix(new double[][]{{7.0, 8.0, 9.0}})
        };
        SimpleMatrix[] biases3 = new SimpleMatrix[]{
                new SimpleMatrix(new double[][]{{0.1}, {0.2}}),
                new SimpleMatrix(new double[][]{{0.4}})
        };
        Genotype genotype3 = new MatrixGenotype(weights3, biases3);

        SimpleMatrix[] weights4 = new SimpleMatrix[]{
                new SimpleMatrix(new double[][]{{1.0, 2.0}, {3.0, 4.0}}),
                new SimpleMatrix(new double[][]{{7.0, 8.0, 9.0}})
        };
        SimpleMatrix[] biases4 = new SimpleMatrix[]{
                new SimpleMatrix(new double[][]{{0.1}, {0.2}, {0.3}}),
                new SimpleMatrix(new double[][]{{0.4, 0.5}})
        };
        Genotype genotype4 = new MatrixGenotype(weights4, biases4);

        // Set up a test neural network with identity activation functions
        ActivationFunction[] activationFunctions = new ActivationFunction[]{
                new IdentityFunction(),
                new IdentityFunction()
        };
        ActivationFunction[] activationFunctions2 = new ActivationFunction[]{
                new IdentityFunction(),
                new IdentityFunction(),
                new IdentityFunction()
        };

        double[][] input = new double[][]{{5}, {-2}};
        double[][] input2 = new double[][]{{-1}, {2}, {3}};

        // Define input and expected output for testing
        //Edge case: test more neurons per layer
        NeuralNetwork neuralNetwork1 = new NeuralNetwork(genotype1, activationFunctions);
        double[][] expectedOutput1 = new double[][]{{212}, {261}};

        //Edge case: test more layers
        NeuralNetwork neuralNetwork2 = new NeuralNetwork(genotype2, activationFunctions2);
        double[][] expectedOutput2 = new double[][]{{324.5}, {-541.66}};

        // Edge case: test invalid number of weights
        NeuralNetwork neuralNetwork3 = new NeuralNetwork(genotype3, activationFunctions);
        double[][] expectedOutput3 = null;

        // Edge case: test invalid number of bias layers.
        NeuralNetwork neuralNetwork4 = new NeuralNetwork(genotype4, activationFunctions);
        double[][] expectedOutput4 = null;

        //Edge case: test invalid number of Activation Functions
        NeuralNetwork neuralNetwork5 = new NeuralNetwork(genotype1, activationFunctions2);

        return Stream.of(
                Arguments.of(neuralNetwork1, input, expectedOutput1, false),
                Arguments.of(neuralNetwork2, input2, expectedOutput2, false),
                Arguments.of(neuralNetwork3, input, expectedOutput3, true),
                Arguments.of(neuralNetwork2, input, expectedOutput4, true),
                Arguments.of(neuralNetwork4, input, expectedOutput4, true),
                Arguments.of(neuralNetwork5, input, expectedOutput1, true),
                // Edge case: test invalid number of inputs
                Arguments.of(neuralNetwork2, input, expectedOutput4, true)

        );
    }

    private static Stream<Arguments> neuralNetworkWeightProvider(){
        // Set up a test genotype with 2 input nodes, 3 hidden nodes, and 1 output node
        SimpleMatrix[] weights1 = new SimpleMatrix[]{
                new SimpleMatrix(new double[][]{{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}}),
                new SimpleMatrix(new double[][]{{7.0, 8.0, 9.0}})
        };
        SimpleMatrix[] biases1 = new SimpleMatrix[]{
                new SimpleMatrix(new double[][]{{0.1}, {0.2}, {0.3}}),
                new SimpleMatrix(new double[][]{{0.4}})
        };
        Genotype genotype1 = new MatrixGenotype(weights1, biases1);

        SimpleMatrix[] weights2 = new SimpleMatrix[]{
                new SimpleMatrix(new double[][]{{-1.0, 2.0}, {-3.0, 4.3}, {1.0, -6.0}}),
                new SimpleMatrix(new double[][]{{7.0, -8.0, 9.2}})
        };
        SimpleMatrix[] biases2 = new SimpleMatrix[]{
                new SimpleMatrix(new double[][]{{-2.5}, {-0.2}, {0.3}}),
                new SimpleMatrix(new double[][]{{-0.4}})
        };
        Genotype genotype2 = new MatrixGenotype(weights2, biases2);

        SimpleMatrix[] weights3 = new SimpleMatrix[]{
                new SimpleMatrix(new double[][]{{-1.0, 2.0}, {0, 4.3}, {1.0, 0}}),
                new SimpleMatrix(new double[][]{{7.0, 0, 9.2}})
        };
        SimpleMatrix[] biases3 = new SimpleMatrix[]{
                new SimpleMatrix(new double[][]{{0}, {-0.2}, {0}}),
                new SimpleMatrix(new double[][]{{0}})
        };
        Genotype genotype3 = new MatrixGenotype(weights3, biases3);

        // Set up a test neural network with identity activation functions
        ActivationFunction[] activationFunctions = new ActivationFunction[]{
                new IdentityFunction(),
                new IdentityFunction()
        };
        double[][] input = new double[][]{{5}, {-2}};

        // Define input and expected output for testing
        NeuralNetwork neuralNetwork1 = new NeuralNetwork(genotype1, activationFunctions);
        double[][] expectedOutput1 = new double[][]{{185.4}};

        //Edge case: test negative weights
        NeuralNetwork neuralNetwork2 = new NeuralNetwork(genotype2, activationFunctions);
        double[][] expectedOutput2 = new double[][]{{268.66}};

        // Edge case: test 0 values
        NeuralNetwork neuralNetwork3 = new NeuralNetwork(genotype3, activationFunctions);
        double[][] expectedOutput3 = new double[][]{{-17}};

        return Stream.of(
                Arguments.of(neuralNetwork1, input, expectedOutput1, false),
                Arguments.of(neuralNetwork2, input, expectedOutput2, false),
                Arguments.of(neuralNetwork3, input, expectedOutput3, false)
        );
    }

    private static Stream<Arguments> neuralNetworkInputProvider() {
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
                Arguments.of(neuralNetwork, input1, expectedOutput1, false),
                Arguments.of(neuralNetwork, input2, expectedOutput2, false),
                Arguments.of(neuralNetwork, input3, expectedOutput3, false),
                Arguments.of(neuralNetwork, input4, expectedOutput4, false),
                Arguments.of(neuralNetwork, input5, expectedOutput5, false)
        );
    }
}
