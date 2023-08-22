package GeneticAlgorithms;

import HelperClasses.Pair;

public class ProportionalSelector implements SelectionHandler{
    public Pair<Integer, Integer> select(Integer[] populationFitness) {
        int totalFitness = 0;
        for (Integer individual : populationFitness) {
            totalFitness += individual;
        }
        int firstIndex = 0;
        int secondIndex = 0;
        int firstRandom = (int) (Math.random() * totalFitness);
        int secondRandom = (int) (Math.random() * totalFitness);
        int currentFitness = 0;
        for (int i = 0; i < populationFitness.length; i++) {
            currentFitness += populationFitness[i];
            if (currentFitness >= firstRandom) {
                firstIndex = i;
                break;
            }
        }
        currentFitness = 0;
        for (int i = 0; i < populationFitness.length; i++) {
            currentFitness += populationFitness[i];
            if (currentFitness >= secondRandom) {
                secondIndex = i;
                break;
            }
        }
        return new Pair<Integer, Integer>(firstIndex, secondIndex);
    }
}
