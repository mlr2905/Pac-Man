package controller.strategy;

import entity.ghost.Ghost;

public interface TargetingStrategy {
   
    int[] getTargetTile(Ghost ghost);
}