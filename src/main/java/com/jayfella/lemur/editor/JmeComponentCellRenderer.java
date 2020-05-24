package com.jayfella.lemur.editor;

import com.jayfella.devkit.props.component.JmeComponent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.list.CellRenderer;

public class JmeComponentCellRenderer implements CellRenderer<JmeComponent> {

    @Override
    public Panel getView(JmeComponent value, boolean selected, Panel existing) {

        JmeComponentCell cell = existing != null
                ? (JmeComponentCell) existing
                : new JmeComponentCell();

        cell.setName(value.getPropertyName());
        cell.setPanel(value.getPanel());

        return cell;
    }

    private static class JmeComponentCell extends Container {

        private static final Insets3f nameInsets = new Insets3f(2,2,2,2);

        private Label label;
        private Panel panel;

        public JmeComponentCell() {
            super();

            setBackground(new QuadBackgroundComponent(ColorRGBA.BlackNoAlpha.clone()));

            label = addChild(new Label(""), 0, 0);

            label.setInsets(nameInsets);

            label.setTextVAlignment(VAlignment.Center);
            label.setTextHAlignment(HAlignment.Right);

            panel = addChild(new Panel(), 0, 1);
        }

        public void setName(String name) {
            label.setText(name);

            float minSize = 150;
            label.setPreferredSize( new Vector3f( minSize, label.getPreferredSize().y, 1 ) );
        }

        public void setPanel(Panel panel) {
            if (panel != null) {
                panel.removeFromParent();
            }

            Vector3f prefSize = panel.getPreferredSize().clone();

            addChild(panel, 0, 1);
            panel.setPreferredSize(prefSize);
        }

    }

}
