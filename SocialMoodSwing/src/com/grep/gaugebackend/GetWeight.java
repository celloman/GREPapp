package com.grep.gaugebackend;

import java.util.concurrent.BlockingQueue;
import java.util.ArrayDeque;

public class GetWeight implements Runnable {
	
	protected BlockingQueue<Tweet> in_queue = null;
	protected BlockingQueue<Tweet> out_queue = null;
	protected String[] keywords = null;
	protected ArrayDeque<Long> seen_ids = new ArrayDeque<Long>();
	
	private void save_id(long id)
	{
		if(this.seen_ids.size() < 100)
			this.seen_ids.addFirst(id);
		else
		{
			this.seen_ids.removeLast();
			this.seen_ids.addFirst(id);
		}
	}
	
	public GetWeight(BlockingQueue<Tweet> in_queue, BlockingQueue<Tweet> out_queue, String[] keywords) {
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
				
				// make sure we only get english
				if(!t.lang.equals("en"))
					continue;
				
				// check for repeated tweets
				if(seen_ids.contains(t.id))
					continue;
				
				// check to make sure it just has one keyword
				int keyword_count = 0;
				for(int i = 0; i < this.keywords.length - 1; i++)
				{
					if(t.text.contains(keywords[i]))
					{
						keyword_count++;
						t.keyword = keywords[i];
					}
				}
				if(keyword_count != 1)
					continue;
				
				// weighting algorithm
				if(t.is_retweet)
				{
					if(seen_ids.contains(t.original_id))
					{
						if(t.retweets > 0)
						{
							t.retweets--;
							save_id(t.id);
						}
						else
						{
							t.followers -= 208;
						}
					}
					else
					{
						save_id(t.id);
					}
				}
				else
				{
					save_id(t.id);
				}
				
				t.weight = t.followers + (208 * t.retweets);
		
				this.out_queue.put(t);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}

}
