package com.amazon.filmhero.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.amazon.filmhero.fragment.MoviesFragment;
import com.amazon.filmhero.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_main, new MoviesFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_now_playing:
                if (item.isChecked()) {
                    break;
                }
                updateSortCriteria(MoviesFragment.NOW_PLAYING);
                item.setChecked(true);
                break;
            case R.id.sort_popular:
                if (item.isChecked()) {
                    break;
                }
                updateSortCriteria(MoviesFragment.POPULAR);
                item.setChecked(true);
                break;
            case R.id.sort_top_rated:
                if (item.isChecked()) {
                    break;
                }
                updateSortCriteria(MoviesFragment.TOP_RATED);
                item.setChecked(true);
                break;
            case R.id.sort_upcoming:
                if (item.isChecked()) {
                    break;
                }
                updateSortCriteria(MoviesFragment.UPCOMING);
                item.setChecked(true);
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(R.anim.fade_in_zoom, R.anim.fade_out);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateSortCriteria(String sortCriteria) {
        final MoviesFragment fragment = (MoviesFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main);
        fragment.setSortCriteria(sortCriteria);
        fragment.setPageNumber(1);
        fragment.getMovies();
    }
}
