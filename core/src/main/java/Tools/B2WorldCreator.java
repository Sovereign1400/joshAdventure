package Tools;

import Sprites.*;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.testGame;

public class B2WorldCreator {
    private Array<Heart> hearts;
    private Array<Speedup> speedups;
    private Array<Shield> shields;
    private Array<Key> keys;
    private Josh player;
    private Array<Door> doors;

    public B2WorldCreator(World world, TiledMap map){
        // This sets basic attributes for all future interactive objects on the map.
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;
        hearts = new Array<>();
        speedups = new Array<>();
        shields = new Array<>();
        keys = new Array<>();


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
        // Accounts for the green bounds, can be safely removed
        /*for(MapObject object : map.getLayers().get("hearts").getObjects().getByType(RectangleMapObject.class)){// Loop through the "graphics" layer of map layers.(counting from bottom)
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
        }*/



    }

    public Array<Heart> createHearts(World world, TiledMap map) {
        hearts = new Array<>();
        for(MapObject object : map.getLayers().get("hearts").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            hearts.add(new Heart(world, map, rect));
        }
        return hearts;
    }

    public Array<Speedup> createSpeedups(World world, TiledMap map){
        speedups = new Array<>();
        for(MapObject object : map.getLayers().get("speedups").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            speedups.add(new Speedup(world, map, rect));
        }
        return speedups;
    }

    public Array<Shield> createShields(World world, TiledMap map){
        shields = new Array<>();
        for(MapObject object : map.getLayers().get("shields").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            shields.add(new Shield(world, map, rect));
        }
        return shields;
    }

    public Array<Key> createKeys(World world, TiledMap map){
        keys = new Array<>();
        for(MapObject object : map.getLayers().get("keys").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            keys.add(new Key(world, map, rect));
        }
        return keys;
    }

    public Array<Door> createDoors(World world, TiledMap map) {
        doors = new Array<>();
        // Create entrance door
        for(MapObject object : map.getLayers().get("entranceDoor").getObjects()) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            doors.add(new Door(world, map, rect, DoorType.ENTRANCE));
        }
        // Create exit door
        for(MapObject object : map.getLayers().get("exitDoor").getObjects()) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            doors.add(new Door(world, map, rect, DoorType.EXIT));
        }
        return doors;
    }
}
