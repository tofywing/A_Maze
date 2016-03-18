package flipboard.interview.yee.maze.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import flipboard.interview.yee.maze.CallBack.MazeServiceCallBack;
import flipboard.interview.yee.maze.Data.Maze;
import flipboard.interview.yee.maze.R;
import flipboard.interview.yee.maze.Service.MazeService;

/**
 * Created by Yee on 3/18/16.
 */
public class MazeInfoAdapter extends RecyclerView.Adapter<MazeInfoAdapter.MazeViewHolder> {

    TextView mStatus;
    TextView mID;
    TextView mData;
    Button mTestButton;

    class MazeViewHolder extends RecyclerView.ViewHolder {

        public MazeViewHolder(View itemView) {
            super(itemView);
            mStatus = (TextView) itemView.findViewById(R.id.mazeInfoStatus);
            mID = (TextView) itemView.findViewById(R.id.mazeInfoID);
            mData = (TextView) itemView.findViewById(R.id.mazeInfoDataReturn);
            mTestButton = (Button) itemView.findViewById(R.id.mazeInfoTestButton);
        }
    }

    ArrayList<Maze> dataSet;

    public MazeInfoAdapter(ArrayList<Maze> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public MazeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_maze_info, parent, false);
        return new MazeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MazeViewHolder holder, final int position) {
        mStatus.setText(dataSet.get(position).getStatus() == 1 ? "Success" : "Failed");
        mID.setText(dataSet.get(position).getId());
        mData.setText(dataSet.get(position).getData());
        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
               MazeService mazeService = new MazeService(v.getContext(), new MazeServiceCallBack() {
                    @Override
                    public void onMazeActionSuccess(Maze maze, ProgressDialog dialog) {
                          dialog.dismiss();
                    }

                    @Override
                    public void onMazeActionFailed(ProgressDialog dialog) {

                    }

                    @Override
                    public void onCheckActionSuccess(boolean success) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                        alert.setMessage(v.getContext().getString(R.string.mazeInfo_TestResult) + (success ?
                                "Success" :
                                "Failed"));
                        alert.setCancelable(false);
                        alert.setNegativeButton(R.string.mazeInfo_AlertButton, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                    }
                });
                mazeService.mazeTest(dataSet.get(position).getId(),dataSet.get(position).getData());
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
