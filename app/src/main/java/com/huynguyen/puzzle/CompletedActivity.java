package com.huynguyen.puzzle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.renderscript.Sampler;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.widget.Toast.LENGTH_SHORT;
import static com.huynguyen.puzzle.MainActivity.DATABASE_NAME;
import static com.huynguyen.puzzle.MainActivity.database;
import static com.huynguyen.puzzle.R.color.maincolor;
import static com.huynguyen.puzzle.R.drawable.ic__x2;
import static com.huynguyen.puzzle.R.drawable.ic__x3;
import static com.huynguyen.puzzle.R.drawable.ic__x4;

import com.google.android.material.tabs.TabLayout;

public class CompletedActivity extends AppCompatActivity {

    ListView lvCompleted1,lvCompleted2,lvCompleted3;
    WonAdapter WonAdapter;
    ImageButton btnSave;
    ImageButton btnBack;
    Bitmap bm;
    ArrayList<Integer> idList;
    int chunkNumbers;
    Long pauseTimeFromSave =SystemClock.uptimeMillis();
    public static FileSave selectedFileSave;
    public static boolean clickFromMainActivity=false;
    int itemPositionToDeleteFromDB;
    TabHost tabHost;
    TabWidget tw;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        addControls();
        setData();
        showFilesaveToListview(2);
        addEvents();
    }

    private void addControls() {
        lvCompleted1 = (ListView) findViewById(R.id.lvCompletedTab1);
        lvCompleted2 = findViewById(R.id.lvCompletedTab2);
        lvCompleted3 = findViewById(R.id.lvCompletedTab3);
        WonAdapter = new WonAdapter(this,R.layout.item_won);
        lvCompleted1.setAdapter(WonAdapter);
        lvCompleted2.setAdapter(WonAdapter);
        lvCompleted3.setAdapter(WonAdapter);
        btnBack = findViewById(R.id.imgbtnBack);
        idList=new ArrayList<>();
        tabHost = findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("t1");
        tab1.setContent(R.id.lvCompletedTab1);
        tab1.setIndicator("",getResources().getDrawable(ic__x2));
        tabHost.addTab(tab1);

        TabHost.TabSpec tab2 = tabHost.newTabSpec("t2");
        tab2.setContent(R.id.lvCompletedTab2);
        tab2.setIndicator("",getResources().getDrawable(ic__x3));
        tabHost.addTab(tab2);

        TabHost.TabSpec tab3 = tabHost.newTabSpec("t3");
        tab3.setContent(R.id.lvCompletedTab3);
        tab3.setIndicator("",getResources().getDrawable(ic__x4));
        tabHost.addTab(tab3);

        tw = (TabWidget)tabHost.findViewById(android.R.id.tabs);
        tw.setBackgroundColor(Color.GRAY);
        tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.WHITE);
        //https://stackoverflow.com/questions/9356546/is-it-possible-to-change-the-color-of-selected-tab-in-android

        View tabView2x2 = tw.getChildTabViewAt(0);
        TextView tv2x2 = (TextView)tabView2x2.findViewById(android.R.id.title);

        View tabView3x3 = tw.getChildTabViewAt(1);
        TextView tv3x3 = (TextView)tabView3x3.findViewById(android.R.id.title);

        View tabView4x4 = tw.getChildTabViewAt(2);
        TextView tv4x4 = (TextView)tabView4x4.findViewById(android.R.id.title);
    }
    private void setData() {

    }

    private void addEvents() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();
            }
        });

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTabChanged(String tabId) {
                changeTabColor();
                if(tabId.equals("t1")){
                    showFilesaveToListview(2);
                }
                if(tabId.equals("t2")){
                    showFilesaveToListview(3);
                }
                if(tabId.equals("t3")){
                    showFilesaveToListview(4);
                }
            }
        });
    }

    private void changeTabColor() {
        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++) {
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.GRAY); //unselected
        }
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.WHITE); //selected
    }

    @Override
    protected void onStop() {
        super.onStop();
        PuzzleActivity.clickFromPuzzel=false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.contextmenu_removeitem_db,menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDelete:{
                //database.delete("filesaveTable","id=?",new String[] {String.valueOf(itemPositionToDeleteFromDB)});
                //showFilesaveToListview();
            }
        }
        return super.onContextItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showFilesaveToListview(int grid) {
        MainActivity.database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.rawQuery("select * from completedTable where grid ="+grid,null);
        //Cursor cursor = MainActivity.database.query("completedTable",null,null,null,null,null,null);
        WonAdapter.clear();
        ArrayList<FileSave> listFileSave = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            byte[] image = cursor.getBlob(1);
            Bitmap bitmap = getImage(image);
            int minute = cursor.getInt(2);
            int second = cursor.getInt(3);
            Long move = Long.valueOf(cursor.getInt(4));
            String date = cursor.getString(5);
            int gird = cursor.getInt(6);
            int[] arr = null;
            FileSave fileSave = new FileSave(id,bitmap,move,second,minute,gird,arr,date);
            listFileSave.add(fileSave);
        }
        listFileSave.sort(Comparator.comparing(FileSave::getMinutes).thenComparing(FileSave::getSeconds));
        WonAdapter.addAll(listFileSave);
        cursor.close();
    }

    private void newFileSave() {
        Intent i = getIntent();
        int minute = i.getIntExtra("minutes",0);
        int second = i.getIntExtra("second",0);
        Long moves = i.getLongExtra("moves",0);
        String date = i.getStringExtra("date");
        ContentValues values = new ContentValues();
        imagesPositions();
        values.put("image",bitmapByteArray());
        values.put("minute",minute);
        values.put("second",second);
        values.put("move",moves);
        values.put("date",date);
        values.put("grid",chunkNumbers);
        values.put("arraypostion",imagesPositions());
        Long k =MainActivity.database.insert("filesaveTable",null,values);
        if(k>0) {
            Toast.makeText(this,"saved successfully",Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] bitmapByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapByteArr = bos.toByteArray();
        return bos.toByteArray();
    }

    private String imagesPositions() {
        String imagePositions = "";
        if(idList.size()>0) {
            for(int i =0;i<idList.size();i++) {
                imagePositions=imagePositions+idList.get(i)+",";
            }
            return imagePositions;
        }
        else {
            Toast.makeText(this,"idlist empty",Toast.LENGTH_SHORT).show();
        }
        return imagePositions;
    }

    private static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    private int[] imagepostionArr(String str) {
        String[] array = str.split("\\,");
        int[] intArr = new int[array.length];
        for(int i =0;i<array.length;i++) {
            intArr[i] = Integer.parseInt(array[i]);
        }
        return intArr;
    }
}