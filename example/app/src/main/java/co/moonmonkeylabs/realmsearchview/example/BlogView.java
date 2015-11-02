package co.moonmonkeylabs.realmsearchview.example;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.moonmonkeylabs.realmsearchview.example.model.Blog;

/**
 * Created by thorben on 11/1/15.
 */
public class BlogView extends RelativeLayout {

    @Bind(R.id.emoji)
    TextView emoji;

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.date)
    TextView date;

    @Bind(R.id.description)
    TextView description;

    public BlogView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.blog_view, this);
        ButterKnife.bind(this);
    }

    public void bind(Blog blog) {
        emoji.setText(blog.getEmoji());
        title.setText(blog.getTitle());
        date.setText(blog.getDate());
        description.setText(blog.getContent());
    }
}
