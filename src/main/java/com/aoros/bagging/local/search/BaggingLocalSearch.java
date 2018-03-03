package com.aoros.bagging.local.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BaggingLocalSearch {

    private BaggingSolution solution;
    private final int timeLimitInSecs;
    private final Random r = new Random();
    private final boolean verbose;

    public BaggingLocalSearch(BaggingSolution startingSolution, int timeLimitInSecs, boolean verbose) {
        this.solution = startingSolution;
        this.timeLimitInSecs = timeLimitInSecs;
        this.verbose = verbose;
    }

    public BaggingSolution performSearch() {
        long startTimer = System.currentTimeMillis();
        while (true) {
            if (verbose)
                Bagging.printBagOfItems(solution);

            if (solution.getSolutionScore() == 0)
                break;

            int randBagNumber = pickOneBagAtRandom();
            int randItemIdFromRandBag = removeOneItemFromRandomBag(randBagNumber);
            List<BaggingSolution> nextMoveSolutions = getAllNextMoveSolutions(randBagNumber, randItemIdFromRandBag);
            List<BaggingSolution> lowestScoresSolutions = findLowestScoreSolutions(nextMoveSolutions);
            copyNextMoveSolution(lowestScoresSolutions);

            //    if timer > timeLimit
            long endTimer = System.currentTimeMillis();
            long durationInSecs = (endTimer - startTimer) / 1000;
            if (durationInSecs >= timeLimitInSecs) {
                break;
            }
        }

        return solution;
    }

    private int pickOneBagAtRandom() {
        //    pick one bag at random from nextSolution
        int randBagNumber = -1;
        boolean bagHasItems = false;
        while (!bagHasItems) {
            randBagNumber = r.nextInt(solution.getBagsOfItems().length);
            if (solution.getBagsOfItems()[randBagNumber].size() > 0)
                bagHasItems = true;
        }
        return randBagNumber;
    }

    private int removeOneItemFromRandomBag(int randBagNumber) {
        //    remove one item from bag at random
        Set<Integer> randBagItemIds = solution.getBagsOfItems()[randBagNumber];
        int randItemIdFromRandBag = r.nextInt(randBagItemIds.size());
        Integer[] integerArray = new Integer[randBagItemIds.size()];
        Integer[] randBagItemsIdsArray = randBagItemIds.toArray(integerArray);

        solution.removeItemFromBag(randBagNumber, randBagItemsIdsArray[randItemIdFromRandBag]);

        return randBagItemsIdsArray[randItemIdFromRandBag];
    }

    private List<BaggingSolution> getAllNextMoveSolutions(int randBagNumber, int randItemIdFromRandBag) {
        //    for each bag in bagsOfItems
        //       put item into each bag -> as new solution
        List<BaggingSolution> nextMoveSolutions = new ArrayList<>();
        for (int i = 0; i < solution.getBagsOfItems().length; i++) {
            BaggingSolution nextMoveSolution = new BaggingSolution(solution);
            if (randBagNumber != i) {
                int itemSize = solution.getItems().getSizes()[i];
                nextMoveSolution.addItemToBag(i, randItemIdFromRandBag);
                nextMoveSolutions.add(nextMoveSolution);
            }
        }
        return nextMoveSolutions;
    }

    private List<BaggingSolution> findLowestScoreSolutions(List<BaggingSolution> nextMoveSolutions) {
        //    select lowest score(s) from new solutions and make that the nextSolution
        int lowestScore = -1000000000;
        for (BaggingSolution nextMoveSolution : nextMoveSolutions) {
            lowestScore = nextMoveSolution.getSolutionScore() > lowestScore ? nextMoveSolution.getSolutionScore() : lowestScore;
        }
        List<BaggingSolution> lowestScoresSolutions = new ArrayList<>();
        for (BaggingSolution nextMoveSolution : nextMoveSolutions) {
            if (lowestScore == nextMoveSolution.getSolutionScore()) {
                lowestScoresSolutions.add(nextMoveSolution);
            }
        }
        return lowestScoresSolutions;
    }

    private void copyNextMoveSolution(List<BaggingSolution> lowestScoresSolutions) {
        // Copy next solution to solution
        if (lowestScoresSolutions.size() > 0) {
            int randNextSolution = r.nextInt(lowestScoresSolutions.size());
            solution = new BaggingSolution(lowestScoresSolutions.get(randNextSolution));
        } else if (lowestScoresSolutions.size() == 1) {
            solution = new BaggingSolution(lowestScoresSolutions.get(0));
        }
    }
}
