/* 
* sliderevents.js
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


springfield_function Sliderevents(options){

	var self = {};
	var settings = {
		'startdragx': 0
	};
	$.extend(settings, options);

	self.putMsg = function(msg){
		try{
			var command = msg.target[0].class;
		}catch(e){
			command = $(msg.target).attr('class');
		}
		var content = msg.content;
		switch(command) { 
		}
	}

	self.sliderHandler = function(event){
		var target = $(event.currentTarget);
		var position = $(target).offset();
		if (components.tablet.indrag()) return;
		//edited
		switch(event.type) {
			case 'dragstart':
				settings.startdragx = position.left;
				break;
			case 'drag':
				var newx = settings.startdragx + event.gesture.deltaX;
				if (newx>34) newx = 34;
				$(target).animate({left:newx+'px'},0,function() {});
				break;
			case 'swipe':
				var speed = event.gesture.velocityX;
				var time = 100*speed;
				var movedistance  = speed * time;
				var newx = (event.gesture.direction=='left') ? position.left-movedistance : (position.left+movedistance>34) ? 34 : position.left+movedistance;
				$(target).animate({left:newx+'px'},time ,function() {});
				break;
		}
	}

	self.sliderHeaderHandler = function(event){
		var target = event.currentTarget;
		if(event.type=='swipe'){
			if(event.gesture.direction=='right'){
				$(target).animate({left:'0px'},400,function() {});
			} else if(event.gesture.direction=='left'){	
				$(target).animate({left:'-565px'},400,function() {});
			}
		}
	}
	
	self.addDragEvents = function(id) {
		$('.sliderblock.'+ id).unbind();
		$('.sliderblock.'+ id).hammer({
			prevent_default: true,
			drag_vertical: true,
			drag_min_distance: 40
		}).bind('hold dragstart dragend drag doubletap tap', function(event){
			switch(event.type){
				case 'hold':
			 		var position = $ (event.currentTarget).offset();
				 	$('#dragblock').css({"visibility": "visible","left": position.left,"top": position.top})
				 		.html($('#'+event.currentTarget.id).html());
					self.externalDrag(event);
					break;
				case "doubletap":
					if (components.tablet.getSettings().infoscreenopen==true) return;
					if ($(event.currentarget).hasClass('chapter')) return;
					components.tablet.getSettings().infoscreenopen=true;
					var	uid = $('#'+event.currentTarget.id).attr("data-uid");
					components.tablet.getSettings().activeinfoscreen = event.currentTarget.id+","+uid;
					var entity = $(this).find('p').text();
					var image = $(this).find('img').attr('src');
					var description = $("#description_"+event.currentTarget.id).text();					
					var orientation = (window.innerHeight > window.innerWidth) ? "portrait" : "landscape";
					
					eddie.putLou("","loadblockdata("+event.currentTarget.id+","+uid+","+orientation+","+entity+","+image+","+description+")");	
					$('#infoscreen').css({"border-top" : "4px solid #555555",
						"border-bottom" : "4px solid #555555",
						"visibility": "visible",
						"opacity": "0"
					}).animate({opacity:'1'},400,function() { components.tablet.animDone("openinfoscreen"); });
					$('#infoscreen').attr("data-uid", uid);
					$('#infoscreen').attr("data-timestamp", new Date().getTime());
					break;
				case "dragend":
					self.externalDrag(event);
					break;
				case "drag":	
					self.externalDrag(event);
					break;	
			}
		});
	}

	self.externalDrag = function(event){
		var tid = event.currentTarget.id;
		var settings = components.tablet.getSettings();
		switch(event.type){
			case 'hold':
				var position = $('#dragblock').position();
				$('#dragblock').attr("data-time", $('#'+tid).attr("data-time"));
				$('#dragblock').attr("data-referid", $('#'+tid).attr("data-referid"));
				settings.startdragx = position.left;
				settings.startdragy = position.top;
				settings.indrag = true;
				break;
			case 'dragend':
				if (settings.indrag==true) {
					settings.indrag = false;
					$('#dragblock').css({"visibility": "hidden"});
					if (settings.dropareaactive==true) {
						$('#droparea').animate({top:'-70px'},300,function() { components.tablet.animDone("dropareadone"); });
						settings.dropareaactive = false;
						var	timepoint = $('#'+tid).attr("data-time");
						var	referid = $('#'+tid).attr("data-referid");
						if (settings.droptarget=="mainscreens") {
							eddie.putLou('video', 'seek(' + timepoint + ')');
							eddie.putLou('hbbtvvideo', 'seek(' + timepoint + ')');
						} else if (settings.droptarget=="bookmarks") {
							eddie.putLou('', 'bookmark('+timepoint+','+tid+','+referid+')');
						} else {
							eddie.putLou('', 'share('+timepoint+','+tid+','+referid+')');	
						}
						//window.components.tablet.localconsole("drop="+settings.droptarget+" tid="+tid+" TIME="+timepoint);	
					}
				} 
				break;
			case 'drag':
				if (settings.indrag== true) {	
					var newx = settings.startdragx + event.gesture.deltaX;
					var newy = settings.startdragy + event.gesture.deltaY;
					$('#dragblock').animate({left:newx+'px', top: newy+'px'},0,function() {});
					if (newy<70 && settings.dropareaactive==false) {
						$('#droparea').animate({top:'0px'},300,function() { components.tablet.animDone("opendroparea"); });
						settings.dropareaactive = true;
					} 
					if (newy>160 && settings.dropareaactive==true) {
						$('#droparea').animate({top:'-70px'},300,function() { components.tablet.animDone("closedroparea"); });
						settings.dropareaactive = false;
					}
					if (newx<220) {
						settings.droptarget = "bookmarks";
						$('#droparea #dropbookmarks').css({"background": "#0098DF"});
						$('#droparea #dropmainscreens').css({"background": "#993C29"});
						$('#droparea #dropmainshares').css({"background": "#258D35"});
					} else if (newx>600) {
						$('#droparea #dropshares').css({"background": "#3FD863"});
						$('#droparea #dropbookmarks').css({"background": "#1E35D9"});
						$('#droparea #dropmainscreens').css({"background": "#993C29"});
						settings.droptarget = "shares";
					} else {
						$('#droparea #dropmainscreens').css({"background": "#FF7A54"});
						$('#droparea #dropbookmarks').css({"background": "#1E35D9"});
						$('#droparea #dropshares').css({"background": "#258D35"});
						settings.droptarget = "mainscreens";
					}
				}
				break;
		}
	}

	return self;
}