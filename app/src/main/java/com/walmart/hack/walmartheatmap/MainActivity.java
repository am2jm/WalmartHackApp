package com.walmart.hack.walmartheatmap;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {


    private String MY_URL = "http://discovermagazine.com/2012/extreme-earth/01-big-one-earthquake-could-devastate-pacific-northwest";
    private String WEBSOCKET_HOST = "ws://10.165.150.129:8081";

    private WebView webview;
    private ScrollView scrollview;
    private LinearLayout linearView;
    private RecyclerView recyclerView;
    private ArrayList<Notification> dataset;
    DatabaseHelper mDbHelper;
    SQLiteDatabase db;
    WebSocketClient mWebSocketClient;

    private TextView numbers;
    private EditText input;
    private Button add, remove, push;
    private Dialog dialog;
    private ArrayList<Integer> aisles;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    scrollview.setVisibility(View.GONE);
                    linearView.setVisibility(View.GONE);
                    setWeb();
                    return true;

                case R.id.navigation_dashboard:
                    webview.setVisibility(View.GONE);
                    linearView.setVisibility(View.GONE);
                    getNotificationPanel();
                    return true;

                case R.id.navigation_notifications:
                    webview.setVisibility(View.GONE);
                    scrollview.setVisibility(View.GONE);
                    setRecycler();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webview = (WebView) findViewById(R.id.webview);
        scrollview = (ScrollView) findViewById(R.id.scrollView);
        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        linearView = (LinearLayout) findViewById(R.id.linear);



        add = (Button) findViewById(R.id.add);
        remove = (Button) findViewById(R.id.remove);
        push = (Button) findViewById(R.id.push);
        numbers = (TextView) findViewById(R.id.numbers);
        aisles = new ArrayList<>();
        aisles.add(5);
        aisles.add(30);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setWeb();
    }

    private void setWeb(){
        webview.setVisibility(View.VISIBLE);

        webview.getSettings().setJavaScriptEnabled(true);
        final Activity activity = this;
        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });
        webview.loadUrl(MY_URL);
    }

    private void getNotificationPanel(){
        scrollview.setVisibility(View.VISIBLE);

        if (aisles.size() > 0) {
            String display = "" + aisles.get(0);
            for (int i = 1; i < aisles.size(); i++) {
                display = display + ", " + aisles.get(i);
            }
            numbers.setText(display);
        }
        //else nothing to doooooo

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog(true);
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog(false);
            }
        });

    }

    private void updateDisplay(int newNum){
        if(!aisles.contains(newNum)) {
            String curr = numbers.getText().toString();
            curr += ", " + newNum;
            numbers.setText(curr);
            aisles.add(newNum);
        }
    }

    private void removeNumber(int num){
        if(aisles.contains(num)){
            aisles.remove(aisles.indexOf(num));
            if (aisles.size() > 0) {
                String display = "" + aisles.get(0);
                for (int i = 1; i < aisles.size(); i++) {
                    display = display + ", " + aisles.get(i);
                }
                numbers.setText(display);
            }
            dialog.dismiss();
        }
        else{
            Toast.makeText(MainActivity.this, "You do not subscribe to this aisle", Toast.LENGTH_SHORT).show();
//            TextView input = dialog.findViewById(R.id.editNumber);
            input.setText("");
//            dialo;
        }
    }

    private void dialog(final boolean amAdding){
        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        dialog.setContentView(R.layout.selection);
        dialog.show();
        input = dialog.findViewById(R.id.editNumber);
        input.requestFocus();

        TextView add_remove = (TextView) dialog .findViewById(R.id.add_remove);

        if(amAdding)
            add_remove.setText("Add Aisle");
        else
            add_remove.setText("Remove Aisle");


        Button bt_yes = (Button)dialog.findViewById(R.id.buttonYes);
        Button bt_no = (Button)dialog.findViewById(R.id.buttonNo);

        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input.getText() != null && input.getText().toString().length() != 0 && input.getText().toString().length() < 4){
                    if(amAdding){
                        updateDisplay(Integer.parseInt(input.getText().toString()));
                        dialog.dismiss();
                    }
                    else{
                        removeNumber(Integer.parseInt(input.getText().toString()));
//                        dialog.dismiss();
                    }
//                    Log.i("My Tag Is:", "HELLOOOOO" + input.getText().toString());


                }
                else{

                    Toast.makeText(MainActivity.this, "No Aisle Selected", Toast.LENGTH_SHORT).show();
                }

            }
        });
        bt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    public void setRecycler(){
        linearView.setVisibility(View.VISIBLE);
//        recyclerView.setVisibility(View.VISIBLE);
//        recycleText.setVisibility(View.VISIBLE);

        dataset = new ArrayList<>();

        mDbHelper = new DatabaseHelper(this);
        db = mDbHelper.getWritableDatabase();

        String[] projection = {
                "title",
                "body"
        };
        Cursor cursor = db.query(
                "notification",                           // The table to query
                projection,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                ""                                        // The sort order
        );

        String title = "";
        String body = "";
        while(cursor.moveToNext()) {
            title = cursor.getString(
                    cursor.getColumnIndexOrThrow("title")
            );
            body = cursor.getString(
                    cursor.getColumnIndexOrThrow("body")
            );
            Log.i("DBData", title + " " + body);
            dataset.add(new Notification(title, body));
        }
        cursor.close();

        mDbHelper.close();
        Collections.reverse(dataset);


        ListAdapter adapter = new ListAdapter(this, dataset);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }



    //***********
    // * Web Socket Stufff


    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI(WEBSOCKET_HOST);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
//                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        TextView textView = (TextView)findViewById(R.id.messages);
//                        textView.sendtText(textView.getText() + "\n" + message);
                        // adding the newest message to the list of messages
                        dataset.add(new Notification("Newest Notification:", message));
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

}
