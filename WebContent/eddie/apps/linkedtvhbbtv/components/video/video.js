/* 
* video.js
* 
* Copyright (c) 2013 Noterik B.V.
* 
* This file is part of smt_trafficlightoneapp, an app for the multiscreen toolkit 
* related to the Noterik Springfield project.
*
* smt_trafficlightoneapp is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* smt_trafficlightoneapp is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with smt_trafficlightoneapp.  If not, see <http://www.gnu.org/licenses/>.
*/


function Video(options) {
	self = {};
	var settings = {};
	$.extend(settings, options);
	var myPlayer = document.getElementById("video_1");
	setInterval((function(){eddie.putLou('','timeupdate('+Math.floor(myPlayer.currentTime)+':'+Math.floor(myPlayer.duration)+')');}), 1000);

	self.putMsg = function(msg) {
	    var myPlayer = document.getElementById("example_video_1");
		try{
			var command = [msg.target[0].class];
		}catch(e){
			command = $(msg.currentTarget).attr('class').split(" ");
		}
		var content = msg.content;
		for(var i=0; i<command.length; i++) {
			switch(command[i]) { 
				case 'play':
					self.handlePlay();
					break;
				case 'pause':
		            self.handlePause();
					break;
				case 'seek':
					self.handleSeek(content);
					break;
				case 'mute':
					myPlayer.volume=0;
					break;
				case 'volumeup':
					myPlayer.volume+=0.1;
					break;
		        case 'volumedown':
		            myPlayer.volume-=0.1;
		            break;
		        case 'volume':
		            myPlayer.volume=eval(content);
		            break;
				case 'speed':
					self.handleSpeed(content);
		            break;
				case 'request_videosrc':
					eddie.putLou('controller','videosrc('+window.videosrc+')');
					break;
				case 'qrcode':
					 video_toggleQRCode();
					break;
				case 'notify':
					break;
				case 'buttonClicked':
					console.log('buttonClicked');
					handleButtonClick(content);
					break;
				case 'setVideo':
					self.setVideo(content);
				case 'setPoster':
					self.setPoster(content);
				default:
					//alert('unhandled msg in video.html : '+msg+' ('+command+','+content+')'); 
			}
		}
	}
	
	self.toggleQRCode = function() {
		if (window.qrcode!='true') {
			eddie.putLou('qrcode','visible(true)'); 
			window.qrcode='true';
		} else {
			eddie.putLou('qrcode','visible(false)'); 
			window.qrcode='false';
		}
	}
	
	self.handlePlay = function() {
		myPlayer.volume=1;
        myPlayer.playbackRate=1;
        myPlayer.play();
		eddie.putLou('notification','show(play)');
	}

	self.handlePause = function() {
		myPlayer.pause();
		eddie.putLou('notification','show(pause)');
	}

	self.handleSeek = function(content) {
		var time = eval(content);
		if (time>1) time = time - 1 ;
		myPlayer.currentTime = time;
		eddie.putLou('notification','show(seek '+Math.floor(time)+')');
	}

	self.handleSpeed = function(content){
		myPlayer.volume=0;
		if (content=="" || content=="1") {
			content = "1";
			myPlayer.volume=1;
		}
        myPlayer.playbackRate=eval(content);
	}
	
	self.setVideo = function(video) {
		$("#src1").attr("src", video);		
	}
	
	self.setPoster = function(poster) {
		$("#video_1").attr("poster", poster);
	}

	handleButtonClick = function(content){
			console.log("action: " + content);
			switch (content){
				case '1': 
					eddie.putLou("video", "url(avro)");
					break;
				case '2':
					eddie.putLou("video", "url(rbb)");
					break;
				case '3':
					eddie.putLou("video", "url(t1)");
					break;
				case '4':
					eddie.putLou("video", "url(t2)");
					break;
				case '5':
					eddie.putLou("video", "url(remix)");
				case 'pause':
					eddie.putLou("video", "pause()");
					break;
				case 'play':
					eddie.putLou("video", "play()");
					break;
				case 'stop':
					eddie.putLou("video", "pause()");
					eddie.putLou("video", "seek(0)")
					break;
				case 'volumeup':
					eddie.putLou("video", "volumeup()");
					break;
				case 'volumedown':
					eddie.putLou("video", "volumedown()");
					break;
				case 'qrcode':
					eddie.putLou("video", "qrcode(toggle)");
					break;
				case 'reverse':
					eddie.putLou("video", "speed(-0.5)");
					break;
				case 'forward':
					eddie.putLou("video", "speed(2)");
					break;
				case 'eject':
					eddie.putLou("video", "qrcode(toggle)");
					break;
		}
	}
	
	return self;
}