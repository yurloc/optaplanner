/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.simplelong;

import org.junit.Test;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolderTest;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleLongScoreHolderTest extends AbstractScoreHolderTest {

    @Test
    public void addConstraintMatchWithConstraintMatch() {
        addConstraintMatch(true);
    }

    @Test
    public void addConstraintMatchWithoutConstraintMatch() {
        addConstraintMatch(false);
    }

    public void addConstraintMatch(boolean constraintMatchEnabled) {
        SimpleLongScoreHolder scoreHolder = new SimpleLongScoreHolder(constraintMatchEnabled);

        scoreHolder.addConstraintMatch(mockRuleContext("scoreRule1"), -1000L);

        RuleContext ruleContext2 = mockRuleContext("scoreRule2");
        scoreHolder.addConstraintMatch(ruleContext2, -200L);
        callUnMatch(ruleContext2);

        RuleContext ruleContext3 = mockRuleContext("scoreRule3");
        scoreHolder.addConstraintMatch(ruleContext3, -30L);
        scoreHolder.addConstraintMatch(ruleContext3, -3L); // Overwrite existing

        assertEquals(SimpleLongScore.valueOf(0, -1003L), scoreHolder.extractScore(0));
        assertEquals(SimpleLongScore.valueOf(-7, -1003L), scoreHolder.extractScore(-7));
        if (constraintMatchEnabled) {
            assertThat(scoreHolder.getConstraintMatchTotals().size()).isEqualTo(3);
        }
    }

}
