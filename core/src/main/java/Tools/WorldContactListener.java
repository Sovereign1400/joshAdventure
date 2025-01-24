package Tools;

import Sprites.Heart;
import Sprites.Josh;
import Sprites.Shield;
import Sprites.Speedup;
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

            if (fixA.getUserData() instanceof Speedup || fixB.getUserData() instanceof Speedup){
                Fixture speedupFix = fixA.getUserData() instanceof Speedup ? fixA : fixB;
                Speedup speedup = (Speedup) speedupFix.getUserData();

                if(!speedup.isCollected()) {
                    speedup.onCollect();
                    player.increaseSpeed(); // increase speed
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
        }

        @Override
        public void endContact(Contact contact) {}

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {}

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {}
    }

