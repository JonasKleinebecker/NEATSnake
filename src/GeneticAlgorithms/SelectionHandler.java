package GeneticAlgorithms;

import HelperClasses.Pair;

public interface SelectionHandler<T> {
    public Pair<Pair<T, Integer>, Pair<T, Integer>> select(Pair<T, Integer>[] population);
}
