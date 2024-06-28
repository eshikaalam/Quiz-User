package com.example.quizuser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.SearchView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.quizuser.CateAdapter;
import com.example.quizuser.Models.Category;
import com.example.quizuser.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RestActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RequestQueue requestQueue;
    private SwipeRefreshLayout refresh;
    private ArrayList<Category> category = new ArrayList<>();
    private ArrayList<Category> filteredCategory = new ArrayList<>();
    private JsonArrayRequest arrayRequest;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private CateAdapter cateAdapter;
    private String url = "https://restcountries.com/v3.1/all";
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);

        refresh = findViewById(R.id.swipedown);
        recyclerView = findViewById(R.id.category);
        searchView = findViewById(R.id.searchView);

        dialog = new Dialog(this);

        refresh.setOnRefreshListener(this);
        refresh.post(new Runnable() {
            @Override
            public void run() {
                category.clear();
                getData();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void getData() {
        refresh.setRefreshing(true);
        arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);

                        // Extract country name from the JSON object
                        JSONObject nameObject = jsonObject.getJSONObject("name");
                        String countryName = nameObject.getString("common");

                        Category cat = new Category();
                        cat.setCategory(countryName);
                        category.add(cat);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                adapterPush(category);
                refresh.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue = Volley.newRequestQueue(RestActivity.this);
        requestQueue.add(arrayRequest);
    }

    private void adapterPush(ArrayList<Category> category) {
        cateAdapter = new CateAdapter(this, category);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cateAdapter);
    }

    private void filter(String text) {
        filteredCategory.clear();
        for (Category cat : category) {
            if (cat.getCategory().toLowerCase().contains(text.toLowerCase())) {
                filteredCategory.add(cat);
            }
        }
        cateAdapter.filterList(filteredCategory);
    }

    @Override
    public void onRefresh() {
        // Implement refresh functionality if needed
    }
}
