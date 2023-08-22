package Genotypes;

import NEATGenes.NodeGene;

public class InnovationCounter {
    private static int innovationNumber = 0;

    public static int getNextInnovationNumber(NodeGene inNode, NodeGene outNode){
        return innovationNumber++;
    }
}
