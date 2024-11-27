package com.example.apptcc;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.view.Window;
import android.widget.TextView;
import java.util.ArrayList;

public class FormDetalhesPet extends AppCompatActivity {

    private RecyclerView rvVacinas;
    private VacinaAdapter vacinaAdapter;
    private List<Vacina> vacinaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_detalhes_pet);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue2));
        }

        TextView txtNomePet = findViewById(R.id.txtNomePet);
        TextView txtRacaPet = findViewById(R.id.txtRacaPet);
        TextView txtGeneroPet = findViewById(R.id.txtGeneroPet);
        TextView txtDataNascPet = findViewById(R.id.txtDataNascPet);

        rvVacinas = findViewById(R.id.rvVacinas);
        rvVacinas.setLayoutManager(new LinearLayoutManager(this));
        vacinaList = new ArrayList<>();
        vacinaAdapter = new VacinaAdapter(vacinaList, false, false, null);
        rvVacinas.setAdapter(vacinaAdapter);

        TextView txtObservacaoPet = findViewById(R.id.txtObservacaoPet);

        Intent intent = getIntent();
        int petId = intent.getIntExtra("id_pet", -1);
        String nome = intent.getStringExtra("nome_pet");
        String raca = intent.getStringExtra("raca_pet");
        String genero = intent.getStringExtra("genero_pet");
        String dataNascimento = intent.getStringExtra("data_nasc_pet");
        String observacoes = intent.getStringExtra("observacao_pet");

        txtNomePet.setText(nome);
        txtRacaPet.setText(raca);
        txtGeneroPet.setText(genero);
        txtDataNascPet.setText(dataNascimento);
        txtObservacaoPet.setText(observacoes);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        vacinaList.addAll(dbHelper.getVacinasDoPet(petId));
        vacinaAdapter.notifyDataSetChanged();
    }
}
