package com.example.apptcc;

public class Tutor {
    private int id_tutor;
    private String nome_tutor;
    private String senha_tutor;
    private String email_tutor;
    private String telefone_tutor;
    private String endereco_tutor;
    private String cidade_tutor;
    private String uf_tutor;

    public Tutor(int id_tutor, String nome_tutor, String senha_tutor, String email_tutor, String telefone_tutor, String endereco_tutor, String cidade_tutor, String uf_tutor) {
        this.id_tutor = id_tutor;
        this.nome_tutor = nome_tutor;
        this.senha_tutor = senha_tutor;
        this.email_tutor = email_tutor;
        this.telefone_tutor = telefone_tutor;
        this.endereco_tutor = endereco_tutor;
        this.cidade_tutor = cidade_tutor;
        this.uf_tutor = uf_tutor;
    }

    public int getId_tutor() { return id_tutor; }
    public String getNome_tutor() { return nome_tutor; }
    public String getSenha_tutor() { return senha_tutor; }
    public String getEmail_tutor() { return email_tutor; }
    public String getTelefone_tutor() { return telefone_tutor; }
    public String getEndereco_tutor() { return endereco_tutor; }
    public String getCidade_tutor() { return cidade_tutor; }
    public String getUf_tutor() { return uf_tutor; }
}
