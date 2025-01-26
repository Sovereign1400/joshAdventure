package Tools;

import Screens.PlayScreen;
import Sprites.*;
import com.badlogic.gdx.physics.box2d.*;

    public class WorldContactListener implements ContactListener {
        private Josh player;
        private PlayScreen screen;

        public WorldContactListener(Josh player, PlayScreen screen) {
            this.player = player;
            this.screen = screen;
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
        }

        @Override
        public void endContact(Contact contact) {}

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {}

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {}


    }

