package om.moeen.medical;

import android.content.Context;
import android.content.Intent;
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
                if (myListData.getDescription().equalsIgnoreCase("Education")) {
                    Intent i = new Intent(context, ConsultationActivity.class);
                    i.putExtra("from", "Edu");
                    context.startActivity(i);
                } else if (myListData.getDescription().equalsIgnoreCase("Family") ||
                        myListData.getDescription().equalsIgnoreCase("Psychology") ||
                        myListData.getDescription().equalsIgnoreCase("Social")) {
                    Intent i = new Intent(context, ConsultationActivity.class);
                    i.putExtra("from", "social");
                    context.startActivity(i);
                } else if (myListData.getDescription().equalsIgnoreCase("selfdevelopment")) {
                    Intent i = new Intent(context, ConsultationActivity.class);
                    i.putExtra("from", "selfdevelopment");
                    context.startActivity(i);
                } else {
                    Intent i = new Intent(context, ConsultationActivity.class);
                    i.putExtra("from", "legal");
                    context.startActivity(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.textView = itemView.findViewById(R.id.textView);
        }
    }
}