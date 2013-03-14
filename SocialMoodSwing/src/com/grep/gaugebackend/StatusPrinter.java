package com.grep.gaugebackend;

import java.util.concurrent.BlockingQueue;

public class StatusPrinter implements Runnable {
	
	protected BlockingQueue<Tweet> queue = null;
	
	public StatusPrinter(BlockingQueue<Tweet> queue) {
		this.queue = queue;
	}
	
	public void run()
	{	
		while(!Thread.currentThread().isInterrupted())
		{
			System.out.println("printing thread running...");
			try {
				Tweet t = this.queue.take();
				System.out.println(t.keyword + " " + t.sentiment + " " + t.weight + " " + t.text);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
