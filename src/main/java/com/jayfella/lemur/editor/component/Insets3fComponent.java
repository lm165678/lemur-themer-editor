package com.jayfella.lemur.editor.component;

import com.jayfella.devkit.props.NumberFilters;
import com.jayfella.devkit.props.component.JmeComponent;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.text.DocumentModel;
import com.simsilica.lemur.text.DocumentModelFilter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Insets3fComponent extends JmeComponent {

    private Container content;

    private TextField topTextField;
    private TextField leftTextField;
    private TextField bottomTextField;
    private TextField rightTextField;

    private VersionedReference<DocumentModel> rVerRef;
    private VersionedReference<DocumentModel> gVerRef;
    private VersionedReference<DocumentModel> bVerRef;
    private VersionedReference<DocumentModel> aVerRef;

    public Insets3fComponent() {
        this(null, null, null);
    }

    public Insets3fComponent(Object parent, Field field) {
        super(parent, field);
        create();
    }

    public Insets3fComponent(Object parent, Method getter, Method setter) {
        super(parent, getter, setter);
        create();
    }

    private void create() {

        content = new Container();
        content.setInsets(new Insets3f(5.0F, 5.0F, 5.0F, 5.0F));

//        this.content = new RollupPanel("", contentContainer, null);
//        this.content.setOpen(false);
        float minWidth = 50.0F;

        DocumentModelFilter topFilter = new DocumentModelFilter();
        topFilter.setInputTransform(NumberFilters.floatFilter());

        DocumentModelFilter leftFilter = new DocumentModelFilter();
        leftFilter.setInputTransform(NumberFilters.floatFilter());

        DocumentModelFilter bottomFilter = new DocumentModelFilter();
        bottomFilter.setInputTransform(NumberFilters.floatFilter());

        DocumentModelFilter rightFilter = new DocumentModelFilter();
        rightFilter.setInputTransform(NumberFilters.floatFilter());

        // top
        Container topContainer = content.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Last, FillMode.Last)), 0, 0);
        Label topLabel = topContainer.addChild(new Label("Top"), 0, 0);
        topLabel.setTextVAlignment(VAlignment.Center);
        topLabel.setInsets(new Insets3f(0.0F, 2.0F, 0.0F, 5.0F));
        this.topTextField = topContainer.addChild(new TextField(topFilter), 0, 1);
        this.topTextField.setPreferredWidth(minWidth);

        // left
        Container leftContainer = content.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Last, FillMode.Last)), 0, 1);
        Label leftLabel = leftContainer.addChild(new Label("Left"), 0, 0);
        leftLabel.setTextVAlignment(VAlignment.Center);
        leftLabel.setInsets(new Insets3f(0.0F, 10.0F, 0.0F, 5.0F));
        this.leftTextField = leftContainer.addChild(new TextField(leftFilter), 0, 1);
        this.leftTextField.setPreferredWidth(minWidth);

        // bottom
        Container bottomContainer = content.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Last, FillMode.Last)), 0, 2);
        Label bottomLabel = bottomContainer.addChild(new Label("Bottom"), 0, 0);
        bottomLabel.setTextVAlignment(VAlignment.Center);
        bottomLabel.setInsets(new Insets3f(0.0F, 10.0F, 0.0F, 5.0F));
        this.bottomTextField = bottomContainer.addChild(new TextField(bottomFilter), 0, 1);
        this.bottomTextField.setPreferredWidth(minWidth);

        // right
        Container rightContainer = content.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Last, FillMode.Last)), 0, 3);
        Label rightLabel = rightContainer.addChild(new Label("Right"), 0, 0);
        rightLabel.setTextVAlignment(VAlignment.Center);
        rightLabel.setInsets(new Insets3f(0.0F, 10.0F, 0.0F, 5.0F));
        this.rightTextField = rightContainer.addChild(new TextField(rightFilter), 0, 1);
        this.rightTextField.setPreferredWidth(minWidth);

        if (this.getReflectedItem() != null) {

            // ColorRGBA colorRGBA = (ColorRGBA)this.getReflectedProperty().getValue();
            Insets3f insets3f = (Insets3f) this.getReflectedItem().getValue();

            if (insets3f != null) {
                this.topTextField.setText("" + insets3f.min.y);
                this.leftTextField.setText("" + insets3f.min.x);
                this.bottomTextField.setText("" + insets3f.max.y);
                this.rightTextField.setText("" + insets3f.max.x);
            } else {
                this.topTextField.setText("0");
                this.leftTextField.setText("0");
                this.bottomTextField.setText("0");
                this.rightTextField.setText("0");
            }
        } else {
            this.topTextField.setText("0");
            this.leftTextField.setText("0");
            this.bottomTextField.setText("0");
            this.rightTextField.setText("0");
        }

        this.rVerRef = this.topTextField.getDocumentModel().createReference();
        this.gVerRef = this.leftTextField.getDocumentModel().createReference();
        this.bVerRef = this.bottomTextField.getDocumentModel().createReference();
        this.aVerRef = this.rightTextField.getDocumentModel().createReference();
    }

    private Insets3f getInsets3f() {
        float t = Float.parseFloat(this.topTextField.getText());
        float l = Float.parseFloat(this.leftTextField.getText());
        float b = Float.parseFloat(this.bottomTextField.getText());
        float r = Float.parseFloat(this.rightTextField.getText());
        return new Insets3f(t, l, b, r);
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);
        Insets3f insets3f = (Insets3f) value;
        this.topTextField.setText("" + insets3f.min.y);
        this.leftTextField.setText("" + insets3f.min.x);
        this.bottomTextField.setText("" + insets3f.max.y);
        this.rightTextField.setText("" + insets3f.max.x);
    }

    @Override
    public Panel getPanel() {
        return content;
    }

    @Override
    public void update(float var1) {

        boolean rUpdate = this.rVerRef.update();
        boolean gUpdate = this.gVerRef.update();
        boolean bUpdate = this.bVerRef.update();
        boolean aUpdate = this.aVerRef.update();

        if (rUpdate) {
            this.topTextField.setText(NumberFilters.filterFloatValue(this.topTextField.getText()));
        }

        if (gUpdate) {
            this.leftTextField.setText(NumberFilters.filterFloatValue(this.leftTextField.getText()));
        }

        if (bUpdate) {
            this.bottomTextField.setText(NumberFilters.filterFloatValue(this.bottomTextField.getText()));
        }

        if (aUpdate) {
            this.rightTextField.setText(NumberFilters.filterFloatValue(this.rightTextField.getText()));
        }

        Insets3f oldValue;
        if ((rUpdate || gUpdate || bUpdate || aUpdate) && this.getPropertyChangedEvent() != null) {
            oldValue = this.getInsets3f();
            this.getPropertyChangedEvent().propertyChanged(oldValue);
        }

        if (this.getReflectedItem() != null) {
            if (this.isFocused(this.topTextField, this.leftTextField, this.bottomTextField, this.rightTextField)) {
                return;
            }

            oldValue = this.getInsets3f();
            Insets3f newValue = (Insets3f)this.getReflectedItem().getValue();
            if (!oldValue.equals(newValue)) {
                this.setValue(newValue);
            }
        }

    }

}
