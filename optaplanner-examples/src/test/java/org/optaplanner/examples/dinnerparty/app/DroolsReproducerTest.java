/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.dinnerparty.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.drools.modelcompiler.ExecutableModelProject;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.event.rule.RuleEventManager;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.score.director.drools.OptaPlannerRuleEventListener;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;
import org.optaplanner.examples.dinnerparty.domain.Guest;
import org.optaplanner.examples.dinnerparty.domain.Job;
import org.optaplanner.examples.dinnerparty.domain.JobType;
import org.optaplanner.examples.dinnerparty.domain.Seat;
import org.optaplanner.examples.dinnerparty.domain.SeatDesignation;

public class DroolsReproducerTest {

    @Test
    public void test() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        kfs.write(kieServices.getResources()
                .newClassPathResource("org/optaplanner/examples/dinnerparty/solver/dinnerPartyConstraints.drl"));
        kfs.writeKModuleXML(kieServices.newKieModuleModel().setConfigurationProperty(
                PropertySpecificOption.PROPERTY_NAME,
                PropertySpecificOption.ALLOWED.toString()).toXML());
        kieServices.newKieBuilder(kfs).buildAll(ExecutableModelProject.class);
        KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        KieSession kieSession = kieContainer.newKieSession();
        ((RuleEventManager) kieSession).addEventListener(new OptaPlannerRuleEventListener());

        AbstractScoreHolder<SimpleScore> scoreHolder = new SimpleScoreDefinition().buildScoreHolder(true);
        kieSession.setGlobal("scoreHolder", scoreHolder);

        SeatDesignation julianSeatDesignation = new SeatDesignation();
        SeatDesignation taylorSeatDesignation = new SeatDesignation();
        Guest julian = new Guest();
        Guest taylor = new Guest();
        Job sharedJob = new Job();
        Seat seatL = new Seat();
        Seat seatR = new Seat();

        julianSeatDesignation.setGuest(julian);
        taylorSeatDesignation.setGuest(taylor);
        julian.setJob(sharedJob);
        taylor.setJob(sharedJob);
        sharedJob.setJobType(JobType.SOCIALITE);

        // Seats are next to each other, but SeatDesignations are not yet assigned to seats => rule does not fire.
        seatL.setRightSeat(seatR);
        julianSeatDesignation.setSeat(seatL);

        kieSession.insert(julianSeatDesignation);
        kieSession.insert(taylorSeatDesignation);

        kieSession.fireAllRules();
        assertEquals(0, scoreHolder.extractScore(0).getScore());

        // Assign Taylor's SD to the right seat. Julian and Taylor now sit next to each other
        // but there is no common hobby => the rule is expected to fire (but it doesn't).
        taylorSeatDesignation.setSeat(seatR);
        kieSession.update(kieSession.getFactHandle(taylorSeatDesignation), taylorSeatDesignation, "seat");
        kieSession.fireAllRules();
        // Assert the corrupted score to make sure the bug is reproducible.
        assertEquals(0, scoreHolder.extractScore(0).getScore());

        // But the rule fires when both facts are inserted into a fresh KieSession.

        // Create a fresh KieSession.
        kieSession = kieContainer.newKieSession();
        scoreHolder = new SimpleScoreDefinition().buildScoreHolder(true);
        kieSession.setGlobal("scoreHolder", scoreHolder);

        // Insert everything into a fresh session to see the uncorrupted score.
        kieSession.insert(julianSeatDesignation);
        kieSession.insert(taylorSeatDesignation);
        kieSession.fireAllRules();
        assertEquals(-100, scoreHolder.extractScore(0).getScore());
    }
}
