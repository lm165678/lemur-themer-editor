package com.jayfella.lemur.editor;

import com.jayfella.devkit.props.PropertyRegistrationService;
import com.jayfella.lemur.LemurThemer;
import com.jayfella.lemur.editor.component.Insets3fComponent;
import com.jayfella.lemur.editor.component.LemurIconComponent;
import com.jayfella.lemur.editor.component.PanelBackgroundComponent;
import com.jayfella.logging.LogUtils;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeVersion;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.TbtQuadBackgroundComponent;
import org.apache.log4j.Level;

import java.util.logging.Logger;

public class Main extends SimpleApplication {

    private static final Logger log = Logger.getLogger("Main");

    public static void main(String... args) {

        LogUtils.initializeLogger(Level.DEBUG, true);

        LogUtils.setLevel(Level.ERROR, "org.reflections");

        Main main = new Main();

        AppSettings appSettings = new AppSettings(true);
        appSettings.setTitle("Lemur Themer Editor: " + JmeVersion.FULL_NAME);
        appSettings.setResolution(1280, 720);
        appSettings.setAudioRenderer(null);

        main.setSettings(appSettings);
        main.setShowSettings(false);

        main.setPauseOnLostFocus(false);

        main.start();
    }

    private Main() {
        super(new AppState[0]);
    }

    Spatial model;

    @Override
    public void simpleInitApp() {

        PropertyRegistrationService.initialize(log);
        PropertyRegistrationService.getInstance().registerComponent(Insets3f.class, Insets3fComponent.class);
        PropertyRegistrationService.getInstance().registerComponent(IconComponent.class, LemurIconComponent.class);
        // assign both types of background elements to the same component so we can choose which type we want in the editor.
        PropertyRegistrationService.getInstance().registerComponent(QuadBackgroundComponent.class, PanelBackgroundComponent.class);
        PropertyRegistrationService.getInstance().registerComponent(TbtQuadBackgroundComponent.class, PanelBackgroundComponent.class);

        GuiGlobals.initialize(this);
        LemurThemer lemurThemer = new LemurThemer();
        lemurThemer.setTheme("./glass.lemur.json");

        viewPort.setBackgroundColor(new ColorRGBA(0.4f, 0.5f, 0.6f, 1.0f));
        cam.setFrustumFar(2000);
        cam.setLocation(new Vector3f(0, 100, 500));
        cam.lookAt(new Vector3f(0, 100, 0), Vector3f.UNIT_Y);

        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(-1, -1, -1).normalizeLocal(), ColorRGBA.White.mult(0.6f));
        rootNode.addLight(directionalLight);

        // https://sketchfab.com/3d-models/sea-keep-lonely-watcher-09a15a0c14cb4accaf060a92bc70413d
        model = assetManager.loadModel("Scene/scene.gltf");
        model.setLocalScale(1);
        rootNode.attachChild(model);

        Spatial lightNode = assetManager.loadModel("Scenes/defaultProbe.j3o");
        LightProbe lightProbe = (LightProbe) lightNode.getLocalLightList().get(0);
        lightProbe.getArea().setRadius(10000);
        rootNode.addLight(lightProbe);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);

        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 4096, 3);
        dlsf.setLight(directionalLight);
        dlsf.setEdgesThickness(10);
        fpp.addFilter(dlsf);

        viewPort.addProcessor(fpp);

        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        Node modelNode = (Node) model;

        Spatial sky = modelNode.getChild("Sky");
        sky.setShadowMode(RenderQueue.ShadowMode.Off);

        stateManager.attach(new ThemeEditorState(lemurThemer));
    }

    float angle, x, y, z, radius = 500;
    Vector3f camLoc = new Vector3f();
    Vector3f lookAt = new Vector3f(0, 100, 0);


    @Override
    public void simpleUpdate(float tpf) {

        angle += tpf * 0.05f;

        x = radius * FastMath.sin(angle);
        y = 100 + 100 * FastMath.sin(angle * 2);
        z = radius * FastMath.cos(angle);

        camLoc.set(x, 100, z);
        cam.setLocation(camLoc);

        lookAt.setY(y);
        cam.lookAt(lookAt, Vector3f.UNIT_Y);

    }



}
