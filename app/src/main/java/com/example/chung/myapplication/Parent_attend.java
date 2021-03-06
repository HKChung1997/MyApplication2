package com.example.chung.myapplication;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Parent_attend extends AppCompatActivity {

    private String TAG = Parent_attend.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;
    //final TextView username = (TextView)findViewById(R.id.test);
    //final TextView password = (TextView)findViewById(R.id.test2);
    // URL to get contacts JSON
    ArrayList<HashMap<String, String>> contactList;
    //String username = getIntent().getExtras().getString("test").toString();
   // String password = getIntent().getExtras().getString("test2").toString();
    String url = "https://lenchan139.org/myWorks/fyp/android/attendDetails.php?username=parentX&password=pw";
    //final String str = intent.getStringExtra("test");
    //final String str2 = intent.getStringExtra("test2");
    //username.setText(str);
    //password.setText(str2);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_attend);
        contactList = new ArrayList<>();
        //Intent intent = getIntent();
        //final String str = intent.getStringExtra("test");
        //final String str2 = intent.getStringExtra("test2");
        //username.setText(str);
        //password.setText(str2);
        lv = (ListView) findViewById(R.id.attenList);
        new GetContacts().execute();
    }
    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Parent_attend.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("studArray");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String studName = c.getString("student_name");
                        String studClass = c.getString("student_class");
                        // Phone node is JSON Object
                        JSONArray date = c.getJSONArray("student_attend");
                        for (int j = 0; j < date.length(); j++) {
                            JSONObject jsonObject = date.getJSONObject(j);

                            String dates = jsonObject.getString("attend_date");
                            // tmp hash map for single contact
                            HashMap<String, String> contact = new HashMap<>();

                            // adding each child node to HashMap key => value
                            contact.put("studName", studName);
                            contact.put("studClass", studClass);
                            contact.put("attenDate", dates);

                            // adding contact to contact list
                            contactList.add(contact);
                        }
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    Parent_attend.this, contactList,
                    R.layout.attenlist_item, new String[]{"studName","studClass",
                    "attenDate"}, new int[]{R.id.studName,
                    R.id.studClass, R.id.attenDate});

            lv.setAdapter(adapter);
        }

    }
}