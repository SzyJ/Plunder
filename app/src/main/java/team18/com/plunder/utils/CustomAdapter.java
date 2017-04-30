package team18.com.plunder.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import team18.com.plunder.R;

/**
 * Created by Szymon on 30-Apr-17.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{
    private Context context;
    private List<HuntCardData> data;

    public CustomAdapter(Context context, List<HuntCardData> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_hunt, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.huntNameText.setText(data.get(position).getName());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        holder.huntCreatedText.setText("Date created: " + df.format(data.get(position).getCreationDate()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView huntNameText;
        public TextView huntCreatedText;

        public ViewHolder(View itemView) {
            super(itemView);

            huntNameText = (TextView) itemView.findViewById(R.id.card_hunt_name);
            huntCreatedText = (TextView) itemView.findViewById(R.id.card_hunt_created_text);
        }
    }
}
