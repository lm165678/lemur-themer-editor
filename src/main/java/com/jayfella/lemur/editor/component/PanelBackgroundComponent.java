package com.jayfella.lemur.editor.component;

import com.jayfella.devkit.props.component.ColorRGBAComponent;
import com.jayfella.devkit.props.component.JmeComponent;
import com.jayfella.devkit.props.component.Vector2fComponent;
import com.jayfella.lemur.util.BackgroundComponents;
import com.jayfella.lemur.util.BackgroundUtils;
import com.jayfella.lemur.util.TextureUtils;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.texture.Texture;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.TbtQuadBackgroundComponent;
import com.simsilica.lemur.core.GuiComponent;

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

public class PanelBackgroundComponent extends JmeComponent {

    private Container content;
    private List<JmeComponent> components = new ArrayList<>();

    public PanelBackgroundComponent() {
        this(null, null, null);
    }

    public PanelBackgroundComponent(Object parent, Field field) {
        super(parent, field);
        create();
    }

    public PanelBackgroundComponent(Object parent, Method getter, Method setter) {
        super(parent, getter, setter);
        create();
    }

    @SuppressWarnings("unchecked")
    private void create() {

        // this gets rebuilt if the user chooses a different background type.
        components.clear();

        if (content != null) {
            content.clearChildren();
        }
        else {
            content = new Container();
            content.setInsets(INSETS);
        }

        GuiComponent guiComponent = (GuiComponent) getReflectedItem().getValue();

        // let the user choose which type of backgroundComponent they want.
        Label label = content.addChild(new Label("Type"), 0, 0);
        label.setTextHAlignment(HAlignment.Right);
        label.setInsets(INSETS);
        Container backgroundTypeContainer = content.addChild(new Container(), 0, 1);

        ColorRGBA oldColor = BackgroundUtils.getBackgroundColor(guiComponent);

        Button colorOnlyBgButton = backgroundTypeContainer.addChild(new Button("Color Only"), 0, 0);
        colorOnlyBgButton.addClickCommands(source -> {
            if (!(guiComponent instanceof QuadBackgroundComponent)) {
                setValue(new QuadBackgroundComponent(oldColor));
                create();
            }
        });

        Button colorAndImageBgButton = backgroundTypeContainer.addChild(new Button("Color and Image"), 0, 1);
        colorAndImageBgButton.addClickCommands(source -> {
            if (!(guiComponent instanceof TbtQuadBackgroundComponent)) {
                setValue(BackgroundComponents.gradient(oldColor));
                create();
            }
        });

        if (guiComponent instanceof QuadBackgroundComponent) {

            try {

                Method getter = QuadBackgroundComponent.class.getDeclaredMethod("getColor");
                Method setter = QuadBackgroundComponent.class.getDeclaredMethod("setColor", ColorRGBA.class);

                label = content.addChild(new Label("Color"), 1, 0);
                label.setTextHAlignment(HAlignment.Right);
                label.setInsets(INSETS);

                ColorRGBAComponent bgColorComponent = new ColorRGBAComponent(guiComponent, getter, setter);
                content.addChild(bgColorComponent.getPanel(), 1, 1);
                components.add(bgColorComponent);

                // margin
                getter = QuadBackgroundComponent.class.getDeclaredMethod("getMargin");
                setter = QuadBackgroundComponent.class.getDeclaredMethod("setMargin", Vector2f.class);

                label = content.addChild(new Label("Margin"), 2, 0);
                Vector2fComponent marginComponent = new Vector2fComponent(guiComponent, getter, setter);
                content.addChild(marginComponent.getPanel(), 2, 1);
                components.add(marginComponent);

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

        }
        else if (guiComponent instanceof TbtQuadBackgroundComponent) {

            TbtQuadBackgroundComponent backgroundComponent = (TbtQuadBackgroundComponent) guiComponent;

            try {
                Method getter = TbtQuadBackgroundComponent.class.getDeclaredMethod("getColor");
                Method setter = TbtQuadBackgroundComponent.class.getDeclaredMethod("setColor", ColorRGBA.class);

                label = content.addChild(new Label("Color"), 1, 0);
                label.setTextHAlignment(HAlignment.Right);
                label.setInsets(INSETS);

                ColorRGBAComponent bgColorComponent = new ColorRGBAComponent(guiComponent, getter, setter);
                content.addChild(bgColorComponent.getPanel(), 1, 1);
                components.add(bgColorComponent);

                label = content.addChild(new Label("Image"), 2, 0);
                label.setTextHAlignment(HAlignment.Right);
                label.setInsets(INSETS);

                Button browseImageButton = content.addChild(new Button("Select Background Image..."), 2, 1);
                browseImageButton.setTextVAlignment(VAlignment.Center);
                browseImageButton.addClickCommands(source -> {

                    JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                    jfc.setDialogTitle("Select a Background Image...");
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

                            // PanelBackground panelBackground = (PanelBackground) getReflectedProperty().getValue();
                            // panelBackground.setBase64Image(imageString);

                            Texture texture = new TextureUtils().fromBase64(imageString);

                            backgroundComponent.setTexture(texture);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });

                // margin
                getter = TbtQuadBackgroundComponent.class.getDeclaredMethod("getMargin");
                setter = TbtQuadBackgroundComponent.class.getDeclaredMethod("setMargin", Vector2f.class);

                label = content.addChild(new Label("Margin"), 3, 0);
                Vector2fComponent marginComponent = new Vector2fComponent(guiComponent, getter, setter);
                content.addChild(marginComponent.getPanel(), 3, 1);
                components.add(marginComponent);


            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

        }

//
//        this.content = new RollupPanel("", contentContainer, null);
//        this.content.setOpen(false);
//
//        // background Image
//        Container bgImageContainer = contentContainer.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Last, FillMode.Last)), 0, 0);
//        Label bgImageLabel = bgImageContainer.addChild(new Label("Background Image"), 0, 0);
//        bgImageLabel.setTextVAlignment(VAlignment.Center);
//        bgImageLabel.setInsets(new Insets3f(0.0F, 2.0F, 0.0F, 5.0F));
//        Button browseImageButton = bgImageContainer.addChild(new Button("Browse..."), 0, 1);
//        Button removeImageButton = bgImageContainer.addChild(new Button("Remove"), 0, 2);
//        bgImageContainer.addChild(new Container(), 0, 3);
//
//        browseImageButton.addClickCommands(source -> {
//
//            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
//            jfc.setDialogTitle("Select a Background Image...");
//            jfc.setMultiSelectionEnabled(false);
//            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
//            jfc.setAcceptAllFileFilterUsed(false);
//            FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
//            jfc.addChoosableFileFilter(filter);
//            int returnValue = jfc.showOpenDialog(null);
//
//            if (returnValue == JFileChooser.APPROVE_OPTION) {
//
//                File file = jfc.getSelectedFile();
//
//                try {
//                    byte[] imageData = Files.readAllBytes(file.toPath());
//                    byte[] stringData = Base64.getEncoder().encode(imageData);
//
//                    String imageString = new String(stringData);
//
//                    PanelBackground panelBackground = (PanelBackground) getReflectedProperty().getValue();
//                    panelBackground.setBase64Image(imageString);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        });
//
//        removeImageButton.addClickCommands(source -> {
//            PanelBackground panelBackground = (PanelBackground) getReflectedProperty().getValue();
//            panelBackground.setBase64Image("");
//        });
//
//
//        try {
//
//            // color
//            Method get = PanelBackground.class.getMethod("getColor");
//            Method set = PanelBackground.class.getMethod("setColor", ColorRGBA.class);
//
//            ColorRGBAComponent bgColorComponent = new ColorRGBAComponent(getReflectedProperty().getValue(), get, set);
//            bgColorComponent.setPropertyName("Color");
//            contentContainer.addChild(bgColorComponent.getPanel());
//            components.add(bgColorComponent);
//
//            // insetTop
//            get = PanelBackground.class.getMethod("getInsetTop");
//            set = PanelBackground.class.getMethod("setInsetTop", int.class);
//
//            IntComponent insetTopComponent = new IntComponent(getReflectedProperty().getValue(), get, set);
//            insetTopComponent.setPropertyName("Inset Top");
//            contentContainer.addChild(insetTopComponent.getPanel());
//            components.add(insetTopComponent);
//
//            // insetLeft
//            get = PanelBackground.class.getMethod("getInsetLeft");
//            set = PanelBackground.class.getMethod("setInsetLeft", int.class);
//
//            IntComponent insetLeftComponent = new IntComponent(getReflectedProperty().getValue(), get, set);
//            insetLeftComponent.setPropertyName("Inset Left");
//            contentContainer.addChild(insetLeftComponent.getPanel());
//            components.add(insetLeftComponent);
//
//            // insetBottom
//            get = PanelBackground.class.getMethod("getInsetBottom");
//            set = PanelBackground.class.getMethod("setInsetBottom", int.class);
//
//            IntComponent insetBottomComponent = new IntComponent(getReflectedProperty().getValue(), get, set);
//            insetBottomComponent.setPropertyName("Inset Bottom");
//            contentContainer.addChild(insetBottomComponent.getPanel());
//            components.add(insetBottomComponent);
//
//            // insetRight
//            get = PanelBackground.class.getMethod("getInsetRight");
//            set = PanelBackground.class.getMethod("setInsetRight", int.class);
//
//            IntComponent insetRightComponent = new IntComponent(getReflectedProperty().getValue(), get, set);
//            insetRightComponent.setPropertyName("Inset Right");
//            contentContainer.addChild(insetRightComponent.getPanel());
//            components.add(insetRightComponent);
//
//            // zOffset
//            get = PanelBackground.class.getMethod("getzOffset");
//            set = PanelBackground.class.getMethod("setzOffset", float.class);
//
//            FloatComponent zOffsetComponent = new FloatComponent(getReflectedProperty().getValue(), get, set);
//            zOffsetComponent.setPropertyName("Z-Offset");
//            contentContainer.addChild(zOffsetComponent.getPanel());
//            components.add(zOffsetComponent);
//
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }


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
