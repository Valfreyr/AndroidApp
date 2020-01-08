/**
 * Class Description    : Adapter class to display list of documents
 * Contributors         : Elise Hosu, Eko Manggaprouw
 */
package com.ncl.csc2022.team10.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.dynamic.SupportFragmentWrapper;
import com.ncl.csc2022.team10.Document;
import com.ncl.csc2022.team10.DownloadDocumentModel;
import com.ncl.csc2022.team10.R;
import com.ncl.csc2022.team10.ServiceCaller;
import com.ncl.csc2022.team10.SubmissionDialog_DeleteDocument;

import java.util.List;

public class Document_RecyclerViewAdapter extends RecyclerView.Adapter<Document_RecyclerViewAdapter.DocumentViewHolder> implements SubmissionDialog_DeleteDocument.SubmissionDialogListener {

    //Variables needed for Documents adapter class
    private static final String TAG = "Document_Adapter";
    private Context mContext;
    private Activity activity;
    private List<Document> DOCS;
    private ServiceCaller serviceCaller;
    private String userID;

    /**
     * Constructor for the Document Adapter class
     * @param mContext Caller class's context
     * @param DOCS List of Documents
     */
    public Document_RecyclerViewAdapter(Context mContext ,List<Document> DOCS, ServiceCaller sc, String userID, Activity a) {
        this.mContext = mContext;
        this.DOCS = DOCS;
        this.serviceCaller = sc;
        this.userID = userID;
        this.activity = a;
    }

    /**
     * View creator and layout inflater for Document adapter class
     * @param parent Parent view group to inflate Document layout
     * @param viewType View type of the new view
     * @return New ViewHolder for Document layout
     */
    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.documents_layout_list, parent, false);
        DocumentViewHolder holder = new DocumentViewHolder(view);
        return holder;
    }

    /**
     * Bind and display the data to the correct position
     * @param holder DocumentViewHolder which holds the content
     * @param position Current item's position
     */
    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder: called");
        holder.documentName.setText(DOCS.get(position).getName());
        holder.documentDate.setText(DOCS.get(position).getDate());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + DOCS.get(position).getName());
            }
        });

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: download " + DOCS.get(position).getName());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final String s = serviceCaller.getObjects("DownloadDocument", new DownloadDocumentModel(userID, DOCS.get(position).getName())).replaceAll("\"", "");
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Uri uri = Uri.parse(s);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        activity.startActivity(intent);
                                    } catch (Exception e) {
                                        Log.e("DocumentAdapter",e.toString());
                                    }
                                }
                            });
                        } catch (Exception e){
                            Log.e("DocumentAdapter", e.toString());
                        }
                    }
                }).start();
                Toast.makeText(mContext, "Download "+DOCS.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
        final FragmentManager sfm = ((AppCompatActivity)activity).getSupportFragmentManager();
        holder.delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SubmissionDialog_DeleteDocument dialog = new SubmissionDialog_DeleteDocument();
                dialog.populate(userID, DOCS.get(position).getName(), serviceCaller);
                dialog.show(sfm, TAG);
            }
        });

    }

    /**
     * Getting total number of elements currently stored in the data set
     * @return Total number elements from list documents passed to adapter
     */
    @Override
    public int getItemCount() {
        return DOCS.size();
    }

    @Override
    public void onDeleteUserClicked(String fileName) {
        for(Document d : DOCS){
            if(d.getName().toLowerCase().equals(fileName.toLowerCase())){
                DOCS.remove(d);
                break;
            }
        }
    }

    /**
     * Inner class for information hold inside one document list
     */
    public class DocumentViewHolder extends RecyclerView.ViewHolder{

        TextView documentName;
        TextView documentDate;
        ConstraintLayout parentLayout;
        ImageView download;
        ImageView delete;

        public DocumentViewHolder(View itemView) {
            super(itemView);

            documentName = itemView.findViewById(R.id.documentName);
            documentDate = itemView.findViewById(R.id.documentDate);
            download = itemView.findViewById(R.id.download);
            parentLayout = itemView.findViewById(R.id.document_parent_layout);
            delete = itemView.findViewById(R.id.deleteDocument);

        }
    }

}
