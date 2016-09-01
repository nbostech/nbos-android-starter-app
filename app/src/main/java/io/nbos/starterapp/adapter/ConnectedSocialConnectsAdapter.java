package io.nbos.starterapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.List;

import io.nbos.starterapp.R;
import io.nbos.starterapp.models.SocialConnects;

/**
 * Created by vivekkiran on 7/12/16.
 */

public class ConnectedSocialConnectsAdapter extends RecyclerView.Adapter<ConnectedSocialConnectsAdapter.ViewHolder> {

    private final Context mContext;
    List<SocialConnects> connectedItems;

    public ConnectedSocialConnectsAdapter(Context context, List<SocialConnects> connectedItems) {
        this.mContext = context;
        this.connectedItems = connectedItems;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }


    @Override

    public void onBindViewHolder(ViewHolder holder, int position) {

        if (connectedItems != null && connectedItems.get(position).getConnectName().contains("Facebook")) {
            holder.account.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_facebook));

        } else if (connectedItems != null && connectedItems.get(position).getConnectName().contains("Google")) {
            holder.account.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_google_plus));

        } else if (connectedItems != null && connectedItems.get(position).getConnectName().contains("Instagram")) {
            holder.account.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_instagram));

        } else if (connectedItems != null && connectedItems.get(position).getConnectName().contains("GitHub")) {
            holder.account.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_github));

        } else if (connectedItems != null && connectedItems.get(position).getConnectName().contains("LinkedIn")) {
            holder.account.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_linkedin));

        } else {
            holder.account.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_launcher));

        }

    }

    @Override
    public int getItemCount() {
        return (null != connectedItems ? connectedItems.size() : 0);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageButton account;

        public ViewHolder(View itemView) {
            super(itemView);
            account = (ImageButton) itemView.findViewById(R.id.socialAccount);

        }

        @Override
        public void onClick(View v) {

        }
    }
}
