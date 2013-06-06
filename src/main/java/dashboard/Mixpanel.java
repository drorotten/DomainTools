/*
 * Copyright (c) 2013 Dror. All rights reserved
 * <p/>
 * The software source code is proprietary and confidential information of Dror.
 * You may use the software source code solely under the terms and limitations of
 * the license agreement granted to you by Dror.
 */

package dashboard;

import java.io.IOException;
import java.util.Date;

// For date conversion to UNIX epoch format
import java.text.SimpleDateFormat;

import org.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

// For the HTTP POST
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

// Mixpanel
//import com.mixpanel.mixpanelapi.ClientDelivery;
//import com.mixpanel.mixpanelapi.MessageBuilder;
//import com.mixpanel.mixpanelapi.MixpanelAPI;

public class Mixpanel
{
//int i = 0;

   // =============================
   // Post CDN event to Mixpanel
   //
   // =============================
   public int postCDNEventToMixpanel(String API_KEY, String TOKEN, 
                                     String ip, String eventName, 
                                     String eventTime, String method,
                                     String fileName, String fName, 
                                     String userAgent, 
                                     String statusCode, 
                                     String registrant) throws IOException 
   {
  
   try {
      SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date date = sdf.parse( eventTime );
      long timeInSecSinceEpoch = date.getTime();
      if (timeInSecSinceEpoch > 0) 
          timeInSecSinceEpoch = timeInSecSinceEpoch / 1000;   

      JSONObject obj1 = new JSONObject();
      obj1.put("distinct_id", ip);
      obj1.put("ip", ip);
      obj1.put("File path", fileName);
      obj1.put("File name", fName);
      obj1.put("User agent", userAgent);
      obj1.put("Status code", statusCode);
      obj1.put("Method", method);
      obj1.put("Registrant", registrant);
      obj1.put("time", timeInSecSinceEpoch ); 
      obj1.put("token", TOKEN);
   
      JSONObject obj2 = new JSONObject();
      obj2.put("event", eventName);
      obj2.put("properties", obj1);

      String s2 = obj2.toString();
      String encodedJSON = Base64.encodeBase64String(StringUtils.getBytesUtf8(s2));
      
      return postRequest( encodedJSON, API_KEY );
     } catch (Exception e) {
         //throw new RuntimeException("Can't POST to Mixpanel.", e);
         e.printStackTrace();
         return 0;
     }

   }
  
   // ========================================
   // Post "Version Check" event to mixpanel
   //
   // ========================================
   public int postVersionCheckToMixpanel(String API_KEY, String TOKEN,
                                         String ip, String registrant, 
                                         String eventName, 
                                         String eventTime, 
                                         String buildNum ) throws IOException 
   { 
   String pattern = "M/dd/yy h:mm a";
   SimpleDateFormat sdf  = new SimpleDateFormat(pattern);
   try {
      Date date = sdf.parse( eventTime );
      long timeInSecSinceEpoch = date.getTime();
      if (timeInSecSinceEpoch > 0) 
          timeInSecSinceEpoch = timeInSecSinceEpoch / 1000;   
      
      JSONObject obj1 = new JSONObject();
      obj1.put("distinct_id", ip);
      obj1.put("ip", ip);
      obj1.put("registrant", registrant);
      obj1.put("build", buildNum);
      obj1.put("time", timeInSecSinceEpoch ); 
      obj1.put("token", TOKEN);
   
      JSONObject obj2 = new JSONObject();
      obj2.put("event", eventName);
      obj2.put("properties", obj1);

      String s2 = obj2.toString();
      String encodedJSON = Base64.encodeBase64String(StringUtils.getBytesUtf8(s2));
      
      return postRequest(  encodedJSON, API_KEY );
    } catch (Exception e) {
	     System.out.println("\n>>> Can't POST Version Check event to Mixpanel.");
       //throw new RuntimeException("Can't POST to Mixpanel.", e);
       return 0;
    }
   }

   // ========================================
   // Post event to mixpanel
   //
   // ========================================
   public int postRequest( String encodedDataJSON, String api_key)
   {
    try {
     String contents = ""; 
     HttpClient client = new HttpClient();
     PostMethod method = new PostMethod( "http://api.mixpanel.com/import" );

	   // Configure the form parameters
	   method.addParameter( "data", encodedDataJSON );
	   method.addParameter( "api_key", api_key );
    
     // Add more details in the POST response 
	   method.addParameter( "verbose", "1" );

	   // Execute the POST method
 
     int statusCode = client.executeMethod( method );
     contents = method.getResponseBodyAsString();
     method.releaseConnection();
     
     if (statusCode != 200 || contents.charAt(11) != '1') {  // Post to Mixpanel Failed
        System.out.println("Mixpanel Post respone: " + Integer.toString(statusCode) + " - " + contents);
        return 0;
     }
     return 1;
    }
    catch( Exception e ) {
        e.printStackTrace();
        return 0;
    }
  }
}