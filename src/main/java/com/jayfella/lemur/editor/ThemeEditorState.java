package com.jayfella.lemur.editor;

import com.jayfella.devkit.props.builder.ReflectedFieldComponentBuilder;
import com.jayfella.devkit.props.component.JmeComponent;
import com.jayfella.lemur.LemurThemer;
import com.jayfella.lemur.ThemedElement;
import com.jayfella.lemur.util.BackgroundUtils;
import com.jayfella.lemur.util.StringUtils;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.event.PopupState;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.IOException;
import java.util.*;

public class ThemeEditorState extends BaseAppState {

    private final LemurThemer themer;
    private Node guiNode;

    private Container themeConfigContainer;

    private Container themeConfigPropsContainer;
    private final List<JmeComponent> propComponents = new ArrayList<>();

    private Node demoNode;

    private ListBox<ThemedElement> themedElementListBox;
    private VersionedReference<Set<Integer>> themedElemRef;

    // private ListBox<JmeComponent> componentListBox;


    public ThemeEditorState(LemurThemer themer) {
        this.themer = themer;
    }

    @Override
    protected void initialize(Application app) {
        guiNode = ((SimpleApplication)app).getGuiNode();
        displayThemeSettings();
        buildDemoNode();
    }

    private void displayThemeSettings() {

        if (themeConfigContainer != null) {
            themeConfigContainer.removeFromParent();
        }

        if (themeConfigPropsContainer != null) {
            themeConfigPropsContainer.removeFromParent();
        }

        themeConfigContainer = new Container();

        Insets3f titleInsets = new Insets3f(5,5,5,10);
        Insets3f valueInsets = new Insets3f(5,5,5,5);

        // Load Theme
        Container loadThemeContainer = themeConfigContainer.addChild(new Container());
        // Label label = loadThemeContainer.addChild(new Label("Load Theme"), 0, 0);
        // label.setTextVAlignment(VAlignment.Center);
        // label.setTextHAlignment(HAlignment.Right);
        // label.setInsets(titleInsets);

        Button newThemeButton = loadThemeContainer.addChild(new Button("New Theme..."), 0, 0);
        newThemeButton.addClickCommands(source -> {

            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            jfc.setDialogTitle("Create a Theme File...");
            jfc.setMultiSelectionEnabled(false);
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Lemur Themes", "json");
            jfc.addChoosableFileFilter(filter);
            int returnValue = jfc.showSaveDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {

                themer.setTheme(jfc.getSelectedFile());

                displayThemeSettings();
                buildDemoNode();

                guiNode.attachChild(themeConfigContainer);
                guiNode.attachChild(themeConfigPropsContainer);
                guiNode.attachChild(demoNode);
            }
        });

        Button loadThemeButton = loadThemeContainer.addChild(new Button("Load Theme..."), 0, 1);
        loadThemeButton.setInsets(valueInsets);
        loadThemeButton.addClickCommands(source -> {

            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            jfc.setDialogTitle("Select a Theme File...");
            jfc.setMultiSelectionEnabled(false);
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Lemur Themes", "json");
            jfc.addChoosableFileFilter(filter);
            int returnValue = jfc.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {

                themer.setTheme(jfc.getSelectedFile());

                displayThemeSettings();
                buildDemoNode();

                guiNode.attachChild(themeConfigContainer);
                guiNode.attachChild(themeConfigPropsContainer);
                guiNode.attachChild(demoNode);
            }

        });

        Label label;

        // Current Theme Name
        // Container themeNameContainer = themeConfigContainer.addChild(new Container());
        label = loadThemeContainer.addChild(new Label("Current Theme"), 1, 0);
        label.setTextHAlignment(HAlignment.Right);
        label.setTextVAlignment(VAlignment.Center);
        label.setInsets(titleInsets);

        label = loadThemeContainer.addChild(new Label(themer.getActiveThemeFile().getName()), 1, 1);
        label.setTextHAlignment(HAlignment.Right);
        label.setTextVAlignment(VAlignment.Center);
        label.setInsets(valueInsets);

        // add each registered ThemedElement
        Label elementsLabel = themeConfigContainer.addChild(new Label("Themed Elements"));
        elementsLabel.setInsets(valueInsets);

        themedElementListBox = themeConfigContainer.addChild(new ListBox<>());
        themedElementListBox.setCellRenderer(new ThemedElementCellRenderer());
        themedElemRef = themedElementListBox.getSelectionModel().createReference();

        int maxItems = 9;
        // themedElementListBox.setVisibleItems(Math.min(themer.getThemedElements().size(), maxItems));
        themedElementListBox.setVisibleItems(Math.min(themer.getActiveTheme().getThemedElementCount(), maxItems));

        themer.getActiveTheme().getThemedElementMap().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparing(o -> o.getClass().getSimpleName())))
                .forEach( elem -> themedElementListBox.getModel().add(elem.getValue()) );

        // finally
        float border = 10;
        themeConfigContainer.setLocalTranslation( border, getApplication().getCamera().getHeight() - border, 1 );

        // finally
        Button updateThemeButton = themeConfigContainer.addChild(new Button("Update Changes"));
        updateThemeButton.addClickCommands(source -> {
            themer.applyTheme();

            Integer selectedElement = themedElementListBox.getSelectionModel().getSelection();

            displayThemeSettings();
            guiNode.attachChild(themeConfigContainer);
            guiNode.attachChild(themeConfigPropsContainer);

            if (selectedElement != null) {
                themedElementListBox.getSelectionModel().setSelection(selectedElement);
            }

            buildDemoNode();
            guiNode.attachChild(demoNode);

        });

        Button saveButton = themeConfigContainer.addChild(new Button("Save Changes"));
        saveButton.addClickCommands(source -> themer.saveActiveTheme());

        themeConfigPropsContainer = new Container();


    }

    private void buildDemoNode() {

        if (demoNode != null) {
            demoNode.removeFromParent();
        }

        demoNode = new Node();

        Container container = new Container();
        demoNode.attachChild(container);

        container.addChild(new Label("I am a label"));
        container.addChild(new Button("I am a button"));
        container.addChild(new Checkbox("I am a checkbox"));

        ProgressBar progressBar = container.addChild(new ProgressBar());
        progressBar.setProgressPercent(0.65f);
        progressBar.getLabel().setText("ProgressBar");

        container.addChild(new Label("")); // a spacer

        ListBox<String> listBox = container.addChild(new ListBox<>());
        for (int i = 0; i < 10; i++) {
            listBox.getModel().add("ListBox Item " + (i + 1));
        }

        container.setLocalTranslation(
                10,
                container.getPreferredSize().y + 10,
                1
        );

        // tabbed panel
        TabbedPanel tabbedPanel = new TabbedPanel();
        for (int i = 0; i < 5; i++) {
            Container tabContainer = new Container();

            Label label = tabContainer.addChild(new Label("Content " + (i + 1) + "\nLorem\nImsum"));

            tabbedPanel.addTab("Tab " + (i + 1), tabContainer);
        }

        tabbedPanel.setLocalTranslation(
                container.getLocalTranslation().x + container.getPreferredSize().x + 10,
                tabbedPanel.getPreferredSize().y + 10,
                1
        );
        demoNode.attachChild(tabbedPanel);

        // Rollup Panel
        RollupPanel rollupPanel = new RollupPanel("Test Rollup Panel", null);
        Container rollupContainer = new Container();
        rollupContainer.addChild(new Label("Content for rollupPanel"));
        rollupPanel.setContents(rollupContainer);

        rollupPanel.setLocalTranslation(
                tabbedPanel.getLocalTranslation().x + tabbedPanel.getPreferredSize().x + 10,
                rollupPanel.getPreferredSize().y + 10,
                1
        );
        demoNode.attachChild(rollupPanel);




    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {
        guiNode.attachChild(themeConfigContainer);
        guiNode.attachChild(themeConfigPropsContainer);
        guiNode.attachChild(demoNode);
    }

    @Override
    protected void onDisable() {
        themeConfigContainer.removeFromParent();
        themeConfigPropsContainer.removeFromParent();
        demoNode.removeFromParent();
    }

    @Override
    public void update(float tpf) {

        if (themedElemRef.update()) {

            Set<Integer> indices = themedElemRef.get();
            int index = indices.iterator().next();

            ThemedElement themedElement = themedElementListBox.getModel().get(index);

            propComponents.clear();

            ReflectedFieldComponentBuilder componentBuilder = new ReflectedFieldComponentBuilder();
            componentBuilder.setObject(themedElement, "elementId", "child");

            List<JmeComponent> builtComponents = componentBuilder.build();

            propComponents.addAll(builtComponents);

            themeConfigPropsContainer.clearChildren();

            Container titleContainer = themeConfigPropsContainer.addChild(new Container(), 0, 1);
            titleContainer.setInsets(JmeComponent.INSETS);
            Label titleLabel = titleContainer.addChild(new Label(StringUtils.splitCamelCase(themedElement.getClass().getSimpleName().replace("Theme", "")) + " Settings"));
            titleLabel.setInsets(JmeComponent.INSETS);
            BackgroundUtils.setMargin(titleContainer.getBackground(), 5, 5);

            for (int i = 0; i < builtComponents.size(); i++) {

                JmeComponent component = builtComponents.get(i);

                Label label = themeConfigPropsContainer.addChild(new Label(component.getPropertyName()), i + 1, 0);
                label.setInsets(new Insets3f(5,5,5,5));
                label.setTextHAlignment(HAlignment.Right);

                themeConfigPropsContainer.addChild(component.getPanel(), i + 1, 1);

            }

            themeConfigPropsContainer.setLocalTranslation(
                    getApplication().getCamera().getWidth() - themeConfigPropsContainer.getPreferredSize().x - 10,
                    getApplication().getCamera().getHeight() - 10,
                    1);

        }

        for (JmeComponent component : propComponents) {
            component.update(tpf);
        }

    }

}
