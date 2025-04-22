# PetCare - Aplicativo de Controle de Vacinas para Pets

Aplicativo desenvolvido como Trabalho de Conclusão de Curso, com o objetivo de auxiliar tutores e médicos veterinários no controle de vacinação dos pets e identificação de pets perdidos. A aplicação foi construída utilizando Android Studio com Java e banco de dados SQLite, além de funcionalidades com leitura de chips NFC.

## 👨‍💻 Tecnologias utilizadas

- Java
- Android Studio
- SQLite
- NFC (Near Field Communication)
- XML (layouts)
- RecyclerView

## 🧠 Funcionalidades principais

### 👥 Acesso por tipo de usuário
- Login com seleção de tipo de usuário (Tutor ou Médico Veterinário)
- Cadastro e login separados para cada tipo

### 🐶 Cadastro de pets (Tutor)
- Inserção de nome, raça, gênero, data de nascimento
- Vinculação com chip NFC na coleira (ID armazenado)
- Lista de pets cadastrados com opção de exclusão

### 💉 Controle de vacinas (Médico Veterinário)
- Tela exclusiva para veterinário com leitura de tag NFC
- Exibição de dados do pet ao ler o chip
- Aplicação de vacina com nome, data e médico responsável
- Visualização do histórico de vacinas
- Edição de observações do pet

## 🧩 Estrutura do Banco de Dados

- **Tutor**: id, nome, senha, e-mail, telefone, endereço, cidade, UF  
- **Médico Veterinário**: CRMV, nome, senha  
- **Pet**: id, nome, raça, gênero, nascimento, tutor_id, nfc_id  
- **Vacina**: id, nome  
- **Aplicações**: id, pet_id, vacina_id, data, medico_id

## 📌 Sobre o projeto
Este projeto teve como objetivo integrar conhecimentos de desenvolvimento mobile, banco de dados local, autenticação por perfil e identificação via NFC para criar uma solução real para clínicas veterinárias ou tutores.

---

Desenvolvido por **Leonardo de Brito** como Trabalho de Conclusão de Curso de Engenharia de Computação(UNIFEV, 2024).
