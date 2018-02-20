package com.aoros.bagging;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.jamesframework.core.problems.datatypes.IntegerIdentifiedData;

public class BaggingData implements IntegerIdentifiedData {

    private final Set<Integer> IDs = new HashSet<>();
    private final Map<Integer, BaggingSolution> IDsToSolutionMap = new HashMap<>();
    private final BaggingItems items;

    public BaggingData(BaggingItems items) {
        this.items = items;
    }

    public void createRandomSolutions(int numToCreate) {
        Random r = new Random();
        for (int i = 0; i < numToCreate; i++) {
            BaggingSolution solution = new BaggingSolution(items);
            for (Integer itemId : items.getItemIds()) {
                int itemSize = items.getSizes()[itemId];
                boolean done = false;
                while (!done) {
                    int bagNumber = r.nextInt(items.getNumAvailableBags());
                    if (solution.willItemFitInBag(bagNumber, itemSize)) {
                        solution.addItemToBag(bagNumber, itemId, itemSize);
                        done = true;
                    }
                }
            }
            IDsToSolutionMap.put(i, solution);
            IDs.add(i);
        }
    }

    @Override
    public Set<Integer> getIDs() {
        return IDs;
    }

    public Map<Integer, BaggingSolution> getIDsToSolutionMap() {
        return IDsToSolutionMap;
    }
}
