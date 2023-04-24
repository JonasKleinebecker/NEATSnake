package GeneticAlgorithms;

import HelperClasses.Pair;

public interface EvaluationHandler<T> {
    public Pair<T, Integer>[] evaluate(Pair<T, Integer>[] population);
}
