/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.grep.ui;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class GaugeInterface {
	Integer m_Value;

    /** Instantiate the interface and set the context */
    GaugeInterface() {}
	
	public void setValue(Integer val) {
		m_Value = val;
	}

    @JavascriptInterface
    public Integer getValue() {
		return m_Value;
    }
}