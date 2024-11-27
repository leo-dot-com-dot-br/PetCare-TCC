package com.example.apptcc;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class FormCadastroTutor extends AppCompatActivity {

    private EditText edt_nomet, edt_senhat, edt_emailt, edt_telt, edt_endt, edt_cidadet, edt_uft;
    private Button btn_cadtutor;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_cadastro_tutor);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        IniciarComponentes();

        btn_cadtutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarTutor();
            }
        });
    }

    private void IniciarComponentes() {
        edt_nomet = findViewById(R.id.edt_nomet);
        edt_senhat = findViewById(R.id.edt_senhat);
        edt_emailt = findViewById(R.id.edt_emailt);
        edt_telt = findViewById(R.id.edt_telt);
        edt_endt = findViewById(R.id.edt_endt);
        edt_cidadet = findViewById(R.id.edt_cidadet);
        edt_uft = findViewById(R.id.edt_uft);
        btn_cadtutor = findViewById(R.id.btn_cadtutor);
    }

    private void cadastrarTutor() {
        String nome = edt_nomet.getText().toString().trim();
        String senha = edt_senhat.getText().toString().trim();
        String email = edt_emailt.getText().toString().trim();
        String telefone = edt_telt.getText().toString().trim();
        String endereco = edt_endt.getText().toString().trim();
        String cidade = edt_cidadet.getText().toString().trim();
        String uf = edt_uft.getText().toString().trim();

        if (nome.isEmpty() || senha.isEmpty() || email.isEmpty() || telefone.isEmpty() || endereco.isEmpty() || cidade.isEmpty() || uf.isEmpty()) {
            Snackbar.make(btn_cadtutor, "Preencha todos os campos!", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(btn_cadtutor, "Insira um email válido!", Snackbar.LENGTH_LONG).show();
            return;
        }

        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("nome_tutor", nome);
            values.put("senha_tutor", senha);
            values.put("email_tutor", email);
            values.put("telefone_tutor", telefone);
            values.put("endereco_tutor", endereco);
            values.put("cidade_tutor", cidade);
            values.put("uf_tutor", uf);

            long newRowId = db.insert("tutor", null, values);

            if (newRowId != -1) {
                Toast.makeText(this, "Tutor cadastrado com sucesso!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(FormCadastroTutor.this, FormLogin.class));
            } else {
                Snackbar.make(btn_cadtutor, "Erro ao cadastrar tutor. Tente novamente.", Snackbar.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao acessar o banco de dados: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}
