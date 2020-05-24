import com.jayfella.lemur.LemurThemer;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.*;

public class TestThemeLoading extends SimpleApplication {

    public static void main(String... args) {

        TestThemeLoading testThemeLoading = new TestThemeLoading();

        AppSettings appSettings = new AppSettings(true);
        appSettings.setResolution(800, 600);

        testThemeLoading.setSettings(appSettings);
        testThemeLoading.setShowSettings(false);
        testThemeLoading.start();

    }

    private TestThemeLoading() {
        super (new AppState[0]);
    }

    @Override
    public void simpleInitApp() {

        GuiGlobals.initialize(this);

        LemurThemer lemurThemer = new LemurThemer();
        lemurThemer.setTheme("./test.lemur.json");

        Container container = new Container();
        container.addChild(new Label("I am a themed label."));
        container.addChild(new Button("I am a themed button"));
        container.addChild(new Checkbox("I am a themed checkbox"));

        container.setLocalTranslation(new Vector3f(
                cam.getWidth() * 0.5f - container.getPreferredSize().x * 0.5f,
                cam.getHeight() * 0.5f + container.getPreferredSize().y * 0.5f,
                1.0f));

        guiNode.attachChild(container);

    }

}
