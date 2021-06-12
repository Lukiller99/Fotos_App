package com.example.aplicacionbdd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button btnCamara,btnEnviar,btnBuscar;
    TextView txtTitulo,txtDesc,txtRuta,txtId;
    ImageView imgView;
    String rutaImagen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCamara = findViewById(R.id.btnCamara);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnBuscar = findViewById(R.id.btnBuscar);

        txtTitulo = findViewById(R.id.txtTitulo);
        txtDesc = findViewById(R.id.txtDesc);
        txtRuta=findViewById(R.id.txtRuta);
        txtId=findViewById(R.id.txtId);

        imgView = findViewById(R.id.viewFoto);

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirCamara();
                txtId.setText("");
                txtDesc.setText("");
                txtTitulo.setText("");
            }

        });
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Registrar(view);
            }
        });
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Buscar(view);
            }
        });
    }

    private void abrirCamara(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File imagenArchivo=null;
        try {
            imagenArchivo=crearImagen();
        }catch(IOException ex){
            Log.e("Error", ex.toString());
        }
        if(imagenArchivo!=null){
            Uri fotoUri= FileProvider.getUriForFile(this,"com.cdp.camara.fileprovider",imagenArchivo);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,fotoUri);
            startActivityForResult(intent, 1);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap imgBitmap = BitmapFactory.decodeFile(rutaImagen);
            imgView.setImageBitmap(imgBitmap);
        }
    }

    private File crearImagen() throws IOException {
        String nombreImagen = "foto_";
        File directorio=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = File.createTempFile(nombreImagen, ".jpg", directorio);
        rutaImagen= imagen.getAbsolutePath();
        txtRuta.setText(rutaImagen);
        return imagen;
    }

    private void Registrar (View view){
        MySql admin = new MySql(this,  "administracion" , null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String id=txtId.getText().toString();
        String titulo = txtTitulo.getText().toString();
        String ruta= txtRuta.getText().toString();
        String descripcion = txtDesc.getText().toString();



        if(!id.isEmpty()&&!titulo.isEmpty()&&!descripcion.isEmpty()&&!ruta.isEmpty()){
            ContentValues registro = new ContentValues();

            registro.put("id",id);
            registro.put("titulo",titulo);
            registro.put("ruta",ruta);
            registro.put("descripcion",descripcion);

            BaseDeDatos.insert("foto",null,registro);


            txtId.setText("");
            txtDesc.setText("");
            txtTitulo.setText("");
            txtRuta.setText("");


            Toast.makeText(this,"Registro exitoso",Toast.LENGTH_SHORT).show();
            BaseDeDatos.close();
        }else{
            Toast.makeText(this,"Debes llenar todos los campos",Toast.LENGTH_SHORT).show();
        }
    }


    public void Buscar (View view){
        MySql admin = new MySql(this,  "administracion" , null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String id = txtId.getText().toString();


        if(!id.isEmpty()){
            Cursor fila = BaseDeDatos.rawQuery("select * from foto where id = "+ id, null);
            if(fila.moveToFirst()){
                txtTitulo.setText(fila.getString(1));
                txtRuta.setText(fila.getString(2));
                txtDesc.setText(fila.getString(3));

                //BaseDeDatos.close();
            }else{
                Toast.makeText(this,"No existe el artículo",Toast.LENGTH_SHORT).show();
                //BaseDeDatos.close();
            }
        }else{
            Toast.makeText(this,"Debes introducir el Id de la foto",Toast.LENGTH_SHORT).show();
        }
    }

}