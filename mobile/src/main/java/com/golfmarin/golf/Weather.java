package com.golfmarin.golf;

import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.net.URL;
import java.net.URLConnection;

//import android.util.Log;
public class Weather {
	
	private RegionListActivity regionListActivity = null;
    private SAXParser saxParser = null;
    private URL weatherRSS = null;
    private URLConnection weatherConnection = null;
    private InputStream xmlStream= null;
	private String currentElement;
    private String localWeather = null;
    private String title = null;
    private String description = null;

    // Constructor
    public Weather(RegionListActivity activity) {
    	regionListActivity = activity;
        try {
            // Create a SAX parser factory
            SAXParserFactory factory = SAXParserFactory.newInstance(); 
                   
            // Obtain a SAX parser
            saxParser = factory.newSAXParser();                  
        } 
        catch (Exception e) {
                e.printStackTrace();
        }      
    }
    
    
    // Method to start the weather rss
    public void getWeather (Region region) {
        // XML Stream
    	try {
        weatherRSS = new URL("http://weather.yahooapis.com/forecastrss?w=" + region.woeid);
        weatherConnection = weatherRSS.openConnection();
        xmlStream = weatherConnection.getInputStream();
                
        // Parse the given XML document using the callback handler
        saxParser.parse(xmlStream, new MySaxHandler()); 
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
	public interface Callbacks {
		/**
		 * Callback for when weather is available.
		 */
		public void onLocalWeatherReady(String weather);
	}


    /*
     * Inner class for the Callback Handlers.
     */
    class MySaxHandler extends DefaultHandler {
            boolean isItem = false;
            
            // Callback to handle element start tag
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    currentElement = qName;
                    if (currentElement.equals("item")) { 
                            isItem = true;
                            description = new String();
                    }
            }

            // Callback to handle element end tag
            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {

                    if (qName.equals("item")) {
                    	isItem = false;
                    	try{
                        saxParser.reset();
                    	xmlStream.close();
                    	}
                    	catch(Exception e){
                    		e.printStackTrace();
                    	}
                    	
	                //    Log.v("myApp", "title and description: " + title + " " + description);
	                    localWeather = title + description;
	                    regionListActivity.onLocalWeatherReady(localWeather);
                    }
                    else {
                    	currentElement = "";
                    }
            }

            // Callback to handle the character text data inside an element
            @Override
            public void characters(char[] chars, int start, int length) throws SAXException {
                    if (currentElement.equals("title") && (isItem == true)) {
                            title = new String(chars, start, length) + "<br />";
                            
                    } else if (currentElement.equals("description")) {
                            description = description + new String(chars, start, length);
                            description = description.replace("null", "");
                     //       Log.v("myApp", "Weather callback, description: " + description);
                    }
            }
            

    }

}
