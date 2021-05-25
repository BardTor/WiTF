package com.gomorra.witf.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.gomorra.witf.R;
import com.gomorra.witf.ScanProduct;
import com.gomorra.witf.data.DataBaseHandler;
import com.gomorra.witf.model.Product;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

//RecyclerView used to maintain ViewHolders of Products

public class RecyclerViewAdapterBP extends Adapter<RecyclerViewAdapterBP.ViewHolder> {

    private Context context;
    private List<Product> productList;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private LayoutInflater layoutInflater;

    public RecyclerViewAdapterBP(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;

    }

    @NonNull
    @Override
    public RecyclerViewAdapterBP.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //db_single_product_row is now "inflated" aka created
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.db_single_product_row, viewGroup, false);

        return new ViewHolder(view, context);
    }

    //finalizing ViewHolder

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterBP.ViewHolder viewHolder, int position) {
        Product product = productList.get(position);
        viewHolder.productName.setText(MessageFormat.format("Product: {0}", product.getProductName()));
        viewHolder.productWeight.setText(MessageFormat.format("Weight (g/ml): {0}", product.getProductWeight()));
        viewHolder.productQuantity.setText(MessageFormat.format("Quantity: {0}", product.getProductQuantity()));
        viewHolder.productExpiryDate.setText(MessageFormat.format("Expiry Date: {0}", product.getProductExpiryDate()));

        if (product.getProductSecondaryQuantity() == 1)
            viewHolder.productTotalQuantity.setText(MessageFormat.format("Total Available (g/ml): {0}", product.getProductTotalQuantity()));
        else
            viewHolder.productTotalQuantity.setText(MessageFormat.format("Total Available (units): {0}", product.getProductTotalQuantity()));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ViewHolder is fed data from db_single_product_row

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public int productId;
        public TextView productName;
        public TextView productWeight;
        public TextView productQuantity;
        public TextView productExpiryDate;
        public TextView productTotalQuantity;
        public ImageButton editProductButton;
        public ImageButton deleteProductButton;


        public ViewHolder(@NonNull View itemView, Context cntx) {
            super(itemView);
            context = cntx;
            productName = itemView.findViewById(R.id.product_name_bp);
            productWeight = itemView.findViewById(R.id.product_weight_bp);
            productQuantity = itemView.findViewById(R.id.product_quantity_bp);
            productExpiryDate = itemView.findViewById(R.id.product_expiry_date_bp);
            productTotalQuantity = itemView.findViewById(R.id.product_total_quantity_bp);

            editProductButton = itemView.findViewById(R.id.edit_Button_bp);
            deleteProductButton = itemView.findViewById(R.id.delete_Button_bp);

            editProductButton.setOnClickListener(this);
            deleteProductButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int productPosition = getAdapterPosition();
            Product productAtPosition = productList.get(productPosition);


            switch (v.getId()) {
                //case enables editing items
                case R.id.edit_Button_bp:

                    editProduct(productAtPosition);

                    break;
                //case enables deleting items

                case R.id.delete_Button_bp:

                    deleteProduct(productAtPosition.getProductId());
                    break;
            }


        }
        //confirmation pop up alert dialog

        private void deleteProduct(final int productId) {

            builder = new AlertDialog.Builder(context);
            layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.browse_products_confirmation_popup, null);

            Button noButton = view.findViewById(R.id.confirmation_no_button_bp);
            Button yesButton = view.findViewById(R.id.confirmation_yes_button_bp);


            builder.setView(view);
            alertDialog = builder.create();
            alertDialog.show();

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataBaseHandler db = new DataBaseHandler(context);
                    db.deleteProduct(productId);
                    //Log.d("Deleting", "Product: " + productId);
                    productList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    alertDialog.dismiss();
                }
            });
        }

        //alert dialog builder below

        private void editProduct(final Product edited_product) {

            builder = new AlertDialog.Builder(context);
            layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.browse_products_edit_products_popup, null);

            TextView productTextView = view.findViewById(R.id.product_textView_bp);
            TextView weightTextView = view.findViewById(R.id.weight_textView_bp);
            EditText quantityEditText = view.findViewById(R.id.quantity_editText_bp);
            EditText dateEditText = view.findViewById(R.id.date_editText_bp);
            EditText totalQuantityEditText = view.findViewById(R.id.total_quantity_editText_bp);

            RelativeLayout secondaryQuantityRelativeLayout = view.findViewById(R.id.secondary_quantity_data_bp);

            Button cancelButton = view.findViewById(R.id.cancel_scan_button_bp);
            Button updateButton = view.findViewById(R.id.confirm_scan_button_bp);


            productTextView.setText(edited_product.getProductName());
            weightTextView.setText(String.valueOf(edited_product.getProductWeight()));
            quantityEditText.setText(String.valueOf(edited_product.getProductQuantity()));
            dateEditText.setText(edited_product.getProductExpiryDate());

            dateEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();
                    //calendar.set(2023, 8, 31);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);
                    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            month = month + 1;
                            String productExpiryDateExtracted = year + "-" + month + "-" + day;
                            dateEditText.setText(productExpiryDateExtracted);
                            //Log.d("Testing_date", "" + dayOfMonth + " " + monthOfYear + " " + year);
                        }
                    };
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.calendar_theme, onDateSetListener, year, month, day);
                    datePickerDialog.show();
                }
            });


            totalQuantityEditText.setText(String.valueOf(edited_product.getProductTotalQuantity()));


            builder.setView(view);
            alertDialog = builder.create();
            alertDialog.show();

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            //updating product details upon pressing update button

            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DataBaseHandler db = new DataBaseHandler(context);

                    edited_product.setProductName(productTextView.getText().toString());
                    edited_product.setProductWeight(Integer.parseInt(weightTextView.getText().toString()));
                    edited_product.setProductQuantity(Integer.parseInt(quantityEditText.getText().toString()));
                    edited_product.setProductExpiryDate(dateEditText.getText().toString());
                    edited_product.setProductTotalQuantity(Integer.parseInt(totalQuantityEditText.getText().toString()));




/*                    Log.d("Testing_update", "Secondary Q: " + edited_product.getProductSecondaryQuantity());
                    Log.d("Testing_update", "Primary Q: " + Integer.parseInt(quantityEditText.getText().toString()));

                    Log.d("Testing_update", "Product name: " + productTextView.getText().toString());
                    Log.d("Testing_update", "Product weight: " + weightTextView.getText().toString());
                    Log.d("Testing_update", "Product quantity: " + quantityEditText.getText().toString());
                    Log.d("Testing_update", "Product date: " + dateEditText.getText().toString());
                    Log.d("Testing_update", "Product secondary quantity: " + totalQuantityEditText.getText().toString());*/

                    if (!String.valueOf(productTextView.getText()).isEmpty()
                            && !String.valueOf(weightTextView.getText()).isEmpty()
                            && !String.valueOf(quantityEditText.getText()).isEmpty()
                            && !String.valueOf(dateEditText.getText()).isEmpty()
                            && !String.valueOf(totalQuantityEditText.getText()).isEmpty()) {

                        db.updateProduct(edited_product);
                        notifyItemChanged(getAdapterPosition(), edited_product);
                        alertDialog.dismiss();

                    } else {
                        alertDialog.dismiss();
                        Toast.makeText(context, "One of the fields is blank, please amend.", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

    }


}
