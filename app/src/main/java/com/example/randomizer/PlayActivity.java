package com.example.randomizer;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static java.lang.System.out;


public class PlayActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "ticket";
    String message="";
    float[] color;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(0);
        path = Environment.getExternalStorageDirectory()+"/Ticket/";
        Intent intent = getIntent();
        message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TableLayout ticket = (TableLayout) findViewById(R.id.ticket);
        String[] ticket_row = message.split("\n");
        color = new float[(ticket.getChildCount()-1)*((TableRow)ticket.getChildAt(0)).getChildCount()];
        for (int i = 0; i < ticket.getChildCount(); i++) {
            TableRow row = (TableRow) ticket.getChildAt(i);
            String[] ticket_col = ticket_row[i].split("\t");
            for (int j = 0; j < row.getChildCount(); j++) {

                Button row_col = (Button) row.getChildAt(j);
                if(i<3){
                    color[i*row.getChildCount()+j]=Float.parseFloat(ticket_col[j].split(",")[1]);
                    row_col.setAlpha(color[i*row.getChildCount()+j]);
                }else if(i>3){
                    color[(i-1)*row.getChildCount()+j]=Float.parseFloat(ticket_col[j].split(",")[1]);
                    row_col.setAlpha(color[(i-1)*row.getChildCount()+j]);
                }
                if (!(ticket_col[j].split(",")[0].equals("-") || ticket_col[j].equals("NAME")))
                    row_col.setText(ticket_col[j].split(",")[0]);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        TableLayout ticket = (TableLayout) findViewById(R.id.ticket);

        for (int i = 0; i < ticket.getChildCount(); i++) {
            TableRow row = (TableRow) ticket.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                Button row_col = (Button) row.getChildAt(j);
                if(i<3){
                    color[i*row.getChildCount()+j]=row_col.getAlpha();
                }else if(i>3){
                    color[(i-1)*row.getChildCount()+j]=row_col.getAlpha();
                }
            }
        }
        outState.putFloatArray("color",color);
        outState.putString("message",message);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        color  = (float[]) savedInstanceState.get("color");
        message = (String) savedInstanceState.get("message");
        TableLayout ticket = (TableLayout) findViewById(R.id.ticket);

        for (int i = 0; i < ticket.getChildCount(); i++) {
            TableRow row = (TableRow) ticket.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                Button row_col = (Button) row.getChildAt(j);
                if(i<3){
                    row_col.setAlpha(color[i*row.getChildCount()+j]);
                }else if(i>3){
                    row_col.setAlpha(color[(i-1)*row.getChildCount()+j]);
                }
            }
        }
    }

    @Override
    public void onBackPressed(){
        String old_message=message;
        message="";
        TableLayout ticket = (TableLayout) findViewById(R.id.ticket);
        for (int i = 0; i < ticket.getChildCount(); i++) {
            TableRow row = (TableRow) ticket.getChildAt(i);
            String[] old_message_row = old_message.split("\n");
            for (int j = 0; j < row.getChildCount(); j++) {
                Button row_col = (Button) row.getChildAt(j);
                String[] old_message_row_col= old_message_row[i].split("\t");
                float alpha = row_col.getAlpha();
                if(i<3) {
                    String str_alpha = Float.toString(alpha);
                    message = message + old_message_row_col[j].split(",")[0] + ","+str_alpha+"\t";

                }else if(i>3){
                    String str_alpha = Float.toString(alpha);
                    message = message + old_message_row_col[j].split(",")[0] + ","+str_alpha+"\t";
                }else{
                    message = message + old_message_row_col[j].split(",")[0] + "\t";
                }
            }
            message = message + "\n";
        }
        String[] rows = old_message.split("\n");
        String name = rows[3].trim();
        if(name.isEmpty())
            name="name";
        String location=path+"text/"+name+".txt";
        if(ContextCompat.checkSelfPermission(PlayActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
            create_folder();
            try {
                File file = new File(location);
                FileOutputStream stream = new FileOutputStream(file);
                stream.write(message.getBytes());
                stream.close();
                Toast.makeText(PlayActivity.this, "Saved to " + location, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
//                Log.e("Exception", "File write failed: " + e.toString());
            }
        }else{
            ActivityCompat.requestPermissions(PlayActivity.this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

        Intent result=new Intent();
        result.putExtra(EXTRA_MESSAGE,message);
        setResult(Activity.RESULT_OK,result);
        super.onBackPressed();
    }

    long lastClickTime=0;
    public void cut_num(View view) {
        long clickTime = System.currentTimeMillis();
        if (view.getAlpha()==0.5 && clickTime-lastClickTime<500){
            view.setAlpha((float)1.0);
        }else if (view.getAlpha()==1.0){
            view.setAlpha((float)0.5);
        }
        lastClickTime=clickTime;
   }

    public void share(View v) {
        Bitmap cs = null;
        TableLayout ticket = (TableLayout) findViewById(R.id.ticket);
        ticket.setDrawingCacheEnabled(true);
        ticket.buildDrawingCache(true);
        cs = Bitmap.createBitmap(ticket.getDrawingCache());
        Canvas canvas = new Canvas(cs);
        ticket.draw(canvas);
        canvas.save();
        ticket.setDrawingCacheEnabled(false);
        String filename=((Button)((TableRow)ticket.getChildAt(3)).getChildAt(0)).getText().toString();
        if(ContextCompat.checkSelfPermission(PlayActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
            create_folder();
            try {
                if (filename.isEmpty())
                    filename = "name";
                File file = new File(path + "pic/" + filename + ".png");
                FileOutputStream stream = new FileOutputStream(file);
                cs.compress(Bitmap.CompressFormat.PNG, 100, stream); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored

                stream.close();
                Toast.makeText(PlayActivity.this, "Saved to " + path + "pic/" + filename + ".png", Toast.LENGTH_SHORT).show();

                Uri uri = Uri.parse(file.getPath());
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/png");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(sharingIntent,
                        "Share image using"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(PlayActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(PlayActivity.this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

    }

    public void create_folder(){
        if(ContextCompat.checkSelfPermission(PlayActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            File folder = new File(path);
            folder.mkdir();
            File folder_text = new File(path,"text");
            folder_text.mkdir();
            File folder_pic = new File(path,"pic");
            folder_pic.mkdir();
        }
    }
}
