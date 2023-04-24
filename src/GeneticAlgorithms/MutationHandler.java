package GeneticAlgorithms;

import HelperClasses.Pair;

public interface MutationHandler<T> {
    public Pair<T, Integer> mutate(Pair<T, Integer> individual);
}
