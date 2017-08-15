package com.iszumi.movielover.network.retrofit;

import com.iszumi.movielover.network.model.MovieResponse;
import com.iszumi.movielover.network.model.ReviewResponse;
import com.iszumi.movielover.network.model.VideoResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Thanks to my sensei: @hendrawd
 */

public interface MovieService {

    @GET("movie/{category}")
    Call<MovieResponse> getMovies(@Path("category") String category);

    @GET("movie/{movie_id}/reviews?language=en-US&page=1")
    Call<ReviewResponse> getReviews(@Path("movie_id") Integer movieId);

    @GET("movie/{movie_id}/videos")
    Call<VideoResponse> getVideos(@Path("movie_id") Integer movieId);
}
