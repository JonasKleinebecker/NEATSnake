package UnitTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import GeneticAlgorithms.TournamentSelector;
import HelperClasses.Pair;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Random;

public class TournamentSelectorTest {

    private Random fixedRandom;

    @BeforeEach
    public void setUp() {
        fixedRandom = new Random(50); 
    }

    @Test
    public void testSelectWithValidTournamentSize() {
        Integer[] population = {-13, -5, 0, 10, 20, 30, 40, 50, 62, 74, 82, 103, 204, 305, 406, 507, 608, 709, 1004, 10000};
        TournamentSelector selector = new TournamentSelector(fixedRandom, 5);

        Pair<Integer, Integer> result = selector.select(population);

        assertEquals(17, result.getFirst());
        assertEquals(13, result.getSecond());
    }

    @Test
    public void testSelectWithInvalidTournamentSizeGreaterThanPopulation() {
        Integer[] population = {10, 20, 30, 40, 50};
        TournamentSelector selector = new TournamentSelector(fixedRandom, 10);

        assertThrows(IllegalArgumentException.class, () -> {
            selector.select(population);
        });
    }

    @Test
    public void testSelectWithInvalidTournamentSizeLessThanTwo() {
        Integer[] population = {10, 20, 30, 40, 50};
        TournamentSelector selector = new TournamentSelector(fixedRandom, 1);

        assertThrows(IllegalArgumentException.class, () -> {
            selector.select(population);
        });
    }
}

