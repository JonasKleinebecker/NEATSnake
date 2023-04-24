package GeneticAlgorithms;

import HelperClasses.Pair;

public class TournamentSelector<T> implements SelectionHandler<T>{
    int tournamentSize;
    public TournamentSelector(int tournamentSize){
        this.tournamentSize = tournamentSize;
    }
    @Override
    public Pair<Pair<T, Integer>, Pair<T, Integer>> select(Pair<T, Integer>[] population) {
        Pair<T, Integer> best = null;
        Pair<T, Integer> secondBest = null;
        for(int i = 0; i < tournamentSize; i++){
            int index = (int)(Math.random() * population.length);
            if(best == null || population[index].getSecond() > best.getSecond()){
                secondBest = best;
                best = population[index];
            }
            else if(secondBest == null || population[index].getSecond() > secondBest.getSecond()){
                secondBest = population[index];
            }
        }
        return new Pair<>(best, secondBest);
    }
}
