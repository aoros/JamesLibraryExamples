package com.aoros.bagging.local.search;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BaggingSolution {

    private final int maxBagSize;
    private final Set<Integer>[] bagsOfItems;
    private final int[] bagSizes;
    private final Map<Integer, Set<Integer>> inclusivityMap;
    private final int[] inclusivityBagViolationScores;
    private final int[] bagSizeViolationScores;
    private final BaggingItems items;
    private int violationScore = 0;

    public BaggingSolution(BaggingItems items) {
        this.items = items;
        this.maxBagSize = items.getMaxBagCapacity();
        bagsOfItems = new Set[items.getNumAvailableBags()];
        for (int i = 0; i < bagsOfItems.length; i++) {
            bagsOfItems[i] = new HashSet<>();
        }
        bagSizes = new int[items.getNumAvailableBags()];
        inclusivityMap = items.getInclusivityMap();
        inclusivityBagViolationScores = new int[items.getNumAvailableBags()];
        bagSizeViolationScores = new int[items.getNumAvailableBags()];
    }

    public BaggingSolution(BaggingSolution solution) {
        this.items = solution.items;
        this.maxBagSize = solution.maxBagSize;
        this.bagsOfItems = new Set[solution.items.getNumAvailableBags()];
        this.bagSizes = new int[solution.bagSizes.length];
        System.arraycopy(solution.bagSizes, 0, this.bagSizes, 0, solution.bagSizes.length);
        this.inclusivityMap = solution.inclusivityMap;
        this.inclusivityBagViolationScores = new int[solution.inclusivityBagViolationScores.length];
        System.arraycopy(solution.inclusivityBagViolationScores, 0, this.inclusivityBagViolationScores, 0, solution.inclusivityBagViolationScores.length);
        this.bagSizeViolationScores = new int[solution.bagSizeViolationScores.length];
        System.arraycopy(solution.bagSizeViolationScores, 0, this.bagSizeViolationScores, 0, solution.bagSizeViolationScores.length);
        for (int i = 0; i < bagsOfItems.length; i++) {
            bagsOfItems[i] = new HashSet<>();
            for (Integer item : solution.bagsOfItems[i]) {
                bagsOfItems[i].add((int) item);
            }
        }
        calculateSolutionScore();
    }

    public BaggingItems getItems() {
        return items;
    }

    public boolean willItemFitInBag(int bagNumber, int itemSize) {
        return bagSizes[bagNumber] + itemSize <= maxBagSize;
    }

    public void addItemToBag(int bagNumber, Integer itemId) {
        int itemSize = items.getSizes()[itemId];
        bagsOfItems[bagNumber].add(itemId);
        bagSizes[bagNumber] += itemSize;
        calcInclusivityViolations(bagNumber, itemId);
        calcBagSizeViolations(bagNumber, itemId);
        calculateSolutionScore();
    }

    public void removeItemFromBag(int bagNumber, Integer itemId) {
        int itemSize = items.getSizes()[itemId];
        if (!bagsOfItems[bagNumber].remove(itemId))
            throw new IllegalStateException("Couldn't remove " + itemId + " from bag number " + bagNumber);
        bagSizes[bagNumber] -= itemSize;
        calcInclusivityViolations(bagNumber, itemId);
        calcBagSizeViolations(bagNumber, itemId);
        calculateSolutionScore();
    }

    public Set<Integer>[] getBagsOfItems() {
        return bagsOfItems;
    }

    public int getBagSize(int index) {
        return bagSizes[index];
    }

    public int getSolutionScore() {
        return violationScore;
    }

    private void calcInclusivityViolations(int bagNumber, Integer itemId) {
        int score = 0;
        for (Iterator<Integer> it = bagsOfItems[bagNumber].iterator(); it.hasNext();) {
            // Get all items in the bag
            Set<Integer> itemsInBag = new HashSet<>();
            itemsInBag.addAll(bagsOfItems[bagNumber]);

            Set<Integer> inclusivitySet = new HashSet<>();
            inclusivitySet.addAll(inclusivityMap.get(it.next()));

            itemsInBag.removeAll(inclusivitySet);
            score -= itemsInBag.size();
        }
        inclusivityBagViolationScores[bagNumber] = score;
    }

    private void calcBagSizeViolations(int bagNumber, Integer itemId) {
        int score = maxBagSize - bagSizes[bagNumber];
        bagSizeViolationScores[bagNumber] = score >= 0 ? 0 : score;
    }

    @Override
    public String toString() {
        return "BaggingSolution{" + "maxBagSize=" + maxBagSize + ", bagsOfItems=" + bagsOfItems + ", bagSizes=" + bagSizes + ", inclusivityMap=" + inclusivityMap + ", inclusivityBagViolationScores=" + inclusivityBagViolationScores + '}';
    }

    private void calculateSolutionScore() {
        violationScore = 0;
        for (int i = 0; i < inclusivityBagViolationScores.length; i++)
            violationScore += inclusivityBagViolationScores[i] + bagSizeViolationScores[i];
    }
}
