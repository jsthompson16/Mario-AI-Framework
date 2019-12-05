package agents.ThompsonWeilerAgent;

import engine.core.MarioAgent;
import engine.core.MarioForwardModel;
import engine.core.MarioTimer;
import engine.helper.MarioActions;

public class Agent implements MarioAgent {
    private boolean action[];
    private StateMachine machine;

    @Override
    public void initialize(MarioForwardModel model, MarioTimer timer) {
        this.action = new boolean[MarioActions.numberOfActions()];
        this.machine = new StateMachine();
    }

    @Override
    public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
        action = this.machine.getNextAction(model, timer);
        return action;
    }

    @Override
    public String getAgentName() {
        return "ThompsonWeilerAgent";
    }
}
