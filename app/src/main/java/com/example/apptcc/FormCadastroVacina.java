package com.example.apptcc;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

public class FormCadastroVacina extends AppCompatActivity {

    private EditText edt_nomev;
    private Button btn_cadvac;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro_vacina);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue2));
        }

        edt_nomev = findViewById(R.id.edt_nomev);
        btn_cadvac = findViewById(R.id.btn_cadvac);

        dbHelper = new DatabaseHelper(this);

        btn_cadvac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome_vacina = edt_nomev.getText().toString();

                if (nome_vacina.isEmpty()) {
                    Toast.makeText(FormCadastroVacina.this, "Por favor, insira o nome da vacina.", Toast.LENGTH_SHORT).show();
                    return;
                }

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("nome_vacina", edt_nomev.getText().toString());

                long newRowId = db.insert("vacina", null, values);

                if (newRowId != -1) {
                    Toast.makeText(FormCadastroVacina.this, "Vacina cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("novaVacinaCadastrada", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(FormCadastroVacina.this, "Erro ao cadastrar a vacina. Verifique se já existe.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}