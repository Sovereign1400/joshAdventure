package io.github.some_example_name;

import Screens.MainMenuScreen;
import Screens.PlayScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.audio.Music;

/**
 * basically the core game class. does screen transitions,
 * loads music, manages a shared SpriteBatch, and sets up a default skin
 * This is where the application starts
 */
/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class testGame extends Game {
    public static final int win_width = 1280;
    public static final int win_height = 720;
    public SpriteBatch batch;
    public static final float PPM = 32f;
    private Skin skin;
    public Music mainMenuMusic;
    public Music playMusic;
    public Music pauseMusic;
    public Music victoryMusic;
    public Music gameOverMusic;

    /**
     * called upon app launch
     * sets up window size, loads music, switches o mainmenu screen
     */
    @Override
    public void create() {

        // Set window size
        Gdx.graphics.setWindowedMode(win_width, win_height);


        batch = new SpriteBatch();

//        gameMap = new TiledGameMap();
        mainMenuMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/mainmenuBG.mp3"));
        playMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/gameplayBG.mp3"));
        pauseMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/pauseBG.mp3"));
        victoryMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/victoryBG.mp3"));
        gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/gameoverBG.mp3"));

        skin = getSkin();
        setScreen(new MainMenuScreen(this));

    }

    /**
     * the main render loop which delegates the active screens render
     */
    @Override
    public void render() {
        super.render();
    }

    /**
     * called when the game is closed
     * disposes batch and music o save resources
     */
    @Override
    public void dispose() {
        batch.dispose();
        mainMenuMusic.dispose();
        playMusic.dispose();
        pauseMusic.dispose();
        victoryMusic.dispose();
        gameOverMusic.dispose();
        super.dispose();
    }

    /**
     * this is the method used to gather the skin, which was used by the UI of all menu screens.
     * @return A LibGDX Skin loaded from "Skin123/pixthulhu-ui.json".
     */
    public Skin getSkin() {
        return new Skin(Gdx.files.internal("Skin123/pixthulhu-ui.json"));
    }
}
