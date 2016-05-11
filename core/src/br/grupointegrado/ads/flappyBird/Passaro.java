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

/**
 * Created by Douglas on 05/10/2015.
 */
public class Passaro {

    private final World mundo;
    private final OrthographicCamera camera;
    private final Texture[] texturas;
    private Body corpo;
    private Sprite sprite;
    private int estagio = 0;

    public Passaro(World mundo, OrthographicCamera camera, Texture[] texturas) {
        this.mundo = mundo;
        this.camera = camera;
        this.texturas = texturas;
        this.sprite = new Sprite(texturas[0]);

        initCorpo();
    }

    private void initCorpo() {
        float x = (camera.viewportWidth / 2) / Util.PIXEL_METRO;
        float y = (camera.viewportHeight / 2) / Util.PIXEL_METRO;

        corpo = Util.criarCorpo(mundo, BodyDef.BodyType.DynamicBody, x, y);

        FixtureDef definicao = new FixtureDef();
        definicao.density = 1;
        definicao.friction = 0.4f;
        definicao.restitution = 0.3f;

        BodyEditorLoader loader =
                new BodyEditorLoader(Gdx.files.internal("physics/bird.json"));
        loader.attachFixture(corpo, "bird", definicao, 1, "PASSARO");
    }

    /**
     * Atualiza o comportamento do pássaro
     *
     * @param delta
     */
    public void atualizar(float delta, boolean movimentar) {
        if (movimentar) {
            atualizarVelocidade();
            atualizarRotacao();
        }
        atualizarEstagio(delta);
    }

    private float tempoEstagio = 0;

    private void atualizarEstagio(float delta) {
        if (corpo.getLinearVelocity().y < 0) {
            // caindo
            estagio = 1;
        } else {
            // parado ou subindo
            tempoEstagio += delta;
            if (tempoEstagio > 0.1) {
                tempoEstagio = 0;
                estagio++;
                if (estagio >= 3) {
                    estagio = 0;
                }
            }
        }
    }

    private void atualizarRotacao() {
        float velocidadeY = corpo.getLinearVelocity().y;
        float rotacao = 0;
        if (velocidadeY < 0) {
            // caindo
            rotacao = -15;
        } else if (velocidadeY > 0) {
            // subindo
            rotacao = 10;
        } else {
            // reto
            rotacao = 0;
        }
        rotacao = (float) Math.toRadians(rotacao); // convertendo graus para radiano
        corpo.setTransform(corpo.getPosition(), rotacao);
    }

    private void atualizarVelocidade() {
        corpo.setLinearVelocity(2f, corpo.getLinearVelocity().y);
    }

    /**
     * Aplica uma força positiva no Y para simular o Pulo
     */
    public void pular() {
        corpo.setLinearVelocity(corpo.getLinearVelocity().x, 0);
        corpo.applyForceToCenter(0, 115, false);
    }

    public Body getCorpo() {
        return corpo;
    }

    public void renderizar(SpriteBatch pincel) {
        Vector2 posicao = corpo.getPosition();
        sprite.setTexture(texturas[estagio]);
        sprite.setPosition(posicao.x * Util.PIXEL_METRO, posicao.y * Util.PIXEL_METRO);
        sprite.setOrigin(0, 0);
        sprite.setRotation((float) Math.toDegrees(corpo.getAngle()));
        sprite.draw(pincel);
    }

    public float getWidth(){
        return sprite.getWidth();
    }
}
