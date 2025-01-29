package Screens;

import Scenes.HUD;
import Sprites.*;
import Tools.B2WorldCreator;
import Tools.WorldContactListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.testGame;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class PlayScreen implements Screen {
    private testGame game;
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private HUD hud;
    private boolean gameOver = false;
    private boolean paused = false;  //to keep track of the game state (paused or not)
    private com.badlogic.gdx.audio.Sound runningSound;
    private long runningSoundId = -1;         //to loop
    private boolean isRunningSoundPlaying = false;
    private com.badlogic.gdx.audio.Sound buttonSound;

    // Tile Map attributes
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private String currentMapPath;

    //Box2d attributes
    private World world;
    private Box2DDebugRenderer b2dr;
    private Josh player;

    // Monster Attributes
    private Array<Monster> monsters;

    // Trap attributes
    private Array<Trap> traps;

    // Fog of War Attributes
    private FrameBuffer fbo;
    private TextureRegion fboRegion;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private static final int DEFAULT_SRC_FUNC = GL20.GL_SRC_ALPHA;
    private static final int DEFAULT_DST_FUNC = GL20.GL_ONE_MINUS_SRC_ALPHA;


    // Heart Attributes:
    private Array<Heart> hearts;

    // Speedup Attributes:
    private Array<Speedup> speedups;

    // Shield Attributes:
    private Array<Shield> shields;

    // Key Attributes
    private Array<Key> keys;

    // Door Attributes
    private Array<Door> doors;
    public boolean showDoorMessage = false;
    public float messageTimer = 0;
    private final float MESSAGE_DURATION = 2f;
    private BitmapFont customFont;
    private boolean shouldLoadNextMap = false;
    public float gameTime = 0;

    // Arrow Attribute
    private DirectionalArrow directionalArrow;


    // Ghost Attribute
    private Array<Ghost> ghosts;


    // Score system
    public static final int HEART_SCORE = 200;
    public static final int SPEEDUP_SCORE = 200;
    public static final int KEY_SCORE = 500;
    public static final int SHIELD_SCORE = 150;






    public PlayScreen(testGame game) {
        this(game, "tileset/customMap_2.tmx");
    }

    public PlayScreen(testGame game, String mapPath) {
        this.game = game;


        // This camera follows the main character.
        gamecam = new OrthographicCamera();
        // This zooms in / out the camera
        gamecam.zoom = 1f;
        // This creates a viewport that stabilize virtual aspect ratio.
        gamePort = new FitViewport(16, 9, gamecam);

        // Typically you have a SpriteBatch around for drawing everything else
        batch = new SpriteBatch();

        // This loads the map.
                this.currentMapPath = mapPath;
                mapLoader = new TmxMapLoader();
                map = mapLoader.load(mapPath);

        // This is for box2D world
        world = new World(new Vector2(0, (float) 0 / testGame.PPM), true); // This set gravity to 0.
        b2dr = new Box2DDebugRenderer();

        // This creates Josh.
        // Where Josh is created:
        Vector2 spawnPoint = getEnterDoorPosition();
        System.out.println("Spawning player at door: " + spawnPoint.x + ", " + spawnPoint.y);

        // Pass 'this' as the PlayScreen reference
        player = new Josh(world, spawnPoint.x, spawnPoint.y);

        // This creates the HUD for scoreboard and other data shown on screen.
        hud = new HUD(game.batch, player);
        world.setContactListener(new WorldContactListener(player, this));
        // This renders the map.
        renderer = new OrthogonalTiledMapRenderer(map, (float) 1 / testGame.PPM);


        // Update the HUD with initial world level
        hud.updateWorldLevel(mapPath);

        // This loads the fonts
        customFont = new BitmapFont(Gdx.files.internal("fonts/colorBasic.fnt"));
        customFont.getData().setScale(1f/testGame.PPM);

        // This initializes arrow
        directionalArrow = new DirectionalArrow(player);


//        // This centers the camera to the center of the screen instead of (0,0).
//        gamecam.position.set((float) gamePort.getScreenWidth() / 2, (float) gamePort.getScreenHeight() / 2, 0);

//        // This set camera angle start with a specific coordinates.
//        gamecam.position.set((float) 360, (float) 360, 0);

        B2WorldCreator creator = new B2WorldCreator(world, map);

        // Please go to update methods - Wong

        // ---------------------------------------
        // This sets fogs of war to main character
        //----------------------------------------
        // Force the viewport to initialize
        gamePort.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        int viewW = gamePort.getScreenWidth();
        int viewH = gamePort.getScreenHeight();

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, viewW, viewH, false);
        fboRegion = new TextureRegion(fbo.getColorBufferTexture());
//        fboRegion.flip(false, true);

        shapeRenderer = new ShapeRenderer();

        // ---------------------------------------
        // This sets Monsters
        //----------------------------------------

        // Initialize the monsters array and traps
        monsters = new Array<Monster>();
        traps = new Array<>();
        createTraps();

        // Create monsters at specific positions
        createMonsters();

        // This sets all interactive objects
        speedups = new Array<>();
        hearts = new Array<>();
        shields = new Array<>();
        keys = new Array<>();

        speedups = creator.createSpeedups(world, map);
        hearts = creator.createHearts(world, map);
        shields = creator.createShields(world, map);
        keys = creator.createKeys(world, map);
        doors = creator.createDoors(world, map); // This creates door on the map.
        ghosts = creator.createGhosts(world, map);

        runningSound = Gdx.audio.newSound(Gdx.files.internal("audio/runningloop.mp3")); // loads the sfx!!
        buttonSound = Gdx.audio.newSound(Gdx.files.internal("audio/buttonSFX.mp3"));
    }

    private void updateDirectionalArrow() {
        if (directionalArrow != null) {
            Door exitDoor = null;
            float minDistance = Float.MAX_VALUE;
            Vector2 playerPos = player.getPosition();

            // Find the nearest exit door
            for (Door door : doors) {
                if (door.getDoorType() == DoorType.EXIT) {
                    float doorX = (door.getBoundsX() + door.getBoundsWidth() / 2) / testGame.PPM;
                    float doorY = (door.getBoundsY() + door.getBoundsHeight() / 2) / testGame.PPM;
                    float distance = Vector2.dst(playerPos.x, playerPos.y, doorX, doorY);

                    if (distance < minDistance) {
                        minDistance = distance;
                        exitDoor = door;
                    }
                }
            }

            if (exitDoor != null) {
                directionalArrow.update(exitDoor);
            }
        }
    }

    private Vector2 getEnterDoorPosition() {
        // Default spawn point in case nothing is found
        Vector2 defaultSpawn = new Vector2(120f, 200f);

        try {
            // Check if the enterDoor layer exists
            if (map.getLayers().get("enterDoor") != null) {
                for (MapObject object : map.getLayers().get("enterDoor").getObjects()) {
                    if (object instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) object).getRectangle();
                        // Add a small offset to position Josh in front of the door
                        float offsetX = 48f; // Adjust this value based on your door width
                        float offsetY = 0f;  // Adjust if needed
                        return new Vector2(rect.x + offsetX, rect.y + offsetY);
                    }
                }
            }

            System.out.println("No enter door found in map, using default spawn point");
            return defaultSpawn;

        } catch (Exception e) {
            System.err.println("Error finding enter door position: " + e.getMessage());
            e.printStackTrace();
            return defaultSpawn;
        }
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt) {
            float moveSpeed = player.getBasemovespeed(); // normal walking speed
            boolean upPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
            boolean downPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);
            boolean leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
            boolean rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

            // Example SHIFT key for running
            boolean shiftPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

            // Calculate current move speed
            float currentSpeed = player.getMovespeed();

             if (upPressed || downPressed || leftPressed || rightPressed) {
                currentSpeed = player.getMovespeed();
                if (shiftPressed) {
                 player.setStance(Josh.Stance.RUN);
                    currentSpeed *= 1.2f;
                } else {
                    player.setStance(Josh.Stance.WALK);
              }
            } else {
                    player.setStance(Josh.Stance.STAND);
                }

            // This part handles stances and movement registration. (Death and Hurt, mainly)
            if (player.isDead()) {
                player.b2body.setLinearVelocity(0, 0);
                return;  // Skip all input handling if dead
            }

            // Add attack input check
            if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
                player.attack();
            }

            // This part can be safely removed, but keep it for now
            // Decide stance (walk or run) if any movement key is pressed
            /*if (upPressed || downPressed || leftPressed || rightPressed) {
                if (shiftPressed) {
                    // RUN stance
                    player.setStance(Josh.Stance.RUN);
                    moveSpeed = player.getBasemovespeed() * 1.2f; // faster speed for running
                } else {
                    // WALK stance
                    player.setStance(Josh.Stance.WALK);
                }
            } else {
                // No movement keys pressed => STAND
                player.setStance(Josh.Stance.STAND);
            }*/

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
                vy = currentSpeed;
            } else if (downPressed) {
                vy = -currentSpeed;
            }

            if (leftPressed) {
                vx = -currentSpeed;
            } else if (rightPressed) {
                vx = currentSpeed;
            }

            player.b2body.setLinearVelocity(vx, vy);

        boolean isMoving = upPressed || downPressed || leftPressed || rightPressed;

        //this is to loop the sfx
        if (isMoving) {
            if (!isRunningSoundPlaying) {
                runningSoundId = runningSound.loop(0.5f);
                // 0.5f is volume; adjust as needed
                isRunningSoundPlaying = true;
            }
        } else {
            // if not moving, don't load the sfx
            if (isRunningSoundPlaying) {
                runningSound.stop(runningSoundId);
                isRunningSoundPlaying = false;
            }
        }
    }

    //This method updates data in the game, dt = delta, a small period of time.
    public void update(float dt){

        // This checks if user has given any inputs.
        handleInput(dt);

        world.step(1 / 60f, 6, 2); // Update physics world
        player.update(dt); // Update player state
        gamecam.position.x = player.b2body.getPosition().x; // Follow the player

        // This tracks the game camera to the player
        gamecam.position.set(player.b2body.getPosition().x, player.b2body.getPosition().y, 0);

        // This updates the camera position.
        gamecam.update();

        // This tells the game cam what to render.
        renderer.setView(gamecam);

        // This updates HUD
        hud.update(dt, false);

        // This renders the monster
        // Update all monsters
        for (Monster monster : monsters) {
            monster.update(dt);
        }

        for (Trap trap : traps) {
            trap.update(dt);
        }

        for(Heart heart : hearts) {
            heart.update();
        }

        for(Speedup speedup : speedups) {
            speedup.update();
        }

        for (Shield shield : shields){
            shield.update();
        }

        for (Key key : keys){
            key.update();
        }

        // This opens the gameOver screen once the game over part set to true.
        if (!gameOver) {
            handleInput(dt);
            world.step(dt, 6, 2);  // Use dt (in game time) or actual time.
            player.update(dt);

            if (player.isDead() && player.deathTimer >= player.deathDuration) {
                gameOver = true;
                game.setScreen(new GameOverScreen(game));
//                dispose();
            }
        }

        // This implements the doors.
        for (Door door : doors) {
            door.update();
        }

        if (showDoorMessage) {
            messageTimer += dt;
            if (messageTimer >= MESSAGE_DURATION) {
                showDoorMessage = false;
                messageTimer = 0;
            }
        }
        // Skip updates if loads next map.
        if (shouldLoadNextMap) {
            return;
        }

        if (!gameOver && !shouldLoadNextMap) {
            gameTime += dt;
        }

        for (Ghost ghost : ghosts) {
            ghost.update(dt);
        }

        updateDirectionalArrow();

        // Optional: Check for collisions between player and monsters
//        checkMonsterCollisions();


    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // Switch to PauseMenuScreen, passing a reference to THIS screen
            game.setScreen(new PauseMenuScreen(game, this));
            runningSound.stop(runningSoundId);
            isRunningSoundPlaying = false;
            return;
        }
        // If paused, skip update logic
        if (!paused) {
            update(delta);
        }

        // This fills the background of the screen with black.
        Gdx.gl.glClearColor(0, 0,0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // This renders the map
        renderer.render();

        // This renders Box2DDebugLines, comment it out if not debugging
        //b2dr.render(world, gamecam.combined);

        // Set our batch to now draw what HUD cam sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        // This renders the whole game batch
        game.batch.setProjectionMatrix(gamecam.combined); // Set the projection for game world
        game.batch.begin(); // Start batch

        // This renders an array of traps.
        for (Trap trap : traps) {
            trap.draw(game.batch);
        }
        // This renders Josh, the main player
        player.draw(game.batch); // Draw player

        // This renders an array of hearts
        for(Heart heart : hearts) {
            if(!heart.isCollected()) {
                heart.draw(game.batch);
            }
        }

        // This renders an array of monsters
        for (Monster monster : monsters) {
            monster.draw(game.batch);
        }



        // This renders an array of speedups
        for(Speedup speedup : speedups) {
            if(!speedup.isCollected()) {
                speedup.draw(game.batch);
            }
        }

        // This renders an array of shields
        for (Shield shield : shields){
            if (!shield.isCollected()){
                shield.draw(game.batch);
            }
        }

        // This renders an array of keys
        for (Key key : keys){
            if (!key.isCollected()){
                key.draw(game.batch);
            }
        }

        // This renders the heart
        for (Door door : doors) {
            door.draw(game.batch);
        }

        // This renders the door
        if (showDoorMessage) {
            customFont.draw(game.batch,
                "You don't have the key!",
                player.b2body.getPosition().x - 1f,
                player.b2body.getPosition().y + 1f);
        }

        for (Ghost ghost : ghosts) {
            ghost.draw(game.batch);
        }

        directionalArrow.draw(game.batch);

        game.batch.end(); // End batch

//        debugDrawShapes();

        // This renders fog of war using the same batch but the second time.
         renderFogOfWar();

        // Set our batch to now draw what HUD cam sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

    }

    private void createMonsters() {
//        // You can create monsters at specific positions
//        addMonster(200, 200);  // First monster
//        addMonster(400, 300);  // Second monster
//        addMonster(600, 400);  // Third monster

        // Alternative: If you want to create monsters from map objects
        // Assuming you have a "Monsters" layer in your Tiled map

        for (MapObject object : map.getLayers().get("monsterSpawns").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                addMonster(rect.x, rect.y);
            }
        }
    }

    private void addMonster(float x, float y) {
        Monster monster = new Monster(world, x, y, player);
        monsters.add(monster);
    }

    private void debugDrawShapes() {
        // 1) We’ll draw directly to the screen (no FBO)
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 2) Window dimensions (ignore letterboxing)
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        // 3) Disable any leftover depth/scissor
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);

        // 4) Simple 2D projection from (0,0) to (w,h)
        shapeRenderer.setProjectionMatrix(
            new Matrix4().setToOrtho2D(0, 0, w, h)
        );

        // 5) Enable normal alpha blending
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

//        // 6) Draw a half‐transparent black rectangle to fill the whole window
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(255, 255, 0, 0.1f);
//        shapeRenderer.rect(0, 0, w, h);
//        shapeRenderer.end();

        // 7) Draw a bright red circle in the exact window center
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 0, 0, 1);  // full opaque red
        shapeRenderer.circle(w / 2f, h / 2f, 100f);
        shapeRenderer.end();
    }

    private void createTraps() {
        for (MapObject object : map.getLayers().get("trap").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                addTrap(rect.x, rect.y);
            }
        }
    }

    private void addTrap(float x, float y) {
        Trap trap = new Trap(world, x, y);
        traps.add(trap);
    }

    private void renderFogOfWar() {
        int viewW = gamePort.getScreenWidth();
        int viewH = gamePort.getScreenHeight();

        Vector3 screenPos = new Vector3(player.b2body.getPosition().x * testGame.PPM,
            player.b2body.getPosition().y * testGame.PPM, 0);
        gamecam.project(screenPos,
            gamePort.getScreenX(),
            gamePort.getScreenY(),
            viewW,
            viewH
        );

        fbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(gamecam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.1f);

        float worldWidth = gamecam.viewportWidth * 2;
        float worldHeight = gamecam.viewportHeight * 2;
        shapeRenderer.rect(
            gamecam.position.x - worldWidth/2,
            gamecam.position.y - worldHeight/2,
            worldWidth,
            worldHeight
        );

        Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);

        float baseRadius = 3f;
        int segments = 50;

        // Add blurred circles to the fog of war
        for (int i = 0; i < 10; i++) {
            float alpha = 1.0f - (i * 0.1f);
            shapeRenderer.setColor(0, 0, 0, alpha);
            float radius = baseRadius + (i * 0.1f);
            shapeRenderer.circle(
                player.b2body.getPosition().x,
                player.b2body.getPosition().y,
                radius,
                segments
            );
        }

        shapeRenderer.end();
        fbo.end();

        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        game.batch.begin();
        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.draw(
            fboRegion,
            gamecam.position.x - gamecam.viewportWidth/2,
            gamecam.position.y - gamecam.viewportHeight/2,
            gamecam.viewportWidth,
            gamecam.viewportHeight
        );
        game.batch.end();
    }

//    public void loadNextMap() {
//        System.out.println("Loading next map..."); // Debug print
//        if (currentMapPath.contains("customMap_2")) {
//            dispose();  // Clean up current resources
//            System.out.println("Map 2 disposed");
//            System.out.println("Creating new screen for customMap3...");
//            PlayScreen nextScreen = new PlayScreen(game, "tileset/customMap_3.tmx");
//            System.out.println("Setting new screen...");
//            game.setScreen(nextScreen);
//            System.out.println("Switched to Map 3");  // Debug print
//        }
//    }

    // Score system
    public void addScore(int amount) {
        hud.addScore(amount);
    }

    public void loadNextMap() {
        try {
            System.out.println("Loading next map from: " + currentMapPath);
            if (currentMapPath.contains("customMap_2")) {
                // Set a flag to prevent physics updates
                shouldLoadNextMap = true;

                // Clear all bodies from the world
                Array<Body> bodies = new Array<Body>();
                world.getBodies(bodies);
                for(Body body : bodies) {
                    world.destroyBody(body);
                }

                // Create and switch to new screen
                PlayScreen nextScreen = new PlayScreen(game, "tileset/customMap_3.tmx");
                nextScreen.hud.addScore(hud.getScore()); // Transfer current score

                // Use Gdx.app.postRunnable to ensure screen switch happens on render thread
                Gdx.app.postRunnable(() -> {
                    dispose();  // Clean up current resources
                    game.setScreen(nextScreen);
                    System.out.println("Switched to Map 3");
                });
            } else if (currentMapPath.contains("customMap_3")) {
                // Set a flag to prevent physics updates
                shouldLoadNextMap = true;

                // Clear all bodies from the world
                Array<Body> bodies = new Array<Body>();
                world.getBodies(bodies);
                for(Body body : bodies) {
                    world.destroyBody(body);
                }

                // Create and switch to new screen with Map 4
                PlayScreen nextScreen = new PlayScreen(game, "tileset/customMap_4.tmx");
                nextScreen.hud.addScore(hud.getScore()); // Transfer current score

                // Use Gdx.app.postRunnable to ensure screen switch happens on render thread
                Gdx.app.postRunnable(() -> {
                    dispose();  // Clean up current resources
                    game.setScreen(nextScreen);
                    System.out.println("Switched to Map 4");
                });
            }
        } catch (Exception e) {
            System.err.println("Error loading next map:");
            e.printStackTrace();
            throw e;
        }
    }

    public Array<Monster> getMonsters() {
        return monsters;
    }


    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        int viewW = gamePort.getScreenWidth();
        int viewH = gamePort.getScreenHeight();

        // Avoid zero or negative dimensions
        if (viewW < 1) viewW = 1;
        if (viewH < 1) viewH = 1;

        // Dispose old FBO if it exists
        if (fbo != null) {
            fbo.dispose();
        }

        // Create the FBO with the updated size
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, viewW, viewH, false);
        fboRegion = new TextureRegion(fbo.getColorBufferTexture());
//        fboRegion.flip(false, true);
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    // Add getter for game instance
    public testGame getGame() {
        return game;
    }

    // Add getter for current map path
    public String getCurrentMapPath() {
        return currentMapPath;
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
        fbo.dispose();
        customFont.dispose();
        runningSound.dispose();

        // This dispose all monsters
        for (Monster monster : monsters) {
            monster.dispose();
        }

        for (Trap trap : traps) {
            trap.dispose();
        }

        if (directionalArrow != null) {
            directionalArrow.dispose();
        }

        for (Ghost ghost : ghosts) {
            ghost.dispose();
        }
    }

    public Josh getPlayer() {
        return player;
    }
}
