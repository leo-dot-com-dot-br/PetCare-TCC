package com.example.apptcc;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VacinasCadastradas extends AppCompatActivity {

    private RecyclerView rvVacinas;
    private Button btnAddNovaVacina;
    private DatabaseHelper dbHelper;
    private SearchView searchViewVacina;
    private VacinaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacinas_cadastradas);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue2));
        }

        rvVacinas = findViewById(R.id.rvVacinas);
        btnAddNovaVacina = findViewById(R.id.btnAddNovaVacina);
        searchViewVacina = findViewById(R.id.searchViewVacina);
        dbHelper = new DatabaseHelper(this);

        int idPet = getIntent().getIntExtra("id_pet", -1);

        if (idPet == -1) {
            Toast.makeText(this, "Erro ao carregar ID do pet.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        rvVacinas.setLayoutManager(new LinearLayoutManager(this));

        carregarVacinas();

        searchViewVacina.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterVacinas(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterVacinas(newText);
                return true;
            }
        });

        btnAddNovaVacina.setOnClickListener(v -> {
            Intent intent = new Intent(VacinasCadastradas.this, FormCadastroVacina.class);
            startActivityForResult(intent, 1);
        });
    }

    private void carregarVacinas() {
        List<Vacina> vacinaList = dbHelper.getAllVacinas();
        if (adapter == null) {
            adapter = new VacinaAdapter(vacinaList, true, false, false, true, vacina -> {
                Intent intent = new Intent();
                intent.putExtra("vacinaId", vacina.getId());
                setResult(RESULT_OK, intent);
                finish();
            });
            rvVacinas.setAdapter(adapter);
        } else {
            adapter.updateVacinas(vacinaList);
        }
    }

    private void filterVacinas(String query) {
        if (adapter != null) {
            List<Vacina> filteredVacinas = dbHelper.searchVacina(query);
            adapter.updateVacinas(filteredVacinas);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            boolean novaVacinaCadastrada = data != null && data.getBooleanExtra("novaVacinaCadastrada", false);
            if (novaVacinaCadastrada) {
                carregarVacinas();
            }
        }
    }
}