package com.grupointegrado.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;

import static com.grupointegrado.flappyBird.Constantes.ESCALA;
import static com.grupointegrado.flappyBird.Constantes.PIXELS;

/**
 * Created by Douglas on 24/09/2015.
 */
public class Passaro {

    public static final String CORPO_PASSARO = "CORPO_PASSARO";
    private final float TEMPO_PULANDO_MAXIMO = 0.5f;
    public static final float DIAMETRO_PASSARO = 40 / PIXELS;


    private final World mundo;
    private final OrthographicCamera camera;
    private Body corpo;
    private float tempoPulando;
    private boolean pulando = false;

    public Passaro(World world, OrthographicCamera camera) {
        this.mundo = world;
        this.camera = camera;

        initCorpo();
    }

    private void initCorpo() {
        CircleShape shape = new CircleShape();
        shape.setRadius(DIAMETRO_PASSARO / 2);
        float x = 0;
        float y = (camera.viewportWidth / PIXELS) / 2;
        corpo = Util.criarCorpo(mundo, BodyDef.BodyType.DynamicBody, x, y);
        Util.criarForma(corpo, shape, CORPO_PASSARO);
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
        corpo.setLinearVelocity(70 * delta, corpo.getLinearVelocity().y);
    }

    public void pular(float delta) {
        pulando = true;
        if (tempoPulando < TEMPO_PULANDO_MAXIMO) {
            corpo.setLinearVelocity(corpo.getLinearVelocity().x, 0);
            corpo.applyForceToCenter(0, (5 / delta), false);
        }
    }
}
