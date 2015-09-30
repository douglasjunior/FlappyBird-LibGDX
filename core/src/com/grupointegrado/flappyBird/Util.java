package com.grupointegrado.flappyBird;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by douglas on 30/09/15.
 */
public class Util {

    public static Body criarCorpo(World mundo, BodyDef.BodyType tipo, float x, float y){
        BodyDef def = new BodyDef();
        def.type = tipo;
        def.fixedRotation = true;
        def.position.set(x, y);
        Body corpo = mundo.createBody(def);
        return corpo;
    }

    public static Fixture criarForma(Body corpo, Shape shape, String userData){
        Fixture fixacao = corpo.createFixture(shape, 1);
        fixacao.setUserData(userData);
        return fixacao;
    }

}
