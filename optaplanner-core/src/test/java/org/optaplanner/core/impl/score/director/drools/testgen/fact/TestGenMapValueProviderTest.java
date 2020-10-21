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

package org.optaplanner.core.impl.score.director.drools.testgen.fact;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;

class TestGenMapValueProviderTest {

    private Map<TestdataEntity, Integer> distanceMap = new HashMap<>();

    @Test
    void distanceMap() throws NoSuchFieldException {
        HashMap<Object, TestGenFact> existingInstances = new HashMap<>();
        TestdataEntity entity = new TestdataEntity();
        TestGenValueFact f1 = new TestGenValueFact(0, entity);
        existingInstances.put(entity, f1);

        distanceMap.put(entity, 321);

        Field field = TestGenMapValueProviderTest.class.getDeclaredField("distanceMap");
        Type[] typeArgs = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
        TestGenMapValueProvider mapValueProvider =
                new TestGenMapValueProvider(this.distanceMap, "distanceMap", typeArgs, existingInstances);

        StringBuilder sb = new StringBuilder();
        mapValueProvider.printSetup(sb);
        System.out.println(sb);
        assertThat(sb).contains("distanceMap.put(testdataEntity_0, 321);");
    }

}
