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

        Match match_17 = new Match();
        Match match_73 = new Match();
        Day day_90 = new Day();
        Day day_99 = new Day();
        Day day_100 = new Day();
        Team team_108 = new Team();
        Team team_109 = new Team();
        Team team_110 = new Team();
        Team team_111 = new Team();
        Team team_112 = new Team();
        Team team_113 = new Team();
        Team team_114 = new Team();
        Team team_115 = new Team();
        Team team_116 = new Team();
        Team team_117 = new Team();
        //NYM+MIL
        match_17.setDay(day_100);
        match_17.setHomeTeam(team_109);
        //STL+NYM
        match_73.setDay(day_99);
        match_73.setHomeTeam(team_116);
        //Day-0
        //Day-9
        day_99.setIndex(9);
        //Day-10
        day_100.setIndex(10);
        //ATL
        //NYM
        //PHI
        //MON
        //FLA
        //PIT
        //CIN
        //CHI
        //STL
        HashMap<Team, Integer> team_116_distanceToTeamMap = new HashMap<>();
        //null => 467
        team_116_distanceToTeamMap.put(team_108, 467);
        //null => 311
        team_116_distanceToTeamMap.put(team_114, 311);
        //null => 328
        team_116_distanceToTeamMap.put(team_117, 328);
        //null => 808
        team_116_distanceToTeamMap.put(team_110, 808);
        //null => 260
        team_116_distanceToTeamMap.put(team_115, 260);
        //null => 557
        team_116_distanceToTeamMap.put(team_113, 557);
        //null => 0
        team_116_distanceToTeamMap.put(team_116, 0);
        //null => 871
        team_116_distanceToTeamMap.put(team_109, 871);
        //null => 878
        team_116_distanceToTeamMap.put(team_111, 878);
        //null => 1060
        team_116_distanceToTeamMap.put(team_112, 1060);
        team_116.setDistanceToTeamMap(team_116_distanceToTeamMap);
        //MIL

        //operation I #17
        kieSession.insert(match_17);
        //operation I #73
        kieSession.insert(match_73);

        //operation F #120
        kieSession.fireAllRules();
        //operation U #121
        match_73.setDay(day_90);
        kieSession.update(kieSession.getFactHandle(match_73), match_73, "day");
        //operation F #124
        kieSession.fireAllRules();
        // This is the corrupted score, just to make sure the bug is reproducible
        assertEquals("0hard/-871soft", scoreHolder.extractScore(0).toString());
        kieSession = kieContainer.newKieSession();
        scoreHolder = new HardSoftScoreDefinition().buildScoreHolder(true);
        kieSession.setGlobal("scoreHolder", scoreHolder);

        // Insert everything into a fresh session to see the uncorrupted score
        //operation I #17
        kieSession.insert(match_17);
        //operation I #73
        kieSession.insert(match_73);
        kieSession.fireAllRules();
        assertEquals("0hard/0soft", scoreHolder.extractScore(0).toString());
    }
}
