package com.grep.ui;

//Defines what a single list item will contain 
public class ListItem {
	private int icon;          //an icon to display (either an edit icon, or delete icon), references a resource
	private String text;       //text item to display (either a TextView string or an EditText field)
	private int itemId;        //id to be associated with this list item, likely to be this list items topic id or keyword id
	private int textTrackerId; //unique id to associate KeywordListItemHolder.textEdit with its associated ListItem in ListItemAdater.listItems
	
    public ListItem(int icon, String text, int itemId) {
        this.icon = icon;
        this.text = text;
        this.itemId = itemId;
        this.textTrackerId = -1;
    }
    
    public int getIcon()
    {
    	return this.icon;
    }
    
    public String getText()
    {
    	return this.text;
    }
    
    public void setText(String text)
    {
    	this.text = text;
    }
    
    public int getItemId()
    {
    	return this.itemId;
    }
    
    public int getTextTrackerId()
    {
    	return this.textTrackerId;
    }
    
    public void setTextTrackerId(int textTrackerId)
    {
    	this.textTrackerId = textTrackerId;
    }
}