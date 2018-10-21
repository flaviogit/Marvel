package com.marvel.app.marvel;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import marvelapi.MarvelResponse;
import model.Comic;
import model.Comics;
import model.MarvelImage;
import retrofit2.Response;
import worker.BroadcastDetailObserver;
import worker.BroadcastReceiverAPI;
import worker.ServiceAPI;

import static com.nostra13.universalimageloader.core.ImageLoaderConfiguration.createDefault;

public class DetailCharacter extends AppCompatActivity  implements Observer {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static String id = "";
    private String description = "";
    private String thumbnail = "";
    private ImageView image_ch = null;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    BroadcastReceiverAPI receiver = new BroadcastReceiverAPI();
    private Observable observable;
    private Object data;
    private static ArrayList<Comic> list_item = new ArrayList<Comic>();
    private static ComicFragment.ListAdapterComic adapter2;

    private static RelativeLayout rl_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_character);


/*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        */
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        ImageLoader.getInstance().init(createDefault(this));

        image_ch = (ImageView)findViewById(R.id.detail_image);

        rl_progress = (RelativeLayout) findViewById(R.id.loadingPanel);
        rl_progress.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("id");
            description = extras.getString("description");
            thumbnail = extras.getString("thumbnail");
        }
        if(!thumbnail.isEmpty())
            RefreshDetailImage(thumbnail);

        if(description.isEmpty())
            description = "Nessuna descrizione per questo personaggio...";

        registerReceiver(receiver, new IntentFilter(ServiceAPI.CHARACTERDETAIL));
        Intent intent = new Intent(this, ServiceAPI.class);
        intent.setAction(ServiceAPI.CHARACTERDETAIL);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        intent.putExtra("id", id);
        startService(intent);

        BroadcastDetailObserver.getInstance().deleteObservers();
        BroadcastDetailObserver.getInstance().addObserver(this);

        list_item.clear();
        //adapter2 = new ComicFragment.ListAdapterComic(getApplicationContext(), list_item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(receiver);
        Intent intent = new Intent(this, ServiceAPI.class);
        stopService(intent);
    }

    @Override
    public void update(Observable observable, Object data) {
        this.observable = observable;
        this.data = data;

        rl_progress.setVisibility(View.GONE);

        ArrayList<Comic> list_item_page = (ArrayList<Comic>)((Intent)data).getSerializableExtra(ServiceAPI.CHARACTERDETAIL);
        //list_item = (ArrayList<Comic>)((Intent)data).getSerializableExtra(ServiceAPI.CHARACTERDETAIL);

        /*
        for(Comic item : list_item_page)
            list_item.add(item);
        */
        //mViewPager.getAdapter().notifyDataSetChanged();
        //adapter2.notifyDataSetChanged();
        //list_item.addAll((ArrayList<Comic>)((Intent)data).getSerializableExtra(ServiceAPI.CHARACTERDETAIL));
        adapter2.updateReceiptsList(list_item_page);
    }

    private void RefreshDetailImage(String path)
    {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                //.showImageOnLoading(R.drawable.placeholder) // resource or drawable
                .showImageForEmptyUri(R.drawable.placeholder) // resource or drawable
                .showImageOnFail(R.drawable.placeholder) // resource or drawable
                .resetViewBeforeLoading(false)  // default
                .delayBeforeLoading(0)
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                    /*.preProcessor(...)
		.postProcessor(...)
		.extraForDownloader(...)*/
                .considerExifParams(false) // default
                //.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                //.decodingOptions(...)
                //.displayer(new SimpleBitmapDisplayer()) // default
                //.handler(new Handler()) // default
                .build();

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(path, image_ch, options);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_character, menu);
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

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail_character, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public static class DescriptionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_DESCRIPTION = "Nessuna descrizione...";

        public DescriptionFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static DescriptionFragment newInstance(String description) {
            DescriptionFragment fragment = new DescriptionFragment();
            Bundle args = new Bundle();
            args.putString(ARG_DESCRIPTION, description);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail_character, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getArguments().getString(ARG_DESCRIPTION));

            return rootView;
        }
    }

    public static class ComicFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static ComicFragment fragment = null;
        Response<MarvelResponse<Comics>> heroList;
        ListView lstComics;
        ListAdapterComic adapter;
        private Observable observable;
        private Object data;
        private int pageCount = 0;

        private static final String ARG_LIST = "comics";

        public ComicFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ComicFragment newInstance(ArrayList<Comic> list_item) {
            fragment = new ComicFragment();
            Bundle args = new Bundle();
            args.putSerializable(ARG_LIST, (Serializable) list_item);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail_comics, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getArguments().getString(ARG_DESCRIPTION));
            //ArrayList<Comic> list_item = (ArrayList<Comic>) getArguments().getSerializable(ARG_LIST);
            lstComics = (ListView) rootView.findViewById(R.id.list_view);
            lstComics.setOnScrollListener(scrollList);
            //new MavenConnection().execute();

            //if(list_item != null) {
            //if(fragment != null && fragment.getActivity() != null) {
            //adapter2 = new ListAdapterComic(fragment.getActivity().getApplicationContext(), list_item);
            adapter2 = new ListAdapterComic(this.getActivity().getApplicationContext(), list_item);
            lstComics.setAdapter(adapter2);
            int a = 0;
            a=2;
            //}
            //}
            return rootView;
        }

        private AbsListView.OnScrollListener scrollList = new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int threshold = 20;
                int count = lstComics.getCount();

                if (scrollState == SCROLL_STATE_IDLE) {
                    boolean refresh = false;
                    if(lstComics.getFirstVisiblePosition() == 0 && pageCount > 1)
                    {
                        //refresh = true;
                        //pageCount--;
                    }
                    if (lstComics.getLastVisiblePosition() == ((threshold * (pageCount+1))-1))
                    {
                        refresh = true;
                        //pageCount++;
                    }
                    if (refresh) {//>= count - threshold) {// && pageCount < 2) {
                        rl_progress.setVisibility(View.VISIBLE);

                        Intent intent = new Intent(view.getRootView().getContext(), ServiceAPI.class);
                        intent.setAction(ServiceAPI.CHARACTERDETAIL);
                        Bundle bundle = new Bundle();
                        intent.putExtras(bundle);
                        intent.putExtra("id", id);
                        intent.putExtra("limit", "" + threshold);
                        if(pageCount == 0)
                            intent.putExtra("offset", "" + threshold);
                        else
                            intent.putExtra("offset", "" + (threshold * (pageCount+1)));

                        //intent.putExtra("nameStartsWith", txtSearch.getQuery().toString());
                        pageCount++;

                        view.getRootView().getContext().startService(intent);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {

            }

        };

        class ListAdapterComic extends ArrayAdapter<Comic>
        {
            ArrayList<Comic> _listtables_ad;
            ListAdapterComic(Context context, ArrayList<Comic> listtables_ad)
            {
                super(context, R.layout.comic_item, listtables_ad);
                _listtables_ad = listtables_ad;
            }

            public void updateReceiptsList(ArrayList<Comic> newlist) {
                //_listtables_ad.clear();
                _listtables_ad.addAll(newlist);
                this.notifyDataSetChanged();
            }

            public View getView(int position, View convertView, ViewGroup parent)
            {
                View row=convertView;

                if (row==null)
                {
                    LayoutInflater inflater=getLayoutInflater();
                    row=inflater.inflate(R.layout.comic_item, parent, false);
                    row.setTag(R.id.listview_item_title, row.findViewById(R.id.listview_item_title));
                    row.setTag(R.id.listview_image, row.findViewById(R.id.listview_image));
                    row.setTag(R.id.txtPrice, row.findViewById(R.id.txtPrice));

                }

                TextView titolo=(TextView)row.getTag(R.id.listview_item_title);
                TextView price=(TextView)row.getTag(R.id.txtPrice);
                //TextView colortable=(TextView)row.getTag(R.id.colortable);
                ImageView image_ch=(ImageView)row.getTag(R.id.listview_image);


                titolo.setText(_listtables_ad.get(position).getTitle().toString());
                String PriceVal = "$ 0";
                if(_listtables_ad.get(position).getPrices().size() > 0)
                    PriceVal = "$ " + _listtables_ad.get(position).getPrices().get(0).getPrice();

                price.setText(PriceVal);
                String path = _listtables_ad.get(position).getThumbnail().getImageUrl(MarvelImage.Size.LANDSCAPE_SMALL);

                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        //.showImageOnLoading(R.drawable.placeholder) // resource or drawable
                        .showImageForEmptyUri(R.drawable.placeholder) // resource or drawable
                        .showImageOnFail(R.drawable.placeholder) // resource or drawable
                        .resetViewBeforeLoading(false)  // default
                        .delayBeforeLoading(0)
                        .cacheInMemory(true) // default
                        .cacheOnDisk(true) // default
                    /*.preProcessor(...)
		.postProcessor(...)
		.extraForDownloader(...)*/
                        .considerExifParams(false) // default
                        //.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                        .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                        //.decodingOptions(...)
                        //.displayer(new SimpleBitmapDisplayer()) // default
                        //.handler(new Handler()) // default
                        .build();
/*
                ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(fragment.getActivity().getApplicationContext());
                config.threadPriority(Thread.NORM_PRIORITY - 2);
                config.denyCacheImageMultipleSizesInMemory();
                config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
                config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
                config.tasksProcessingOrder(QueueProcessingType.LIFO);
                config.writeDebugLogs();
*/
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(path, image_ch, options);

                return(row);

            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position==0)
                return DescriptionFragment.newInstance(description);
            else
                return ComicFragment.newInstance(list_item);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }
}
