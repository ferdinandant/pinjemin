package pinjem.pinjemin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class BackgroundTaskDatabase extends AsyncTask<Void, Object, Void> {

    //String host = "http://pinjemin.coolpage.biz/";
    static String host = "http://kemalamru.cloudapp.net/ppl/";
    String json_string;

    Activity activity;
    Context context;
    String path;
    String method;
    String objectType;
    TreeMap<String, String> inputSend;
    //String[] inputSend;

    //Bagian Login
    String username;
    String password;
    String uid;
    String realname;
    boolean loginSuccess = false;
    boolean pernahLogin = false;

    //Bagian RecyclerView
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    // Penampung Object RecyclerView
    String[] inputReceive;
    ArrayList<PostSupply> arraySupply;
    ArrayList<PostDemand> arrayDemand;

    /** ==============================================================================
     * Login constructor
     * ============================================================================== */
    public BackgroundTaskDatabase(Context context, String path, String method, String username, String password) {
        this.context = context;
        activity = (Activity) context;
        this.path = path;
        this.method = method;
        this.username = username;
        this.password = password;
    }

    /** ==============================================================================
     * Constructor Send To Database
     * ============================================================================== */
    public BackgroundTaskDatabase(Context context, String path, String method, TreeMap<String, String> inputSend) {
        this.context = context;
        activity = (Activity) context;
        this.path = path;
        this.method = method;
        this.inputSend = inputSend;
    }

    /** ==============================================================================
     * Constructor Receive From Database
     * ============================================================================== */
    public BackgroundTaskDatabase(Context context, String path, String method, String objectType, String[] inputReceive) {
        this.context = context;
        activity = (Activity) context;
        this.path = path;
        this.method = method;
        this.objectType = objectType;
        this.inputReceive = inputReceive;
    }

    @Override
    protected void onPreExecute() {
        if (method.equalsIgnoreCase("receive")) {
            if (objectType.equalsIgnoreCase("postSupply")) {
                arraySupply = new ArrayList<>();
                recyclerView = (RecyclerView) activity.findViewById(R.id.recylerViewSupply);

                adapter = new RecyclerAdapter(arraySupply, "postSupply", 1);
                layoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);

                recyclerView.addOnItemTouchListener(new TimelineSupplyFragment.RecyclerTouchListener(context, recyclerView, new TimelineSupplyFragment.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        PostSupply postSupply = arraySupply.get(position);
                        context.startActivity(new Intent(context, LoginActivity.class));
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

            } else if (objectType.equalsIgnoreCase("postDemand")) {
                arrayDemand = new ArrayList<>();
                recyclerView = (RecyclerView) activity.findViewById(R.id.recylerViewDemand);
                adapter = new RecyclerAdapter(arrayDemand, "postDemand");

                recyclerView.setAdapter(adapter);
                layoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);

                recyclerView.addOnItemTouchListener(new TimelineSupplyFragment.RecyclerTouchListener(context, recyclerView, new TimelineSupplyFragment.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        PostSupply postSupply = arraySupply.get(position);
                        context.startActivity(new Intent(context, LoginActivity.class));
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));
            }
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (method.equalsIgnoreCase("receive")) {
            try {
                URL url = new URL(host + path);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder stringBuilder = new StringBuilder();

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                json_string = stringBuilder.toString().trim();

                try {

                    JSONObject jsonObject = new JSONObject(json_string);
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");

                    String[] input = new String[inputReceive.length];

                    int count = 0;
                    while (count < jsonArray.length()) {

                        JSONObject JO = jsonArray.getJSONObject(count);

                        for (int ii = 0; ii < input.length; ii++) {
                            input[ii] = JO.getString(inputReceive[ii]);
                        }

                        if (objectType.equalsIgnoreCase("postSupply")) {

                            String formatTanggal = Utility.formatPostTimestamp(input[0]);
                            PostSupply postSupply = new PostSupply(input[1], input[2], formatTanggal);
                            publishProgress(postSupply);

                        } else if (objectType.equalsIgnoreCase("postDemand")) {
                            String formatTanggal = Utility.formatPostTimestamp(input[0]);
                            PostDemand postDemand = new PostDemand(input[1], input[2], formatTanggal);
                            publishProgress(postDemand);
                        }


                        count++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("JSON_STRING", json_string);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        } else if (method.equalsIgnoreCase("send")) {
            try {
                Connect.submitPhp(path, inputSend);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (method.equalsIgnoreCase("register")) {

            try {



                Connect.submitPhp(path, inputSend);
                //String result = submitPhp(path, inputSend);
                //Log.d("hasil database", result);


            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }

            if (method.equalsIgnoreCase("register")) {
                realname = inputSend.get("realname");
            }

        } else if (method.equals("login")) {
            try {
                URL url = new URL(host + path);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                              URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                Log.d("Data Login", data);

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder stringBuilder = new StringBuilder();

                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                json_string = stringBuilder.toString().trim();

                JSONObject jsonObject = new JSONObject(json_string);
                JSONArray jsonArray = jsonObject.getJSONArray("server_response");

                if (jsonArray.length() == 0) {

                } else {
                    JSONObject JO = jsonArray.getJSONObject(0);

                    uid = JO.getString("uid");
                    realname = JO.getString("realname");

                    loginSuccess = true;

                    if (!realname.equalsIgnoreCase("")) {
                        pernahLogin = true;
                    }
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                Log.d("Disconnect Login", "Disconnected");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Object... object) {

        if (method.equalsIgnoreCase("receive")) {
            if (objectType.equalsIgnoreCase("postSupply")) {
                arraySupply.add((PostSupply) object[0]);
                adapter.notifyDataSetChanged();

            } else if (objectType.equalsIgnoreCase("postDemand")) {
                arrayDemand.add((PostDemand) object[0]);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (method.equalsIgnoreCase("login")) {
            if (loginSuccess) {
                SessionManager session = new SessionManager(context);

                if (pernahLogin) {
                    session.createLoginSession(uid, username);
                    session.createRegisterSession(realname);

                    // Starting MainActivity
                    context.startActivity(new Intent(context, MainActivity.class));
                } else {
                    session.createLoginSession(uid, username);
                    context.startActivity(new Intent(context, RegisterActivity.class));
                }

            } else {
                Toast.makeText(context, "Login Failed", Toast.LENGTH_LONG).show();
            }

        } else if (method.equalsIgnoreCase("register")) {
            SessionManager session = new SessionManager(context);
            session.createRegisterSession(realname);

            // Starting MainActivity
            context.startActivity(new Intent(context, MainActivity.class));
        }
    }
}