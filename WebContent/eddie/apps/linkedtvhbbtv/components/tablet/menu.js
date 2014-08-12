/* 
* menu.js
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


$('.screensbutton').hammer({
	prevent_default: true,
	drag_vertical: true,
	drag_min_distance: 40
}).bind('tap', function(event){
	if(event.type=='tap'){	
 		var con = event.target.id;
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
});