package com.grep.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//adapts our list to fit a specific list type (list of keywords with delete icon or topics with edit icon)
public class ListItemAdapter extends ArrayAdapter<ListItem>{
	
	public enum listItemType {TOPIC, KEYWORD};
	
	Context context;        	     //current context (activity/state of app)
    int layoutResourceId;            //xml layout to use to populate a single row of list    
    List<ListItem> listItems = null; //empty list to be populated later
    listItemType type;               //"topic" or "keyword", defines which ListItemHolder to use
	
    static class TopicListItemHolder
    {
        ImageView editIcon;
        TextView textView;
    }
    
    static class KeywordListItemHolder
    {
    	EditText textEdit;
    	ImageView deleteIcon;
    }
       
    public ListItemAdapter(Context context, int layoutResourceId, List<ListItem> listItems, listItemType type) {
        super(context, layoutResourceId, listItems);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.listItems = listItems;
        this.type = type;
    }

    //gets called to determine how the adapter will fill the listView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        //determine the type of list we are dealing with and set it up accordingly
        if (this.type == listItemType.TOPIC) {
        	row = setUpTopicListItemHolder(row, position, parent);
        }
        else if (this.type == listItemType.KEYWORD) {
        	row = setUpKeywordListItemHolder(row, position, parent);
        }
        
        return row;
    }
    
    //set up the holder and give it values for a list of topic items
    private View setUpTopicListItemHolder(View row, int position, ViewGroup parent)
    {
        TopicListItemHolder holder = null;
        
        if(row == null)
        {
        	//LayoutInflater basically inflates the xml into View objects or in this case a row
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false); //issue here
            
            holder = new TopicListItemHolder();
            holder.editIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.textView = (TextView)row.findViewById(R.id.txtTitle);
            
            row.setTag(holder);
        }
        else
        {
            holder = (TopicListItemHolder)row.getTag();
        }
        
        ListItem item = listItems.get(position);
        holder.textView.setText(item.getText());
        holder.editIcon.setTag(holder.textView.getText());
        holder.editIcon.setImageResource(item.getIcon());
        
        return row;
    }
    
    //set up the holder and give it values for a list of keyword items
    private View setUpKeywordListItemHolder(View row, int position, ViewGroup parent)
    {
        KeywordListItemHolder holder = null;
        
        if(row == null)
        {
        	//LayoutInflater basically inflates the xml into View objects or in this case a row
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false); 
            
            holder = new KeywordListItemHolder();
            holder.deleteIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.textEdit = (EditText)row.findViewById(R.id.txtTitle);
            
            row.setTag(holder);
        }
        else
        {
            holder = (KeywordListItemHolder)row.getTag();
        }
        
        ListItem item = listItems.get(position);
        
        //set values for our holder and give the icon a Tag id to which it can associate w/ topic String
        holder.textEdit.setText(item.getText());
        holder.deleteIcon.setImageResource(item.getIcon());
        //TODO figure out what we are going to setTag()'s to for the list items
        holder.deleteIcon.setTag(position);
        
        /*
		holder.textEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
			    EditText keyword = (EditText) v;
				if(hasFocus) {
			        if (position == 0)
			        	Toast.makeText(context, "gained focus" + keyword.getText(), Toast.LENGTH_SHORT).show();
			    }else
			    	if (position == 0)
			    		Toast.makeText(context, "lost focus" + keyword.getText(), Toast.LENGTH_SHORT).show();
			    }
			});
		*/
        
    	return row;
    }
    
}