package com.grep.ui;

import com.grep.gaugebackend.GaugeBackend;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import com.grep.gaugebackend.Gauge;
import com.grep.gaugebackend.WebToast;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * GaugeActivity displays the results of the current analysis session in real time, via
 * a gauge, statistical data, and popular Tweets. 
 * 
 * @author Gresham, Ryan, Everett, Pierce
 *
 */

public class GaugeActivity extends FragmentActivity {
	
	static public Thread m_gaugeConsumerThread;
	static protected GaugeConsumer m_gaugeConsumer = null;
	
	public void showToast(final String toast) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(GaugeActivity.this, toast, Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gauge);
		setTitle(R.string.title_activity_gauge);
		
		int duration = getIntent().getIntExtra("analysisDuration", 10);
	
		String[] keywords = {"doma", "defense of marriage act", "traditional marriage", "marriage", "conservative marriage", "biblical marriage"};
		BlockingQueue<WebToast> webToasts = new ArrayBlockingQueue<WebToast>(100);
		BlockingQueue<Gauge> gaugeValues = new ArrayBlockingQueue<Gauge>(100);
		GaugeBackend.start(keywords, webToasts, gaugeValues, duration);

		WebView webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
		webView.loadUrl("file:///android_asset/gauge.html");
		
		// start another thread to process gauge values TODO add another to process
		// the popular Tweets
		m_gaugeConsumer = new GaugeConsumer(gaugeValues, webToasts, webView);
		m_gaugeConsumerThread = new Thread(m_gaugeConsumer);
		m_gaugeConsumerThread.start();
	}
	
	static public void stopGauge() {
	   // stop the threads (hopefully...)
	   GaugeBackend.stop();
	   GaugeActivity.m_gaugeConsumerThread.interrupt();
	   try {
		   GaugeActivity.m_gaugeConsumerThread.join();
	   } catch (InterruptedException ex) {
		   System.out.println("something went wrong while killing the gauge consumer thread");
	   }
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// get latest gauge value from consumer and save to database
		//if(m_gaugeConsumer != null && m_gaugeConsumer.m_latestGauge != null) {
			
		//}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_gauge, menu);
		return true;
	}
	
	public void showWarningMessage(View v) {
		// Create an instance of the dialog fragment and show it
        DialogFragment dialog = new WarningDialogFragment();
        dialog.show(getSupportFragmentManager(), "WarningDialogFragment");
	}
	
	@Override
	public void onBackPressed() {
		//display the Warning dialog, don't let user exit GaugeActivity with back button alone
	    showWarningMessage(getCurrentFocus());
	}
}
