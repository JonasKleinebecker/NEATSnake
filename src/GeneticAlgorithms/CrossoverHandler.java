package GeneticAlgorithms;

import HelperClasses.Pair;

public interface CrossoverHandler<T> {
    public Pair<T,Integer> crossover(Pair<Pair<T,Integer>,Pair<T,Integer>> parents);
}
