package com.firstapp.handyclickapp_v1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener, PopupMenu.OnMenuItemClickListener {

    int buttonset = 1; // ONLY CHANGE THIS

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
    private Button top_left;
    private Button bottom_left;
    private Button top_right;
    private Button bottom_right;
    String phone_nr = "Empty";
    String website_name = "Empty";
    String navigation_address = "";
    private Button clicked_button;
    String assigned_function_1 = "";
    String assigned_function_2 = "";
    String assigned_function_3 = "";
    String assigned_function_4 = "";
    String assigned_function = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        top_left = (Button) findViewById(R.id.Button1);

        bottom_left = (Button) findViewById(R.id.Button2);

        top_right = (Button) findViewById(R.id.Button3);
        bottom_right = (Button) findViewById(R.id.Button4);
        button = (Button) findViewById(R.id.buttonAct2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity2();
            }
        });
        /*top_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked_button = top_left;
                showPopup(v);
                assigned_function_1 = assigned_function;
            }
        });
        top_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked_button = top_right;
                showPopup(v);
                assigned_function_2 = assigned_function;
            }
        });
        bottom_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked_button = bottom_right;
                showPopup(v);
                assigned_function_3 = assigned_function;
            }
        });
        bottom_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked_button = bottom_left;
                showPopup(v);
                assigned_function_4 = assigned_function;

            }
        });*/

        openDialog();

        ImageView img = (ImageView) findViewById(R.id.imageView);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });


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
        String url = "https://vanginkelschoonmaak.nl/handyclick/data.php?buttonset=" + buttonset;
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
                    doAction(buttonset);
                }
            });
        }

        Thread.sleep(1000); // Niet nodig, alleen voor de website niet te hard te belasten.

        // Start again
        requestWebPage();
    }

    public void doAction(int buttonset) {
        if (buttonset != 3) {

            if (tempAction.equals("1_single")) {
                if (assigned_function_1.equals("")){
                    return;
                }
                if (assigned_function_1.equals("Website")) {
//                textView.setText("BUTTON1 GELEZEN");
                    Log.d(TAG, "Website will be opened");
                    String google = website_name;
                    Uri webaddress = Uri.parse(google);

                    Intent gotoGoogle = new Intent(Intent.ACTION_VIEW, webaddress); //No application context to get, so we'll go outside our app (using ACTION VIEW)
                    if (gotoGoogle.resolveActivity(getPackageManager()) != null) {
                        startActivity(gotoGoogle);
                    }
                }
                if (assigned_function_1.equals("Youtube")) {
                    //textView.setText("BUTTON2 GELEZEN");
                    Log.d(TAG, "Youtube will be opened ");
                    Intent i = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                    startActivity(i);
                }
                if (assigned_function_1.equals("Call")) {
                    //textView.setText("BUTTON3 GELEZEN");
                    callPhoneNumber();
                    Log.d(TAG, "Contact will be called");
                }
                if (assigned_function_1.equals("Camera")) {
                    //textView.setText("BUTTON4 GELEZEN");
                    Log.d(TAG, "Camera will be opened");
                    Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(openCamera);
                }
                if (assigned_function_1.equals("Cooking")) {
//                textView.setText("BUTTON4 GELEZEN");
                    Log.d(TAG, "Cooking tutorial will be opened");
                    String google = "https://www.youtube.com/watch?v=X5oD_thIk3c";
                    Uri webaddress = Uri.parse(google);

                    Intent gotoGoogle = new Intent(Intent.ACTION_VIEW, webaddress); //No application context to get, so we'll go outside our app (using ACTION VIEW)
                    if (gotoGoogle.resolveActivity(getPackageManager()) != null) {
                        startActivity(gotoGoogle);
                    }
                }
                if (assigned_function_1.equals("Navigation")) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + navigation_address);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                }
            }
            if (tempAction.equals("2_single")) {
                if (assigned_function_2.equals("")){
                    return;
                }
                if (assigned_function_2.equals("Website")) {
//                textView.setText("BUTTON1 GELEZEN");
                    Log.d(TAG, "Website will be opened");
                    String google = website_name;
                    Uri webaddress = Uri.parse(google);

                    Intent gotoGoogle = new Intent(Intent.ACTION_VIEW, webaddress); //No application context to get, so we'll go outside our app (using ACTION VIEW)
                    if (gotoGoogle.resolveActivity(getPackageManager()) != null) {
                        startActivity(gotoGoogle);
                    }
                }
                if (assigned_function_2.equals("Youtube")) {
                    //textView.setText("BUTTON2 GELEZEN");
                    Log.d(TAG, "Youtube will be opened ");
                    Intent i = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                    startActivity(i);
                }
                if (assigned_function_2.equals("Call")) {
                    //textView.setText("BUTTON3 GELEZEN");
                    callPhoneNumber();
                    Log.d(TAG, "Contact will be called");
                }
                if (assigned_function_2.equals("Camera")) {
                    //textView.setText("BUTTON4 GELEZEN");
                    Log.d(TAG, "Camera will be opened");
                    Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(openCamera);
                }
                if (assigned_function_2.equals("Cooking")) {
//                textView.setText("BUTTON4 GELEZEN");
                    Log.d(TAG, "Cooking tutorial will be opened");
                    String google = "https://www.youtube.com/watch?v=X5oD_thIk3c";
                    Uri webaddress = Uri.parse(google);

                    Intent gotoGoogle = new Intent(Intent.ACTION_VIEW, webaddress); //No application context to get, so we'll go outside our app (using ACTION VIEW)
                    if (gotoGoogle.resolveActivity(getPackageManager()) != null) {
                        startActivity(gotoGoogle);
                    }
                }
                if (assigned_function_2.equals("Navigation")) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + navigation_address);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                }
            }
            if (tempAction.equals("3_single")) {
                if (assigned_function_3.equals("")){
                    return;
                }
                if (assigned_function_3.equals("Website")) {
//                textView.setText("BUTTON1 GELEZEN");
                    Log.d(TAG, "Website will be opened");
                    String google = website_name;
                    Uri webaddress = Uri.parse(google);

                    Intent gotoGoogle = new Intent(Intent.ACTION_VIEW, webaddress); //No application context to get, so we'll go outside our app (using ACTION VIEW)
                    if (gotoGoogle.resolveActivity(getPackageManager()) != null) {
                        startActivity(gotoGoogle);
                    }
                }
                if (assigned_function_3.equals("Youtube")) {
                    //textView.setText("BUTTON2 GELEZEN");
                    Log.d(TAG, "Youtube will be opened ");
                    Intent i = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                    startActivity(i);
                }
                if (assigned_function_3.equals("Call")) {
                    //textView.setText("BUTTON3 GELEZEN");
                    callPhoneNumber();
                    Log.d(TAG, "Contact will be called");
                }
                if (assigned_function_3.equals("Camera")) {
                    //textView.setText("BUTTON4 GELEZEN");
                    Log.d(TAG, "Camera will be opened");
                    Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(openCamera);
                }
                if (assigned_function_3.equals("Cooking")) {
//                textView.setText("BUTTON4 GELEZEN");
                    Log.d(TAG, "Cooking tutorial will be opened");
                    String google = "https://www.youtube.com/watch?v=X5oD_thIk3c";
                    Uri webaddress = Uri.parse(google);

                    Intent gotoGoogle = new Intent(Intent.ACTION_VIEW, webaddress); //No application context to get, so we'll go outside our app (using ACTION VIEW)
                    if (gotoGoogle.resolveActivity(getPackageManager()) != null) {
                        startActivity(gotoGoogle);
                    }
                }
                if (assigned_function_3.equals("Navigation")) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + navigation_address);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                }
            }
            if (tempAction.equals("4_single")) {
                if (assigned_function_4.equals("")){
                    return;
                }
                if (assigned_function_4.equals("Website")) {
//                textView.setText("BUTTON1 GELEZEN");
                    Log.d(TAG, "Website will be opened");
                    String google = website_name;
                    Uri webaddress = Uri.parse(google);

                    Intent gotoGoogle = new Intent(Intent.ACTION_VIEW, webaddress); //No application context to get, so we'll go outside our app (using ACTION VIEW)
                    if (gotoGoogle.resolveActivity(getPackageManager()) != null) {
                        startActivity(gotoGoogle);
                    }
                }
                if (assigned_function_4.equals("Youtube")) {
                    //textView.setText("BUTTON2 GELEZEN");
                    Log.d(TAG, "Youtube will be opened ");
                    Intent i = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                    startActivity(i);
                }
                if (assigned_function_4.equals("Call")) {
                    //textView.setText("BUTTON3 GELEZEN");
                    callPhoneNumber();
                    Log.d(TAG, "Contact will be called");
                }
                if (assigned_function_4.equals("Camera")) {
                    //textView.setText("BUTTON4 GELEZEN");
                    Log.d(TAG, "Camera will be opened");
                    Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(openCamera);
                }
                if (assigned_function_4.equals("Cooking")) {
//                textView.setText("BUTTON4 GELEZEN");
                    Log.d(TAG, "Cooking tutorial will be opened");
                    String google = "https://www.youtube.com/watch?v=X5oD_thIk3c";
                    Uri webaddress = Uri.parse(google);

                    Intent gotoGoogle = new Intent(Intent.ACTION_VIEW, webaddress); //No application context to get, so we'll go outside our app (using ACTION VIEW)
                    if (gotoGoogle.resolveActivity(getPackageManager()) != null) {
                        startActivity(gotoGoogle);
                    }
                }
                if (assigned_function_4.equals("Navigation")) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + navigation_address);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                }
            }
            /*if (tempAction.equals("2_single")){
//                textView.setText("BUTTON2 GELEZEN");
                Log.d(TAG, "Youtube will be opened ");
                Intent i = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                startActivity(i);
            }
            if (tempAction.equals("3_single")){
//                textView.setText("BUTTON3 GELEZEN");
                callPhoneNumber();
                Log.d(TAG, "Contact will be called");

            }
            if (tempAction.equals("4_single")){
//                textView.setText("BUTTON4 GELEZEN");
                Log.d(TAG, "Camera will be opened");
                Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(openCamera);
            }

            if (tempAction.equals("2_double")) {
//                textView.setText("BUTTON2 GELEZEN");
                Log.d(TAG, "Van Mossel will be opened");
                String google = "https://www.vanmossel.nl";
                Uri webaddress = Uri.parse(google);

                Intent gotoGoogle = new Intent(Intent.ACTION_VIEW, webaddress); //No application context to get, so we'll go outside our app (using ACTION VIEW)
                if (gotoGoogle.resolveActivity(getPackageManager()) != null) {
                    startActivity(gotoGoogle);
                }
            }
            if (tempAction.equals("4_double")) {
//                textView.setText("BUTTON4 GELEZEN");
                Log.d(TAG, "Cooking tutorial will be opened");
                String google = "https://www.youtube.com/watch?v=X5oD_thIk3c";
                Uri webaddress = Uri.parse(google);

                Intent gotoGoogle = new Intent(Intent.ACTION_VIEW, webaddress); //No application context to get, so we'll go outside our app (using ACTION VIEW)
                if (gotoGoogle.resolveActivity(getPackageManager()) != null) {
                    startActivity(gotoGoogle);
                }
            }
            if (tempAction.equals("1_double")) {
//              Log.d(TAG, "Cooking tutorial will be opened");
                //Uri IntentUri = Uri.parse("google.navigation:0,0?q=" + navigation_address + "&mode=d");
                //Intent intent = new Intent(Intent.ACTION_VIEW, IntentUri);
                //Uri.parse("google.navigation:q=51.6878954,5.0574822&mode=1");
                //intent.setPackage("com.google.android.apps.maps");

                //if(intent.resolveActivity(getPackageManager()) != null){
                //    startActivity(intent);
                //}

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + navigation_address);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

            }*/
        } else {
            if (tempAction.equals("1_on")) {
//                textView.setText("BUTTON1 GELEZEN");
                Log.d(TAG, "Website will be opened");
                String google = website_name;
                Uri webaddress = Uri.parse(google);

                Intent gotoGoogle = new Intent(Intent.ACTION_VIEW, webaddress); //No application context to get, so we'll go outside our app (using ACTION VIEW)
                if (gotoGoogle.resolveActivity(getPackageManager()) != null) {
                    startActivity(gotoGoogle);
                }
            }
            if (tempAction.equals("2_on")){
//                textView.setText("BUTTON2 GELEZEN");
                Log.d(TAG, "Youtube will be opened ");
                Intent i = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                startActivity(i);
            }
            if (tempAction.equals("3_on")){
//                textView.setText("BUTTON3 GELEZEN");
                callPhoneNumber();
                Log.d(TAG, "Contact will be called");
            }
            if (tempAction.equals("4_on")){
//                textView.setText("BUTTON4 GELEZEN");
                Log.d(TAG, "Camera will be opened");
                Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(openCamera);
            }
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
    public void applyTexts(String phonenumber, String webURL, String location) {
        phone_nr = phonenumber;
        website_name = webURL;
        navigation_address = location;
        Log.d(TAG, "Your phonenumber is " + phone_nr);
        Log.d(TAG, "Your web address is " + website_name);
        Log.d(TAG, "Your destination is " + navigation_address);

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


    public void showPopup(View v){
        if (v.equals(top_left)){
            clicked_button = top_left;
            PopupMenu popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.popup_menu);
            popup.show();
            assigned_function_1 = assigned_function;
        }
        if (v.equals(top_right)){
            clicked_button = top_right;
            PopupMenu popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.popup_menu);
            popup.show();
            assigned_function_2 = assigned_function;
        }
        if (v.equals(bottom_left)){
            clicked_button = bottom_left;
            PopupMenu popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.popup_menu);
            popup.show();
            assigned_function_3 = assigned_function;
        }
        if (v.equals(bottom_right)){
            clicked_button = bottom_right;
            PopupMenu popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.popup_menu);
            popup.show();
            assigned_function_4 = assigned_function;
        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item0:
            clicked_button.setText("No Function");
            assigned_function = "";
            return true;

            case R.id.item1:
            clicked_button.setText("Call Contact");
            assigned_function = "Call";
            return true;

            case R.id.item2:
            clicked_button.setText("Open Youtube");
            assigned_function = "Youtube";
            return true;

            case R.id.item3:
            clicked_button.setText("Open Camera");
            assigned_function = "Camera";
            return true;

            case R.id.item4:
            clicked_button.setText("Open Website");
            assigned_function = "Website";
            return true;

            case R.id.item5:
                clicked_button.setText("Navigation");
                assigned_function = "Navigation";
                return true;

            case R.id.item6:
                clicked_button.setText("Cooking");
                assigned_function = "Cooking";
                return true;
            default:
                return false;

        }
    }
}