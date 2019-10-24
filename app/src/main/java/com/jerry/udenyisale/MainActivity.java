package com.jerry.udenyisale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;

    ActionBarDrawerToggle actionToggle;
    NavigationView navigationView;
    TextView textView, textchange;
    Button addB, listB;
    TextInputEditText productField, quantityField, unitPriceField;

    public static ArrayList<Stock> stocks = new ArrayList<Stock>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //tool bar implementation
        toolbar =  findViewById(R.id.toolbar);
        textchange = findViewById(R.id.textchange);

        collapsingToolbarLayout = findViewById(R.id.collapseBar);
        setSupportActionBar(toolbar);

        textView = findViewById(R.id.textchange);

        addB = findViewById(R.id.addId);
        listB = findViewById(R.id.listId);
        productField = findViewById(R.id.productId);
        quantityField = findViewById(R.id.quantityId);
        unitPriceField = findViewById(R.id.unitPrice);
        ImageView imageView = findViewById(R.id.imageViewId);
        //**********************************************************************************
        addB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });
        listB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (stocks.size() < 1) {
                    Toast.makeText(MainActivity.this, "No product in the List", Toast.LENGTH_SHORT).show();
                } else {
                    Intent listIntent = new Intent(MainActivity.this, SelectedItem.class);

                    startActivity(listIntent);
                    overridePendingTransition(R.transition.slide_in_right,R.transition.slide_out_left);

                }
            }
        });

        //**********************************************************************************************
         Animation expandIn = AnimationUtils.loadAnimation(MainActivity.this,R.anim.expanding_in);
        imageView.startAnimation(expandIn);
        textchange.startAnimation(expandIn);

    }

    private void addItem() {

        if(productField.getText().toString().isEmpty()||quantityField.getText().toString().isEmpty()
                ||unitPriceField.getText().toString().isEmpty()) {


            if(productField.getText().toString().isEmpty()){
                productField.setError("Product field Is Empty");

            }
            if(quantityField.getText().toString().isEmpty()) {
                quantityField.setError("Quantity field Is Empty");
            }
            if(unitPriceField.getText().toString().isEmpty()){
                unitPriceField.setError("Price field Is Empty");

            }

        }  else {
            String productName = productField.getText().toString().trim();

            int quantity = Integer.parseInt(quantityField.getText().toString().trim());

            double unitPrice = Double.parseDouble(unitPriceField.getText().toString().trim());
            //double totalAmout =(quantity*unitPrice);

            stocks.add(new Stock(productName, quantity, unitPrice));
            quantityField.setText("");
            productField.setText("");
            unitPriceField.setText("");
            productField.requestFocus();

        }





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){

            case R.id.about:
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("About me:");
                alertDialog.setMessage("My name is Idoko Agada Jerry. I am the developer of this App." +
                        " You can contact me for desktop and android app developments of any type.\n" +
                        "08160332264\nidokoidoko4@gmail.com");
                alertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();

                break;
            case R.id.exit:
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }


// on back button response
//back button click notification for exit
boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if(doubleBackToExitPressedOnce){
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, " Click again to Exit",Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable(){
            public void run(){
                doubleBackToExitPressedOnce = false;
            }
        },2000);



    }

}
