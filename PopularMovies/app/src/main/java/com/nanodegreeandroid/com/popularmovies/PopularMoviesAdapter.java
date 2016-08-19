package com.nanodegreeandroid.com.popularmovies;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by indah on 16/8/16.
 */
public class PopularMoviesAdapter extends ArrayAdapter<Movie> {

    public static final String BASE_IMAGE_URI = "https://image.tmdb.org/t/p/w185/";

    public PopularMoviesAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView)convertView.findViewById(R.id.imageView_movie);
            convertView.setTag(viewHolder);
        }
        ViewHolder vw = ((ViewHolder)convertView.getTag());
        ImageView imageView = vw.imageView;
        String url = Uri.parse(BASE_IMAGE_URI).buildUpon().appendEncodedPath(movie.getImagePath()).toString();
        imageView.setContentDescription(movie.getTitle());
        Picasso.with(getContext()).load(url).into(imageView);
        return convertView;
    }



    static class ViewHolder {
        ImageView imageView;
    }
}
