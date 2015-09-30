package br.grupointegrado.ads.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

public class TelaJogo extends TelaBase {

    private static final float ESCALA = 2;
    private static final float PIXEL_METRO = 32;

    private OrthographicCamera camera; // camera do jogo
    private World mundo; // representa o mundo do Box2D

    private Box2DDebugRenderer debug; // desenha o mundo na tela para ajudar no desenvolvimento

    public TelaJogo(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / ESCALA, Gdx.graphics.getHeight() / ESCALA);
        debug = new Box2DDebugRenderer();
        mundo = new World(new Vector2(0, -9.8f), false);

        initPassaro();
    }

    private void initPassaro() {
        BodyDef def = new BodyDef(); // objeto de definição do copor
        def.type = BodyDef.BodyType.DynamicBody;
        float y = (Gdx.graphics.getHeight() / 2 / ESCALA) / PIXEL_METRO + 2;
        float x = (Gdx.graphics.getWidth() / 2 / ESCALA) / PIXEL_METRO + 2;
        def.position.set(x, y);
        def.fixedRotation = true;

        Body corpo = mundo.createBody(def); // criação do corpo

        CircleShape shape = new CircleShape(); // forma do corpo
        shape.setRadius(20 / PIXEL_METRO);

        Fixture fixacao = corpo.createFixture(shape, 1); // objeto de exibição do corpo

        shape.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1); // limpa a tela e pinta a cor de fundo
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // mantem o buffer de cores

        camera.position.set(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, 0);
        camera.update();

        mundo.step(delta, 6, 2);

        debug.render(mundo, camera.combined.scl(PIXEL_METRO));
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / ESCALA, height / ESCALA);
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
