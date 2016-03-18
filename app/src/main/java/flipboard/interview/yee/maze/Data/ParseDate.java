package flipboard.interview.yee.maze.Data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yee on 3/16/16.
 */
public interface ParseDate {
    void parseJSON(JSONObject jsonData) throws JSONException;
}
