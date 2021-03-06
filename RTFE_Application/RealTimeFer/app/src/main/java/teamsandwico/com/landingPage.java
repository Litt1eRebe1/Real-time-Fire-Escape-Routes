package teamsandwico.com;

import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Date;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class landingPage extends AppCompatActivity {
    //note: activity elements
    private TextView text_result, text_update;
    private EditText input_url, input_name, input_pass;
    private Button button_parse;
    private Button button_check;
    private pl.droidsonroids.gif.GifImageView image_loading;
    //note: variable objects
    private int screentap = 0;
    private OkHttpClient client = new OkHttpClient();
    private Handler handler = new Handler();
    private String defaultHost = "192.168.137.1:8080";
    private String position, status, route;
    public Boolean login = false, binded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        text_result = findViewById(R.id.text_result);
        text_update = findViewById(R.id.text_update);
        input_url = findViewById(R.id.input_url);
        input_name = findViewById(R.id.input_name);
        input_pass = findViewById(R.id.input_pass);
        button_parse = findViewById(R.id.button_parse);
        button_check = findViewById(R.id.button_check);
        image_loading = findViewById(R.id.image_loading);

        text_result.setMovementMethod(new ScrollingMovementMethod());
        text_update.setVisibility(View.GONE);

        button_parse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!input_name.getText().equals("")) {
                    loginState("disable");
                    button_parse.setVisibility(View.GONE);
                    image_loading.setVisibility(View.VISIBLE);
                    new Login().execute();
                }
            }
        });

        button_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binded) {
                    new StatusCheck().execute();
                    if (position != null && status != null && route != null) {
                        if (route != null) {
                            View main = findViewById(R.id.relativeLayout);
                            main.setBackgroundColor(identifyColor(route));
                        }
                        String updateMsg =
                                "Time: " + getCurrentTime() + "\n" +
                                        "Status: " + status + "\n" +
                                        "Position: " + position + "\n" +
                                        "Route: " + route + "\n";
                        text_update.setText(updateMsg);
                        if (text_update.getVisibility() == View.GONE)
                            text_update.setVisibility(View.VISIBLE);
                    } else {
                        //note: display when status is safe
                        View main = findViewById(R.id.relativeLayout);
                        main.setBackgroundColor(identifyColor("-1"));

                        String updateMsg =
                                "Time: " + getCurrentTime() + "\n" +
                                        "Status: " + status + "\n";
                        text_update.setText(updateMsg);
                        if (text_update.getVisibility() == View.GONE)
                            text_update.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private String getCurrentTime() {
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime.toString();
    }
    /**
     * function will return the devices unique id of the current device
     * @return string: devices unique id
     */
    private String getUniqueId(){
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    /**
     * function identifies the color from the route
     * @param route: string of the route value
     * @return an hexadecimal color value
     */
    private int identifyColor(String route){
        switch(route.replaceAll("\\s+","")){
            case "0": return getResources().getColor(R.color.Orange);
            case "1": return getResources().getColor(R.color.Green);
            case "2": return getResources().getColor(R.color.Blue);
            case "3": return getResources().getColor(R.color.Red);
            case "4": return getResources().getColor(R.color.Pink);
            case "5": return getResources().getColor(R.color.Purple);
            default: return getResources().getColor(R.color.White);
        }
    }

    private void loginState(String state){
        if (state.equals("disable")) {
            input_url.setEnabled(false);
            input_name.setEnabled(false);
            input_pass.setEnabled(false);
        }else if (state.equals("enable")){
            input_url.setEnabled(true);
            input_name.setEnabled(true);
            input_pass.setEnabled(true);
        }
    }

    private void loginSuccess(){
        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        new BindUser().execute();
                        if (binded) {
                            new StatusCheck().execute();
                            if (position != null && status != null && route != null) {
                                if (route != null) {
                                    View main = findViewById(R.id.relativeLayout);
                                    main.setBackgroundColor(identifyColor(route));
                                }
                                String updateMsg = "Status: " + status + "\n" +
                                        "Position: " + position + "\n" +
                                        "Route: " + route + "\n";
                                text_update.setText(updateMsg);
                                if (text_update.getVisibility() == View.GONE)
                                    text_update.setVisibility(View.VISIBLE);
                            } else if (status != null) {
                                View main = findViewById(R.id.relativeLayout);
                                main.setBackgroundColor(identifyColor("-1"));

                                String updateMsg =
                                        "Time: " + getCurrentTime() + "\n" +
                                                "Status: " + status + "\n";
                                text_update.setText(updateMsg);
                                if (text_update.getVisibility() == View.GONE)
                                    text_update.setVisibility(View.VISIBLE);
                            }
                        }
                        handler.postDelayed(this, 2500);

                    }
                }, 1000);
    }
    /**
     * function will print json object to the debug terminal
     * @param string: is a string of the json object
     */
    private void printJson(String string){
        try {
            JSONObject json = new JSONObject(string);
            text_result.append("Host: " + (input_url.getText().length() == 0 ? defaultHost : input_url.getText()) + "\n");
            text_result.append("Status: " + json.getString("status") + "\n");
            text_result.append("Msg: " + json.getString("msg") + "\n");
        }catch (Exception e){
            text_result.append("Error: " + e.getMessage() + "\n");
        }
    }
    /**
     * removes interface of the log in
     */
    private void removeLogin(){
        input_url.setVisibility(View.GONE);
        input_name.setVisibility(View.GONE);
        input_pass.setVisibility(View.GONE);
        button_parse.setVisibility(View.GONE);
    }
    /**
     * function is used to detect screen tap, in order to activate developer terminal
     * @param v
     */
    public void screenTapped(View v){
        screentap++;
        if (screentap == 5 && text_result.getVisibility() == View.GONE) {
            text_result.setVisibility(View.VISIBLE);
            text_result.append("Developer Terminal:\n");
            text_result.append("Android Id: " + getUniqueId() + "\n");
        }else if (screentap > 9 && text_result.getVisibility() == View.VISIBLE){
            screentap = 0;
            text_result.setVisibility(View.GONE);
            text_result.setText("");
        }
    }

    public void showToast(String text, int image){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_root));

        TextView text_toast = layout.findViewById(R.id.toast_text);
        ImageView image_toast = layout.findViewById(R.id.toast_image);

        text_toast.setText(text);
        image_toast.setImageResource(image);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);

        toast.show();

    }

    private void stateOfActivity(String state){
        switch(state){
            case "logged":
                button_check.setVisibility(View.VISIBLE);
                break;
        }
    }
    /**
     * class contains async call to service in order to login
     */
    public class Login extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params){
             try{
                 String json = "{'type': login," +
                         " 'email' : '"+input_name.getText().toString()+"'," +
                         " 'pass' : '"+input_pass.getText().toString()+"'}";

                 final MediaType JSON
                         = MediaType.parse("application/json; charset=utf-8");

                 RequestBody body = RequestBody.create(JSON, json);

                 String url = (input_url.getText().length() == 0 ? defaultHost : input_url.getText().toString());

                 Request request = new Request.Builder()
                         .url("http://" +
                                 url +
                                 "/database")
                         .post(body)
                         .build();

                 Response resp = client.newCall(request).execute();
                 return resp.body().string();
             }catch(Exception e){
                 return "Error on connection: " + e.getMessage();
             }
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            printJson(s);
            try {
                JSONObject json = new JSONObject(s);
                if (json.getString("status").equals("true")){
                    login = true;
                    image_loading.setVisibility(View.GONE);
                    removeLogin();
                    stateOfActivity("logged");
                    loginSuccess();
                }else{
                    throw new Exception("Invalid credentials");
                }
            }catch(Exception e){
                String msg = "Could not sign in!";
                int img = R.drawable.ic_toast_error;
                showToast(msg, img);
                loginState("enable");
                image_loading.setVisibility(View.GONE);
                button_parse.setVisibility(View.VISIBLE);
                text_result.append("Error on login: " + e.getMessage());
            }


        }
    }
    /**
     * class contains async call to service in order to call status check
     */
    public class StatusCheck extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params){
            try{
                String json = "{'type': personInfo," +
                        " 'name' : '"+input_name.getText().toString()+"'," +
                        " 'device_id' : '"+ getUniqueId() +"'}";

                final MediaType JSON
                        = MediaType.parse("application/json; charset=utf-8");

                RequestBody body = RequestBody.create(JSON, json);

                String url = (input_url.getText().length() == 0 ? defaultHost : input_url.getText().toString());

                Request request = new Request.Builder()
                        .url("http://" +
                                url +
                                "/building")
                        .post(body)
                        .build();

                Response resp = client.newCall(request).execute();
                return resp.body().string();
            }catch(Exception e){
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            try {
                JSONObject json = new JSONObject(s);
                String[] message = json.getString("message").split("-");
                position = null;
                status = null;
                route = null;
                for (String item: message) {
                    String[] value = item.split(":");
                    switch (value[0].replaceAll("\\s+","")){
                        case "ID":
                            text_result.append("ID: " + value[1] + "\n");
                            break;
                        case "Position":
                            position = value[1];
                            text_result.append("Position: " + position + "\n");
                            break;
                        case "Status":
                            status = value[1];
                            text_result.append("Status: " + status + "\n");
                            break;
                        case "AssignedRouteID":
                            route = value[1];
                            text_result.append("Route: " + route + "\n");
                            break;
                    }
                }
            }catch(Exception e){
                text_result.append("Error on function Post: " + e.getMessage() + "\n");
            }
        }
    }

    public class BindUser extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params){
            try{
                String json = "{'type': 'bind'," +
                        " 'id' : 1," +
                        " 'device_id' : '"+getUniqueId()+"'}";

                final MediaType JSON
                        = MediaType.parse("application/json; charset=utf-8");

                RequestBody body = RequestBody.create(JSON, json);

                String url = (input_url.getText().length() == 0 ? defaultHost : input_url.getText().toString());

                Request request = new Request.Builder()
                        .url("http://" +
                                url +
                                "/building")
                        .post(body)
                        .build();

                Response resp = client.newCall(request).execute();
                return resp.body().string();
            }catch(Exception e){
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            printJson(s);
            try {
                JSONObject json = new JSONObject(s);
                text_result.append(json.toString());
                if (json.getString("status").equals("true") && json.getString("message").equals("true")){
                    binded = true;
                }
            }catch(Exception e){
                text_result.append("Error: " + e.getMessage());
            }


        }
    }

}
