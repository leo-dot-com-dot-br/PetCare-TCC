package com.example.apptcc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;
import android.os.Build;
import android.view.Window;
import android.util.Log;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.ContextCompat;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.apptcc.databinding.ActivityFormInicioMedVetBinding;

public class FormInicioMedVet extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityFormInicioMedVetBinding binding;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String id_nfc;
    private int id_pet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormInicioMedVetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue2));
        }

        setSupportActionBar(binding.appBarFormInicioMedVet.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_conta, R.id.nav_sobre, R.id.nav_sair)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_form_inicio_med_vet);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_conta) {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
                String crmv = sharedPreferences.getString("crmv", null);

                if (crmv != null) {
                    Intent contaIntent = new Intent(FormInicioMedVet.this, FormContaMedVet.class);
                    contaIntent.putExtra("crmv", crmv);
                    startActivity(contaIntent);
                } else {
                    Toast.makeText(this, "Erro ao identificar o médico veterinário logado.", Toast.LENGTH_SHORT).show();
                }
            }
            else if (id == R.id.nav_sobre) {
                Intent sobreIntent = new Intent(FormInicioMedVet.this, FormSobreTutor.class);
                startActivity(sobreIntent);
            } else if (id == R.id.nav_sair) {
                SharedPreferences.Editor editor = getSharedPreferences("AppPreferences", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                Intent loginIntent = new Intent(FormInicioMedVet.this, FormLogin.class);
                startActivity(loginIntent);
                finish();
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "Este dispositivo não suporta NFC.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "Ative o NFC nas configurações.", Toast.LENGTH_SHORT).show();
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        intentFiltersArray = new IntentFilter[]{tagDetected};

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.appBarFormInicioMedVet.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                id_nfc = bytesToHex(tag.getId());
                Toast.makeText(this, "NFC capturado", Toast.LENGTH_LONG).show();
                buscarDadosDoPet(id_nfc);
            } else {
                Toast.makeText(this, "Nenhuma tag NFC detectada.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void buscarDadosDoPet(String id_nfc) {
        DatabaseHelper db = new DatabaseHelper(this);
        Pet pet = db.getPetByNfcId(id_nfc);
        int id_pet = pet.getId_pet();

        Log.d("FormInicioTutor", "ID do Pet encontrado: " + id_pet);
        if (pet != null) {
            Intent intent = new Intent(this, FormNFCLidoMed.class);
            intent.putExtra("id_pet", id_pet);
            intent.putExtra("idNfc", id_nfc);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Nenhum pet encontrado com esse ID NFC.", Toast.LENGTH_LONG).show();
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_form_inicio_med_vet);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
