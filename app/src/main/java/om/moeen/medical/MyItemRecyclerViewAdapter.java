package om.moeen.medical;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {
    private MyListData[] listdata;
    Context context;

    public MyItemRecyclerViewAdapter(Context context, MyListData[] listdata) {
        this.context = context;
        this.listdata = listdata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.fragment_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyListData myListData = listdata[position];

        holder.imageView.setImageResource(myListData.getImgId());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "";
                if (myListData.getDescription().equalsIgnoreCase("Education")) {
                    url = "https://edu.moeen.om/";
                } else if (myListData.getDescription().equalsIgnoreCase("Family") ||
                        myListData.getDescription().equalsIgnoreCase("Social") ||
                        myListData.getDescription().equalsIgnoreCase("selfdevelopment")) {
                    url = "https://family.moeen.om/";
                } else if (myListData.getDescription().equalsIgnoreCase("Psychology")) {
                    Intent i = new Intent(context, MainActivity.class);
                    context.startActivity(i);
                } else {
                    url = "https://legal.moeen.om/";
                }

                Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                httpIntent.setData(Uri.parse(url));
                context.startActivity(httpIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
        }
    }
}