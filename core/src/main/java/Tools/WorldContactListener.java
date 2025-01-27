package Tools;

import Screens.PlayScreen;
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

            if(fixA.getUserData() instanceof Heart || fixB.getUserData() instanceof Heart) {
                Fixture heartFix = fixA.getUserData() instanceof Heart ? fixA : fixB;
                Heart heart = (Heart)heartFix.getUserData();

                if(!heart.isCollected()) {
                    heart.onCollect();
                    player.increaseHealth();
                }
            }

            // Check if one fixture is the player and the other is a speedup
            if (fixA.getUserData() instanceof Speedup || fixB.getUserData() instanceof Speedup) {
                Speedup speedup;
                if (fixA.getUserData() instanceof Speedup) {
                    speedup = (Speedup) fixA.getUserData();
                } else {
                    speedup = (Speedup) fixB.getUserData();
                }

                if (!speedup.isCollected()) {
                    player.activateSpeedBoost();
                    speedup.onCollect();
                }
            }

            // This interacts the shield
            if (fixA.getUserData() instanceof Shield || fixB.getUserData() instanceof Shield){
                Fixture shieldFix = fixA.getUserData() instanceof Shield ? fixA : fixB;
                Shield shield = (Shield) shieldFix.getUserData();

                if(!shield.isCollected()) {
                    shield.onCollect();
                    player.increaseHealth(); // increase speed
                }
            }

            // This interacts the keys
            if (fixA.getUserData() instanceof Key || fixB.getUserData() instanceof Key) {
                if (fixA.getUserData() instanceof Key) {
                    ((Key)fixA.getUserData()).onCollect();
                    screen.getPlayer().collectKey();
                } else {
                    ((Key)fixB.getUserData()).onCollect();
                    screen.getPlayer().collectKey();
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

            // Door collision handling
            // Door collision handling
            if (fixA.getUserData() instanceof Door || fixB.getUserData() instanceof Door) {
                Fixture doorFix = fixA.getUserData() instanceof Door ? fixA : fixB;
                Door door = (Door)doorFix.getUserData();

                if (door.getDoorType() == DoorType.EXIT) {
                    if (player.hasKey()) {
                        System.out.println("Player touched exit door with key");
                        door.interact(player);
                        loadingNextMap = true;  // Set flag before loading

                        // Use Gdx.app.postRunnable to ensure screen loading happens on render thread
                        Gdx.app.postRunnable(() -> {
                            screen.loadNextMap();
                        });
                    } else {
                        screen.showDoorMessage = true;
                        screen.messageTimer = 0;
                    }
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

