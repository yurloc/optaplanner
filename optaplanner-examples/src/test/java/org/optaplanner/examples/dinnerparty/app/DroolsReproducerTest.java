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

import org.drools.modelcompiler.ExecutableModelProject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;

public class DroolsReproducerTest {

    @Test
    public void test() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        kfs.write(kieServices.getResources()
                .newClassPathResource("org/optaplanner/examples/dinnerparty/solver/dinnerPartyConstraints2.drl"));
        kieServices.newKieBuilder(kfs).buildAll(ExecutableModelProject.class);
        KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        KieSession kieSession = kieContainer.newKieSession();

        AbstractScoreHolder<?> scoreHolder = new SimpleScoreDefinition().buildScoreHolder(true);
        kieSession.setGlobal("scoreHolder", scoreHolder);

        SeatDesignation matthewSeatDesignation = new SeatDesignation();
        SeatDesignation rileySeatDesignation = new SeatDesignation();
        //Matthew
        matthewSeatDesignation.setGuest("matthew");
        matthewSeatDesignation.setSeat(20);
        //Riley - is now right of Matthew because 20 + 1 = 21 (r.isRightOf(m) == true).
        rileySeatDesignation.setGuest("riley");
        rileySeatDesignation.setSeat(21);

        // Insert Matthew and Riley seat designations.
        kieSession.insert(matthewSeatDesignation);
        kieSession.insert(rileySeatDesignation);

        kieSession.fireAllRules();
        // Expected score. leftHasHobbyInCommon is broken because Matthew and Riley sit next to each other
        // and they do not have a common hobby.
        Assertions.assertEquals("-100", scoreHolder.extractScore(0).toString());

        // Move Matthew away from Riley.
        matthewSeatDesignation.setSeat(999);
        kieSession.update(kieSession.getFactHandle(matthewSeatDesignation), matthewSeatDesignation, "seat");

        kieSession.fireAllRules();
        // This is the corrupted score (expected score is 0).
        // leftHasHobbyInCommon shouldn't fire because Riley(21) is no longer right of Matthew(999).
        Assertions.assertEquals("-100", scoreHolder.extractScore(0).toString());

        // Create a fresh kieSession.
        kieSession = kieContainer.newKieSession();
        scoreHolder = new SimpleScoreDefinition().buildScoreHolder(true);
        kieSession.setGlobal("scoreHolder", scoreHolder);

        // Insert everything into a fresh session to see the uncorrupted score.
        kieSession.insert(matthewSeatDesignation);
        kieSession.insert(rileySeatDesignation);
        kieSession.fireAllRules();
        Assertions.assertEquals("0", scoreHolder.extractScore(0).toString());
    }

    public static class SeatDesignation {
        private String guest;
        private int seat;

        public boolean isRightOf(SeatDesignation leftSeatDesignation) {
            if (seat == 0 || leftSeatDesignation.seat == 0) {
                return false;
            }
            return seat == leftSeatDesignation.seat + 1;
        }

        public String getGuest() {
            return guest;
        }

        public void setGuest(String guest) {
            this.guest = guest;
        }

        public int getSeat() {
            return seat;
        }

        public void setSeat(int seat) {
            this.seat = seat;
        }
    }

    public static class HobbyPractician {
        private String guest;
        private String hobby;

        public String getGuest() {
            return guest;
        }

        public void setGuest(String guest) {
            this.guest = guest;
        }

        public String getHobby() {
            return hobby;
        }

        public void setHobby(String hobby) {
            this.hobby = hobby;
        }
    }
}
