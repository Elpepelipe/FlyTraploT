package com.example.flytraplot;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText input_codigo, input_trampa, input_captura, input_observaciones;
    Button boton_enviar;
    ListView lista_contactos;

    List<String> datos_contactos = new ArrayList<>();
    List<Integer> lista_id = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        input_codigo = findViewById(R.id.input_codigo);
        input_trampa = findViewById(R.id.input_trampa);
        input_captura = findViewById(R.id.input_captura);
        input_observaciones = findViewById(R.id.input_observaciones);
        boton_enviar = findViewById(R.id.boton_enviar);
        lista_contactos = findViewById(R.id.lista_contactos);

        cargarContactos();

        boton_enviar.setOnClickListener(v -> crearContacto());

        lista_contactos.setOnItemClickListener((parent, view, posicion, id) -> {
            if (lista_id.isEmpty()) return; // Evita el crash cuando no hay registros

            Integer id_actual = lista_id.get(posicion);
            Intent intent_detalle = new Intent(MainActivity.this, DetalleActivity.class);
            intent_detalle.putExtra("id_actual", id_actual);
            startActivity(intent_detalle);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarContactos(); // Recarga la lista al volver de Detalle o Editar
    }

    public void cargarContactos() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase db = admin.getReadableDatabase();

        datos_contactos = new ArrayList<>();
        lista_id = new ArrayList<>();

        Cursor fila = db.rawQuery("SELECT id, codigo_sector, tipo_trampa, nivel_captura, observaciones FROM contactos", null);

        if (fila.moveToFirst()) {
            do {
                int id_actual = fila.getInt(0);
                String codigo_actual = fila.getString(1);
                String trampa_actual = fila.getString(2);
                String captura_actual = fila.getString(3);
                String observaciones_actual = fila.getString(4);

                String contacto_actual = "#" + id_actual + " | Código: " + codigo_actual + " | Trampa: " + trampa_actual + " | Captura: " + captura_actual;
                datos_contactos.add(contacto_actual);
                lista_id.add(id_actual);
            } while (fila.moveToNext());
        } else {
            datos_contactos.add("No existen registros en la base de datos");
        }

        fila.close();
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos_contactos);
        lista_contactos.setAdapter(adapter);
    }

    public void crearContacto() {
        String codigo = input_codigo.getText().toString().trim();
        String trampa = input_trampa.getText().toString().trim();
        String captura = input_captura.getText().toString().trim();
        String observaciones = input_observaciones.getText().toString().trim();

        if (!codigo.isEmpty() && !trampa.isEmpty() && !captura.isEmpty()) {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
            SQLiteDatabase db = admin.getWritableDatabase();

            ContentValues nuevo_dato = new ContentValues();
            nuevo_dato.put("codigo_sector", codigo);
            nuevo_dato.put("tipo_trampa", trampa);
            nuevo_dato.put("nivel_captura", captura);
            nuevo_dato.put("observaciones", observaciones);

            db.insert("contactos", null, nuevo_dato);
            db.close();

            input_codigo.setText("");
            input_trampa.setText("");
            input_captura.setText("");
            input_observaciones.setText("");

            cargarContactos();
        } else {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
        }
    }
}