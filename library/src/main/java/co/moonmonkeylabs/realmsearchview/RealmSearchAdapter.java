package co.moonmonkeylabs.realmsearchview;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

/**
 * Created by thorben on 11/1/15.
 */
public abstract class RealmSearchAdapter<T extends RealmObject, VH extends RealmViewHolder>
        extends RealmBasedRecyclerViewAdapter<T, VH> {

    private Class modelClass;
    private String filterColumnName;

    public RealmSearchAdapter(
            Context context,
            Class modelClass,
            String filterColumnName) {
        super(context, null, false, false);
        this.modelClass = modelClass;
        this.filterColumnName = filterColumnName;
    }

    public void filter(Context context, String input) {
        RealmResults<T> businesses;
        Realm realm = Realm.getInstance(context);
        if (input.isEmpty()) {
            businesses = realm.where(modelClass).findAllSorted(filterColumnName, true);
        } else {
            businesses = realm.where(modelClass)
                    .contains(filterColumnName, input, false)
                    .findAllSorted(filterColumnName, true);
        }
        updateRealmResults(businesses);
    }
}
