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


/*********************************************************************************************
 * from hbbtv.js
 * 
 * *******************************************************************************************/

/* global definitions */
var apps;
var currentApp;
var remoteControlKeys;
var video;

/*initialize the start site*/
function init() {
	document.getElementById("red_button").focus();

    try {
        var appManager = document.getElementById("applicationManager");
        currentApp = appManager.getOwnerApplication(document);
        currentApp.show();

        remoteControlKeys = currentApp.privateData.keyset;
        remoteControlKeys.setValue(remoteControlKeys.RED);
    }
    catch (e) {
        //document.getElementById('info').innerHTML = 'no application manager available';
    }

    apps = document.getElementById("apps");
    document.addEventListener("keydown", handleKeyCodes, false);
}

/*initialize the player site*/
function initPlayer() {
	video = document.getElementById("video_1");
	video.play(1);
	/*  check the simple version first; 
    try {
        var appManager = document.getElementById("applicationManager");
        currentApp = appManager.getOwnerApplication(document);
        currentApp.show();

        remoteControlKeys = currentApp.privateData.keyset;
        remoteControlKeys.setValue(remoteControlKeys.NAVIGATION + remoteControlKeys.PVR);

        setInterval('checkPlayState()', 700);
        video.play(1);
    }
    catch (e) {
        //document.getElementById('info').innerHTML = 'no application manager available';
    }
    */
    document.getElementById("back").focus();
	document.addEventListener("keydown", handleKeyCodes, false);
}

/*handle keycodes on hbbtv and pc browsers*/
function handleKeyCodes(e) {
	if (typeof (VK_RED) == "undefined") {
       var VK_RED      = KeyEvent.VK_RED || e.VK_RED || 403 || 116;
       var VK_GREEN    = KeyEvent.VK_GREEN || e.VK_GREEN || 404 || 117;
       var VK_YELLOW   = KeyEvent.VK_YELLOW || e.VK_YELLOW || 405 || 118;
       var VK_BLUE     = KeyEvent.VK_BLUE || e.VK_BLUE || 406 || 119;

       var VK_LEFT     = KeyEvent.VK_LEFT || e.VK_LEFT || 37;
       var VK_UP       = KeyEvent.VK_UP || e.VK_UP || 38;
       var VK_RIGHT    = KeyEvent.VK_RIGHT || e.VK_RIGHT || 39;
       var VK_DOWN     = KeyEvent.VK_DOWN || e.VK_DOWN || 40;
       var VK_ENTER    = KeyEvent.VK_ENTER || e.VK_ENTER || 13;

       var VK_0        = KeyEvent.VK_0 || e.VK_0 || 48;
       var VK_1        = KeyEvent.VK_1 || e.VK_1 || 49;
       var VK_2        = KeyEvent.VK_2 || e.VK_2 || 50;
       var VK_3        = KeyEvent.VK_3 || e.VK_3 || 51;
       var VK_4        = KeyEvent.VK_4 || e.VK_4 || 52;
       var VK_5        = KeyEvent.VK_5 || e.VK_5 || 53;
       var VK_6        = KeyEvent.VK_6 || e.VK_6 || 54;
       var VK_7        = KeyEvent.VK_7 || e.VK_7 || 55;
       var VK_8        = KeyEvent.VK_8 || e.VK_8 || 56;
       var VK_9        = KeyEvent.VK_9 || e.VK_9 || 57;

       var VK_PLAY          = KeyEvent.VK_PLAY || e.VK_PLAY || 415 || 80;
       var VK_PAUSE         = KeyEvent.VK_PAUSE || e.VK_PAUSE || 414 || 81;
       var VK_STOP          = KeyEvent.VK_STOP || e.VK_STOP || 413 || 83;
       var VK_FAST_FWD      = KeyEvent.VK_FAST_FWD || e.VK_FAST_FWD || 417;
       var VK_REWIND        = KeyEvent.VK_REWIND || e.VK_REWIND || 412;
       var VK_BACK          = KeyEvent.VK_BACK || e.VK_BACK || 461 || 166;

       var VK_TELETEXT      = KeyEvent.VK_TELETEXT || e.VK_TELETEXT || 459;
	}
	switch (e.keyCode) {
		// e.VK_RED or KeyEvent.VK_RED for the remote control, 116 = F5 on common keyboards
		case e.VK_RED: case KeyEvent.VK_RED: case 116:
			//Red Button pressed: now an external hbbtv application could be created using currentApp.createApplication('www.example.de/new_hbbtv_app.html', false),
			//or just show hidden parts of this application.
            document.getElementById("red_button").focus();
			toggleApps();
			break;
		case e.VK_GREEN: case KeyEvent.VK_GREEN: case 117:
            document.getElementById("green_button").focus();
			alert("Green Button pressed");
			break;
		case KeyEvent.VK_YELLOW: case e.VK_YELLOW: case 118:
            document.getElementById("yellow_button").focus();
			alert("Yellow Button pressed");
			break;
		case KeyEvent.VK_BLUE: case e.VK_BLUE: case 119:
            document.getElementById("blue_button").focus();
            window.location.replace("videoPlayer.html");
			break;
        case KeyEvent.VK_PLAY: case e.VK_PLAY: case 80:
            playVideo();
            break;
        case KeyEvent.VK_PAUSE: case e.VK_PAUSE: case 81:
            pauseVideo();
			break;
        case KeyEvent.VK_STOP: case e.VK_STOP: case 83:
            stopVideo();
			break;
		case KeyEvent.VK_BACK: case e.VK_BACK: case 8:
			//Back Button pressed, now you can go back in the history or destroy the application using currentApp.destroyApplication();
            window.history.back();
			try {
                currentApp.hide();
			    remoteControlKeys.setValue(remoteControlKeys.RED);
			} catch (e) {
				//document.getElementById('info').innerHTML = 'no application manager available';
			}
		default:
			return;
	}
	// turn off browser default behaviour
	e.preventDefault();
}

/*check playstate of the video*/
function checkPlayState() {
    var playState = video.playState;
    var currentState = "no playState available in this browser";
    var currentError = "";

    switch (playState) {
        case 0: // stopped
            document.getElementById('stop').focus();
            currentState = '0 - stopped; the current media pointed to by data has stopped playback';
            break;
        case 1: // playing
            document.getElementById('play').focus();
            currentState = '1 - playing; the current media pointed to by data is currently playing';
            break;
        case 2: // paused
            document.getElementById('pause').focus();
            currentState = '2 - paused; the current media pointed to by data has been paused';
            break;
        case 3: // connecting
            currentState = '3 - connecting; connecting to media server';
        case 4: // buffering
            currentState = '4 - buffering; the media is being buffered before playback';
            break;
        case 5: // finished
            document.getElementById('back').focus();
            currentState = '5 - finished; the playback of the current media has finished';
            break;
        case 6: // error
            stopVideo();
            document.getElementById('back').focus();
            currentState = '6 - error; an error occurred during media playback: ';
            switch (video.error) {
                case 0:
                    currentError = '0 - A/V format not supported';
                    break;
                case 1:
                    currentError = '1 - cannot connect to server or connection lost';
                    break;
                case 2:
                    currentError = '2 - unidentified error';
                    break;
            }
            break;
        default:
            // do nothing
            break;
    }

    document.getElementById('info').innerHTML = currentState + currentError;
}

/*toggle between red button / all color buttons*/
function toggleApps() {
	if (apps.style.visibility == 'hidden' || apps.style.visibility == '') {
        try {
            remoteControlKeys.setValue(remoteControlKeys.RED + remoteControlKeys.GREEN + remoteControlKeys.YELLOW + remoteControlKeys.BLUE);
        }
        catch (e) {
            //document.getElementById('info').innerHTML = 'no application manager available';
        }
		apps.style.visibility = 'visible';
	} 
	else {
        try {
			remoteControlKeys.setValue(remoteControlKeys.RED);
        }
        catch (e) {
            //document.getElementById('info').innerHTML = 'no application manager available';
        }
		apps.style.visibility = 'hidden';
	}	 
}

/*AV Control embedded object -> play()*/
function playVideo() {
	eddie.putLou("hbbtvvideo", "play()");
    /*document.getElementById('play').focus();
    try {
        video.play(1);
    }
    catch(e) {
        document.getElementById('info').innerHTML = 'video.play(1) not available in this browser';
    }*/
}

/*AV Control embedded object -> pause()*/
function pauseVideo() {
	eddie.putLou("hbbtvvideo", "pause()");
	/*document.getElementById('pause').focus();
    try {
        video.play(0);
    }
    catch(e) {
        document.getElementById('info').innerHTML = 'video.play(0) not available in this browser';
    }*/
}

/*AV Control embedded object -> stop()*/
function stopVideo() {
	eddie.putLou("hbbtvvideo", "pause()");
	eddie.putLou("hbbtvvideo", "seek(0)");
	/*document.getElementById('stop').focus();
    try {
        video.stop();
    }
    catch(e) {
        document.getElementById('info').innerHTML = 'video.stop() not available in this browser';
    }*/
}
	
/*************************************************************************************************
 * orig eddie parts
 * @param options
 * @returns {___anonymous_self}
 */
function Hbbtvvideo(options) {
	self = {};
	var settings = {};
	$.extend(settings, options);
	setInterval((function(){var position = isNaN(document.getElementById("video_1").playPosition) ? 0 : document.getElementById("video_1").playPosition; var duration = isNaN(document.getElementById("video_1").playTime) ? 0 : document.getElementById("video_1").playTime; eddie.putLou('','timeupdate('+Math.floor(position/1000)+':'+Math.floor(duration/1000)+')');}), 1000);

	self.putMsg = function(msg) {
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
				case 'qrcode':
					 video_toggleQRCode();
					break;
				case 'setVideo':
					self.setVideo(content);
					break;
				default:
					alert('unhandled msg in hbbtvvideo.html : '+msg+' ('+command+','+content+')'); 
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
		document.getElementById("video_1").play(1);
		var position = isNaN(document.getElementById("video_1").playPosition) ? 0 : document.getElementById("video_1").playPosition;
		eddie.putLou('', 'started('+position+')');
		eddie.putLou('notification','show(play)');
	}

	self.handlePause = function() {
		document.getElementById("video_1").play(0);
		var position = isNaN(document.getElementById("video_1").playPosition) ? 0 : document.getElementById("video_1").playPosition;
		eddie.putLou('', 'paused('+position+')');
		eddie.putLou('notification','show(pause)');
	}

	self.handleSeek = function(content) {
		var ms = parseFloat(content)*1000;
		var time = parseInt(ms);
		if (time>1) time = time - 1 ;
		document.getElementById("video_1").seek(time);
		eddie.putLou('notification','show(seek '+Math.floor(time)+')');
	}
	
	self.setVideo = function(video) {
		$("#video_1").attr("data", video);		
	}

	handleButtonClick = function(content){
			console.log("action: " + content);
			switch (content){
				case '1': 
					eddie.putLou("hbbtvvideo", "url(avro)");
					break;
				case '2':
					eddie.putLou("hbbtvvideo", "url(rbb)");
					break;
				case '3':
					eddie.putLou("hbbtvvideo", "url(t1)");
					break;
				case '4':
					eddie.putLou("hbbtvvideo", "url(t2)");
					break;
				case '5':
					eddie.putLou("hbbtvvideo", "url(remix)");
				case 'pause':
					eddie.putLou("hbbtvvideo", "pause()");
					break;
				case 'play':
					eddie.putLou("hbbtvvideo", "play()");
					break;
				case 'stop':
					eddie.putLou("hbbtvvideo", "pause()");
					eddie.putLou("hbbtvvideo", "seek(0)");
					break;
				case 'volumeup':
					eddie.putLou("hbbtvvideo", "volumeup()");
					break;
				case 'volumedown':
					eddie.putLou("hbbtvvideo", "volumedown()");
					break;
				case 'qrcode':
					eddie.putLou("hbbtvvideo", "qrcode(toggle)");
					break;
				case 'reverse':
					eddie.putLou("hbbtvvideo", "speed(-0.5)");
					break;
				case 'forward':
					eddie.putLou("hbbtvvideo", "speed(2)");
					break;
				case 'eject':
					eddie.putLou("hbbtvvideo", "qrcode(toggle)");
					break;
		}
	}	
	return self;
}