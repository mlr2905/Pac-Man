package entity.ghost.strategy;

import entity.ghost.Ghost;

public interface TargetingStrategy {
   
    int[] getTargetTile(Ghost ghost);
}