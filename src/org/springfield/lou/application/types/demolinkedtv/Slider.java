/* 
* Slider.java
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

package org.springfield.lou.application.types.demolinkedtv;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springfield.lou.application.types.LinkedtvhbbtvApplication;
import org.springfield.fs.FsNode;
import org.springfield.fs.FsTimeLine;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.user.User;
import org.springfield.lou.user.UserManager;

/**
 * Slider class to represent entities
 * 
 * @author Daniel Ockeloen, Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2013
 * @package org.springfield.lou.application.types
 *
 */
public class Slider {
	public static final Map<String, String> colorClasses;
	public static final Map<String, String> positions;	
	public static final Map<String, String> blockDataClasses;
	
	static{
		colorClasses = new HashMap<String, String>();
		colorClasses.put("whoslider", "triangleblue");
		colorClasses.put("whatslider", "trianglegreen");
		colorClasses.put("whereslider", "trianglepink");
		colorClasses.put("chapterslider", "trianglebrown");
		colorClasses.put("bookmarkslider", "triangledarkblue");
		colorClasses.put("sharedslider", "triangledarkgreen");
		colorClasses.put("joinedslider", "trianglebrown");
		colorClasses.put("chatslider", "trianglered");
	}
	static{
		blockDataClasses = new HashMap<String, String>();
		blockDataClasses.put("whoslider", "triangleblue");
		blockDataClasses.put("whatslider", "trianglegreen");
		blockDataClasses.put("whereslider", "trianglepink");
		blockDataClasses.put("chapterslider", "trianglebrown");
		colorClasses.put("bookmarkslider", "triangledarkblue");
		colorClasses.put("sharedslider", "triangledarkgreen");
		colorClasses.put("joinedslider", "trianglebrown");
		colorClasses.put("chatslider", "trianglered");
	}
	static{
		positions = new HashMap<String, String>();
		positions.put("whoslider", "pos1");
		positions.put("whatslider", "pos2");
		positions.put("whereslider", "pos3");
		positions.put("chapterslider", "pos4");
		positions.put("bookmarkslider", "pos1");
		positions.put("sharedslider", "pos2");
		positions.put("joinedslider", "pos1");
		positions.put("chatslider", "pos2");
	}
	
	/**
	 * 
	 * 
	 * @param parent
	 * @param content
	 * @param slider
	 * @return
	 */
	public static String getBlockData(LinkedtvhbbtvApplication parent,String content, String slider) {
		String orientation = content.split(",")[2];
		System.out.println("orientation = "+orientation);
	
		String body = "<div class=\"triangleshift\"><div class=\""+blockDataClasses.get(slider)+"\"></div></div>";
		body+="<p>test text</p>";
		return body;
	}
	
	/**
	 * Load data for the 'who' layer
	 * 
	 * @param parent - parent class
	 * @param timeline - parent timeline
	 * @param start - start of the layer
	 * @param duration - end of the layer
	 * @return html formatted layer
	 */
	public static String loadDataWho(LinkedtvhbbtvApplication parent,FsTimeLine timeline, float start, float duration) {
		String body = "";
		int i = 1;
		if (timeline == null) {
			System.out.println("timeline appears to be empty!");
		}
		
		for(Iterator<FsNode> iter = timeline.getFsNodesByType("person"); iter.hasNext(); ) {
			FsNode node = (FsNode)iter.next();
			
			if (node.getStarttime() >= start && node.getStarttime() <= (start+duration)) {			
				String title = node.getProperty("title");
				body += "<div class=\"sliderblock who\" data-referid=\""+node.getPath()+"\" data-time=\""+node.getStarttime()/1000+"\"";
				body += "data-uid=\""+node.getId()+"\" data-entity=\""+title+"\" data-locator=\""+node.getProperty("locator")+"\" id=\"who_block"+(i)+"\">";
			
				body+="<img class=\"sliderimg\" src=\""+node.getScreenShotUrl()+"\" />";
				body+="<div class=\"timecode bluet\">"+parent.getTimeCodeString(node.getStarttime()/1000)+"</div>";
				
				if (title == null) {
					
				} else if (title.length()>40) {
					body+="<div class=\"overlay\"><p>"+title.substring(0,37)+"...</p></div>";
				} else {
					body+="<div class=\"overlay\"><p>"+title+"</p></div>";
				}
				body += "<div style=\"display:none\" id=\"description_who_block"+(i)+"\"></div>";
				body += "<div style=\"display:none\" id=\"locator_who_block"+(i)+"\">"+node.getProperty("locator")+"</div>";
				body+="</div>";					
			}
			i++;
		}
		return body;
	}
	
	/**
	 * Load data for the 'what' layer
	 * 
	 * @param parent - parent class
	 * @param timeline - parent timeline
	 * @param start - start of the layer
	 * @param duration - end of the layer
	 * @return html formatted layer
	 */
	public static String loadDataWhat(LinkedtvhbbtvApplication parent,FsTimeLine timeline, float start, float duration) {
		String body = "";
		int i = 1;
		if (timeline == null) {
			System.out.println("timeline appears to be empty!");
		} 
		
		for(Iterator<FsNode> iter = timeline.getFsNodesByType("object"); iter.hasNext(); ) {
			FsNode node = (FsNode)iter.next();
			
			if (node.getStarttime() >= start && node.getStarttime() <= (start+duration)) {		
				String title = node.getProperty("title");
				body += "<div class=\"sliderblock what\" data-referid=\""+node.getPath()+"\" data-time=\""+node.getStarttime()/1000+"\"";
				body += "data-uid=\""+node.getId()+"\" data-entity=\""+title+"\" data-locator=\""+node.getProperty("locator")+"\" id=\"what_block"+(i)+"\">";
						
				body+="<img class=\"sliderimg\" src=\""+node.getScreenShotUrl()+"\" />";
				body+="<div class=\"timecode greent\">"+parent.getTimeCodeString(node.getStarttime()/1000)+"</div>";
				
				if (title == null) {
					
				} else if (title.length()>40) {
					body+="<div class=\"overlay\"><p>"+title.substring(0,37)+"...</p></div>";
				} else {
					body+="<div class=\"overlay\"><p>"+title+"</p></div>";
				}
				body += "<div style=\"display:none\" id=\"description_what_block"+(i)+"\"></div>";
				body += "<div style=\"display:none\" id=\"locator_what_block"+(i)+"\">"+node.getProperty("locator")+"</div>";
				body+="</div>";				
			}
			i++;
		}
		return body;
	}
	
	/**
	 * Load data for the 'where' layer
	 * 
	 * @param parent - parent class
	 * @param timeline - parent timeline
	 * @param start - start of the layer
	 * @param duration - end of the layer
	 * @return html formatted layer
	 */
	public static String loadDataWhere(LinkedtvhbbtvApplication parent,FsTimeLine timeline, float start, float duration) {
		String body = "";
		int i = 1;
		if (timeline == null) {
			System.out.println("timeline appears to be empty!");
		} 
		
		for(Iterator<FsNode> iter = timeline.getFsNodesByType("location"); iter.hasNext(); ) {
			FsNode node = (FsNode)iter.next();
			
			if (node.getStarttime() >= start && node.getStarttime() <= (start+duration)) {		
				String title = node.getProperty("title");
				body += "<div class=\"sliderblock where\" data-referid=\""+node.getPath()+"\" data-time=\""+node.getStarttime()/1000+"\"";
				body += "data-uid=\""+node.getId()+"\" data-entity=\""+title+"\" data-locator=\""+node.getProperty("locator")+"\" id=\"where_block"+(i)+"\">";
				
				body+="<img class=\"sliderimg\" src=\""+node.getScreenShotUrl()+"\" />";
				body+="<div class=\"timecode pinkt\">"+parent.getTimeCodeString(node.getStarttime()/1000)+"</div>";
				
				if (title == null) {
					
				} else if (title.length()>40) {
					body+="<div class=\"overlay\"><p>"+title.substring(0,37)+"...</p></div>";
				} else {
					body+="<div class=\"overlay\"><p>"+title+"</p></div>";
				}
				body += "<div style=\"display:none\" id=\"description_where_block"+(i)+"\"></div>";
				body += "<div style=\"display:none\" id=\"locator_where_block"+(i)+"\">"+node.getProperty("locator")+"</div>";
				body+="</div>";	
			}
			i++;
		}
		return body;
	}
	
	/**
	 * Load data for the chapter layer
	 * 
	 * @param parent - parent class
	 * @param timeline - parent timeline
	 * @return html formatted layer
	 */
	public static String loadDataChapter(LinkedtvhbbtvApplication parent, FsTimeLine timeline) {
		String body = "";		
		
		int i = 1;
		if (timeline == null) {
			System.out.println("timeline appears to be empty!");
		} 
		
		for(Iterator<FsNode> iter = timeline.getFsNodesByType("chapter"); iter.hasNext(); ) {
			FsNode node = (FsNode)iter.next();
			String title = node.getProperty("title");
			body += "<div class=\"sliderblock chapter\" data-referid=\""+node.getPath()+"\" data-time=\""+node.getStarttime()/1000+"\"";
			body += "data-uid=\""+node.getProperty("uid")+"\" data-entity=\""+title+"\" id=\"chapter_block"+(i)+"\">";
		
			body+="<img class=\"sliderimg\" src=\""+node.getScreenShotUrl()+"\" />";
			body+="<div class=\"timecode brownt\">"+parent.getTimeCodeString(node.getStarttime()/1000)+"</div>";
			
			if (title == null) {
				
			} else if (title.length()>40) {
				body+="<div class=\"overlay\"><p>"+title.substring(37)+"...</p></div>";
			} else {
				body+="<div class=\"overlay\"><p>"+title+"</p></div>";
			}
			body+="</div>";	
			i++;
		}
		return body;
	}
	
	/**
	 * Load bookmark data
	 * 
	 * @param s - screen
	 * @param parent - parent class
	 * @param timeline - parent timeline
	 * @return html formatted data
	 */
	public static String loadDataBookmark(Screen s,LinkedtvhbbtvApplication parent,FsTimeLine timeline) {
		String body = "";
		UserManager um = parent.getUserManager();
		String username = s.getUserName();
		if (username==null) {
			body = "<div class=\"sliderempty\"><p>Not logged in, login for bookmarks</p></div>";
		}
		User u = um.getUser(username);
		if (u!=null) {
			List<String> bookmarks = u.getBookmarks();
			if (bookmarks.size()==0) {
				body = "<div class=\"sliderempty\"><p>No bookmarks yet</p></div>";
			} else {
				for (int i=0;i<bookmarks.size();i++) {
					String line = bookmarks.get(i);
					String[] values = line.split(",");
					System.out.println("BOOKMARK("+i+")="+line+" BZ="+values.length);
					
					if (values.length>2) {						
						String typ= "";
						String blocktype = values[1].substring(0, values[1].indexOf("_"));
						if (blocktype.equals("who")) {
							typ = "person";
						} else if (blocktype.equals("what")) {
							typ = "object";
						} else if (blocktype.equals("where")) {
							typ = "location";
						} else {
							typ = blocktype;
						}
						
						String blockid = values[1].substring(values[1].indexOf("_block")+6);
						
						System.out.println("Getting node with type "+typ+" and id "+typ+blockid);

						FsNode node = timeline.getFsNodeById(typ, Integer.parseInt(blockid));
						if (node!=null) {
							System.out.println("NODE="+node);
							String type = node.getName();
							if (type.equals("person")) {
								String title = node.getProperty("title");
								body += "<div class=\"sliderblock who\" data-referid=\""+node.getPath()+"\" data-time=\""+node.getStarttime()/1000+"\"";
								body += "data-uid=\""+node.getId()+"\" data-entity=\""+title+"\" data-locator=\""+node.getProperty("locator")+"\" id=\"who_block"+(i)+"\">";
							
								body+="<img class=\"sliderimg\" src=\""+node.getScreenShotUrl()+"\" />";
								body+="<div class=\"timecode bluet\">"+parent.getTimeCodeString(node.getStarttime()/1000)+"</div>";
								
								if (title == null) {
									
								} else if (title.length()>40) {
									body+="<div class=\"overlay\"><p>"+title.substring(0,37)+"...</p></div>";
								} else {
									body+="<div class=\"overlay\"><p>"+title+"</p></div>";
								}
								body += "<div style=\"display:none\" id=\"description_who_block"+(i)+"\"></div>";
								body += "<div style=\"display:none\" id=\"locator_who_block"+(i)+"\">"+node.getProperty("locator")+"</div>";
								body+="</div>";				
							} else if (type.equals("object")) {
								String title = node.getProperty("title");
								body += "<div class=\"sliderblock what\" data-referid=\""+node.getPath()+"\" data-time=\""+node.getStarttime()/1000+"\"";
								body += "data-uid=\""+node.getId()+"\" data-entity=\""+title+"\" data-locator=\""+node.getProperty("locator")+"\" id=\"what_block"+(i)+"\">";
										
								body+="<img class=\"sliderimg\" src=\""+node.getScreenShotUrl()+"\" />";
								body+="<div class=\"timecode greent\">"+parent.getTimeCodeString(node.getStarttime()/1000)+"</div>";
								
								if (title == null) {
									
								} else if (title.length()>40) {
									body+="<div class=\"overlay\"><p>"+title.substring(0,37)+"...</p></div>";
								} else {
									body+="<div class=\"overlay\"><p>"+title+"</p></div>";
								}
								body += "<div style=\"display:none\" id=\"description_what_block"+(i)+"\"></div>";
								body += "<div style=\"display:none\" id=\"locator_what_block"+(i)+"\">"+node.getProperty("locator")+"</div>";
								body+="</div>";
							} else if (type.equals("location")) {
								String title = node.getProperty("title");
								body += "<div class=\"sliderblock where\" data-referid=\""+node.getPath()+"\" data-time=\""+node.getStarttime()/1000+"\"";
								body += "data-uid=\""+node.getId()+"\" data-entity=\""+title+"\" data-locator=\""+node.getProperty("locator")+"\" id=\"where_block"+(i)+"\">";
								
								body+="<img class=\"sliderimg\" src=\""+node.getScreenShotUrl()+"\" />";
								body+="<div class=\"timecode pinkt\">"+parent.getTimeCodeString(node.getStarttime()/1000)+"</div>";
								
								if (title == null) {
									
								} else if (title.length()>40) {
									body+="<div class=\"overlay\"><p>"+title.substring(0,37)+"...</p></div>";
								} else {
									body+="<div class=\"overlay\"><p>"+title+"</p></div>";
								}
								body += "<div style=\"display:none\" id=\"description_where_block"+(i)+"\"></div>";
								body += "<div style=\"display:none\" id=\"locator_where_block"+(i)+"\">"+node.getProperty("locator")+"</div>";
								body+="</div>";	
							} else if (type.equals("chapter")) {
								String title = node.getProperty("title");
								body += "<div class=\"sliderblock chapter\" data-referid=\""+node.getPath()+"\" data-time=\""+node.getStarttime()/1000+"\"";
								body += "data-uid=\""+node.getProperty("uid")+"\" data-entity=\""+title+"\" id=\"chapter_block"+(i)+"\">";
							
								body+="<img class=\"sliderimg\" src=\""+node.getScreenShotUrl()+"\" />";
								body+="<div class=\"timecode brownt\">"+parent.getTimeCodeString(node.getStarttime()/1000)+"</div>";
								
								if (title == null) {
									
								} else if (title.length()>40) {
									body+="<div class=\"overlay\"><p>"+title.substring(37)+"...</p></div>";
								} else {
									body+="<div class=\"overlay\"><p>"+title+"</p></div>";
								}
								body+="</div>";	
							}
						}
					}
				}
			}
		}
		return body;
	}
	
	/**
	 * Load sharing data
	 * 
	 * @param s - screen
	 * @param parent - parent class
	 * @param timeline - parent timeline
	 * @return html formatted data
	 */
	public static String loadDataShared(Screen s,LinkedtvhbbtvApplication parent,FsTimeLine timeline) {
		String body = "";
		UserManager um = parent.getUserManager();
		String username = s.getUserName();
		if (username==null) {
			body = "<div class=\"sliderempty\"><p>Not logged in, login for shares</p></div>";
		}
		User u = um.getUser(username);
		if (u!=null) {
			List<String> shared = u.getShared();
			if (shared.size()==0) {
				body = "<div class=\"sliderempty\"><p>No shares yet</p></div>";
			} else {
				System.out.println("Number of shares "+shared.size());
				for (int i=0;i<shared.size();i++) {
					String line = shared.get(i);
					String[] values = line.split(",");
					System.out.println("SHARED("+i+")="+line+" BZ="+values.length);
					
					
					if (values.length>2) {
						String typ= "";
						String blocktype = values[1].substring(0, values[1].indexOf("_"));
						if (blocktype.equals("who")) {
							typ = "person";
						} else if (blocktype.equals("what")) {
							typ = "object";
						} else if (blocktype.equals("where")) {
							typ = "location";
						} else {
							typ = blocktype;
						}

						String blockid = values[1].substring(values[1].indexOf("_block")+6);
						
						System.out.println("Getting node with type "+typ+" and id "+typ+blockid);

						FsNode node = timeline.getFsNodeById(typ, Integer.parseInt(blockid));
						if (node!=null) {
							System.out.println("NODE="+node);
							String type = node.getName();
							if (type.equals("person")) {
								String title = node.getProperty("title");
								body += "<div class=\"sliderblock who\" data-referid=\""+node.getPath()+"\" data-time=\""+node.getStarttime()/1000+"\"";
								body += "data-uid=\""+node.getId()+"\" data-entity=\""+title+"\" data-locator=\""+node.getProperty("locator")+"\" id=\"who_block"+(i)+"\">";
							
								body+="<img class=\"sliderimg\" src=\""+node.getScreenShotUrl()+"\" />";
								body+="<div class=\"timecode bluet\">"+parent.getTimeCodeString(node.getStarttime()/1000)+"</div>";
								
								if (title == null) {
									
								} else if (title.length()>40) {
									body+="<div class=\"overlay\"><p>"+title.substring(0,37)+"...</p></div>";
								} else {
									body+="<div class=\"overlay\"><p>"+title+"</p></div>";
								}
								body += "<div style=\"display:none\" id=\"description_who_block"+(i)+"\"></div>";
								body += "<div style=\"display:none\" id=\"locator_who_block"+(i)+"\">"+node.getProperty("locator")+"</div>";
								body+="</div>";
							} else if (type.equals("object")) {
								String title = node.getProperty("title");
								body += "<div class=\"sliderblock what\" data-referid=\""+node.getPath()+"\" data-time=\""+node.getStarttime()/1000+"\"";
								body += "data-uid=\""+node.getId()+"\" data-entity=\""+title+"\" data-locator=\""+node.getProperty("locator")+"\" id=\"what_block"+(i)+"\">";
										
								body+="<img class=\"sliderimg\" src=\""+node.getScreenShotUrl()+"\" />";
								body+="<div class=\"timecode greent\">"+parent.getTimeCodeString(node.getStarttime()/1000)+"</div>";
								
								if (title == null) {
									
								} else if (title.length()>40) {
									body+="<div class=\"overlay\"><p>"+title.substring(0,37)+"...</p></div>";
								} else {
									body+="<div class=\"overlay\"><p>"+title+"</p></div>";
								}
								body += "<div style=\"display:none\" id=\"description_what_block"+(i)+"\"></div>";
								body += "<div style=\"display:none\" id=\"locator_what_block"+(i)+"\">"+node.getProperty("locator")+"</div>";
								body+="</div>";
							} else if (type.equals("location")) {
								String title = node.getProperty("title");
								body += "<div class=\"sliderblock where\" data-referid=\""+node.getPath()+"\" data-time=\""+node.getStarttime()/1000+"\"";
								body += "data-uid=\""+node.getId()+"\" data-entity=\""+title+"\" data-locator=\""+node.getProperty("locator")+"\" id=\"where_block"+(i)+"\">";
								
								body+="<img class=\"sliderimg\" src=\""+node.getScreenShotUrl()+"\" />";
								body+="<div class=\"timecode pinkt\">"+parent.getTimeCodeString(node.getStarttime()/1000)+"</div>";
								
								if (title == null) {
									
								} else if (title.length()>40) {
									body+="<div class=\"overlay\"><p>"+title.substring(0,37)+"...</p></div>";
								} else {
									body+="<div class=\"overlay\"><p>"+title+"</p></div>";
								}
								body += "<div style=\"display:none\" id=\"description_where_block"+(i)+"\"></div>";
								body += "<div style=\"display:none\" id=\"locator_where_block"+(i)+"\">"+node.getProperty("locator")+"</div>";
								body+="</div>";	
							} else if (type.equals("chapter")) {
								String title = node.getProperty("title");
								body += "<div class=\"sliderblock chapter\" data-referid=\""+node.getPath()+"\" data-time=\""+node.getStarttime()/1000+"\"";
								body += "data-uid=\""+node.getProperty("uid")+"\" data-entity=\""+title+"\" id=\"chapter_block"+(i)+"\">";
							
								body+="<img class=\"sliderimg\" src=\""+node.getScreenShotUrl()+"\" />";
								body+="<div class=\"timecode brownt\">"+parent.getTimeCodeString(node.getStarttime()/1000)+"</div>";
								
								if (title == null) {
									
								} else if (title.length()>40) {
									body+="<div class=\"overlay\"><p>"+title.substring(37)+"...</p></div>";
								} else {
									body+="<div class=\"overlay\"><p>"+title+"</p></div>";
								}
								body+="</div>";	
							}
						}
					} 
					System.out.println("BODY="+body);
				}
			}
		}
		return body;
	}
	
	/**
	 * Load who joined data
	 * 
	 * @param parent - parent class
	 * @param timeline - parent timeline
	 * @return html formatted data
	 */
	public static String loadDataJoined(LinkedtvhbbtvApplication parent,FsTimeLine timeline) {
		String body = "";
		UserManager um = parent.getUserManager();
		for(Iterator<String> iter = um.getUsers(); iter.hasNext(); ) {
			String name = iter.next();
			User u = um.getUser(name);
			if (name.equals("bert")) {
				body += "<div class=\"sliderblock joined\" id=\"joined_block3\">";
				body+="<img class=\"sliderimg\" src=\"/eddie/img/people/bert.png\"  />";
				body+="<div class=\"timecode brownt\">5m</div>";
				body+="<div class=\"overlay\"><p>Bert</p></div></div>";
			} else if (name.equals("anne")) {
				body += "<div class=\"sliderblock joined\" id=\"joined_block1\">";
				body+="<img class=\"sliderimg\" src=\"/eddie/img/people/anne.png\"  />";
				body+="<div class=\"timecode brownt\">10m</div>";
				body+="<div class=\"overlay\"><p>Anne</p></div></div>";
			} else if (name.equals("ralph")) {
				body += "<div class=\"sliderblock joined\" id=\"joined_block2\">";
				body+="<img class=\"sliderimg\" src=\"/eddie/img/people/ralph.png\"  />";
				body+="<div class=\"timecode brownt\">7m</div>";
				body+="<div class=\"overlay\"><p>Ralph</p></div></div>";
			} else if (name.equals("nina")) {
				body += "<div class=\"sliderblock joined\" id=\"joined_block4\">";
				body+="<img class=\"sliderimg\" src=\"/eddie/img/people/nina.png\"  />";
				body+="<div class=\"timecode brownt\">2m</div>";
				body+="<div class=\"overlay\"><p>Nina</p></div></div>";
			}
		}
		return body;
	}
	
	/**
	 * Load chat data 
	 * 
	 * @param parent - parent class
	 * @param timeline - parent timeline
	 * @return html formatted data
	 */
	public static String loadDataChat(LinkedtvhbbtvApplication parent,FsTimeLine timeline) {
		String body = "";
		body += "<div class=\"sliderblock chat\" id=\"chat_block1\">";
		body+="<img class=\"sliderimg\" src=\"/eddie/img/people/rutger.png\"  />";
		body+="<div class=\"timecode redt\">3:10</div>";
		body+="<div class=\"overlay\"><p>Rutger: Nice example</p></div></div>";

		body += "<div class=\"sliderblock chat\" id=\"chat_block2\">";
		body+="<img class=\"sliderimg\" src=\"/eddie/img/people/david.png\"  />";
		body+="<div class=\"timecode redt\">2:13</div>";
		body+="<div class=\"overlay\"><p>David: Hello anyone here ?</p></div></div>";

		body += "<div class=\"sliderblock chat\" id=\"chat_block3\">";
		body+="<img class=\"sliderimg\" src=\"/eddie/img/people/david.png\"  />";
		body+="<div class=\"timecode redt\">1:04</div>";
		body+="<div class=\"overlay\"><p>David: I have seen better</p></div></div>";

		body += "<div class=\"sliderblock chat\" id=\"chat_block4\">";
		body+="<img class=\"sliderimg\" src=\"/eddie/img/people/daniel.png\"  />";
		body+="<div class=\"timecode redt\">55</div>";
		body+="<div class=\"overlay\"><p>Daniel: I agree with Rutger</p></div></div>";	

		
		body += "<div class=\"sliderblock chat\" id=\"chat_block5\">";
		body+="<img class=\"sliderimg\" src=\"/eddie/img/people/kati.png\"  />";
		body+="<div class=\"timecode redt\">20</div>";
		body+="<div class=\"overlay\"><p>Kati: Are there more episodes ?</p></div></div>";
		return body;
	}
}
