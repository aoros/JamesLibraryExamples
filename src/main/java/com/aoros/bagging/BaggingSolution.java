package com.aoros.bagging;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BaggingSolution
{

	private final int maxBagSize;
	private final Set<Integer>[] bagsOfItems;
	private final int[] bagSizes;
	private final Map<Integer, Set<Integer>> inclusivityMap;
	private final int[] inclusivityBagViolationScores;
	private final BaggingItems items;

	public BaggingSolution(BaggingItems items)
	{
		this.items = items;
		this.maxBagSize = items.getMaxBagCapacity();
		bagsOfItems = new Set[items.getNumAvailableBags()];
		for (int i = 0; i < bagsOfItems.length; i++)
		{
			bagsOfItems[i] = new HashSet<>();
		}
		bagSizes = new int[items.getNumAvailableBags()];
		inclusivityMap = items.getInclusivityMap();
		inclusivityBagViolationScores = new int[items.getNumAvailableBags()];
	}

	public BaggingSolution(BaggingSolution solution)
	{
		this.items = solution.items;
		this.maxBagSize = solution.maxBagSize;
		this.bagsOfItems = solution.bagsOfItems;
		this.bagSizes = solution.bagSizes;
		this.inclusivityMap = new HashMap<>();
		this.inclusivityMap.putAll(solution.inclusivityMap);
		this.inclusivityBagViolationScores = solution.inclusivityBagViolationScores;
	}

	public BaggingItems getItems()
	{
		return items;
	}

	public boolean willItemFitInBag(int bagNumber, int itemSize)
	{
		return bagSizes[bagNumber] + itemSize <= maxBagSize;
	}

	public void addItemToBag(int bagNumber, Integer itemId, Integer itemSize)
	{
		bagsOfItems[bagNumber].add(itemId);
		bagSizes[bagNumber] += itemSize;
		addToInclusivityViolations(bagNumber, itemId);
	}

	public Set<Integer>[] getBagsOfItems()
	{
		return bagsOfItems;
	}

	public int getBagSize(int index)
	{
		return bagSizes[index];
	}

	public int getSolutionScore()
	{
		int solutionScore = 0;
		for (int i = 0; i < inclusivityBagViolationScores.length; i++)
			solutionScore += inclusivityBagViolationScores[i];
		return solutionScore;
	}

	private void addToInclusivityViolations(int bagNumber, Integer itemId)
	{
		int score = 0;
		for (Iterator<Integer> it = bagsOfItems[bagNumber].iterator(); it.hasNext();)
		{
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

	@Override
	public String toString()
	{
		return "BaggingSolution{" + "maxBagSize=" + maxBagSize + ", bagsOfItems=" + bagsOfItems + ", bagSizes=" + bagSizes + ", inclusivityMap=" + inclusivityMap + ", inclusivityBagViolationScores=" + inclusivityBagViolationScores + '}';
	}
}
