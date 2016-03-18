package flipboard.interview.yee.maze.CallBack;

import android.app.ProgressDialog;

import flipboard.interview.yee.maze.Data.Maze;

/**
 * Created by Yee on 3/16/16.
 */
public interface MazeServiceCallBack {

    void onMazeActionSuccess(Maze maze, ProgressDialog dialog);
    void onMazeActionFailed(ProgressDialog dialog);
    void onCheckActionSuccess(boolean success);
}
