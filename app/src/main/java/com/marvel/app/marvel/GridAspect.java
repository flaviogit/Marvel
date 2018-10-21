package com.marvel.app.marvel;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

import model.Character;
import model.MarvelImage;
import worker.BroadcastGridObserver;
import worker.BroadcastReceiverAPI;
import worker.ServiceAPI;

import static com.nostra13.universalimageloader.core.ImageLoaderConfiguration.createDefault;

public class GridAspect extends AppCompatActivity implements Observer {

    BroadcastReceiverAPI receiver = new BroadcastReceiverAPI();
    ArrayList<Character> list_item_local = new ArrayList<Character>();
    GridView lstCharacters;
    android.support.v7.widget.SearchView txtSearch;
    ListAdapterCharacter adapter;
    ImageButton btnRevert;
    private Observable observable;
    private Object data;
    RelativeLayout rl_progress;
    private int pageCount = 0;
    private boolean orderBy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_aspect);

        ImageLoader.getInstance().init(createDefault(this));

        lstCharacters = (GridView) findViewById(R.id.grid_view);
        txtSearch = (android.support.v7.widget.SearchView)findViewById(R.id.search);



        btnRevert = (ImageButton)findViewById(R.id.btnRevert);
        btnRevert.setOnClickListener(Revert);
        rl_progress = (RelativeLayout) findViewById(R.id.loadingPanel);
        rl_progress.setVisibility(View.VISIBLE);

        InitializeItems();
    }

    @Override
    protected void onResume() {
        super.onResume();

        txtSearch.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                rl_progress.setVisibility(View.VISIBLE);
                pageCount = 0;

                Intent intent = new Intent(getApplicationContext(), ServiceAPI.class);
                intent.setAction(ServiceAPI.CHARACTERGRID);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                int threshold = 20;
                intent.putExtra("limit", "" + threshold);
                intent.putExtra("offset", "" + 0);
                intent.putExtra("nameStartsWith", query);
                list_item_local.clear();
                adapter.notifyDataSetChanged();

                startService(intent);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty())
                    this.onQueryTextSubmit(newText.toString());

                return false;
            }


        });

        registerReceiver(receiver, new IntentFilter(ServiceAPI.CHARACTERGRID));
        BroadcastGridObserver.getInstance().deleteObservers();
        BroadcastGridObserver.getInstance().addObserver(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(BroadcastReceiverAPI);
        unregisterReceiver(receiver);
        Intent intent = new Intent(this, ServiceAPI.class);
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        list_item_local.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void update(Observable observable, Object data) {
        this.observable = observable;
        this.data = data;
        rl_progress.setVisibility(View.GONE);

        ArrayList<Character> list_item = (ArrayList<Character>)((Intent)data).getSerializableExtra(ServiceAPI.CHARACTERGRID);
        if(orderBy) {
            Collections.reverse(list_item_local);
            list_item_local.addAll(list_item);
            Collections.reverse(list_item_local);
        }
        else
            list_item_local.addAll(list_item);

        adapter.notifyDataSetChanged();

    }

    private void InitializeItems()
    {
        list_item_local.clear();


        Intent intent = new Intent(this, ServiceAPI.class);
        intent.setAction(ServiceAPI.CHARACTERGRID);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        startService(intent);

        adapter = new ListAdapterCharacter(getApplicationContext(), list_item_local);
        lstCharacters.setAdapter(adapter);
        lstCharacters.setOnItemClickListener(SelectItemList);
        lstCharacters.setOnScrollListener(scrollList);
    }

    private View.OnClickListener Revert = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            try
            {
                try {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            orderBy = !orderBy;
                            Collections.reverse(adapter.filteredData);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            catch(Exception e)
            {

            }
        }
    };

    private AdapterView.OnItemClickListener SelectItemList = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Character entry= (Character) parent.getAdapter().getItem(position);
            //Toast.makeText(getApplicationContext(), entry.getId(), Toast.LENGTH_SHORT).show();
            Intent intent = null; //new Intent(TablesActivity.this, OrderActivity.class);
            intent = new Intent(GridAspect.this, DetailCharacter.class);
            intent.putExtra("id", entry.getId());
            intent.putExtra("description", entry.getDescription());
            intent.putExtra("thumbnail", entry.getThumbnail().getImageUrl(MarvelImage.Size.DETAIL));

            startActivity(intent);

        }
    };

    private AbsListView.OnScrollListener scrollList = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            int threshold = 20;
            int count = lstCharacters.getCount();

            if (scrollState == SCROLL_STATE_IDLE) {
                boolean refresh = false;
                if(lstCharacters.getFirstVisiblePosition() == 0 && pageCount > 1)
                {
                    //refresh = true;
                    //pageCount--;
                }
                if (lstCharacters.getLastVisiblePosition() == ((threshold * (pageCount+1))-1))
                {
                    refresh = true;
                    //pageCount++;
                }
                if (refresh) {//>= count - threshold) {// && pageCount < 2) {
                    rl_progress.setVisibility(View.VISIBLE);

                    Intent intent = new Intent(getApplicationContext(), ServiceAPI.class);
                    intent.setAction(ServiceAPI.CHARACTERGRID);
                    Bundle bundle = new Bundle();
                    intent.putExtras(bundle);
                    intent.putExtra("limit", "" + threshold);
                    if(pageCount == 0)
                        intent.putExtra("offset", "" + threshold);
                    else
                        intent.putExtra("offset", "" + (threshold * (pageCount+1)));

                    intent.putExtra("nameStartsWith", txtSearch.getQuery().toString());
                    pageCount++;
                    startService(intent);
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {

        }

    };

    class ListAdapterCharacter extends ArrayAdapter<Character>
    {
        private ArrayList<Character>filteredData = null;

        ListAdapterCharacter(Context context, ArrayList<Character> listtables_ad)
        {
            super(context, R.layout.character_item_grid, listtables_ad);
            filteredData = listtables_ad;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            View row=convertView;

            if (row==null)
            {
                LayoutInflater inflater=getLayoutInflater();
                row=inflater.inflate(R.layout.character_item_grid, parent, false);
                row.setTag(R.id.listview_item_title, row.findViewById(R.id.listview_item_title));
                row.setTag(R.id.listview_image, row.findViewById(R.id.listview_image));

            }

            TextView titolo=(TextView)row.getTag(R.id.listview_item_title);
            ImageView image_ch=(ImageView)row.getTag(R.id.listview_image);
            titolo.setText(filteredData.get(position).getName().toString());
            String path = filteredData.get(position).getThumbnail().getImageUrl(MarvelImage.Size.LANDSCAPE_SMALL);

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.placeholder)
                    .showImageOnFail(R.drawable.placeholder)
                    .resetViewBeforeLoading(false)
                    .delayBeforeLoading(0)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(false)
                    .bitmapConfig(Bitmap.Config.ARGB_8888)
                    .build();

            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(path, image_ch, options);

            return(row);

        }

    }


}
