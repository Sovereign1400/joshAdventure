package Tools;

import Sprites.Brick;
import Sprites.Heart;
import Sprites.Josh;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.testGame;

import java.util.List;

public class B2WorldCreator {
    private Array<Heart> hearts;
    private Josh player;

    public B2WorldCreator(World world, TiledMap map){
        // This sets basic attributes for all future interactive objects on the map.
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;
        hearts = new Array<>();

        // This applys these attributes to the layers of objects drawn on the map
        for(MapObject object : map.getLayers().get("background").getObjects().getByType(RectangleMapObject.class)){// Loop through the "graphics" layer of map layers.(counting from bottom)
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            // This creates ground object bodies and fixtures.
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth()/2) / testGame.PPM, (rect.getY() + rect.getHeight() / 2) / testGame.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / testGame.PPM, rect.getHeight() / 2 / testGame.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        };

        // This creates bricks
        for(MapObject object : map.getLayers().get("bricks").getObjects().getByType(RectangleMapObject.class)){// Loop through the "graphics" layer of map layers.(counting from bottom)
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            new Brick(world, map, rect);

//            // This creates ground object bodies and fixtures.
//            bdef.type = BodyDef.BodyType.StaticBody;
//            bdef.position.set((rect.getX() + rect.getWidth()/2) / testGame.PPM, (rect.getY() + rect.getHeight() / 2) / testGame.PPM);
//
//            body = world.createBody(bdef);
//
//            shape.setAsBox(rect.getWidth() / 2 / testGame.PPM, rect.getHeight() / 2 / testGame.PPM);
//            fdef.shape = shape;
//            body.createFixture(fdef);
        };

        // This creates mushrooms/hearts
        for(MapObject object : map.getLayers().get("pickups").getObjects().getByType(RectangleMapObject.class)){// Loop through the "graphics" layer of map layers.(counting from bottom)
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            hearts.add(new Heart(world, map, rect));
//            // This creates ground object bodies and fixtures.
//            bdef.type = BodyDef.BodyType.StaticBody;
//            bdef.position.set((rect.getX() + rect.getWidth()/2) / testGame.PPM, (rect.getY() + rect.getHeight() / 2) / testGame.PPM);
//
//            body = world.createBody(bdef);
//
//            shape.setAsBox(rect.getWidth() / 2 / testGame.PPM, rect.getHeight() / 2 / testGame.PPM);
//            fdef.shape = shape;
//            body.createFixture(fdef);
        };



        System.out.println("Looking for pickups layer...");
        MapLayer pickupsLayer = map.getLayers().get("pickups");
        if (pickupsLayer == null) {
            System.out.println("Pickups layer not found!");
        } else {
            System.out.println("Found pickups objects: " + pickupsLayer.getObjects().getCount());
        }
    }

    public Array<Heart> createHearts(World world, TiledMap map) {
        Array<Heart> hearts = new Array<>();
        for(MapObject object : map.getLayers().get("pickups").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            hearts.add(new Heart(world, map, rect));
        }
        return hearts;
    }

    public Array<Heart> getHearts() {
        return hearts;
    }
}
