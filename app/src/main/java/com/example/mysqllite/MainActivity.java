package com.example.mysqllite;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button btnAddData;
    ArrayList<Mahasiswa> mhs = new ArrayList<Mahasiswa>();
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    GridView gridViewData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddData = (Button) findViewById(R.id.buttonAddData);
        gridViewData=(GridView) findViewById(R.id.gridViewDataAll);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
        }

        if(databaseHelper.getCountData()>0){
            mhs=databaseHelper.TransferDataArrayList();
            if(mhs.size()>0){
                setAdapterGridView();
            }
        }

        btnAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("Mode", "Insert");
                startActivity(intent);
            }
        });

        gridViewData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(MainActivity.this, MainActivity2.class);
                Mahasiswa mhsData=new Mahasiswa();
                mhsData.setNim( ((TextView)view.findViewById(R.id.textViewNim)).getText().toString() );
                mhsData.setNama( ((TextView)view.findViewById(R.id.textViewNama)).getText().toString() );
                mhsData.setPath( ((TextView)view.findViewById(R.id.textViewPath)).getText().toString() );
                mhsData.setUmur( Integer.parseInt(((TextView)view.findViewById(R.id.textViewUmur)).getText().toString()) );

                intent.putExtra("Mode", "UpdateDelete");
                intent.putExtra("Nim", mhsData.getNim());
                intent.putExtra("Nama", mhsData.getNama());
                intent.putExtra("Path", mhsData.getPath());
                intent.putExtra("Umur", mhsData.getUmur());

                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1001:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "Permission Granted read external storage", Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(getApplicationContext(), "Permission Denied read external storage", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    void setAdapterGridView(){
        ArrayAdapter<Mahasiswa> adapter= new myListAdapter();
        gridViewData.setAdapter(adapter);
    }

    class myListAdapter extends ArrayAdapter<Mahasiswa>{
        public myListAdapter(){
            super(MainActivity.this, R.layout.ls_item, mhs);

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView=convertView;
            if(itemView==null){
                itemView = getLayoutInflater().inflate(R.layout.ls_item, parent, false);

            }
            Mahasiswa currentMhs=mhs.get(position);

            ImageView imgfoto=(ImageView) itemView.findViewById(R.id.imageViewData);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize=16;
            Bitmap img = BitmapFactory.decodeFile(currentMhs.getPath(), options);

            myImage mi = new myImage();
            img= mi.automatic_rotateImg(img, currentMhs.getPath());

            TextView txNim= (TextView) itemView.findViewById(R.id.textViewNim);
            txNim.setText(currentMhs.getNim());
            TextView txNama=(TextView) itemView.findViewById(R.id.textViewNama);
            txNama.setText(currentMhs.getNama());
            TextView txUmur= (TextView) itemView.findViewById(R.id.textViewUmur);
            txUmur.setText(String.valueOf(currentMhs.getNim()));

            return itemView;
        }
    }
}