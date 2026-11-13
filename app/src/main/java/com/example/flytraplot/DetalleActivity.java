package com.example.flytraplot;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DetalleActivity extends AppCompatActivity {
    public TextView txt_codigo, txt_trampa, txt_captura, txt_observaciones;
    public Button boton_editar, boton_eliminar;
    public int id_actual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.detalle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txt_codigo = findViewById(R.id.txt_codigo);
        txt_trampa = findViewById(R.id.txt_trampa);
        txt_captura = findViewById(R.id.txt_captura);
        txt_observaciones = findViewById(R.id.txt_observaciones);

        boton_editar = findViewById(R.id.boton_editar);
        boton_eliminar = findViewById(R.id.boton_eliminar);

        id_actual = getIntent().getIntExtra("id_actual", -1);

        cargarContacto(id_actual);

        boton_eliminar.setOnClickListener(v -> eliminarContacto(id_actual));

        boton_editar.setOnClickListener(v -> {
            Intent editar_intent = new Intent(DetalleActivity.this, EditarActivity.class);
            editar_intent.putExtra("id_actual", id_actual);
            startActivity(editar_intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarContacto(id_actual); // Recargar los datos al volver de editar
    }

    public void cargarContacto(int id) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor consulta = db.rawQuery("SELECT codigo_sector, tipo_trampa, nivel_captura, observaciones FROM contactos WHERE id = ?", new String[]{String.valueOf(id)});

        if (consulta.moveToFirst()) {
            txt_codigo.setText("Código: " + consulta.getString(0));
            txt_trampa.setText("Trampa: " + consulta.getString(1));
            txt_captura.setText("Captura: " + consulta.getString(2));
            txt_observaciones.setText("Observaciones: " + consulta.getString(3));
        }

        consulta.close(); // Siempre cerrar el cursor antes de la base de datos
        db.close();
    }

    public void eliminarContacto(int id) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase db = admin.getWritableDatabase();

        db.delete("contactos", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        finish();
    }
}