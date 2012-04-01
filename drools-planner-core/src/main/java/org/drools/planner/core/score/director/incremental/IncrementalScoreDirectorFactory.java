/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.core.score.director.incremental;

import org.drools.planner.core.score.director.AbstractScoreDirectorFactory;
import org.drools.planner.core.score.director.ScoreDirectorFactory;
import org.drools.planner.core.score.director.simple.SimpleScoreDirector;

/**
 * Incremental implementation of {@link ScoreDirectorFactory}.
 * @see IncrementalScoreDirector
 * @see ScoreDirectorFactory
 */
public class IncrementalScoreDirectorFactory extends AbstractScoreDirectorFactory {

    private final IncrementalScoreCalculator incrementalScoreCalculator;

    public IncrementalScoreDirectorFactory(IncrementalScoreCalculator incrementalScoreCalculator) {
        this.incrementalScoreCalculator = incrementalScoreCalculator;
    }

    public IncrementalScoreCalculator getIncrementalScoreCalculator() {
        return incrementalScoreCalculator;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public IncrementalScoreDirector buildScoreDirector() {
        return new IncrementalScoreDirector(this, incrementalScoreCalculator);
    }

}
