package Screens;


import Scenes.HUD;
import Sprites.Josh;
import Sprites.Monster;
import Tools.B2WorldCreator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.testGame;
import com.badlogic.gdx.utils.Array;

public class PlayScreen implements Screen {
    private testGame game;
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private HUD hud;

    // Tile Map attributes
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d attributes
    private World world;
    private Box2DDebugRenderer b2dr;
    private Josh player;
    private Array<Monster> monsters;



    public PlayScreen(testGame game) {
        this.game = game;


        // This camera follows the main character.
        gamecam = new OrthographicCamera();

        // This zooms in / out the camera
        gamecam.zoom = 0.01f;

        // This creates a viewport that stabilize virtual aspect ratio.
        gamePort = new FitViewport(testGame.win_width, testGame.win_height, gamecam);

        // This creates the HUD for scoreboard and other data shown on screen.
        hud = new HUD(game.batch);

        // This loads the map.
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("tileset/customMap2.tmx");

        // This renders the map.
        renderer = new OrthogonalTiledMapRenderer(map, (float) 1 / testGame.PPM);

//        // This centers the camera to the center of the screen instead of (0,0).
//        gamecam.position.set((float) gamePort.getScreenWidth() / 2, (float) gamePort.getScreenHeight() / 2, 0);

        // This set camera angle start with a specific coordinates.
        gamecam.position.set((float) 360, (float) 360, 0);

        // This is for box2D world
        world = new World(new Vector2(0, (float) 0 / testGame.PPM), true); // This set gravity to 0.
        b2dr = new Box2DDebugRenderer();

        new B2WorldCreator(world, map);

        // This initialize Josh.
        player = new Josh(world);

        //This creates a list of Monsters for management of Monsters.
        monsters = new Array<Monster>();

        // This initialize the Monster
        Texture monsterTexture = new Texture("monster.png");

        MapLayer monsterSpawnLayer = map.getLayers().get("MonsterSpawns");
        Rectangle bounds = null;
        if (monsterSpawnLayer != null) {
            for (MapObject object : monsterSpawnLayer.getObjects()) {
                bounds = ((RectangleMapObject) object).getRectangle();
                monsters.add(new Monster(world, map, bounds, monsterTexture, 2.0f)); // Speed = 2.0f
            }
        }
        // go to update methods

    }

    @Override
    public void show() {

    }

    public void handleInput(float dt) {

            float moveSpeed = 5f; // normal walking speed
            boolean upPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
            boolean downPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);
            boolean leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
            boolean rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

            // Example SHIFT key for running
            boolean shiftPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

            // Decide stance (walk or run) if any movement key is pressed
            if (upPressed || downPressed || leftPressed || rightPressed) {
                if (shiftPressed) {
                    // RUN stance
                    player.setStance(Josh.Stance.RUN);
                    moveSpeed = 8f; // faster speed for running
                } else {
                    // WALK stance
                    player.setStance(Josh.Stance.WALK);
                }
            } else {
                // No movement keys pressed => STAND
                player.setStance(Josh.Stance.STAND);
            }

            // Determine facing direction
            if (leftPressed) {
                player.facingLeft = false;
            } else if (rightPressed) {
                player.facingLeft = true;
            }

            // Now set Box2D velocity based on pressed keys
            float vx = 0f;
            float vy = 0f;

            if (upPressed) {
                vy = moveSpeed;
            } else if (downPressed) {
                vy = -moveSpeed;
            }

            if (leftPressed) {
                vx = -moveSpeed;
            } else if (rightPressed) {
                vx = moveSpeed;
            }

            player.b2body.setLinearVelocity(vx, vy);
    }

    //This method updates data in the game, dt = delta, a small period of time.
    public void update(float dt){

        // This checks if user has given any inputs.
        handleInput(dt);

        world.step(1 / 60f, 6, 2); // Update physics world
        player.update(dt); // Update player state
        gamecam.position.x = player.b2body.getPosition().x; // Follow the player
        gamecam.update();

        // This tracks the game camera to the player
        gamecam.position.set(player.b2body.getPosition().x, player.b2body.getPosition().y, 0);

        // This updates the camera position.
        gamecam.update();

        // This tells the game cam what to render.
        renderer.setView(gamecam);

        //This updates monster position
        for (Monster monster : monsters) {
            monster.update(dt, player.getPosition()); // Assume player.getPosition() returns a Vector2
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        // This fills the background of the screen with black.
        Gdx.gl.glClearColor(0, 0,0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // This renders the map
        renderer.render();

        // This renders Box2DDebugLines, comment it out if not debugging
        b2dr.render(world, gamecam.combined);

        // Set our batch to now draw what HUD cam sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        // This renders the whole game batch
        game.batch.setProjectionMatrix(gamecam.combined); // Set the projection for game world
        game.batch.begin(); // Start batch

        // This renders Josh, the main player
        player.draw(game.batch); // Draw player

        // This renders Monster.
        for (Monster monster : monsters) {
            monster.render(game.batch);
        }

        game.batch.end(); // End batch

    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
