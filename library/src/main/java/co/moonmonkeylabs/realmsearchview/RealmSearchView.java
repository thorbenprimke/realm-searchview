package co.moonmonkeylabs.realmsearchview;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;

/**
 * A View that has a search bar with result view for displaying typeahead results in a list that is
 * backed by a Realm.
 */
public class RealmSearchView extends LinearLayout {

    private RealmRecyclerView realmRecyclerView;

    private EditText searchBar;
    private RealmSearchAdapter adapter;


    public RealmSearchView(Context context) {
        super(context);
        init(context, null);
    }

    public RealmSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RealmSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.realm_search_view, this);
        setOrientation(VERTICAL);

        realmRecyclerView = (RealmRecyclerView) findViewById(R.id.realm_recycler_view);
        searchBar = (EditText) findViewById(R.id.search_bar);

        searchBar.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        adapter.filter(s.toString());
                    }
                }
        );
    }

    public void setAdapter(RealmSearchAdapter adapter) {
        this.adapter = adapter;
        realmRecyclerView.setAdapter(adapter);
        this.adapter.filter("");
    }
}
