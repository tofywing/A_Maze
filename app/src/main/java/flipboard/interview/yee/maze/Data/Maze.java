package flipboard.interview.yee.maze.Data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Yee on 3/16/16.
 */
public class Maze implements Parcelable {

    String id;
    int status;
    String data;

    public Maze(String id, int status, String data) {
        this.id = id;
        this.status = status;
        this.data = data;
    }

    protected Maze(Parcel in) {
        id = in.readString();
        status = in.readInt();
        data = in.readString();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public static final Creator<Maze> CREATOR = new Creator<Maze>() {
        @Override
        public Maze createFromParcel(Parcel in) {
            return new Maze(in);
        }

        @Override
        public Maze[] newArray(int size) {
            return new Maze[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(status);
        dest.writeString(data);
    }
}
