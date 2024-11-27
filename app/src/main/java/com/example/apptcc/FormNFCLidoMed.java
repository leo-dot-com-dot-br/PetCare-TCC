package com.example.apptcc;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import androidx.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormNFCLidoMed extends AppCompatActivity {

    private EditText edtRacaPet, edtGeneroPet, edtDataNascPet, edtObservacaoPet;
    private TextView txtNomePet ;
    private RecyclerView rvVacinas;
    private Button btnAddVacina, btnFinalizar;
    private DatabaseHelper dbHelper;
    private String idNfc;
    private int petId;
    private Pet pet;
    private VacinaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_nfclido_med);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue2));
        }
        txtNomePet = findViewById(R.id.txtNomePet);
        edtRacaPet = findViewById(R.id.edtRacaPet);
        edtGeneroPet = findViewById(R.id.edtGeneroPet);
        edtDataNascPet = findViewById(R.id.edtDataNascPet);
        rvVacinas = findViewById(R.id.rvVacinas);
        edtObservacaoPet = findViewById(R.id.edtObservacaoPet);
        btnAddVacina = findViewById(R.id.btnAddVacina);
        btnFinalizar = findViewById(R.id.btnFinalizar);

        dbHelper = new DatabaseHelper(this);
        idNfc = getIntent().getStringExtra("idNfc");

        rvVacinas.setLayoutManager(new LinearLayoutManager(this));

        if (idNfc != null && !idNfc.isEmpty()) {
            carregarDadosPet(idNfc);
        } else {
            Toast.makeText(this, "Erro ao carregar o ID NFC do pet.", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnAddVacina.setOnClickListener(v -> {
            Intent intent = new Intent(FormNFCLidoMed.this, VacinasCadastradas.class);
            startActivityForResult(intent, 2);
        });
        btnFinalizar.setOnClickListener(v -> finalizarAtendimento());
    }

    private void carregarDadosPet(String idNfc) {
        Pet pet = dbHelper.getPetByNfcId(idNfc);
        if (pet != null) {
            txtNomePet.setText(pet.getNome_pet());
            edtRacaPet.setText(pet.getRaca_pet());
            edtGeneroPet.setText(pet.getGenero_pet());
            edtDataNascPet.setText(pet.getData_nasc_pet());
            edtObservacaoPet.setText(pet.getObservacao_pet());
            petId = pet.getId_pet();
            carregarVacinas(petId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            int vacinaId = data.getIntExtra("vacinaId", -1);
            if (vacinaId != -1) {
                String dataAplicacao = getDataAtual();
                String crmv = "CRMV do Veterinário";

                boolean inserido = dbHelper.inserirVacinaParaPet(petId, vacinaId, dataAplicacao, crmv);
                if (inserido) {
                    carregarVacinas(petId);
                    Toast.makeText(this, "Vacina adicionada com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Erro ao adicionar a vacina.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void carregarVacinas(int petId) {
        List<Vacina> vacinaList = dbHelper.getVacinasDoPet(petId);

        if (adapter == null) {
            adapter = new VacinaAdapter(vacinaList, true, false, vacina -> {
            });
            rvVacinas.setAdapter(adapter);
        } else {
            adapter.updateVacinas(vacinaList);
        }
    }

    private void salvarDadosPet() {
        String raca = edtRacaPet.getText().toString();
        String genero = edtGeneroPet.getText().toString();
        String dataNasc = edtDataNascPet.getText().toString();
        String observacao = edtObservacaoPet.getText().toString();

        if (petId != -1) {
            boolean atualizado = dbHelper.atualizarDadosPet(petId, raca, genero, dataNasc, observacao);
            if (atualizado) {
                Toast.makeText(this, "Dados do pet salvos com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erro ao salvar os dados do pet.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Erro ao salvar os dados: ID do pet não encontrado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void finalizarAtendimento() {
        salvarDadosPet();
        Toast.makeText(this, "Atendimento finalizado com sucesso!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private String getDataAtual() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (pet != null) {
            List<Vacina> vacinaList = dbHelper.carregarVacinasAplicadas(pet.getId_pet());
            if (adapter != null) {
                adapter.updateVacinas(vacinaList);
            }
        }
    }
}
