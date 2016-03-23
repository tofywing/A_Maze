package flipboard.interview.yee.maze;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import flipboard.interview.yee.maze.Adapter.MazeInfoAdapter;
import flipboard.interview.yee.maze.CallBack.MazeServiceCallBack;
import flipboard.interview.yee.maze.Data.Maze;
import flipboard.interview.yee.maze.Service.MazeService;

/**
 * Created by Yee on 3/16/16.
 */
public class MainActivity extends Activity {
    FloatingActionButton mStartButton;
    MazeService mService;
    RecyclerView mMazeInfo;
    MazeInfoAdapter mMazeInfoAdapter;
    static ArrayList<Maze> dataSet = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        mMazeInfo = (RecyclerView) findViewById(R.id.mazeInfoViewContainer);
        mMazeInfo.setLayoutManager(new LinearLayoutManager(this));
        mMazeInfo.setHasFixedSize(true);
        mMazeInfoAdapter = new MazeInfoAdapter(dataSet);
        mStartButton = (FloatingActionButton) findViewById(R.id.startButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mService = new MazeService(v.getContext(), new MazeServiceCallBack() {
                    @Override
                    public void onMazeActionSuccess(Maze maze, ProgressDialog dialog) {
                        dataSet.add(maze);
                        mMazeInfoAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                    @Override
                    public void onMazeActionFailed(ProgressDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onCheckActionSuccess(boolean success) {

                    }
                });
                mService.startMaze();
            }
        });
        mMazeInfo.setAdapter(mMazeInfoAdapter);
    }
}
