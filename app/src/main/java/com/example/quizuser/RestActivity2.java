package com.example.quizuser;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RestActivity2 extends AppCompatActivity {
    EditText t1,t2;
    TextView tv1;
    Button b1;

    public  static final String url = "http://192.168.35.224/api/setdata.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest2);

        t1 = (EditText) findViewById(R.id.t1);
        t2 = (EditText) findViewById(R.id.t2);
        tv1 = (TextView) findViewById(R.id.tv1);
        b1 = (Button) findViewById(R.id.b1);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processdata(t1.getText().toString(), t2.getText().toString());
            }
        });

    }
    public void processdata(final String name, final String email){
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                t1.setText("");
                t2.setText("");
                tv1.setText(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                t1.setText("");
                t2.setText("");
                tv1.setText(error.toString());
                tv1.setTextColor(Color.RED);
            }
        }
        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> map = new HashMap<String, String>();
                map.put("name",name);
                map.put("email",email);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);

    }
}