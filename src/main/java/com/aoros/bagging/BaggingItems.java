package com.aoros.bagging;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaggingItems {

    private final int numAvailableBags, maxBagCapacity;
    private final Map<String, Integer> nameToIDMap = new HashMap<>();
    private final Set<Integer> IDs = new HashSet<>();
    private final int[] sizes;
    private final Map<Integer, Set<Integer>> inclusivityMap = new HashMap<>();
    private final Set<Integer>[] inclusivitySets;

    public BaggingItems(int numAvailableBags, int maxBagCapacity, List<GroceryItem> groceryItems) {
        this.numAvailableBags = numAvailableBags;
        this.maxBagCapacity = maxBagCapacity;
        sizes = new int[groceryItems.size()];
        inclusivitySets = new Set[groceryItems.size()];

        int i = 0;
        for (GroceryItem item : groceryItems) {
            nameToIDMap.put(item.getItemName(), i);
            IDs.add(i);
            sizes[i] = item.getSize();
            i++;
        }

        buildInclusivityMap(groceryItems);
    }

    private void buildInclusivityMap(List<GroceryItem> groceryItems) {
        for (GroceryItem item : groceryItems) {
            Integer currentItemID = nameToIDMap.get(item.getItemName());
            Set<Integer> includeIDsSet = new HashSet<>();
            includeIDsSet.add(currentItemID);

            Set<String> allItems = new HashSet<>();
            allItems.addAll(nameToIDMap.keySet());

            if ("+".equals(item.getClusivitySymbol())) {
                for (String itemName : item.getClusivityItems())
                    includeIDsSet.add(nameToIDMap.get(itemName));
            } else if ("-".equals(item.getClusivitySymbol())) {
                allItems.removeAll(item.getClusivityItems());
                Set<String> inclusiveItems = allItems;
                for (String itemName : inclusiveItems)
                    includeIDsSet.add(nameToIDMap.get(itemName));
            } else if ("".equals(item.getClusivitySymbol())) {
                for (String itemName : allItems)
                    includeIDsSet.add(nameToIDMap.get(itemName));
            }

            inclusivitySets[currentItemID] = includeIDsSet;
            inclusivityMap.put(currentItemID, includeIDsSet);
        }
    }

    public Set<Integer> getItemIds() {
        return IDs;
    }

    public int getNumAvailableBags() {
        return numAvailableBags;
    }

    public int getMaxBagCapacity() {
        return maxBagCapacity;
    }

    public Map<String, Integer> getNameToIDMap() {
        return nameToIDMap;
    }

    public int[] getSizes() {
        return sizes;
    }

    public Map<Integer, Set<Integer>> getInclusivityMap() {
        return inclusivityMap;
    }

    public Set<Integer>[] getInclusivitySets() {
        return inclusivitySets;
    }
}
