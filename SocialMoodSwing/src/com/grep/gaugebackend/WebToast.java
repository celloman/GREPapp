package com.grep.gaugebackend;

public class WebToast {
	public String m_type;
	public String m_message;
	
	public String m_heading;
	public int m_data1;
	public int m_data2;
	public int m_data3;
	
	public WebToast(String type, String message) {
		m_type = type;
		m_message = message;
	}
	
	public WebToast(String type, String message, String heading, int d1, int d2, int d3) {
		m_type = type;
		m_message = message;
		m_heading = heading;
		m_data1 = d1;
		m_data2 = d2;
		m_data3 = d3;
	}
}
