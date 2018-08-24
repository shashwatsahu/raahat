package com.example.disaster;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.ProductViewHolder> implements View.OnClickListener{
    private Context context;
    private ArrayList<Product> products;
    private SparseBooleanArray selectedItems;
    private static final String TAG = "PRODUCTADAPTER";
    private int position;

    public ProductRecyclerAdapter(Context context, ArrayList<Product> products){
        this.context = context;
        this.products = products;
        selectedItems = new SparseBooleanArray();

        Log.i(TAG, "constructor:" + products.size());
    }

    //todo refresh values
    public void refreshValues(){
        notifyDataSetChanged();
    }

    public void toggleSelection(int pos){
        position = pos;

        final Product product = products.get(pos);

        for (int i = 0; i < selectedItems.size();i++){
            Log.i(TAG, "selected flag:" + selectedItems.get(i));
        }

        if(selectedItems.get(pos)){
            Log.i(TAG, "if selected!");
            // the below line is commmented because i don't know!
            // selectedItems.delete(pos);
            selectedItems.put(pos, false);
            product.setOrders(0);
            notifyItemChanged(pos);

        }
        else {
            Log.i(TAG, "else");

            final Dialog situationDialog = new Dialog(context);
            situationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            situationDialog.setCancelable(false);
            situationDialog.setContentView(R.layout.add_quantity_dialog);

            ImageButton increaseBtn = situationDialog.findViewById(R.id.increase_btn);
            ImageButton decreaseBtn = situationDialog.findViewById(R.id.decrease_btn);
            final EditText quantityEdit = situationDialog.findViewById(R.id.edit_quantity);

            increaseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String quantity = quantityEdit.getText().toString().trim();
                    int num = Integer.parseInt(quantity);
                    num++;
                    quantityEdit.setText(String.valueOf(num));
                }
            });

            decreaseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String quantity = quantityEdit.getText().toString().trim();
                    int num = Integer.parseInt(quantity);
                    if(num > 0) {
                        num--;
                        quantityEdit.setText(String.valueOf(num));
                    }
                }
            });

            Button confirm = situationDialog.findViewById(R.id.done_quantity);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String quantity = quantityEdit.getText().toString().trim();
                    int num = Integer.parseInt(quantity);
                    if(num > 0) {
                        product.setOrders(num);
                        selectedItems.put(position, true);
                        notifyItemChanged(position);
                        situationDialog.dismiss();
                    }
                }
            });

            Button cancelBtn = situationDialog.findViewById(R.id.cancel_quantity);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    product.setOrders(0);
                    notifyDataSetChanged();
                    selectedItems.put(position, false);
                    situationDialog.dismiss();
                }
            });

            situationDialog.show();
            //below syntax is commented because i was checking long touch
        }
    }

    public int getSelectedItemCount(){
        int sum= 0;
        Log.i(TAG, "sparsearray:" + selectedItems.size());
        for(int i = 0; i < selectedItems.size(); i++){
            if(selectedItems.get(i)){
                sum++;
            }
        }
        Log.i(TAG, "sum:" + sum);
        return sum;
    }

    public ArrayList<Product> getSelectedItems(){
       ArrayList<Product> productList = new ArrayList<Product>();

        for(int i = 0; i < selectedItems.size(); i++){
            if(selectedItems.get(i)){
                productList.add(products.get(i));
            }
        }
        return productList;
    }

    public void removeData(int position){
        Log.i(TAG, "position:"+ position);
        try {
            products.remove(position);
        } catch (NullPointerException e){
            Log.i(TAG, "null value :" + e + "value:" + position);
        }
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "creatvh");

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.product_list_items, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Log.i(TAG, "bind");

        Product product = products.get(position);

        String productName = product.getProductName();
        int imgId = product.getImgDrawable();

        holder.img.setImageDrawable(context.getDrawable(imgId));
        holder.nameTxt.setText(productName);
        holder.orderBtn.setTag((Integer) position);
        holder.orderBtn.setBackground(selectedItems.get(position)? context.getDrawable(R.drawable.button)
                        : context.getDrawable(R.drawable.button_deselect));

        if(product.getOrders() > 0){
            holder.quantity.setVisibility(View.VISIBLE);
            holder.quantity.setText(" Orders  " + product.getOrders());
        }else
            holder.quantity.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public void onClick(View view) {



    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "MyHolder";

        ImageView img;
        TextView nameTxt, quantity;
        Button orderBtn;

        private ProductViewHolder(View itemView) {
            super(itemView);
            //ASSIGN
            img = itemView.findViewById(R.id.product_img);
            nameTxt= itemView.findViewById(R.id.product_name);
            orderBtn= itemView.findViewById(R.id.order_btn);
            quantity = itemView.findViewById(R.id.orders_txt);
            // cardView.setCardBackgroundColor(selectedItems.get(position)? 0x9934B5E4
            //        : Color.TRANSPARENT);

            //**itemView.setOnClickListener(this);
        }
    }
}
