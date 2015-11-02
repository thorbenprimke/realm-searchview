package co.moonmonkeylabs.realmsearchview.example;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.moonmonkeylabs.realmsearchview.example.model.Blog;

/**
 * View for a {@link Blog} model.
 */
public class BlogItemView extends RelativeLayout {

    @Bind(R.id.emoji)
    TextView emoji;

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.date)
    TextView date;

    @Bind(R.id.description)
    TextView description;

    public BlogItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.blog_item_view, this);
        ButterKnife.bind(this);
    }

    public void bind(Blog blog) {
        emoji.setText(blog.getEmoji());
        title.setText(blog.getTitle());
        date.setText(blog.getDate());
        description.setText(blog.getContent());
    }
}
