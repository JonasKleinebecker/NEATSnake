package UnitTests;

import Genotypes.NEATGenotype;
import HelperClasses.NodeType;
import HelperClasses.Pair;
import NEATGenes.ConnectionGene;
import NEATGenes.NodeGene;
import org.ejml.simple.SimpleMatrix;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NeatGenotypeToPhenotypeTest {

    NodeGene[] inputNodes;
    NodeGene[] hiddenNodes;
    NodeGene[] outputNodes;

    @BeforeEach
    public void setUp(){
        // create 3 input nodes
        inputNodes = new NodeGene[3];
        inputNodes[0] = new NodeGene(1, NodeType.INPUT);
        inputNodes[1] = new NodeGene(2, NodeType.INPUT);
        inputNodes[2] = new NodeGene(3, NodeType.INPUT);

        //create 6 hidden nodes
        hiddenNodes = new NodeGene[6];
        hiddenNodes[0] = new NodeGene(4, NodeType.HIDDEN);
        hiddenNodes[1] = new NodeGene(5, NodeType.HIDDEN);
        hiddenNodes[2] = new NodeGene(6, NodeType.HIDDEN);
        hiddenNodes[3] = new NodeGene(7, NodeType.HIDDEN);
        hiddenNodes[4] = new NodeGene(8, NodeType.HIDDEN);
        hiddenNodes[5] = new NodeGene(9, NodeType.HIDDEN);

        //create 3 output node
        outputNodes = new NodeGene[3];
        outputNodes[0] = new NodeGene(10, NodeType.OUTPUT);
        outputNodes[1] = new NodeGene(11, NodeType.OUTPUT);
        outputNodes[2] = new NodeGene(12, NodeType.OUTPUT);
    }

    @Test
    public void noBiasNoHiddenNoRecurrentNoLongRangeSortedGenes(){
        // create a mock genome
        List<NodeGene> nodeGenes = new ArrayList<>();
        nodeGenes.add(inputNodes[0]);
        nodeGenes.add(inputNodes[1]);
        nodeGenes.add(outputNodes[0]);

        List<ConnectionGene> connectionGenes = new ArrayList<>();
        connectionGenes.add(new ConnectionGene(0.5, inputNodes[0], outputNodes[0], true, 1));
        connectionGenes.add(new ConnectionGene(0.6, inputNodes[1], outputNodes[0], true, 2));

        NEATGenotype genome = new NEATGenotype(nodeGenes, connectionGenes);

        // call the toPhenotype function
        genome.updateNodeLocations();
        Pair<SimpleMatrix[], SimpleMatrix[]> result = genome.toPhenotype();

        // check the results
        assertNotNull(result);

        SimpleMatrix[] weights = result.getFirst();
        SimpleMatrix[] biases = result.getSecond();

        assertEquals(1, weights.length);
        assertEquals(1, weights[0].numRows());
        assertEquals(2, weights[0].numCols());

        assertEquals(1, biases.length);
        assertEquals(1, biases[0].numRows());
        assertEquals(1, biases[0].numCols());

        assertEquals(0.5, weights[0].get(0, 0), 0.001);
        assertEquals(0.6, weights[0].get(0, 1), 0.001);
    }

    @Test
    public void noBiasHiddenNoRecurrentLongRangeUnsortedGenes(){
        // create a mock genome
        List<NodeGene> nodeGenes = new ArrayList<>();
        // add the nodes in a random order
        nodeGenes.add(inputNodes[1]);
        nodeGenes.add(hiddenNodes[2]);
        nodeGenes.add(outputNodes[0]);
        nodeGenes.add(hiddenNodes[1]);
        nodeGenes.add(hiddenNodes[0]);
        nodeGenes.add(outputNodes[1]);
        nodeGenes.add(inputNodes[0]);
        nodeGenes.add(hiddenNodes[3]);

        List<ConnectionGene> connectionGenes = new ArrayList<>();
        //add the connections in a random order
        connectionGenes.add(new ConnectionGene(0.8, inputNodes[1], hiddenNodes[1], true, 4));
        connectionGenes.add(new ConnectionGene(0.5, inputNodes[0], hiddenNodes[0], true, 1));
        connectionGenes.add(new ConnectionGene(0.9, hiddenNodes[0], outputNodes[0], true, 5));
        connectionGenes.add(new ConnectionGene(1.1, inputNodes[1], outputNodes[0], true, 7));
        connectionGenes.add(new ConnectionGene(3.0, hiddenNodes[1], hiddenNodes[2], true, 8));
        connectionGenes.add(new ConnectionGene(2.0, hiddenNodes[0], hiddenNodes[3], true, 10));
        connectionGenes.add(new ConnectionGene(1.5, hiddenNodes[3], outputNodes[0], true, 12));
        connectionGenes.add(new ConnectionGene(4.0, hiddenNodes[2], outputNodes[0], true, 9));
        connectionGenes.add(new ConnectionGene(0.7, inputNodes[0], hiddenNodes[1], true, 3));
        connectionGenes.add(new ConnectionGene(1.7, inputNodes[0], hiddenNodes[3], true, 6));
        connectionGenes.add(new ConnectionGene(0.2, hiddenNodes[1], outputNodes[1], true, 11));
        connectionGenes.add(new ConnectionGene(0.6, inputNodes[1], hiddenNodes[0], true, 2));

        NEATGenotype genome = new NEATGenotype(nodeGenes, connectionGenes);

        // call the toPhenotype function
        genome.updateNodeLocations();
        Pair<SimpleMatrix[], SimpleMatrix[]> result = genome.toPhenotype();

        // check the results
        assertNotNull(result);

        SimpleMatrix[] weights = result.getFirst();
        SimpleMatrix[] biases = result.getSecond();

        assertEquals(3, weights.length);
        assertEquals(4, weights[0].numRows());
        assertEquals(2, weights[0].numCols());
        assertEquals(5, weights[1].numRows());
        assertEquals(4, weights[1].numCols());
        assertEquals(2, weights[2].numRows());
        assertEquals(5, weights[2].numCols());

        assertEquals(3, biases.length);
        assertEquals(4, biases[0].numRows());
        assertEquals(1, biases[0].numCols());
        assertEquals(5, biases[1].numRows());
        assertEquals(1, biases[1].numCols());
        assertEquals(2, biases[2].numRows());
        assertEquals(1, biases[2].numCols());

        assertEquals(0.5, weights[0].get(0, 0), 0.001);
        assertEquals(0.6, weights[0].get(0, 1), 0.001);
        assertEquals(0.7, weights[0].get(1, 0), 0.001);
        assertEquals(0.8, weights[0].get(1, 1), 0.001);
        assertEquals(0.0, weights[1].get(0, 0), 0.001);
        assertEquals(1.0, weights[1].get(2, 2), 0.001);
        assertEquals(0.9, weights[1].get(3, 0), 0.001);
        assertEquals(1.5, weights[2].get(0, 1), 0.001);
        assertEquals(1, weights[2].get(1, 4), 0.001);
    }
}
