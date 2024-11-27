package com.example.apptcc;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class FormCadastroMed extends AppCompatActivity {

    private EditText edt_crmv, edt_nome, edt_senha;
    private Button btn_cadmed;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_cadastro_med);
        dbHelper = new DatabaseHelper(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        IniciarComponentes();


        btn_cadmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("crmv", edt_crmv.getText().toString());
                    values.put("nome_mdv", edt_nome.getText().toString());
                    values.put("senha_mdv", edt_senha.getText().toString());
                    long newRowId = db.insert("MDV", null, values);
                    if (newRowId != -1) {
                        Toast.makeText(FormCadastroMed.this, "Médico veterinário cadastrado com sucesso!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(FormCadastroMed.this, FormLogin.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(FormCadastroMed.this, "Erro ao cadastrar médico veterinário!", Toast.LENGTH_LONG).show();
                    }

                    // Fecha a conexão com o banco de dados
                    db.close();
                }
        });

    }

    private void IniciarComponentes(){
        edt_crmv = findViewById(R.id.edt_crmv);
        edt_nome = findViewById(R.id.edt_nome);
        edt_senha = findViewById(R.id.edt_senha);
        btn_cadmed = findViewById(R.id.btn_cadmed);
    }
}