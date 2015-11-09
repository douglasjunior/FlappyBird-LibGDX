package br.grupointegrado.ads.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import static br.grupointegrado.ads.flappyBird.Util.ALTURA_TELA;

/**
 * Created by Douglas on 22/10/2015.
 */
public class TelaInicio extends TelaBase {

    private BitmapFont fonte;
    private Batch batch;
    private GlyphLayout textoCarregando;

    public TelaInicio(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        initFontes();
    }


    private void initFontes() {
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = (int) ((Gdx.graphics.getHeight() * 24f) / ALTURA_TELA);
        param.color = Color.WHITE;
        param.shadowColor = Color.BLACK;
        param.shadowOffsetX = (int) (2f * Gdx.graphics.getDensity());
        param.shadowOffsetY = (int) (2f * Gdx.graphics.getDensity());

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));

        fonte = generator.generateFont(param);

        generator.dispose();

        textoCarregando = new GlyphLayout(fonte, "Carregando...");
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        fonte.draw(batch, textoCarregando, Gdx.graphics.getWidth() / 2 - textoCarregando.width / 2, Gdx.graphics.getHeight() / 2 - textoCarregando.height / 2);
        batch.end();

        iniciarJogo();
    }

    private void iniciarJogo() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                game.setScreen(new TelaJogo(game));
            }
        });
    }

    @Override
    public void resize(int width, int height) {

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
        fonte.dispose();
    }
}
