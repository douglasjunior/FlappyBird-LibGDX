package com.grupointegrado.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;

import static com.grupointegrado.flappyBird.Util.ALTURA_CHAO;
import static com.grupointegrado.flappyBird.Util.ESCALA;
import static com.grupointegrado.flappyBird.Util.FPS;
import static com.grupointegrado.flappyBird.Util.PIXELS_METRO;

/**
 * Created by Douglas on 24/09/2015.
 */
public class TelaJogo extends TelaBase {

    private static final String CORPO_CHAO = "CORPO_CHAO";

    private OrthographicCamera camera;
    private OrthographicCamera cameraInfo;
    private World mundo;
    private Box2DDebugRenderer debug;
    private Passaro passaro;
    private Body chao;
    private boolean iniciou = false;
    private Array<Obstaculo> obstaculos = new Array<Obstaculo>();
    private int pontuacao = 0;

    private Stage palco;
    private Label lbPontuacao;
    private ImageButton btnPlay;
    private BitmapFont fonte;

    private Music musicaFundo;
    private Sound somAsas;
    private Sound somGameover;
    private boolean gameover = false;

    private SpriteBatch batch;
    private Texture[] texturaPassaro = new Texture[3];
    private Texture texturaFundo;
    private Texture texturaChao;
    private Sprite spriteChao1;
    private Sprite spriteChao2;
    private Texture texturaObstaculoCima;
    private Texture texturaObstaculoBaixo;
    private Texture texturaGameover;
    private Texture texturaBotao;

    public TelaJogo(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / ESCALA, Gdx.graphics.getHeight() / ESCALA);
        cameraInfo = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        debug = new Box2DDebugRenderer();
        batch = new SpriteBatch();

        initTexturas();
        initMundo();
        initPassaro();
        initChao();
        initAudio();
        initFontes();
        initInformacoes();
    }

    private void initTexturas() {
        texturaPassaro[0] = new Texture("sprites/bird-1.png");
        texturaPassaro[1] = new Texture("sprites/bird-2.png");
        texturaPassaro[2] = new Texture("sprites/bird-3.png");
        texturaFundo = new Texture("sprites/bg.png");
        texturaChao = new Texture("sprites/ground.png");
        texturaObstaculoCima = new Texture("sprites/toptube.png");
        texturaObstaculoBaixo = new Texture("sprites/bottomtube.png");
        texturaGameover = new Texture("sprites/gameover.png");
        texturaBotao = new Texture("sprites/playbtn.png");
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
        Gdx.input.setInputProcessor(palco);

        Label.LabelStyle estilo = new Label.LabelStyle();
        estilo.font = fonte;

        lbPontuacao = new Label("Pontuação: 0", estilo);
        palco.addActor(lbPontuacao);

        ImageButton.ImageButtonStyle estiloBotao = new ImageButton.ImageButtonStyle();
        estiloBotao.up = new SpriteDrawable(new Sprite(texturaBotao));
        btnPlay = new ImageButton(estiloBotao);
        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                iniciou = true;
                btnPlay.remove();
            }
        });
        palco.addActor(btnPlay);
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
        }
    }

    private void initPassaro() {
        passaro = new Passaro(mundo, camera, texturaPassaro);
    }

    private void initChao() {
        chao = Util.criarCorpo(mundo, BodyDef.BodyType.StaticBody, 0, 0);

        float inicioCamera = 0 - camera.viewportWidth / 2;
        float altura = (ALTURA_CHAO * PIXELS_METRO) / ESCALA;

        spriteChao1 = new Sprite(texturaChao);
        spriteChao1.setBounds(inicioCamera, 0, camera.viewportWidth, altura);

        spriteChao2 = new Sprite(texturaChao);
        spriteChao2.setBounds(inicioCamera + camera.viewportWidth, 0, camera.viewportWidth, altura);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        atualizar(delta);
        renderizar(delta);

        palco.act(delta);
        palco.draw();

        debug.render(mundo, camera.combined.cpy().scl(PIXELS_METRO));
    }

    private void renderizar(float delta) {
        batch.begin();

        batch.setProjectionMatrix(cameraInfo.combined);
        batch.draw(texturaFundo, 0, 0, cameraInfo.viewportWidth, cameraInfo.viewportHeight);

        passaro.renderizar(batch);
        for (Obstaculo obs : obstaculos) {
            obs.renderizar(batch);
        }

        batch.setProjectionMatrix(cameraInfo.combined);
        if (gameover)
            batch.draw(texturaGameover, cameraInfo.viewportWidth / 2 - texturaGameover.getWidth() / 2, cameraInfo.viewportHeight / 2);

        batch.setProjectionMatrix(camera.combined);
        spriteChao1.draw(batch);
        spriteChao2.draw(batch);

        batch.end();
    }

    private void atualizar(float delta) {
        if (gameover) {
            if (musicaFundo.isPlaying())
                musicaFundo.stop();
        } else {
            if (!musicaFundo.isPlaying())
                musicaFundo.play();
        }
        capturaTeclas(delta);
        passaro.getCorpo().setFixedRotation(!gameover);
        passaro.atualizar(delta, !gameover);
        if (iniciou) {
            mundo.step(1f / FPS, 6, 2);
            atualizarObstaculos();
        }
        if (!gameover) {
            atualizarCamera();
            atualizarChao();
        }
        atualizarInformacoes();
    }

    private void atualizarInformacoes() {
        lbPontuacao.setText("Pontuação: " + pontuacao);
        lbPontuacao.setPosition(10, palco.getHeight() - lbPontuacao.getPrefHeight() - 10);

        btnPlay.setPosition(cameraInfo.viewportWidth / 2 - btnPlay.getPrefWidth() / 2,
                cameraInfo.viewportHeight / 2 - btnPlay.getPrefHeight() * 2);
    }

    private void atualizarObstaculos() {
        if (obstaculos.size < 4) {
            Obstaculo ultimoObstaculo = null;
            if (obstaculos.size > 0) {
                ultimoObstaculo = obstaculos.peek();
            }
            Obstaculo obstaculo = new Obstaculo(mundo, camera, ultimoObstaculo, texturaObstaculoCima, texturaObstaculoBaixo);
            obstaculos.add(obstaculo);
        }
        for (Obstaculo obstaculo : obstaculos) {
            float inicioCameraX = (camera.position.x - camera.viewportWidth / 2) / PIXELS_METRO - obstaculo.getLargura();
            if (inicioCameraX > obstaculo.getX()) {
                obstaculo.remover();
                obstaculos.removeValue(obstaculo, true);
            } else if (!obstaculo.isPassou() && passaro.getCorpo().getPosition().x > obstaculo.getX()) {
                obstaculo.setPassou(true);
                pontuacao++;
            }
        }
    }

    private void atualizarChao() {
        Vector2 posicao = passaro.getCorpo().getPosition();
        chao.setTransform(posicao.x + passaro.getLargura() / 2, 0, 0);

        float inicioCamera = (camera.position.x - camera.viewportWidth / 2) - camera.viewportWidth;

        if (spriteChao1.getX() < inicioCamera) {
            spriteChao1.setBounds(spriteChao2.getX() + camera.viewportWidth, 0, camera.viewportWidth, spriteChao1.getHeight());
        }
        if (spriteChao2.getX() < inicioCamera) {
            spriteChao2.setBounds(spriteChao1.getX() + camera.viewportWidth, 0, camera.viewportWidth, spriteChao2.getHeight());
        }
    }

    private void atualizarCamera() {
        camera.position.x = (passaro.getCorpo().getPosition().x + passaro.getLargura() / 2) * PIXELS_METRO;
        camera.update();
    }

    private void capturaTeclas(float delta) {
        if (iniciou && !gameover && Gdx.input.isTouched()) {
            passaro.pular();
            if (Gdx.input.justTouched()) {
                somAsas.play(1);
            }
        }
//        if (gameover && Gdx.input.justTouched()){
//            game.setScreen(new TelaMenu(game));
//        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / ESCALA, height / ESCALA);
        camera.update();
        cameraInfo.setToOrtho(false, width, height);
        cameraInfo.update();
        dimensionaChao();
    }

    private void dimensionaChao() {
        chao.getFixtureList().clear();
        float larguraChao = camera.viewportWidth / PIXELS_METRO;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(larguraChao / 2, ALTURA_CHAO / 2);
        Util.criarForma(chao, shape, CORPO_CHAO);
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
        batch.dispose();
        debug.dispose();
        mundo.dispose();
        palco.dispose();
        fonte.dispose();
        for (Texture text : texturaPassaro) {
            text.dispose();
        }
        texturaFundo.dispose();
        texturaChao.dispose();
        texturaObstaculoCima.dispose();
        texturaObstaculoBaixo.dispose();
        texturaGameover.dispose();
        texturaBotao.dispose();
        musicaFundo.dispose();
        somAsas.dispose();
        somGameover.dispose();
    }
}
