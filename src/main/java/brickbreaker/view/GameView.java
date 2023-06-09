package brickbreaker.view;

import brickbreaker.common.GameImages;
import brickbreaker.common.GameObjectsImages;
import brickbreaker.common.Vector2D;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import brickbreaker.model.world.gameObjects.Ball;
import brickbreaker.model.world.gameObjects.Bar;
import brickbreaker.model.world.gameObjects.Brick;

import java.util.HashMap;
import java.util.List;

public class GameView extends ViewImpl {

    private static final Integer CANVAS_HEIGHT = 576;
    private static final Integer CANVAS_WIDTH = 576;

    private static final Integer BRICK_WIDTH = 64;
    private static final Integer BRICK_HEIGHT = 32;

    @FXML 
    private Label scoreLabel;
    
    @FXML 
    private ImageView backGround;
    
    @FXML 
    private Canvas foreGround;
    
    @FXML 
    private ImageView ball;
    
    @FXML 
    private ImageView pauseButton;

    private GraphicsContext gcF;
    private HashMap<Integer, Image> brickImages;

    @Override
    public void init() {
        this.backGround.setImage(GameImages.CITY_LANDSCAPE.getImage());
        this.gcF = foreGround.getGraphicsContext2D();
        this.setUpBrickImages();
        this.getStage().getScene().setOnKeyPressed(e -> handleKeyPressed(e.getCode()));
        this.getController().play();
    }

    public void setUpBrickImages() {
        this.brickImages = new HashMap<>(10);

        for (Integer i = 0; i < 10; i++) {
            this.brickImages.put(Integer.valueOf(i + 1), GameObjectsImages.values()[9 - i].getImage());
        }
    }

    public void render() {

        Platform.runLater(() -> {
            this.gcF.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

            List<Brick> b = this.getController().getModel().getWorld().getBricks();
            for (Brick item : b) {
                Image i = this.brickImages.get(item.getLife());
                Vector2D p = item.getPosition();
                this.gcF.drawImage(i, p.getX()*BRICK_WIDTH, p.getY()*BRICK_HEIGHT, BRICK_WIDTH, BRICK_HEIGHT);
            }

            Bar bar = this.getController().getModel().getWorld().getBar();
            Ball ball = this.getController().getModel().getWorld().getBalls().get(0);
            this.gcF.drawImage(GameObjectsImages.BAR.getImage(), bar.getPosition().getX(), bar.getPosition().getY(), 128, 32);
            this.gcF.drawImage(GameObjectsImages.BALL.getImage(), ball.getPosition().getX(), ball.getPosition().getY(), 32, 32);
        });
    }

    public void handleKeyPressed(KeyCode keyCode) {
        if (keyCode == KeyCode.LEFT) {
            this.getController().getInputController().notifyMoveLeft();
        } else if (keyCode == KeyCode.RIGHT) {
            this.getController().getInputController().notifyMoveRight();
        }
    }
}
