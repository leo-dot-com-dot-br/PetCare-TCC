package com.example.apptcc;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.google.android.material.navigation.NavigationView;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.apptcc.databinding.ActivityFormInicioTutorBinding;
import androidx.appcompat.widget.SearchView;
import android.content.SharedPreferences;
import android.widget.Toast;
import android.util.Log;

public class FormInicioTutor extends AppCompatActivity {

    private RecyclerView recyclerViewPets;
    private PetAdapter petAdapter;
    private DatabaseHelper databaseHelper;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityFormInicioTutorBinding binding;
    private int id_tutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormInicioTutorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue2));
        }

        recyclerViewPets = findViewById(R.id.recyclerViewPets);
        recyclerViewPets.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);

        List<Pet> petsDoTutor = databaseHelper.getPetsDoTutor(id_tutor);
        petAdapter = new PetAdapter(petsDoTutor);
        recyclerViewPets.setAdapter(petAdapter);

        setSupportActionBar(binding.appBarFormInicioTutor.toolbar);
        binding.appBarFormInicioTutor.icAdc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FormInicioTutor.this, FormCadastroPet.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_form_inicio_tutor);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        SearchView searchView = findViewById(R.id.searchViewPets);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterPets(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPets(newText);
                return true;
            }
            private void filterPets(String query) {
                List<Pet> filteredPets = databaseHelper.searchPets(query);
                petAdapter.updatePetList(filteredPets);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_form_inicio_tutor);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarPets();
    }

    private void carregarPets() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        int idTutor = sharedPreferences.getInt("id_tutor", -1);

        Log.d("FormInicioTutor", "ID do tutor logado: " + idTutor);

        if (idTutor == -1) {
            Toast.makeText(this, "Erro ao identificar tutor logado.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Pet> petsDoTutor = databaseHelper.getPetsDoTutor(idTutor);

        if (petsDoTutor.isEmpty()) {
            Toast.makeText(this, "Nenhum pet cadastrado para este tutor.", Toast.LENGTH_SHORT).show();
        } else {
            petAdapter.updatePetList(petsDoTutor);
        }
    }
}