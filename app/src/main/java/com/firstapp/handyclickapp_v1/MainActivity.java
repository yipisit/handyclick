package com.firstapp.handyclickapp_v1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener{

    String[] lastRequest = new String[]{"false"}; // {ACTION},{TIMESTAMP}
    String[] tempRequest;
    String tempAction;
    String tempTime;
    boolean excecute = false;
    String[] currentRequest = new String[]{"false"};
    int lastTimestamp;
    int currentTimestamp;


    //private TextView mTextViewResult;
    private TextView mBatteryStatus;
    //private TextView mPhoneTime;
    //private TextView mPressedTime;
    static final String TAG = "MainActivity";
    private Button button;
    private TextView textView;
    String phone_nr = "Empty";
    String website_name = "Empty";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.buttonAct2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity2();
            }
        });

        textView = (TextView) findViewById(R.id.textView2);

        openDialog();


        //String onlyTime = currentTime.substring(9,16);

       // mTextViewResult = findViewById(R.id.text_view_result);
        mBatteryStatus = findViewById(R.id.battery_status);
        //mPhoneTime = findViewById(R.id.phone_time);
        //mPressedTime = findViewById(R.id.pressed_time);
        //Button buttonParse = findViewById(R.id.button_parse);
//        Handler autoUpdateHandler = new Handler();
//        autoUpdateHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                sendRequest();
//                autoUpdateHandler.postDelayed(this,1000);
//            }
//        },500);

        sendRequest();

//        jsonParse();
    }

    public void sendRequest() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            requestWebPage();
                        } catch (Exception e) {
                            Log.d("webMessage", "Knop gedrukt mislukt: '" + e);
                        }
                    }
                }.start();
            }
        });
    }

    public void requestWebPage() throws InterruptedException {
        OkHttpClient client = new OkHttpClient();
        String url = "https://vanginkelschoonmaak.nl/handyclick/data.php?buttonset=1";
        Request request = new Request.Builder()
                .url(url)
                .build();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                countDownLatch.countDown();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    int phoneTime = (int) (System.currentTimeMillis()/1000);
                    int timestamp;

                    try {
                        JSONObject obj = new JSONObject(myResponse);
                        timestamp = Integer.parseInt(obj.getString("timestamp"));
                        currentTimestamp = timestamp;

                        tempAction = obj.getString("action");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (lastTimestamp == 0) {
                        lastTimestamp = currentTimestamp;
                    }

                    if (currentTimestamp != lastTimestamp) {
                        Log.d("webMessage", "Knop gedrukt op tijdstip: '" + currentTimestamp + "' en actie: " + tempAction + "'");

                        // Set timestamp06
                        lastTimestamp = currentTimestamp;

                        excecute = true;
                    }

                    countDownLatch.countDown();
                }
            }
        });
        //Does this value get set to true before returning if appropriate.
        countDownLatch.await();

        if (excecute) {
            // Excecute the action
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    doAction();

                }
            });
        }

        Thread.sleep(1000); // Niet nodig, alleen voor de website niet te hard te belasten.

        // Start again
        requestWebPage();
    }

    public void doAction() {
        if (tempAction.equals("1_single")) {
            textView.setText("BUTTON1 GELEZEN");
            Log.d(TAG, "Website will be opened");
            String google = website_name;
            Uri webaddress = Uri.parse(google);

            Intent gotoGoogle = new Intent(Intent.ACTION_VIEW, webaddress); //No application context to get, so we'll go outside our app (using ACTION VIEW)
            if (gotoGoogle.resolveActivity(getPackageManager()) != null) {
                startActivity(gotoGoogle);
            }

        }
        if (tempAction.equals("2_double")) {
            textView.setText("BUTTON2 GELEZEN");
            Log.d(TAG, "Van Mossel will be opened");
            String google = "https://www.vanmossel.nl";
            Uri webaddress = Uri.parse(google);

            Intent gotoGoogle = new Intent(Intent.ACTION_VIEW, webaddress); //No application context to get, so we'll go outside our app (using ACTION VIEW)
            if (gotoGoogle.resolveActivity(getPackageManager()) != null) {
                startActivity(gotoGoogle);
            }
        }
        if (tempAction.equals("4_double")) {
            textView.setText("BUTTON4 GELEZEN");
            Log.d(TAG, "Cooking tutorial will be opened");
            String google = "https://www.youtube.com/watch?v=X5oD_thIk3c";
            Uri webaddress = Uri.parse(google);

            Intent gotoGoogle = new Intent(Intent.ACTION_VIEW, webaddress); //No application context to get, so we'll go outside our app (using ACTION VIEW)
            if (gotoGoogle.resolveActivity(getPackageManager()) != null) {
                startActivity(gotoGoogle);
            }
        }
        if (tempAction.equals("2_single")){
            textView.setText("BUTTON2 GELEZEN");
            Log.d(TAG, "Youtube will be opened ");
            Intent i = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
            startActivity(i);
        }
        if (tempAction.equals("3_single")){
            textView.setText("BUTTON3 GELEZEN");
            callPhoneNumber();
            Log.d(TAG, "Contact will be called");
        }
        if (tempAction.equals("4_single")){
            textView.setText("BUTTON4 GELEZEN");
            Log.d(TAG, "Camera will be opened");
            Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(openCamera);
        }
        excecute = false;
    }

    public void openActivity2(){
        Intent open = new Intent(this, MainActivity2.class);
        startActivity(open);

    }
//    private void jsonParse(){
//        String url =  "https://vanginkelschoonmaak.nl/handyclick/data.php?buttonset=1";
//
//
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        int phoneTime = (int) (System.currentTimeMillis()/1000);
//                        Log.d(TAG,"TIMESTAMP" + String.valueOf(phoneTime));
//                        try {
//                            String action = response.getString("action");
//                            int timestamp = response.getInt("timestamp");
//                            if (timestamp > (phoneTime - 2)) {
//
//                                if (action.equals("1_single")) {
//                                        textView.setText("BUTTON1 GELEZEN");
//                                        Log.d(TAG, "Website will be opened");
//                                        String google = website_name;
//                                        Uri webaddress = Uri.parse(google);
//
//                                        Intent gotoGoogle = new Intent(Intent.ACTION_VIEW, webaddress); //No application context to get, so we'll go outside our app (using ACTION VIEW)
//                                        if (gotoGoogle.resolveActivity(getPackageManager()) != null) {
//                                            startActivity(gotoGoogle);
//                                        }
//
//                                }
//                                if (action.equals("2_double")) {
//                                    textView.setText("BUTTON2 GELEZEN");
//                                    Log.d(TAG, "Van Mossel will be opened");
//                                    String google = "https://www.vanmossel.nl";
//                                    Uri webaddress = Uri.parse(google);
//
//                                    Intent gotoGoogle = new Intent(Intent.ACTION_VIEW, webaddress); //No application context to get, so we'll go outside our app (using ACTION VIEW)
//                                    if (gotoGoogle.resolveActivity(getPackageManager()) != null) {
//                                        startActivity(gotoGoogle);
//                                    }
//                                }
//                                if (action.equals("4_double")) {
//                                    textView.setText("BUTTON4 GELEZEN");
//                                    Log.d(TAG, "Cooking tutorial will be opened");
//                                    String google = "https://www.youtube.com/watch?v=X5oD_thIk3c";
//                                    Uri webaddress = Uri.parse(google);
//
//                                    Intent gotoGoogle = new Intent(Intent.ACTION_VIEW, webaddress); //No application context to get, so we'll go outside our app (using ACTION VIEW)
//                                    if (gotoGoogle.resolveActivity(getPackageManager()) != null) {
//                                        startActivity(gotoGoogle);
//                                    }
//                                }
//                                if (action.equals("2_single")){
//                                    textView.setText("BUTTON2 GELEZEN");
//                                    Log.d(TAG, "Youtube will be opened ");
//                                    Intent i = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
//                                    startActivity(i);
//                                }
//                                if (action.equals("3_single")){
//                                        textView.setText("BUTTON3 GELEZEN");
//                                        callPhoneNumber();
//                                        Log.d(TAG, "Contact will be called");
////                                    Log.d(TAG, "Number will be called");
////                                    String phone = "0640225927";
////                                    String s = "tel:" + phone;
////                                    Intent callNr = new Intent(Intent.ACTION_CALL);
////                                    callNr.setData(Uri.parse(s));
////                                    startActivity(callNr);
//                                }
//                                if (action.equals("4_single")){
//                                    textView.setText("BUTTON4 GELEZEN");
//                                    Log.d(TAG, "Camera will be opened");
//                                    Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                    startActivity(openCamera);
//                                }
//
//                           }
//                            //mTextViewResult.setText(action +"\n" + button_id + "\n" + battery );
//                            //mPressedTime.setText("Pressed Time: "+ timestamp);
//                            //mPhoneTime.setText(String.valueOf(unixTimestamp));
//
//                        }catch (JSONException e){
//                            e.printStackTrace();
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//
//            }
//        });
//        mQueue.add(jsonObjectRequest);
//    }

    public void callPhoneNumber()
    {
        try
        {
            if(Build.VERSION.SDK_INT > 22)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 101);

                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone_nr)); //ALS BELLEN NIET MEER WERKT, MOET JE PHONE_NR VERANDEREN IN EEN STRING MET HET NUMMER ERIN!!!
                startActivity(callIntent);

            }
            else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone_nr)); //ALS BELLEN NIET MEER WERKT, MOET JE PHONE_NR VERANDEREN IN EEN STRING MET HET NUMMER ERIN!!!
                startActivity(callIntent);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    public void openDialog(){
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    /*public void openDialogWeb(){
        ExampleDialogWeb exampleDialogWeb = new ExampleDialogWeb();
        exampleDialogWeb.show(getSupportFragmentManager(), "example dialog web");
    }*/

    @Override
    public void applyTexts(String phonenumber, String webURL) {
        phone_nr = phonenumber;
        website_name = webURL;
        Log.d(TAG, "Your phonenumber is " + phone_nr);
        Log.d(TAG, "Your web address is " + website_name);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(requestCode == 101)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                callPhoneNumber();
            }
            else 
            {
                Log.e(TAG, "Permission not Granted");
            }
        }
    }
}