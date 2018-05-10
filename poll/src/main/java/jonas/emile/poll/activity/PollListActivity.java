package jonas.emile.poll.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import jonas.emile.poll.PollService;
import jonas.emile.poll.R;
import jonas.emile.poll.model.Poll;
import org.json.JSONArray;

import java.util.Arrays;

import static jonas.emile.poll.PollService.IGNORE;

public class PollListActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private PollService pollService;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), new Poll[]{});

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show());
        fab.setVisibility(View.INVISIBLE);

        this.pollService = new PollService(this);

        pollService.getAll().accept((JSONArray array) -> {
            final int length = array.length();
            Log.i("PollActivity", String.format("Receive array with %d element", length));
            final Poll[] polls = new Gson().fromJson(array.toString(), Poll[].class);
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), polls);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            Arrays.stream(polls).forEach(o -> pollService.getAvailablesChoices(o.getUuid()).accept((a) ->
                    Log.i("#getAvailablesChoices", a.toString()), IGNORE));
        }, (VolleyError error) -> {
            Log.w("PollActivity", String.format("Something got wrong, code %d", error.networkResponse.statusCode));
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_poll_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_SECTION_POLL = "section_poll";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, Poll poll) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putSerializable(ARG_SECTION_POLL, poll);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_poll_list, container, false);
            Poll p = (Poll) getArguments().getSerializable(ARG_SECTION_POLL);
            TextView title = (TextView) rootView.findViewById(R.id.poll_title);
            TextView content = (TextView) rootView.findViewById(R.id.poll_content);
            title.setText(p.getProposition());
            content.setText(p.getDetails());
            new PollService(getContext()).getAvailablesChoices(p.getUuid()).accept((choices) -> {
                Log.i("PollListActivity", String.format("Got %d for %s", choices.length(), p.getProposition()));
            }, IGNORE);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final Poll[] polls;

        public SectionsPagerAdapter(FragmentManager fm, Poll[] polls) {
            super(fm);
            this.polls = polls;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, polls[position]);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return polls.length;
        }
    }
}
