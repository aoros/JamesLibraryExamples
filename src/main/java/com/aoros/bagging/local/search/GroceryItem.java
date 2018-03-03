package com.aoros.bagging.local.search;

import java.util.Set;

public class GroceryItem {

    private final String itemName;
    private final int size;
    private final String clusivitySymbol;
    private final Set<String> clusivityItems;

    public GroceryItem(String itemName, int size, String clusivitySymbol, Set<String> clusivityItems) {
        this.itemName = itemName;
        this.size = size;
        this.clusivitySymbol = clusivitySymbol;
        this.clusivityItems = clusivityItems;
    }

    public String getItemName() {
        return itemName;
    }

    public int getSize() {
        return size;
    }

    public String getClusivitySymbol() {
        return clusivitySymbol;
    }

    public Set<String> getClusivityItems() {
        return clusivityItems;
    }

}
