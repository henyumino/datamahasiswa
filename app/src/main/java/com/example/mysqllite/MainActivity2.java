package com.example.mysqllite;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity {

    Button btInsert, btUpdate, btDelete, btView;
    EditText etNim, etNama, etUmur;
    ImageView imgFoto;
    DatabaseHelper dbHelper= new DatabaseHelper(this);
    String path=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        etNim = (EditText) findViewById(R.id.editTextNim);
        etNama = (EditText) findViewById(R.id.editTextNama);
        etUmur = (EditText) findViewById(R.id.editTextUmur);

        imgFoto = (ImageView) findViewById(R.id.imageViewFoto);

        btInsert = (Button) findViewById(R.id.buttonInsert);
        btUpdate = (Button) findViewById(R.id.buttonUpdate);
        btDelete = (Button) findViewById(R.id.buttonDelete);
        btView = (Button) findViewById(R.id.buttonView);

        Intent intent = getIntent();
        String ModeResult = intent.getStringExtra("Mode");

        if(ModeResult.equalsIgnoreCase("UpdateDelete")){
            etNim.setText(intent.getStringExtra("Nim"));
            etNama.setText(intent.getStringExtra("Nama"));
            etUmur.setText(String.valueOf(intent.getIntExtra("Umur",0)));
            path=intent.getStringExtra("Path");
            BitmapFactory.Options options= new BitmapFactory.Options();
            Bitmap pic = BitmapFactory.decodeFile(path, options);
            myImage mi= new myImage();
            pic=mi.automatic_rotateImg(pic, path);
            imgFoto.setImageBitmap(pic);
            etNim.setEnabled(false);
            btInsert.setEnabled(false);
        }else if(ModeResult.equalsIgnoreCase("Insert")){
            btInsert.setEnabled(true);
            btDelete.setEnabled(false);
            btUpdate.setEnabled(false);
            imgFoto.setImageResource(R.mipmap.ic_launcher_round);

        }

        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAlert("update");
            }
        });

        btInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAlert("insert");
            }
        });

        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAlert("delete");
            }
        });

        btView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent1);
            }
        });


        imgFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("image/jpeg|image/png");
                chooseFile=Intent.createChooser(chooseFile, "Choose File: ");
                startActivityForResult(chooseFile, 100);
            }
        });

    }

    void showDialogAlert(final String mode){
        AlertDialog.Builder albuilder = new AlertDialog.Builder(this);
        int buttonpic=0;
        String message=null;
        switch (mode){
            case "insert":
                buttonpic=R.drawable.save_icon;
                albuilder.setTitle("Are you sure to save data");
                message="click yes to save data";
                break;
            case "update":
                buttonpic=R.drawable.update_icon;
                albuilder.setTitle("Are you sure to update data");
                message="click yes to update data";
                break;
            case "delete":
                buttonpic=R.drawable.delete_icon;
                albuilder.setTitle("Are you sure to delete data");
                message="click yes to delete data";
                break;
        }

        albuilder
                .setMessage(message)
                .setIcon(buttonpic)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (mode){
                            case "insert":
                                Mahasiswa currentMhs=dbHelper.getDataExist(etNim.getText().toString());
                                if(currentMhs==null){
                                    Mahasiswa dataMhs = new Mahasiswa(etNim.getText().toString(), etNama.getText().toString(), path,
                                                Integer.parseInt(etUmur.getText().toString()));
                                    boolean benar = dbHelper.insertData(dataMhs);
                                    if(benar == true){
                                        Toast.makeText(getApplicationContext(), "insert succesfully", Toast.LENGTH_SHORT).show();
                                        cleanComponent();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "insert failed", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(), "Nim Owned By: "+currentMhs.getNama().toString(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case "update":
                                if(dbHelper.updateData(etNim.getText().toString(), etNama.getText().toString(), path, Integer.parseInt(etUmur.getText().toString()))){
                                    Toast.makeText(getApplicationContext(), "Update succesfully", Toast.LENGTH_SHORT).show();
                                    cleanComponent();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Update failed", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case "delete":
                                if(dbHelper.deleteData(etNim.getText().toString())>0){
                                    Toast.makeText(getApplicationContext(), "delete succesfully", Toast.LENGTH_SHORT).show();
                                    cleanComponent();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "delete failed", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (mode){
                    case "insert":
                        Toast.makeText(getApplicationContext(), "Insert cancelled", Toast.LENGTH_SHORT).show();
                        break;
                    case "update":
                        Toast.makeText(getApplicationContext(), "Update cancelled", Toast.LENGTH_SHORT).show();
                        break;
                    case "delete":
                        Toast.makeText(getApplicationContext(), "Delete cancelled", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        AlertDialog alertDialog=albuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null){
            return;
        }
        else{
            switch (requestCode){
                case 100:
                    Uri fileUri=data.getData();
                    myImage mi = new myImage();
                    path = mi.getRealPathFromUri(fileUri, this);
                    try{
                        BitmapFactory.Options options=new BitmapFactory.Options();
                        options.inSampleSize=0;
                        Bitmap mypic=BitmapFactory.decodeFile(path, options);
                        mypic=mi.automatic_rotateImg(mypic,path);
                        imgFoto.setImageBitmap(mypic);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    void cleanComponent(){
        etUmur.setText("");
        etNama.setText("");
        etNim.setText("");
        imgFoto.setImageResource(R.mipmap.ic_launcher_round);

    }
}