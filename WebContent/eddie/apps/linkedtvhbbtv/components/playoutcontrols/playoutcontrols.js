/* 
* playoutcontrols.js
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


var Playoutcontrols = function(options){
	var self = {};

	function initialize(){
		settings.project = eddie.getComponent("project");
		listen();
		resize();
		createComponents();
	}
	
	function createComponents(){
		//Create the play button, with a play icon. 
		settings.playButton.button({
			icons: {
				'primary': 'ui-icon-play'
			}
		});
		
		//Set playing to false, after initializing a video is never playing. 
		settings.playButton.on('click', playButtonClicked);
	}
	
	function playButtonClicked(){
		var icons = settings.playButton.button( "option", "icons" );
		if(icons['primary'] == 'ui-icon-play'){
			eddie.putLou('', 'start()');
		}else{
			eddie.putLou('', 'stop()');
		}
	}
	
	self.putMsg = function(msg){
		var command = $(msg.target).attr('id');
		var content = msg.content;
		var args = content.split(",");
		if(typeof self["_" + command] == "function"){
			self["_" + command].apply(this, args);
		}else{
			console.log("Message not handled!");
		}
	}
	
	self._started = function(){
		settings.playButton.button( "option", "icons", { primary: "ui-icon-pause" } );
	}
	
	self._stopped = function(){
		settings.playButton.button( "option", "icons", { primary: "ui-icon-play" } );
	}
	
	initialize();
	
	return self;
}
	