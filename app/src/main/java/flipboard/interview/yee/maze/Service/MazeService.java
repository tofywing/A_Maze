package flipboard.interview.yee.maze.Service;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import flipboard.interview.yee.maze.CallBack.MazeServiceCallBack;
import flipboard.interview.yee.maze.Data.Maze;
import flipboard.interview.yee.maze.Data.MazeManager;
import flipboard.interview.yee.maze.Data.ParseDate;
import flipboard.interview.yee.maze.R;

/**
 * Created by Yee on 3/16/16.
 */
public class MazeService {

    public static final String DOMAIN = "https://challenge.flipboard.com/";
    public static final String URL_STEP_FORMAT = "step?s=%s&x=%d&y=%d";
    public static final String URL_CHECK_FORMAT = "check?s=%s&guess=%s";
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILED = 0;

    MazeServiceCallBack mCallBack;
    AsyncTask mAsyncTask;
    Context mContext;
    URL mNewUrl;
    ProgressDialog mDialog;
    String id;
    int x = 0;
    int y = 0;
    int[] previous;
    int[] current;
    boolean end;
    boolean failed;
    String letter;
    ArrayList<int[]> adjacentArray;
    //key: currentCoordinate value: all its previousCoordinates;
    HashMap<int[], ArrayList<int[]>> stepMap;
    TreeMap<Integer, int[]> xPriorityMap;
    TreeMap<Integer, int[]> yPriorityMap;
    TreeMap<Integer, int[]> _xPriorityMap;
    TreeMap<Integer, int[]> _yPriorityMap;
    ArrayList<int[]> prioritySample;
    boolean isMoved;

    public MazeService(Context context, MazeServiceCallBack callBack) {
        mContext = context;
        mCallBack = callBack;
    }

    public void startMaze() {
        mazeInitialize();
    }

    private void mazeInitialize() {
        showDialog(mContext);
        mAsyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String startUrl = "https://challenge.flipboard.com/start";
                try {
                    URL url = new URL(startUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    connection.getInputStream();
                    String stringNewUrl = connection.getURL().toString();
                    id = getId(stringNewUrl);
                    String newUrlInJson = stringNewUrl + "?format=json";
                    return parseStreaming(new URL(newUrlInJson));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return mContext.getString(R.string.server_error);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject jsonData = new JSONObject(s);
                    end = jsonData.optBoolean("end");
                    letter = jsonData.optString("letter");
                    JSONObject adjacent = jsonData.optJSONArray("adjacent").getJSONObject(0);
                    x = adjacent.getInt("x");
                    y = adjacent.getInt("y");
                    mNewUrl = new URL(String.format(DOMAIN + URL_STEP_FORMAT, id, x, y));
                    if (!end) {
                        adjacentArray = new ArrayList<>(2);
                        prioritySample = new ArrayList<>();
                        xPriorityMap = new TreeMap<>(new Comparator<Integer>() {
                            @Override
                            public int compare(Integer lhs, Integer rhs) {
                                return rhs.compareTo(lhs);
                            }
                        });
                        yPriorityMap = new TreeMap<>(new Comparator<Integer>() {
                            @Override
                            public int compare(Integer lhs, Integer rhs) {
                                return rhs.compareTo(lhs);
                            }
                        });
                        _xPriorityMap = new TreeMap<>();
                        _yPriorityMap = new TreeMap<>();
                        previous = new int[]{0, 0};
                        current = new int[]{x, y};
                        isMoved = true;
                        failed = false;
                        stepMap = new HashMap<>();
                        mazeInfo();
                    } else {
                        mCallBack.onMazeActionSuccess(new Maze(id, STATUS_SUCCESS, letter), mDialog);
                        mCallBack.onMazeActionFailed(mDialog);
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void mazeInfo() throws IOException {
        if (!end) {
            mAsyncTask = new AsyncTask<URL, Void, String>() {
                @Override
                protected String doInBackground(URL... params) {
                    try {
                        return parseStreaming(params[0]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return mContext.getString(R.string.server_error);
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    try {
                        JSONObject jsonData = new JSONObject(s);
                        end = jsonData.optBoolean("end");
                        if (end) {
                            letter += jsonData.optString("letter");
                            mDialog.setMessage(letter);
                            mCallBack.onMazeActionSuccess(new Maze(id, failed ? STATUS_FAILED : STATUS_SUCCESS, letter),
                                    mDialog);
                            mCallBack.onMazeActionFailed(mDialog);
                        }
                        if (stepMap.containsKey(current)) stepMapUpdateStep(current);
                        else stepMapNewStep(current);
                        letter += jsonData.optString("letter");
                        JSONArray adjacent = jsonData.optJSONArray("adjacent");
                        for (int i = 0; i < adjacent.length(); i++) {
                            int x = adjacent.getJSONObject(i).optInt("x");
                            int y = adjacent.getJSONObject(i).optInt("y");
                            adjacentArray.add(new int[]{x, y});
                        }
                        if (adjacentArray.size() == 1) {
                            previous = current;
                            x = adjacentArray.get(0)[0];
                            y = adjacentArray.get(0)[1];
                        } else {
                            ArrayList<int[]> currentCheckArray = stepMap.get(current);
                            for (int[] array : adjacentArray) {
                                boolean pass = true;
                                for (int[] checkArray : currentCheckArray) {
                                    if (Arrays.equals(array, checkArray)) pass = false;
                                }
                                if (pass) {
                                    int tempX = array[0];
                                    int tempY = array[1];
                                    isMoved = true;
                                    prioritySample.add(new int[]{tempX, tempY});
                                }
                            }
                            int[] tempArray = isMoved ? getBetterStep(prioritySample) : getBetterStep(adjacentArray);
                            previous = new int[]{x, y};
                            x = tempArray[0];
                            y = tempArray[1];
                        }
                        current = new int[]{x, y};
                        prioritySample.clear();
                        xPriorityMap.clear();
                        yPriorityMap.clear();
                        _yPriorityMap.clear();
                        _xPriorityMap.clear();
                        adjacentArray.clear();
                        mNewUrl = new URL(String.format(DOMAIN + URL_STEP_FORMAT, id, x, y));
                        mDialog.setMessage(letter);
                        isMoved = false;
                        mazeInfo();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }.execute(mNewUrl);
        }
    }

    public void mazeTest(final String Id, final String data) {
        showDialog(mContext);
        mAsyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    return parseStreaming(new URL(DOMAIN + String.format(URL_CHECK_FORMAT, Id, data)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject jsonData = new JSONObject(s);
                    mCallBack.onMazeActionSuccess(null,mDialog);
                    mCallBack.onCheckActionSuccess(jsonData.optBoolean("success"));
                    mCallBack.onMazeActionFailed(mDialog);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }


    void showDialog(final Context context) {
        mDialog = new ProgressDialog(context);
        mDialog.setMessage(context.getString(R.string.dialog_loading));
        mDialog.setCancelable(false);
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopService();
            }
        });
        mDialog.show();
    }

    String getId(String url) {
        Pattern pattern = Pattern.compile(".*?=(.*?)&.*");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String parseStreaming(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) stringBuilder.append(line);
        return stringBuilder.toString();
    }

    int[] getBetterStep(ArrayList<int[]> prioritySample) {
        int tempX;
        int tempY;
        int[] result = new int[]{0, 0};
        for (int[] array : prioritySample) {
            tempX = array[0];
            tempY = array[1];
            if (tempX > x) {
                xPriorityMap.put(tempX - x, new int[]{tempX, tempY});
            }
        }
        if (!xPriorityMap.isEmpty()) {
            result = xPriorityMap.firstEntry().getValue();
        } else {
            for (int[] array : prioritySample) {
                tempX = array[0];
                tempY = array[1];
                if (tempY < y) {
                    yPriorityMap.put(y - tempY, new int[]{tempX, tempY});
                }
            }
            if (!yPriorityMap.isEmpty()) {
                result = yPriorityMap.firstEntry().getValue();
            } else {
                for (int[] array : prioritySample) {
                    tempX = array[0];
                    tempY = array[1];
                    if (tempX < x) {
                        _xPriorityMap.put(x - tempX, new int[]{tempX, tempY});
                    }
                }
                if (!_xPriorityMap.isEmpty()) {
                    result = _xPriorityMap.firstEntry().getValue();
                } else {
                    for (int[] array : prioritySample) {
                        tempX = array[0];
                        tempY = array[1];
                        if (y < tempY) {
                            _yPriorityMap.put(tempY - y, new int[]{tempX, tempY});
                        }
                    }
                    if (!_yPriorityMap.isEmpty()) {
                        result = _yPriorityMap.firstEntry().getValue();
                    }
                }
            }
        }
        return result;
    }

    void stepMapNewStep(int[] current) {
        ArrayList<int[]> tempArray = new ArrayList<>();
        tempArray.add(previous);
        stepMap.put(current, tempArray);
    }

    void stepMapUpdateStep(int[] current) {
        ArrayList<int[]> tempArray = stepMap.get(current);
        tempArray.add(previous);
        stepMap.put(current, tempArray);
    }

    void stopService() {
        mAsyncTask.cancel(true);
        mCallBack.onMazeActionSuccess(new Maze(id, STATUS_FAILED, letter), mDialog);
    }
}
