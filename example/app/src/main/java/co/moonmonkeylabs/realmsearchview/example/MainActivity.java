package co.moonmonkeylabs.realmsearchview.example;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import co.moonmonkeylabs.realmsearchview.RealmSearchAdapter;
import co.moonmonkeylabs.realmsearchview.RealmSearchView;
import co.moonmonkeylabs.realmsearchview.example.model.Blog;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmViewHolder;

public class MainActivity extends AppCompatActivity {

    private RealmSearchView realmSearchView;
    private BusinessRecyclerViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        resetRealm();
        loadBlogData();

        realmSearchView = (RealmSearchView) findViewById(R.id.search_view);
        adapter = new BusinessRecyclerViewAdapter(this, Blog.class, "title");
        realmSearchView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadBlogData() {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonFactory jsonFactory = new JsonFactory();
        try {
            JsonParser jp = jsonFactory.createParser(getResources().openRawResource(R.raw.blog));
            List<Blog> entries = objectMapper.readValue(jp, new TypeReference<List<Blog>>() {
            });
            Realm realm = Realm.getInstance(this);
            realm.beginTransaction();
            realm.copyToRealm(entries);
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            throw new IllegalStateException("Could not load blog data.");
        }
    }

    private void resetRealm() {
        RealmConfiguration realmConfig = new RealmConfiguration
                .Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.deleteRealm(realmConfig);
    }

    public class BusinessRecyclerViewAdapter extends RealmSearchAdapter<Blog,
                    BusinessRecyclerViewAdapter.ViewHolder> {

        public BusinessRecyclerViewAdapter(
                Context context,
                Class modelClass,
                String filterColumnName) {
            super(context, modelClass, filterColumnName);
        }

        public class ViewHolder extends RealmViewHolder {
            public FrameLayout container;
            public TextView blogTextView;
            public ViewHolder(FrameLayout container) {
                super(container);
                this.container = container;
                this.blogTextView = (TextView) container.findViewById(R.id.blog_text_view);
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
            final Blog blog = realmResults.get(position);
            viewHolder.blogTextView.setText(blog.getTitle());
        }

        @Override
        public ViewHolder convertViewHolder(RealmViewHolder viewHolder) {
            return ViewHolder.class.cast(viewHolder);
        }
    }
}
