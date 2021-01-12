package com.example.income_and_expenses;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class expenseAdapter extends RecyclerView.Adapter<expenseAdapter.MyViewHolder> {

    private JSONArray dataExpense;
    public LinearLayout ln;
    public Context contx;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView TvproductName, Tvdate, Tvquantity, Tvprice, Tvtotal;
//        public LinearLayout lnProduct;

        public MyViewHolder(LinearLayout lnout) {
            super(lnout);
//            lnProduct = lnout.findViewById(R.id.productLinear);
            TvproductName = lnout.findViewById(R.id.product);
            Tvdate = lnout.findViewById(R.id.date);
            Tvquantity = lnout.findViewById(R.id.qty);
            Tvprice = lnout.findViewById(R.id.exprice);
            Tvtotal = lnout.findViewById(R.id.exptotal);
        }
    }

    public expenseAdapter(Context context, JSONArray expdata) {
        dataExpense = expdata;
        contx = context;
    }

    @Override
    public expenseAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        ln = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expenses_text_views, parent, false);
        MyViewHolder vh = new MyViewHolder(ln);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            JSONObject object = dataExpense.getJSONObject(position);
            int price = Integer.parseInt(object.getString("price"));
            int qty = Integer.parseInt(object.getString("quantity"));
            int total = price*qty;
            holder.TvproductName.setText(object.getString("name"));
            holder.Tvdate.setText(object.getString("created_at"));
            holder.Tvquantity.setText(object.getString("quantity"));
            holder.Tvprice.setText(object.getString("price"));
            holder.Tvtotal.setText(String.valueOf(total));
//            holder.Tvtotal.setText((Integer.parseInt(object.getString("price"))*Integer.parseInt(object.getString("quantity"))));
//            holder.Tvtotal.setText(Integer.parseInt(holder.Tvprice.getText().toString())* Integer.parseInt(holder.Tvquantity.getText().toString()));


//            holder.Tvtotal.setText(object.getString("created_at"));

        }catch (JSONException ex){

        }
    }

    @Override
    public int getItemCount() {
        return dataExpense.length();
    }


}

