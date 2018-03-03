package com.aoros.bagging.local.search;

import org.jamesframework.core.exceptions.IncompatibleDeltaEvaluationException;
import org.jamesframework.core.problems.constraints.Constraint;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.moves.SubsetMove;

/**
 * Knapsack constraint verifying that the maximum total weight is not exceeded.
 */
public class BaggingConstraint implements Constraint<SubsetSolution, BaggingItems> {

    // maximum total weight
    private final double maxWeight;

    public BaggingConstraint(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    @Override
    public Validation validate(SubsetSolution solution, BaggingItems data) {
        // compute sum of weights of selected items
        double weight = 1.0;
//        double weight = solution.getSelectedIDs().stream().mapToDouble(data::getWeight).sum();
        // return custom validation object
        return new BaggingValidation(weight, maxWeight);
    }

    @Override
    public Validation validate(Move move, SubsetSolution curSolution, Validation curValidation, BaggingItems data) {
        // check move type
        if (!(move instanceof SubsetMove)) {
            throw new IncompatibleDeltaEvaluationException("Knapsack constraint should be used in combination "
                    + "with neighbourhoods that generate moves of type SubsetMove.");
        }
        // cast move
        SubsetMove subsetMove = (SubsetMove) move;
        // cast current validation object (known to be of the required type as both 'validate'-methods return such object)
        BaggingValidation kVal = (BaggingValidation) curValidation;
        // extract current sum of weights
        double weight = kVal.getCurWeight();
        // account for added items
//        weight += subsetMove.getAddedIDs().stream().mapToDouble(data::getWeight).sum();
        // account for removed items
//        weight -= subsetMove.getDeletedIDs().stream().mapToDouble(data::getWeight).sum();
        // return updated validation
        return new BaggingValidation(weight, maxWeight);
    }

}
