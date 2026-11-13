package com.example.flytraplot;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditarActivity extends AppCompatActivity {
    EditText input_codigo, input_trampa, input_captura, input_observaciones;
    Button boton_enviar;
    public int id_actual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.editar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        input_codigo = findViewById(R.id.input_codigo2);
        input_trampa = findViewById(R.id.input_trampa2);
        input_captura = findViewById(R.id.input_captura2);
        input_observaciones = findViewById(R.id.input_observaciones2);
        boton_enviar = findViewById(R.id.boton_enviar2);

        id_actual = getIntent().getIntExtra("id_actual", -1);

        cargarContacto(id_actual);

        boton_enviar.setOnClickListener(v -> actualizarContacto(id_actual));
    }

    public void cargarContacto(int id) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor consulta = db.rawQuery("SELECT codigo_sector, tipo_trampa, nivel_captura, observaciones FROM contactos WHERE id = ?", new String[]{String.valueOf(id)});

        if (consulta.moveToFirst()) {
            input_codigo.setText(consulta.getString(0));
            input_trampa.setText(consulta.getString(1));
            input_captura.setText(consulta.getString(2));
            input_observaciones.setText(consulta.getString(3));
        }

        consulta.close();
        db.close();
    }

    public void actualizarContacto(int id) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase db = admin.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("codigo_sector", input_codigo.getText().toString());
        valores.put("tipo_trampa", input_trampa.getText().toString());
        valores.put("nivel_captura", input_captura.getText().toString());
        valores.put("observaciones", input_observaciones.getText().toString());

        db.update("contactos", valores, "id = ?", new String[]{String.valueOf(id)});

        db.close();
        finish();
    }
}