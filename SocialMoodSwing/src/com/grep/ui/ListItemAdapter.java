package com.grep.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
	public enum listItemType {TOPIC, KEYWORD};
	
	//keeps track of whether a keyword was just deleted or not, required for bug fix #26, set true right after keyword is deleted
	static int keywordDeleted = -1;
	static boolean keywordJustAdded = false;
	
	//id counter for me to use to keep track of keyword text, incremented each time a new id is given out
	int textTrackerId = 1; 
	
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
    	
        /* Was causing bug #33, comment out rather than remove for now, until I can be sure
        View currentFocus = ((Activity)context).getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
        */
        
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
            listItems.get(position).setTextTrackerId(this.textTrackerId);
            this.textTrackerId++; //special id that I can use for lookup later when wanting to save off the text of the string before recycling the view
            
            row.setTag(holder);
        }
        else {
            holder = (KeywordListItemHolder) row.getTag();
            
            //index is the position in listItems where the ListItem previously occupying this row view can be found
            //we will be recycling this row view and filling it with the data from the ListItem at listItems[position]
            //index value of -1 is invalid
            int index = getIndexByTextTrackerId(holder.textTrackerId);
            
            if(index == -1) {
            	Log.e("Error", "Invalid index for text tracker, shouldn't ever have happened. Bad juju!");
            }
            else {
            	//save the contents of the EditText to the ListItem previously occupying this row view, and update the textTrackerId's
            	listItems.get(index).setText(holder.textEdit.getText().toString());
                listItems.get(index).setTextTrackerId(-1);
                listItems.get(position).setTextTrackerId(holder.textTrackerId);
            }
        }
        
        //we need to update adapter with the new text, once editing is finished
        holder.textEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){ //if lost focus, save off the text in case changes were made
                	System.out.println(((Integer)position).toString() + "focus lost");
                	//if the keyword lost focus (not due to a deletion of the keyword), then save the contents of the EditText
                	final EditText edittext = (EditText) v;
            		
                	if (keywordJustAdded) {
            			listItems.get(position + 1).setText(edittext.getText().toString());
            		}
            		else if (keywordDeleted != position) {	
                		//decide to update the listItems list based off what just happened
                		if(keywordDeleted < position && keywordDeleted != -1) {
                			listItems.get(position-1).setText(edittext.getText().toString());
                		}
                		else {
                			listItems.get(position).setText(edittext.getText().toString());
                		}
                	}
                	

                }
            }
        });
        
        //get the data from the ListItem at listItems[position] and use the data to update/populate the KeywordListItemHolder
        ListItem item = listItems.get(position);
        
        holder.textEdit.setText(item.getText());
        holder.deleteIcon.setImageResource(item.getIcon());
        holder.deleteIcon.setTag(position);
        holder.itemId = item.getItemId();
        
    	//reset special case flags
    	keywordDeleted = -1;
    	keywordJustAdded = false;
        
    	return row;
    }
    
    //given a textTrackerId, find the ListItem is listItems which contains this textTrackerId and return its index in listItems
    private int getIndexByTextTrackerId(int targetTextTrackerId)
    {
    	for(int i = 0; i < listItems.size(); i++)
    	{
    		if(listItems.get(i).getTextTrackerId() == targetTextTrackerId) {
    			return i;
    		}
    	}
    	
    	//if not found (shouldn't happen), return index -1
    	return -1;
    }
}
