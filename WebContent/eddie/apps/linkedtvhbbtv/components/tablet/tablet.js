/* 
* tablet.js
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


springfield_function Tablet(options){
	var self = {};
	var settings = {
		'lastevent': 0,
		'startdragx': 0,
		'startdragy': 0,
		'lastscreen': "screens_episode",
		'infoscreenopen': false,
		'activeinfoscreen': "",
		'lasttap': "",
		'indrag': false,
		'droptarget': "none",
		'dropareaactive': false
	};
	$.extend(settings, options);

	self.putMsg = springfield_function(msg){
		try{
			var command = [msg.target[0].class];
		}catch(e){
			command = $(msg.currentTarget).attr('class').split(" ");
		}
		var content = msg.content;
		for(i=0;i<command.length;i++){
			switch(command[i]) { 
				case "openapps":
					$('#content').animate({bottom:'140px'},300,function() { self.animDone("openapps"); });
	  				break;
	  			case "closeapps":
					$('#content').animate({bottom:'60px'},300,function() { self.animDone("content"); });
	  				break;
	  	  		case "menuhtml":
	  				handleMenu(content);
	  				break;
	  	  		case "fakeusershtml":
	  				$('#loginoverlay').html(content);
	  				break;
	  			case "button":
	  				handleButtonClick(msg);
	  				break;
				case "dragblock":
					handleDragBlock(msg);
					break;
				case "tablet":
					break;
				case "screensbutton":
					handleScreenSelection(msg);
					break;
				default:
					// console.log('unhandled msg in tablet.html : '+command); 
			}
		}
	}
	
	eddie.putLou("","loadscreen(screens_episode)");	
	
	self.animDone = function(reason){
		if (reason=="closeinfoscreen") {
			$('#infoscreen').css({visibility: "hidden"});
		}
	}

	self.getSettings = function(){
		return settings;
	}

	self.indrag = function(){
		return settings.indrag;
	}

	handleMenu = function(content){
		$('#screens').html(content);
		$('#topname').html("Related");
		$('#screens_episode').css({color: "#1EC9F3"});
	}

	handleScreenSelection = function(event){
		var con = event.currentTarget.id;
 		$('script#script_content').remove();
		$('#content').animate({left:'0px'},250,function() { });
		$('#screens').animate({left:'-250px'},250,function() { });
		$('#screens_join,#screens_overview,#screens_episode,#screens_social,#screens_bookmarks').css({color: "#bbbbbb"});
					
		if (con=="screens_join") { $('#screens_join').css({color: "#1EC9F3"});$('#topname').html("Channels"); }
		if (con=="screens_overview") { $('#screens_overview').css({color: "#1EC9F3"});$('#topname').html("Overview"); }
		if (con=="screens_episode") { $('#screens_episode').css({color: "#1EC9F3"});$('#topname').html("Related"); }
		if (con=="screens_social") { $('#screens_social').css({color: "#1EC9F3"});$('#topname').html("Social"); }	
		if (con=="screens_bookmarks") { $('#screens_bookmarks').css({color: "#1EC9F3"});$('#topname').html("Bookmarks"); }
		
		if (components.tablet.getSettings().lastscreen!=con) {
			components.tablet.getSettings().lastscreen = con;
			eddie.putLou("","loadscreen("+con+")");	
		}
	}

	handleButtonClick = function(event){
		var button = $(event.currentTarget).attr('id');
		switch(button){
			case 'toplogin':
				$('#loginoverlay').css({"display": "inherit"});
				eddie.putLou("","loadfakeusers()");
				break;
			case 'addmore':
				eddie.putLou("notification","show(add more is not implemented yet)");
				break;
			case 'tophelp':
				eddie.putLou("notification","show(help is not implemented yet)");
				break;
			case 'topsettings':
				eddie.putLou("notification","show(settings is not implemented yet)");
				break;
			case 'topmenu':
				showMenu(event);
				break;
			case "content":
				handleContent(event);
				break;
			case "infoscreen":
				handleInfoScreen(event);
				break;
		}
	}
	
	self.localconsole = function(line){
	  	$('#localconsole').html(line);
	}

	showMenu = function(event){
		if(event.type=='tap'){
			var vleft = $('#content').css("left");
			if (vleft=="0px") {
				$('#screens').show().animate({left:'0px'},250,function() { });
				$('#content').animate({left:'250px'},250,function() { });
				
			} else {
				$('#content').animate({left:'0px'},250,function() { });
				$('#screens').animate({left:'-250px'},250,function() { }).hide(1);
			}
		}
	}

	handleContent = function(event){
		if(event.gesture.direction=='up' && event.type=='swipe'){}
		else if(event.gesture.direction=='down' && event.type=='swipe'){} 
	}

	handleDragBlock = function(event){
		if(event.type=='drag' || event.type=='dragend') components.sliderevents.externalDrag(event);
	}

	handleInfoScreen = function(event){
		if (event.type=="hold") {
	 		var position = $ ('#'+event.currentTarget.id).position();
		 	$('#infoscreen').css({"visibility": "hidden"});
		 	$('#dragblock').animate({visibility: "visible",left: position.left,top: position.top},0,function() { });
			components.sliderevents.externalDrag(event.currentTarget.id,"dragstart",0,0);
		} else if(event.type=='doubletap'){
			if(components.tablet.getSettings().infoscreenopen==false) return;
			components.tablet.getSettings().infoscreenopen=false;
			$('#infoscreen').animate({opacity:'0'},400,function() { components.tablet.animDone("closeinfoscreen"); });
		}
	}
	
	window.addEventListener("orientationchange", function() {
			console.log("CHANGING ORIENTATION: "+window.orientation);
			var currentBlockId = settings.activeinfoscreen.substring(0, settings.activeinfoscreen.indexOf(","));
			var entity = $("#"+currentBlockId).find('p').text();
			var image = $("#"+currentBlockId).find('img').attr('src');
			var description = $("#description_"+currentBlockId).text();
			var orientation = (window.innerHeight > window.innerWidth) ? "portrait" : "landscape";
			eddie.putLou("","loadblockdata("+settings.activeinfoscreen+","+orientation+","+entity+","+image+","+description+")");	
	}, false);

	return self;
}