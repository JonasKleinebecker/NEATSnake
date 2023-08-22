package GeneticAlgorithms;

import HelperClasses.Pair;

public interface SelectionHandler {
    public Pair<Integer, Integer> select(Integer[] population);
}
