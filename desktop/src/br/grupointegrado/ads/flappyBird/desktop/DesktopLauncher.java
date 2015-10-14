package br.grupointegrado.ads.flappyBird.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import br.grupointegrado.ads.flappyBird.MainGame;

import static br.grupointegrado.ads.flappyBird.Util.FPS;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.height = 640;
        config.width = 480;
        config.backgroundFPS = (int) FPS;
        config.foregroundFPS = (int) FPS;
        new LwjglApplication(new MainGame(), config);
    }
}
