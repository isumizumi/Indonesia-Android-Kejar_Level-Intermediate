package com.iszumi.movielover.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iszumi.movielover.util.CustomToast;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import com.iszumi.movielover.R;
import com.iszumi.movielover.db.FavoriteMovie;
import com.iszumi.movielover.network.UrlComposer;
import com.iszumi.movielover.network.model.Movie;
import com.iszumi.movielover.network.model.Review;
import com.iszumi.movielover.network.model.ReviewResponse;
import com.iszumi.movielover.network.model.Video;
import com.iszumi.movielover.network.model.VideoResponse;
import com.iszumi.movielover.network.retrofit.MovieService;
import com.iszumi.movielover.network.retrofit.ServiceGenerator;
import com.iszumi.movielover.view.Generator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Thanks to my sensei: @hendrawd
 */

public class DetailActivity extends AppCompatActivity {

//    private static final String TAG = "DetailActivity";
    public static final String MOVIE_KEY = "movie";

    private Call<VideoResponse> mRequestVideoCall;
    private Call<ReviewResponse> mRequestReviewCall;
    private MenuItem mMenuItemFavorite;
    private boolean mIsFavoriteMovie;
    private boolean mFavoriteMovieClicked;
    private String mFirstVideoKey;

    @BindView(R.id.tv_original_title)
    TextView tvOriginalTitle;
    @BindView(R.id.iv_poster_image)
    ImageView ivPosterImage;
    @BindView(R.id.tv_description)
    TextView tvDescription;
    @BindView(R.id.tv_rating)
    TextView tvRating;
    @BindView(R.id.tv_release_date)
    TextView tvReleaseDate;
    @BindView(R.id.ll_video_container)
    LinearLayout llVideoContainer;
    @BindView(R.id.ll_review_container)
    LinearLayout llReviewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        // workaround for shared element transition stuck
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }

        setupBackButton();

        // mendapatkan data yang dipassing dari MainActivity
        Movie data = getMovieData();

        // set text ke original title
        tvOriginalTitle.setText(data.getOriginalTitle());

        // set text ke deskripsi
        tvDescription.setText(data.getOverview());

        // set text ke rata-rata vote
        double voteAverage = data.getVoteAverage();
        double maxVote = 10;
        tvRating.setText(voteAverage + "/" + maxVote);

        // set text ke tanggal rilis
        tvReleaseDate.setText(data.getReleaseDate());

        // set image poster
        Picasso.with(this)
                .load(
                        UrlComposer.getPosterUrl(
                                data.getPosterPath()
                        )
                )
                .into(ivPosterImage);

        requestVideoList(data.getId());
        requestReviewList(data.getId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // cancelling calls if activity already destroyed, to avoid activity leak
        mRequestVideoCall.cancel();
        mRequestReviewCall.cancel();
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("halo", "Success Unfavorite!");
        if (mFavoriteMovieClicked) {
            setResult(RESULT_OK, resultIntent);
        }
        super.onBackPressed();
    }

    /**
     * Membuat back button
     */
    private void setupBackButton() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_detail, menu);
        mMenuItemFavorite = menu.findItem(R.id.action_favorite);
        setFavoriteMenuItemFirstState();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int actionId = item.getItemId();

        // Handle back button click
        switch (actionId) {
            case android.R.id.home: {
                // kembali ke parent Activity
                // NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
            }
            case R.id.action_share: {
                if (mRequestVideoCall != null) {
                    ShareCompat.IntentBuilder shareIntentBuilder = ShareCompat.IntentBuilder.from(DetailActivity.this);
                    shareIntentBuilder
                            .setType("text/plain")
                            .setText(
                                    getString(
                                            R.string.share_first_trailer,
                                            "https://www.youtube.com/watch?v=" + mFirstVideoKey
                                    )
                            );
                    startActivity(shareIntentBuilder.getIntent());
                } else {
                    CustomToast.show(this, getString(R.string.no_trailer));
                }
                return true;
            }
            case R.id.action_favorite: {
                toggleFavorite();
                mFavoriteMovieClicked = true;
                return true;
            }
        }

        return false;
    }

    private Movie getMovieData() {
        return getIntent().getParcelableExtra(MOVIE_KEY);
    }

    private void toggleFavorite() {
        final Movie movie = getMovieData();
        // best practice for using realm
        // https://medium.com/@Zhuinden/how-to-use-realm-for-android-like-a-champ-and-how-to-tell-if-youre-doing-it-wrong-ac4f66b7f149
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(
                    new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mIsFavoriteMovie = realm.where(FavoriteMovie.class)
                                    .equalTo("id", movie.getId())
                                    .count() > 0;
                            if (mIsFavoriteMovie) {
                                realm.where(FavoriteMovie.class)
                                        .equalTo("id", movie.getId())
                                        .findAll()
                                        .deleteAllFromRealm();
                            } else {
                                realm.insert(FavoriteMovie.fromMovie(movie));
                            }
                            mIsFavoriteMovie = !mIsFavoriteMovie;
                            toggleFavoriteMenuIcon();
                        }
                    }
            );
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private void toggleFavoriteMenuIcon() {
        mMenuItemFavorite.setIcon(
                ResourcesCompat.getDrawable(
                        getResources(),
                        mIsFavoriteMovie ? R.drawable.ic_favorite : R.drawable.ic_favorite_border,
                        null
                )
        );
    }

    private void setFavoriteMenuItemFirstState() {
        final Movie movie = getMovieData();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(
                    new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mIsFavoriteMovie = realm.where(FavoriteMovie.class)
                                    .equalTo("id", movie.getId())
                                    .count() > 0;
                            toggleFavoriteMenuIcon();
                        }
                    }
            );
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    /**
     * Mendapatkan video list dari network menggunakan retrofit
     */
    private void requestVideoList(Integer movieId) {
        mRequestVideoCall = ServiceGenerator
                .createService(MovieService.class)
                .getVideos(movieId);

        mRequestVideoCall.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(@NonNull Call<VideoResponse> call, @NonNull Response<VideoResponse> response) {
                VideoResponse videoResponse = response.body();
                if (videoResponse != null) {
                    List<Video> videoList = videoResponse.getResults();
                    for (int i = 0; i < videoList.size(); i++) {
                        Video video = videoList.get(i);
                        if(i == 0){
                            //assign first video key to member variable
                            mFirstVideoKey = video.getKey();
                        }
                        View videoRow = Generator.getVideo(
                                DetailActivity.this,
                                video.getName(),
                                video.getType(),
                                video.getKey()
                        );
                        llReviewContainer.addView(videoRow);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<VideoResponse> call, @NonNull Throwable t) {
                // TODO handle request failure
            }
        });
    }

    /**
     * Mendapatkan review list dari network menggunakan retrofit
     */
    private void requestReviewList(Integer movieId) {
        mRequestReviewCall = ServiceGenerator
                .createService(MovieService.class)
                .getReviews(movieId);

        mRequestReviewCall.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReviewResponse> call, @NonNull Response<ReviewResponse> response) {
                ReviewResponse reviewResponse = response.body();
                if (reviewResponse != null) {
                    List<Review> reviewList = reviewResponse.getResults();
                    for (Review review : reviewList) {
                        View reviewRow = Generator.getReview(
                                DetailActivity.this,
                                review.getAuthor(),
                                review.getContent()
                        );
                        llReviewContainer.addView(reviewRow);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReviewResponse> call, @NonNull Throwable t) {
                // TODO handle request failure
            }
        });
    }
}