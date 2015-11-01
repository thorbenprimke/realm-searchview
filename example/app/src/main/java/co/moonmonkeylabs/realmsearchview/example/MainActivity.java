package co.moonmonkeylabs.realmsearchview.example;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import co.moonmonkeylabs.realmsearchview.RealmSearchAdapter;
import co.moonmonkeylabs.realmsearchview.RealmSearchView;
import co.moonmonkeylabs.realmsearchview.example.model.Business;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

public class MainActivity extends AppCompatActivity {

    private Realm realm;
    private RealmSearchView realmSearchView;
    private BusinessRecyclerViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        resetRealm();
        realm = Realm.getInstance(this);

        realm.beginTransaction();
        final List<Business> businessesData = loadBusinessesData();
        realm.copyToRealm(businessesData);
        realm.commitTransaction();

        RealmResults<Business> businesses =
                realm.where(Business.class).findAllSorted("name", true);

        realmSearchView = (RealmSearchView) findViewById(R.id.search_view);
        adapter = new BusinessRecyclerViewAdapter(getBaseContext(), businesses, Business.class, "name");
        realmSearchView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public final List<Business> loadBusinessesData() {
        List<Business> businesses = new ArrayList<>();

        InputStream is = getResources().openRawResource(R.raw.businesses);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                if (lineNumber++ == 0) {
                    continue;
                }

                String[] rowData = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (rowData[6].isEmpty()) {
                    continue;
                }

                businesses.add(new Business(
                        Integer.parseInt(rowData[0]),
                        removeQuotes(rowData[1]),
                        Float.parseFloat(removeQuotes(rowData[6])),
                        Float.parseFloat(removeQuotes(rowData[7]))));
            }
        }
        catch (IOException ex) {}
        finally {
            try {
                is.close();
            }
            catch (IOException e) {}
        }
        return businesses;
    }

    private String removeQuotes(String original) {
        return original.subSequence(1, original.length() - 1).toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }

    private void resetRealm() {
        RealmConfiguration realmConfig = new RealmConfiguration
                .Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.deleteRealm(realmConfig);
    }

    public class BusinessRecyclerViewAdapter extends RealmSearchAdapter<Business,
                    BusinessRecyclerViewAdapter.ViewHolder> {

        public BusinessRecyclerViewAdapter(
                Context context,
                RealmResults<Business> realmResults,
                Class modelClass,
                String filterColumnName) {
            super(context, realmResults, modelClass, filterColumnName);
        }

        public class ViewHolder extends RealmViewHolder {
            public FrameLayout container;
            public TextView quoteTextView;
            public ViewHolder(FrameLayout container) {
                super(container);
                this.container = container;
                this.quoteTextView = (TextView) container.findViewById(R.id.quote_text_view);
            }
        }

        @Override
        public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
            View v = inflater.inflate(R.layout.item_view, viewGroup, false);
            ViewHolder vh = new ViewHolder((FrameLayout) v);
            return vh;
        }

        @Override
        public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {
            final Business quoteModel = realmResults.get(position);
            viewHolder.quoteTextView.setText(quoteModel.getName());
        }

        @Override
        public ViewHolder convertViewHolder(RealmViewHolder viewHolder) {
            return ViewHolder.class.cast(viewHolder);
        }
    }
}
