package com.example.apptcc;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FormContaMedVet extends AppCompatActivity {

    private EditText edt_crmv, edt_nome, edt_senha;
    private TextView tv_excluir_conta;
    private Button btn_salvar;
    private DatabaseHelper databaseHelper;
    private String crmv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_conta_med_vet);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edt_crmv = findViewById(R.id.edt_crmv);
        edt_nome = findViewById(R.id.edt_nome);
        edt_senha = findViewById(R.id.edt_senha);
        btn_salvar = findViewById(R.id.btn_salvar);
        tv_excluir_conta = findViewById(R.id.tv_excluir_conta);

        databaseHelper = new DatabaseHelper(this);

        crmv = getSharedPreferences("AppPreferences", MODE_PRIVATE).getString("crmv", null);

        if (crmv != null) {
            carregarDadosMedVet(crmv);
        } else {
            Toast.makeText(this, "Erro ao carregar dados do Médico Veterinário.", Toast.LENGTH_SHORT).show();
            finish();
        }

        btn_salvar.setOnClickListener(v -> salvarAlteracoes());
        tv_excluir_conta.setOnClickListener(v -> excluirConta());
    }

    private void excluirConta() {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Conta")
                .setMessage("Tem certeza de que deseja excluir sua conta? Esta ação não pode ser desfeita.")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    boolean excluido = databaseHelper.deleteMedVetByCrmv(crmv);
                    if (excluido) {
                        Toast.makeText(this, "Conta excluída com sucesso.", Toast.LENGTH_SHORT).show();
                        getSharedPreferences("AppPreferences", MODE_PRIVATE).edit().remove("crmv").apply();
                        Intent intent = new Intent(FormContaMedVet.this, FormLogin.class);
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
    private void carregarDadosMedVet(String crmv) {
        Cursor cursor = databaseHelper.getMedVetByCrmv(crmv);
        if (cursor != null && cursor.moveToFirst()) {
            edt_crmv.setText(cursor.getString(cursor.getColumnIndexOrThrow("crmv")));
            edt_nome.setText(cursor.getString(cursor.getColumnIndexOrThrow("nome_mdv")));
            edt_senha.setText(cursor.getString(cursor.getColumnIndexOrThrow("senha_mdv")));
            cursor.close();
        } else {
            Toast.makeText(this, "Não foi possível carregar os dados.", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarAlteracoes() {
        String nome = edt_nome.getText().toString().trim();
        String senha = edt_senha.getText().toString().trim();

        if (nome.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean atualizado = databaseHelper.updateMedVet(crmv, nome, senha);

        if (atualizado) {
            Toast.makeText(this, "Dados atualizados com sucesso.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Erro ao atualizar os dados.", Toast.LENGTH_SHORT).show();
        }
    }
}
