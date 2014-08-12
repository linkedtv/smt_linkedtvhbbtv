/* 
* tablet_gestures.js
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


$('.topbutton,#dragblock,#content,#infoscreen,#tablet').hammer({
	prevent_default: true,
	drag_vertical: true,
	drag_min_distance: 40
}).bind('swipe drag dragstart dragend tap doubletap hold', function(event){
	if($(this).attr('id')=='tablet') return;
	if($(this).attr('class')=='topbutton'){
		switch($(this).attr('id')){
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
				if(event.type=='tap'){
					var vleft = $('#content').css("left");
					if (vleft=="0px") {
						$('#screens').show().animate({left:'0px'},250,function() { });
						$('#content').animate({left:'250px'},250,function() { });
						
					} else {
						$('#content').animate({left:'0px'},250,function() { });
						$('#screens').animate({left:'-250px'},250,function() { }).hide(250);
					}
				}
				break;
			case 'apps':
				break;
		}
	}
	else{
		switch($(this).attr('id')){
			case "content":
				if(event.direction=='up' && event.type=='swipe'){}
				else if(event.direction=='down' && event.type=='swipe'){} 
				break;
			case "dragblock":
				if(event.type=='dragstart') {
					var position = $ ('#dragblock').position();
					components.tablet.getSettings().startdragx = position.left;
					components.tablet.getSettings().startdragy = position.top;
				} else if (event.type=='drag') {	
					var newx = components.tablet.getSettings().startdragx + event.distanceX;
					var newy = components.tablet.getSettings().startdragy + event.distanceY;
					$('#dragblock').animate({left: newx+"px", top: newy+"px"},0,function() { });
				}
				break;
			case "infoscreen":
				if (event.type=="hold") {
			 		var position = $ ('#'+event.target.id).position();
				 	$('#infoscreen').css({"visibility": "hidden"});
				 	$('#dragblock').animate({visibility: "visible",left: position.left,top: position.top},0,function() { });
				 	console.log("the problem");
					components.sliderevents.externalDrag(event.target.id,"dragstart",0,0);
					console.log("is here");
				} else if(event.type=='doubletap'){
					if(components.tablet.getSettings().infoscreenopen==false) return;
					components.tablet.getSettings().infoscreenopen=false;
					$('#infoscreen').animate({opacity:'0'},400,function() { components.tablet.animDone("closeinfoscreen"); });
				}
		}
	}		
});