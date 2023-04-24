package GeneticAlgorithms;

import HelperClasses.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GeneticAlgorithm<T> {
    Pair<T,Integer>[] population; //Pair of Individual and Fitness
    SelectionHandler<T> selectionHandler;
    MutationHandler<T> mutationHandler;
    CrossoverHandler<T> crossoverHandler;
    EvaluationHandler<T> evaluationHandler;

    public GeneticAlgorithm(Pair<T,Integer>[] population, SelectionHandler selectionHandler, MutationHandler mutationHandler, CrossoverHandler crossoverHandler, EvaluationHandler evaluationHandler) {
        this.selectionHandler = selectionHandler;
        this.mutationHandler = mutationHandler;
        this.crossoverHandler = crossoverHandler;
        this.evaluationHandler = evaluationHandler;
        population = population;
    }
    public void calculateNextGeneration(){
        Pair<T,Integer>[] newPopulation = new Pair[population.length];
        population = evaluationHandler.evaluate(population);
        Arrays.sort(population, Comparator.comparing(Pair::getSecond)); //sort by fitness
        for(int i=0;i<population.length;i++){
            Pair<Pair<T,Integer>, Pair<T,Integer>> parents = selectionHandler.select(population);
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
