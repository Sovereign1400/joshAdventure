package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.testGame;

/**
 * The MainMenuScreen class represents the initial main menu of the Maze Runner game.
 * It provides options to start the game, view the story, view the credits, or exit.
 *
 * MENU BUTTONS:
 *  - START (goes to PlayScreen)
 *  - STORY (opens a sub-stage with story text)
 *  - CREDITS (opens a sub-stage with credits text)
 *  - EXIT (closes the game)
 */
public class MainMenuScreen implements Screen {

    private final testGame game;    // Reference to the core game class
    private Stage menuStage;        // The main menu stage
    private Stage storyStage;       // Sub-stage for "Story"
    private Stage creditsStage;     // Sub-stage for "Credits"
    private Stage activeStage;      // Which stage is currently shown (menuStage by default)

    private Skin skin;              // The UI skin

    public MainMenuScreen(testGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Load the skin from the game (assuming game.getSkin() returns a valid Skin)
        skin = game.getSkin();

        // Create the 3 stages
        menuStage = new Stage(new ScreenViewport());
        storyStage = new Stage(new ScreenViewport());
        creditsStage = new Stage(new ScreenViewport());

        // Build the menu UI
        createMenuStage();

        // Build the story sub-stage
        createStoryStage();

        // Build the credits sub-stage
        createCreditsStage();

        // Initially show the main menu stage
        activeStage = menuStage;

        // Accept input on the current stage
        Gdx.input.setInputProcessor(activeStage);
    }

    /**
     * Creates the main menu with 4 buttons: START, STORY, CREDITS, EXIT.
     */
    private void createMenuStage() {
        Table table = new Table();
        table.setFillParent(true);

        // Slightly fancy background (assuming "white" is a drawable in your skin)
        table.setBackground(skin.newDrawable("white", Color.FIREBRICK));

        // Default padding for each cell
        table.defaults().pad(15);

        menuStage.addActor(table);

        // Title
        Label titleLabel = new Label("Maze Runner", skin, "title");
        table.add(titleLabel).colspan(1).padBottom(30);
        table.row();

        // START button
        TextButton startButton = new TextButton("Start Game", skin);
        startButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Switch to the gameplay screen
                game.setScreen(new PlayScreen(game));
            }
        });
        table.add(startButton).width(200);
        table.row();

        // STORY button
        TextButton storyButton = new TextButton("Story", skin);
        storyButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Switch to storyStage
                activeStage = storyStage;
                Gdx.input.setInputProcessor(storyStage);
            }
        });
        table.add(storyButton).width(200);
        table.row();

        // CREDITS button
        TextButton creditsButton = new TextButton("Credits", skin);
        creditsButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Switch to creditsStage
                activeStage = creditsStage;
                Gdx.input.setInputProcessor(creditsStage);
            }
        });
        table.add(creditsButton).width(200);
        table.row();

        // EXIT button
        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        table.add(exitButton).width(200);
    }

    /**
     * Creates a sub-stage that shows the STORY text and has a BACK button.
     */
    private void createStoryStage() {
        Table table = new Table();
        table.setFillParent(true);
        table.setBackground(skin.newDrawable("white", Color.ROYAL));
        table.defaults().pad(15);

        storyStage.addActor(table);

        Label storyLabel = new Label(
            "Once upon a time...\n" +
                "A hero entered the labyrinth to find secrets...\n" +
                "This is the story introduction.",
            skin
        );
        // Make the text wrap if necessary
        storyLabel.setWrap(true);
        table.add(storyLabel).width(400);
        table.row();

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Return to main menu stage
                activeStage = menuStage;
                Gdx.input.setInputProcessor(menuStage);
            }
        });
        table.add(backButton).center();
    }

    /**
     * Creates a sub-stage that shows the CREDITS text and has a BACK button.
     */
    private void createCreditsStage() {
        Table table = new Table();
        table.setFillParent(true);
        table.setBackground(skin.newDrawable("white", Color.FOREST));
        table.defaults().pad(15);

        creditsStage.addActor(table);

        Label creditsLabel = new Label(
            "Created by:\n" +
                " - Awesome Dev\n" +
                " - Best Artist\n" +
                " - Music by Skilled Composer",
            skin
        );
        creditsLabel.setWrap(true);
        table.add(creditsLabel).width(400);
        table.row();

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Return to main menu
                activeStage = menuStage;
                Gdx.input.setInputProcessor(menuStage);
            }
        });
        table.add(backButton).center();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update & draw whichever stage is active
        activeStage.act(delta);
        activeStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        menuStage.getViewport().update(width, height, true);
        storyStage.getViewport().update(width, height, true);
        creditsStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        menuStage.dispose();
        storyStage.dispose();
        creditsStage.dispose();
    }
}
