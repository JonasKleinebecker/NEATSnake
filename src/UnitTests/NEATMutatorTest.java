package UnitTests;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import GeneticAlgorithms.NEATMutator;
import Genotypes.NEATGenotype;
import HelperClasses.NodeType;
import HelperClasses.Pair;
import NEATGenes.ConnectionGene;
import NEATGenes.NodeGene;

public class NEATMutatorTest {
    
    @Test
    public void testSetProbabilities() {
        NEATMutator mutator = new NEATMutator(0.5, 0.5, 0.1,0.15,0.2,0.25,0.3, new Random(20));
        
        assertEquals(mutator.getMutationRate(), 0.5);
        assertEquals(mutator.getWeightMutationPower(), 0.5);
        assertEquals(mutator.getMutationDistribution().get("weight"), 0.1);
        assertEquals(mutator.getMutationDistribution().get("split"), 0.15);
        assertEquals(mutator.getMutationDistribution().get("add"), 0.2);
        assertEquals(mutator.getMutationDistribution().get("disable"), 0.25);
        assertEquals(mutator.getMutationDistribution().get("enable"), 0.3);
    }

    @Test
    public void testSetProbabilitiesIllegalArguments() {
        assertThrows(IllegalArgumentException.class, () -> {
            new NEATMutator(0.5, 0.5, 0.15,0.15,0.2,0.25,0.3, new Random(20));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new NEATMutator(0.5, 0.5, 0.1,-0.15,0.35,0.25,0.3, new Random(20));
        });
    }

    @Test
    public void testMutate() {
        NodeGene[] inputNodes;
        NodeGene[] hiddenNodes;
        NodeGene[] outputNodes;
    
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
        connectionGenes.add(new ConnectionGene( inputNodes[1],  hiddenNodes[1],0.8, true, 4));
        connectionGenes.add(new ConnectionGene( inputNodes[0],  hiddenNodes[0],0.5, true, 1));
        connectionGenes.add(new ConnectionGene( hiddenNodes[0], outputNodes[0],0.9, false, 5));
        connectionGenes.add(new ConnectionGene( inputNodes[1],  outputNodes[0],1.1, false, 7));
        connectionGenes.add(new ConnectionGene( hiddenNodes[1], hiddenNodes[2],3.0, true, 8));
        connectionGenes.add(new ConnectionGene( hiddenNodes[0], hiddenNodes[3],2.0, true, 10));
        connectionGenes.add(new ConnectionGene( hiddenNodes[3], outputNodes[0],1.5, true, 12));
        connectionGenes.add(new ConnectionGene( hiddenNodes[2], outputNodes[0],4.0, true, 9));
        connectionGenes.add(new ConnectionGene( inputNodes[0],  hiddenNodes[1],0.7, false, 3));
        connectionGenes.add(new ConnectionGene( inputNodes[0],  hiddenNodes[3],1.7, true, 6));
        connectionGenes.add(new ConnectionGene( hiddenNodes[1], outputNodes[1],0.2, true, 11));
        connectionGenes.add(new ConnectionGene( inputNodes[1],  hiddenNodes[0],0.6, false, 2));

        NEATGenotype genome = new NEATGenotype(nodeGenes, connectionGenes, new Random(10));

        NEATMutator mutator = new NEATMutator(0.8, 0.5, 0.2,0.2,0.2,0.2,0.2, new Random(20));

        Pair<NEATGenotype, Integer> individual = new Pair<>(genome, 0);

        //mutate split
        mutator.mutate(individual); 
        assertEquals(14, genome.getNumberOfConnectionGenes());
        assertEquals(9, genome.getNumberOfNodeGenes());
        assertTrue(genome.connectionExists(genome.getNodeGenes().get(4), genome.getNodeGenes().get(8)));

        //mutate weight
        mutator.mutate(individual);
        assertEquals(-0.145, genome.getConnectionGenes().get(11).getWeight(),0.01);

        //mutate add connection
        mutator.mutate(individual);
        assertTrue(genome.connectionExists(genome.getNodeGenes().get(0), genome.getNodeGenes().get(4)));

        //mutate enable
        mutator.mutate(individual);
        assertTrue(genome.getConnectionGenes().get(10).isEnabled());

        //mutate disable
        mutator.mutate(individual);
        mutator.mutate(individual);
        assertTrue(!genome.getConnectionGenes().get(6).isEnabled());
        }
    }
