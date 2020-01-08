/**
 * Class Description    : Adapter class to display users in list view
 * Contributors         : Eko Manggaprouw
 */
package com.ncl.csc2022.team10.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ncl.csc2022.team10.Employee;
import com.ncl.csc2022.team10.Profile;
import com.ncl.csc2022.team10.R;

import net.karthikraj.shapesimage.ShapesImage;

import java.util.ArrayList;
import java.util.List;

public class ListView_RecyclerViewAdapter extends RecyclerView.Adapter<ListView_RecyclerViewAdapter.ViewHolder> implements Filterable {

    //Variables needed for ListView adapter class
    private static final String TAG = "ListView_Adapter";
    private Context context;
    private List<Employee> mData;
    private List<Employee> searchData;

    /**
     * Constructor for the ListView Adapter class
     * @param context Caller class's context
     * @param mData Data to be displayed in RecyclerView
     */
    public ListView_RecyclerViewAdapter(Context context, List<Employee> mData) {
        this.context = context;
        this.mData = mData;
        searchData = new ArrayList<>(mData); //A copy which can be used independently for search
    }

    /**
     * View creator and layout inflater for ListView adapter class
     * @param viewGroup Parent view group to inflate list layout
     * @param i View type of the new view
     * @return New ViewHolder for List layout
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.employee_layout_list, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    /**
     * Bind and display the data to the correct position
     * @param viewHolder ViewHolder which holds the content
     * @param position Current item's position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        // Setting default profile image
        if(mData.get(position).getProfile_pic() == null){
            viewHolder.image.setImageResource(R.drawable.no_profile_img);
        }else{
            viewHolder.image.setImageBitmap(mData.get(position).getProfile_pic());
        }

        // Set user's info to be displayed
        viewHolder.textView_name.setText(mData.get(position).getName());
        viewHolder.textView_title.setText(mData.get(position).getTitle());

        // Setting up listener to go to user's profile screen
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Profile.class);
                intent.putExtra("id", mData.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    /**
     * Getting total number of elements currently stored in the data set
     * @return Total number elements from list of users passed to adapter
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * Inner class for information hold inside one user list
     */
    public class ViewHolder extends RecyclerView.ViewHolder{

        ShapesImage image;
        TextView textView_name;
        TextView textView_title;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.profilePicture);
            textView_name = itemView.findViewById(R.id.employeeName);
            textView_title = itemView.findViewById(R.id.employeeTitle);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Employee> filteredList = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0){
                filteredList.addAll(searchData);
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(Employee employee : searchData){
                    if(employee.getName().toLowerCase().contains(filterPattern) || employee.getTitle().toLowerCase().contains(filterPattern) || employee.getSkills().toLowerCase().contains(filterPattern)){
                        filteredList.add(employee);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mData.clear();
            mData.addAll((List)filterResults.values);
            notifyDataSetChanged();
        }
    };
}
