package GeneticAlgorithms;

import HelperClasses.Pair;

public class ProportionalSelector<T> implements SelectionHandler<T>{
    public Pair<Pair<T, Integer>, Pair<T, Integer>> select(Pair<T, Integer>[] population) {
        int totalFitness = 0;
        for (Pair<T, Integer> individual : population) {
            totalFitness += individual.getSecond();
        }
        int firstIndex = 0;
        int secondIndex = 0;
        int firstRandom = (int) (Math.random() * totalFitness);
        int secondRandom = (int) (Math.random() * totalFitness);
        int currentFitness = 0;
        for (int i = 0; i < population.length; i++) {
            currentFitness += population[i].getSecond();
            if (currentFitness >= firstRandom) {
                firstIndex = i;
                break;
            }
        }
        currentFitness = 0;
        for (int i = 0; i < population.length; i++) {
            currentFitness += population[i].getSecond();
            if (currentFitness >= secondRandom) {
                secondIndex = i;
                break;
            }
        }
        return new Pair<Pair<T, Integer>, Pair<T, Integer>>(population[firstIndex], population[secondIndex]);
    }
}
