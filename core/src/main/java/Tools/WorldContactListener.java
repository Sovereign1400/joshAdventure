package Tools;

import Screens.PlayScreen;
import Screens.VictoryScreen;
import Sprites.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;

    public class WorldContactListener implements ContactListener {
        private Josh player;
        private PlayScreen screen;
        private boolean loadingNextMap = false;  // Add this flag

        public WorldContactListener(Josh player, PlayScreen screen) {
            this.player = player;
            this.screen = screen;
        }

        @Override
        public void beginContact(Contact contact) {
            Fixture fixA = contact.getFixtureA();
            Fixture fixB = contact.getFixtureB();

            if (loadingNextMap) return;

            if (fixA.getUserData() instanceof Heart || fixB.getUserData() instanceof Heart) {
                // First, identify which fixture is the heart and which is the potential collector
                Fixture heartFixture = fixA.getUserData() instanceof Heart ? fixA : fixB;
                Fixture otherFixture = heartFixture == fixA ? fixB : fixA;

                // Only allow collection if the other fixture is the player's body
                if (otherFixture.getBody() == player.b2body) {
                    Heart heart = (Heart) heartFixture.getUserData();
                    if (!heart.isCollected()) {
                        heart.onCollect(player);
                        player.increaseHealth();
                    }
                }
            }

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
                    }
                }
            }

            // This interacts the shield
            if (fixA.getUserData() instanceof Shield || fixB.getUserData() instanceof Shield){
                Fixture shieldFix = fixA.getUserData() instanceof Shield ? fixA : fixB;
                Shield shield = (Shield) shieldFix.getUserData();

                if(!shield.isCollected()) {
                    shield.onCollect();
                    // missing shield functionality
                }
            }

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
                    }
                }
            }

            // Check monster collision from 2 ways: Josh touchs monster and monster touched Josh.
            if (fixA.getBody() == player.b2body && fixB.getUserData() instanceof Monster) {
                player.damage();
            }
            else if (fixB.getBody() == player.b2body && fixA.getUserData() instanceof Monster) {
                player.damage();
            }


            // Check the collision of the door, make sure the touch registration only happens 1 time.
            if (loadingNextMap) return;  // Early return if loading next map

            // Door collision handling and Victory check.
            if (fixA.getUserData() instanceof Door || fixB.getUserData() instanceof Door) {
                Fixture doorFix = fixA.getUserData() instanceof Door ? fixA : fixB;
                Door door = (Door)doorFix.getUserData();

                if (door.getDoorType() == DoorType.EXIT) {
                    if (player.hasKey()) {
                        System.out.println("Player touched exit door with key");
                        door.interact(player);
                        loadingNextMap = true;  // Set flag before loading

                        if (screen.getCurrentMapPath().contains("customMap_4")) {
                            // Victory condition - reached exit door in final map
                            System.out.println("Victory achieved!");
                            Gdx.app.postRunnable(() -> {
                                // Convert gameTime to integer seconds for score calculation
                                int elapsedTime = (int)screen.gameTime;
                                screen.getGame().setScreen(new VictoryScreen(screen.getGame(), elapsedTime));
                            });
                        } else {
                            // Not final map, proceed to next map
                            Gdx.app.postRunnable(() -> {
                                screen.loadNextMap();
                            });
                        }
                    } else {
                        screen.showDoorMessage = true;
                        screen.messageTimer = 0;
                    }
                }
            }

            // Check when the player touches the trap.
            if (fixA.getBody() == player.b2body && fixB.getUserData() instanceof Trap) {
                player.damage();
            } else if (fixB.getBody() == player.b2body && fixA.getUserData() instanceof Trap) {
                player.damage();
            }

            // Check the player attack
            // If Josh is attacking and touches a monster, the monster gets hit
            if (player.isAttacking) {
                if ((fixA.getBody() == player.b2body && fixB.getUserData() instanceof Monster)) {
                    Monster monster = (Monster) fixB.getUserData();
                    monster.getHit();
                    System.out.println("Monster hit by attack!");  // Debug log
                }
                else if ((fixB.getBody() == player.b2body && fixA.getUserData() instanceof Monster)) {
                    Monster monster = (Monster) fixA.getUserData();
                    monster.getHit();
                    System.out.println("Monster hit by attack!");  // Debug log
                }
            }
        }

        @Override
        public void endContact(Contact contact) {}

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {}

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {}


    }

