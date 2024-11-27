package com.example.apptcc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class VacinaAdapter extends RecyclerView.Adapter<VacinaAdapter.VacinaViewHolder> {

    private List<Vacina> vacinaList;
    private boolean exibirBotaoAplicar;
    private OnVacinaClickListener onVacinaClickListener;
    private boolean exibirDetalhes;

    public interface OnVacinaClickListener {
        void onVacinaClick(Vacina vacina);
    }

    public VacinaAdapter(List<Vacina> vacinaList, boolean exibirDetalhes, boolean exibirBotaoAplicar, OnVacinaClickListener onVacinaClickListener) {
        this.vacinaList = vacinaList;
        this.onVacinaClickListener = onVacinaClickListener;
        this.exibirDetalhes = exibirDetalhes;
        this.exibirBotaoAplicar = exibirBotaoAplicar;
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

            if (exibirBotaoAplicar) {
                holder.btnAplicarVacina.setVisibility(View.VISIBLE);
                holder.btnAplicarVacina.setOnClickListener(v -> {
                    if (onVacinaClickListener != null) {
                        onVacinaClickListener.onVacinaClick(vacina);
                    }
                });
            } else {
                holder.btnAplicarVacina.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public int getItemCount() {
        return (vacinaList != null) ? vacinaList.size() : 0;
    }

    public static class VacinaViewHolder extends RecyclerView.ViewHolder {
        Button btnAplicarVacina;
        TextView txtNomeVacina, txtDataAplicacao;

        VacinaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeVacina = itemView.findViewById(R.id.txtNomeVacina);
            txtDataAplicacao = itemView.findViewById(R.id.txtDataAplicacao);
            btnAplicarVacina = itemView.findViewById(R.id.btnAplicarVacina);
        }
    }
}
