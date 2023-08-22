package GeneticAlgorithms;

import HelperClasses.NodeType;
import HelperClasses.Pair;
import NEATGenes.ConnectionGene;
import Genotypes.NEATGenotype;
import NEATGenes.NodeGene;

import java.util.Map;
import java.util.Random;

public class NEATMutator implements MutationHandler<NEATGenotype>{
    double mutationRate;
    double weightMutationPower; //Weight is changed by -1 to 1 * weightMutationPower
    Map<String, Double> mutationDistribution;
    Random randomGenerator;

    public NEATMutator(double mutationRate, double weightMutationPower, double weightMutationDistribution, double splitMutationDistribution, double addMutationDistribution, double disableMutationDistribution, double enableMutationDistribution, Random randomGenerator) {
        this.mutationRate = mutationRate;
        this.weightMutationPower = weightMutationPower;
        this.randomGenerator = randomGenerator;
        setProbabilities(Map.of("weight", weightMutationDistribution, "split", splitMutationDistribution, "add", addMutationDistribution, "disable", disableMutationDistribution, "enable", enableMutationDistribution));
    }

    public void setProbabilities(Map<String, Double> mutationDistribution){
        this.mutationDistribution = mutationDistribution;
        double total = 0.0;
        for (double probability : mutationDistribution.values()) {
            if(probability < 0.0 || probability > 100.0){
                throw new IllegalArgumentException("Individual probabilities must be between 0% and 100%");
            }
            total += probability;
        }
        if (total != 100.0) {
            throw new IllegalArgumentException("Probabilities must add up to 100%");
        }
        this.mutationDistribution = mutationDistribution;
    }

    @Override
    public void mutate(Pair<NEATGenotype, Integer> individual){
        NEATGenotype genotype = individual.getFirst();
        if(randomGenerator.nextDouble(1) < mutationRate){
            double random = randomGenerator.nextDouble(1);
            if(random < mutationDistribution.get("weight")){
                mutateWeight(genotype);
            }
            else if(random < mutationDistribution.get("weight") + mutationDistribution.get("split")){
                mutateSplit(genotype);
            }
            else if(random < mutationDistribution.get("weight") + mutationDistribution.get("split") + mutationDistribution.get("add")){
                mutateAdd(genotype);
            }
            else if(random < mutationDistribution.get("weight") + mutationDistribution.get("split") + mutationDistribution.get("add") + mutationDistribution.get("disable")){
                mutateDisable(genotype);
            }
            else{
                mutateEnable(genotype);
            }
        }
    }

    private void mutateWeight(NEATGenotype genotype){
        ConnectionGene connectionGene = genotype.getRandomEnabledConnectionGene();
        connectionGene.setWeight(connectionGene.getWeight() + (randomGenerator.nextDouble(1) * 2 - 1) * weightMutationPower);
    }

    private void mutateSplit(NEATGenotype genotype){
        ConnectionGene connectionGene = genotype.getRandomEnabledConnectionGene();
        genotype.splitConnection(connectionGene);
    }

    private void mutateAdd(NEATGenotype genotype){
        NodeGene inNode;
        NodeGene outNode;
        do{
            inNode = genotype.getRandomNodeGene();
        }while(inNode.getType() == NodeType.OUTPUT);

        do{
            outNode = genotype.getRandomNodeGene();
        }while(outNode.getType() == NodeType.INPUT || outNode.isBias() || genotype.connectionExists(inNode, outNode) || inNode.getLayer() >= outNode.getLayer()); //TODO: Implement recurrent Connections

        genotype.createConnection(inNode, outNode, randomGenerator.nextDouble(1));
    }

    private void mutateDisable(NEATGenotype genotype){
        ConnectionGene connectionGene;
        do{
            connectionGene = genotype.getFirstEnabledConnectionFromOffset((int) (randomGenerator.nextDouble(1) * genotype.getNumberOfConnectionGenes()));
        }while(!connectionGene.isEnabled());
        connectionGene.setEnabled(false);
    }

    private void mutateEnable(NEATGenotype genotype){
        ConnectionGene connectionGene;
        do{
            connectionGene = genotype.getFirstDisabledConnectionFromOffset((int) (randomGenerator.nextDouble(1) * genotype.getNumberOfConnectionGenes()));
        }while(connectionGene.isEnabled());
        connectionGene.setEnabled(true);
    }
}
