/*
 * Copyright 2014 Ghent University, Bayer CropScience.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aoros.bagging.local.search;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Reads an input file for the bagging problem (plain text). The first row
 * contains a single number N that indicates the number of available knapsack
 * items. The next N rows each contain the profit and weight (in this order) of
 * a single item, separated by one or more spaces.
 *
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class BaggingFileReader {

    public BaggingItems read(String filePath) throws FileNotFoundException {
        Scanner in = new Scanner(new FileReader(filePath));

        int numAvailableBags = Integer.parseInt(scrubRow(in.nextLine()));
        int maxBagCapacity = Integer.parseInt(scrubRow(in.nextLine()));

        List<GroceryItem> groceryItems = new ArrayList<>();
        while (in.hasNext()) {
            String[] items = scrubRow(in.nextLine()).split("\\s+");

            String clusivitySymbol = "";
            Set<String> clusivityItems = new HashSet<>();
            if (items.length > 2) {
                clusivitySymbol = items[2];
                if (!isValidSymbol(clusivitySymbol))
                    throw new IllegalArgumentException("Invalid Clusivity Symbol (use '+' or '-'): " + clusivitySymbol);
                for (int i = 3; i < items.length; i++) {
                    clusivityItems.add(items[i]);
                }
            }
            groceryItems.add(new GroceryItem(
                    items[0],
                    Integer.parseInt(items[1]),
                    clusivitySymbol, clusivityItems));
        }

        // create and return data object
        return new BaggingItems(numAvailableBags, maxBagCapacity, groceryItems);
    }

    private String scrubRow(String rowText) {
        int commentIndex = rowText.indexOf("//");
        if (commentIndex <= 0) {
            return rowText.trim();
        }
        return rowText.substring(0, commentIndex).trim();
    }

    private boolean isValidSymbol(String clusivitySymbol) {
        return "".equals(clusivitySymbol)
                || "-".equals(clusivitySymbol)
                || "+".equals(clusivitySymbol);
    }
}
