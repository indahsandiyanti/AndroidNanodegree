package com.nanodegreeandroid.com.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String MOVIE_STATE_KEY = "movies";
    private boolean shouldFetchData = true;

    private PopularMoviesAdapter popularMoviesAdapter;
    private ArrayList<Movie> movies;
    private String[] sortingCriteriaArr;
    private String selectedCriteria;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sortingCriteriaArr = getResources().getStringArray(R.array.movie_sort_value);

        if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_STATE_KEY)) {
            movies = new ArrayList<>();
        } else {
            movies = savedInstanceState.getParcelableArrayList(MOVIE_STATE_KEY);
            shouldFetchData = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_STATE_KEY, movies);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (shouldFetchData) {
            updateMovies();
        }
    }

    private void updateMovies() {
        Log.i("MainActivityFragment", "Update movies");
        FetchPopularMovies fetchPopularMovies = new FetchPopularMovies();
        fetchPopularMovies.execute(selectedCriteria);
    }

    private Movie[] getMoviesFromJson(String movieJsonStr) throws JSONException, ParseException {

        final String ARR_RESULT = "results";
        final String ATTR_ID = "id";
        final String ATTR_TITLE = "original_title";
        final String ATTR_OVERVIEW = "overview";
        final String ATTR_RELEASE_DATE = "release_date";
        final String ATTR_POSTER_PATH = "poster_path";
        final String ATTR_VOTE_AVERAGE = "vote_average";
        JSONObject jsonObject = new JSONObject(movieJsonStr);
        JSONArray jsonArray = jsonObject.getJSONArray(ARR_RESULT);
        int len = jsonArray.length();
        Movie[] movies = new Movie[len];
        for (int i = 0; i<len; i++) {
            JSONObject movie = jsonArray.getJSONObject(i);
            int id = movie.getInt(ATTR_ID);
            String overview = movie.getString(ATTR_OVERVIEW);
            String title = movie.getString(ATTR_TITLE);
            String releaseDate = movie.getString(ATTR_RELEASE_DATE);
            String url = movie.getString(ATTR_POSTER_PATH);
            double averageVote = movie.getDouble(ATTR_VOTE_AVERAGE);
            Movie m = new Movie(id, title, overview, releaseDate, url, averageVote);
            movies[i] = m;
        }
        return movies;
    }


    private int getSortingCriteriaIdx() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        selectedCriteria = sharedPreferences.getString(
                getString(R.string.pref_sortCriteria_key),
                getString(R.string.pref_sortCriteria_default));
        int selectionIdx = Arrays.binarySearch(sortingCriteriaArr, selectedCriteria);
        return selectionIdx;
    }

    private void updateSortingCriteriaPreference(int selectedIdx) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        selectedCriteria = sortingCriteriaArr[selectedIdx];
        editor.putString(getString(R.string.pref_sortCriteria_key), selectedCriteria);
        editor.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("MainActivityFragment", "OnCreateView is called");

        popularMoviesAdapter = new PopularMoviesAdapter(getActivity(),
                movies);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView)rootView.findViewById(R.id.gridView_movies);
        gridView.setAdapter(popularMoviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent detailMovieIntent = new Intent(getActivity(),
                        DetailActivity.class);
                detailMovieIntent.putExtra(Movie.EXTRA_DETAIL, popularMoviesAdapter.getItem(i));
                startActivity(detailMovieIntent);
            }
        });


        Spinner spinner = (Spinner)rootView.findViewById(R.id.spinner_movie_sort);
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.movie_sort_label, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sortAdapter);
        spinner.setSelection(getSortingCriteriaIdx());
        Log.i("MainActivityFragment", "Set Adapter");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
//            @Override
//            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
//                sharedPreferences
//            }
//        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("MainActivityFragment", "Spinner On Item Selected is chosen");
                updateSortingCriteriaPreference(i);
                updateMovies();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return rootView;
    }



    public class FetchPopularMovies extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchPopularMovies.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;

            final String BASE_URL = "https://api.themoviedb.org/3/";
            final String APP_ID_PARAM = "api_key";
            final String ACTION = "movie";

            String requestUri = Uri.parse(BASE_URL).buildUpon()
                    .appendEncodedPath(ACTION)
                    .appendEncodedPath(strings[0])
                    .appendQueryParameter(APP_ID_PARAM,
                            BuildConfig.TMDB_API_KEY).build().toString();
            try {
                URL url = new URL(requestUri);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                Log.d(LOG_TAG, "Requesting from " + requestUri);
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line+"\n");
                }

                if (stringBuffer.length() > 0) {
                    movieJsonStr = stringBuffer.toString();
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }
                }
            }

            if (movieJsonStr == null) {
                return new Movie[0];
            }
            try {
                Movie[] movieArr = getMoviesFromJson(movieJsonStr);
                return movieArr;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            } catch (ParseException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return new Movie[0];

        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            popularMoviesAdapter.clear();
            popularMoviesAdapter.addAll(movies);
        }
    }
}
