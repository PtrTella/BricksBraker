package brickbreaker.controllers;

import java.util.Optional;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import brickbreaker.common.Difficulty;
import brickbreaker.common.State;
import brickbreaker.common.Vector2D;
import brickbreaker.controllers.state.LevelControllerImpl;
import brickbreaker.model.Level;
import brickbreaker.model.rank.GameRank;
import brickbreaker.model.rank.Rank;
import brickbreaker.model.user.User;
import brickbreaker.view.GameScene;

public class Controller extends ModelController {

    private GameScene loopScene;
    private boolean haveWin;

    private User user = null;

    public Controller(final GameScene gameWindow) {
        super();
        this.loopScene = gameWindow;
    }

    public void setUser(final String username) {
        this.user = this.getUserController().getUser(username);
    }

    public void createEndless(final Difficulty d) {
        Rank r = new GameRank("endless.json", d.ordinal());
        final Integer maxIteration = 10;
        Integer totalScore = 0;

        do {
            haveWin = false;
            Integer it = 0;
            Level level = null;
            do {
                this.getErrorListener().getErrorList().clear();
                level = this.getModel().getRandomLevel(Optional.of(d));
                it++;
            } while (this.getErrorListener().getErrorPresent() && it < maxIteration);

            if (it < maxIteration) {
                this.setLevelController(new LevelControllerImpl(level));
                loopScene.init();
                this.getLevelController().gameLoop();
                totalScore += this.getLevelController().getScore();
                if(this.getLevelController().getLevel().getState().equals(State.WIN)) {
                    haveWin = true;
                } else if (this.getLevelController().getLevel().getState().equals(State.LOST) && this.user != null){
                    r.addRank(user.getName(), totalScore);
                }
            } else {
                // mostra errore caricamento
            }
        } while (haveWin);
    }

    public synchronized void createLevels(final Integer id) {
        Rank r = new GameRank("levels.json", id);
        this.getErrorListener().getErrorList().clear();
        Level level = this.getModel().getLevel(id);
        if (!this.getErrorListener().getErrorPresent()) {
            this.setLevelController(new LevelControllerImpl(level));
            loopScene.init();
            this.getLevelController().gameLoop();
            if(this.getLevelController().getLevel().getState().equals(State.WIN) && this.user != null) {
                r.addRank(user.getName(), this.getLevelController().getScore());
            }
        } else {
            // mostra errore caricamento
        }
    }
}
