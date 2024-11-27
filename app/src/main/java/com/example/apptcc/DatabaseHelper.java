package com.example.apptcc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bancoTCC.db";
    private static final int DATABASE_VERSION = 3;

    private static final String CREATE_TABLE_MDV = "CREATE TABLE MDV (" +
            "crmv TEXT PRIMARY KEY, " +
            "nome_mdv TEXT NOT NULL, " +
            "senha_mdv TEXT NOT NULL);";

    private static final String CREATE_TABLE_TUTOR = "CREATE TABLE tutor (" +
            "id_tutor INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nome_tutor TEXT NOT NULL, " +
            "senha_tutor TEXT NOT NULL, " +
            "email_tutor TEXT NOT NULL, " +
            "telefone_tutor TEXT NOT NULL, " +
            "endereco_tutor TEXT NOT NULL, " +
            "cidade_tutor TEXT NOT NULL, " +
            "uf_tutor TEXT NOT NULL);";

    private static final String CREATE_TABLE_PET = "CREATE TABLE pet (" +
            "id_pet INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nome_pet TEXT NOT NULL, " +
            "raca_pet TEXT NOT NULL, " +
            "genero_pet TEXT NOT NULL, " +
            "data_nasc_pet TEXT NOT NULL, " +
            "id_nfc TEXT NOT NULL UNIQUE, " +
            "observacao_pet TEXT, " +
            "id_tutor INTEGER, " +
            "FOREIGN KEY (id_tutor) REFERENCES tutor(id_tutor));";

    private static final String CREATE_TABLE_VACINA = "CREATE TABLE vacina (" +
            "id_vacina INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nome_vacina TEXT NOT NULL UNIQUE);";

    private static final String CREATE_TABLE_APLICACAO_VACINA = "CREATE TABLE aplicacao_vacina (" +
            "id_aplicacao INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_pet INTEGER, " +
            "id_vacina INTEGER, " +
            "data_aplicacao TEXT NOT NULL, " +
            "crmv TEXT, " +
            "FOREIGN KEY (id_pet) REFERENCES Pet(id_pet)," +
            "FOREIGN KEY (id_vacina) REFERENCES Vacina(id_vacina)," +
            "FOREIGN KEY (crmv) REFERENCES MDV(crmv));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MDV);
        db.execSQL(CREATE_TABLE_TUTOR);
        db.execSQL(CREATE_TABLE_PET);
        db.execSQL(CREATE_TABLE_VACINA);
        db.execSQL(CREATE_TABLE_APLICACAO_VACINA);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(),
                "Atualizando banco de dados da versão " + oldVersion + " para " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS MDV");
        db.execSQL("DROP TABLE IF EXISTS tutor");
        db.execSQL("DROP TABLE IF EXISTS pet");
        db.execSQL("DROP TABLE IF EXISTS vacina");
        db.execSQL("DROP TABLE IF EXISTS aplicacao_vacina");
        onCreate(db);
    }

    public boolean excluirPet(int petId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("pet", "id_pet = ?", new String[]{String.valueOf(petId)});
        db.close();
        return result > 0;
    }

    public Pet getPetByNfcId(String idNfc) {
        SQLiteDatabase db = this.getReadableDatabase();
        Pet pet = null;

        Cursor cursor = db.query("pet",
                new String[]{"id_pet", "nome_pet", "raca_pet", "genero_pet", "data_nasc_pet", "id_nfc", "observacao_pet"},
                "id_nfc = ?", new String[]{idNfc}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            pet = new Pet(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id_pet")),
                    cursor.getString(cursor.getColumnIndexOrThrow("id_nfc")),
                    cursor.getString(cursor.getColumnIndexOrThrow("nome_pet")),
                    cursor.getString(cursor.getColumnIndexOrThrow("raca_pet")),
                    cursor.getString(cursor.getColumnIndexOrThrow("genero_pet")),
                    cursor.getString(cursor.getColumnIndexOrThrow("data_nasc_pet")),
                    cursor.getString(cursor.getColumnIndexOrThrow("observacao_pet")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("id_tutor"))
            );
            cursor.close();
        }
        db.close();
        return pet;
    }

    public List<Vacina> getVacinasDoPet(int petId) {
        List<Vacina> vacinas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT aplicacao_vacina.id_vacina, vacina.nome_vacina, aplicacao_vacina.data_aplicacao " +
                        "FROM aplicacao_vacina " +
                        "INNER JOIN vacina ON aplicacao_vacina.id_vacina = vacina.id_vacina " +
                        "WHERE aplicacao_vacina.id_pet = ?",
                new String[]{String.valueOf(petId)}
        );
        if (cursor.moveToFirst()) {
            do {
                int idVacina = cursor.getInt(cursor.getColumnIndexOrThrow("id_vacina"));
                String nomeVacina = cursor.getString(cursor.getColumnIndexOrThrow("nome_vacina"));
                String dataAplicacao = cursor.getString(cursor.getColumnIndexOrThrow("data_aplicacao"));

                vacinas.add(new Vacina(idVacina, nomeVacina, dataAplicacao));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return vacinas;
    }

    public List<Vacina> carregarVacinasAplicadas(int petId) {
        List<Vacina> vacinas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT vacina.nome_vacina AS nome, aplicacao_vacina.data_aplicacao AS dataAplicacao, aplicacao_vacina.crmv AS crmv " +
                        "FROM aplicacao_vacina " +
                        "INNER JOIN vacina ON aplicacao_vacina.id_vacina = vacina.id_vacina " +
                        "WHERE aplicacao_vacina.id_pet = ?",
                new String[]{String.valueOf(petId)}
        );

        while (cursor.moveToNext()) {
            String nomeVacina = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
            String dataAplicacao = cursor.getString(cursor.getColumnIndexOrThrow("dataAplicacao"));
            String crmv = cursor.getString(cursor.getColumnIndexOrThrow("crmv"));

            vacinas.add(new Vacina(nomeVacina, dataAplicacao, crmv));
        }

        cursor.close();
        db.close();

        return vacinas;
    }

    public boolean atualizarDadosPet(int petId, String raca, String genero, String dataNasc, String observacao) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("raca_pet", raca);
        values.put("genero_pet", genero);
        values.put("data_nasc_pet", dataNasc);
        values.put("observacao_pet", observacao);

        int rowsAffected = db.update("pet", values, "id_pet = ?", new String[]{String.valueOf(petId)});
        db.close();
        return rowsAffected > 0;
    }

    public boolean inserirVacinaParaPet(int petId, int vacinaId, String dataAplicacao, String crmv) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_pet", petId);
        values.put("id_vacina", vacinaId);
        values.put("data_aplicacao", dataAplicacao);
        values.put("crmv", crmv);

        long result = db.insert("aplicacao_vacina", null, values);
        db.close();
        return result != -1;
    }

    public List<Vacina> getAllVacinas() {
        List<Vacina> vacinas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM vacina";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_vacina"));
                String nome = cursor.getString(cursor.getColumnIndexOrThrow("nome_vacina"));
                vacinas.add(new Vacina(id, nome));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return vacinas;
    }

    public Tutor getTutorByNfcId(String id_nfc) {
        SQLiteDatabase db = this.getReadableDatabase();
        Tutor tutor = null;

        Cursor cursor = db.rawQuery(
                "SELECT t.* FROM Tutor t " +
                        "INNER JOIN Pet p ON t.id_tutor = p.id_tutor " +
                        "WHERE p.id_nfc = ?",
                new String[]{id_nfc}
        );

        Log.d("Debug", "Executando query com ID NFC: " + id_nfc);

        if (cursor.moveToFirst()) {
            Log.d("Debug", "Resultado encontrado no banco.");
            tutor = new Tutor(
                    cursor.getString(cursor.getColumnIndexOrThrow("telefone_tutor"))
            );
        } else {
            Log.d("Debug", "Nenhum resultado encontrado para o ID NFC.");
        }
        cursor.close();
        db.close();
        return tutor;
    }

    public List<Pet> searchPets(String query) {
        List<Pet> petList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM pet WHERE nome_pet LIKE ? OR raca_pet LIKE ?",
                new String[]{"%" + query + "%", "%" + query + "%"});
        if (cursor.moveToFirst()) {
            do {
                Pet pet = new Pet(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id_pet")),
                        cursor.getString(cursor.getColumnIndexOrThrow("id_nfc")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nome_pet")),
                        cursor.getString(cursor.getColumnIndexOrThrow("raca_pet")),
                        cursor.getString(cursor.getColumnIndexOrThrow("genero_pet")),
                        cursor.getString(cursor.getColumnIndexOrThrow("data_nasc_pet")),
                        cursor.getString(cursor.getColumnIndexOrThrow("observacao_pet")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("id_tutor"))
                );
                petList.add(pet);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return petList;
    }

    public List<Pet> getPetsDoTutor(int id_tutor) {
        List<Pet> petsDoTutor = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM pet WHERE id_tutor = ?", new String[]{String.valueOf(id_tutor)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Pet pet = new Pet();
                pet.setId_pet(cursor.getInt(cursor.getColumnIndexOrThrow("id_pet")));
                pet.setNome_pet(cursor.getString(cursor.getColumnIndexOrThrow("nome_pet")));
                pet.setRaca_pet(cursor.getString(cursor.getColumnIndexOrThrow("raca_pet")));
                pet.setGenero_pet(cursor.getString(cursor.getColumnIndexOrThrow("genero_pet")));
                pet.setData_nasc_pet(cursor.getString(cursor.getColumnIndexOrThrow("data_nasc_pet")));
                pet.setObservacao_pet(cursor.getString(cursor.getColumnIndexOrThrow("observacao_pet")));
                pet.setId_nfc(cursor.getString(cursor.getColumnIndexOrThrow("id_nfc")));
                pet.setId_tutor(cursor.getInt(cursor.getColumnIndexOrThrow("id_tutor")));
                petsDoTutor.add(pet);
            }
            cursor.close();
        }
        db.close();
        return petsDoTutor;
    }
}