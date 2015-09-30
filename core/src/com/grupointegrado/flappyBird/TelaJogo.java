package com.grupointegrado.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import static com.grupointegrado.flappyBird.Constantes.ESCALA;
import static com.grupointegrado.flappyBird.Constantes.PIXELS;

/**
 * Created by Douglas on 24/09/2015.
 */
public class TelaJogo extends TelaBase {

    private static final String CORPO_BORDA = "CORPO_BORDA";

    private OrthographicCamera camera;
    private World mundo;
    private Box2DDebugRenderer debug;
    private Passaro passaro;
    private Body borda1;
    private boolean iniciou = false;
    private float larguraBorda;
    private float alturaBorda;
    private Array<Obstaculo> obstaculos = new Array<Obstaculo>();

    public TelaJogo(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / ESCALA, Gdx.graphics.getHeight() / ESCALA);
        debug = new Box2DDebugRenderer();

        initMundo();
        initPassaro();
        initBordas();
    }

    private void initMundo() {
        mundo = new World(new Vector2(0f, -9.8f), false);
        mundo.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                detectarColisao(contact.getFixtureA(), contact.getFixtureB());
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }

    private void detectarColisao(Fixture fixtureA, Fixture fixtureB) {
        if (Passaro.CORPO_PASSARO.equals(fixtureA.getUserData()) ||
                Passaro.CORPO_PASSARO.equals(fixtureB.getUserData())) {
            System.out.println(fixtureA.getUserData() + " ... " + fixtureB.getUserData());
        }
    }

    private void initPassaro() {
        passaro = new Passaro(mundo, camera);
    }

    private void initBordas() {
        borda1 = Util.criarCorpo(mundo, BodyDef.BodyType.StaticBody, -larguraBorda, 0);
        dimensionaBorda();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        atualizarCamera();
        capturaTeclas(delta);

        if (iniciou) {
            atualizarBorda();
            atualizarObstaculos();
            passaro.atualizar(delta);
            mundo.step(delta, 6, 2);
        }

        debug.render(mundo, camera.combined.scl(PIXELS));
    }

    private void atualizarObstaculos() {
        if (obstaculos.size == 0) {
            Obstaculo obstaculo = new Obstaculo(mundo, camera);
            obstaculos.add(obstaculo);
        }
    }

    private void atualizarBorda() {
        Vector2 posicao = passaro.getCorpo().getPosition();
        borda1.setTransform(posicao.x, 0, 0);
    }

    private void atualizarCamera() {
        Vector3 posicao = new Vector3();
        posicao.x = passaro.getCorpo().getPosition().x * PIXELS;
        posicao.y = camera.position.y;
        camera.position.set(posicao);
        camera.update();
    }

    private void capturaTeclas(float delta) {
        if (Gdx.input.isTouched()) {
            iniciou = true;
            passaro.pular(delta);
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / ESCALA, height / ESCALA);
        camera.update();

        dimensionaBorda();
    }

    private void dimensionaBorda() {
        borda1.getFixtureList().clear();
        larguraBorda = camera.viewportWidth / ESCALA / PIXELS;
        alturaBorda = 10 / ESCALA / PIXELS;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(larguraBorda, alturaBorda);
        Util.criarForma(borda1, shape, CORPO_BORDA);
        shape.dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        mundo.dispose();
        debug.dispose();
    }
}
