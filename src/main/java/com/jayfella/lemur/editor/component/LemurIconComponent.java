package com.jayfella.lemur.editor.component;

import com.jayfella.devkit.props.component.ColorRGBAComponent;
import com.jayfella.devkit.props.component.FloatComponent;
import com.jayfella.devkit.props.component.JmeComponent;
import com.jayfella.devkit.props.component.Vector2fComponent;
import com.jayfella.lemur.util.TextureUtils;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.texture.Texture;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.IconComponent;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class LemurIconComponent extends JmeComponent {

    private Container content;
    private List<JmeComponent> components = new ArrayList<>();

    public LemurIconComponent() {
        this(null, null, null);
    }

    public LemurIconComponent(Object parent, Field field) {
        super(parent, field);
        create();
    }

    public LemurIconComponent(Object parent, Method getter, Method setter) {
        super(parent, getter, setter);
        create();

    }

    public void create() {

        content = new Container();

        content.setInsets(new Insets3f(5.0F, 5.0F, 5.0F, 5.0F));

        // Icon Image
        // Container iconImageContainer = contentContainer.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Last, FillMode.Last)), 0, 0);

        Button browseImageButton = content.addChild(new Button("Select a Icon Image..."), 0);

        browseImageButton.addClickCommands(source -> {

            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            jfc.setDialogTitle("Select a Icon Image...");
            jfc.setMultiSelectionEnabled(false);
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
            jfc.addChoosableFileFilter(filter);
            int returnValue = jfc.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {

                File file = jfc.getSelectedFile();

                try {
                    byte[] imageData = Files.readAllBytes(file.toPath());
                    byte[] stringData = Base64.getEncoder().encode(imageData);

                    String imageString = new String(stringData);
                    Texture texture = new TextureUtils().fromBase64(imageString);

                    IconComponent iconComponent = (IconComponent) getReflectedItem().getValue();

                    iconComponent.setImageTexture(texture);

                    // Icon icon = (Icon) getReflectedProperty().getValue();
                    // icon.setBase64Image(imageString);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        try {

            // color
            Method get = IconComponent.class.getMethod("getColor");
            Method set = IconComponent.class.getMethod("setColor", ColorRGBA.class);

            ColorRGBAComponent iconColorComponent = new ColorRGBAComponent(getReflectedItem().getValue(), get, set);
            iconColorComponent.setPropertyName("Color");
            content.addChild(iconColorComponent.getPanel());
            components.add(iconColorComponent);

            // margin
            get = IconComponent.class.getMethod("getMargin");
            set = IconComponent.class.getMethod("setMargin", Vector2f.class);

            Vector2fComponent marginComponent = new Vector2fComponent(getReflectedItem().getValue(), get, set);
            marginComponent.setPropertyName("Margin");
            content.addChild(marginComponent.getPanel());
            components.add(marginComponent);

            // zOffset
            get = IconComponent.class.getMethod("getZOffset");
            set = IconComponent.class.getMethod("setZOffset", float.class);

            FloatComponent zOffsetComponent = new FloatComponent(getReflectedItem().getValue(), get, set);
            zOffsetComponent.setPropertyName("Z-Offset");
            content.addChild(zOffsetComponent.getPanel());
            components.add(zOffsetComponent);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Panel getPanel() {
        return content;
    }

    @Override
    public void update(float tpf) {
        components.forEach(component -> component.update(tpf));
    }

}
