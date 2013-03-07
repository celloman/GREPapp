package com.grep.gaugebackend;

import java.util.concurrent.BlockingQueue;
import java.util.Queue;
import java.util.PriorityQueue;

public class GetWeight implements Runnable {
	
	protected BlockingQueue<Tweet> in_queue = null;
	protected BlockingQueue<Tweet> out_queue = null;
	protected String[] keywords = null;
	protected Queue<Long> seen_ids = new PriorityQueue<Long>(100);
	
	public GetWeight(BlockingQueue<Tweet> in_queue, BlockingQueue<Tweet> out_queue, String[] keywords) {
		this.in_queue = in_queue;
		this.out_queue = out_queue;
		this.keywords = keywords;
	}
        
	private void save_id(long id)
	{
		if(!seen_ids.offer(id))
		{
			seen_ids.remove();
			seen_ids.add(id);
		}
	}
        
	private int get_keyword(Tweet t)
	{
		int count = 0;
		// loop through the keywords
		for(int i = 0; i < this.keywords.length - 1; i++)
		{
			if(t.text.contains(keywords[i]))
			{
				// count matches
				count++;
				// save the match
				t.keyword = keywords[i];
			}
		}
		return count;
	}
	
	public void run()
	{
		while(true)
		{
			try {
				// get from prev module
				Tweet t = this.in_queue.take();
				
				// make sure we only get english
				if(!t.lang.equals("en"))
					continue;
				
				// check for repeated tweets
				if(seen_ids.contains(t.id))
					continue;
				
				// check to make sure it just has one keyword, and store that keyword in the tweet
				if(get_keyword(t) != 1)
					continue;
				
				// weighting algorithm (believe it or not, this is equivalent)
				if(t.is_retweet && seen_ids.contains(t.original_id))
					t.retweets--;
					
				save_id(t.id);
				
				t.weight = t.followers + (208 * t.retweets);

				// send to the next module
				this.out_queue.put(t);
				
			} catch (InterruptedException e) {
				e.printStackTrace(); // come up with something better to do here
			}
			
		}
	}

}
