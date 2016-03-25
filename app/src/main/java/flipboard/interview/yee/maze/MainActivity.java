package flipboard.interview.yee.maze;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

    public static final String MAZE_SAVED = "mazeSaved";
    FloatingActionButton mStartButton;
    FloatingActionButton mClearButton;
    MazeService mService;
    RecyclerView mMazeInfo;
    MazeInfoAdapter mMazeInfoAdapter;
    ArrayList<Maze> dataSet = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        mMazeInfo = (RecyclerView) findViewById(R.id.mazeInfoViewContainer);
        mMazeInfo.setLayoutManager(new LinearLayoutManager(this));
        mMazeInfo.setHasFixedSize(true);
        if (savedInstanceState != null) {
            Toast.makeText(this, "NOT", Toast.LENGTH_LONG).show();
            dataSet = savedInstanceState.getParcelableArrayList(MAZE_SAVED);
        }
        mMazeInfoAdapter = new MazeInfoAdapter(dataSet);
        mStartButton = (FloatingActionButton) findViewById(R.id.startButton);
        int[][] colorPattern = new int[2][];
        colorPattern[0] = new int[]{Color.parseColor("#D3503A")};
        colorPattern[1] = new int[]{Color.parseColor("#000000")};
        ColorStateList red = new ColorStateList(colorPattern, colorPattern[0]);
        ColorStateList black = new ColorStateList(colorPattern, colorPattern[1]);
        mStartButton.setBackgroundTintList(red);
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
        mClearButton = (FloatingActionButton) findViewById(R.id.clearButton);
        mClearButton.setBackgroundTintList(black);
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataSet.clear();
                mMazeInfoAdapter.notifyDataSetChanged();
            }
        });
        mMazeInfo.setAdapter(mMazeInfoAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MAZE_SAVED, dataSet);
        super.onSaveInstanceState(outState);
    }
}
