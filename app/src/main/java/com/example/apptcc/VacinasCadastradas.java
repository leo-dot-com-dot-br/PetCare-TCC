package com.example.apptcc;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VacinasCadastradas extends AppCompatActivity {

    private RecyclerView rvVacinas;
    private Button btnAddNovaVacina;
    private DatabaseHelper dbHelper;

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
        dbHelper = new DatabaseHelper(this);

        rvVacinas.setLayoutManager(new LinearLayoutManager(this));

        carregarVacinas();

        btnAddNovaVacina.setOnClickListener(v -> {
            Intent intent = new Intent(VacinasCadastradas.this, FormCadastroVacina.class);
            startActivityForResult(intent, 1);
        });
    }

    private void carregarVacinas() {
        List<Vacina> vacinaList = dbHelper.getAllVacinas();
        VacinaAdapter adapter = new VacinaAdapter(vacinaList, false, true, vacina -> {
            Intent intent = new Intent();
            intent.putExtra("vacinaId", vacina.getId());
            setResult(RESULT_OK, intent);
            finish();
        });

        rvVacinas.setAdapter(adapter);
        rvVacinas.setLayoutManager(new LinearLayoutManager(this));
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