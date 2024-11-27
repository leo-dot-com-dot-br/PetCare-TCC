package com.example.apptcc;

public class Pet {

    private int id_pet;
    private String id_nfc;
    private String nome_pet;
    private String raca_pet;
    private String genero_pet;
    private String data_nasc_pet;
    private String observacao_pet;
    private int id_tutor;

    public Pet(int id_pet, String id_nfc, String nome_pet, String raca_pet, String genero_pet, String data_nasc_pet, String observacao_pet, int id_tutor) {
        this.id_pet = id_pet;
        this.id_nfc = id_nfc;
        this.nome_pet = nome_pet;
        this.raca_pet = raca_pet;
        this.genero_pet = genero_pet;
        this.data_nasc_pet = data_nasc_pet;
        this.observacao_pet = observacao_pet;
        this.id_tutor = id_tutor;
    }
    public Pet() {

    }
    public int getId_pet() {
        return id_pet;
    }

    public void setId_pet(int id_pet) {
        this.id_pet = id_pet;
    }

    public String getId_nfc() {
        return id_nfc;
    }

    public void setId_nfc(String id_nfc) {
        this.id_nfc = id_nfc;
    }

    public String getNome_pet() {
        return nome_pet;
    }

    public void setNome_pet(String nome_pet) {
        this.nome_pet = nome_pet;
    }

    public String getRaca_pet() {
        return raca_pet;
    }

    public void setRaca_pet(String raca_pet) {
        this.raca_pet = raca_pet;
    }

    public String getGenero_pet() {
        return genero_pet;
    }

    public void setGenero_pet(String genero_pet) {
        this.genero_pet = genero_pet;
    }

    public String getData_nasc_pet() {
        return data_nasc_pet;
    }

    public void setData_nasc_pet(String data_nasc_pet) {
        this.data_nasc_pet = data_nasc_pet;
    }

    public String getObservacao_pet() {
        return observacao_pet;
    }

    public void setObservacao_pet(String observacao_pet) {
        this.observacao_pet = observacao_pet;
    }

    public int getId_tutor() {
        return id_tutor;
    }

    public void setId_tutor(int id_tutor) {
        this.id_tutor = id_tutor;
    }
}
