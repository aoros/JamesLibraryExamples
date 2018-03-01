package com.aoros.bagging;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BaggingLocalSearch {

    private BaggingSolution solution;
    private final int timeLimitInSecs;
    private final Random r = new Random();

    public BaggingLocalSearch(BaggingSolution startingSolution, int timeLimitInSecs) {
        this.solution = startingSolution;
        this.timeLimitInSecs = timeLimitInSecs;
    }

    public BaggingSolution performSearch() {
        // start timer
        // while(not done)
        //    pick one bag at random from nextSolution
        //    remove one item from bag at random
        //    for each bag in bagsOfItems
        //       put item into each bag -> as new solution
        //    select lowest score(s) from new solutions and make that the nextSolution
        //    if nextSolution score is 0
        //       return nextSolution
        //    if timer > timeLimit
        //       return nextSolution or null (I'll have to decide this later)
        long startTimer = System.currentTimeMillis();
        while (true) {
            if (solution.getSolutionScore() == 0) {
                break;
            }

            //    pick one bag at random from nextSolution
            int randBagNumber = -1;
            boolean bagHasItems = false;
            while(!bagHasItems) {
                randBagNumber = r.nextInt(solution.getBagsOfItems().length);
                if (solution.getBagsOfItems()[randBagNumber].size() > 0)
                    bagHasItems = true;
            }
            //    remove one item from bag at random
            Set<Integer> randBagItemIds = solution.getBagsOfItems()[randBagNumber];
            Integer[] integerArray = new Integer[randBagItemIds.size()];
            Integer[] randBagItemsIdsArray = randBagItemIds.toArray(integerArray);
            int randItemIdFromRandBag = r.nextInt(randBagItemsIdsArray.length);
            solution.getBagsOfItems()[randBagNumber].remove(randItemIdFromRandBag);

            //    for each bag in bagsOfItems
            //       put item into each bag -> as new solution
            List<BaggingSolution> nextMoveSolutions = new ArrayList<>();
            for (int i = 0; i < solution.getBagsOfItems().length; i++) {
                BaggingSolution nextMoveSolution = new BaggingSolution(solution);
                if (randBagNumber != i) {
                    if (nextMoveSolution.willItemFitInBag(randBagNumber, solution.getItems().getSizes()[randItemIdFromRandBag])) {
                        nextMoveSolution.getBagsOfItems()[i].add(randItemIdFromRandBag);
                        nextMoveSolutions.add(nextMoveSolution);
                    }
                }
            }

            //    select lowest score(s) from new solutions and make that the nextSolution
            int bestScore = -1000000000;
            for (BaggingSolution nextMoveSolution : nextMoveSolutions) {
                bestScore = nextMoveSolution.getSolutionScore() > bestScore ? nextMoveSolution.getSolutionScore() : bestScore;
            }
            List<BaggingSolution> lowestScoresSolutions = new ArrayList<>();
            for (BaggingSolution nextMoveSolution : nextMoveSolutions) {
                if (bestScore == nextMoveSolution.getSolutionScore()) {
                    lowestScoresSolutions.add(nextMoveSolution);
                }
            }
            if (lowestScoresSolutions.size() > 0) {
                int randNextSolution = r.nextInt(lowestScoresSolutions.size());
                solution = lowestScoresSolutions.get(randNextSolution);
            }

            //    if timer > timeLimit
            long endTimer = System.currentTimeMillis();
            long durationInSecs = (endTimer - startTimer) / 1000;
            if (durationInSecs >= timeLimitInSecs) {
                break;
            }
            //       return nextSolution or null (I'll have to decide this later)
        }

        return solution;
    }

}
