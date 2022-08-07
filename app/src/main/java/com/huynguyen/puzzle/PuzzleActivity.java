
package com.huynguyen.puzzle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.service.autofill.FillEventHistory;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huynguyen.puzzle.GridViewAdapter;
import com.huynguyen.puzzle.MainActivity;
import com.huynguyen.puzzle.Pieces;
import com.huynguyen.puzzle.R;
import com.huynguyen.puzzle.SaveActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static android.widget.Toast.LENGTH_SHORT;

public class PuzzleActivity extends AppCompatActivity {
    GridViewAdapter imageAdapter;
   public static ArrayList<Bitmap> bitmapsList;
   public static ArrayList<Bitmap> chunkedImagesLoad;
   private ArrayList<Bitmap> bitmapsListOrigin;
   public static int chunkNumbers;
   int height=MainActivity.height;
   int width= MainActivity.width;
   int Seconds,Minutes;
   Long countMove;
   private GridView gridView;
   Bitmap scaledLastBitmap,lastBitmapForWin;
   public static Bitmap bm;
   ImageView imgZoom;
   private FrameLayout rootLayout;
   boolean clicked= false;
   Long startTime=0L;
   public static Long pauseTime=0L;
   Handler handler;
   TextView textTime, textMoves;
   ImageButton btnSave,btnHome;
   public static ArrayList<Pieces> originalPiecesList;
   public static ArrayList<Integer> pieceIdToSaveList;
   public static boolean clickFromPuzzel = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
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
        addEvents();

    }
    private void addControls() {
        bitmapsList = new ArrayList<Bitmap>();
        bitmapsListOrigin = new ArrayList<Bitmap>();
        imgZoom = (ImageView) findViewById(R.id.imgZoom);
        gridView = (GridView) findViewById(R.id.GV);
        rootLayout = (FrameLayout) findViewById(R.id.activity_puzzle);
        textTime = (TextView) findViewById(R.id.textTime);
        textMoves = (TextView) findViewById(R.id.textMoves);
        btnSave = (ImageButton) findViewById(R.id.btnSave);
        btnHome = (ImageButton) findViewById(R.id.btnHome);
        originalPiecesList = new ArrayList<Pieces>();
        pieceIdToSaveList = new ArrayList<>();
        handler = new Handler();
    }

    private void setData() {
        chunkNumbers = MainActivity.chunkNumbers;
        imgZoom.setImageBitmap(MainActivity.bm);
        bm = MainActivity.bm;
        for(int i =0;i<MainActivity.chunkedImages.size(); i++)
        {
            bitmapsList.add(MainActivity.chunkedImages.get(i));
        }
        countMove = 0L;
        textMoves.setText("0");
        if(MainActivity.clickLoadButton==true)
        {
            chunkNumbers = SaveActivity.selectedFileSave.getChunkNumber();
            Toast.makeText(this, "", LENGTH_SHORT);
            bm = SaveActivity.selectedFileSave.getImg();
            imgZoom.setImageBitmap(bm);
            height= SaveActivity.selectedFileSave.getImg().getHeight();
            width = SaveActivity.selectedFileSave.getImg().getWidth();
            divideImage(bm,chunkNumbers);
            bitmapsList.clear();
            for(int i =0;i<chunkedImagesLoad.size(); i++)
            {
                bitmapsList.add(chunkedImagesLoad.get(i));
            }
            Minutes = SaveActivity.selectedFileSave.getMinutes();
            Seconds = SaveActivity.selectedFileSave.getSeconds();
            countMove = SaveActivity.selectedFileSave.getMoves();
            textMoves.setText(countMove+"");
        }
        startTime = SystemClock.uptimeMillis();
        gridView.setBackgroundColor(Color.BLACK);
        gridView.setNumColumns(chunkNumbers);
        gridView.setPadding(0,0,5,5);
        imgZoom.setBackgroundColor(Color.BLACK);
        imgZoom.setPadding(2,2,2,2);
        imgZoom.bringToFront();
        setViewDimensions(imgZoom);
        setViewDimensions(gridView);
        setImagviewDimensions();

        Bitmap lastBitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.grey);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(lastBitmap,lastBitmap.getWidth(),lastBitmap.getHeight(),true);

        int bitmapWidth = lastBitmap.getWidth()/10;
        int bitmapHeight = bitmapWidth*height/width;
        scaledLastBitmap = Bitmap.createBitmap(scaledBitmap,0,0,bitmapWidth,bitmapHeight);
        lastBitmapForWin = bitmapsList.get(bitmapsList.size()-1);

        bitmapsList.remove(bitmapsList.size()-1);
        bitmapsList.add(bitmapsList.size(),scaledLastBitmap);
        for(int i =0;i<bitmapsList.size();i++)
        {
            bitmapsListOrigin.add(bitmapsList.get(i));
        }
        makeOriginalPiecesList();
        if(MainActivity.clickLoadButton==true)
        {
            bitmapsList.clear();
            makeNewBitmapListFromFilesave();
        }
        imageAdapter = new GridViewAdapter(this,bitmapsList,width,height);
        gridView.setAdapter(imageAdapter);

        checkPositionsForUntidy();
        if(MainActivity.clickLoadButton==false)
        {
            untidyPositions();
        }
    }

    private void addEvents() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changePosition(position);
                textMoves.setText(countMove+"");
                checkPositions();
                checkWin();
            }
        });
        imgZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int []imgLocation = new int[2];
                int []gridLocation = new int[2];
                imgZoom.getLocationInWindow(imgLocation);
                gridView.getLocationInWindow(gridLocation);
                int imageX = imgLocation[0];
                int imageY = imgLocation[1];
                int gridX = gridLocation[0];
                int gridY = gridLocation[1];
                int rootWidth = rootLayout.getWidth();
                int rootHeight =rootLayout.getHeight();
                if(!clicked)
                {
                    imgZoom.setPadding(0,0,0,0);
                    imgZoom.animate().translationY(gridY+gridView.getHeight()/2-imageY-imgZoom.getHeight()/2);
                    imgZoom.animate().translationX(-(imageX-rootWidth/2+imgZoom.getWidth()/2));
                    if(width>height)
                    {
                        imgZoom.animate().scaleX(gridView.getWidth()/imgZoom.getWidth());
                        imgZoom.animate().scaleY(gridView.getWidth()/imgZoom.getWidth());
                    }
                    else
                    {
                        imgZoom.animate().scaleX(gridView.getHeight()/imgZoom.getHeight());
                        imgZoom.animate().scaleY(gridView.getHeight()/imgZoom.getHeight());
                    }
                    gridView.animate().alpha(0);
                    clicked=true;
                }
                else if(clicked)
                {
                    imgZoom.animate().scaleX(1f);
                    imgZoom.animate().scaleY(1f);
                    imgZoom.animate().translationY(0);
                    imgZoom.animate().translationX(0);
                    gridView.animate().alpha(1);
                    imgZoom.setPadding(5,5,5,5);
                    clicked=false;
                }
                imgZoom.animate().setDuration(200);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePieceIdToSaveList();
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                int currentMinute = Minutes;
                int currentSecond = Seconds;
                Long currentCountMoves = countMove;
                Intent i = new Intent(PuzzleActivity.this,SaveActivity.class);
                i.putExtra("date",currentDate);
                i.putExtra("minutes",currentMinute);
                i.putExtra("second",currentSecond);
                i.putExtra("moves",currentCountMoves);
                i.putExtra("chunknumber",chunkNumbers);
                clickFromPuzzel = true;
                MainActivity.clickLoadButton=false;
                startActivity(i);
                SaveActivity.clickFromMainActivity=false;
            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHomeFromPuzzleDialog();
            }
        });
    }

    private void goHomeFromPuzzleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PuzzleActivity.this);
        builder.setTitle("Back to Home screen").setMessage("Your progress will not be saved. Are you sure to countinue?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(PuzzleActivity.this,MainActivity.class);
                dialog.cancel();
                startActivity(i);
                pauseTime=0l;
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void makeNewBitmapListFromFilesave()
    {
        for(int i=0;i<SaveActivity.selectedFileSave.getArrId().length;i++)
        {
            for(int j =0;j<originalPiecesList.size();j++)
            {
                if(SaveActivity.selectedFileSave.getArrId()[i] == originalPiecesList.get(j).getId())
                {
                    bitmapsList.add(originalPiecesList.get(j).getBitmap());
                }
            }
        }
    }

    private void makeOriginalPiecesList()
    {
        for(int i=0;i<bitmapsListOrigin.size();i++)
        {
            Pieces piece = new Pieces(i,bitmapsListOrigin.get(i));
            originalPiecesList.add(piece);
        }
    }
    private void makePieceIdToSaveList()
    {
        for(int i =0;i<bitmapsListOrigin.size();i++)
        {
            for(int j =0;j<bitmapsList.size();j++)
            {

                if(bitmapsList.get(i).sameAs(originalPiecesList.get(j).getBitmap()))
                {
                    if(pieceIdToSaveList.contains(originalPiecesList.get(j).getId()))
                    {
                        continue;
                    }
                    else
                    {
                        pieceIdToSaveList.add(originalPiecesList.get(j).getId());
                        break;
                    }
                }
            }
        }
    }
    private void checkWin() {
        if (checkPositions()==true)
        {
            bitmapsList.remove(bitmapsList.size()-1);
            bitmapsList.add(bitmapsList.size(),lastBitmapForWin);
            handler.removeCallbacks(runnable);
            Toast.makeText(this,"win",Toast.LENGTH_SHORT).show();
            newWon();
        }
    }

    private void newWon() {
        ContentValues values = new ContentValues();
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        values.put("image",bitmapByteArray());
        values.put("minute",Minutes);
        values.put("second",Seconds);
        values.put("move",countMove);
        values.put("date",currentDate);
        values.put("grid",chunkNumbers);
        values.put("arraypostion","");
        Long k =MainActivity.database.insert("completedTable",null,values);
        if(k>0) {
            Toast.makeText(this,"saved successfully",Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] bitmapByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapByteArr = bos.toByteArray();
        return bitmapByteArr;
    }

    private boolean checkPositions() {
        int count=0;
        for (int i =0;i<bitmapsList.size()-1;i++)
        {
            if (bitmapsList.get(i).sameAs(bitmapsListOrigin.get(i)))
            {
                count++;
            }
        }
        if(count==bitmapsList.size()-1)
        {
            return true;
        }
        return false;
    }

    private void changePosition(int position) {
        Bitmap selectedBitmap = (Bitmap) bitmapsList.get(position);

        if(bitmapsList.size()>position+1 && (position+1)%chunkNumbers!=0)
        {
            Bitmap bitmapRight = (Bitmap) bitmapsList.get(position+1);
            if(bitmapRight.sameAs(scaledLastBitmap))
            {
               swapTwoItems(position,position+1,selectedBitmap,bitmapRight);
                countMove++;
            }
        }
        if((position%chunkNumbers!=0))
        {
            Bitmap bitmapLeft = (Bitmap) bitmapsList.get(position-1);
            if(bitmapLeft.sameAs(scaledLastBitmap))
            {
                swapTwoItems(position,position-1,selectedBitmap,bitmapLeft);
                countMove++;
            }
        }
        if (position>=chunkNumbers)
        {
            Bitmap bitmapAbove = (Bitmap) bitmapsList.get(position-chunkNumbers);
            if(bitmapAbove.sameAs(scaledLastBitmap))
            {
                swapTwoItems(position,position-chunkNumbers,selectedBitmap,bitmapAbove);
                countMove++;
            }
        }
        if(position<bitmapsList.size()-chunkNumbers)
        {
            Bitmap bitmapBelow = (Bitmap) bitmapsList.get(position+chunkNumbers);
            if(bitmapBelow.sameAs(scaledLastBitmap))
            {
                swapTwoItems(position,position+chunkNumbers,selectedBitmap,bitmapBelow);
                countMove++;
            }
        }
    }

   private void swapTwoItems(int position, int positionSwap, Bitmap selectedBitmap, Bitmap bitmapSwap) {
       bitmapsList.remove(position);
       bitmapsList.add(position,bitmapSwap);
       bitmapsList.remove(positionSwap);
       bitmapsList.add(positionSwap,selectedBitmap);
       imageAdapter.notifyDataSetChanged();
   }

    private void setImagviewDimensions() {
        ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
        if (height>width)
        {
            int newWidth = layoutParams.width/chunkNumbers;
            int newHeight = layoutParams.height/chunkNumbers;
            height = newHeight;
            width = newWidth;
        }
    }

    private void setViewDimensions(View v) {
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        int newGVheight=0;
        int newGVwidth=0;
        if (height>width)
       {
           newGVwidth = layoutParams.height*width/height;
           layoutParams.width = newGVwidth;
       }
        else if(height<=width)
        {
            newGVheight = layoutParams.width*height/width;
            layoutParams.height = newGVheight;
        }
        v.setLayoutParams(layoutParams);
        v.requestLayout();
    }

    private int checkPositionsForUntidy() {
        int count=0;
        for (int i =0;i<bitmapsList.size()-1;i++)
        {
            if (bitmapsList.get(i).sameAs(bitmapsListOrigin.get(i)))
            {
                count++;
            }
        }
        return count;
    }

    private void untidyPositions() {
        Random rd = new Random();
        int position = bitmapsList.size()-1;
        while (checkPositionsForUntidy()>0)
        {
            int x = 1 + rd.nextInt(5);
            if(x==1 && position%chunkNumbers!=0)
            {
                Bitmap bitmapScaled = (Bitmap) gridView.getItemAtPosition(position);
                Bitmap bitmapLeft = (Bitmap) gridView.getItemAtPosition(position-1);
                swapTwoItems(position,position-1,bitmapScaled,bitmapLeft);
                position--;
            }
            else if (x==2 && !(position<chunkNumbers))
            {
                Bitmap bitmapScaled = (Bitmap) gridView.getItemAtPosition(position);
                int positionAbove = position-chunkNumbers;
                Bitmap bitmapAbove = (Bitmap) gridView.getItemAtPosition(positionAbove);
                swapTwoItems(position,positionAbove,bitmapScaled,bitmapAbove);
                position=positionAbove;
            }
            else if (x==3 && (position+1)%chunkNumbers!=0)
            {
                Bitmap bitmapScaled = (Bitmap) gridView.getItemAtPosition(position);
                int positionRight = position+1;
                Bitmap bitmapRight = (Bitmap) gridView.getItemAtPosition(positionRight);
                swapTwoItems(position,positionRight,bitmapScaled,bitmapRight);
                position=positionRight;
            }
            else if (x==4 && (bitmapsList.size()-position-1)>=chunkNumbers)
            {
                Bitmap bitmapScaled = (Bitmap) gridView.getItemAtPosition(position);
                int positionBelow = position+chunkNumbers;
                Bitmap bitmapBelow = (Bitmap) gridView.getItemAtPosition(positionBelow);
                swapTwoItems(position,positionBelow,bitmapScaled,bitmapBelow);
                position=positionBelow;
            }
        }
    }

    private void divideImage(Bitmap bitmap, int chunkNumbers) {
        chunkedImagesLoad = new ArrayList<Bitmap>();
        //BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        //Bitmap bitmap = drawable.getBitmap();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);

        int rows = chunkNumbers;
        int cols = chunkNumbers;
        int bitmapWidth = bitmap.getWidth()/chunkNumbers;
        int bitmapHeight = bitmap.getHeight()/chunkNumbers;
        int yCoord = 0;
        for (int x = 0; x<rows;x++)
        {
            int xCoord = 0;
            for(int y = 0; y<cols; y++)
            {
                chunkedImagesLoad.add(Bitmap.createBitmap(scaledBitmap,xCoord,yCoord,bitmapWidth,bitmapHeight));
                xCoord += bitmapWidth;
            }
            yCoord += bitmapHeight;
        }
    }

    public Runnable runnable = new Runnable() {
        public void run() {
            Long MillisecondTime = SystemClock.uptimeMillis() - startTime - pauseTime;
            if(MainActivity.clickLoadButton==true)
            {
                if(SaveActivity.selectedFileSave != null)
                {
                    MillisecondTime = MillisecondTime +SaveActivity.selectedFileSave.getSeconds()*1000;
                }
            }
            Seconds = (int) (MillisecondTime / 1000);
            if(MainActivity.clickLoadButton==true)
            {
                if(SaveActivity.selectedFileSave != null)
                    Minutes = Seconds / 60 + SaveActivity.selectedFileSave.getMinutes();
            }
            else
            {
                Minutes = Seconds / 60;
            }
            Seconds = Seconds % 60;
            if(Minutes>=10)
            {
                textTime.setText("" + Minutes + ":" + String.format("%02d", Seconds) );
            }
            else
            {
                textTime.setText("0" + Minutes + ":" + String.format("%02d", Seconds) );
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(runnable);
    }

}