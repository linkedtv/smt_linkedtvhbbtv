/* 
* logo.js
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


var Logo = function(options){
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
				case 'broadcaster':
					self.setbroadcaster(content);
					break;
				default:
				console.log('unhandled msg in notification.html : '+msg); 
			}
		}
	}
	
	self.setbroadcaster = function(line) {
		$('#broadcaster_logo').html("<img src=\"/eddie/img/watermarks/"+line+".png\" />");
	}

	$('#broadcaster_logo').css('z-index', 9999);

	return self;
}