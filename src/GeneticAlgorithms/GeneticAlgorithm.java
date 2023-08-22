package GeneticAlgorithms;

import HelperClasses.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GeneticAlgorithm<T> {
    Pair<T,Integer>[] population; //Pair of Individual and Fitness
    SelectionHandler selectionHandler;
    MutationHandler<T> mutationHandler;
    CrossoverHandler<T> crossoverHandler;
    EvaluationHandler<T> evaluationHandler;

    public GeneticAlgorithm(Pair<T,Integer>[] population, SelectionHandler selectionHandler, MutationHandler<T> mutationHandler, CrossoverHandler<T> crossoverHandler, EvaluationHandler<T> evaluationHandler) {
        this.selectionHandler = selectionHandler;
        this.mutationHandler = mutationHandler;
        this.crossoverHandler = crossoverHandler;
        this.evaluationHandler = evaluationHandler;
        this.population = population;
    }
    public void calculateNextGeneration(){
        Pair<T,Integer>[] newPopulation = new Pair[population.length];

        population = evaluationHandler.evaluate(population);
        Arrays.sort(population, Comparator.comparing(Pair::getSecond)); //sort by fitness
        Integer[] populationFitness = new Integer[population.length];
        for(int i=0;i<population.length;i++){
            Pair<Integer, Integer> parentIndices = selectionHandler.select(populationFitness);
            Pair<T, Integer> parent1 = population[parentIndices.getFirst()];
            Pair<T, Integer> parent2 = population[parentIndices.getSecond()];
            Pair<Pair<T, Integer>, Pair<T, Integer>> parents = new Pair<Pair<T, Integer>, Pair<T, Integer>>(parent1, parent2);
            Pair<T,Integer> child = crossoverHandler.crossover(parents);
            mutationHandler.mutate(child);
            newPopulation[i] = child;
        }
        population = newPopulation;
    }

    public Pair<T, Integer>[] getPopulation() {
        return population;
    }
}
