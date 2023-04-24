package GeneticAlgorithms;

import HelperClasses.Pair;

public interface MutationHandler<T> {
    public void mutate(Pair<T, Integer> individual);
}
