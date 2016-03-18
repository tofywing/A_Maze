package flipboard.interview.yee.maze.Data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Yee on 3/17/16.
 */
public class MazeManager {
    public static final String MAZE_STATE = "mazeState";
    public static final String STATUS = "mazeStatus";
    public static final String ID = "id";
    public static final String DATA = "data";
    private SharedPreferences mazePrefs;

    MazeManager(Context context) {
        mazePrefs = context.getSharedPreferences(MAZE_STATE, Context.MODE_PRIVATE);
    }

    public void saveMazeState(Maze maze) {
        SharedPreferences.Editor editor = mazePrefs.edit();
        editor.putString(ID, maze.getId());
        editor.putInt(STATUS, maze.getStatus());
        editor.putString(DATA, maze.getData());
        editor.apply();
    }

    //Maze only with ID and Status, Data
    Maze getSavedMaze() {
        return new Maze(mazePrefs.getString(ID, null),mazePrefs.getInt(STATUS,0),mazePrefs.getString(DATA,null));
    }
}
