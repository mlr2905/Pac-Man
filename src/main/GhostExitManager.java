package main;

import java.util.LinkedList;
import java.util.Queue;
import entity.ghost.ExitingHouseState;
import entity.ghost.Ghost;

public class GhostExitManager {
    
    private GamePanel gp;
    private Queue<Ghost> exitQueue = new LinkedList<>();
    private boolean isExitingLaneBusy = false;

    public GhostExitManager(GamePanel gp) {
        this.gp = gp;
    }

    public void requestToExit(Ghost ghost) {
        if (!exitQueue.contains(ghost)) {
            exitQueue.add(ghost);
        }
    }

    public void setExitingLaneBusy(boolean busy) {
        this.isExitingLaneBusy = busy;
    }
    
    public void manageGhostExits() {
        if (!isExitingLaneBusy && !exitQueue.isEmpty()) {
            Ghost ghostToExit = exitQueue.poll();
            isExitingLaneBusy = true;
            ghostToExit.setState(new ExitingHouseState());
        }
    }

    public void reset() {
        exitQueue.clear();
        isExitingLaneBusy = false;
    }
}