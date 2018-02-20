package com.aoros.bagging;

import java.io.FileNotFoundException;

/**
 * Main class for the bagging problem.
 */
public class Bagging {

    /**
     * Runs the bagging problem. Expects three parameters: (1) the input file
     * path, (2) the runtime limit (in seconds).
     *
     * @param args array containing the input file path and runtime limit
     */
    public static void main(String[] args) {
        System.out.println("########################");
        System.out.println("# BAGGING PROBLEM #");
        System.out.println("########################");
        // parse arguments
        if (args.length != 2) {
            System.err.println("Usage: java Bagging <inputfile> <runtime>");
            System.exit(1);
        }
        String filePath = args[0];
        int timeLimit = Integer.parseInt(args[1]);
        run(filePath, timeLimit);
    }

    private static void run(String filePath, int timeLimit) {
        System.out.println("# PARSING INPUT");
        System.out.println("Reading file: " + filePath);

        try {

            BaggingItems items = new BaggingFileReader().read(filePath);
            BaggingData data = new BaggingData(items);
            data.createRandomSolutions(100000);

            System.out.println(data.getIDsToSolutionMap());

        } catch (FileNotFoundException ex) {
            System.err.println("Failed to read file: " + filePath);
            System.exit(2);
        }

    }
}
