package com.example.apptcc;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.nfc.NfcAdapter;
import android.net.Uri;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

public class FormLogin extends AppCompatActivity {

    private EditText editId, editSenha;
    private Spinner spinner;
    private TextView txtMedVet, txtTutor;
    private Button btEntrar;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean isTriggeredByNfc = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue2));
        }

        initComponents();

        configureNfc();

        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);

        configureSpinner();

        btEntrar.setOnClickListener(view -> handleLogin());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initComponents() {
        dbHelper = new DatabaseHelper(this);

        editId = findViewById(R.id.edit_id);
        editSenha = findViewById(R.id.edit_senha);
        spinner = findViewById(R.id.spinner);
        btEntrar = findViewById(R.id.bt_entrar);
        txtMedVet = findViewById(R.id.txt_medvet);
        txtTutor = findViewById(R.id.txt_tutor);

        txtMedVet.setOnClickListener(view -> startActivity(new Intent(FormLogin.this, FormCadastroMed.class)));
        txtTutor.setOnClickListener(view -> startActivity(new Intent(FormLogin.this, FormCadastroTutor.class)));
    }

    private void configureSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    editId.setHint("DIGITE O CRMV-SP");
                    editId.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    editId.setHint("DIGITE O EMAIL");
                    editId.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                editId.setHint("...");
            }
        });
    }

    private void configureNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "Este dispositivo não suporta NFC.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "Ative o NFC nas configurações.", Toast.LENGTH_SHORT).show();
        }

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{tagDetected}, null);
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
            byte[] tagId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            if (tagId == null || tagId.length == 0) {
                Toast.makeText(this, "Erro ao ler a tag NFC.", Toast.LENGTH_SHORT).show();
                return;
            }

            String idNfc = bytesToHex(tagId).toUpperCase().trim();
            Log.d("FormLogin", "ID NFC Lido: " + idNfc);

            Tutor tutor = dbHelper.getTutorByNfcId(idNfc);
            if (tutor != null) {
                isTriggeredByNfc = true;
                obterLocalizacaoEEnviarWhatsApp(tutor.getTelefone_tutor(), tutor.getNome_tutor());
            } else {
                Toast.makeText(this, "Nenhum tutor vinculado ao pet.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleLogin() {
        String id = editId.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();
        int selectedRole = spinner.getSelectedItemPosition();

        if (id.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedRole == 0) {
            if (validateVetLogin(id, senha)) {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("crmv", id);
                editor.apply();
                startActivity(new Intent(this, FormInicioMedVet.class));
            } else {
                Toast.makeText(this, "Login de Médico Veterinário inválido", Toast.LENGTH_SHORT).show();
            }
        } else if (selectedRole == 1) {
            int idTutor = validateTutorLogin(id, senha);
            if (idTutor != -1) {
                SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("id_tutor", idTutor);
                editor.apply();
                startActivity(new Intent(this, FormInicioTutor.class));
            }
            else {
                Toast.makeText(this, "Login de Tutor inválido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateVetLogin(String crmv, String senhaMdv) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("MDV", new String[]{"crmv"}, "crmv = ? AND senha_mdv = ?",
                new String[]{crmv, senhaMdv}, null, null, null);
        boolean isValid = cursor.moveToFirst();
        cursor.close();
        db.close();
        return isValid;
    }

    private int validateTutorLogin(String emailTutor, String senhaTutor) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Tutor",
                new String[]{"id_tutor"},
                "email_tutor = ? AND senha_tutor = ?",
                new String[]{emailTutor, senhaTutor},
                null, null, null);

        int idTutor = -1;
        if (cursor.moveToFirst()) {
            idTutor = cursor.getInt(cursor.getColumnIndexOrThrow("id_tutor"));
        }
        cursor.close();
        db.close();
        return idTutor;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private void obterLocalizacaoEEnviarWhatsApp(String telefoneTutor, String nomeTutor) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            isTriggeredByNfc = true; // Indique que a próxima permissão é devido à NFC
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null && isTriggeredByNfc) {
                        isTriggeredByNfc = false;
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        abrirWhatsAppComLocalizacao(telefoneTutor, nomeTutor, latitude, longitude);
                    } else {
                        Toast.makeText(this, "Não foi possível obter a localização", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void abrirWhatsAppComLocalizacao(String telefoneTutor, String nomeTutor, double latitude, double longitude) {
        try {
            String mensagem = "Olá, " + nomeTutor + ", você perdeu seu pet? " +
                    "Aqui está a localização do pet: https://www.google.com/maps?q=" + latitude + "," + longitude;
            String url = "https://api.whatsapp.com/send?phone=" + telefoneTutor + "&text=" + Uri.encode(mensagem);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao abrir WhatsApp", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isTriggeredByNfc) {
                    isTriggeredByNfc = false;
                    obterLocalizacaoEEnviarWhatsApp("telefoneTutor", "nomeTutor");
                }
            } else {
                Toast.makeText(this, "Permissão de localização negada.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}