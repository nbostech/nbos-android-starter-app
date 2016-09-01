package io.nbos.starterapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import io.nbos.capi.api.v0.IdnCallback;
import io.nbos.capi.modules.identity.v0.IdentityApi;
import io.nbos.capi.modules.identity.v0.models.SocialConnectUrlResponse;
import io.nbos.capi.modules.ids.v0.IDS;

import java.util.List;

import io.nbos.starterapp.R;
import io.nbos.starterapp.models.SocialConnects;
import io.nbos.starterapp.view.SocialWebViewActivity;
import retrofit2.Response;

/**
 * Created by vivekkiran on 7/12/16.
 */

public class AvailableSocialConnectsAdapter extends RecyclerView.Adapter<AvailableSocialConnectsAdapter.ViewHolder> {

    private final Context mContext;
    List<SocialConnects> connectedItems;

    public AvailableSocialConnectsAdapter(Context context, List<SocialConnects> connectedItems) {
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

    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (connectedItems != null && connectedItems.get(position).getConnectName().contains("Facebook")) {
            holder.account.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_facebook));
            holder.account.setOnClickListener(view -> {
                IdentityApi identityApi = IDS.getModuleApi("identity");
                identityApi.socialWebViewLogin(connectedItems.get(position).getConnectName().toLowerCase(), new IdnCallback<SocialConnectUrlResponse>() {

                    @Override
                    public void onResponse(Response<SocialConnectUrlResponse> response) {
                        Intent i = new Intent(mContext, SocialWebViewActivity.class);
                        i.putExtra("name", connectedItems.get(position).getConnectName());
                        i.putExtra("url", response.body().getUrl());
                        ((AppCompatActivity) mContext).startActivityForResult(i, 10);
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }


                });
            });
        } else if (connectedItems != null && connectedItems.get(position).getConnectName().contains("Google")) {
            holder.account.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_google_plus));
            holder.account.setOnClickListener(view -> {
                IdentityApi identityApi = IDS.getModuleApi("identity");
                identityApi.socialWebViewLogin(connectedItems.get(position).getConnectName().toLowerCase(), new IdnCallback<SocialConnectUrlResponse>() {

                    @Override
                    public void onResponse(Response<SocialConnectUrlResponse> response) {
                        SocialWebViewActivity socialWebViewActivity = new SocialWebViewActivity();
                        Intent i = new Intent(mContext, SocialWebViewActivity.class);
                        i.putExtra("name", connectedItems.get(position).getConnectName());
                        i.putExtra("url", response.body().getUrl());
                        ((AppCompatActivity) mContext).startActivityForResult(i, 10);
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }


                });
            });
        } else if (connectedItems != null && connectedItems.get(position).getConnectName().contains("Instagram")) {
            holder.account.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_instagram));
            holder.account.setOnClickListener(view -> {
                IdentityApi identityApi = IDS.getModuleApi("identity");
                identityApi.socialWebViewLogin(connectedItems.get(position).getConnectName().toLowerCase(), new IdnCallback<SocialConnectUrlResponse>() {

                    @Override
                    public void onResponse(Response<SocialConnectUrlResponse> response) {
                        Intent i = new Intent(mContext, SocialWebViewActivity.class);
                        i.putExtra("name", connectedItems.get(position).getConnectName());
                        i.putExtra("url", response.body().getUrl());
                        ((AppCompatActivity) mContext).startActivityForResult(i, 10);
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }


                });
            });
        } else if (connectedItems != null && connectedItems.get(position).getConnectName().contains("gitHub")) {
            holder.account.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_github));
            holder.account.setOnClickListener(view -> {
                IdentityApi identityApi = IDS.getModuleApi("identity");
                identityApi.socialWebViewLogin(connectedItems.get(position).getConnectName(), new IdnCallback<SocialConnectUrlResponse>() {

                    @Override
                    public void onResponse(Response<SocialConnectUrlResponse> response) {
                        Intent i = new Intent(mContext, SocialWebViewActivity.class);
                        i.putExtra("name", connectedItems.get(position).getConnectName());
                        i.putExtra("url", response.body().getUrl());
                        ((AppCompatActivity) mContext).startActivityForResult(i, 10);
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }


                });
            });
        } else if (connectedItems != null && connectedItems.get(position).getConnectName().contains("linkedIn")) {
            holder.account.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_linkedin));
            holder.account.setOnClickListener(view -> {
                IdentityApi identityApi = IDS.getModuleApi("identity");
                identityApi.socialWebViewLogin(connectedItems.get(position).getConnectName(), new IdnCallback<SocialConnectUrlResponse>() {

                    @Override
                    public void onResponse(Response<SocialConnectUrlResponse> response) {
                        Intent i = new Intent(mContext, SocialWebViewActivity.class);
                        i.putExtra("name", connectedItems.get(position).getConnectName());
                        i.putExtra("url", response.body().getUrl());
                        ((AppCompatActivity) mContext).startActivityForResult(i, 10);
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }


                });
            });
        } else {
            holder.account.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_launcher));
            holder.account.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    IdentityApi identityApi = IDS.getModuleApi("identity");
                    identityApi.socialWebViewLogin(connectedItems.get(position).getConnectName().toLowerCase(), new IdnCallback<SocialConnectUrlResponse>() {

                        @Override
                        public void onResponse(Response<SocialConnectUrlResponse> response) {
                            Intent i = new Intent(mContext, SocialWebViewActivity.class);
                            i.putExtra("name", connectedItems.get(position).getConnectName());
                            i.putExtra("url", response.body().getUrl());
                            ((AppCompatActivity) mContext).startActivityForResult(i, 10);
                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }


                    });
                }
            });
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
