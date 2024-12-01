package com.example.apptcc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.widget.Toast;
import android.widget.EditText;

public class VacinaAdapter extends RecyclerView.Adapter<VacinaAdapter.VacinaViewHolder> {

    private List<Vacina> vacinaList;
    private boolean exibirBotaoAplicar;
    private OnVacinaClickListener onVacinaClickListener;
    private boolean exibirDetalhes;
    private boolean exibirBotaoEditar;
    private boolean isForSelection;

    public interface OnVacinaClickListener {
        void onVacinaClick(Vacina vacina);
    }

    public VacinaAdapter(List<Vacina> vacinaList, boolean exibirBotaoEditar, boolean exibirDetalhes, boolean exibirBotaoAplicar, boolean isForSelection, OnVacinaClickListener onVacinaClickListener) {
        this.vacinaList = vacinaList;
        this.onVacinaClickListener = onVacinaClickListener;
        this.exibirDetalhes = exibirDetalhes;
        this.exibirBotaoAplicar = exibirBotaoAplicar;
        this.exibirBotaoEditar = exibirBotaoEditar;
        this.isForSelection = isForSelection;
    }

    public void updateVacinas(List<Vacina> newVacinaList) {
        vacinaList.clear();
        vacinaList.addAll(newVacinaList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VacinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vacina, parent, false);
        return new VacinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VacinaViewHolder holder, int position) {
        Vacina vacina = vacinaList.get(position);

        if (vacina != null) {
            holder.txtNomeVacina.setText(vacina.getNomeVacina());

            if (exibirDetalhes) {
                holder.txtDataAplicacao.setVisibility(View.VISIBLE);
                holder.txtDataAplicacao.setText(vacina.getDataAplicacao());
            } else {
                holder.txtDataAplicacao.setVisibility(View.GONE);
            }

            if (isForSelection) {
                holder.itemView.setOnClickListener(v -> {
                    if (onVacinaClickListener != null) {
                        onVacinaClickListener.onVacinaClick(vacina);
                    }
                });
                holder.btnAplicarVacina.setVisibility(View.GONE);
                holder.btnEditarVacina.setVisibility(View.VISIBLE);
                holder.btnEditarVacina.setOnClickListener(v -> abrirDialogEditarVacina(holder.itemView, vacina, holder.getAdapterPosition()));
            } else {
                if (exibirBotaoAplicar) {
                    holder.btnAplicarVacina.setVisibility(View.VISIBLE);
                    holder.btnAplicarVacina.setOnClickListener(v -> {
                        DatabaseHelper dbHelper = new DatabaseHelper(v.getContext());

                        boolean vacinaDuplicada = dbHelper.verificarVacinaJaAplicada(vacina.getId_pet(), vacina.getId());

                        if (vacinaDuplicada) {
                            Toast.makeText(v.getContext(), "Vacina já aplicada.", Toast.LENGTH_SHORT).show();
                        } else {
                            boolean sucesso = dbHelper.inserirVacinaParaPet(
                                    vacina.getId_pet(),
                                    vacina.getId(),
                                    vacina.getDataAplicacao(),
                                    vacina.getCrmv()
                            );

                            if (sucesso) {
                                Toast.makeText(v.getContext(), "Vacina aplicada com sucesso!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(v.getContext(), "Vacina já aplicada!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    holder.btnAplicarVacina.setVisibility(View.GONE);
                }
                if (exibirBotaoEditar) {
                    holder.btnEditarVacina.setVisibility(View.VISIBLE);
                    holder.btnEditarVacina.setOnClickListener(v -> abrirDialogEditarVacina(holder.itemView, vacina, holder.getAdapterPosition()));
                } else {
                    holder.btnEditarVacina.setVisibility(View.GONE);
                }
            }
        }
    }

    private void abrirDialogEditarVacina(View view, Vacina vacina, int position) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(view.getContext());
        builder.setTitle("Editar Vacina");

        final EditText input = new EditText(view.getContext());
        input.setText(vacina.getNomeVacina());
        builder.setView(input);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String novoNome = input.getText().toString().trim();
            if (!novoNome.isEmpty()) {
                vacina.setNomeVacina(novoNome);

                DatabaseHelper dbHelper = new DatabaseHelper(view.getContext());
                boolean atualizado = dbHelper.atualizarNomeVacina(vacina.getId(), novoNome);

                if (atualizado) {
                    notifyItemChanged(position);
                    Toast.makeText(view.getContext(), "Vacina atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(), "Erro ao atualizar a vacina.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(view.getContext(), "O nome da vacina não pode ser vazio.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public int getItemCount() {
        return (vacinaList != null) ? vacinaList.size() : 0;
    }

    public static class VacinaViewHolder extends RecyclerView.ViewHolder {
        Button btnAplicarVacina, btnEditarVacina;
        TextView txtNomeVacina, txtDataAplicacao;

        VacinaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeVacina = itemView.findViewById(R.id.txtNomeVacina);
            txtDataAplicacao = itemView.findViewById(R.id.txtDataAplicacao);
            btnAplicarVacina = itemView.findViewById(R.id.btnAplicarVacina);
            btnEditarVacina = itemView.findViewById(R.id.btnEditarVacina);
        }
    }
}
