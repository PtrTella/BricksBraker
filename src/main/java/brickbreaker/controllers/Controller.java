package brickbreaker.controllers;

import brickbreaker.common.Difficulty;
import brickbreaker.common.State;
import brickbreaker.model.Level;
import brickbreaker.model.rank.GameRank;
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
        final Integer maxIteration = 10;
        Integer totalScore = 0;
        long playTime = 0;

        do {
            haveWin = false;
            Integer it = 0;
            Level level = null;
            do {
                this.getErrorListener().getErrorList().clear();
                level = this.getModel().getRandomLevel(d);
                it++;
            } while (this.getErrorListener().getErrorPresent() && it < maxIteration);

            if (it < maxIteration) {
                this.setLevelController(new LevelControllerImpl(level));
                loopScene.init(getLevelController().getLevel());
                playTime += this.getLevelController().gameLoop();
                totalScore += this.getLevelController().getScore();
                if(this.getLevelController().getLevel().getState().equals(State.WIN)) {
                    haveWin = true;
                } else if (this.getLevelController().getLevel().getState().equals(State.LOST) && this.user != null){
                    new GameRank("endless.json", d.ordinal()).addToRank(user.getName(), (int) (totalScore / playTime));
                }
            } else {
                // mostra errore caricamento
            }
        } while (haveWin);
    }

    public synchronized void createLevels(final Integer id) {
        long playTime = 0;
        this.getErrorListener().getErrorList().clear();
        Level level = this.getModel().getLevel(id);
        if (!this.getErrorListener().getErrorPresent()) {
            this.setLevelController(new LevelControllerImpl(level));
            loopScene.init(getLevelController().getLevel());
            playTime = this.getLevelController().gameLoop();
            if(this.getLevelController().getLevel().getState().equals(State.WIN) && this.user != null) {
                new GameRank("levels.json", id).addToRank(user.getName(), (int) (this.getLevelController().getScore() / playTime));
                if (user.getLevelReached() == id) {
                    user.incLevelReached();
                }
            }
        } else {
            // mostra errore caricamento
        }
    }
}
