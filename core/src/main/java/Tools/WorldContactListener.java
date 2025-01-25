package Tools;

import Sprites.*;
import com.badlogic.gdx.physics.box2d.*;

    public class WorldContactListener implements ContactListener {
        private Josh player;

        public WorldContactListener(Josh player) {
            this.player = player;
        }

        @Override
        public void beginContact(Contact contact) {
            Fixture fixA = contact.getFixtureA();
            Fixture fixB = contact.getFixtureB();

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
                System.out.println("Speedup contact detected!"); // Debug line
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

            if (fixA.getUserData() instanceof Shield || fixB.getUserData() instanceof Shield){
                Fixture shieldFix = fixA.getUserData() instanceof Shield ? fixA : fixB;
                Shield shield = (Shield) shieldFix.getUserData();

                if(!shield.isCollected()) {
                    shield.onCollect();
                    player.increaseHealth(); // increase speed
                }
            }

            if (fixA.getUserData() instanceof Key || fixB.getUserData() instanceof Key){
                Fixture keyFix = fixA.getUserData() instanceof Key ? fixA : fixB;
                Key key = (Key) keyFix.getUserData();

                if(!key.isCollected()) {
                    key.onCollect();
                    player.increaseHealth(); // increase speed
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

