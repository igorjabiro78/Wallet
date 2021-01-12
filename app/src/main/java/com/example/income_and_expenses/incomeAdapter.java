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

public class incomeAdapter extends RecyclerView.Adapter<incomeAdapter.MyViewHolder> {

    private JSONArray dataIncome;
    public LinearLayout ln;
    public Context contx;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView Tvname, Tvdate, Tvamount;
//        public LinearLayout lnProduct;

        public MyViewHolder(LinearLayout lnout) {
            super(lnout);
//            lnProduct = lnout.findViewById(R.id.productLinear);
            Tvname = lnout.findViewById(R.id.personName);
            Tvdate = lnout.findViewById(R.id.date);
            Tvamount = lnout.findViewById(R.id.amount);

        }
    }

    public incomeAdapter(Context context, JSONArray incdata) {
        dataIncome = incdata;
        contx = context;
    }

    @Override
    public incomeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        ln = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.income_text_view, parent, false);
        MyViewHolder vh = new MyViewHolder(ln);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            JSONObject object = dataIncome.getJSONObject(position);
            holder.Tvname.setText(object.getString("names")); // object.getString is used when you are fetching from serverfu
            holder.Tvdate.setText(object.getString("created_at"));
            holder.Tvamount.setText(object.getString("amount"));
//            holder.Tvtotal.setText((Integer.parseInt(object.getString("price"))*Integer.parseInt(object.getString("quantity"))));
//            holder.Tvtotal.setText(Integer.parseInt(holder.Tvprice.getText().toString())* Integer.parseInt(holder.Tvquantity.getText().toString()));


//            holder.Tvtotal.setText(object.getString("created_at"));

        }catch (JSONException ex){

        }
    }

    @Override
    public int getItemCount() {

        return dataIncome.length();
    }

}
