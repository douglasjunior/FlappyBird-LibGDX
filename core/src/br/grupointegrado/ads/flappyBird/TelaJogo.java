package br.grupointegrado.ads.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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

import static br.grupointegrado.ads.flappyBird.Util.ALTURA_CHAO;
import static br.grupointegrado.ads.flappyBird.Util.ALTURA_TELA;
import static br.grupointegrado.ads.flappyBird.Util.ESCALA;
import static br.grupointegrado.ads.flappyBird.Util.FPS;
import static br.grupointegrado.ads.flappyBird.Util.PIXELS_METRO;

/**
 * Created by Douglas on 24/09/2015.
 */
public class TelaJogo extends TelaBase {

    private static final String CORPO_CHAO = "CORPO_CHAO";
    private static final String PREFERENCIAS = "FLAPPY_BIRD";
    private static final String MAIOR_PONTUACAO = "MAIOR_PONTUACAO";

    private OrthographicCamera camera;
    private OrthographicCamera cameraInfo;
    private World mundo;
    private Box2DDebugRenderer debug;
    private Passaro passaro;
    private Body chao;
    private boolean iniciou = false;
    private Array<Obstaculo> obstaculos = new Array<Obstaculo>();

    private int maiorPontuacao = 0;
    private int pontuacao = 0;
    private Stage palco;
    private Label lbPontuacaoMaxima;
    private Label lbPontuacao;
    private ImageButton btnPlay;

    private BitmapFont fonte;
    private BitmapFont fontePontuacao;
    private Music musicaFundo;
    private Sound somAsas;
    private Sound somGameover;
    private Sound somPonto;

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
    private boolean sair = false;
    private boolean pulando = false;

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
        param.size = (int) ((cameraInfo.viewportHeight * 24f) / ALTURA_TELA);
        param.color = Color.WHITE;
        param.shadowColor = Color.BLACK;
        param.shadowOffsetX = (int) (2f * Gdx.graphics.getDensity());
        param.shadowOffsetY = (int) (2f * Gdx.graphics.getDensity());

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));

        fonte = generator.generateFont(param);

        param.size = (int) ((cameraInfo.viewportHeight * 56f) / ALTURA_TELA);
        param.shadowOffsetX = (int) (4f * Gdx.graphics.getDensity());
        param.shadowOffsetY = (int) (4f * Gdx.graphics.getDensity());

        fontePontuacao = generator.generateFont(param);

        generator.dispose();
    }

    private void initInformacoes() {
        palco = new Stage(new FillViewport(cameraInfo.viewportWidth, cameraInfo.viewportHeight, cameraInfo));
        Gdx.input.setInputProcessor(palco);

        Label.LabelStyle estilo = new Label.LabelStyle();
        estilo.font = fonte;

        lbPontuacaoMaxima = new Label("Maior: 0", estilo);
        palco.addActor(lbPontuacaoMaxima);

        estilo.font = fontePontuacao;

        lbPontuacao = new Label("0", estilo);
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
        btnPlay.setSize(((cameraInfo.viewportHeight * texturaBotao.getWidth()) / ALTURA_TELA), ((cameraInfo.viewportHeight * texturaBotao.getHeight()) / ALTURA_TELA));
        palco.addActor(btnPlay);

        maiorPontuacao = Gdx.app.getPreferences(PREFERENCIAS).getInteger(MAIOR_PONTUACAO);
    }

    private void initAudio() {
        musicaFundo = Gdx.audio.newMusic(Gdx.files.internal("songs/music.mp3"));
        musicaFundo.setLooping(true);
        musicaFundo.setVolume(0.2f);

        somAsas = Gdx.audio.newSound(Gdx.files.internal("songs/wing.mp3"));

        somGameover = Gdx.audio.newSound(Gdx.files.internal("songs/game-over.mp3"));

        somPonto = Gdx.audio.newSound(Gdx.files.internal("songs/coin.mp3"));
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
            salvarPontuacao();
        }
    }

    private void initPassaro() {
        passaro = new Passaro(mundo, camera, texturaPassaro);
    }

    private void initChao() {
        chao = Util.criarCorpo(mundo, BodyDef.BodyType.StaticBody, 0, 0);

        float inicioCamera = 0 - camera.viewportWidth / 2;
        float altura = (ALTURA_CHAO * PIXELS_METRO) / 2;

        spriteChao1 = new Sprite(texturaChao);
        spriteChao1.setBounds(inicioCamera, 0, camera.viewportWidth, altura);

        spriteChao2 = new Sprite(texturaChao);
        spriteChao2.setBounds(inicioCamera + camera.viewportWidth, 0, camera.viewportWidth, altura);
    }

    @Override
    public synchronized void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        capturaTeclas();

        if (!sair) {
            atualizar(delta);
            renderizar();

            //debug.render(mundo, camera.combined.cpy().scl(PIXELS_METRO));
        }
    }

    private void renderizar() {
        batch.begin();

        batch.setProjectionMatrix(cameraInfo.combined);
        batch.draw(texturaFundo, 0, 0, cameraInfo.viewportWidth, cameraInfo.viewportHeight);

        batch.setProjectionMatrix(camera.combined);
        passaro.renderizar(batch);
        for (Obstaculo obs : obstaculos) {
            obs.renderizar(batch);
        }
        spriteChao1.draw(batch);
        spriteChao2.draw(batch);

        if (gameover) {
            batch.setProjectionMatrix(cameraInfo.combined);
            float largura = (cameraInfo.viewportHeight * texturaGameover.getWidth()) / ALTURA_TELA;
            float altura = (cameraInfo.viewportHeight * texturaGameover.getHeight()) / ALTURA_TELA;
            batch.draw(texturaGameover, cameraInfo.viewportWidth / 2 - largura / 2, cameraInfo.viewportHeight / 2,
                    largura, altura);
        }

        batch.end();

        palco.draw();
    }

    private void atualizar(float delta) {
        palco.act(delta);
        if (pulando) {
            passaro.pular();
            somAsas.play(1);
        }
        if (gameover) {
            if (musicaFundo.isPlaying())
                musicaFundo.stop();
        } else {
            if (!musicaFundo.isPlaying())
                musicaFundo.play();
        }
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
        lbPontuacaoMaxima.setVisible(!iniciou);
        lbPontuacaoMaxima.setText("Maior: " + maiorPontuacao);
        lbPontuacaoMaxima.setPosition(10, palco.getHeight() - lbPontuacaoMaxima.getPrefHeight());

        lbPontuacao.setVisible(iniciou);
        lbPontuacao.setText(pontuacao + "");
        lbPontuacao.setPosition(cameraInfo.viewportWidth / 2 - lbPontuacao.getPrefWidth() / 2, palco.getHeight() - lbPontuacao.getPrefHeight());

        btnPlay.setPosition(cameraInfo.viewportWidth / 2 - btnPlay.getWidth() / 2,
                cameraInfo.viewportHeight / 2 - btnPlay.getHeight() * 2);
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
                somPonto.play(0.4f);
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

    private void capturaTeclas() {
        pulando = false;
        if (iniciou && !gameover && Gdx.input.justTouched()) {
            pulando = true;
        }
        if (gameover && Gdx.input.justTouched()) {
            reiniciar();
        }
    }

    private void reiniciar() {
        sair = true;
        game.setScreen(new TelaInicio(game));
    }

    private void salvarPontuacao() {
        Preferences pref = Gdx.app.getPreferences(PREFERENCIAS);
        int maiorPontuacao = pref.getInteger(MAIOR_PONTUACAO);
        if (pontuacao > maiorPontuacao) {
            pref.putInteger(MAIOR_PONTUACAO, pontuacao);
            pref.flush();
        }
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
    public synchronized void dispose() {
        debug.dispose();
        mundo.dispose();
        palco.dispose();
        batch.dispose();
        fonte.dispose();
        fontePontuacao.dispose();
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
        somPonto.dispose();
    }
}
