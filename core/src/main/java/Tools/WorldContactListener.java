package Tools;

import Sprites.Heart;
import Sprites.Josh;
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
        }

        @Override
        public void endContact(Contact contact) {}

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {}

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {}
    }

