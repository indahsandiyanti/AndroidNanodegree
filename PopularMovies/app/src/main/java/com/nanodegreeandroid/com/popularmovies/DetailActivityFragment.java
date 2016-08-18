package com.nanodegreeandroid.com.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Movie.EXTRA_DETAIL)) {
            Movie movie = intent.getParcelableExtra(Movie.EXTRA_DETAIL);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView_movie);
            String url = Uri.parse(PopularMoviesAdapter.BASE_IMAGE_URI).buildUpon()
                    .appendEncodedPath(movie.getImagePath()).toString();
            imageView.setContentDescription(movie.getTitle());
            Picasso.with(getContext()).load(url).into(imageView);

            TextView titleTextView = (TextView)rootView.findViewById(R.id.textView_Title);
            titleTextView.setText(movie.getTitle());

            TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.textView_releaseDate);
            releaseDateTextView.setText(movie.getReleaseDate());

            TextView synopsisTextView = (TextView) rootView.findViewById(R.id.textView_synopsis);
            synopsisTextView.setText(movie.getOverview());

            TextView averageVote = (TextView) rootView.findViewById(R.id.textView_voteAverage);
            averageVote.setText(Double.toString(movie.getVoteAverage()));
        }
        return rootView;
    }
}
