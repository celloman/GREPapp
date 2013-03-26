package com.grep.ui;

//defines what a single row of a list will contain 
public class ListItem {
	private int icon;    //an icon to display (either an edit icon, or delete icon), references a resource
	private String text; //text item to display (either a TextView string or an EditText field)
          
    public ListItem(int icon, String text) {
        this.icon = icon;
        this.text = text;
    }
    
    public int getIcon()
    {
    	return this.icon;
    }
    
    public String getText()
    {
    	return this.text;
    }
}