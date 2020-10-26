package org.optaplanner.examples.travelingtournament.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.drools.modelcompiler.ExecutableModelProject;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.event.rule.RuleEventManager;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.core.impl.score.director.drools.OptaPlannerRuleEventListener;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;
import org.optaplanner.examples.travelingtournament.domain.Day;
import org.optaplanner.examples.travelingtournament.domain.Match;
import org.optaplanner.examples.travelingtournament.domain.Team;

public class DroolsReproducerTest {

    @Test
    public void test() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        kfs.write(kieServices.getResources().newClassPathResource(
                "org/optaplanner/examples/travelingtournament/solver/travelingTournamentConstraints.drl"));
        kfs.writeKModuleXML(kieServices.newKieModuleModel().setConfigurationProperty(
                PropertySpecificOption.PROPERTY_NAME,
                PropertySpecificOption.ALLOWED.toString()).toXML());
        kieServices.newKieBuilder(kfs).buildAll(ExecutableModelProject.class);
        // Use this instead of the executable model to see the expected (correct) behavior (the reproducer will fail).
        //kieServices.newKieBuilder(kfs).buildAll();
        KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        KieSession kieSession = kieContainer.newKieSession();
        ((RuleEventManager) kieSession).addEventListener(new OptaPlannerRuleEventListener());

        AbstractScoreHolder<?> scoreHolder = new HardSoftScoreDefinition().buildScoreHolder(true);
        kieSession.setGlobal("scoreHolder", scoreHolder);

        // Set up the problem.
        Match matchAB = new Match();
        Match matchBC = new Match();
        Day day0 = new Day();
        Day day6 = new Day();
        Day day7 = new Day();
        Team teamA = new Team();
        Team teamB = new Team();

        day6.setIndex(6);
        day7.setIndex(7);

        matchAB.setDay(day6);
        matchAB.setHomeTeam(teamA);
        matchBC.setDay(day7);
        matchBC.setHomeTeam(teamB);

        HashMap<Team, Integer> distanceToTeamMap = new HashMap<>();
        distanceToTeamMap.put(teamB, 871);
        teamA.setDistanceToTeamMap(distanceToTeamMap);

        // Insert and fire.
        kieSession.insert(matchAB);
        kieSession.insert(matchBC);
        kieSession.fireAllRules();
        // This score is correct.
        assertEquals("0hard/-871soft", scoreHolder.extractScore(0).toString());

        matchAB.setDay(day0);
        kieSession.update(kieSession.getFactHandle(matchAB), matchAB, "day");
        kieSession.fireAllRules();
        // Assert the corrupted score to make sure the bug is reproducible.
        assertEquals("0hard/-871soft", scoreHolder.extractScore(0).toString());

        // Create a fresh kieSession.
        kieSession = kieContainer.newKieSession();
        scoreHolder = new HardSoftScoreDefinition().buildScoreHolder(true);
        kieSession.setGlobal("scoreHolder", scoreHolder);

        // Insert everything into a fresh session to see the uncorrupted score.
        kieSession.insert(matchAB);
        kieSession.insert(matchBC);
        kieSession.fireAllRules();
        assertEquals("0hard/0soft", scoreHolder.extractScore(0).toString());
    }
}
