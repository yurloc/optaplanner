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

        SeatDesignation seatDesignation_138 = new SeatDesignation();
        SeatDesignation seatDesignation_139 = new SeatDesignation();
        Guest guest_282 = new Guest();
        Guest guest_283 = new Guest();
        Job job_590 = new Job();
        Seat seat_752 = new Seat();
        Seat seat_753 = new Seat();
        //Julian
        seatDesignation_138.setGuest(guest_282);
        //Taylor
        seatDesignation_139.setGuest(guest_283);
        //Julian
        guest_282.setJob(job_590);
        //Taylor
        guest_283.setJob(job_590);
        //Republican(Socialite)
        job_590.setJobType(JobType.SOCIALITE);
        //11.10
        seat_752.setRightSeat(seat_753);
        //11.11

        //operation I #138
        kieSession.insert(seatDesignation_138);
        //operation I #139
        kieSession.insert(seatDesignation_139);

        //operation U #11389
        seatDesignation_138.setSeat(seat_752);
        kieSession.update(kieSession.getFactHandle(seatDesignation_138), seatDesignation_138, "seat");
        //operation F #11393
        kieSession.fireAllRules();
        //operation U #11398
        seatDesignation_139.setSeat(seat_753);
        kieSession.update(kieSession.getFactHandle(seatDesignation_139), seatDesignation_139, "seat");
        //operation F #11399
        kieSession.fireAllRules();
        // Assert the corrupted score to make sure the bug is reproducible.
        assertEquals(0, scoreHolder.extractScore(0).getScore());

        // Create a fresh kieSession.
        kieSession = kieContainer.newKieSession();
        scoreHolder = new SimpleScoreDefinition().buildScoreHolder(true);
        kieSession.setGlobal("scoreHolder", scoreHolder);

        // Insert everything into a fresh session to see the uncorrupted score.
        //operation I #138
        kieSession.insert(seatDesignation_138);
        //operation I #139
        kieSession.insert(seatDesignation_139);
        kieSession.fireAllRules();
        assertEquals(-100, scoreHolder.extractScore(0).getScore());
    }
}
