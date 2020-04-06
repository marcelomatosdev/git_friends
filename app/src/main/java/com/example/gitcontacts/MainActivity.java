package com.example.gitcontacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ContactCardAdapter.OnItemClickListener {

    public static final String EXTRA_IMAGE_URL = "imageUrl";
    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_BIO = "bio";
    public static final String EXTRA_REPOSITORIES = "repositories";
    public static final String EXTRA_FOLLOWERS = "followers";
    public static final String EXTRA_ID = "id";

    private Toolbar toolbar;

    private RecyclerView mRecyclerView;
    private ContactCardAdapter mContactCardAdapter;
    private ArrayList<ContactItem> mContactList;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.MainMenu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Git Contacts");

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mContactList = new ArrayList<>();

        mRequestQueue = Volley.newRequestQueue(this);
        parseJSON();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menubar_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.addContact_menuItem:
                Intent intentRefresh = new Intent(MainActivity.this, AddContactActivity.class);
                startActivity(intentRefresh);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void parseJSON() {

        String url = "https://my-git-network.herokuapp.com/contacts";
        Log.d("Marcelo", "onResponse: ");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,url,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject contact = response.getJSONObject(i);

                                int id = contact.getInt("id");
                                String imageUrl = contact.getString("avatar_url");
                                String username = contact.getString("login");
                                String name = contact.getString("name");
                                String bio = contact.getString("bio");
                                //int repositories = contact.getInt("repositories");
                                //int followers = contact.getInt("followers");
                                int repositories = 10;
                                int followers = 20;
                                mContactList.add(new ContactItem(imageUrl, username,name,bio,repositories,followers, id));
                            }
                            mContactCardAdapter = new ContactCardAdapter(MainActivity.this, mContactList);
                            mRecyclerView.setAdapter(mContactCardAdapter);
                            mContactCardAdapter.setOnItemClickListener(MainActivity.this);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mRequestQueue.add(request);
    }

    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(this, DetailContactActivity.class);
        ContactItem clickedItem = mContactList.get(position);

        detailIntent.putExtra(EXTRA_IMAGE_URL, clickedItem.getImageUrl());
        detailIntent.putExtra(EXTRA_USERNAME, clickedItem.getUsername());
        detailIntent.putExtra(EXTRA_NAME, clickedItem.getName());
        detailIntent.putExtra(EXTRA_BIO, clickedItem.getBio());
        detailIntent.putExtra(EXTRA_REPOSITORIES, clickedItem.getRepositories());
        detailIntent.putExtra(EXTRA_FOLLOWERS, clickedItem.getFollowers());
        detailIntent.putExtra(EXTRA_ID,clickedItem.getId());
        startActivity(detailIntent);
    }
}