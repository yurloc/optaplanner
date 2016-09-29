/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.score.director.easy;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class EasyScoreDirectorTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constraintMatchTotalsUnsupported() {
        EasyScoreDirector<Object> director
                = new EasyScoreDirector<>(mockEasyScoreDirectorFactory(), true, null);
        assertThat(director.isConstraintMatchEnabled()).isFalse();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("not supported");
        director.getConstraintMatchTotals();
    }

    @SuppressWarnings("unchecked")
    private EasyScoreDirectorFactory<Object> mockEasyScoreDirectorFactory() {
        EasyScoreDirectorFactory<Object> factory = mock(EasyScoreDirectorFactory.class);
        when(factory.getSolutionDescriptor()).thenReturn(mock(SolutionDescriptor.class));
        return factory;
    }

}
