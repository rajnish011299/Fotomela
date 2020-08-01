package com.rajumia.fotomela;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Search extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    ProgressDialog progress;

    EditText userInput;
    String userInputString = "";
    ImageButton searchButton;

    Query mQuery;
    FirestorePagingAdapter adapter;
    FirestorePagingOptions options;
    PagedList.Config config;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.searchPage);
        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(RecyclerViewLayoutManager);

        userInput = findViewById(R.id.searchBox);
        searchButton = findViewById(R.id.searchButton);

        db = FirebaseFirestore.getInstance();

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        // disable dismiss by tapping outside of the dialog
        progress.setCancelable(false);

        // Init Paging Configuration
        config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(15)
                .setPageSize(5)
                .build();

        // Scope for showing recent searches and stuff like that here
        mQuery = db.collection("search_profile").whereGreaterThan("username","{");
        options = new FirestorePagingOptions.Builder<SearchViewModel>()
                .setLifecycleOwner(Search.this)
                .setQuery(mQuery, config, SearchViewModel.class)
                .build();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentInput = userInput.getText().toString().trim();
                if(!currentInput.equals(userInputString) && currentInput.length()>0)
                {
                    userInputString = currentInput;
                    mQuery = getDataFromFirebase(userInputString);
                    options = new FirestorePagingOptions.Builder<SearchViewModel>()
                            .setLifecycleOwner(Search.this)
                            .setQuery(mQuery, config, SearchViewModel.class)
                            .build();
                    adapter.updateOptions(options);
                }
            }
        });

        // Instantiate Paging Adapter for RecyclerView
        adapter = new FirestorePagingAdapter<SearchViewModel, PostViewHolder>(options) {
            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.adapter, parent, false);
                return new PostViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder viewHolder, int i, @NonNull SearchViewModel post) {
                viewHolder.bind(post);
            }

            @Override
            protected void onError(@NonNull Exception e) {
                super.onError(e);
                Log.e("SearchActivity", e.getMessage());
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {

                    case LOADING_INITIAL:
                        break;
                    case LOADING_MORE:
                        progress.show();
                        break;

                    case LOADED:
                    case FINISHED:
                        progress.dismiss();
                        break;

                    case ERROR:
                        Toast.makeText(getApplicationContext(), "Error Occurred!", Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                        break;
                }
            }
        };
        recyclerView.setAdapter(adapter);
    }
    private Query getDataFromFirebase(String input)
    {
        String start = input + 'a';
        String end = input + 'z';
        CollectionReference userList = db.collection("search_profile");
        Query Q = userList.whereGreaterThanOrEqualTo("username",start)
                          .whereLessThanOrEqualTo("username",end);
        return Q;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        private TextView useName;
        private TextView fullName;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            useName = itemView.findViewById(R.id.username);
            fullName = itemView.findViewById(R.id.fullName);
        }

        public void bind(SearchViewModel post) {
            useName.setText(post.getUsername());
            fullName.setText(post.getCity());
        }
    }
}