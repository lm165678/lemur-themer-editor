package com.jayfella.lemur.editor;

import com.jayfella.lemur.ThemedElement;
import com.jayfella.lemur.util.StringUtils;
import com.jme3.math.ColorRGBA;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.list.CellRenderer;

public class ThemedElementCellRenderer implements CellRenderer<ThemedElement> {

    @Override
    public Panel getView(ThemedElement value, boolean selected, Panel existing) {

//        Label element = existing != null
//                ? (Label) existing
//                : new Label("");

        ThemedElementContainer element = existing != null
                ? (ThemedElementContainer) existing
                : new ThemedElementContainer();

        element.setText(StringUtils.splitCamelCase(value.getClass().getSimpleName().replace("Theme", "")));

        return element;
    }

    /**
     * A simple class that allows us to easily re-use the existing panel.
     * We use a container so the whole row width is clickable instead of just the label.
     */
    private static class ThemedElementContainer extends Container {

        private final Label label;

        public ThemedElementContainer() {
            super();
            setBackground(new QuadBackgroundComponent(ColorRGBA.BlackNoAlpha.clone()));
            label = addChild(new Label(""));
        }

        public void setText(String text) {
            label.setText(text);
        }

    }



}
