package com.example.alcchallenge2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getUid();
    private CollectionReference Ref = db.collection("Posts");

    private FirestoreRecyclerAdapter<BlogPost, MovieHolder> adapter;
    private DocumentSnapshot documentSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FloatingActionButton post = findViewById(R.id.add_post_btn);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postIntent = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(postIntent);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        Query query = Ref.orderBy("time", Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<BlogPost> options = new FirestoreRecyclerOptions.Builder<BlogPost>()
                .setQuery(query, BlogPost.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<BlogPost, MovieHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MovieHolder movieHolder, int i, @NonNull BlogPost blogPost) {
                movieHolder.movieTitle(blogPost.getTitle());
                movieHolder.moviePrice(blogPost.getPrice());
                movieHolder.setMovieDesc(blogPost.getDesc());
                movieHolder.movieImage(blogPost.getImage());

            }

            @NonNull
            @Override
            public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list, parent, false);
                return new MovieHolder(view);
            }

        };
        recyclerView.setAdapter(adapter);
    }

    class MovieHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView movieTitle;
        private TextView movieDesc;
        private TextView moviePrice;
        private ImageView movieImage;

        public MovieHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void movieTitle(String title) {
            movieTitle = mView.findViewById(R.id.title);
            movieTitle.setText(title);
        }

        public void setMovieDesc(String desc) {
            movieDesc = mView.findViewById(R.id.desc);
            movieDesc.setText(desc);
        }

        public void movieImage(String image) {
            movieImage = mView.findViewById(R.id.imageView);
            Uri Photo = Uri.parse(image);
            Glide.with(MainActivity.this).load(Photo).into(movieImage);
        }

        public void moviePrice(String price) {
            moviePrice = mView.findViewById(R.id.price);
            moviePrice.setText(price);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {

            adapter.startListening();

        } else {

            sendToLogin();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout:
                logOut();
                return true;

            case R.id.settings:

                Intent settingsIntent = new Intent(MainActivity.this, SetupActivity.class);
                startActivity(settingsIntent);

                return true;

            default:
                return false;


        }
    }

    //Sign User Out
    private void logOut(){


        mAuth.signOut();
        sendToLogin();
    }

    //When User is not signed-in.
    private void sendToLogin() {

        Intent loginIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(loginIntent);
        finish();

    }

}
