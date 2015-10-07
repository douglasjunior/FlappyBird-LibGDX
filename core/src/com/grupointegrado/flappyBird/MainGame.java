package com.grupointegrado.flappyBird;

import com.badlogic.gdx.Game;

public class MainGame extends Game {

    @Override
    public void create() {
        setScreen(new TelaJogo(this));
    }
}
