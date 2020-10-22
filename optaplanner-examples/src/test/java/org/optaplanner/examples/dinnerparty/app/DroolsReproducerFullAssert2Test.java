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
import org.kie.api.runtime.rule.FactHandle;
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
import org.optaplanner.examples.dinnerparty.domain.Table;

public class DroolsReproducerFullAssert2Test {

    @Test
    public void test() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        kfs.write(kieServices.getResources()
                .newClassPathResource("org/optaplanner/examples/dinnerparty/solver/dinnerPartyConstraints2.drl"));
        kieServices.newKieBuilder(kfs).buildAll(ExecutableModelProject.class);
        KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        KieSession kieSession = kieContainer.newKieSession();
        ((RuleEventManager) kieSession).addEventListener(new OptaPlannerRuleEventListener());

        AbstractScoreHolder<SimpleScore> scoreHolder = new SimpleScoreDefinition().buildScoreHolder(true);
        kieSession.setGlobal("scoreHolder", scoreHolder);

        SeatDesignation seatDesignation = new SeatDesignation();
        Guest guest = new Guest();
        Job job = new Job();
        JobType jobType = JobType.SOCIALITE;
        Seat seat = new Seat();
        Table table = new Table();
        seatDesignation.setGuest(guest);
        guest.setJob(job);
        job.setJobType(JobType.SOCIALITE);
        seat.setTable(table);

        kieSession.insert(seatDesignation);
        kieSession.insert(jobType);
        kieSession.insert(table);

        // seatDesignation is not assigned a seat & table => atLeastOneJobTypePerTableScoreGuider fires.
        kieSession.fireAllRules();
        assertEquals(-100, scoreHolder.extractScore(0).getScore());

        // Put seatDesignation to the table.
        // atLeastOneJobTypePerTableScoreGuider should unmatch and the score should raise to 0.
        seatDesignation.setSeat(seat);
        kieSession.update(kieSession.getFactHandle(seatDesignation), seatDesignation, "seat");

        kieSession.fireAllRules();
        // This is the corrupted score (expected score is 0).
        assertEquals(-100, scoreHolder.extractScore(0).getScore());

        // Create a fresh kieSession.
        kieSession = kieContainer.newKieSession();
        scoreHolder = new SimpleScoreDefinition().buildScoreHolder(true);
        kieSession.setGlobal("scoreHolder", scoreHolder);

        // Insert everything into a fresh session to see the uncorrupted score.
        kieSession.insert(seatDesignation);
        kieSession.insert(jobType);
        kieSession.insert(table);
        kieSession.fireAllRules();
        assertEquals(0, scoreHolder.extractScore(0).getScore());
    }
}
