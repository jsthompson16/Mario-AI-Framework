package agents.ThompsonWeilerAgent;

import engine.core.MarioForwardModel;
import engine.core.MarioTimer;

enum State {
    RUN, JUMP, WAIT, WALK, JUMPBACK, RETREAT
}

public class StateMachine {

    State currentState = State.RUN;
    State previousState;
    float currentYPosition;
    float previousYPosition = 0;
    boolean groundRun = false;
    boolean groundWalk = false;
    int stuckCounter = 0;
    int groundRunCounter = 0;
    int groundWalkCounter = 0;
    int continuousJumpCounter = 0;

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
            case WALK:
                action = new boolean[] {false, true, false, false, false};
                break;
            case JUMPBACK:
                action = new boolean[] {true, false, false, true, true};
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

    private boolean enemyInFront(int[][] enemies) {
        for (int i = 0; i > -2; i--) {
            for (int j = 1; j < 4; j++) {
                if (getLocation(j, i, enemies) > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public void checkforJump(int[][] scene, int[][] enemies) {
        if (enemyInFront(enemies) || thereIsHole(scene) || thereIsObstacle(scene)) {
            currentState = State.JUMP;

            if (previousState == State.JUMP) {
                continuousJumpCounter++;
                if (continuousJumpCounter == 4) {
                    currentState = State.WAIT;
                    continuousJumpCounter = 0;
                }
            }
        }
    }

    public void updateState(MarioForwardModel model, MarioTimer timer) {
        int[][] scene = model.getMarioSceneObservation(1);
        int[][] enemies = model.getMarioEnemiesObservation();
        float[] position = model.getMarioFloatPos();

        currentYPosition = position[1];

        if (groundRun) {
            currentState = State.RUN;
            checkforJump(scene, enemies);

            if (currentState == State.RUN) {
                groundRunCounter++;
                if (groundRunCounter == 3) {
                    groundRun = false;
                    groundRunCounter = 0;
                }
            }

            return;
        }

        if (groundWalk) {
            currentState = State.WALK;
            checkforJump(scene, enemies);

            if (currentState == State.WALK) {
                groundWalkCounter++;
                if (groundWalkCounter == 6) {
                    groundWalk = false;
                    groundWalkCounter = 0;
                }
            }

            return;
        }

        if (model.isMarioOnGround()) {
            double rand = Math.random();
            System.out.println(rand);
            if (rand <= 0.60) {
                currentState = State.RUN;
                //groundRun = true;
            }
            else {
                currentState = State.WALK;
                //groundWalk = true;
            }
        }

        checkforJump(scene, enemies);

        if (previousYPosition != 0 && previousYPosition == currentYPosition && ((previousState == State.WALK) || (previousState == State.RUN))) {
            stuckCounter++;
            if (stuckCounter == 2) {
                System.out.println("Hello i am here");
                currentState = State.JUMP;
                stuckCounter = 0;
            }
        }

        previousState = currentState;
        previousYPosition = currentYPosition;

        if (currentState == State.RUN)
            System.out.println("Current state is RUN");
        else if (currentState == State.WAIT)
            System.out.println("Current state is WAIT");
        else if (currentState == State.WALK)
            System.out.println("Current state is WALK");
        else if (currentState == State.JUMPBACK)
            System.out.println("Current state is JUMPBACK");
        else
            System.out.println("Current state is JUMP");

    }
}
