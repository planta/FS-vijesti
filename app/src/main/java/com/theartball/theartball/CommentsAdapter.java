package com.theartball.theartball;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mario on 21.11.2015..
 */
public class CommentsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CommentItem> komentari;

    public CommentsAdapter(Context c, ArrayList<CommentItem> commentsList) {
        context = c;
        komentari = commentsList;
    }

    @Override
    public int getCount() {
        return komentari.size();
    }

    @Override
    public Object getItem(int position) {
        return komentari.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View commentCellView;

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            commentCellView = new View(context);
        } else {
            commentCellView = (View)convertView;
        }
        CommentItem commentItem = komentari.get(position);
        commentCellView = layoutInflater.inflate(R.layout.comment_cell, null);
        TextView author = (TextView)commentCellView.findViewById(R.id.commentAuthor);
        author.setText(commentItem.author);
        TextView date = (TextView)commentCellView.findViewById(R.id.commentDate);
        date.setText(commentItem.date);
        TextView comment = (TextView)commentCellView.findViewById(R.id.commentContent);
        comment.setText(commentItem.comment);

        return commentCellView;
    }
}
