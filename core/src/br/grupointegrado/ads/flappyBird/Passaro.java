package br.grupointegrado.ads.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import static br.grupointegrado.ads.flappyBird.Util.PIXELS_METRO;

/**
 * Created by Douglas on 24/09/2015.
 */
public class Passaro {

    public static final String CORPO_PASSARO = "CORPO_PASSARO";

    private final World mundo;
    private final OrthographicCamera camera;
    private Texture[] texturaPassaro;
    private Body corpo;
    private float tempoEstagio;

    private Sprite spritePassaro;
    private int estagio = 0;

    public Passaro(World world, OrthographicCamera camera, Texture[] texturaPassaro) {
        this.mundo = world;
        this.camera = camera;
        this.texturaPassaro = texturaPassaro;

        initCorpo();
        initTextura();
    }

    private void initTextura() {
        spritePassaro = new Sprite(texturaPassaro[1]);
    }

    private void initCorpo() {
        float x = 0;
        float y = (camera.viewportHeight / 2) / PIXELS_METRO;
        corpo = Util.criarCorpo(mundo, BodyDef.BodyType.DynamicBody, x, y);

        FixtureDef def = new FixtureDef();
        def.density = 1;
        def.friction = 0.4f;
        def.restitution = 0.3f;

        br.grupointegrado.ads.flappyBird.BodyEditorLoader loader = new br.grupointegrado.ads.flappyBird.BodyEditorLoader(Gdx.files.internal("physics/bird.json"));
        loader.attachFixture(corpo, "bird", def, 1, CORPO_PASSARO);
    }

    public Body getCorpo() {
        return corpo;
    }

    public void atualizar(float delta, boolean movimentar) {
        if (movimentar) {
            atualizarVelocidade();
            atualizarRotacao();
        }
        atualizarEstagio(delta);
    }

    private void atualizarEstagio(float delta) {
        if (corpo.getLinearVelocity().y >= 0) {
            tempoEstagio += delta;
            if (tempoEstagio > 0.1f) {
                tempoEstagio = 0;
                estagio++;
                if (estagio > 2) {
                    estagio = 0;
                }
            }
        } else {
            estagio = 1;
        }
    }

    private void atualizarVelocidade() {
        corpo.setLinearVelocity(2f, corpo.getLinearVelocity().y);
    }

    private void atualizarRotacao() {
        if (corpo.getLinearVelocity().y > 0) {
            corpo.setTransform(corpo.getPosition(), (float) Math.toRadians(10));
        } else if (corpo.getLinearVelocity().y < 0) {
            corpo.setTransform(corpo.getPosition(), (float) Math.toRadians(-15));
        } else {
            corpo.setTransform(corpo.getPosition(), (float) Math.toRadians(0));
        }
    }

    public void pular() {
        corpo.setLinearVelocity(corpo.getLinearVelocity().x, 0);
        corpo.applyForceToCenter(0, 100, false);
    }

    public void renderizar(SpriteBatch pintor) {
        Vector2 posicao = corpo.getPosition();
        spritePassaro.setTexture(texturaPassaro[estagio]);
        spritePassaro.setPosition(posicao.x * PIXELS_METRO, posicao.y * PIXELS_METRO);
        spritePassaro.setOrigin(0, 0);
        spritePassaro.setRotation((float) Math.toDegrees(corpo.getAngle()));
        spritePassaro.draw(pintor);
    }

    public float getAltura() {
        return texturaPassaro[0].getHeight() / PIXELS_METRO;
    }

    public float getLargura() {
        return texturaPassaro[0].getWidth() / PIXELS_METRO;
    }
}
