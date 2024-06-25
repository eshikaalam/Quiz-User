package com.example.quizuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quizuser.Adapters.CategoryAdapter;
import com.example.quizuser.Models.CategoryModel;
import com.example.quizuser.databinding.ActivityTestBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import com.google.firebase.auth.FirebaseAuth;


public class TestActivity extends AppCompatActivity {


    ActivityTestBinding binding;
    FirebaseDatabase database;
    ArrayList<CategoryModel> list;
    CategoryAdapter adapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();

        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        list = new ArrayList<>();

        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        binding.recyCategory.setLayoutManager(layoutManager);

        adapter = new CategoryAdapter(this,list);
        binding.recyCategory.setAdapter(adapter);

        database.getReference().child("Registered users").child(currentUserUid)
                .child("categories").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear(); // Clear the list before adding new data
                        if (snapshot.exists()) {

                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                list.add(new CategoryModel(
                                        dataSnapshot.child("categoryName").getValue().toString(),
                                        dataSnapshot.child("categoryImage").getValue().toString(),
                                        dataSnapshot.getKey(),
                                        Integer.parseInt(dataSnapshot.child("setNum").getValue().toString())
                                ));
                            }
                            updateUI(list);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(TestActivity.this, "Category not exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void updateUI(ArrayList<CategoryModel> list) {
                        adapter = new CategoryAdapter(TestActivity.this, list);
                        binding.recyCategory.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(TestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }
}