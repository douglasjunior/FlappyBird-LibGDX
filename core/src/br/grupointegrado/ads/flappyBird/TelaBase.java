package br.grupointegrado.ads.flappyBird;

import com.badlogic.gdx.Screen;

public abstract class TelaBase implements Screen {

    protected MainGame game;

    public TelaBase(MainGame game){
        this.game = game;
    }

    @Override
    public void hide() {
        dispose();
    }
}
