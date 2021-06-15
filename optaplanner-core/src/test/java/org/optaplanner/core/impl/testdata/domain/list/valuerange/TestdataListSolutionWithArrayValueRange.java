/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.testdata.domain.list.valuerange;

import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningSolution
public class TestdataListSolutionWithArrayValueRange {

    public static SolutionDescriptor<TestdataListSolutionWithArrayValueRange> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(
                TestdataListSolutionWithArrayValueRange.class,
                TestdataListEntityWithArrayValueRange.class);
    }

    private TestdataValue[] valueArray;
    private TestdataListEntityWithArrayValueRange entity;
    private SimpleScore score;

    @ValueRangeProvider(id = "arrayValueRange")
    @ProblemFactCollectionProperty
    public TestdataValue[] getValueArray() {
        return valueArray;
    }

    public void setValueArray(TestdataValue[] valueArray) {
        this.valueArray = valueArray;
    }

    @PlanningEntityProperty
    public TestdataListEntityWithArrayValueRange getEntity() {
        return entity;
    }

    public void setEntity(TestdataListEntityWithArrayValueRange entity) {
        this.entity = entity;
    }

    @PlanningScore
    public SimpleScore getScore() {
        return score;
    }

    public void setScore(SimpleScore score) {
        this.score = score;
    }
}
