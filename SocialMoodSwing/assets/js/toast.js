max_toasts = 3;

function makeToast(type, message, handle, followers, retweets, sentiment){
	var html = "<div class='toast "+type+"'>"+message+"</div>";
	$('#toast-container').append($(html));
	
	var $toasts = $('.toast');
	
	var extra = $toasts.length - max_toasts;
	if(extra < 0) extra = 0;
	
	$toasts.slice(0, extra).fadeOut('normal', function(){
		$(this).remove();
	});
	
	var $newToast = $toasts.last();
	
	$newToast.hide().delay(500).fadeIn();
	
	if(handle != undefined){
		$newToast.click(function(){
			makeModal(handle, message, followers, retweets, sentiment);
		});
	}
	
	setTimeout(function() {
		$newToast.fadeOut('normal', function(){
			$(this).remove();
		});
	}, 7000 );
}

function makeModal(handle, tweet, followers, retweets, sentiment){
	var mood = "";
	if(sentiment > 0) mood = "<span class='sentiment positive'>positive</span>";
	if(sentiment === 0) mood = "<span class='sentiment neutral'>neutral</span>";
	if(sentiment < 0) mood = "<span class='sentiment negative'>negative</span>";
	
	var html = "<span class='handle'>@"+handle+"</span>"+
			"<div class='tweet-content'>"+tweet+"</div>"+
			"<div>Followers: <span class='followers'>"+followers+"</span></div>"+
			"<div>Retweets: <span class='retweets'>"+retweets+"</span></div>"+
			"<div>Sentiment: "+mood+"</div>";
	
	$('#modal-content').html(html);
	$('.modal').fadeIn();
}