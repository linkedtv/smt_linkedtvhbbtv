/* 
* fakeuserevents.js
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


$('.fakeuser').hammer({
	prevent_default: false,
	drag_vertical: false,
	drag_min_distance: 40
}).bind('tap', function(event){
	if(event.type=='tap'){
 		var con = event.currentTarget.id;
 		$('#loginoverlay').css({display: "none"});
 		if (components.tablet.getSettings().lasttap==con) return;
 		components.tablet.getSettings().lasttap=con;
 		
		if (con=="user_bert") { eddie.putLou("login", "login(bert,bert12)");}
		if (con=="user_anne") { eddie.putLou("login", "login(anne,anne12)");}
		if (con=="user_ralph") { eddie.putLou("login", "login(ralph,ralph12)");}
		if (con=="user_nina") { eddie.putLou("login", "login(nina,nina12)");}
		
		// ugly but o well
		$('#toplogin_img').attr('src','/eddie/apps/linkedtvhbbtv/img/people/'+con.substring(5)+'.png');
		$('#toplogin').css({opacity: "0.9"});
	}
});