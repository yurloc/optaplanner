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

package org.optaplanner.examples.common.swingui.components;

import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Display the user-friendly {@link Labeled#getLabel()} instead of the developer-friendly {@link Object#toString()}.
 * @param <E> the type of elements in the combo box
 */
public class LabeledComboBoxRenderer<E extends Labeled> implements ListCellRenderer<E> {

    public static <E extends Labeled> void applyToComboBox(JComboBox<E> comboBox) {
        comboBox.setRenderer(new LabeledComboBoxRenderer<>(comboBox.getRenderer()));
    }

    private final ListCellRenderer<? super E> originalListCellRenderer;

    public LabeledComboBoxRenderer(ListCellRenderer<? super E> originalListCellRenderer) {
        this.originalListCellRenderer = originalListCellRenderer;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends E> list, E value, int index,
            boolean isSelected, boolean cellHasFocus) {
        Component component = originalListCellRenderer
                .getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        ((JLabel) component).setText(value == null ? "" : value.getLabel());
        return component;
    }

}
