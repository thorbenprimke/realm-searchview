package co.moonmonkeylabs.realmsearchview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;

/**
 * A View that has a search bar with a results view for displaying typeahead results in a list that
 * is backed by a Realm.
 */
public class RealmSearchView extends LinearLayout {

    private RealmRecyclerView realmRecyclerView;
    private ClearableEditText searchBar;
    private RealmSearchAdapter adapter;

    private boolean addFooterOnIdle;

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
        searchBar = (ClearableEditText) findViewById(R.id.search_bar);

        initAttrs(context, attrs);

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
                        addFooterHandler(s.toString());
                    }
                }
        );
    }

    private Handler handler = null;

    private void addFooterHandler(final String search) {
        if (!addFooterOnIdle) {
            return;
        }
        if (handler != null) {
            return;
        }

        adapter.removeFooter();
        handler = new Handler();
        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if (search.equals(searchBar.getText().toString())) {
                            adapter.addFooter();
                        }
                        handler = null;
                    }
                },
                300);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.RealmSearchView);

        int hintTextResId = typedArray.getResourceId(
                R.styleable.RealmSearchView_rsvHint,
                R.string.rsv_default_search_hint);
        searchBar.setHint(hintTextResId);

        int clearDrawableResId =
                typedArray.getResourceId(R.styleable.RealmSearchView_rsvClearDrawable, -1);
        if (clearDrawableResId != -1) {
            searchBar.setClearDrawable(getResources().getDrawable(clearDrawableResId));
        }

        addFooterOnIdle = typedArray.getBoolean(R.styleable.RealmSearchView_rsvAddFooter, false);

        typedArray.recycle();
    }

    public void setAdapter(RealmSearchAdapter adapter) {
        this.adapter = adapter;
        realmRecyclerView.setAdapter(adapter);
        this.adapter.filter("");
    }

    public String getSearchBarText() {
        return searchBar.getText().toString();
    }

    public void addSearchBarTextChangedListener(TextWatcher watcher) {
        searchBar.addTextChangedListener(watcher);
    }

    public void removeSearchBarTextChangedListener(TextWatcher watcher) {
        searchBar.removeTextChangedListener(watcher);
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener onEditorActionListener) {
        searchBar.setOnEditorActionListener(onEditorActionListener);
    }
}
