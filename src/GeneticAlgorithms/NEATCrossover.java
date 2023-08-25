package GeneticAlgorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Genotypes.NEATGenotype;
import HelperClasses.Pair;
import NEATGenes.ConnectionGene;
import NEATGenes.NodeGene;

public class NEATCrossover implements CrossoverHandler<NEATGenotype>{

    Random randomGenerator;

    public NEATCrossover(Random randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    @Override
    public Pair<NEATGenotype, Integer> crossover(Pair<Pair<NEATGenotype, Integer>, Pair<NEATGenotype, Integer>> parents) {
        Pair<NEATGenotype, Integer> parent1 = parents.getFirst();
        Pair<NEATGenotype, Integer> parent2 = parents.getSecond();

        Pair<NEATGenotype, Integer> fitterParent = parent1.getSecond() > parent2.getSecond() ? parent1 : parent2;
        Pair<NEATGenotype, Integer> lessFitParent = parent1.getSecond() > parent2.getSecond() ? parent2 : parent1;

        List<NodeGene> childNodeGenes = new ArrayList<>();
        List<ConnectionGene> childConnectionGenes = new ArrayList<>();

        fitterParent.getFirst().sortConnectionGenesByInnovationNumber();
        lessFitParent.getFirst().sortConnectionGenesByInnovationNumber();

        int iterator = 0;
        List<ConnectionGene> lessFitParentConnectionGenes = lessFitParent.getFirst().getConnectionGenes();
        for(ConnectionGene connectionGene : fitterParent.getFirst().getConnectionGenes()){
            ConnectionGene connectionGeneToAdd = connectionGene;
            int currentInnovationNumber = connectionGene.getInnovationNumber();
            while(lessFitParentConnectionGenes.get(iterator).getInnovationNumber() < currentInnovationNumber){
                if(iterator >= lessFitParentConnectionGenes.size() - 1){
                    break;
                }
                iterator++;
            }
            if(lessFitParentConnectionGenes.get(iterator).getInnovationNumber() == currentInnovationNumber){
                if(randomGenerator.nextBoolean()){
                    connectionGeneToAdd = lessFitParentConnectionGenes.get(iterator);
                }
            }
            childConnectionGenes.add(connectionGeneToAdd);
        }

        for(NodeGene nodeGene : fitterParent.getFirst().getNodeGenes()){
            childNodeGenes.add(nodeGene);
        };

        NEATGenotype child = new NEATGenotype(childNodeGenes, childConnectionGenes,fitterParent.getFirst().getRandomGenerator());

        return new Pair<NEATGenotype,Integer>(child, 0);
    }
}