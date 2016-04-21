/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.examples.nurserostering.app;

import java.io.File;

import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.persistence.NurseRosteringDao;

public class Planner488 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        NurseRosteringDao dao = new NurseRosteringDao();
        NurseRoster solution = (NurseRoster) dao.readSolution(new File(dao.getDataDir(), "unsolved/long01-2empl.xml"));

        solution.getEmployeeList().stream().forEach(e -> {
            e.getDayOffRequestMap().clear();
            e.getDayOnRequestMap().clear();
            e.getShiftOffRequestMap().clear();
            e.getShiftOnRequestMap().clear();
        });

        SolverFactory<NurseRoster> solverFactory = SolverFactory.createFromXmlResource(NurseRosteringApp.SOLVER_CONFIG);
        solverFactory.buildSolver().solve(solution);
    }
}
