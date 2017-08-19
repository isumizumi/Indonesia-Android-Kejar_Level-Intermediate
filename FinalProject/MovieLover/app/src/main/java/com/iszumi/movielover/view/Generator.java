package com.iszumi.movielover.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.iszumi.movielover.R;
import com.iszumi.movielover.activity.DetailActivity;
import com.iszumi.movielover.network.UrlComposer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import com.iszumi.movielover.network.model.Video;
import com.iszumi.movielover.network.model.VideoResponse;

import static android.R.attr.name;

/**
 * Thanks to my sensei: @hendrawd
 */

public class Generator {

    public static View getReview(Context context, String author, String description) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_review, null);
        TextView tvAuthor = (TextView) view.findViewById(R.id.tv_author);
        tvAuthor.setText(author);
        TextView tvDescription = (TextView) view.findViewById(R.id.tv_description);
        tvDescription.setText(description);
        return view;
    }

    public static View getVideo(Context context, String name, String type) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.row_video, null);
        TextView tvName = (TextView) row.findViewById(R.id.tv_name);
        tvName.setText(name);
        TextView tvType = (TextView) row.findViewById(R.id.tv_type);
        tvType.setText(type);
        return row;
    }
}
