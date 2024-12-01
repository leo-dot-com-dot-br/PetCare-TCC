package com.example.apptcc;

public class MedVet {
    private String crmv;
    private String nome_mdv;
    private String senha_mdv;

    public MedVet() {}

    public MedVet(String crmv, String nome_mdv, String senha_mdv) {
        this.crmv = crmv;
        this.nome_mdv = nome_mdv;
        this.senha_mdv = senha_mdv;
    }

    public String getCrmv() {
        return crmv;
    }

    public void setCrmv(String crmv) {
        this.crmv = crmv;
    }

    public String getNome_mdv() {
        return nome_mdv;
    }

    public void setNome_mdv(String nome_mdv) {
        this.nome_mdv = nome_mdv;
    }

    public String getSenha_mdv() {
        return senha_mdv;
    }

    public void setSenha_mdv(String senha_mdv) {
        this.senha_mdv = senha_mdv;
    }
}
