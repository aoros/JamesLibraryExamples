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

package org.jamesframework.examples.util;

import org.jamesframework.core.problems.sol.Solution;
import org.jamesframework.core.problems.constraints.validations.Validation;
import org.jamesframework.core.problems.objectives.evaluations.Evaluation;
import org.jamesframework.core.search.Search;
import org.jamesframework.core.search.listeners.SearchListener;

/**
 * Search listener that prints search progress to standard output.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class ProgressSearchListener implements SearchListener<Solution> {

    @Override
    public void searchStarted(Search search) {
        System.out.println(" >>> Search started");
    }

    @Override
    public void searchStopped(Search search) {
        System.out.println(" >>> Search stopped (" + search.getRuntime()/1000 + " sec, " + search.getSteps() + " steps)");
    }

    @Override
    public void newBestSolution(Search search,
                                Solution newBestSolution,
                                Evaluation newBestSolutionEvaluation,
                                Validation newBestSolutionValidation) {
        System.out.println(" >>> New best solution: " + newBestSolutionEvaluation);
    }
    
}