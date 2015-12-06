package co.moonmonkeylabs.realmsearchview;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmViewHolder;

/**
 * ViewHolder used with {@link RealmBasedRecyclerViewAdapter}
 */
public class RealmSearchViewHolder extends RealmViewHolder {

    public TextView footerTextView;

    public RealmSearchViewHolder(View itemView) {
        super(itemView);
    }

    public RealmSearchViewHolder(FrameLayout container, TextView footerTextView) {
        super(container);
        this.footerTextView = footerTextView;
    }
}