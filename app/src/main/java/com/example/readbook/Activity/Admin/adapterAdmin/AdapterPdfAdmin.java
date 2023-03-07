package com.example.readbook.Activity.Admin.adapterAdmin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readbook.Activity.Admin.PdfEditActivity;
import com.example.readbook.Activity.Admin.filtersAdmin.FilterPdfAdmin;
import com.example.readbook.Activity.Admin.modeladmin.ModelPdfFile;
import com.example.readbook.Activity.Admin.MyApplication;
import com.example.readbook.Activity.Details_Book;
import com.example.readbook.databinding.RowPdfAdminBinding;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> implements Filterable {
    //Context
    private Context context;

    //arraylist to hold list of data of type Model pdf
    public ArrayList<ModelPdfFile> pdfFileArrayList,filterList;

    private  FilterPdfAdmin filter;
    //Constructor
    //Progress
    ProgressDialog progressDialog;


    private static final String TAG ="PDF_ADAPTER";
    public AdapterPdfAdmin(Context context, ArrayList<ModelPdfFile> pdfFileArrayList) {
        this.context = context;
        this.pdfFileArrayList = pdfFileArrayList;
        this.filterList  = pdfFileArrayList;

        //init progressdialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please waits");
        progressDialog.setCanceledOnTouchOutside(false);
    }
    //View binn row_pdf.xml
    private RowPdfAdminBinding binding;

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {
        //get data ,setdata ,handle click
        ModelPdfFile model = pdfFileArrayList.get(position);
        String pdfId = model.getId();
        String CategoryId = model.getCategoryId();
        String title = model.getTitle();
        String description = model.getDescription();
        String pdfUrl = model.getUrl();

        long timetamp = model.getTimestamp();
        //we need to convert timetamp to dd/MM/yyyy
        String formatedate= MyApplication.formatTimetamp(timetamp);
        //setData
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formatedate);

        //load further details like category ,pdf from url ,pdf size    in separe function
        MyApplication.loadCategory(
                ""+CategoryId,
                holder.categoriesTv
        );
        MyApplication.loadFilePdfUrl(
                ""+pdfUrl,
                ""+title,
                holder.pdfView,
                holder.progressBar,
                null
        );
        MyApplication.loadSizePdf(
                ""+pdfUrl,
                ""+title,
                holder.sizeTv
        );
        //we will need these function many time so insteady

        //handle click  ,show dialog with option
        holder.moreBtn.setOnClickListener(v->{
            moreOptionDialog(model,holder);
        });
        //handle book /pdf Click open Details book
        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context, Details_Book.class);
            intent.putExtra("bookId",pdfId);
            context.startActivity(intent);
        });

    }

    private void moreOptionDialog(ModelPdfFile model, HolderPdfAdmin holder) {
        String bookId = model.getId();
        String bookUrl = model.getUrl();
        String bookTitle = model.getTitle();
        //option to show in dialog
        String[] options = {"Edit","Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            //Edit Click Open new  activity to edit the book info
                            Intent intent = new Intent(context, PdfEditActivity.class);
                            intent.putExtra("bookId",bookId);
                            context.startActivity(intent);
                        }else if(i==1){
                            MyApplication.deleteBook(
                                    context,
                                    ""+bookId,
                                    ""+bookUrl,
                                    "" +bookTitle

                            );
//                            deleteBook(model,holder);
                        }
                    }
                })
                .show();

     }





    @Override
    public int getItemCount() {
        return pdfFileArrayList.size();//Return of number record
    }

    @Override
    public Filter getFilter() {
        if(filter == null) {
            filter = new FilterPdfAdmin(filterList,this);
        }
        return filter;
    }


    //View holder class for row_pdf.xml
    class HolderPdfAdmin extends RecyclerView.ViewHolder{
        //UI View Of row_pdf.xml
        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv,descriptionTv,categoriesTv,sizeTv,dateTv;
        ImageButton moreBtn;

        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);
            pdfView = binding.pdfView;
            progressBar = binding.progressBar;
            titleTv = binding.titleTv;
            descriptionTv =binding.desriptionTv;
            categoriesTv=binding.CategoriesTv;
            sizeTv=binding.sizeTv;
            dateTv=binding.dateTv;
            moreBtn=binding.moreBtn;





        }
    }
}
