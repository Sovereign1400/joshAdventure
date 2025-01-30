package Sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

/**
 * this is the class of the basic brick tile within the game world,
 * it extends the interactive object functionality
 * main use of it is collision
 */

public class Brick extends InteractiveTileObject{
    /**
     * makes a brick at a certain location
     *
     * @param world  the world the brick is in
     * @param map    the map that tells its position and shape
     * @param bounds the bricks bounds
     */
    public Brick(World world, TiledMap map, Rectangle bounds){
        super(world, map, bounds);
    }
}
