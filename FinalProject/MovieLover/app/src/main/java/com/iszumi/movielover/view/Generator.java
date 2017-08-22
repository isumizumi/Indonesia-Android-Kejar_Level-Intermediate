package com.iszumi.movielover.view;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iszumi.movielover.R;
import com.iszumi.movielover.network.UrlComposer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.iszumi.movielover.util.Util;

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

    public static View getVideo(final Activity context, String name, String type, final String videoKey) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.row_video, null);
        ImageView ivThumbnail = (ImageView) row.findViewById(R.id.iv_thumbnail);

        //load image glide
        if (TextUtils.isEmpty(videoKey)) {
            ivThumbnail.setImageResource(R.drawable.error_landscape);
        } else {
            RequestOptions options = new RequestOptions().error(R.drawable.error_landscape);
            Glide.with(ivThumbnail.getContext())
                    .load(UrlComposer.getYoutubeThumbnail(videoKey))
                    .apply(options)
                    .into(ivThumbnail);
        }

        //set click action
        row.findViewById(R.id.main_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.playYoutubeVideo(context, videoKey);
            }
        });

        TextView tvName = (TextView) row.findViewById(R.id.tv_name);
        tvName.setText(name);
        TextView tvType = (TextView) row.findViewById(R.id.tv_type);
        tvType.setText(type);
        return row;
    }
}
