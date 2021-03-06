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

package org.jamesframework.examples.knapsack;

import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.sol.RandomSolutionGenerator;
import org.jamesframework.core.subset.SubsetProblem;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.search.algo.ParallelTempering;
import org.jamesframework.core.search.algo.RandomDescent;
import org.jamesframework.core.subset.neigh.SinglePerturbationNeighbourhood;
import org.jamesframework.core.search.stopcriteria.MaxRuntime;
import org.jamesframework.core.util.SetUtilities;
import org.jamesframework.examples.util.ProgressSearchListener;

/**
 * Main class for the knapsack example (example 2A).
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class Knapsack {
        
    /**
     * Runs the knapsack problem. Expects three parameters: (1) the input file path, (2) the capacity of the knapsack
     * and (3) the runtime limit (in seconds). The input is specified in a text file in which the first row contains
     * a single number N that indicates number of available knapsack items. The next N rows each contain the profit
     * and weight (in this order) of a single item, separated by one or more spaces.
     * 
     * @param args array containing the input file path, knapsack capacity and runtime limit
     */
    public static void main(String[] args) {
        System.out.println("########################");
        System.out.println("# 0/1 KNAPSACK PROBLEM #");
        System.out.println("########################");
        // parse arguments
        if(args.length != 3){
            System.err.println("Usage: java -cp james-examples.jar org.jamesframework.examples.knapsack.KnapSack <inputfile> <capacity> <runtime>");
            System.exit(1);
        }
//        String filePath = args[0];
        String filePath = "src/main/resources/knapsack-100";
        double capacity = Double.parseDouble(args[1]);
        int timeLimit = Integer.parseInt(args[2]);
        run(filePath, capacity, timeLimit);
    }
    
    private static void run(String filePath, double capacity, int timeLimit){
        
        /***************/
        /* PARSE INPUT */
        /***************/
        
        System.out.println("# PARSING INPUT");
        System.out.println("Reading file: " + filePath);
        
        try {
            
            KnapsackData data = new KnapsackFileReader().read(filePath);
        
            /*********************/
            /* OPTIMIZE KNAPSACK */
            /*********************/

            System.out.println("# OPTIMIZING KNAPSACK");

            System.out.println("Dataset size: " + data.getIDs().size());
            System.out.println("Knapsack capacity: " + capacity);
            System.out.println("Time limit: " + timeLimit + " seconds");

            // create objective
            KnapsackObjective obj = new KnapsackObjective();
                        
            // create constraint
            KnapsackConstraint constraint = new KnapsackConstraint(capacity);
            // create subset problem (all sizes allowed)
            SubsetProblem<KnapsackData> problem = new SubsetProblem<>(data, obj);
            // add mandatory constraint
            problem.addMandatoryConstraint(constraint);
            
            /**************************************************/
            /* Set custom random solution generator to ensure */
            /* initial selection within knapsack capacity     */
            /**************************************************/
            
            // retrieve default random solution generator
            RandomSolutionGenerator<? extends SubsetSolution, ? super KnapsackData> defaultRndSolGen = problem.getRandomSolutionGenerator();
            
            // set custom generator
            problem.setRandomSolutionGenerator((r,d) -> {
                // 1: create default random initial solution
                SubsetSolution sol = defaultRndSolGen.create(r, d);
                // 2: compute current total weight
                double weight = computeSelectionWeight(sol, d);
                // 3: remove random items as long as total weight is larger than the capacity
                while(weight > capacity){
                    int id = SetUtilities.getRandomElement(sol.getSelectedIDs(), r);
                    sol.deselect(id);
                    weight -= data.getWeight(id);
                }
                // 4: retain random subset to increase initial solution variability
                int finalSize = r.nextInt(sol.getNumSelectedIDs()+1);
                sol.deselectAll(SetUtilities.getRandomSubset(sol.getSelectedIDs(),
                                                                         sol.getNumSelectedIDs()-finalSize,
                                                                         r));
                return sol;
            });
            
            /******************/
            /* RANDOM DESCENT */
            /******************/
            
            System.out.println("# RANDOM DESCENT");
            
            // create random descent search with single perturbation neighbourhood
            RandomDescent<SubsetSolution> randomDescent = new RandomDescent<>(problem, new SinglePerturbationNeighbourhood());
            // set maximum runtime
            randomDescent.addStopCriterion(new MaxRuntime(timeLimit, TimeUnit.SECONDS));
            // attach listener
            randomDescent.addSearchListener(new ProgressSearchListener());

            // start search
            randomDescent.start();
            // print results
            Integer randomDescentKnapsackSize = null;
            Evaluation randomDescentKnapsackProfit = null;
            Double randomDescentKnapsackWeight = null;
            if(randomDescent.getBestSolution() != null){
                randomDescentKnapsackSize = randomDescent.getBestSolution().getNumSelectedIDs();
                System.out.println("Items in knapsack: " + randomDescentKnapsackSize + "/" + data.getIDs().size());
                randomDescentKnapsackProfit = randomDescent.getBestSolutionEvaluation();
                System.out.println("Total profit: " + randomDescentKnapsackProfit);
                randomDescentKnapsackWeight = computeSelectionWeight(randomDescent.getBestSolution(), data);
                System.out.println("Total weight: " + randomDescentKnapsackWeight + "/" + capacity);
            } else {
                System.out.println("No valid solution found...");
            }
            // dispose search
            randomDescent.dispose();
           
            /**********************/
            /* PARALLEL TEMPERING */
            /**********************/
            
            System.out.println("# PARALLEL TEMPERING");
            
            // set temperature range, scaled according to average profit of knapsack items
            double scale = computeAverageProfit(data);
            double minTemp = scale * 0.001;
            double maxTemp = scale * 0.1;
            // create parallel tempering with single perturbation neighbourhood
            int numReplicas = 10;
            ParallelTempering<SubsetSolution> parallelTempering = new ParallelTempering<>(problem,
                                                                        new SinglePerturbationNeighbourhood(),
                                                                        numReplicas, minTemp, maxTemp);
            System.out.println("Min. temperature: " + minTemp);
            System.out.println("Max. temperature: " + maxTemp);
            
            // set maximum runtime
            parallelTempering.addStopCriterion(new MaxRuntime(timeLimit, TimeUnit.SECONDS));
            // attach listener
            parallelTempering.addSearchListener(new ProgressSearchListener());
            
            // start search
            parallelTempering.start();
            // print results
            Integer parallelTemperingKnapsackSize = null;
            Evaluation parallelTemperingKnapsackProfit = null;
            Double parallelTemperingKnapsackWeight = null;
            if(parallelTempering.getBestSolution() != null){
                parallelTemperingKnapsackSize = parallelTempering.getBestSolution().getNumSelectedIDs();
                System.out.println("Items in knapsack: " + parallelTemperingKnapsackSize + "/" + data.getIDs().size());
                parallelTemperingKnapsackProfit = parallelTempering.getBestSolutionEvaluation();
                System.out.println("Total profit: " + parallelTemperingKnapsackProfit);
                parallelTemperingKnapsackWeight = computeSelectionWeight(parallelTempering.getBestSolution(), data);
                System.out.println("Total weight: " + parallelTemperingKnapsackWeight + "/" + capacity);
            } else {
                System.out.println("No valid solution found...");
            }
            // dispose search
            parallelTempering.dispose();
            
            /***********/
            /* SUMMARY */
            /***********/
            
            System.out.println("------------------------------------------------------");
            System.out.println("Summary:");
            System.out.println("------------------------------------------------------");

            System.out.println("Dataset size: " + data.getIDs().size());
            System.out.println("Knapsack capacity: " + capacity);
            System.out.println("Time limit: " + timeLimit + " seconds");
            System.out.println("------------------------------------------------------");

            System.out.format("%20s %10s %10s %10s \n", "", "size", "profit", "weight");
            System.out.format("%20s %10s %10s %10s \n",
                                "Random descent:",
                                randomDescentKnapsackSize != null ? randomDescentKnapsackSize : "-",
                                randomDescentKnapsackProfit != null ? randomDescentKnapsackProfit : "-",
                                randomDescentKnapsackWeight != null ? randomDescentKnapsackWeight : "-");
            System.out.format("%20s %10s %10s %10s \n",
                                "Parallel tempering:",
                                parallelTemperingKnapsackSize != null ? parallelTemperingKnapsackSize : "-",
                                parallelTemperingKnapsackProfit != null ? parallelTemperingKnapsackProfit : "-",
                                parallelTemperingKnapsackWeight != null ? parallelTemperingKnapsackWeight : "-");
            System.out.println("------------------------------------------------------");
            
        } catch (FileNotFoundException ex) {
            System.err.println("Failed to read file: " + filePath);
            System.exit(2);
        }
        
    }
    
    private static double computeSelectionWeight(SubsetSolution solution, KnapsackData data){
        return solution.getSelectedIDs().stream().mapToDouble(data::getWeight).sum();
    }
    
    private static double computeAverageProfit(KnapsackData data){
        return data.getIDs().stream().mapToDouble(data::getProfit).average().getAsDouble();
    }
    
}