package com.grupointegrado.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Douglas on 24/09/2015.
 */
public class Passaro {

    private final World world;
    private Body corpo;

    public Passaro(World world) {
        this.world = world;

        initCorpo();
    }

    private void initCorpo() {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(0, Gdx.graphics.getHeight() / 2);
        def.fixedRotation = true;
        corpo = world.createBody(def);
        CircleShape shape = new CircleShape();
        shape.setRadius(50);
        corpo.createFixture(shape, 1);
        shape.dispose();
    }

    public Body getCorpo() {
        return corpo;
    }

    public void pular(float delta) {
        corpo.applyForceToCenter(0, 300, false);
    }
}
