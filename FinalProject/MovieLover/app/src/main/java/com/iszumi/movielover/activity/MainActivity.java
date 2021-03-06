package com.iszumi.movielover.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import com.iszumi.movielover.R;
import com.iszumi.movielover.adapter.MovieAdapter;
import com.iszumi.movielover.db.FavoriteMovie;
import com.iszumi.movielover.network.model.Movie;
import com.iszumi.movielover.network.model.MovieResponse;
import com.iszumi.movielover.network.retrofit.MovieService;
import com.iszumi.movielover.network.retrofit.ServiceGenerator;
import com.iszumi.movielover.util.CustomToast;
import com.iszumi.movielover.util.GridSpacingItemDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Thanks to my sensei: @hendrawd
 */

public class MainActivity extends AppCompatActivity implements Callback<MovieResponse>, MovieAdapter.ItemClickListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_UPDATE_RECYCLER_VIEW = 42;
    private RecyclerView mRecyclerView;
    private Call<MovieResponse> mCall;
    private boolean isFavoriteCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        initRecyclerView();
        requestMovieList(getString(R.string.category_api_popular));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCall != null) {
            //cancel retrofit call kalau activity sudah didestroy
            mCall.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_UPDATE_RECYCLER_VIEW) {
            loadFavoriteMoviesFromDb();
            CustomToast.show(this, data.getStringExtra("halo"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // mendapatkan reference dari MenuInflater
        final MenuInflater inflater = getMenuInflater();

        // inflate menu yang kita buat(menu_main)
        // ke dalam menu di activity ini
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_popular:
                Toast.makeText(getApplicationContext(), "Popular Movies", Toast.LENGTH_SHORT).show();
                requestMovieList(getString(R.string.category_api_popular));
                return true;
            case R.id.action_top_rated:
                Toast.makeText(getApplicationContext(), "Top Rated Movies", Toast.LENGTH_SHORT).show();
                requestMovieList(getString(R.string.category_api_top_rated));
                return true;
            case R.id.action_favorite:
                Toast.makeText(getApplicationContext(), "Favorite Movies", Toast.LENGTH_SHORT).show();
                loadFavoriteMoviesFromDb();
                return true;
            case R.id.action_about:
                Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_SHORT).show();
                Intent k = new Intent(MainActivity.this, About.class); //explicit intent
                k.putExtra("Value3", "About");
                startActivity(k);
                return true;
        }

        return false;
    }

    /**
     * Memuat list favorite movie dari database ke adapter
     */
    private void loadFavoriteMoviesFromDb() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(
                    new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            List<Movie> movieList = new ArrayList<>();
                            RealmResults<FavoriteMovie> favoriteMovies = realm
                                    .where(FavoriteMovie.class)
                                    .findAll();
                            for (FavoriteMovie favoriteMovie : favoriteMovies) {
                                movieList.add(FavoriteMovie.toMovie(favoriteMovie));
                            }
                            showMovieList(movieList);
                        }
                    }
            );
            isFavoriteCategory = true;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private void initRecyclerView() {
        Integer spanCount = getResources().getInteger(R.integer.span_count);
        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, spanCount);
        mRecyclerView.setLayoutManager(layoutManager);
        int gridSpacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, gridSpacing, true));
    }

    /**
     * Mendapatkan movie list dari network menggunakan retrofit
     */
    private void requestMovieList(String category) {
        mCall = ServiceGenerator
                .createService(MovieService.class)
                .getMovies(category);

        // Kenapa implement interface daripada memakai anonymous class?
        // Berasal dari buku Effective Java, Item 5: Avoid creating unnecessary objects
        mCall.enqueue(MainActivity.this);
    }

    private void showMovieList(List<Movie> movieList) {
        MovieAdapter movieAdapter = new MovieAdapter(MainActivity.this, movieList);
        mRecyclerView.setAdapter(movieAdapter);
    }

    /**
     * Callback yang dipanggil saat mendapatkan response dari server
     *
     * @param call     Call
     * @param response Response
     */
    @Override
    public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
        MovieResponse movieResponse = response.body();
        if (movieResponse != null) {
            List<Movie> movieList = movieResponse.getResults();
            showMovieList(movieList);
            isFavoriteCategory = false;
        }
    }

    /**
     * Callback yang dipanggil saat terjadi error waktu mendapatkan response dari server
     *
     * @param call Call
     * @param t    Throwable yang berisi error
     */
    @Override
    public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
        //TODO implement action if there is a failure
        Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
    }

    /**
     * Callback yang akan dipanggil ketika item di grid diklik
     *
     * @param position int posisi dari item yang diklik
     */
    @Override
    public void onItemClick(int position) {
        // Menampilkan Toast yang menunjukkan posisi yang diklik
        // CustomToast.show(MainActivity.this, "Item with position " + position + " clicked");

        // Mendapatkan MovieAdapter
        MovieAdapter adapter = (MovieAdapter) mRecyclerView.getAdapter();

        // Mendapatkan data dari MovieAdapter
        List<Movie> movieList = adapter.getData();

        // Membuat Intent untuk membuka DetailActivity
        Intent openDetailActivityIntent = new Intent(MainActivity.this, DetailActivity.class);

        // Mendapatkan data dengan posisi yang diklik
        Movie movieData = movieList.get(position);

        // Set data ke Intent
        openDetailActivityIntent.putExtra(DetailActivity.MOVIE_KEY, movieData);

        // Start DetailActivity
        if (isFavoriteCategory) {
            startActivityForResult(openDetailActivityIntent, REQUEST_CODE_UPDATE_RECYCLER_VIEW);
        } else {
            startActivity(openDetailActivityIntent);
        }
    }
}
