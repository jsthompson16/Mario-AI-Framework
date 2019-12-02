package agents.ThompsonWeilerAgent;

import engine.core.MarioForwardModel;
import engine.core.MarioTimer;

enum State {
    RUN, JUMP, WAIT, FIREBALL, RETREAT
}

public class StateMachine {

    State currentState = State.RUN;
    int swap = 0;

    public boolean[] getNextAction(MarioForwardModel model, MarioTimer timer) {
        boolean action[] = new boolean[] {false, false, false, false, false};

        updateState(model, timer);

        switch(currentState) {
            case JUMP:
                action = new boolean[] {false, true, false, true, true};
                break;
            case RUN:
                action = new boolean[] {false, true, false, true, false};
                break;
            case WAIT:
                action = new boolean[] {false, false, false, false, false};
                break;
        }

        return action;
    }

    private int getLocation(int relX, int relY, int[][] scene) {
        int realX = 8 + relX;
        int realY = 8 + relY;

        return scene[realX][realY];
    }

    private boolean thereIsObstacle(int[][] scene) {
        int[] inFrontOf = new int[] { getLocation(1, 0, scene), getLocation(2, 0, scene), getLocation(2, -1, scene) };

        for (int i = 0; i < inFrontOf.length; i++) {
            if (inFrontOf[i] == 17 || inFrontOf[i] == 23 || inFrontOf[i] == 24) {
                return true;
            }
        }

        return false;
    }

    private boolean thereIsHole(int[][] scene) {
        for (int i = 1; i < 3; i++) {
            for (int j = 2; j < 8; j++) {
                if (getLocation(i, j, scene) != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean thereIsPipe(int[][] scene) {
        int temp;
        for (int i = 0; i > -2; i--) {
            for (int j = 1; j < 2; j++) {
                temp = getLocation(i, j, scene);
                System.out.println(temp);
                if (temp == 17) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean enemyInFront(int[][] enemies) {
        for (int i = 0; i > -2; i--) {
            for (int j = 1; j < 2; j++) {
                if (getLocation(j, i, enemies) > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateState(MarioForwardModel model, MarioTimer timer) {
        int[][] scene = model.getMarioSceneObservation(1);
        int[][] enemies = model.getMarioEnemiesObservation();

        if (enemyInFront(enemies) || thereIsHole(scene))
            currentState = State.JUMP;
        else {
            if (model.isMarioOnGround())
                currentState = State.RUN;
        }


        if (currentState == State.RUN)
            System.out.println("Current state is RUN");
        else
            System.out.println("Current state is JUMP");

    }
}
