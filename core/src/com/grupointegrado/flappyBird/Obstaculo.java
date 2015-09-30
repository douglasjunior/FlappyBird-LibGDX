package com.grupointegrado.flappyBird;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import static com.grupointegrado.flappyBird.Constantes.PIXELS;
import static com.grupointegrado.flappyBird.Passaro.DIAMETRO_PASSARO;

/**
 * Created by douglas on 30/09/15.
 */
public class Obstaculo {

    public static final String OBSTACULO_CIMA = "OBSTACULO_CIMA";
    public static final String OBSTACULO_BAIXO = "OBSTACULO_BAIXO";

    private final World mundo;
    private final OrthographicCamera camera;
    private float x, yCima, yBaixo;
    private Body corpoCima;
    private Body corpoBaixo;
    private float largura, alturaCima, alturaBaixo;

    public Obstaculo(World mundo, OrthographicCamera camera) {
        this.mundo = mundo;
        this.camera = camera;

        initPosicao();
        initCorpoCima();
        initCorpoBaixo();
    }

    private void initPosicao() {
        x = (camera.viewportWidth / PIXELS)  / 2;
        yCima = camera.viewportHeight / Constantes.PIXELS;
        yBaixo = 0;
        largura = 10 / PIXELS;
        alturaCima = camera.viewportHeight / Constantes.PIXELS / 2 - DIAMETRO_PASSARO * 1.2f;
        alturaBaixo = camera.viewportHeight / Constantes.PIXELS / 2 - DIAMETRO_PASSARO * 1.2f;
    }

    private void initCorpoCima() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(largura, alturaCima);
        corpoCima = Util.criarCorpo(mundo, BodyDef.BodyType.StaticBody, x, yCima);
        Util.criarForma(corpoCima, shape, OBSTACULO_CIMA);
        shape.dispose();
    }

    private void initCorpoBaixo() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(largura, alturaBaixo);
        corpoBaixo = Util.criarCorpo(mundo, BodyDef.BodyType.StaticBody, x, yBaixo);
        Util.criarForma(corpoBaixo, shape, OBSTACULO_BAIXO);
        shape.dispose();
    }

    public void remover(){
        mundo.destroyBody(corpoCima);
        mundo.destroyBody(corpoBaixo);
    }
}
