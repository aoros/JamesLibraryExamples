package com.aoros.bagging.local.search;

import java.io.FileNotFoundException;
import java.util.Set;

/**
 * Main class for the bagging problem.
 */
public class Bagging {

    private static String filePath = "src/main/resources/test4";
    private static int timeLimitInSecs = 30;
    private static final boolean VERBOSE = false;

    /**
     * Runs the bagging problem. Expects two parameters: (1) the input file
     * path, (2) the runtime limit (in seconds).
     *
     * @param args array containing the input file path and runtime limit
     */
    public static void main(String[] args) {
        System.out.println("########################");
        System.out.println("# BAGGING PROBLEM #");
        System.out.println("########################");
        // parse arguments
        if (args.length != 2 && "".equals(filePath)) {
            System.err.println("Usage: java Bagging <inputfile> <runtime>");
            System.exit(1);
        }
        if ("".equals(filePath)) {
            filePath = args[0];
            timeLimitInSecs = Integer.parseInt(args[1]);
        }

        run(filePath, timeLimitInSecs);
    }

    private static void run(String filePath, int timeLimitInSecs) {
        System.out.println("# PARSING INPUT");
        try {
            printMsg("Reading file: " + filePath + "...", VERBOSE);
            BaggingItems items = new BaggingFileReader().read(filePath);
            printMsg("Creating BaggingData...", VERBOSE);
            BaggingData data = new BaggingData(items);
            printMsg("Creating random solutions...", VERBOSE);

            boolean solutionFound = false;
            for (int k = 0; k < 10; k++) {
                System.out.println("=== Start of ITERATION: " + k + " ===");
                BaggingSolution randomBaggingSolution = data.createRandomSolution();
                BaggingLocalSearch search = new BaggingLocalSearch(randomBaggingSolution, timeLimitInSecs, VERBOSE);
                BaggingSolution solution = search.performSearch();

                if (solution.getSolutionScore() == 0) {
                    printBagOfItems(solution);
                    solutionFound = true;
                    break;
                }
                System.out.println("=== End of ITERATION: " + k + " ===");
            }
            if (!solutionFound)
                System.out.println("No Solution Found");
        } catch (FileNotFoundException ex) {
            System.err.println("Failed to read file: " + filePath);
            System.exit(2);
        }

    }

    public static void printBagOfItems(BaggingSolution randomBaggingSolution) {
        System.out.println("-----------------------------------------------");
        System.out.println("Solution Score: " + randomBaggingSolution.getSolutionScore());
        for (Set<Integer> bagOfItems : randomBaggingSolution.getBagsOfItems()) {
            System.out.println(bagOfItems);
        }
    }

    private static void printMsg(String msg, boolean shouldPrint) {
        if (shouldPrint)
            System.out.println(msg);
    }
}
