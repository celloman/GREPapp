package com.grep.gaugebackend;

import java.util.concurrent.BlockingQueue;
//import com.loopj.android.http.*;

/*public class GetSentiment implements Runnable {

	protected BlockingQueue<Tweet> in_queue = null;
	protected BlockingQueue<Tweet> out_queue = null;
	protected String[] keywords = null;
	
	public GetSentiment(BlockingQueue<Tweet> in_queue, BlockingQueue<Tweet> out_queue, String[] keywords) {
		this.in_queue = in_queue;
		this.out_queue = out_queue;
		this.keywords = keywords;
	}
	
	public void run()
	{
		while(true)
		{
			try {
				
				Tweet t = this.in_queue.take();
				
				AsyncHttpClient client = new AsyncHttpClient();
				client.get("http://www.sentiment140.com/api/classify?text=" + t.text.replace(' ', '+') + "&query=" + t.keyword, 
						new AsyncHttpResponseHandler() {
				    @Override
				    public void onSuccess(String response) {
				        System.out.println(response);
				    }
				});
				
				//this.out_queue.put(t);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
}
*/