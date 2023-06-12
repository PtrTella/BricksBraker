package brickbreaker.model.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import brickbreaker.MapInfo;
import brickbreaker.ResourceLoader;
import brickbreaker.common.Difficulty;
import brickbreaker.common.TypePower;
import brickbreaker.common.TypePowerUp;
import brickbreaker.common.Vector2D;
import brickbreaker.model.world.World;
import brickbreaker.model.world.WorldImpl;
import brickbreaker.model.world.gameObjects.Ball;
import brickbreaker.model.world.gameObjects.Bar;
import brickbreaker.model.world.gameObjects.Brick;
import brickbreaker.model.world.gameObjects.bounding.RectBoundingBox;

/**
 * Factory class for creating game World.
 */
public class WorldFactory {

    public static Double X_SPEED = 0.1;
    public static final Double Y_SPEED = -15.0;

    public static final Double BOUNDARIES_SIZE = 600.0;

    private static WorldFactory instance;

    /**
     * @return the instance of WorldFactory if it not exists yet.
     */
    public static WorldFactory getInstance() {
        if (instance == null) {
            instance = new WorldFactory();
        }

        return instance;
    }

    /**
     * This method gets a new game world without any brick.
     * The resulting world will only have a Bar object and a Ball object.
     * @return a new empty World object.
     */
    private World getEmptyWorld() {
        Bar newBar = GameFactory.getInstance().createBar(new Vector2D(BOUNDARIES_SIZE/2, BOUNDARIES_SIZE - 20));
        Ball newBall = GameFactory.getInstance().createBall(new Vector2D(BOUNDARIES_SIZE/2, BOUNDARIES_SIZE  - newBar.getHeight() - Ball.RADIUS), new Vector2D(X_SPEED, Y_SPEED));
        RectBoundingBox boundary = new RectBoundingBox(new Vector2D(BOUNDARIES_SIZE/2, BOUNDARIES_SIZE/2), BOUNDARIES_SIZE, BOUNDARIES_SIZE);
        World w = new WorldImpl(boundary);

        w.setBar(newBar);
        w.addBall(newBall);
        return w;
    }

    /**
     * This method gets a new game world where the bricks are created randomically, on the basis of the difficulty.
     * Every brick created has random position (only legal positions, that do not 
     * overcome the world boundaries) and a random lifes quantity (a value between 1 or 9).
     * @param d the difficulty that describe how the game world will be.
     * @return a new World object.
     */
    public World getRandomWorld(final Difficulty d) {
        World w = this.getEmptyWorld();
        w.addBricks(GameFactory.getInstance().createRandomBricks(d, Brick.BRICKS_COL, Brick.BRICKS_ROW));
        randomPowerUpAssignment(d, w.getBricks());
        return w;
    }

    /**
     * This method gets a new game World object loading it from the maps database.
     * The loaded map is used to create all the bricks on the basis of the map difficulty.
     * @param index an Integer value which will function as an index to get the map from database.
     * @return a new World object.
     */
    public World getWorld(final Integer index) {
        World w = this.getEmptyWorld();
        MapInfo i = ResourceLoader.getInstance().getMapsInfo().stream().filter(item -> item.getIndex() == index).findFirst().get();
        w.addBricks(GameFactory.getInstance().createBricks(i.getBricksData(), ResourceLoader.getInstance().MAP_COLUMNS_FILE_FORMAT, ResourceLoader.getInstance().MAP_ROWS_FILE_FORMAT));
        randomPowerUpAssignment(i.getDifficulty(), w.getBricks());
        return w;
    }

    /**
     * This method assign random power ups (bonuses or maluses) to a list of brick
     * on the basis of the Difficulty value passed as parameter.
     * @param d a Difficulty value which describes the power ups quantity
     *          and how many has to be bonuses or not.
     * @param b a List<Brick> object which contains the bricks where the power ups has to
     *          be stored.
     */
    private void randomPowerUpAssignment(final Difficulty d, final List<Brick> b) {
        Random r = new Random();

        Integer numPowerUp = b.size() - ( b.size() / 4);
        List<TypePower> p = this.getWorldPowerUp(numPowerUp, d.getBonusPercentage());
        
        List<Integer> val = IntStream.range(0, b.size())
        .boxed()
        .collect(Collectors.toList());
        
        for (TypePower i : p) {
            b.get(val.remove(r.nextInt(val.size()))).setPowerUp(i);
        }
        System.out.println("Bonus: " + p.toString());
    }

    /**
     * This method returns a list of TypePower which are the power up types that are
     * present in the current World.
     * @param pQuantity is the power up quantity value.
     * @param bonus is true if the powerup to fill 
     * @return a list of Typepower
     */
    private List<TypePower> getWorldPowerUp(final Integer pQuantity, final Integer bonus) {
        Random r = new Random();
        List<TypePower> ret = new ArrayList<>();
        
        Integer positive = pQuantity * bonus / 100;
        List<TypePower> p = TypePower.getElement(TypePowerUp.POSITIVE);
        for (int i=0; i < positive; i++) {
            ret.add(p.get(r.nextInt(p.size())));
        }

        List<TypePower> n = TypePower.getElement(TypePowerUp.NEGATIVE);
        for (int i=0; i < (pQuantity - positive); i++) {
            ret.add(n.get(r.nextInt(n.size())));
        }

        return ret;
    }
}
