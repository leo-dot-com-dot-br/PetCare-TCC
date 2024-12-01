package com.example.apptcc;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;

public class FormContaTutor extends AppCompatActivity {

    private EditText edt_nomet, edt_senhat, edt_emailt, edt_telt, edt_endt, edt_cidadet, edt_uft;
    private Button btn_cadtutor;
    private TextView tv_excluir_conta;
    private DatabaseHelper databaseHelper;
    private int id_tutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_conta_tutor);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edt_nomet = findViewById(R.id.edt_nomet);
        edt_senhat = findViewById(R.id.edt_senhat);
        edt_emailt = findViewById(R.id.edt_emailt);
        edt_telt = findViewById(R.id.edt_telt);
        edt_endt = findViewById(R.id.edt_endt);
        edt_cidadet = findViewById(R.id.edt_cidadet);
        edt_uft = findViewById(R.id.edt_uft);
        btn_cadtutor = findViewById(R.id.btn_cadtutor);
        tv_excluir_conta = findViewById(R.id.tv_excluir_conta);

        databaseHelper = new DatabaseHelper(this);

        id_tutor = getIntent().getIntExtra("id_tutor", -1);

        if (id_tutor != -1) {
            carregarDadosTutor(id_tutor);
        } else {
            Toast.makeText(this, "Erro ao carregar dados do tutor.", Toast.LENGTH_SHORT).show();
        }

        btn_cadtutor.setOnClickListener(v -> salvarAlteracoes());
        tv_excluir_conta.setOnClickListener(v -> excluirConta());
    }

    private void excluirConta() {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Conta")
                .setMessage("Tem certeza de que deseja excluir sua conta? Esta ação não pode ser desfeita.")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    Log.d("ExcluirConta", "Tentando excluir os pets do tutor ID: " + id_tutor);

                    boolean petsExcluidos = databaseHelper.excluirPetsPorTutor(id_tutor);
                    if (petsExcluidos) {
                        Log.d("ExcluirConta", "Pets excluídos com sucesso ou nenhum pet encontrado.");
                    } else {
                        Log.d("ExcluirConta", "Nenhum pet encontrado para excluir. Continuando exclusão da conta.");
                    }

                    Log.d("ExcluirConta", "Tentando excluir a conta do tutor.");

                    boolean contaExcluida = databaseHelper.deleteTutorById(id_tutor);
                    if (contaExcluida) {
                        Toast.makeText(this, "Conta excluída com sucesso.", Toast.LENGTH_SHORT).show();
                        getSharedPreferences("AppPreferences", MODE_PRIVATE).edit().remove("id_tutor").apply();
                        Intent intent = new Intent(FormContaTutor.this, FormLogin.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Erro ao excluir a conta.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void carregarDadosTutor(int id_tutor) {
        Cursor cursor = databaseHelper.getTutorById(id_tutor);
        if (cursor != null && cursor.moveToFirst()) {
            edt_nomet.setText(cursor.getString(cursor.getColumnIndexOrThrow("nome_tutor")));
            edt_senhat.setText(cursor.getString(cursor.getColumnIndexOrThrow("senha_tutor")));
            edt_emailt.setText(cursor.getString(cursor.getColumnIndexOrThrow("email_tutor")));
            edt_telt.setText(cursor.getString(cursor.getColumnIndexOrThrow("telefone_tutor")));
            edt_endt.setText(cursor.getString(cursor.getColumnIndexOrThrow("endereco_tutor")));
            edt_cidadet.setText(cursor.getString(cursor.getColumnIndexOrThrow("cidade_tutor")));
            edt_uft.setText(cursor.getString(cursor.getColumnIndexOrThrow("uf_tutor")));
            cursor.close();
        } else {
            Toast.makeText(this, "Não foi possível carregar os dados.", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarAlteracoes() {
        String nome_tutor = edt_nomet.getText().toString().trim();
        String senha_tutor = edt_senhat.getText().toString().trim();
        String email_tutor = edt_emailt.getText().toString().trim();
        String telefone_tutor = edt_telt.getText().toString().trim();
        String endereco_tutor = edt_endt.getText().toString().trim();
        String cidade_tutor = edt_cidadet.getText().toString().trim();
        String uf_tutor = edt_uft.getText().toString().trim();

        if (nome_tutor.isEmpty() || senha_tutor.isEmpty() || email_tutor.isEmpty() || telefone_tutor.isEmpty() || endereco_tutor.isEmpty() || cidade_tutor.isEmpty() || uf_tutor.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean atualizado = databaseHelper.updateTutor(
                id_tutor,
                nome_tutor,
                senha_tutor,
                email_tutor,
                telefone_tutor,
                endereco_tutor,
                cidade_tutor,
                uf_tutor
        );

        if (atualizado) {
            Toast.makeText(this, "Dados atualizados com sucesso.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Erro ao atualizar os dados.", Toast.LENGTH_SHORT).show();
        }
    }
}
