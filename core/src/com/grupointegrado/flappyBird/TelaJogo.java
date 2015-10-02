package com.grupointegrado.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;

import static com.grupointegrado.flappyBird.Constantes.ALTURA_BORDA;
import static com.grupointegrado.flappyBird.Constantes.ESCALA;
import static com.grupointegrado.flappyBird.Constantes.FPS;
import static com.grupointegrado.flappyBird.Constantes.PIXELS_METRO;

/**
 * Created by Douglas on 24/09/2015.
 */
public class TelaJogo extends TelaBase {

    private static final String CORPO_BORDA = "CORPO_BORDA";

    private OrthographicCamera camera;
    private OrthographicCamera cameraInfo;
    private World mundo;
    private Box2DDebugRenderer debug;
    private Passaro passaro;
    private Body borda1;
    private boolean iniciou = false;
    private float larguraBorda;
    private Array<Obstaculo> obstaculos = new Array<Obstaculo>();
    private int pontuacao = 0;

    private Stage palco;
    private Label lbPontuacao;
    private BitmapFont fonte;

    private Music musicaFundo;
    private Sound somAsas;
    private Sound somGameover;
    private boolean gameover = false;

    private SpriteBatch pintor;
    private Texture texturaFundo;
    private Texture texturaChao;

    public TelaJogo(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / ESCALA, Gdx.graphics.getHeight() / ESCALA);
        cameraInfo = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        debug = new Box2DDebugRenderer();
        pintor = new SpriteBatch();

        initTexturas();
        initMundo();
        initPassaro();
        initBordas();
        initAudio();
        initFontes();
        initInformacoes();
    }

    private void initTexturas() {
        texturaFundo = new Texture("sprites/bg.png");
        texturaChao = new Texture("sprites/ground.png");
    }

    private void initFontes() {
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 24;
        param.color = Color.WHITE;
        param.shadowColor = Color.BLACK;
        param.shadowOffsetX = 2;
        param.shadowOffsetY = 2;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));

        fonte = generator.generateFont(param);

        generator.dispose();
    }

    private void initInformacoes() {
        palco = new Stage(new FillViewport(cameraInfo.viewportWidth, cameraInfo.viewportHeight, cameraInfo));

        Label.LabelStyle estilo = new Label.LabelStyle();
        estilo.font = fonte;

        lbPontuacao = new Label("Pontuação: 0", estilo);
        palco.addActor(lbPontuacao);
    }

    private void initAudio() {
        musicaFundo = Gdx.audio.newMusic(Gdx.files.internal("songs/music.mp3"));
        musicaFundo.setLooping(true);
        musicaFundo.setVolume(0.3f);

        somAsas = Gdx.audio.newSound(Gdx.files.internal("songs/wing.ogg"));

        somGameover = Gdx.audio.newSound(Gdx.files.internal("songs/game-over.mp3"));
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
            if (!gameover)
                somGameover.play(1);
            gameover = true;
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

        if (gameover) {
            if (musicaFundo.isPlaying())
                musicaFundo.stop();
            passaro.getCorpo().setFixedRotation(false);
            mundo.step(1f / FPS, 6, 2);
        } else {
            capturaTeclas(delta);
            if (iniciou) {
                if (!musicaFundo.isPlaying())
                    musicaFundo.play();
                passaro.atualizar(delta);
                mundo.step(1f / FPS, 6, 2);
                atualizarBorda();
                atualizarObstaculos();
            }
            atualizarCamera();
        }

        pintor.begin();
        pintor.setProjectionMatrix(cameraInfo.combined);
        pintor.draw(texturaFundo, 0, 0, cameraInfo.viewportWidth, cameraInfo.viewportHeight);
        pintor.draw(texturaChao, 0, ALTURA_BORDA * PIXELS_METRO - texturaChao.getHeight(), cameraInfo.viewportWidth, texturaChao.getHeight());
        passaro.pintar(pintor, camera, cameraInfo);
        pintor.end();

        atualizarInformacoes();
        palco.act(delta);
        palco.draw();

        debug.render(mundo, camera.combined.cpy().scl(PIXELS_METRO));
    }

    private void atualizarInformacoes() {
        lbPontuacao.setText("Pontuação: " + pontuacao);
        lbPontuacao.setPosition(10, palco.getHeight() - lbPontuacao.getPrefHeight() - 10);
    }

    private void atualizarObstaculos() {
        if (obstaculos.size < 3) {
            Obstaculo ultimoObstaculo = null;
            if (obstaculos.size > 0) {
                ultimoObstaculo = obstaculos.peek();
            }
            Obstaculo obstaculo = new Obstaculo(mundo, camera, ultimoObstaculo);
            obstaculos.add(obstaculo);
        }
        for (Obstaculo obstaculo : obstaculos) {
            float inicioCameraX = (camera.position.x - camera.viewportWidth / 2) / PIXELS_METRO;
            if (inicioCameraX > obstaculo.getX()) {
                obstaculo.remover();
                obstaculos.removeValue(obstaculo, true);
            } else if (!obstaculo.isPassou() && passaro.getCorpo().getPosition().x > obstaculo.getX()) {
                obstaculo.setPassou(true);
                pontuacao++;
                System.out.println("pontuacao: " + pontuacao);
            }
        }
    }

    private void atualizarBorda() {
        Vector2 posicao = passaro.getCorpo().getPosition();
        borda1.setTransform(posicao.x, 0, 0);
    }

    private void atualizarCamera() {
        camera.position.x = passaro.getCorpo().getPosition().x * PIXELS_METRO;
        camera.update();
    }

    private void capturaTeclas(float delta) {
        if (Gdx.input.isTouched()) {
            iniciou = true;
            passaro.pular();
            if (Gdx.input.justTouched()) {
                somAsas.play(1);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / ESCALA, height / ESCALA);
        camera.update();
        cameraInfo.setToOrtho(false, width, height);
        cameraInfo.update();
        dimensionaBorda();
    }

    private void dimensionaBorda() {
        borda1.getFixtureList().clear();
        larguraBorda = camera.viewportWidth / PIXELS_METRO;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(larguraBorda / 2, ALTURA_BORDA / 2);
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
        musicaFundo.dispose();
        somAsas.dispose();
        fonte.dispose();
        palco.dispose();
        texturaFundo.dispose();
        passaro.dispose();
        texturaChao.dispose();
    }
}
