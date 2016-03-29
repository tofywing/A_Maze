package flipboard.interview.yee.maze.Data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

import flipboard.interview.yee.maze.Data.Maze;

/**
 * Created by Yee on 3/28/16.
 */
public class MazeInfoManager {

    public static final String MAZE_PREFERENCE = "preference";
    public static final String MAZE_ID = "info";
    public static final String MAZE_STATUS = "status";
    public static final String MAZE_DATA = "data";
    public static final String MAZE_SIZE = "size";
    SharedPreferences mPreference;

    public MazeInfoManager(Context context) {
        mPreference = context.getSharedPreferences(MAZE_PREFERENCE, Context.MODE_PRIVATE);
    }

    public void saveMazeInfo(ArrayList<Maze> dataSet){
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putInt(MAZE_SIZE, dataSet.size());
        for (int i = 0; i < dataSet.size(); i++) {
            Maze maze = dataSet.get(i);
            editor.putString(MAZE_ID + i, maze.getId());
            editor.putInt(MAZE_STATUS + i, maze.getStatus());
            editor.putString(MAZE_DATA + i, maze.getData());
        }
        editor.apply();
    }

    public ArrayList<Maze> getMazeInfo() {
        ArrayList<Maze> result = new ArrayList<>();
        int size = mPreference.getInt(MAZE_SIZE, 0);
        if (size > 0) {
            int index = 0;
            while (index < size) {
                result.add(new Maze(mPreference.getString(MAZE_ID + index, ""), mPreference.getInt(MAZE_STATUS +
                        index, 0), mPreference.getString(MAZE_DATA + index, "")));
                index++;
            }
            return result;
        }
        return new ArrayList<>();
    }
}
