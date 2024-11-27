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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FormLogin extends AppCompatActivity {

    private EditText editId, editSenha;
    private Spinner spinner;
    private TextView txtMedVet, txtTutor;
    private Button btEntrar;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue2));
        }

        initComponents();

        configureNfc();

        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);

        configureSpinner();

        btEntrar.setOnClickListener(view -> handleLogin());
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
                abrirWhatsApp(tutor.getTelefone_tutor());
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

    private void abrirWhatsApp(String telefoneTutor) {
        try {
            String url = "https://api.whatsapp.com/send?phone=" + telefoneTutor;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao abrir WhatsApp", Toast.LENGTH_SHORT).show();
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}