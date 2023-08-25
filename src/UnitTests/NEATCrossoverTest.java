package UnitTests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import GeneticAlgorithms.NEATCrossover;
import Genotypes.NEATGenotype;
import HelperClasses.NodeType;
import HelperClasses.Pair;
import NEATGenes.ConnectionGene;
import NEATGenes.NodeGene;

public class NEATCrossoverTest {
    @Test
    public void testCrossover(){
        NodeGene[] inputNodes;
        NodeGene[] hiddenNodes;
        NodeGene[] outputNodes;
    
        // create 3 input nodes
        inputNodes = new NodeGene[2];
        inputNodes[0] = new NodeGene(1, NodeType.INPUT);
        inputNodes[1] = new NodeGene(2, NodeType.INPUT);

        //create 6 hidden nodes
        hiddenNodes = new NodeGene[3];
        hiddenNodes[0] = new NodeGene(3, NodeType.HIDDEN);
        hiddenNodes[1] = new NodeGene(4, NodeType.HIDDEN);
        hiddenNodes[2] = new NodeGene(5, NodeType.HIDDEN);

        //create 3 output node
        outputNodes = new NodeGene[2];
        outputNodes[0] = new NodeGene(6, NodeType.OUTPUT);
        outputNodes[1] = new NodeGene(7, NodeType.OUTPUT);

        List<NodeGene> nodeGenes1 = new ArrayList<>();
        // add the nodes in a random order
        nodeGenes1.add(inputNodes[1]);
        nodeGenes1.add(outputNodes[0]);
        nodeGenes1.add(hiddenNodes[1]);
        nodeGenes1.add(hiddenNodes[0]);
        nodeGenes1.add(inputNodes[0]);

        List<NodeGene> nodeGenes2 = new ArrayList<>();
        // add the nodes in a random order
        nodeGenes2.add(inputNodes[1]);
        nodeGenes2.add(outputNodes[0]);
        nodeGenes2.add(hiddenNodes[1]);
        nodeGenes2.add(hiddenNodes[0]);
        nodeGenes2.add(inputNodes[0]);
        nodeGenes2.add(hiddenNodes[2]);
        nodeGenes2.add(outputNodes[1]);

        List<ConnectionGene> connectionGenes1 = new ArrayList<>();
        //add the connections in a random order
        connectionGenes1.add(new ConnectionGene( inputNodes[1],  hiddenNodes[1],0.8, true, 5));
        connectionGenes1.add(new ConnectionGene( inputNodes[0],  hiddenNodes[0],0.5, true, 4));
        connectionGenes1.add(new ConnectionGene( hiddenNodes[0], outputNodes[0],0.9, false, 3));
        connectionGenes1.add(new ConnectionGene( inputNodes[1],  outputNodes[0],1.1, false, 2));
        connectionGenes1.add(new ConnectionGene( hiddenNodes[1], outputNodes[0],3.0, true, 1));

        
        List<ConnectionGene> connectionGenes2 = new ArrayList<>();
        //add the connections in a random order
        connectionGenes2.add(new ConnectionGene( inputNodes[1],  hiddenNodes[1],1.2, true, 5));
        connectionGenes2.add(new ConnectionGene( inputNodes[0],  hiddenNodes[0],0.5, true, 7));
        connectionGenes2.add(new ConnectionGene( hiddenNodes[2], outputNodes[1],0.9, false, 6));
        connectionGenes2.add(new ConnectionGene( inputNodes[1],  outputNodes[0],1.1, true, 2));
        connectionGenes2.add(new ConnectionGene( hiddenNodes[1], outputNodes[0],3.0, true, 1));
        connectionGenes2.add(new ConnectionGene( inputNodes[0], hiddenNodes[2],2.0, true, 8));
        connectionGenes2.add(new ConnectionGene( hiddenNodes[1], outputNodes[0], -3.0, true, 9));

        NEATGenotype genome1 = new NEATGenotype(nodeGenes1, connectionGenes1, new Random(10));
        NEATGenotype genome2 = new NEATGenotype(nodeGenes2, connectionGenes2, new Random(10));

        NEATCrossover crossover = new NEATCrossover(new Random(15));

        Pair<NEATGenotype, Integer> child = crossover.crossover(new Pair<Pair<NEATGenotype,Integer>,Pair<NEATGenotype,Integer>>(new Pair<NEATGenotype, Integer>(genome1, 1), new Pair<NEATGenotype, Integer>(genome2, 2)));

        NEATGenotype childGenome = child.getFirst();
        assertTrue(childGenome.getConnectionGenes().size() == 7);
        assertTrue(childGenome.getNodeGenes().size() == 7);
        assertTrue(childGenome.connectionExists(hiddenNodes[2], outputNodes[1]));
        assertTrue(childGenome.connectionExists(inputNodes[1], outputNodes[0]));
        assertTrue(childGenome.connectionExists(inputNodes[0], hiddenNodes[0]));
    }
}
