package com.grupointegrado.flappyBird;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;

import static com.grupointegrado.flappyBird.Constantes.PIXELS_METRO;

/**
 * Created by Douglas on 24/09/2015.
 */
public class Passaro {

    public static final String CORPO_PASSARO = "CORPO_PASSARO";
    private final float TEMPO_PULANDO_MAXIMO = 0.5f;
    public static final float DIAMETRO_PASSARO = 40 / PIXELS_METRO;


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
        float y = (camera.viewportHeight / 2) / PIXELS_METRO;
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
        atualizarVelocidade();
        atualizarRotacao();
    }

    private void atualizarVelocidade() {
        corpo.setLinearVelocity(1, corpo.getLinearVelocity().y);
    }

    private void atualizarRotacao() {
        if (corpo.getLinearVelocity().y > 0) {
            corpo.setTransform(corpo.getPosition(), (float) Math.toRadians(15));
        } else if (corpo.getLinearVelocity().y < 0) {
            corpo.setTransform(corpo.getPosition(), (float) Math.toRadians(-15));
        } else {
            corpo.setTransform(corpo.getPosition(), (float) Math.toRadians(0));
        }
    }

    public void pular(float delta) {
        pulando = true;
        if (tempoPulando < TEMPO_PULANDO_MAXIMO) {
            corpo.setLinearVelocity(corpo.getLinearVelocity().x, 0);
            corpo.applyForceToCenter(0, 250, false);
        }
    }
}
