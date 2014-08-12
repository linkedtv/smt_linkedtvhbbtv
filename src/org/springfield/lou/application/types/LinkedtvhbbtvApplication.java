/* 
* LinkedtvhbbtvApplication.java
* 
* Copyright (c) 2013 Noterik B.V.
* 
* This file is part of smt_demolinkedtvapp, an app for the multiscreen toolkit 
* related to the Noterik Springfield project.
*
* smt_demolinkedtvapp is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* smt_demolinkedtvapp is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with smt_demolinkedtvapp.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.springfield.lou.application.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springfield.lou.application.Html5Application;
import org.springfield.lou.application.components.BasicComponent;
import org.springfield.lou.application.components.ComponentInterface;
import org.springfield.lou.application.types.demolinkedtv.Slider;
import org.springfield.fs.*;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.lou.screen.Capabilities;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.tools.FsFileReader;
import org.springfield.lou.user.User;
import org.springfield.mojo.http.HttpHelper;
import org.springfield.mojo.http.Response;
import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;
import org.springfield.mojo.linkedtv.Channel;
import org.springfield.mojo.linkedtv.Episode;

/**
 * LinkedTV HbbTV application based on LinkedTV Demo Application.
 * The multiscreen application shows the video on the main TV screen 
 * while the second screen shows related information about the current 
 * moment in video divided into four slider layers, "who", "what", 
 * "where" and chapters. Users can select items from the seconds screen 
 * to read a short abstract about it or rotate their device to go to 
 * a website with more information.
 * Users can also push the entities to the screen, the video on the 
 * mainscreen then jumps to the attached moment in time.
 * Further more it's possible to login and share or bookmark entities
 * from the different layers.
 * 
 * @author Daniel Ockeloen, Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2013
 * @package org.springfield.lou.application.types
 * 
 */
public class LinkedtvhbbtvApplication extends Html5Application {
	
	private String presentationCollectionUri = "/domain/linkedtv/user/rbb/collection/default/presentation/249";
	// private String presentationCollectionUri = "/domain/linkedtv/user/rbb/collection/default/presentation/1621";
	private Presentation presentation = null;
	public FsTimeLine timeline = null;
	private enum sliders { whoslider,whatslider,whereslider,chapterslider,bookmarkslider,sharedslider, joinedslider; }
	private enum blocks { who,what,where,chapter; }
	private boolean curated = false;
	private int currentChapter = -1;
	private long currentTime = 0l;
	private String mediaresourceUUID = "";
	private boolean hbbtvMode = false; 
	private Episode e = null;
	
	public LinkedtvhbbtvApplication(String id) {
		super(id); 
		System.out.println("LINKEDTVHBBTV APPLICATION STARTED");
	}
	
	public LinkedtvhbbtvApplication(String id, String remoteReceiver) {
		super(id, remoteReceiver); 
		System.out.println("LINKEDTVHBBTV APPLICATION STARTED 2");
	}
	
	/**
	 * Handling new screen
	 * 
	 * @param s - screen
	 */
	public void onNewScreen(Screen s) {
		String fixedrole = s.getParameter("role");
		// so we want to load based on device type
		Capabilities caps = s.getCapabilities();
		String dstyle = caps.getDeviceModeName();

		// try to load special style first if not fallback.
		loadStyleSheet(s,"animate");
		loadStyleSheet(s,"generic");
		loadStyleSheet(s,"terms");
		// Do we already have a screen in the application that claims to be a mainscreen ?
		if (screenmanager.hasRole("mainscreen") && (fixedrole==null || !fixedrole.equals("mainscreen"))) {
			System.out.println("Second screen");
			loadSecondScreen(s);
			if (s.getParameter("curated") != null) {
				//curated version requested
				System.out.println("curated version");
				if (this.curated == false) {
					if (timeline == null) {
						timeline = new FsTimeLine();
					}
					timeline.removeNodes();
					FsTimeTagNodes tags = FSHelper.getTagNodes("linkedtv", presentation.getMediaResourceId(), presentation.getVideoUri(), presentation.getImageUri(), true);
					timeline.addNodes(tags.getAllNodes());					
				}
				this.curated = true;
			} else {
				//non curated version requested
				System.out.println("non curated version");
				if (this.curated == true) {
					if (timeline == null) {
						timeline = new FsTimeLine();
					}
					timeline.removeNodes();
					FsTimeTagNodes tags = FSHelper.getTagNodes("linkedtv", presentation.getMediaResourceId(), presentation.getVideoUri(), presentation.getImageUri(), false);
					timeline.addNodes(tags.getAllNodes());
				}
				this.curated = false;
			}
		} else {
			// this is the main screen
			// reset for screens that might have run in the past?
			currentTime = 0l;
			
			if(s.getParameter("presentation") != null){
				this.presentationCollectionUri = s.getParameter("presentation");
			} else if (s.getParameter("uuid") != null){
				this.mediaresourceUUID = s.getParameter("uuid");
				e = new Episode(mediaresourceUUID); 
				this.presentationCollectionUri = e.getStreamUri();
			}
			if (s.getParameter("curated") != null) {
				this.curated = true;
			}
			
			System.out.println("Main screen presentation uri = "+this.presentationCollectionUri);
			s.setRole("mainscreen");
			loadMainScreen(s);			
		}
		
		if (s.getParameter("id") != null) {
			if (e == null) {
				e = new Episode(s.getParameter("id"));
			}
			System.out.println("Title: "+e.getTitle());
			System.out.println("Duration: "+e.getDuration());
			System.out.println("Stills uri: "+e.getStillsUri());
			System.out.println("Stream uri: "+e.getStreamUri());
			System.out.println("Presentation: "+e.getPresentationId());
			
			//FSList annotations = e.getAnnotations();
			//List<FsNode> locations = annotations.getNodesByName("location");

			//get chapters
			FSList chaptersList = e.getChapters();
			int size = chaptersList.size();
			//if we have some chapters
			System.out.println("Number of chapters: "+size);
			if (size > 0) {
				List<FsNode> chapters = chaptersList.getNodes();	
				for (FsNode chapter : chapters) {					
					FSList annotationsList = e.getAnnotationsFromChapter(chapter); 
					List<FsNode> annotations = annotationsList.getNodes();
										
					String chapterTitle = chapter.getProperty("title");
					System.out.println("Chapter title : "+chapterTitle);
						
					for (FsNode annotation : annotations) {							
						System.out.println("annotation : "+annotation.getProperty("title")+"(start: "+annotation.getProperty("starttime")+" duration: "+annotation.getProperty("duration")+")");
						
						FSList enrichmentsList = e.getEnrichmentsFromAnnotation(annotation);
						List<FsNode> enrichments = enrichmentsList.getNodes();
						for (FsNode enrichment : enrichments) {
							System.out.println("enrichment : "+enrichment.getProperty("locator"));
						}						
					}
				}
			}
		}
		/*if (s.getParameter("provider") != null) {
			Channel c = new Channel("linkedtv", s.getParameter("provider"));
			List<Episode> episodes = c.getEpisodes();
			System.out.println("size of channel: "+episodes.size());
			
			Episode e = c.getLatestEpisode();
			
			System.out.println("Id: "+e.getMediaResourceId());
			System.out.println("Title: "+e.getTitle());
			System.out.println("Duration: "+e.getDuration());
			System.out.println("Stills uri: "+e.getStillsUri());
			System.out.println("Stream uri: "+e.getStreamUri());
		}*/
		
		loadContent(s, "notification");
	}
	
	/**
	 * Loading the main screen
	 * 
	 * @param s - the main screen
	 */
	private void loadMainScreen(Screen s) {
		presentation = new Presentation(presentationCollectionUri);
		// switch between HTML5 version and HbbTV version
		if (hbbtvMode == false) {
			loadContent(s, "video");
			// loadContent(s, "connectinfo");
			// loadContent(s, "logo");
			// loadContent(s, "terms");
			this.componentmanager.getComponent("video").put("app", "setVideo("+ presentation.getVideoUri() +"/rawvideo/3/raw.mp4)");
			this.componentmanager.getComponent("video").put("app", "setPoster("+ presentation.getImageUri() +"/h/0/m/0/sec1.jpg)");
			// s.putMsg("terms", "", "show()");
			// s.putMsg("logo", "", "broadcaster(" +presentation.getUser()+")");
		} else {
			loadContent(s, "hbbtvvideo");
			this.componentmanager.getComponent("hbbtvvideo").put("app", "setVideo("+ presentation.getVideoUri() +"/rawvideo/3/raw.mp4)");
			this.componentmanager.getComponent("hbbtvvideo").put("app", "setPoster("+ presentation.getImageUri() +"/h/0/m/0/sec1.jpg)");
		}
		
	}
	
	/**
	 * Loading secondary screens
	 * 
	 * @param s - the secondary screen
	 */
	private void loadSecondScreen(Screen s) {		
		s.setRole("secondaryscreen");
		loadContent(s, "login");
		loadContent(s, "tablet");
		loadContent(s, "droparea");		
		loadContent(s, "signal");
		loadContent(s, "screens", "menu");
	}
	
	/**
	 * Handling new user join
	 * 
	 * @param s - screen
	 * @param name - name of the user
	 */
	public void onNewUser(Screen s,String name) {
		super.onNewUser(s, name);
		String body = Slider.loadDataJoined(this,timeline);
		ComponentInterface comp = getComponentManager().getComponent("joinedslider");
		if (comp!=null) {
			comp.put("app", "html("+body+")");
		}
		
		comp = getComponentManager().getComponent("notification");
		comp.put("app", "login("+name+")");
	}
	
	/** 
	 * Handling log out of user
	 * 
	 * @param s - screen
	 * @param name - name of the user
	 */
	public void onLogoutUser(Screen s,String name) {
		super.onLogoutUser(s, name);
		String body = Slider.loadDataJoined(this,timeline);
		ComponentInterface comp = getComponentManager().getComponent("joinedslider");
		if (comp!=null) {
			comp.put("app", "html("+body+")");
		}
		comp = getComponentManager().getComponent("notification");
		if (name!=null) {
			comp.put("app", "logout("+name+")");
		}
	}
	
	/**
	 * Handling request from screen
	 * 
	 * @param s - screen
	 * @param from - 
	 * @param msg - the message send
	 */
	public void putOnScreen(Screen s,String from,String msg) {
        int pos = msg.indexOf("(");
        if (pos!=-1) {
                String command = msg.substring(0,pos);
                String content = msg.substring(pos+1,msg.length()-1);
                if (command.equals("orientationchange")) {
                	handleOrientationChange(s,content);
                } else if (command.equals("timeupdate")) {
                	handleTimeupdate(s,content);
                } else if (command.equals("gesture")) {
                	handleGesture(s,content);
                } else if (command.equals("loaddata")) {
                	handleLoadData(s,content);
                } else if (command.equals("loadscreen")) {
                	handleLoadScreen(s,content);
                } else if (command.equals("loadblockdata")) {
                	handleLoadBlockData(s,content);
                } else if(command.equals("started")){
					started();
				}else if(command.equals("stopped")){
					stopped();
				} else if (command.equals("loadfakeusers")) {
                	handleLoadFakeUsers(s);
                } else if (command.equals("bookmark")) {
                	handleBookmark(s,content);
                } else if (command.equals("share")) {
                	handleShare(s,content);
                } else {
                	super.putOnScreen(s, from, msg);
                }
        }
	}
	
	/**
	 * Handling gestures
	 * 
	 * @param s - screen
	 * @param content - source and type of the gesture
	 */
	private void handleGesture(Screen s,String content) {
		System.out.println("GESTURE FROM SCREEN ="+s.getId()+" "+content);
		String[] params = content.split(",");
		String source = params[0];
		String type = params[1];
		if (source.equals("screens")) {
			if (type.equals("swipeup")) {
				System.out.println("SEND OPEN APPS");
				// we need to start the animation and load the content
				s.putMsg("tablet","app", "openapps()");
			}
		} else if (source.equals("content")) {
			if (type.equals("swipedown")) {
				s.putMsg("tablet","app", "closeapps()");	
			}
		}
	}
	
	/**
	 * Loading new slider data
	 * 
	 * @param s - screen
	 * @param content - slider to load
	 */
	private void handleLoadData(Screen s,String content) {
		if (timeline==null) {
			timeline = new FsTimeLine();
			FsTimeTagNodes tags = FSHelper.getTagNodes("linkedtv", presentation.getMediaResourceId(), presentation.getVideoUri(), presentation.getImageUri(), this.curated);
			timeline.addNodes(tags.getAllNodes());
		}

		float chapterStart = 0f;
		float chapterDuration = 0f;
		
		FsNode chapter = timeline.getCurrentFsNode("chapter", currentTime);
		if (chapter != null) {
			chapterStart = chapter.getStarttime();
			chapterDuration = chapter.getDuration();
		}
			
		switch (sliders.valueOf(content)) {
			case whoslider:
				String body = Slider.loadDataWho(this,timeline, chapterStart, chapterDuration);
				s.putMsg("whoslider","app", "html("+body+")");
				break;
			case whatslider:
				body = Slider.loadDataWhat(this,timeline, chapterStart, chapterDuration);
				s.putMsg("whatslider","app", "html("+body+")");
				break;
			case whereslider:
				body = Slider.loadDataWhere(this,timeline, chapterStart, chapterDuration);
				s.putMsg("whereslider","app", "html("+body+")");
				break;
			case chapterslider:
				body = Slider.loadDataChapter(this,timeline);
				s.putMsg("chapterslider","app", "html("+body+")");
				break;
			case bookmarkslider:
				body = Slider.loadDataBookmark(s,this,timeline);
				s.putMsg("bookmarkslider","app", "html("+body+")");
				break;
			case sharedslider:
				body = Slider.loadDataShared(s,this,timeline);
				s.putMsg("sharedslider","app", "html("+body+")");
				break;
			case joinedslider:
				body = Slider.loadDataJoined(this,timeline);
				s.putMsg("joinedslider","app", "html("+body+")");
				break;
		}
	}
	
	/**
	 * Loading data for the specified entity
	 * 
	 * @param s - screen
	 * @param content - wrapped content contains type, uid, screen orientation, entity, image
	 */
	private void handleLoadBlockData(Screen s,String content) {
		String params[] = content.split(",");		
		String type = params[0].substring(0,params[0].indexOf("_"));
		String uid = params[1].substring(params[1].lastIndexOf("/")+1);
		String orientation = params[2];
		String entity = params[3];
		String image = params[4];
		String description = content.substring(content.indexOf(",", content.indexOf(image+",")+image.length())+1);
		System.out.println("LOAD BLOCKDATA="+content+" TYPE="+type);
		String body = "";
		String color = "";
		System.out.println("ENTITY = "+entity+" DESCRIPTION = "+description);
		List<Element> enrichments = FSHelper.getEnrichment("linkedtv", uid);
		
		try {
			switch (blocks.valueOf(type)) {
				case who: color = Slider.colorClasses.get("whoslider");break;
				case what: color = Slider.colorClasses.get("whatslider");break;
				case where: color = Slider.colorClasses.get("whereslider");break;
				case chapter: color = Slider.colorClasses.get("chapterslider");break;
			}
		} catch(Exception e) {}		
		
		System.out.println("Orientation = "+orientation);
		
		if (orientation.equals("portrait")) {
			if (enrichments.size() == 1) {
				body += "<iframe id=\"ext_inf\" src=\""+enrichments.get(0).valueOf("properties/locator")+"\"></iframe>";
			} else {			
				String lang = presentation.getLanguage();
				body += "<iframe id=\"ext_inf\" src=\"http://"+lang+".wikipedia.org/wiki/"+entity+"\"></iframe>";
			}
		} else {
			body += "<div class=\"triangleshift\"><div class=\""+color+"_large\"></div></div>";
			body += "<div class=\"infoscreen_div_centered\">";
			body += "<p id=\"infoscreen_title\" class=\"info_text_1\">"+entity+"</p>";
			body += "<div>";
			body += "<center>";
			body += "<p id=\"info_description\">"+description+"</p>";
			body += "<p class=\"info_text_1\">FIND OUT MORE</p>";
			body += "<div>";
			for (Iterator<Element> i = enrichments.iterator(); i.hasNext(); ) {
				Element enrichment = (Element) i.next();
				body += "<a href=\""+enrichment.valueOf("properties/locator")+"\">";
				body += enrichment.valueOf("properties/type");
				if (!enrichment.valueOf("properties/source").equals("")) {
					body += "-"+enrichment.valueOf("properties/source");
				}
				body += "</a><br/>";
			}
			body += "<p class=\"info_text_2\">Rotate your screen to read more</p>";
			body += "</div>";
			body += "</center>";
			body += "</div>";
			body += "</div>";
			body += "<div class=\"infoscreen_div_centered\">";
			body += "<img id=\"infimg\" src=\""+image+"\"/>";
			body += "</div>";
		}

		System.out.println("DATA="+body);
		s.setContent("infoscreen",body);
	}
	
	/**
	 * Highlighting blocks in the slider when in play range
	 * 
	 * @param s - screen
	 * @param content - video time
	 */
	private void handleTimeupdate(Screen s,String content) {
		if (timeline==null) {
			System.out.println("Timeline is empty, make new one");
			timeline = new FsTimeLine();
			FsTimeTagNodes tags = FSHelper.getTagNodes("linkedtv", presentation.getMediaResourceId(), presentation.getVideoUri(), presentation.getImageUri(), this.curated);
			timeline.addNodes(tags.getAllNodes());
		}
		
		String[] t = content.split(":");
		long ms = Long.parseLong(t[0])*1000;
		int newChapter = currentChapter;
		float chapterStart = 0f;
		float chapterDuration = 0f;
		currentTime = ms;
		
		ComponentInterface comp = getComponentManager().getComponent("chapterslider");
		if (comp!=null) {
			int blocknumber = timeline.getCurrentFsNodeNumber("chapter", ms);
			if (blocknumber!=-1) {
				newChapter = blocknumber;
				comp.put("app", "highlightblock("+(blocknumber+1+","+Slider.colorClasses.get("chapterslider"))+")");
			}
			chapterStart = timeline.getCurrentFsNode("chapter", ms).getStarttime();
			chapterDuration = timeline.getCurrentFsNode("chapter", ms).getDuration();
			System.out.println("Setting chapter start and duration to "+chapterStart+" "+chapterDuration);
		}

		comp = getComponentManager().getComponent("whoslider");
		if (comp!=null) {
			if (currentChapter != newChapter) {
				String body = Slider.loadDataWho(this,timeline, chapterStart, chapterDuration);
				System.out.println("Sending new layer data: "+body);
				comp.put("app", "html("+body+")");
			}
			
			int blocknumber = timeline.getCurrentFsNodeNumber("person", ms);
			if (blocknumber!=-1) {				
				comp.put("app", "highlightblock("+(blocknumber+1+","+Slider.colorClasses.get("whoslider"))+")");
			}
		}
		
		comp = getComponentManager().getComponent("whatslider");
		if (comp!=null) {
			if (currentChapter != newChapter) {
				String body = Slider.loadDataWhat(this,timeline, chapterStart, chapterDuration);
				System.out.println("Sending new layer data: "+body);
				comp.put("app", "html("+body+")");
			}
			
			int blocknumber = timeline.getCurrentFsNodeNumber("object", ms);
			if (blocknumber!=-1) {
				comp.put("app", "highlightblock("+(blocknumber+1+","+Slider.colorClasses.get("whatslider"))+")");
			}
		}
		
		comp = getComponentManager().getComponent("whereslider");
		if (comp!=null) {
			if (currentChapter != newChapter) {
				String body = Slider.loadDataWhere(this,timeline, chapterStart, chapterDuration);
				System.out.println("Sending new layer data: "+body);
				comp.put("app", "html("+body+")");
			}
			
			int blocknumber = timeline.getCurrentFsNodeNumber("location", ms);
			if (blocknumber!=-1) {
				comp.put("app", "highlightblock("+(blocknumber+1+","+Slider.colorClasses.get("whereslider"))+")");
			}
		}
		currentChapter = newChapter;
	}
	
	/**
	 * Loading screen
	 * 
	 * @param s - screen
	 * @param content - screen name
	 */
	private void handleLoadScreen(Screen s,String content) {
		removeCurrentSliders(s);
		System.out.println("LOAD SCREEN="+content);
		if (content.equals("screens_episode")) {
			s.setContent("content","");
			addSlider(s, "content", "whoslider");
			addSlider(s, "content", "whatslider");
			addSlider(s, "content", "whereslider");
			addSlider(s, "content", "chapterslider");
			loadContent(s, "sliderevents");
		} else if (content.equals("screens_overview")) {
			s.setContent("content","");
		} else if (content.equals("screens_bookmarks")) {
			s.setContent("content","");
			addSlider(s, "content", "bookmarkslider");
			addSlider(s, "content", "sharedslider");	
			loadContent(s, "sliderevents");
		} else if (content.equals("screens_social")) {
			s.setContent("content","");
			addSlider(s, "content", "joinedslider");	
			loadContent(s, "sliderevents");
		}
	}
	
	/**
	 * Add slider, replaces some names in javascript files to match the slider type
	 * 
	 * @param s - screen
	 * @param target - target to add slider
	 * @param slider - type of slider
	 */
	private void addSlider(Screen s, String target, String slider){
		String body = FsFileReader.getFileContent(this, slider, getComponentManager().getComponentPath("slider"));
				
		body = body.replaceAll("/slider", slider);
		body = body.replaceAll("/NAME", slider.substring(0, slider.indexOf("slider")).toUpperCase());
		body = body.replaceAll("/class", Slider.colorClasses.get(slider));
		body = body.replaceAll("/position", Slider.positions.get(slider));
		
		s.addContent(target, body);
		
		body = FsFileReader.getFileContent(this, slider, componentmanager.getComponentJS("slider"));
		body = body.replaceAll("/sliderid", slider.substring(0, slider.indexOf("slider")));
		body = body.replaceAll("/slider", slider);
		body = body.replaceAll("/Slider", slider.substring(0, 1).toUpperCase() + slider.substring(1));
		
		s.setScript(target, body);
		BasicComponent sliderComponent = (BasicComponent)this.componentmanager.getComponent(slider);
		if(sliderComponent==null){
			sliderComponent = new BasicComponent(); 
			sliderComponent.setId(slider);
			sliderComponent.setApplication(this);
		}
		this.addComponentToScreen(sliderComponent, s);

		sliderComponent.put("app", "setlanguage("+presentation.getLanguage()+")");
	}
	
	/**
	 * 
	 * 
	 * @param s - screen
	 */
	public void removeCurrentSliders(Screen s){
		Iterator<String> it = s.getComponentManager().getComponents().keySet().iterator();
		while(it.hasNext()){
			String comp = it.next();
			if(isSlider(comp)) this.removeComponentFromScreen(comp, s);
		}
	}
	
	/**
	 * Find out if name is a slider or not
	 * 
	 * @param name - the name of the component
	 * @return true if name is a slider, otherwise false
	 */
	private boolean isSlider(String name){
		try{
			sliders.valueOf(name);
			System.out.println(name + ": is a slider");
			return true;
		}catch(IllegalArgumentException e){
			System.out.println(name + ": is not a slider");
			return false;
		}
	}

	/**
	 * Update style sheet according to second screen orientation
	 * 
	 * @param s - screen
	 * @param o - the orientation
	 */
	private void handleOrientationChange(Screen s,String o) {
		// set the changed capabilities
		Capabilities caps = s.getCapabilities();
		caps.addCapability("orientation", o); // set the new orientation
		
		// reload the style sheet (should we not remove the old?)
		String dstyle = caps.getDeviceModeName();
		loadStyleSheet(s,dstyle,appname);
	}
	
	/**
	 * Handling bookmarks
	 * 
	 * @param s - screen
	 * @param content - the block to bookmark
	 */
	private void handleBookmark(Screen s,String content) {
		// get the user of this screen
		String username = s.getUserName();
		System.out.println("BOOKMARK="+content+" USER="+username);
		if (username==null) {
			s.putMsg("notification","app","show(to bookmark please login)");
		}
		User u = getUserManager().getUser(username);
		if (u!=null) {
			u.addBookmark(content);
			s.putMsg("notification","app","show(bookmarked "+u.getBookmarks().size()+")");
		}		
	}
	
	/**
	 * Handle sharing
	 * 
	 * @param s - screen
	 * @param content - the block to share
	 */
	private void handleShare(Screen s,String content) {
		// get the user of this screen
		String username = s.getUserName();
		System.out.println("SHARE="+content+" USER="+username);
		if (username==null) {
			s.putMsg("notification","app","show(to share please login)");
		}
		
		for(Iterator<String> iter = getUserManager().getUsers(); iter.hasNext(); ) {
			String uname = (String)iter.next();
			if (uname.equals(username)) {
				s.putMsg("notification","app","show(share send out)");
			} else {
				User u = getUserManager().getUser(uname);
				if (u!=null) {
					u.addShared(content);
					Iterator<String> it = this.getScreenManager().getScreens().keySet().iterator();
					while(it.hasNext()){
						String next = (String) it.next();
						Screen nscreen = getScreenManager().get(next);
						String nname = nscreen.getUserName();
						if (nname!=null && !nname.equals(username)) {
							nscreen.putMsg("notification","app","show("+username+" shared with you)");
							String body = Slider.loadDataShared(nscreen,this,timeline);
							nscreen.putMsg("sharedslider","app", "html("+body+")");
						}
					}
				}
			}
		}
	}
	
	/**
	 * Get a nicely formatted time string
	 * 
	 * @param seconds - number of seconds
	 * @return a formatted time string
	 */
	public String getTimeCodeString(double seconds) {
		String result = null;
		int sec = 0;
		int hourSecs = 3600;
		int minSecs = 60;
		int hours = 0;
		int minutes = 0;
		while (seconds >= hourSecs) {
			hours++;
			seconds -= hourSecs;
		}
		while (seconds >= minSecs) {
			minutes++;
			seconds -= minSecs;
		}
		sec = new Double(seconds).intValue();
		result = minutes+":";
		if (sec<10) {
			result += "0"+sec;
		} else {
			result += sec;
		}
		return result;
	}
	
	/**
	 * 
	 */
	private void started(){
		if (hbbtvMode == false) {
			this.componentmanager.getComponent("video").put("app", "started()");
		} else {
			this.componentmanager.getComponent("hbbtvvideo").put("app", "started()");
		}
	}
	
	/**
	 * 
	 */
	private void stopped(){
		if (hbbtvMode == false) {
			this.componentmanager.getComponent("video").put("app", "stopped()");
		} else {
			this.componentmanager.getComponent("hbbtvvideo").put("app", "stopped()");
		}
	}
	
	/*private void timeUpdate(String args){
		System.out.println("timeUpdate(" + args + ")");
		String[] arguments = args.split(",");
		int time = (int) Double.parseDouble(arguments[0]);
		boolean scrub = false;
		if(arguments.length > 1){
			scrub = Boolean.parseBoolean(arguments[1]);
		}
		
		timeUpdate(time, scrub);
	}*/
	
	/*private void scrubStart(){
		this.project.set("scrubbing", true);
	}
	
	private void scrubStop(){
		this.project.set("scrubbing", false);
	}
	
	private void timeUpdate(int time, boolean scrub){
		this.project.set("currentTime", time);
	}*/
	
	/**
	 * Loading fake users for now
	 * 
	 * @param s - screen
	 */
	private void handleLoadFakeUsers(Screen s) {
		String body = "<table><tr><td><div class=\"fakeuser\" id=\"user_bert\"><p>Bert</p><img src=\"/eddie/img/people/bert.png\"></div>";
		body += "</td><td><div class=\"fakeuser\" id=\"user_anne\"><p>Anne</p><img src=\"/eddie/img/people/anne.png\"></div>";
		body += "</td><td><div class=\"fakeuser\" id=\"user_ralph\"><p>Ralph</p><img src=\"/eddie/img/people/ralph.png\"></div>";
		body += "</td></tr><tr><tr><td><div class=\"fakeuser\" id=\"user_nina\"><p>Nina</p><img src=\"/eddie/img/people/nina.png\"></div></td></tr></table>";
		s.putMsg("tablet","app", "fakeusershtml("+body+")");
		s.loadScript("tablet", "tablet/fakeuserevents.js", this);
	}
}

/**
 * Presentation class to load data from the file system 
 * 
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2013
 * @package org.springfield.lou.application.types
 *
 */
final class Presentation {
	private String uri;
	private String presentationUri;
	private String videoUri;
	private String imageUri;
	private String mount;
	private String mediaResourceId;
	
	/**
	 * Create new presentation from the given uri
	 * 
	 * @param uri - the uri of the presentation
	 */
	public Presentation(String uri) {
		this.uri = uri;
		loadProject();
	}
	
	/**
	 * Get video uri
	 * 
	 * @return the video uri
	 */
	public String getVideoUri() {
		return "http://"+mount+".noterik.com/progressive/"+mount+videoUri;
	}
	
	/**
	 * Get the image uri for this presentation
	 * 
	 * @return - the image uri
	 */
	public String getImageUri() {
		return imageUri;
	}
	
	/**
	 * Get the media resource id
	 * 
	 * @return - the media resource id
	 */
	public String getMediaResourceId() {
		return mediaResourceId;
	}
	
	/**
	 * Get the user from the uri
	 * 
	 * @return - the user from the uri
	 */
	public String getUser() {
		return uri.substring(uri.indexOf("/user/")+6, uri.indexOf("/collection/"));
	}
	
	/**
	 * Load project
	 */
	private void loadProject() {
		if (loadCollectionPresentation()) {
			if (loadPresentation()) {
				loadVideo();
			}
		}
	}
	
	/**
	 * Load the collection presentation from the file system
	 * 
	 * @return true if everything went ok, otherwise false
	 */
	private boolean loadCollectionPresentation() {
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) return false;
		//String data = LazyHomer.sendRequestBart("GET", uri, null, null);
		String data = smithers.get(uri, null, null);
		try {
			Document response = DocumentHelper.parseText(data);
			String presentationUri = response.selectSingleNode("//presentation/@referid") == null ? "" : response.selectSingleNode("//presentation/@referid").getText();
			this.presentationUri = presentationUri;
			return true;
		} catch (Exception e) {
			System.out.println("ERROR: loading collection failed");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Load the presentation from the file system
	 * 
	 * @return true if everything went ok, otherwise false
	 */
	private boolean loadPresentation() {
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) return false;
		//String data = LazyHomer.sendRequestBart("GET", presentationUri, null, null);
		// String data = smithers.get(presentationUri, null, null);
		String data = smithers.get(presentationUri, "<fsxml><properties><depth>2</depth></properties></fsxml>", "text/xml");
		try {
			Document response = DocumentHelper.parseText(data);
			String videoUri = response.selectSingleNode("//videoplaylist[@id='1']/video[@id='1']/@referid") == null ? "" : response.selectSingleNode("//videoplaylist[@id='1']/video[@id='1']/@referid").getText();
			String mediaResourceId = response.selectSingleNode("//properties/media_resource_id") == null ? "" : response.selectSingleNode("//properties/media_resource_id").getText();
			
			this.videoUri = videoUri;
			this.mediaResourceId = mediaResourceId;
			return true;
		} catch (Exception e) {
			System.out.println("ERROR: loading presentation failed");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Load the video from the file system
	 * 
	 * @return true if everything went ok, otherwise false
	 */
	private boolean loadVideo() {
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) return false;
		//String data = LazyHomer.sendRequestBart("GET", videoUri, null, null);
		String data = smithers.get(videoUri, null, null);
		try {
			Document response = DocumentHelper.parseText(data);
			String imageUri = response.selectSingleNode("//screens[@id='1']/properties/uri") == null ? "" : response.selectSingleNode("//screens[@id='1']/properties/uri").getText();
			String mount = response.selectSingleNode("//rawvideo[@id='1']/properties/mount") == null ? "" : response.selectSingleNode("//rawvideo[@id='1']/properties/mount").getText();
			mount = mount.substring(0, mount.indexOf(","));
			
			this.imageUri = imageUri;
			this.mount = mount;
			return true;
		} catch (Exception e) {
			System.out.println("ERROR: loading video failed");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Get the language based on the user
	 * 
	 * @return language found
	 */
	public String getLanguage() {
		String language = "en";
		if (getUser().equals("rbb")) {
			language = "de";
		}
		return language;
	}
}

/**
 * Helper class to retrieve the entities from the LinkedTV system through Maggie (in fsxml)
 * 
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2013
 * @package org.springfield.lou.application.types
 *
 */
final class FSHelper {
	//TODO: fugly
	private static String FS_SERVER = "http://player2.noterik.com/maggie/";
	
	public FSHelper() { }
	
	/**
	 * Get entities for the given media resource id
	 * 
	 * @param domain - the domain the presentation is in
	 * @param mediaResourceId - the media resource id of the video
	 * @param path - the path 
	 * @param imageBasePath - the image path
	 * @param curated - curated version or not
	 * @return FsTimeTagNodes
	 */
	public static FsTimeTagNodes getTagNodes(String domain, String mediaResourceId, String path, String imageBasePath, boolean curated) {
		FsTimeTagNodes results = new FsTimeTagNodes();
	
		String uri = FS_SERVER+"?domain="+domain+"&id="+mediaResourceId+"&annotations";
		if (curated) {
			System.out.println("Curated request");
			uri += "&curated&renew";
		}
		
		String data = HttpHelper.sendRequest("GET", uri, null, null).getResponse();
		System.out.println("data = "+data);
		try {
			Document response = DocumentHelper.parseText(data);
			List<Node> annotations = response.selectNodes("//annotations/*");
			System.out.println("number of childs:"+annotations.size());
			for (Iterator<Node> i = annotations.iterator(); i.hasNext(); ) {
				Element annotation = (Element) i.next();
				FsNode node = new FsNode();
				node.setName(annotation.getName());
				node.setId(annotation.attribute("id").getText());
				node.setPath(path+"/"+node.getName()+"/"+node.getId());
				node.setImageBaseUri(imageBasePath);
				List<Node> properties = annotation.selectNodes("properties/*");
				for (Iterator<Node> j = properties.iterator(); j.hasNext(); ) {
					Element property = (Element) j.next();
					node.setProperty(property.getName(), property.getText());
				}
				results.addNode(node);
			}					
		} catch (Exception e) {
			System.out.println("Error in parsing nodes from maggie");
		}
		data = HttpHelper.sendRequest("GET", FS_SERVER+"?domain="+domain+"&id="+mediaResourceId+"&chapters", null, null).getResponse();
		System.out.println("data from chapters = "+data);
		try {
			Document response = DocumentHelper.parseText(data);
			List<Node> chapters = response.selectNodes("//chapters/*");
			System.out.println("number of childs:"+chapters.size());
			for (Iterator<Node> i = chapters.iterator(); i.hasNext(); ) {
				Element chapter = (Element) i.next();

				FsNode node = new FsNode();
				node.setName(chapter.getName());
				node.setId(chapter.attribute("id").getText());
				node.setPath(path+"/"+node.getName()+"/"+node.getId());
				node.setImageBaseUri(imageBasePath);
				List<Node> properties = chapter.selectNodes("properties/*");
				for (Iterator<Node> j = properties.iterator(); j.hasNext(); ) {
					Element property = (Element) j.next();
					node.setProperty(property.getName(), property.getText());
				}
				results.addNode(node);
			}					
		} catch (Exception e) {
			System.out.println("Error in parsing nodes from maggie");
		}
		
		System.out.println("current ammount of annotations = "+results.size());
		return results;
	}
	
	/**
	 * Get list of enrichments for the given entity
	 * 
	 * @param domain - the current domain
	 * @param entityId - the entity id
	 * @return List of enrichment elements
	 */
	public static List<Element> getEnrichment(String domain, String entityId) {
		String uri = FS_SERVER+"?domain="+domain+"&id="+entityId+"&enrichments";		
		System.out.println("Getting enrichment "+entityId+" ("+uri+")");
		String data = HttpHelper.sendRequest("GET", uri, null, null).getResponse();
		List<Element> results = null;
		
		try {
			Document response = DocumentHelper.parseText(data);
			List<Node> enrichments = response.selectNodes("//enrichments/*");
			System.out.println("number of childs:"+enrichments.size());
			results = new ArrayList<Element>(enrichments.size());
			for (Iterator<Node> i = enrichments.iterator(); i.hasNext(); ) {
				Element enrichment = (Element) i.next();				
				results.add(enrichment);
			}					
		} catch (Exception e) {
			System.out.println("Error in parsing nodes from maggie");
		}
		return results;
	}
}

