package com.aoros.bagging;

import java.io.FileNotFoundException;

/**
 * Main class for the bagging problem.
 */
public class Bagging
{
	private static String filePath = "src/main/resources/test4";
	private static int timeLimitInSecs = 1;
	private static final boolean VERBOSE = true;

	/**
	 * Runs the bagging problem. Expects two parameters: (1) the input file path, (2) the runtime limit (in seconds).
	 *
	 * @param args array containing the input file path and runtime limit
	 */
	public static void main(String[] args)
	{
		System.out.println("########################");
		System.out.println("# BAGGING PROBLEM #");
		System.out.println("########################");
		// parse arguments
		if (args.length != 2 && "".equals(filePath))
		{
			System.err.println("Usage: java Bagging <inputfile> <runtime>");
			System.exit(1);
		}
		if ("".equals(filePath))
		{
			filePath = args[0];
			timeLimitInSecs = Integer.parseInt(args[1]);
		}

		run(filePath, timeLimitInSecs);
	}

	private static void run(String filePath, int timeLimitInSecs)
	{
		System.out.println("# PARSING INPUT");
		try
		{
			printMsg("Reading file: " + filePath + "...", VERBOSE);
			BaggingItems items = new BaggingFileReader().read(filePath);
			printMsg("Creating BaggingData...", VERBOSE);
			BaggingData data = new BaggingData(items);
			printMsg("Creating random solutions...", VERBOSE);

			for (int k = 0; k < 1000; k++)
			{
				BaggingSolution randomBaggingSolution = data.createRandomSolution();
				BaggingLocalSearch search = new BaggingLocalSearch(randomBaggingSolution, timeLimitInSecs);
				BaggingSolution solution = search.performSearch();
				if (solution.getSolutionScore() == 0)
				{
					System.out.println("success");
					break;
				}
			}
			System.out.println("failed");

//			data.createRandomSolutions(2000);
//			data.printOutBaggingDataSolutionScores();
		}
		catch (FileNotFoundException ex)
		{
			System.err.println("Failed to read file: " + filePath);
			System.exit(2);
		}

	}

	private static void printMsg(String msg, boolean shouldPrint)
	{
		if (shouldPrint)
			System.out.println(msg);
	}
}
