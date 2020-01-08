/**
 * Class Description    : Adapter class to display employee in cards view
 * Contributors         : Eko Manggaprouw
 */
package com.ncl.csc2022.team10.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.ncl.csc2022.team10.Employee;
import com.ncl.csc2022.team10.Profile;
import com.ncl.csc2022.team10.R;

import net.karthikraj.shapesimage.ShapesImage;

import java.util.ArrayList;
import java.util.List;

public class CardView_RecyclerViewAdapter extends RecyclerView.Adapter<CardView_RecyclerViewAdapter.CardViewViewHolder> implements Filterable {

    //Variables needed for CardView adapter class
    private static final String TAG = "CardView_RecyclerViewAd";
    private Context mContext;
    private List<Employee> mData;
    private List<Employee> searchData;

    /**
     * Constructor for the CardView Adapter class
     * @param mContext Caller class's context
     * @param mData Data to be displayed in RecyclerView
     */
    public CardView_RecyclerViewAdapter(Context mContext, List<Employee> mData) {
        this.mContext = mContext;
        this.mData = mData;
        searchData = new ArrayList<>(mData); //A copy which can be used independently for search
    }

    /**
     * View creator and layout inflater for CardView adapter class
     * @param parent Parent view group to inflate Card layout
     * @param i View type of the new view
     * @return New ViewHolder for Card layout
     */
    @NonNull
    @Override
    public CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.employee_layout_card,parent, false);
        return new CardViewViewHolder(view);
    }

    /**
     * Bind and display the data to the correct position
     * @param holder CardViewHolder which holds the content
     * @param position Current item's position
     */
    @Override
    public void onBindViewHolder(final CardViewViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        // Setting default profile image
        if(mData.get(position).getProfile_pic() == null){
            holder.profile_pic.setImageResource(R.drawable.no_profile_img);
        }else{
            holder.profile_pic.setImageBitmap(mData.get(position).getProfile_pic());
        }

        // Set user's info to be displayed
        holder.name.setText(mData.get(position).getName());
        holder.title.setText(mData.get(position).getTitle());
        holder.skills.setText(mData.get(position).getSkills());

        // Setting up listener to go to user's profile screen
        holder.parentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Profile.class);
                intent.putExtra("id", mData.get(position).getId());
                mContext.startActivity(intent);
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
     * Inner class for information hold inside one user card
     */
    public static class CardViewViewHolder extends RecyclerView.ViewHolder{

        CardView parentLayout;
        ShapesImage profile_pic;
        TextView name;
        TextView title;
        TextView skills;

        public CardViewViewHolder(@NonNull View itemView) {
            super(itemView);
            // Binding layouts
            parentLayout = itemView.findViewById(R.id.cardview_parent);
            profile_pic = itemView.findViewById(R.id.cardview_profile_picture);
            name = itemView.findViewById(R.id.cardview_employee_name);
            title = itemView.findViewById(R.id.cardview_employee_title);
            skills = itemView.findViewById(R.id.cardview_employee_skills);
        }
    }

    /**
     * Get filter to search over the current data set
     * @return Search filter initialised in this adapter class
     */
    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    /**
     * Filter to search over employee names, employee roles, and employee skills
     */
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
