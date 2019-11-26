package c.bilgin.anipal.Adapters.Message;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import c.bilgin.anipal.Model.Message.AnipalMessage;

public abstract class ViewHolder extends RecyclerView.ViewHolder {
    private int type;
    public ViewHolder(@NonNull View itemView,int type) {
        super(itemView);this.type= type;
    }

    public abstract void bindType(AnipalMessage m);

    public int getType() {
        return type;
    }
}
