package Tools;

import Screens.PlayScreen;
import Sprites.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Handles all collision events in the game world
 * Processes interactions between player, monsters, collectibles and environment
 */
public class WorldContactListener implements ContactListener {
    private Josh player;
    private PlayScreen screen;
    private boolean loadingNextMap = false;  // Add this flag
    private com.badlogic.gdx.audio.Sound trapSound;
    private com.badlogic.gdx.audio.Sound damageSound;
    private com.badlogic.gdx.audio.Sound deathSound;
    private com.badlogic.gdx.audio.Sound boostSound;
    private com.badlogic.gdx.audio.Sound hitSound;
    private com.badlogic.gdx.audio.Sound ghostSound;
    private com.badlogic.gdx.audio.Sound healSound;
    private com.badlogic.gdx.audio.Sound keySound;

    /**
     * initialized when creating new world contact listener
     * loads all required sound effects
     * @param player the main character Josh
     * @param screen the current play screen
     */

    public WorldContactListener(Josh player, PlayScreen screen) {
        this.player = player;
        this.screen = screen;
        ghostSound = Gdx.audio.newSound(Gdx.files.internal("audio/ghostSFX.mp3"));
        healSound = Gdx.audio.newSound(Gdx.files.internal("audio/healSFX.mp3"));
        boostSound = Gdx.audio.newSound(Gdx.files.internal("audio/boostSFX.mp3"));
        damageSound = Gdx.audio.newSound(Gdx.files.internal("audio/hitSFX.mp3"));
        trapSound = Gdx.audio.newSound(Gdx.files.internal("audio/trapSFX.mp3"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("audio/hitSFX.mp3"));
        keySound = Gdx.audio.newSound(Gdx.files.internal("audio/keySFX.mp3"));
    }


    /**
     * called when two fixtures begin contact
     * handles all collision logic between game objects
     * @param contact the contact point between two fixtures
     */

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if (loadingNextMap) return;

        /**
         * heals player if health < 3
         * plays heal sound
         * adds points to score
         * only collectable once
         */

        if (fixA.getUserData() instanceof Heart || fixB.getUserData() instanceof Heart) {
            // First, identify which fixture is the heart and which is the potential collector
            Fixture heartFixture = fixA.getUserData() instanceof Heart ? fixA : fixB;
            Fixture otherFixture = heartFixture == fixA ? fixB : fixA;

            // Only allow collection if the other fixture is the player's body
            if (otherFixture.getBody() == player.b2body) {
                Heart heart = (Heart) heartFixture.getUserData();
                if (!heart.isCollected()) {
                    if (player.getHealth() < 3){
                        heart.onCollect(player);
                        player.increaseHealth();
                        healSound.play(0.3F);
                        screen.addScore(PlayScreen.HEART_SCORE);
                    }

                }
            }
        }

        /**
         * activates speed boost
         * plays boost sound
         * adds points to score
         * only collectable once
         */

        if (fixA.getUserData() instanceof Speedup || fixB.getUserData() instanceof Speedup) {
            // First, identify which fixture is the speedup and which is the potential collector
            Fixture speedupFixture = fixA.getUserData() instanceof Speedup ? fixA : fixB;
            Fixture otherFixture = speedupFixture == fixA ? fixB : fixA;

            // Only allow collection if the other fixture is the player's body
            if (otherFixture.getBody() == player.b2body) {
                Speedup speedup = (Speedup) speedupFixture.getUserData();
                if (!speedup.isCollected()) {
                    speedup.onCollect();
                    player.activateSpeedBoost();
                    boostSound.play(0.3F);
                    screen.addScore(PlayScreen.SPEEDUP_SCORE);
                }
            }
        }

        /**
         * adds scoreup points
         * adds points to score
         * only collectable once
         */

        // This interacts the Scoreup
        if (fixA.getUserData() instanceof Scoreup || fixB.getUserData() instanceof Scoreup){
            Fixture shieldFix = fixA.getUserData() instanceof Scoreup ? fixA : fixB;
            Scoreup scoreup = (Scoreup) shieldFix.getUserData();



            if(!scoreup.isCollected()) {
                scoreup.onCollect();
                screen.addScore(PlayScreen.SHIELD_SCORE);
            }
        }

        /**
         * adds key to inventory
         * plays key sound
         * adds points to score
         * only collectable once
         */

        // This interacts the keys
        if (fixA.getUserData() instanceof Key || fixB.getUserData() instanceof Key) {
            // First, identify which fixture is the key and which is the potential collector
            Fixture keyFixture = fixA.getUserData() instanceof Key ? fixA : fixB;
            Fixture otherFixture = keyFixture == fixA ? fixB : fixA;

            // Only allow collection if the other fixture is the player's body
            if (otherFixture.getBody() == player.b2body) {
                Key key = (Key) keyFixture.getUserData();
                if (!key.isCollected()) {
                    key.onCollect();
                    player.collectKey();
                    screen.addScore(PlayScreen.KEY_SCORE);
                    keySound.play(0.3F);
                }
            }
        }


        /**
         * damages player on touch
         * plays damage sound
         * can be defeated if player is attacking
         */

        // Check monster collision from 2 ways: Josh touchs monster and monster touched Josh.
        if (fixA.getBody() == player.b2body && fixB.getUserData() instanceof Monster) {
            player.damage();
            damageSound.play(0.3F);
        }
        else if (fixB.getBody() == player.b2body && fixA.getUserData() instanceof Monster) {
            player.damage();
            damageSound.play(0.3F);
        }

        // Check the collision of the door, make sure the touch registration only happens 1 time.
        if (loadingNextMap) return;  // Early return if loading next map


        /**
         * checks if player has key
         * loads next level if key collected
         * shows message if no key
         * prevents multiple loads
         */

        // Door collision handling and Victory check.
        if (fixA.getUserData() instanceof Door || fixB.getUserData() instanceof Door) {
            Fixture doorFix = fixA.getUserData() instanceof Door ? fixA : fixB;
            Door door = (Door)doorFix.getUserData();

            if (door.getDoorType() == DoorType.EXIT) {
                if (player.hasKey()) {
                    door.interact(player);
                    loadingNextMap = true;  // Set flag before loading

                    // Not final map, proceed to next map
                    Gdx.app.postRunnable(() -> {
                        screen.loadNextMap();
                    });
                } else {
                    screen.showDoorMessage = true;
                    screen.messageTimer = 0;
                }
            }
        }

        /**
         * damages player
         * plays trap sound
         */

        // Check when the player touches the trap.
        if (fixA.getBody() == player.b2body && fixB.getUserData() instanceof Trap) {
            player.damage();
            trapSound.play();

        } else if (fixB.getBody() == player.b2body && fixA.getUserData() instanceof Trap) {
            player.damage();
            trapSound.play();
        }

        /**
         * checks player attack state
         * damages monster if player attacking
         * plays hit sound on successful hit
         */

        // Check the player attack
        // If Josh is attacking and touches a monster, the monster gets hit
        if (player.isAttacking) {
            if ((fixA.getBody() == player.b2body && fixB.getUserData() instanceof Monster)) {
                Monster monster = (Monster) fixB.getUserData();
                monster.getHit();
                hitSound.play();
                System.out.println("Monster hit by attack!");  // Debug log
            }
            else if ((fixB.getBody() == player.b2body && fixA.getUserData() instanceof Monster)) {
                Monster monster = (Monster) fixA.getUserData();
                monster.getHit();
                hitSound.play();
                System.out.println("Monster hit by attack!");  // Debug log
            }
        }

        /**
         * damages player
         * plays ghost sound
         */

        // This sets for ghost collision
        if (fixA.getBody() == player.b2body && fixB.getUserData() instanceof Ghost) {
            player.damage();
            ghostSound.play();
        }
        else if (fixB.getBody() == player.b2body && fixA.getUserData() instanceof Ghost) {
            player.damage();
            ghostSound.play();
        }
    }

    /**
     * called when two fixtures end contact
     * currently unused
     */
    @Override
    public void endContact(Contact contact) {}


    /**
     * called before collision resolution
     * currently unused
     */
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    /**
     * called after collision resolution
     * currently unused
     */
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}

