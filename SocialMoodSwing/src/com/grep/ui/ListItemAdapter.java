package com.grep.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

//Adapts our listview to be a list of custom views (a list of ListItem's behind the scences)
//Each view (widget) within our listview will be either a TopicListItemHolder or KeywordListItemHolder
public class ListItemAdapter extends ArrayAdapter<ListItem>
{
	static boolean keywordDeleted = false;
	int textTrackerId = 1; //id for me to use to keep track of keyword text
	public enum listItemType {TOPIC, KEYWORD};
	
	Context context;        	     //current context (activity/state of app)
    int layoutResourceId;            //xml layout to use to populate a single row of list    
    List<ListItem> listItems = null; //empty list to be populated later
    listItemType type;               //"topic" or "keyword", defines which ListItemHolder to use
	
    static class TopicListItemHolder
    {
        ImageView editIcon;
        TextView textView;
        int itemId;
    }
    
    static class KeywordListItemHolder
    {
    	EditText textEdit;
    	ImageView deleteIcon;
    	int itemId;
    	int textTrackerId;
    }
       
    public ListItemAdapter(Context context, int layoutResourceId, List<ListItem> listItems, listItemType type)
    {
        super(context, layoutResourceId, listItems);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.listItems = listItems;
        this.type = type;
    }

    //gets called to determine what type of view will be used to fill the listView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) 
    {
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
        else {
            holder = (TopicListItemHolder) row.getTag();
        }
        
        ListItem item = listItems.get(position);
        
        holder.textView.setText(item.getText());
        holder.editIcon.setImageResource(item.getIcon());
        holder.editIcon.setTag(item.getItemId());
        holder.itemId = item.getItemId();
        
        return row;
    }
    
    //set up the holder and give it values for a list of keyword items
    private View setUpKeywordListItemHolder(View row, final int position, ViewGroup parent)
    {
        KeywordListItemHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false); 
            
            holder = new KeywordListItemHolder();
            holder.deleteIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.textEdit = (EditText)row.findViewById(R.id.txtTitle);
            holder.textTrackerId = this.textTrackerId;
            listItems.get(position).textTrackerId = this.textTrackerId;
            this.textTrackerId++; //special id that I can use for lookup later when wanting to save off the text of the string before recycling the view
            
            row.setTag(holder);
        }
        else {
            holder = (KeywordListItemHolder) row.getTag();
            int index = getIndexByTextTrackerId(holder.textTrackerId);
            
            if(index == -1) {
            	//Error should never get here
            }
            else {
            	listItems.get(index).setText(holder.textEdit.getText().toString());
                listItems.get(index).textTrackerId = -1;
                listItems.get(position).textTrackerId = holder.textTrackerId;
            }
        
            //listItems.get(position).setText(holder.textEdit.getText().toString());
        }
        
/*        final EditText editText = holder.textEdit;

        holder.textEdit.addTextChangedListener(new TextWatcher() {
        	public void afterTextChanged(Editable s) {
        		listItems.get(position).setText(editText.getText().toString());
        	}
        	public void beforeTextChanged(CharSequence s, int start, int count, int after){
        	}
        	public void onTextChanged(CharSequence s, int start, int before, int count) {
        		//listItems.get(position).setText(editText.getText().toString());
        	}
        });        
*/
        
//Need to just play around with what I have more, but this could possibly be fixed with current code       
        
// This almost works all the time of saving text changes to item.text
        //we need to update adapter once we finish with editing
        holder.textEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                	if (!keywordDeleted) {
                		final EditText edittext = (EditText) v;
                		listItems.get(position).setText(edittext.getText().toString());
                	}
                	keywordDeleted = false;
                }
            }
        });
        
        
        
        ListItem item = listItems.get(position);
        
        holder.textEdit.setText(item.getText());
        holder.deleteIcon.setImageResource(item.getIcon());
        holder.deleteIcon.setTag(position);
        holder.itemId = item.getItemId();
        
    	return row;
    }
    
    private int getIndexByTextTrackerId(int targetTextTrackerId)
    {
    	for(int i = 0; i < listItems.size(); i++)
    	{
    		if(listItems.get(i).textTrackerId == targetTextTrackerId) {
    			return i;
    		}
    	}
    	return -1;
    }
}
