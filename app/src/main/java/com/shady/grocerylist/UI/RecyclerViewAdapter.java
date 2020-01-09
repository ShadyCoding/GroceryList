package com.shady.grocerylist.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.shady.grocerylist.Activities.DetailsActivity;
import com.shady.grocerylist.Data.DatabaseHandler;
import com.shady.grocerylist.Model.Grocery;
import com.shady.grocerylist.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Grocery> mGroceryItems;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mDialog;
    private LayoutInflater mInflater;

    public RecyclerViewAdapter(Context context, List<Grocery> groceryItems) {
        mContext = context;
        mGroceryItems = groceryItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);
        return new ViewHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Grocery grocery = mGroceryItems.get(position);
        holder.grocertyItemName.setText(grocery.getName());
        holder.quantity.setText(grocery.getQuantity());
        holder.dateAdded.setText(grocery.getDateItemAdded());


    }

    @Override
    public int getItemCount() {
        return mGroceryItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView grocertyItemName;
        public TextView quantity;
        public TextView dateAdded;
        public Button editBtn;
        public Button deleteBtn;
        public int id;

        public ViewHolder(@NonNull View itemView, final Context ctx) {
            super(itemView);
            mContext = ctx;

            grocertyItemName = itemView.findViewById(R.id.name);
            quantity = itemView.findViewById(R.id.quantity);
            dateAdded = itemView.findViewById(R.id.dateAdded);

            editBtn = itemView.findViewById(R.id.editButton);
            editBtn.setOnClickListener(this);

            deleteBtn = itemView.findViewById(R.id.deletButton);
            deleteBtn.setOnClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //go to next screen//details activity
                    int postion = getAdapterPosition();
                    Grocery grocery = mGroceryItems.get(postion);
                    Intent intent = new Intent(mContext, DetailsActivity.class);
                    intent.putExtra("name", grocery.getName());
                    intent.putExtra("quantity", grocery.getQuantity());
                    intent.putExtra("id", grocery.getId());
                    intent.putExtra("date", grocery.getDateItemAdded());
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.editButton:
                    int position = getAdapterPosition();
                    Grocery grocery = mGroceryItems.get(position);
                    editItem(grocery);
                    break;
                case R.id.deletButton:
                    position = getAdapterPosition();
                    grocery = mGroceryItems.get(position);
                    deleteItem(grocery.getId());
                    break;
            }
        }

        public void deleteItem(final int id){

            mBuilder = new AlertDialog.Builder(mContext);
            mInflater = LayoutInflater.from(mContext);
            View view = mInflater.inflate(R.layout.confirmation_dialog, null);

            Button noBtn = view.findViewById(R.id.noButton);
            Button yesBtn = view.findViewById(R.id.yesButton);

            mBuilder.setView(view);
            mDialog = mBuilder.create();
            mDialog.show();

            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });

            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHandler db = new DatabaseHandler(mContext);
                    db.deleteGrocery(id);

                    mGroceryItems.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                    mDialog.dismiss();
                }
            });

        }

        public void editItem(final Grocery grocery){

            mBuilder = new AlertDialog.Builder(mContext);
            mInflater = LayoutInflater.from(mContext);
            final View view = mInflater.inflate(R.layout.popup, null);
            final EditText groceryItem = view.findViewById(R.id.groceryItem);
            final EditText quantity = view.findViewById(R.id.groceryQty);
            final TextView title = view.findViewById(R.id.title);
            title.setText("Edit Grocery");
            Button saveButton = view.findViewById(R.id.saveButton);

            mBuilder.setView(view);
            mDialog = mBuilder.create();
            mDialog.show();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHandler db = new DatabaseHandler(mContext);

                    //update items
                    grocery.setName(groceryItem.getText().toString());
                    grocery.setQuantity(quantity.getText().toString());

                    if (!groceryItem.getText().toString().isEmpty()
                    && !quantity.getText().toString().isEmpty()){
                        db.updateGrocery(grocery);
                        notifyItemChanged(getAdapterPosition(), grocery);
                    }else {
                        Snackbar.make(view, "Add Grocery and Quantity",Snackbar.LENGTH_SHORT).show();
                    }
                    mDialog.dismiss();
                }
            });
        }
    }
}
