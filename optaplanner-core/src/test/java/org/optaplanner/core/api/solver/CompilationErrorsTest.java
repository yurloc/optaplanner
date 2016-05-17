/*
 * Copyright 2016 JBoss by Red Hat.
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
package org.optaplanner.core.api.solver;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public class CompilationErrorsTest {

    public static class MyListener extends PhaseLifecycleListenerAdapter {

        @Override
        public void stepEnded(AbstractStepScope stepScope) {
            Solution createOrGetClonedSolution = stepScope.createOrGetClonedSolution();
        }

        @Override
        public void phaseEnded(AbstractPhaseScope phaseScope) {
            Solution workingSolution = phaseScope.getWorkingSolution();
        }

        @Override
        public void solvingEnded(DefaultSolverScope solverScope) {
            Solution bestSolution = solverScope.getBestSolution();
        }
    }

    public static class MyClonerWithoutTypeArgument implements SolutionCloner {

        @Override
        public Solution cloneSolution(Solution original) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    public static class DummyCalculator implements EasyScoreCalculator {

        @Override
        public Score calculateScore(Solution solution) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    public static class MyMoveFactory implements MoveListFactory {

        @Override
        public List<Solution> createMoveList(Solution solution) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    public static class MyInitializer implements CustomPhaseCommand {

        @Override
        public void applyCustomProperties(Map<String, String> customPropertyMap) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void changeWorkingSolution(ScoreDirector scoreDirector) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Test
    public void getBestSolution_should_be_assignable_to_Solution() {
        SolverFactory solverFactory = SolverFactory.createEmpty();
        initFactory(solverFactory);
        Solver solver = solverFactory.buildSolver();
        Solution solution = solver.getBestSolution();
    }

    private void initFactory(SolverFactory<?> solverFactory) {
        solverFactory.getSolverConfig().setSolutionClass(DummySolution.class);
        solverFactory.getSolverConfig().setEntityClassList(Arrays.asList(DummyEntity.class));
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setEasyScoreCalculatorClass(DummyCalculator.class);
        solverFactory.getSolverConfig().setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
    }

    @PlanningSolution
    public static class DummySolution implements Solution {

        @Override
        public Score getScore() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setScore(Score score) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Collection getProblemFacts() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @PlanningEntityProperty
        public Collection getEntities() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    @PlanningEntity
    public static class DummyEntity {

        @PlanningVariable(valueRangeProviderRefs = "x")
        int variable;

        @ValueRangeProvider(id = "x")
        public Collection getValues() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
