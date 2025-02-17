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

/**
 * The playscreen class is where the main game takes place
 * it loads a tiled map, places the player, sets up Box2D physics,
 * manages item pickups, draws monsters, and handles overall game logic
 * like pausing, updating, and going to other screens (victory, etc.)
 */
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
    private com.badlogic.gdx.audio.Sound slashSound;
    private com.badlogic.gdx.audio.Sound deathSound;


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
    private Array<Scoreup> scoreups;

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
    public static final int SCORE_PER_SECOND = 5;

    // Camara zoom in/out
    private static final float MIN_ZOOM = 0.75f;
    private static final float MAX_ZOOM = 1.1f;
    private static final float ZOOM_SPEED = 0.1f;
    private static final float DEFAULT_ZOOM = 1f;
    private float currentZoom = DEFAULT_ZOOM;

    /**
     * creates a playscreen using a map ("customMap_2.tmx")
     * @param game , holds resources and helps screen changes
     */
    public PlayScreen(testGame game) {
        this(game, "tileset/customMap_2.tmx");
    }

    /**
     * this one creates a playscreen for a map path and sets up the camera,
     * Box2D world, tiled map, and the player
     * also loads music, SFX and other objects
     * @param game     the game initialization
     * @param mapPath  path to the tiled map
     */
    public PlayScreen(testGame game, String mapPath) {
        this.game = game;
        slashSound = Gdx.audio.newSound(Gdx.files.internal("audio/joshsword.mp3"));
        deathSound = Gdx.audio.newSound(Gdx.files.internal("audio/deathSFX.mp3"));


        // Create camera with larger viewport dimensions for better scaling
        gamecam = new OrthographicCamera();
        // Use a larger viewport size (adjust these values based on your game's needs)
        gamePort = new FitViewport(16, 9, gamecam);  // 16:9 aspect ratio but larger
        gamecam.zoom = currentZoom;

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


        // This sets fogs of war to main character
        // Force the viewport to initialize
        gamePort.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        int viewW = gamePort.getScreenWidth();
        int viewH = gamePort.getScreenHeight();

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, viewW, viewH, false);
        fboRegion = new TextureRegion(fbo.getColorBufferTexture());
//        fboRegion.flip(false, true);

        shapeRenderer = new ShapeRenderer();

        // This sets Monsters

        // Initialize the monsters array and traps
        monsters = new Array<Monster>();
        traps = new Array<>();
        createTraps();

        // Create monsters at specific positions
        createMonsters();

        // This sets all interactive objects
        speedups = new Array<>();
        hearts = new Array<>();
        scoreups = new Array<>();
        keys = new Array<>();

        speedups = creator.createSpeedups(world, map);
        hearts = creator.createHearts(world, map);
        scoreups = creator.createScoreup(world, map);
        keys = creator.createKeys(world, map);
        doors = creator.createDoors(world, map); // This creates door on the map.
        ghosts = creator.createGhosts(world, map);


        runningSound = Gdx.audio.newSound(Gdx.files.internal("audio/runningloop.mp3")); // loads the sfx!!
        buttonSound = Gdx.audio.newSound(Gdx.files.internal("audio/buttonSFX.mp3"));
    }

    /**
     * finds the nearest exit door and updates the directional arrow to point towards it
     */
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

    /**
     * finds a door object in the Tiled map named enterDoor and returns a spawn position near it
     * If none is found, defaults to (120,200) to avoid conflict
     * @return determines the position the player should spawn
     */
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

    /**
     * called when this screen is shown
     * stops the main menu music, starts the gameplay music.
     */
    @Override
    public void show() {
        game.mainMenuMusic.stop();
        game.playMusic.setLooping(true);
        game.playMusic.play();
        game.playMusic.setVolume(0.1f);


    }

    /**
     * Processes input for movement, attacking, and camera zoom each frame
     * @param dt delta time since last frame in seconds
     */
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
                runningSound.pause();
                isRunningSoundPlaying = false;
                deathSound.play(0.2F);
                return;  // Skip all input handling if dead
            }


            // Zoom handling
            handleZoomInput(dt);

            // Add attack input check
            if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
                player.attack();
                if (!player.isDead()){
                    slashSound.play(0.2F);
                }
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
                runningSoundId = runningSound.loop(0.15f);
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

    /**
     * Allows the player to zoom in with +, out with - and resets zoom with R key
     * @param dt delta time here to adjust how quickly we zoom
     */
    private void handleZoomInput(float dt) {
        // Zoom in with NumPad + or regular +
        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_ADD) ||
            Gdx.input.isKeyPressed(Input.Keys.PLUS)) {
            currentZoom = Math.max(MIN_ZOOM, currentZoom - ZOOM_SPEED * dt);
        }
        // Zoom out with NumPad - or regular -
        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_SUBTRACT) ||
            Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            currentZoom = Math.min(MAX_ZOOM, currentZoom + ZOOM_SPEED * dt);
        }
        // Reset zoom with R key
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            currentZoom = DEFAULT_ZOOM;
        }

        gamecam.zoom = currentZoom;
    }

    /**
     * updates everything in playscreen each frame thanks to dt (if not paused)
     * this includes input, physics, player, monsters, items, etc
     * @param dt delta time since last frame as usual
     */
    public void update(float dt){
        if (!paused) {
            handleInput(dt);
            // This checks if user has given any inputs.
            handleInput(dt);

            world.step(1 / 60f, 6, 2); // Update physics world
            player.update(dt); // Update player state
            gamecam.position.x = player.b2body.getPosition().x; // Follow the player

            gamecam.zoom = currentZoom;
            gamecam.update();

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

            for (Heart heart : hearts) {
                heart.update();
            }

            for (Speedup speedup : speedups) {
                speedup.update();
            }

            for (Scoreup scoreup : scoreups) {
                scoreup.update();
            }

            for (Key key : keys) {
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


        }
    }

    /**
     * renders the entire game world, HUD, player, enemies, traps and fog of war
     * also checks for the pause key which is escape
     * @param delta the time since the last frame
     */
    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // Switch to PauseMenuScreen, passing a reference to THIS screen
            game.setScreen(new PauseMenuScreen(game, this));
            runningSound.stop(runningSoundId);
            isRunningSoundPlaying = false;
            game.playMusic.pause();
            game.setScreen(new PauseMenuScreen(game, this));
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
        for (Scoreup scoreup : scoreups){
            if (!scoreup.isCollected()){
                scoreup.draw(game.batch);
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

    /**
     * this one creates all monster sprites by reading positions from the Tiled maps monsterspawns layer
     */
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

    /**
     * this method creates a monster in a specific location
     * @param x X position in map space.
     * @param y Y position in map space.
     */
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

    /**
     * the method creates traps by reading positions from the Tiled map's trap layer this time
     */
    private void createTraps() {
        for (MapObject object : map.getLayers().get("trap").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                addTrap(rect.x, rect.y);
            }
        }
    }



    /**
     * adds a trap to a specific location
     * @param x X coordinate of the trap
     * @param y Y coordinate of the trap
     */
    private void addTrap(float x, float y) {
        Trap trap = new Trap(world, x, y);
        traps.add(trap);
    }



    /**
     * this method fully renders fog of war, so that the player doesnt see too far
     * scales the fog of war so that zooming out doesn't reveal more than intended
     */
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

        // Calculate a larger rectangle size based on camera zoom
        float zoomFactor = gamecam.zoom;
        float worldWidth = gamecam.viewportWidth * 4 * zoomFactor;
        float worldHeight = gamecam.viewportHeight * 4 * zoomFactor;

        shapeRenderer.rect(
            gamecam.position.x - worldWidth/2,
            gamecam.position.y - worldHeight/2,
            worldWidth,
            worldHeight
        );

        Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Scale the visible circle based on zoom
        float baseRadius = 3f * zoomFactor;  // Base radius scales with zoom
        int segments = 50;

        // Add blurred circles to the fog of war
        for (int i = 0; i < 10; i++) {
            float alpha = 1.0f - (i * 0.1f);
            shapeRenderer.setColor(0, 0, 0, alpha);
            float radius = baseRadius + (i * 0.1f * zoomFactor);  // Radius scales with zoom
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

        // Draw the fog texture to cover the entire visible area
        float fogWidth = gamecam.viewportWidth * zoomFactor;
        float fogHeight = gamecam.viewportHeight * zoomFactor;

        game.batch.draw(
            fboRegion,
            gamecam.position.x - fogWidth/2,
            gamecam.position.y - fogHeight/2,
            fogWidth,
            fogHeight
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

    /**
     * adds a certain amount of score to the hud (goes to victory screen too)
     * @param amount points that wll be added
     */
    public void addScore(int amount) {
        hud.addScore(amount);
    }

    /**
     * we call this when the player reaches another exit so another map needs to be loaded
     * preserves music position so that it continues where its left off
     */
    public void loadNextMap() {
        try {
            System.out.println("Loading next map from: " + currentMapPath);
            if (currentMapPath.contains("customMap_2")) {
                // Set a flag to prevent physics updates
                shouldLoadNextMap = true;

                // Clear all bodies from the world
                Array<Body> bodies = new Array<>();
                world.getBodies(bodies);
                for(Body body : bodies) {
                    world.destroyBody(body);
                }
                float currentPos = game.playMusic.getPosition();

                // Create and switch to new screen
                PlayScreen nextScreen = new PlayScreen(game, "tileset/customMap_3.tmx");
                nextScreen.hud.addScore(hud.getScore()); // Transfer current score

                // Use Gdx.app.postRunnable to ensure screen switch happens on render thread
                Gdx.app.postRunnable(() -> {
                    dispose();  // Clean up current resources
                    game.setScreen(nextScreen);
                    game.playMusic.play();
                    game.playMusic.setPosition(currentPos);
                    System.out.println("Switched to Map 3");
                });
            } else if (currentMapPath.contains("customMap_3")) {
                // Set a flag to prevent physics updates
                shouldLoadNextMap = true;

                // Clear all bodies from the world
                Array<Body> bodies = new Array<>();
                world.getBodies(bodies);
                for(Body body : bodies) {
                    world.destroyBody(body);
                }

                float currentPos = game.playMusic.getPosition();

                // Create and switch to new screen with Map 4
                PlayScreen nextScreen = new PlayScreen(game, "tileset/customMap_4.tmx");
                nextScreen.hud.addScore(hud.getScore()); // Transfer current score

                // Use Gdx.app.postRunnable to ensure screen switch happens on render thread
                Gdx.app.postRunnable(() -> {
                    dispose();  // Clean up current resources
                    game.setScreen(nextScreen);
                    game.playMusic.play();       // if it was stopped
                    game.playMusic.setPosition(currentPos);
                    System.out.println("Switched to Map 4");
                });
            } else if (currentMapPath.contains("customMap_4")) {
                // Only show victory screen on final map
                shouldLoadNextMap = true;

                Gdx.app.postRunnable(() -> {
                    dispose();
                    game.setScreen(new VictoryScreen(game, hud.getScore()));
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


    /**
     * called when the screen is resized
     * it updates our viewport, the HUD viewport and adjust the camera to match the new screen
     * @param width new window width
     * @param height new window height
     */
    @Override
    public void resize(int width, int height) {
        // Update viewport with new screen size
        gamePort.update(width, height, true);

        // Update HUD viewport
        hud.resize(width, height);

        // Center camera
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        // Maintain zoom level
        gamecam.zoom = currentZoom;

        // Update camera
        gamecam.update();

        // Update FBO for fog of war
        int viewW = gamePort.getScreenWidth();
        int viewH = gamePort.getScreenHeight();

        if (viewW < 1) viewW = 1;
        if (viewH < 1) viewH = 1;

        if (fbo != null) {
            fbo.dispose();
        }

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, viewW, viewH, false);
        fboRegion = new TextureRegion(fbo.getColorBufferTexture());
    }

    /**
     * Sets paused, which skips logic updates in the update method
     * @param paused true to pause
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     * getter for game
     * @return the testGame object controlling playscreen
     */
    public testGame getGame() {
        return game;
    }

    /**
     * add getter for current map path
     * @return the path to the currently loaded Tiled map
     */
    public String getCurrentMapPath() {
        return currentMapPath;
    }

    /**
     * Called when the game is paused
     */
    @Override
    public void pause() {

    }

    /**
     * Called when the game is resumed
     */
    @Override
    public void resume() {

    }

    /**
     * called when this screen is hidden and stops input/music
     */
    @Override
    public void hide() {

    }

    /**
     * disposes of the screen when we're done so that resources are freed up
     */
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

    /**
     * @return Josh instance
     */
    public Josh getPlayer() {
        return player;
    }

    /**
     * @return the associated HUD
     */
    public HUD getHud() {
        return hud;
    }
}
