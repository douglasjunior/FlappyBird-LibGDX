package br.grupointegrado.ads.flappyBird;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import static br.grupointegrado.ads.flappyBird.Util.ALTURA_CHAO;
import static br.grupointegrado.ads.flappyBird.Util.PIXELS_METRO;


/**
 * Created by douglas on 30/09/15.
 */
public class Obstaculo {

    public static final String OBSTACULO_CIMA = "OBSTACULO_CIMA";
    public static final String OBSTACULO_BAIXO = "OBSTACULO_BAIXO";

    private final World mundo;
    private final OrthographicCamera camera;
    private final Obstaculo ultimoObstaculo;
    private final Texture texturaObstaculoCima;
    private final Texture texturaObstaculoBaixo;
    private float x, yCima, yBaixo;
    private Body corpoCima, corpoBaixo;
    private float largura, altura;
    private boolean passou = false;

    public Obstaculo(World mundo, OrthographicCamera camera, Obstaculo ultimoObstaculo, Texture texturaObstaculoCima, Texture texturaObstaculoBaixo) {
        this.mundo = mundo;
        this.camera = camera;
        this.ultimoObstaculo = ultimoObstaculo;
        this.texturaObstaculoCima = texturaObstaculoCima;
        this.texturaObstaculoBaixo = texturaObstaculoBaixo;

        initPosicao();
        initCorpoCima();
        initCorpoBaixo();
    }

    private void initPosicao() {
        largura = 40 / PIXELS_METRO;
        altura = (camera.viewportHeight / PIXELS_METRO);

        float inicialX = largura;
        if (ultimoObstaculo != null)
            inicialX = ultimoObstaculo.getX();
        x = inicialX + 4;

        float parcela = (altura - ALTURA_CHAO) / 6;

        int multiplicador = MathUtils.random(1, 3);

        yBaixo = ALTURA_CHAO + (parcela * multiplicador) - (altura / 2);
        yCima = yBaixo + altura + 2f; // 2f espaço entre os canos
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

    public void renderizar(SpriteBatch batch) {
        float x = (corpoCima.getPosition().x - largura / 2) * PIXELS_METRO;
        float y = (corpoCima.getPosition().y - altura / 2) * PIXELS_METRO;
        batch.draw(texturaObstaculoCima, x, y, largura * PIXELS_METRO, altura * PIXELS_METRO);

        x = (corpoBaixo.getPosition().x - largura / 2) * PIXELS_METRO;
        y = (corpoBaixo.getPosition().y - altura / 2) * PIXELS_METRO;
        batch.draw(texturaObstaculoBaixo, x, y, largura * PIXELS_METRO, altura * PIXELS_METRO);
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
