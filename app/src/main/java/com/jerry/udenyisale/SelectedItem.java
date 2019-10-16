package com.jerry.udenyisale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.transition.Slide;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static androidx.core.content.FileProvider.getUriForFile;

public class SelectedItem extends AppCompatActivity {

    TextView listText = null;
    // private ArrayList<Stock> stocks = new ArrayList<Stock>();
    ArrayList<Stock> newList = MainActivity.stocks;
    double totalAmout = 0.00;
    String lines = "------------------------------------------------------------\n";
    Button fab;
    final private int REQUEST_CODE_PERMISSION = 111;
    File pdfFile;//pdf storage file object
    TextInputEditText cusName;
    TextInputEditText address;
    EditText dateText;
    Button clearBtn;
    //business address and discriptions



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_item);
        listText =  findViewById(R.id.itemListText);
        fab =  findViewById(R.id.fabBtn);
        clearBtn = findViewById(R.id.fabClear);

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearArray();
            }
        });

        itemListMethod();

        // enter transition from activity A.
        Slide slide = new Slide();
        slide.setDuration(800);
        getWindow().setEnterTransition(slide);

    }

    private void clearArray() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Erase All");
        alertDialog.setMessage("Do you want to Delete The Selected Items?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                newList.clear();//clear arraylist contents
                listText.setText("");
                finish();
                Toast.makeText(SelectedItem.this,"Selected Items Erased!!!",Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }



    private void itemListMethod() {

        for (int i = 0; i < newList.size(); i++) {

            listText.append(lines);
            listText.append("Product Name-->: " + newList.get(i).getItemName() + "\n" + "quantity-->: " + newList.get(i).getQuantity() + "\n"
                    + "Unit Price-->" + newList.get(i).getUnitPrice() + "\n" + "Total Price--> " + (newList.get(i).getQuantity() * newList.get(i).getUnitPrice()) + "\n");
        }
        for (int i = 0; i < newList.size(); i++) {
            totalAmout += newList.get(i).getSumTotal();

        }
        listText.append("----------------------------------------------------\n");
        listText.append("Grand Total-->" + totalAmout + " Naira\n");
        listText.append("----------------------------------------------------\n");

    }

    public void fabListener(View view) {


//dialog for customer details
        final Dialog customerDialog = new Dialog(this);
        customerDialog.setContentView(R.layout.customer_details);
        cusName = (TextInputEditText) customerDialog.findViewById(R.id.customerName);
        address = (TextInputEditText) customerDialog.findViewById(R.id.addressId);
        Button printB = (Button) customerDialog.findViewById(R.id.printB);
        dateText = customerDialog.findViewById(R.id.dateField);
        customerDialog.setTitle("Customer Info");
        //
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String stringDate = sFormat.format(date);
        dateText.setText(stringDate);
        cusName.requestFocus();
        //
        printB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cusName.getText().toString().isEmpty() || address.getText().toString().isEmpty()
                        ||dateText.getText().toString().isEmpty()) {
                    if (cusName.getText().toString().isEmpty()) {
                        cusName.setError("Customer Name Field is empty!");
                    }
                    if (address.getText().toString().isEmpty()) {
                        address.setError("Address Field is empty!");
                    }
                    if (dateText.getText().toString().isEmpty()) {
                        address.setError("Date Field is empty!");
                    }
                } else


                    try {

                        createPDF();
                        openExternalMemory();

                        newList.clear();// clearing arrayList after the print button is clicked

                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                customerDialog.dismiss();


            }
        });

        customerDialog.show();


    }

    public void backBtn(View view) {
        Intent backIntent = new Intent(SelectedItem.this, MainActivity.class);
        startActivity(backIntent);
        //backIntent
    }


    //pdf file creation method
    private void createPDF() throws FileNotFoundException, DocumentException {
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessage("You need to allow Access to Storage", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
                            }
                        }

                    });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            }
            return;
        } else {
            File docsFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/Documents");
            if (!docsFolder.exists()) {
                docsFolder.mkdir();
            }

            //write to the folder, saleFolder that was created above
            pdfFile = new File(docsFolder.getAbsolutePath(), cusName.getText().toString() + "Receipt.pdf");
            OutputStream outPut = new FileOutputStream(pdfFile);

            Document doc = new Document();
            PdfWriter.getInstance(doc, outPut);
            doc.open();
            Font red = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.UNDERLINE, BaseColor.RED);
            Paragraph invoiceParagraph = new Paragraph("INVOICE", red);
            invoiceParagraph.setAlignment(Element.ALIGN_RIGHT);
            doc.add(invoiceParagraph);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd");
            Paragraph datePara = new Paragraph(sformat.format(calendar.getTime()));
            datePara.setAlignment(Element.ALIGN_RIGHT);
            doc.add(datePara);
            //

            //image logo on top of the document
            Drawable draw = getResources().getDrawable(R.drawable.udenyi_logo);
            BitmapDrawable bitdW = ((BitmapDrawable) draw);
            Bitmap bitMap = bitdW.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitMap.compress((Bitmap.CompressFormat.PNG), 100, stream);
            try {
                Image image = Image.getInstance(stream.toByteArray());

                float[] column2= { 0.4f, 1.5f };
                // logo table
                PdfPTable table1 = new PdfPTable(column2);
                // table width percentage of the page width
                table1.setTotalWidth(1);

                PdfPCell cell1 = new PdfPCell();

                cell1.setBorder(Rectangle.NO_BORDER);

                //
                table1.addCell(image);
                // cell1.addElement(myImage);
                cell1.setPhrase(new Phrase("Motto: His Grace Is \r\n" + "	Sufficient For Us. 2Cor 12:9"));
                table1.addCell(cell1);

                Paragraph tableLogo = new Paragraph();
                tableLogo.setAlignment(Element.ALIGN_CENTER);
                tableLogo.add(table1);
                doc.add(tableLogo);
                table1.setSpacingBefore(20);
                table1.setSpacingAfter(50);

            // table for address of the company
                Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.UNDERLINE, BaseColor.RED);
                Paragraph redParag = new Paragraph("UDENYI HOME RESOURCE", redFont);
                redParag.setAlignment(Element.ALIGN_CENTER);
                redParag.setSpacingAfter(30);
                doc.add(redParag);

                //
                float[] column1 = { 1.7f, 1.7f, 2.5f };
                // pdf table
                PdfPTable table2 = new PdfPTable(column1);
                // table width percentage of the page width
                table2.setTotalWidth(1);
                table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell cell = new PdfPCell();

                cell.setBorder(Rectangle.NO_BORDER);

                cell.setPhrase(new Phrase(
                        "ADDRESS 1\n" + "Behind Mabuchi Ultra Modern Market\n" + "FCT,\nKatampe Abuja\n" +
                                "Phone:07033060883"));
                table2.addCell(cell);
                //
                cell.setPhrase(new Phrase("ADDRESS 2\n Galtimary Primary School Junction\n Usmanty Maiduguri \n"
                        + "Borno State\nPhone:08120868260, 07036617433"));
                table2.addCell(cell);

                //
                cell.setPhrase(new Phrase("PRODUCTS: \n Sales of paints,Rosa ,POP Moulding\n OUR SERVICES:\n"
                        + "Instalation Of POP, Screeding, Painting,\nProduction Of Paint"));
                table2.addCell(cell);


                Paragraph tableP2 = new Paragraph();
                tableP2.setAlignment(Element.ALIGN_CENTER);
                tableP2.add(table2);
                doc.add(tableP2);
                table2.setSpacingBefore(20);
                table2.setSpacingAfter(50);



                //
            } catch (IOException e) {
                e.printStackTrace();
            }


            //table of items bought
            //column width
            float[] columnWidth = {7f, 1.5f, 2f, 2.2f};
            //pdf table
            PdfPTable table = new PdfPTable(columnWidth);
            //table width percentage of the page width
            table.setWidthPercentage(90f);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell("Product Descriptions");
            table.addCell("Quantity");
            table.addCell("Unit Price");
            table.addCell("Total Price");
            table.setHeaderRows(1);
            table.setSpacingBefore(20);
            PdfPCell[] cells = table.getRow(0).getCells();
            for (int i = 0; i < cells.length; i++) {
                cells[i].setBackgroundColor(BaseColor.ORANGE);
            }
            for (int j = 0; j < newList.size(); j++) {
                table.addCell("" + newList.get(j).getItemName());
                table.addCell("" + newList.get(j).getQuantity());
                table.addCell("" + newList.get(j).getUnitPrice());
                table.addCell("" + newList.get(j).getQuantity() * newList.get(j).getUnitPrice());
                //
                // table.addCell(""+totalAmout);


            }

            Paragraph tableP = new Paragraph();
            tableP.setAlignment(Element.ALIGN_CENTER);
            tableP.add(table);
            doc.add(tableP);
            //
            //another table tht hold total
            float[] column = {2f, 1.5f};
            //pdf table
            PdfPTable table2 = new PdfPTable(column);
            //table width percentage of the page width
            table2.setWidthPercentage(90f);
            table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table2.addCell("Grand Total");


            table2.addCell("" + totalAmout+" (Naira)");
            table2.setSpacingBefore(10);
            table2.setSpacingAfter(20);

            Paragraph tableP2 = new Paragraph();
            tableP2.setAlignment(Element.ALIGN_CENTER);
            tableP2.add(table2);
            doc.add(tableP2);
            //

            //
            float[] column3= {  1.5f };
            // logo table
            PdfPTable table1 = new PdfPTable(column3);
            // table width percentage of the page width
            table1.setTotalWidth(1);

            PdfPCell cell1 = new PdfPCell();

            cell1.setBorder(Rectangle.NO_BORDER);


            cell1.setPhrase(new Phrase("Customer's Name: " + cusName.getText().toString()));
            table1.addCell(cell1);
            //
            cell1.setPhrase(new Phrase("Customer's Address: " + address.getText().toString()));
            table1.addCell(cell1);

            Paragraph tableLogo = new Paragraph();
            tableLogo.setAlignment(Element.ALIGN_CENTER);
            tableLogo.add(table1);
            doc.add(tableLogo);
            table1.setSpacingBefore(20);
            table1.setSpacingAfter(50);
            //
            Paragraph adminName = new Paragraph("Authorized By:----------------------  Signed  ----------");
            adminName.setSpacingBefore(20);
            adminName.setSpacingAfter(60);
            adminName.setAlignment(Element.ALIGN_CENTER);
            doc.add(adminName);

            //QRCODE

            BarcodeQRCode barCode = new BarcodeQRCode("Udenyi Resources- 07036617433",1000,1000,null);

            Image codeQRImage =barCode.getImage();
            codeQRImage.scaleAbsolute(100,100);
            doc.add(codeQRImage);

            Toast.makeText(this, "The file is created Successfully", Toast.LENGTH_LONG).show();
            listText.setText("PDF file is created in Documents folder inside internal memory");
            doc.close();

        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    try {
                        createPDF();

                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (DocumentException dx) {
                        dx.printStackTrace();
                    }
                } else {
                    //when permission is denied
                    Toast.makeText(this, "WRITE_EXTERNAL permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }


    //show permission message
    private void showMessage(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    // this method launch internal storage when print button is clicked
    private void openExternalMemory() {



        //////////////////////////////////////////
        // file path in the internal memory///////
        ////////////////////////////////////////
        //
        // this file path is alway valid///////////////////////////////////////////////////////////////////////////////////////

        //File pdfFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),  "Documents/JudeReciept.pdf");///
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




        File pdfFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "Documents/"+cusName.getText().toString()+"Receipt.pdf");


        Intent fileIntent = new Intent(Intent.ACTION_VIEW);
        fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = getUriForFile(this, "com.jerry.udenyisale.fileprovider", pdfFile);
        fileIntent.setDataAndType(uri, "application/pdf");
        fileIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(Intent.createChooser(fileIntent,"Choose pdf app To Open The receipt"));


    }




}
