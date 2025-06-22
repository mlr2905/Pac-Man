package entity.ghost;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import map.MapData;

public class GhostNavigator {

    public static String getShortestPathDirection(int startCol, int startRow, int targetCol, int targetRow) {
        Queue<int[]> queue = new LinkedList<>();
        Map<String, String> parentMap = new HashMap<>();

        queue.offer(new int[]{startCol, startRow});
        parentMap.put(startCol + "," + startRow, "start");

        int[] dRow = {-1, 1, 0, 0}; 
        int[] dCol = {0, 0, -1, 1}; 

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            if (current[0] == targetCol && current[1] == targetRow) {
                return getFirstStepFromPath(startCol, startRow, targetCol, targetRow, parentMap);
            }

            for (int i = 0; i < 4; i++) {
                int nextCol = current[0] + dCol[i];
                int nextRow = current[1] + dRow[i];
                String nextCoord = nextCol + "," + nextRow;

                if (isValidTile(nextCol, nextRow) && !parentMap.containsKey(nextCoord)) {
                    parentMap.put(nextCoord, current[0] + "," + current[1]);
                    queue.offer(new int[]{nextCol, nextRow});
                }
            }
        }
        return "none"; 
    }

    private static boolean isValidTile(int col, int row) {
        if (row < 0 || row >= MapData.INITIAL_MAP_DATA.length || col < 0 || col >= MapData.INITIAL_MAP_DATA[0].length) {
            return false;
        }
        int tileValue = MapData.INITIAL_MAP_DATA[row][col];
        return tileValue == 0 || tileValue == 2 || tileValue == 7;
    }

    private static String getFirstStepFromPath(int startCol, int startRow, int targetCol, int targetRow, Map<String, String> parentMap) {
        LinkedList<int[]> path = new LinkedList<>();
        int[] current = {targetCol, targetRow};

        while (true) {
            path.addFirst(current);
            String parentCoord = parentMap.get(current[0] + "," + current[1]);
            if (parentCoord == null || "start".equals(parentCoord)) break;

            String[] parts = parentCoord.split(",");
            current = new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
        }

        if (path.size() > 1) {
            int[] nextStep = path.get(1);
            if (nextStep[0] > startCol) return "right";
            if (nextStep[0] < startCol) return "left";
            if (nextStep[1] > startRow) return "down";
            if (nextStep[1] < startRow) return "up";
        }
        return "none";
    }
}