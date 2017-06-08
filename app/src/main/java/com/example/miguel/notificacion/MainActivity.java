package com.example.miguel.notificacion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ShareDialog shareDialog;
    private Button logout;
    RequestQueue queue;
    String urlpost = "http://cce36dbb.ngrok.io/fcm/v1/devices/";
    String id,nameR;
    String token = FirebaseInstanceId.getInstance().getToken();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        queue= Volley.newRequestQueue(getApplicationContext());
        shareDialog = new ShareDialog(this);

        Bundle inBundle = getIntent().getExtras();
        String name = inBundle.get("name").toString();
        nameR=name;
        String surname = inBundle.get("surname").toString();
        String imageUrl = inBundle.get("imageUrl").toString();

        TextView nameView = (TextView)findViewById(R.id.nameAndSurname);
        nameView.setText("" + name + " " + surname);
        Button logout = (Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                Intent login = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
            }
        });
        try {
            Button token = (Button)findViewById(R.id.token);
            token.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnRegistrar_click(view);

                }
            });
        }catch (Exception x){
        }


        new MainActivity.DownloadImage((ImageView)findViewById(R.id.profileImage)).execute(imageUrl);
    }
    public void btnRegistrar_click(View v){
        final String token = FirebaseInstanceId.getInstance().getToken();

        // Log and toast
        final String msg = token;
        Log.d("TAG", "token: "+msg);
        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();


        StringRequest request=new StringRequest(Request.Method.POST, urlpost, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getBaseContext(),response, Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("POSTERROR", "Error en el POST");
            }
        }){
            protected Map<String,String> getParams() {
                Random r= new Random();
                Map<String, String> map = new HashMap<String, String>();
                map.put("dev_id",Integer.toString(r.nextInt()));
                map.put("reg_id",msg);
                map.put("name", nameR);
                map.put("is_active", "False");

                return map;
            }
        };


        queue.add(request);
    }
    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
