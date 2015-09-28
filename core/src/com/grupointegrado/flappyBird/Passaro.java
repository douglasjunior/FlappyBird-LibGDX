package com.grupointegrado.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

import static com.grupointegrado.flappyBird.Constantes.ESCALA;
import static com.grupointegrado.flappyBird.Constantes.PIXELS;

/**
 * Created by Douglas on 24/09/2015.
 */
public class Passaro {

    public static final String CORPO_PASSARO = "CORPO_PASSARO";
    private final float TEMPO_PULANDO_MAXIMO = 0.2f;

    private final World world;
    private Body corpo;
    private float tempoPulando;
    private boolean pulando = false;

    public Passaro(World world) {
        this.world = world;

        initCorpo();
    }

    private void initCorpo() {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(0, (Gdx.graphics.getHeight() / ESCALA / 2) / PIXELS);
        def.fixedRotation = false;
        corpo = world.createBody(def);
        CircleShape shape = new CircleShape();
        shape.setRadius(20 / PIXELS);
        Fixture fixacao = corpo.createFixture(shape, 1);
        fixacao.setUserData(CORPO_PASSARO);
        shape.dispose();
    }

    public Body getCorpo() {
        return corpo;
    }


    public void atualizar(float delta) {
        if (pulando) {
            tempoPulando += delta;
        } else {
            tempoPulando = 0;
        }
        pulando = false;
    }

    public void pular(float delta) {
        pulando = true;
        if (tempoPulando < TEMPO_PULANDO_MAXIMO) {
            corpo.setLinearVelocity(corpo.getLinearVelocity().x, 0);
            corpo.applyForceToCenter(0, 300f, false);
        }
    }
}
