package com.example.harjot.musicstreamer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements StreamMusicFragment.OnTrackSelectedListener
        , LocalMusicFragment.OnLocalTrackSelectedListener
        , PlayerFragment.reloadCurrentInstanceListener {

    static Toolbar toolbar;
    static Toolbar bottomPlayer;
    static TabLayout tabLayout;
    static ViewPager viewPager;

    static ImageView selected_track_image;
    static TextView selected_track_title;
    static ImageView player_controller;

    View playerContainer;

    static View ab;

    int playeState = 0; // 0 -> completely Hidden  1-> Completely Visible

    boolean isPlayerVisible = false;

    static boolean localSelected = false;
    static boolean streamSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ab = findViewById(R.id.appBar);

        playerContainer = findViewById(R.id.playerFragContainer);

        requestPermissions();

        selected_track_image = (ImageView) findViewById(R.id.selected_track_image_bp);
        selected_track_title = (TextView) findViewById(R.id.selected_track_title_bp);
        player_controller = (ImageView) findViewById(R.id.player_control_bp);

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        bottomPlayer = (Toolbar) findViewById(R.id.bottomPlayer);
        bottomPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.Fragment frag = getFragmentManager().findFragmentByTag("player");
                android.app.FragmentManager fm = getFragmentManager();
                if (!isPlayerVisible) {
                    isPlayerVisible = true;
                    hideTabs();
                    showPlayer();
                    /*fm.beginTransaction()
                            .setCustomAnimations(R.animator.slide_up,
                                    R.animator.slide_down,
                                    R.animator.slide_up,
                                    R.animator.slide_down)
                            .show(frag)
                            .commit();*/
                } else {
                    isPlayerVisible = false;
                    showTabs();
                    hidePlayer();
                    /*fm.beginTransaction()
                            .setCustomAnimations(R.animator.slide_up,
                                    R.animator.slide_down,
                                    R.animator.slide_up,
                                    R.animator.slide_down)
                            .hide(frag)
                            .commit();*/
                }
            }
        });

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StreamMusicFragment(), "Stream");
        adapter.addFragment(new LocalMusicFragment(), "Local");
        viewPager.setAdapter(adapter);
    }

    public void onTrackSelected(int position) {

        bottomPlayer.setVisibility(View.VISIBLE);
        hideTabs();
        isPlayerVisible = true;

        android.app.Fragment frag = getFragmentManager().findFragmentByTag("player");
        android.app.FragmentManager fm = getFragmentManager();
        PlayerFragment newFragment = new PlayerFragment();
        if (frag == null) {
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.playerFragContainer, newFragment, "player")
                    .show(newFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            if (PlayerFragment.track != null && StreamMusicFragment.selectedTrack.getTitle() == PlayerFragment.track.getTitle()) {
                /*fm.beginTransaction()
                        .setCustomAnimations(R.animator.slide_up,
                                R.animator.slide_down,
                                R.animator.slide_up,
                                R.animator.slide_down)
                        .show(frag)
                        .commit();*/
            } else {
                PlayerFragment.mMediaPlayer.stop();
                PlayerFragment.mMediaPlayer.reset();
                PlayerFragment.mVisualizer.release();
                PlayerFragment.init();
                fm.beginTransaction()
                        .remove(frag)
                        .setCustomAnimations(R.animator.slide_up,
                                R.animator.slide_down,
                                R.animator.slide_up,
                                R.animator.slide_down)
                        .add(R.id.playerFragContainer, newFragment, "player")
                        .show(newFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }

        showPlayer();

    }

    @Override
    public void onLocalTrackSelected(int position) {

        bottomPlayer.setVisibility(View.VISIBLE);
        hideTabs();
        isPlayerVisible = true;

        android.app.Fragment frag = getFragmentManager().findFragmentByTag("player");
        android.app.FragmentManager fm = getFragmentManager();
        PlayerFragment newFragment = new PlayerFragment();
        if (frag == null) {
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.playerFragContainer, newFragment, "player")
                    .show(newFragment)
                    .commit();
        } else {
            if (PlayerFragment.localTrack != null && LocalMusicFragment.selectedTrack.getTitle() == PlayerFragment.localTrack.getTitle()) {
                /*fm.beginTransaction()
                        .setCustomAnimations(R.animator.slide_up,
                                R.animator.slide_down,
                                R.animator.slide_up,
                                R.animator.slide_down)
                        .show(frag)
                        .commit();*/
            } else {
                PlayerFragment.mMediaPlayer.stop();
                PlayerFragment.mMediaPlayer.reset();
                PlayerFragment.mVisualizer.release();
                PlayerFragment.init();
                fm.beginTransaction()
                        .remove(frag)
                        .setCustomAnimations(R.animator.slide_up,
                                R.animator.slide_down,
                                R.animator.slide_up,
                                R.animator.slide_down)
                        .add(R.id.playerFragContainer, newFragment, "player")
                        .show(newFragment)
                        .commit();
            }
        }

        showPlayer();
    }

    @Override
    public void reloadCurrentInstance() {
        Fragment frag = getSupportFragmentManager().findFragmentByTag("player");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.playerFragContainer, frag, "player")
                .show(frag)
                .commit();
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
//        android.app.Fragment frag = getFragmentManager().findFragmentByTag("player");
//        if (frag == null || !frag.isVisible())
//            super.onBackPressed();
//        else {
//            isPlayerVisible = false;
//            showTabs();
//            hidePlayer();
//            /*getFragmentManager().beginTransaction()
//                    .setCustomAnimations(R.animator.slide_up,
//                            R.animator.slide_down,
//                            R.animator.slide_up,
//                            R.animator.slide_down)
//                    .hide(frag)
//                    .commit();*/
//        }

        if(isPlayerVisible){
            hidePlayer();
            showTabs();
            isPlayerVisible = false;
        }
        else{
            super.onBackPressed();
        }
    }

    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        1);
            }
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.MODIFY_AUDIO_SETTINGS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS},
                        2);
            }
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        3);
            }
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        4);
            }
        }
    }

    public void hideTabs() {
        ab.setVisibility(View.VISIBLE);
        ab.setAlpha(1.0f);

        ab.animate()
                .translationX(-1 * ab.getWidth())
                .alpha(0.0f);
    }

    public void showTabs() {
        ab.setVisibility(View.VISIBLE);
        ab.setAlpha(0.0f);

        ab.animate()
                .translationX(0)
                .alpha(1.0f);
    }

    public void hidePlayer(){
        playerContainer.setVisibility(View.VISIBLE);
        playerContainer.setAlpha(1.0f);

        playerContainer.animate()
                .translationX(playerContainer.getWidth())
                .alpha(0.0f);
    }

    public void showPlayer(){
        playerContainer.setVisibility(View.VISIBLE);
        playerContainer.setAlpha(0.0f);

        playerContainer.animate()
                .translationX(0)
                .alpha(1.0f);
    }

}
