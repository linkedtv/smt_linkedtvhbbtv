/* 
* notification.js
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


var Notification = function(options){
	var self = {};
	var settings = {
		
	}
	$.extend(settings, options);
	
	self.putMsg = function(msg){
		try{
			var command = [msg.target[0].class];
		}catch(e){
			command = $(msg.currentTarget).attr('class').split(" ");
		}
		var content = msg.content;
		for(i=0;i<command.length;i++){
			switch(command[i]) { 
				case 'show':
					self.show(content);
	  				break;
	  			case 'showlong':
	  				self.showLong(content);
	  				break;
	  			case 'closelong':
	  				self.closeLong();
	  				break;
				case 'login':
					$('#notificationshort #box').css({width: "#160px"});
					$('#notificationshort').html("<img src=\"/eddie/img/people/"+content+".png\" /><div id=\"box\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+content+" logged in</div>");

	    			$('#notificationshort').animate({top:'10px'},400,function() { self.animDone('in'); });
	  				break;
				case 'logout':
					$('#notificationshort #box').css({width: "#200px"});
					$('#notificationshort').html("<img src=\"/eddie/img/people/"+content+".png\" /><div id=\"box\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+content+" logged out</div>");
	    			$('#notificationshort').animate({top:'10px'},400,function() { self.animDone('in'); });
	  				break;
				case 'sound':
					self.makesound(content);
	  				break;
				default:
					console.log('unhandled msg in notification.html : '+msg); 
			}
		}
	}
	
	self.show = function(line) {
		$('#notificationshort').html(line);
        $('#notificationshort').animate({top:'80px'},400,function() { self.animDone('in'); });
	}

	self.showLong = function(line) {
		$('#notificationlong').html(line);
        $('#notificationlong').animate({top:'50px'},400,function() { });
	}

	self.closeLong = function(){
		$('#notificationlong').animate({top:'-40px'},400,function() {});
	}
	
	self.animDone = function(step) {
		if (step=='in') {
        	$('#notificationshort').animate({top:'80px'},1000,function() { self.animDone('out'); });
		} else if (step=='out') {
        	$('#notificationshort').animate({top:'-40px'},400,function() { self.animDone('done'); });
		}
	}
	
	self.makesound = function(sound) {
   		var audio = $('<audio />', {
   			autoPlay : 'autoplay'
   		});
    	$('<source>').attr('src', '/eddie/sounds/'+sound+'.mp3').appendTo(audio);
     	audio.appendTo('body');    
	}
	
	$('#notificationshort').css('z-index', 9999);
	$('#notificationlong').css('z-index', 9999);
	var shortid = eddie.getScreenId().substring(eddie.getScreenId().lastIndexOf("/")+1);
	eddie.putLou('notification','show(new screen '+ shortid + ')');
	
	return self;
}