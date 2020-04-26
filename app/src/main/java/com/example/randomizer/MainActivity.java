package com.example.randomizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "ticket";
    String[] text;
    String path;
    String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(0);
        path = Environment.getExternalStorageDirectory()+"/Ticket/";
        TableLayout ticket = (TableLayout) findViewById(R.id.ticket);
        text = new String[(ticket.getChildCount()-1)*((TableRow)ticket.getChildAt(0)).getChildCount()+1];
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
        create_folder();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        TableLayout ticket = (TableLayout) findViewById(R.id.ticket);

        for (int i = 0; i < ticket.getChildCount(); i++) {
            TableRow row = (TableRow) ticket.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                TextInputLayout col = (TextInputLayout) row.getChildAt(j);
                TextInputEditText row_col = (TextInputEditText) col.getEditText();
                if (i<3)
                    text[i * row.getChildCount() + j] = row_col.getText().toString().trim();
                else if(i>3)
                    text[(i-1) * row.getChildCount() + j] = row_col.getText().toString().trim();
                else
                    text[(ticket.getChildCount()-1)*((TableRow)ticket.getChildAt(0)).getChildCount()] = row_col.getText().toString().trim();
            }
        }
        outState.putStringArray(EXTRA_MESSAGE, text);
        outState.putString("message",message);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        text = (String[]) savedInstanceState.get(EXTRA_MESSAGE);
        message=(String) savedInstanceState.get("message");
        TableLayout ticket = (TableLayout) findViewById(R.id.ticket);

        for (int i = 0; i < ticket.getChildCount(); i++) {
            TableRow row = (TableRow) ticket.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                TextInputLayout col = (TextInputLayout) row.getChildAt(j);
                TextInputEditText row_col = (TextInputEditText) col.getEditText();
                if (i<3)
                    row_col.setText(text[i * row.getChildCount() + j]);
                else if(i>3)
                    row_col.setText(text[(i-1) * row.getChildCount() + j]);
                else
                    row_col.setText(text[(ticket.getChildCount()-1)*((TableRow)ticket.getChildAt(0)).getChildCount()]);
            }
        }

    }

    public void create_folder(){
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            File folder = new File(path);
            folder.mkdir();
            File folder_text = new File(path,"text");
            folder_text.mkdir();
            File folder_pic = new File(path,"pic");
            folder_pic.mkdir();
        }
    }

    public void save_ticket(View view) {
        // Do something in response to button
        Log.d("start",message+"message");
        TableLayout ticket = (TableLayout) findViewById(R.id.ticket);
        if(message==null){
            message="";
            for (int i = 0; i < ticket.getChildCount(); i++) {
                TableRow row = (TableRow) ticket.getChildAt(i);
                for (int j = 0; j < row.getChildCount(); j++) {
                    TextInputLayout col = (TextInputLayout) row.getChildAt(j);
                    TextInputEditText row_col = (TextInputEditText) col.getEditText();
                    String num = row_col.getHint().toString() + row_col.getText().toString();
                    if(i!=3) {
                        if (!num.equals(row_col.getHint().toString())) {
                            message = message + num.substring(row_col.getHint().toString().length()) + ",1.0\t";
                        } else {
                            message = message + row_col.getHint().toString() + ",1.0\t";
                        }
                    }else{
                        if (!num.equals(row_col.getHint().toString())) {
                            message = message + num.substring(row_col.getHint().toString().length()) + "\t";
                        } else {
                            message = message + row_col.getHint().toString() + "\t";
                        }
                    }
                }
                message = message + "\n";
            }
        }else {
            String old_message=message;
            message="";
            for (int i = 0; i < ticket.getChildCount(); i++) {
                TableRow row = (TableRow) ticket.getChildAt(i);
                String[] old_message_row = old_message.split("\n");
                for (int j = 0; j < row.getChildCount(); j++) {
                    TextInputLayout col = (TextInputLayout) row.getChildAt(j);
                    TextInputEditText row_col = (TextInputEditText) col.getEditText();
                    String num = row_col.getHint().toString() + row_col.getText().toString();

                    if(i!=3) {
                        String color = old_message_row[i].split("\t")[j].split(",")[1];
                        if (!num.equals(row_col.getHint().toString())) {
                            message = message + num.substring(row_col.getHint().toString().length()) + ","+color+"\t";
                        } else {
                            message = message + row_col.getHint().toString() + ","+color+"\t";
                        }
                    }else{
                        if (!num.equals(row_col.getHint().toString())) {
                            message = message + num.substring(row_col.getHint().toString().length()) + "\t";
                        } else {
                            message = message + row_col.getHint().toString() + "\t";
                        }
                    }
                }
                message = message + "\n";
            }
        }
        TableRow row=(TableRow) ticket.getChildAt(3);
        TextInputLayout col = (TextInputLayout) row.getChildAt(0);
        TextInputEditText row_col = (TextInputEditText) col.getEditText();
        String name;
        if(row_col.getText().toString().isEmpty())
            name = row_col.getHint().toString();
        else
            name = row_col.getText().toString();
        String location=path+"text/"+name+".txt";
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
            create_folder();
            try {
                File file = new File(location);
                FileOutputStream stream = new FileOutputStream(file);
                stream.write(message.getBytes());
                stream.close();
                Toast.makeText(MainActivity.this, "Saved to " + location, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, PlayActivity.class);
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivityForResult(intent,2);
            } catch (IOException e) {
//                Log.e("Exception", "File write failed: " + e.toString());
            }
        }else{
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
        Log.d("end",message);
    }

    public void setData(){
        TableLayout ticket = (TableLayout) findViewById(R.id.ticket);
        String[] ticket_row = message.split("\n");
        for (int i = 0; i < ticket.getChildCount(); i++) {
            TableRow row = (TableRow) ticket.getChildAt(i);
            String[] ticket_col = ticket_row[i].split("\t");

            for (int j = 0; j < row.getChildCount(); j++) {
                TextInputLayout col = (TextInputLayout) row.getChildAt(j);
                TextInputEditText row_col = (TextInputEditText) col.getEditText();

                if (!(ticket_col[j].equals("-,1.0") || ticket_col[j].equals("NAME"))) {
                    if (i < 3) {
                        text[i * row.getChildCount() + j] = ticket_col[j].split(",")[0];
                        row_col.setText(text[i * row.getChildCount() + j]);


                    } else if (i > 3) {
                        text[(i - 1) * row.getChildCount() + j] = ticket_col[j].split(",")[0];
                        row_col.setText(text[(i - 1) * row.getChildCount() + j]);
                    } else {
                        text[(ticket.getChildCount() - 1) * ((TableRow) ticket.getChildAt(0)).getChildCount()] = ticket_col[j];
                        row_col.setText(text[(ticket.getChildCount() - 1) * ((TableRow) ticket.getChildAt(0)).getChildCount()]);
                    }
                }else{
                    row_col.setText("");
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1  && resultCode  == RESULT_OK) {

                String requiredValue = data.getDataString();

                requiredValue=path+requiredValue.substring(requiredValue.lastIndexOf("Ticket/") + 7);
                if (!requiredValue.substring(requiredValue.lastIndexOf(".") + 1).equals("txt"))
                    return;
                message="";
                try {
                    BufferedReader br = new BufferedReader(new FileReader(requiredValue));
                    String line;

                    while ((line = br.readLine()) != null) {
                        message=message+line+"\n";
                    }

                    br.close();
                    setData();
                    Intent intent = new Intent(this, PlayActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, message);


                    startActivityForResult(intent,2);
                }
                catch (IOException e) {
                    //You'll need to add proper error handling here
                }
            }

        if (requestCode == 2  && resultCode  == RESULT_OK) {
            message=data.getStringExtra("ticket");
            setData();
        }
    }


    public void open_file(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(path+"text/");
        intent.setDataAndType(uri, "text/plain");
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
            create_folder();
            startActivityForResult(intent, 1);
        }else{
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
    }

}
