package com.gameex.dw.justtalk.groupChat;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gameex.dw.justtalk.R;
import com.github.siyamed.shapeimageview.CircularImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.GroupMemberHolder> {

    private Context mContext;
    private List<Uri> mUris;

    GroupMemberAdapter(Context context, List<Uri> uris) {
        mContext = context;
        mUris = uris;
    }

    @NonNull
    @Override
    public GroupMemberHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.group_member_item, viewGroup, false);
        return new GroupMemberHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMemberHolder holder, int position) {
        if (position == 0) {
            holder.groupMaster.setVisibility(View.VISIBLE);
        }
        Uri uri = mUris.get(position);
        Glide.with(mContext)
                .load(uri)
                .into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return mUris == null ? 0 : mUris.size();
    }

    class GroupMemberHolder extends RecyclerView.ViewHolder {
        CircularImageView icon;
        TextView groupMaster;

        GroupMemberHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.group_member_circle);
            groupMaster = itemView.findViewById(R.id.group_master);
        }
    }
}
