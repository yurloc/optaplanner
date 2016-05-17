/*
 * Copyright 2016 JBoss by Red Hat.
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
package org.optaplanner.examples.dinnerparty.app;

import java.io.File;

import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.dinnerparty.domain.DinnerParty;
import org.optaplanner.examples.dinnerparty.persistence.DinnerPartyDao;

public class Drools1179 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DinnerPartyDao dao = new DinnerPartyDao();
        DinnerParty solution = dao.readSolution(new File(dao.getDataDir(), "unsolved/wedding01.xml"));
        SolverFactory<DinnerParty> solverFactory = SolverFactory.createFromXmlResource(DinnerPartyApp.SOLVER_CONFIG);
        solverFactory.getSolverConfig().setEnvironmentMode(EnvironmentMode.NON_INTRUSIVE_FULL_ASSERT);
        solverFactory.buildSolver().solve(solution);
    }

}
