package com.grupointegrado.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Douglas on 24/09/2015.
 */
public class TelaJogo extends TelaBase {

    private OrthographicCamera camera;
    private World mundo;
    private Box2DDebugRenderer debug;
    private Passaro passaro;

    public TelaJogo(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        mundo = new World(new Vector2(0, -98f), false);
        debug = new Box2DDebugRenderer();

        passaro = new Passaro(mundo);
        initBordas();
    }

    private Body bordaDireita, bordaEsquerda, bordaCima, bordaBaixo;

    private void initBordas() {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.fixedRotation = true;
        PolygonShape shape = new PolygonShape();

        def.position.set(0, 0);
        shape.setAsBox(camera.viewportWidth, 10);

        bordaBaixo = mundo.createBody(def);
        bordaBaixo.createFixture(shape, 1);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.position.set(passaro.getCorpo().getPosition().x, camera.position.y, camera.position.z);
        camera.update();

        capturaTeclas(delta);

        mundo.step(delta, 6, 2);
        debug.render(mundo, camera.combined);
    }

    private void capturaTeclas(float delta) {
        if (Gdx.input.justTouched()) {
            passaro.pular(delta);
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
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
        mundo.dispose();
        debug.dispose();
    }
}
