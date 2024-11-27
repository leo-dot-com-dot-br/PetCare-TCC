package com.example.apptcc;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private List<Pet> petsDoTutor;

    public PetAdapter(List<Pet> petsDoTutor) {
        this.petsDoTutor = petsDoTutor;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pets, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petsDoTutor.get(position);

        holder.txtNomePet.setText(pet.getNome_pet());
        holder.txtRacaPet.setText(pet.getRaca_pet());
        holder.txtGeneroPet.setText(pet.getGenero_pet());
        holder.txtDataNascPet.setText(pet.getData_nasc_pet());

        holder.ivMenuOptions.setOnClickListener(v -> showPopupMenu(v, pet, position));

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, FormDetalhesPet.class);

            intent.putExtra("id_pet", pet.getId_pet());
            intent.putExtra("nome_pet", pet.getNome_pet());
            intent.putExtra("raca_pet", pet.getRaca_pet());
            intent.putExtra("genero_pet", pet.getGenero_pet());
            intent.putExtra("data_nasc_pet", pet.getData_nasc_pet());
            intent.putExtra("observacao_pet", pet.getObservacao_pet());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return petsDoTutor.size();
    }

    private void showPopupMenu(View view, Pet pet, int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_pet_options, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_excluir) {
                excluirPet(view.getContext(), pet.getId_pet(), position);
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void excluirPet(Context context, int petId, int position) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        boolean isDeleted = dbHelper.excluirPet(petId);

        if (isDeleted) {
            petsDoTutor.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, petsDoTutor.size());
            Toast.makeText(context, "Pet excluído com sucesso", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erro ao excluir o pet", Toast.LENGTH_SHORT).show();
        }
    }

    public void updatePetList(List<Pet> petsDoTutor) {
        this.petsDoTutor.clear();
        this.petsDoTutor.addAll(petsDoTutor);
        notifyDataSetChanged();
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder {

        TextView txtNomePet, txtRacaPet, txtGeneroPet, txtDataNascPet;
        ImageView ivMenuOptions;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNomePet = itemView.findViewById(R.id.txtNomePet);
            txtRacaPet = itemView.findViewById(R.id.txtRacaPet);
            txtGeneroPet = itemView.findViewById(R.id.txtGeneroPet);
            txtDataNascPet = itemView.findViewById(R.id.txtDataNascPet);
            ivMenuOptions = itemView.findViewById(R.id.ivMenuOptions);
        }
    }
}
