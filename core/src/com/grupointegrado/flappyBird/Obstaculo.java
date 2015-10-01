package com.grupointegrado.flappyBird;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import static com.grupointegrado.flappyBird.Constantes.PIXELS_METRO;
import static com.grupointegrado.flappyBird.Passaro.DIAMETRO_PASSARO;

/**
 * Created by douglas on 30/09/15.
 */
public class Obstaculo {

    public static final String OBSTACULO_CIMA = "OBSTACULO_CIMA";
    public static final String OBSTACULO_BAIXO = "OBSTACULO_BAIXO";

    private final World mundo;
    private final OrthographicCamera camera;
    private final Obstaculo ultimoObstaculo;
    private float x, yCima, yBaixo;
    private Body corpoCima, corpoBaixo;
    private float largura, altura;
    private boolean passou = false;

    public Obstaculo(World mundo, OrthographicCamera camera, Obstaculo ultimoObstaculo) {
        this.mundo = mundo;
        this.camera = camera;
        this.ultimoObstaculo = ultimoObstaculo;

        initPosicao();
        initCorpoCima();
        initCorpoBaixo();
    }

    private void initPosicao() {
        largura = 20 / PIXELS_METRO;
        altura = (camera.viewportHeight / PIXELS_METRO);

        float inicialX = 0;
        if (ultimoObstaculo != null)
            inicialX = ultimoObstaculo.getX();
        x = inicialX + (camera.viewportWidth / 2) / PIXELS_METRO;

        float parcela = altura / 6;

        int multiplicador = MathUtils.random(1, 4);

        yBaixo = parcela * multiplicador - altura / 2;
        yCima = yBaixo + altura + DIAMETRO_PASSARO * 2f;
    }

    private void initCorpoCima() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(largura / 2, altura / 2);
        corpoCima = Util.criarCorpo(mundo, BodyDef.BodyType.StaticBody, x, yCima);
        Util.criarForma(corpoCima, shape, OBSTACULO_CIMA);
        shape.dispose();
    }

    private void initCorpoBaixo() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(largura / 2, altura / 2);
        corpoBaixo = Util.criarCorpo(mundo, BodyDef.BodyType.StaticBody, x, yBaixo);
        Util.criarForma(corpoBaixo, shape, OBSTACULO_BAIXO);
        shape.dispose();
    }

    public void remover() {
        mundo.destroyBody(corpoCima);
        mundo.destroyBody(corpoBaixo);
    }

    public float getX() {
        return x;
    }

    public float getyCima() {
        return yCima;
    }

    public float getyBaixo() {
        return yBaixo;
    }

    public Body getCorpoCima() {
        return corpoCima;
    }

    public Body getCorpoBaixo() {
        return corpoBaixo;
    }

    public float getLargura() {
        return largura;
    }

    public float getAltura() {
        return altura;
    }

    public boolean isPassou() {
        return passou;
    }

    public void setPassou(boolean passou) {
        this.passou = passou;
    }
}
