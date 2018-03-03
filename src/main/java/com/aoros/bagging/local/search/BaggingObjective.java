package com.aoros.bagging.local.search;

import java.util.Map;
import org.jamesframework.core.exceptions.IncompatibleDeltaEvaluationException;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.problems.objectives.Objective;
import org.jamesframework.core.problems.objectives.evaluations.SimpleEvaluation;
import org.jamesframework.core.search.neigh.Move;
import org.jamesframework.core.subset.SubsetSolution;
import org.jamesframework.core.subset.neigh.moves.SubsetMove;

/**
 * Objective for the bagging problem: maximize the total profit.
 */
public class BaggingObjective implements Objective<SubsetSolution, BaggingData> {

    @Override
    public Evaluation evaluate(SubsetSolution solution, BaggingData data) {
        // compute sum of profits of selected items
        Map<Integer, BaggingSolution> solutionMap = data.getIDsToSolutionMap();
        BaggingSolution baggingSolution = solutionMap.get(solution.getSelectedIDs().iterator().next());
        double value = baggingSolution.getSolutionScore();

        // wrap in simple evaluation object
        return SimpleEvaluation.WITH_VALUE(value);
    }

    @Override
    public Evaluation evaluate(Move move, SubsetSolution curSolution, Evaluation curEvaluation, BaggingData data) {
        // check move type
        if (!(move instanceof SubsetMove)) {
            throw new IncompatibleDeltaEvaluationException("Bagging objective should be used in combination "
                    + "with neighbourhoods that generate moves of type SubsetMove.");
        }
        // cast move
        SubsetMove subsetMove = (SubsetMove) move;
        // get current profit
        double value = curEvaluation.getValue();
//        // account for added items
//        value += subsetMove.getAddedIDs().stream().mapToDouble(data::getIDsToSolutionMap).sum();
//        // account for removed items
//        value -= subsetMove.getDeletedIDs().stream().mapToDouble(data::getProfit).sum();
        // return updated evaluation
        return SimpleEvaluation.WITH_VALUE(value);
    }

    @Override
    public boolean isMinimizing() {
        return false;
    }

}
