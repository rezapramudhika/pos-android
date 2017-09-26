package com.ezpz.pos.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ezpz.pos.R;
import com.ezpz.pos.activity.CashierActivity;
import com.ezpz.pos.provider.Product;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by RezaPramudhika on 8/31/2017.
 */

public class BillAdapter extends ArrayAdapter{
    private List<Product> listProduct;
    private int resource;
    private LayoutInflater inflater;
    Activity thisActivity;
    private boolean is_available;

    public BillAdapter(Activity thisActivity, int resource, List<Product> listProduct) {
        super(thisActivity, resource, listProduct);
        this.listProduct = listProduct;
        this.resource = resource;
        this.thisActivity = thisActivity;
        inflater =(LayoutInflater) thisActivity.getSystemService(LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return super.getCount();
    }

    public List<Product> getListProduct() {

        return listProduct;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();

            convertView = inflater.inflate(resource,null);
            holder.txtProductName = (TextView) convertView.findViewById(R.id.itemBillProductName);
            holder.txtProductPrice = (TextView) convertView.findViewById(R.id.itemBillPrice);
            holder.btnDeleteItem = (Button) convertView.findViewById(R.id.btnBillItemDelete);
            holder.txtQuantity = (TextView) convertView.findViewById(R.id.itemBillQuantity);
            holder.btnItemPlus = (Button) convertView.findViewById(R.id.btnPlus);
            holder.btnItemMin = (Button) convertView.findViewById(R.id.btnMin);
            holder.txtProductDisc = (TextView) convertView.findViewById(R.id.itemBillDisc);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final Product product = listProduct.get(position);


        final ViewHolder finalHolder = holder;
        holder.txtProductName.setText(product.getProductName());
        if(product.getDisc()==0){
            holder.txtProductDisc.setVisibility(View.GONE);
            holder.txtProductPrice.setText(String.valueOf(product.getSellingPrice()));
        }else{
            int prodDisc = product.getSellingPrice()*product.getDisc()/100;
            int finalPrice = product.getSellingPrice()-prodDisc;
            holder.txtProductDisc.setText("Disc: "+product.getDisc()+"%");
            holder.txtProductPrice.setText(String.valueOf(finalPrice));
        }
//        holder.txtProductPrice.setText(String.valueOf(product.getSellingPrice()));


        holder.txtQuantity.setText("x"+product.getItemQuantity());
        holder.btnItemPlus.setTag(position);
        holder.btnItemPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product.setItemQuantity(product.getItemQuantity()+1);
                notifyDataSetChanged();
                if(product.getItemQuantity()==product.getStock()){
                    finalHolder.btnItemPlus.setEnabled(false);
                }
                if(thisActivity instanceof CashierActivity) {
                    ((CashierActivity) thisActivity).sumQty();
                }
                finalHolder.btnItemMin.setEnabled(true);
            }
        });
        holder.btnItemMin.setTag(position);
        holder.btnItemMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (product.getItemQuantity()==1){
                    finalHolder.btnItemMin.setEnabled(false);
                }else{
                    finalHolder.btnItemMin.setEnabled(true);
                    product.setItemQuantity(product.getItemQuantity()-1);
                    notifyDataSetChanged();
                    if(thisActivity instanceof CashierActivity) {
                        ((CashierActivity) thisActivity).sumQty();
                    }
                }
                if(product.getItemQuantity()<product.getStock()){
                    finalHolder.btnItemPlus.setEnabled(true);
                }
            }
        });
        holder.btnDeleteItem.setTag(position);
        holder.btnDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer index = (Integer) view.getTag();
                listProduct.remove(index.intValue());
                notifyDataSetChanged();
                if(thisActivity instanceof CashierActivity){
                    ((CashierActivity)thisActivity).setQuantity(String.valueOf(getCount()));
                    List<Product> products = getListProduct();
                    int sum = 0;
                    int size = products.size();
                    for(int x = 0; x < size; x++){
                        sum += products.get(x).getSellingPrice();
                    }
                    ((CashierActivity)thisActivity).setTotalPrice(String.valueOf(sum));
                    product.setItemQuantity(product.getItemQuantity()-product.getItemQuantity()+1);
                    int tax = sum*10/100;
                    int netBill = sum+tax;

                    ((CashierActivity)thisActivity).setTotalTax(String.valueOf(tax));
                    ((CashierActivity)thisActivity).setNetBill(String.valueOf(netBill));
                    ((CashierActivity)thisActivity).sumQty();
                    if (products.size()==0){
                        ((CashierActivity)thisActivity).btnCheckoutDisable();
                    }
                }
            }
        });




        return convertView;
    }




    class ViewHolder{
        private TextView txtProductName;
        private TextView txtProductPrice;
        private TextView txtQuantity;
        private Button btnDeleteItem;
        private Button btnItemPlus;
        private Button btnItemMin;
        private TextView txtProductDisc;
    }

}
