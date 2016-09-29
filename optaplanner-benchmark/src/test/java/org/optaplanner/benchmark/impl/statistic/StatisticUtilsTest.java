/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.benchmark.impl.statistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class StatisticUtilsTest {

    private static final double DELTA = 0.001;

    @Test
    public void singleDetermineStandardDeviationDoubles() throws Exception {
        List<SubSingleBenchmarkResult> subSingleBenchmarkResultList = Arrays.asList(createSubSingleBenchmarkResult(SimpleScore.valueOfInitialized(0), 0));
        assertArrayEquals(new double[]{0d}, StatisticUtils.determineStandardDeviationDoubles(subSingleBenchmarkResultList, SimpleScore.valueOfInitialized(0), subSingleBenchmarkResultList.size()), DELTA);
    }

    @Test
    public void multipleDetermineStandardDeviationDoubles() throws Exception {
        List<SubSingleBenchmarkResult> subSingleBenchmarkResultList = new ArrayList<>(2);
        subSingleBenchmarkResultList.add(createSubSingleBenchmarkResult(SimpleScore.valueOfInitialized(-2), 0));
        subSingleBenchmarkResultList.add(createSubSingleBenchmarkResult(SimpleScore.valueOfInitialized(-4), 1));
        assertArrayEquals(new double[]{1d}, StatisticUtils.determineStandardDeviationDoubles(subSingleBenchmarkResultList, SimpleScore.valueOfInitialized(-3), subSingleBenchmarkResultList.size()), DELTA);
    }

    @Test
    public void largeDetermineStandardDeviationDoubles() throws Exception {
        long[] subSingleBenchmarkScores = new long[]{-19289560268L, -19345935795L, -19715516752L, -19589259253L, -19390707618L, -19641410518L};
        List<SubSingleBenchmarkResult> subSingleBenchmarkResultList = new ArrayList<>(6);
        SimpleLongScore averageScore = SimpleLongScore.valueOfInitialized(0);
        for (int i = 0; i < subSingleBenchmarkScores.length; i++) {
            SimpleLongScore current = SimpleLongScore.valueOfInitialized(subSingleBenchmarkScores[i]);
            subSingleBenchmarkResultList.add(createSubSingleBenchmarkResult(current, i));
            averageScore = averageScore.add(current);
        }
        averageScore = averageScore.divide(subSingleBenchmarkScores.length);
        assertThat(StatisticUtils.determineStandardDeviationDoubles(subSingleBenchmarkResultList, averageScore, subSingleBenchmarkResultList.size()))
                .containsExactly(new double[]{160338212.294}, Assertions.offset(DELTA));
    }

    @Test
    public void getStandardDeviationString() throws Exception {
        assertThat(StatisticUtils.getStandardDeviationString(null)).isEqualTo(null);
        assertThat(StatisticUtils.getStandardDeviationString(new double[]{2.0})).isEqualTo("2.0");
        assertThat(StatisticUtils.getStandardDeviationString(new double[]{Math.sqrt(2.0)})).isEqualTo("1.41");
        assertThat(StatisticUtils.getStandardDeviationString(new double[]{160338212.294})).isEqualTo("1.6E8");
        assertThat(StatisticUtils.getStandardDeviationString(new double[]{2000000000.0})).isEqualTo("2.0E9");
        assertThat(StatisticUtils.getStandardDeviationString(new double[]{20000000000.0})).isEqualTo("2.0E10");
    }

    private SubSingleBenchmarkResult createSubSingleBenchmarkResult(Score score, int index) {
        SubSingleBenchmarkResult subSingleBenchmarkResult = spy(new SubSingleBenchmarkResult(null, index));
        when(subSingleBenchmarkResult.getAverageScore()).thenReturn(score);
        when(subSingleBenchmarkResult.hasAllSuccess()).thenReturn(true);
        return subSingleBenchmarkResult;
    }

}
