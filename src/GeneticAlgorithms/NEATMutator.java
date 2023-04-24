package GeneticAlgorithms;

import Genotypes.Genotype;
import HelperClasses.NodeType;
import HelperClasses.Pair;
import NEATGenes.ConnectionGene;
import Genotypes.NEATGenotype;
import NEATGenes.NodeGene;

import java.util.Map;

public class NEATMutator implements MutationHandler<NEATGenotype>{
    double mutationRate;
    double weightMutationPower; //Weight is changed by -1 to 1 * weightMutationPower
    Map<String, Double> mutationDistribution;

    public NEATMutator(double mutationRate, double weightMutationPower, double weightMutationDistribution, double splitMutationDistribution, double addMutationDistribution, double disableMutationDistribution, double enableMutationDistribution) {
        this.mutationRate = mutationRate;
        this.weightMutationPower = weightMutationPower;
        setProbabilities(Map.of("weight", weightMutationDistribution, "split", splitMutationDistribution, "add", addMutationDistribution, "disable", disableMutationDistribution, "enable", enableMutationDistribution));
    }

    public void setProbabilities(Map<String, Double> mutationDistribution){
        this.mutationDistribution = mutationDistribution;
        double total = 0.0;
        for (double probability : mutationDistribution.values()) {
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
        if(Math.random() < mutationRate){
            double random = Math.random();
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
        ConnectionGene connectionGene = genotype.getRandomConnectionGene();
        connectionGene.setWeight(connectionGene.getWeight() + (Math.random() * 2 - 1) * weightMutationPower);
    }

    private void mutateSplit(NEATGenotype genotype){
        ConnectionGene connectionGene = genotype.getRandomConnectionGene();
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

        genotype.createConnection(inNode, outNode, Math.random());
    }

    private void mutateDisable(NEATGenotype genotype){
        ConnectionGene connectionGene;
        do{
            connectionGene = genotype.getFirstEnabledConnectionFromOffset((int) (Math.random() * genotype.getNumberOfConnectionGenes()));
        }while(!connectionGene.isEnabled());
        connectionGene.setEnabled(false);
    }

    private void mutateEnable(NEATGenotype genotype){
        ConnectionGene connectionGene;
        do{
            connectionGene = genotype.getFirstDisabledConnectionFromOffset((int) (Math.random() * genotype.getNumberOfConnectionGenes()));
        }while(connectionGene.isEnabled());
        connectionGene.setEnabled(true);
    }
}
