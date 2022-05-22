package com.huynguyen.puzzle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;
import static com.huynguyen.puzzle.R.color.maincolor;

public class MainActivity extends AppCompatActivity {

    static ImageView imgHinh;
    Button btnUpHinh,btnLoadMain,btnExit;
    ImageButton btnCompleted;
    ImageButton btnStart;
    public static String DATABASE_NAME = "FileSaved.sqlite";
    String DB_PATH_SUFFIX = "/databases/";
    public static SQLiteDatabase database =null;
    int rqCode = 123;
    public static int chunkNumbers;
    public static int rows;
    public static int cols;
    public static int height, width, bitmapWidth, bitmapHeight;
    public static ArrayList<Bitmap> chunkedImages;
    private boolean imgLoaded=false;
    private RadioButton rad2x2,rad3x3,rad4x4;
    public static Bitmap bm;
    public static boolean clickLoadButton=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addControls();
        copyDatabaseFromAsset();
        addEvents();
    }

    private void addEvents() {
        btnUpHinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imgLoaded)
                {
                    if(imgHinh.getWidth()<=imgHinh.getHeight()) {
                        clickLoadButton=false;
                        setChunknumbers();
                        Intent intent = new Intent(MainActivity.this,PuzzleActivity.class);
                        startActivity(intent);
                        finish();
                    }
                   else {
                       Toast.makeText(MainActivity.this,"Please only choose vertical images",LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Vui lòng chọn hình",LENGTH_SHORT).show();
                }
            }
        });
        btnLoadMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickLoadButton=true;
                    Intent intent = new Intent(MainActivity.this,SaveActivity.class);
                    startActivity(intent);
                    SaveActivity.clickFromMainActivity=true;
                    finish();
            }
        });
        btnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CompletedActivity.class);
                startActivity(intent);
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitAppDialog();
            }
        });
    }

    private void exitAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exit Game").setMessage("You wanna quit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
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

    private void copyDatabaseFromAsset() {
        try {
            InputStream myInput = getAssets().open(DATABASE_NAME);

            // Lưu vào đường dẫn chứa database:
            String outFileName = getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;

            //nếu chưa tồn tại thư mục databases thì phải tạo ra nó:
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if(!f.exists())
            {
                //hàm mkdir để tạo ra đường đẫn tới thư mục :
                f.mkdir();
                OutputStream myOutPut = new FileOutputStream(outFileName);
                byte []buffer = new byte[1024];
                int length;

                //mỗi lần đọc 1024byte thành công thì mới lưu vào:
                while ((length=myInput.read(buffer)) > 0)
                {
                    myOutPut.write(buffer,0,length);
                }

                //các lệnh bắt buộc phải có khi kết thúc:
                myOutPut.flush();
                myOutPut.close();
                myInput.close();
            }
        }
        catch (Exception ex)
        {
            Log.e("loi ",ex.toString());
        }
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
    }

    public void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent,rqCode);
    }

    private void setChunknumbers()
    {
        int number = 0;
        if(rad2x2.isChecked())
        {
            number=2;
        }
        else if(rad3x3.isChecked())
        {
            number=3;
        }
        else if(rad4x4.isChecked())
        {
            number=4;
        }
        setChunkNumbers(number);
        BitmapDrawable drawable = (BitmapDrawable) imgHinh.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        divideImage(bitmap,chunkNumbers);
        setImgHinhWidthHeight(chunkNumbers);
    }


    private void setImgHinhWidthHeight(int chunkNumbers) {
        height = imgHinh.getHeight()/chunkNumbers;
        width = imgHinh.getWidth()/chunkNumbers;
    }

    private void setChunkNumbers(int number) {
        chunkNumbers=number;
    }

    public static void divideImage(Bitmap bitmap, int chunkNumbers) {
        chunkedImages = new ArrayList<Bitmap>();
        //BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        //Bitmap bitmap = drawable.getBitmap();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);

        rows = cols = chunkNumbers;
        bitmapWidth = bitmap.getWidth()/chunkNumbers;
        bitmapHeight = bitmap.getHeight()/chunkNumbers;
        int yCoord = 0;
        for (int x = 0; x<rows;x++)
        {
            int xCoord = 0;
            for(int y = 0; y<cols; y++)
            {
                chunkedImages.add(Bitmap.createBitmap(scaledBitmap,xCoord,yCoord,bitmapWidth,bitmapHeight)); 
                xCoord += bitmapWidth;
            }
            yCoord += bitmapHeight;
        }
    }

    public static void imageviewFitsBitmap( int bitmapWidth, int bitmapHeight) {
        int originalWild = 0;
        int originalHeight = 0;
        if(imgHinh.getMeasuredHeight()>imgHinh.getMeasuredWidth())
        {
            originalWild=imgHinh.getHeight();
            originalHeight=imgHinh.getHeight();
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(imgHinh.getHeight(),imgHinh.getHeight());
            imgHinh.setLayoutParams(layoutParams);
            imgHinh.requestLayout();
        }
        else if(imgHinh.getMeasuredHeight()<imgHinh.getMeasuredWidth())
        {
            originalWild=imgHinh.getMeasuredWidth();
            originalHeight=imgHinh.getMeasuredWidth();
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(imgHinh.getMeasuredWidth(),imgHinh.getMeasuredWidth());
            imgHinh.setLayoutParams(layoutParams);
            imgHinh.requestLayout();
        }
        else if(imgHinh.getMeasuredHeight()==imgHinh.getMeasuredWidth())
        {
            originalHeight=imgHinh.getMeasuredHeight();
            originalWild=imgHinh.getMeasuredWidth();
        }
        if(bitmapHeight>bitmapWidth)
        {
            int newWidth = originalHeight*bitmapWidth/bitmapHeight;
            imgHinh.getLayoutParams().width = newWidth;
        }
        else if(bitmapHeight<bitmapWidth)
        {
            int newHeight = originalWild*bitmapHeight/bitmapWidth;
            imgHinh.getLayoutParams().height = newHeight;
        }
        imgHinh.requestLayout();
       // Toast.makeText(this,bitmapWidth+"-"+bitmapHeight+"",LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == rqCode && resultCode == RESULT_OK)
        {
            if(data == null)
            {
                return;
            }
            Uri selectecFileUri = data.getData();
            imgHinh.setImageURI(selectecFileUri);
            try {
                bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectecFileUri);
                imageviewFitsBitmap(bm.getWidth(),bm.getHeight());
                imgHinh.requestLayout();
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgLoaded=true;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        super.onResume();
    }

    private void addControls() {
        imgHinh = (ImageView) findViewById(R.id.img_hinh);
        btnUpHinh = (Button) findViewById(R.id.BTN_upHihn);
        btnStart = findViewById(R.id.BTN_start);
        btnLoadMain = (Button) findViewById(R.id.btnLoadMain);
        btnCompleted = findViewById(R.id.btnCompleted);
        btnExit = findViewById(R.id.btn_Exit);
        rad2x2 = (RadioButton) findViewById(R.id.two);
        rad3x3 = (RadioButton) findViewById(R.id.three);
        rad4x4 = (RadioButton) findViewById(R.id.four);
        chunkedImages = new ArrayList<Bitmap>();

        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}