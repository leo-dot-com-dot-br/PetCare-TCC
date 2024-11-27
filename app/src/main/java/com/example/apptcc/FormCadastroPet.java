package com.example.apptcc;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.database.Cursor;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.SharedPreferences;
import java.util.Calendar;
import java.util.Locale;
import android.text.TextWatcher;
import android.text.Editable;

public class FormCadastroPet extends AppCompatActivity {

    private EditText edt_nomep, edt_racap, edt_sxp, edt_dtnasc;
    private Button btn_cadpet;
    private DatabaseHelper dbHelper;
    private int id_tutor;
    private NfcAdapter nfcAdapter;
    private String id_nfc;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro_pet);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue2));
        }

        dbHelper = new DatabaseHelper(this);
        IniciarComponentes();

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

        btn_cadpet.setOnClickListener(v -> {
            if (edt_nomep.getText().toString().isEmpty() || edt_racap.getText().toString().isEmpty() ||
                    edt_sxp.getText().toString().isEmpty() || edt_dtnasc.getText().toString().isEmpty()) {
                Toast.makeText(FormCadastroPet.this, "Todos os campos devem ser preenchidos.", Toast.LENGTH_LONG).show();
                return;
            }

            if (!edt_sxp.getText().toString().equalsIgnoreCase("M") && !edt_sxp.getText().toString().equalsIgnoreCase("F")) {
                Toast.makeText(FormCadastroPet.this, "O campo sexo deve conter 'M' ou 'F'.", Toast.LENGTH_LONG).show();
                return;
            }

            if (id_nfc == null) {
                Toast.makeText(FormCadastroPet.this, "Por favor, aproxime o chip NFC para vinculação.", Toast.LENGTH_LONG).show();
                return;
            }

            int idTutor = getIdTutor();
            Log.d("CadastroPet", "ID do tutor recuperado: " + idTutor);

            if (idTutor == -1) {
                Toast.makeText(FormCadastroPet.this, "Erro ao obter o ID do tutor. Faça login novamente.", Toast.LENGTH_LONG).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT id_pet FROM pet WHERE id_nfc = ?", new String[]{id_nfc});
            if (cursor.getCount() > 0) {
                Toast.makeText(FormCadastroPet.this, "Este ID NFC já está vinculado a outro pet.", Toast.LENGTH_LONG).show();
                cursor.close();
                db.close();
                return;
            }
            cursor.close();
            db.close();

            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("nome_pet", edt_nomep.getText().toString());
            values.put("raca_pet", edt_racap.getText().toString());
            values.put("genero_pet", edt_sxp.getText().toString());
            values.put("data_nasc_pet", edt_dtnasc.getText().toString());
            values.put("id_nfc", id_nfc);
            values.put("id_tutor", id_tutor);

            long newRowId = db.insert("pet", null, values);
            if (newRowId != -1) {
                Toast.makeText(FormCadastroPet.this, "Pet cadastrado com sucesso!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(FormCadastroPet.this, FormInicioTutor.class);
                startActivity(intent);
            } else {
                Toast.makeText(FormCadastroPet.this, "Erro ao cadastrar pet! Tente novamente.", Toast.LENGTH_LONG).show();
            }
            db.close();
        });
    }

    private int getIdTutor() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        return sharedPreferences.getInt("id_tutor", -1);
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
                Log.d("FormCadastroPet", "ID lido: " + id_nfc);
            } else {
                Toast.makeText(this, "Nenhuma tag NFC detectada.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private void IniciarComponentes() {
        edt_nomep = findViewById(R.id.edt_nomep);
        edt_racap = findViewById(R.id.edt_racap);
        edt_sxp = findViewById(R.id.edt_sxp);
        edt_dtnasc = findViewById(R.id.edt_dtnasc);
        btn_cadpet = findViewById(R.id.btn_cadpet);

        edt_dtnasc.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private final String ddmmyyyy = "ddmmyyyy";
            private final Calendar cal = Calendar.getInstance();

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d]", "");
                    int sel = clean.length();
                    int index = 2;

                    while (index < clean.length() && index < 6) {
                        sel++;
                        index += 2;
                    }

                    if (clean.length() == 8) {
                        int month = Integer.parseInt(clean.substring(0, 2));
                        int day = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        month = (month < 1) ? 1 : Math.min(month, 12);
                        cal.set(Calendar.MONTH, month - 1);
                        year = (year < 1900) ? 1900 : Math.min(year, 2100);
                        cal.set(Calendar.YEAR, year);
                        day = Math.min(day, cal.getActualMaximum(Calendar.DATE));
                        clean = String.format(Locale.US, "%02d%02d%04d", month, day, year);

                        clean = String.format(Locale.US, "%s/%s/%s", clean.substring(0, 2),
                                clean.substring(2, 4),
                                clean.substring(4, 8));
                    }

                    sel = Math.max(sel, 0);
                    current = clean;
                    edt_dtnasc.setText(current);
                    edt_dtnasc.setSelection(Math.min(sel, current.length()));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edt_sxp.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    String input = s.toString().toUpperCase();
                    if (!input.equals("M") && !input.equals("F")) {
                        edt_sxp.setError("Digite 'M' para Masculino ou 'F' para Feminino");
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
