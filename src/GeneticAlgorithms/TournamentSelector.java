package GeneticAlgorithms;

import java.util.Random;

import HelperClasses.Pair;

public class TournamentSelector implements SelectionHandler{
    int tournamentSize;
    Random randomGenerator;
     
    public TournamentSelector(Random randomGenerator, int tournamentSize){
        this.tournamentSize = tournamentSize;
        this.randomGenerator = randomGenerator;
    }
    @Override
    public Pair<Integer, Integer> select(Integer[] population) {
        if(tournamentSize > population.length){
            throw new IllegalArgumentException("Tournament size cannot be greater than population size");
        }
        else if(tournamentSize <= 2){
            throw new IllegalArgumentException("Tournament size must be atleast 2");
        }

        Integer indexBest = null;
        Integer indexSecondBest = null;
        for(int i = 0; i < tournamentSize; i++){
            int index = (randomGenerator.nextInt(population.length));
            if(indexBest == null || population[index] > population[indexBest]){
                indexSecondBest = indexBest;
                indexBest = index;
            }
            else if(indexSecondBest == null || population[index] > population[indexSecondBest]){
                indexSecondBest = index;
            }
        }
        return new Pair<>(indexBest, indexSecondBest);
    }
}
