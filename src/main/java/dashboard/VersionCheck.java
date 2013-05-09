/*
 * Copyright (c) 2013 Dror. All rights reserved
 * <p/>
 * The software source code is proprietary and confidential information of Dror.
 * You may use the software source code solely under the terms and limitations of
 * the license agreement granted to you by Dror.
 */

package dashboard;

import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

public class VersionCheck 
{      
   // =======================================================================
   // Read log file (CSV) and Import the "Version Check" operations to Mixpanel 
   //
   //
   // =======================================================================
   public int importVersionCheckLog(int n, String apiuser, String apipass, String IPfile,
                                    String versionCheckURL, String versionCheckPass,
                                    String API_KEY, String TOKEN)  throws Exception 
   {
      int lines = 0;
      String ip = "";
	    String registrant = "";
      Mixpanel mix = new Mixpanel();
      String build = "";
      String eventTime = "3/14/13 10:10 AM";
      int x = 0;      
      int mixpanelStatus = 0;
      int errors = 0;      
      String prevIP = "";    
      int index = 0;
      Registrant r;
      ArrayList<Registrant> rList = new ArrayList<Registrant>();
      IPList ipl = new IPList();
      Whois w = new Whois( apiuser, apipass);
      
      System.out.println(">>>  Version Check log - " + versionCheckURL);
      URL logURL = new URL( versionCheckURL );
      String base64EncodedString = Base64.encodeBase64String(StringUtils.getBytesUtf8( versionCheckPass ));

      URLConnection conn = logURL.openConnection();
      conn.setRequestProperty ("Authorization", "Basic " + base64EncodedString);

      InputStream in = conn.getInputStream();       
      BufferedReader br = null;
      
      try {
        br = new BufferedReader( new InputStreamReader (in));       
        String inputLine = br.readLine();

        // Skip first line (headers)
        if (inputLine != null)  inputLine = br.readLine();

        // Load list of IP - REGISTRANT               
        ipl.loadList(  rList, IPfile );
        ipl.printList(rList, 5);

 	    // Loop - limited to n cycles (parameter defined by user)
        while (inputLine != null & lines < n) {
           String[] dataArray = inputLine.split(",");
           x = 0;
           for (String ttt : dataArray) x++;
           if (x == 3) {
              ip = dataArray[0];
              build = dataArray[1];
              eventTime = dataArray[2];
           }
           else if (x == 4) {
              errors++;
              ip = dataArray[1];
              build = dataArray[2];
              eventTime = dataArray[3];
           }
           
           if (ip != prevIP)
               prevIP = ip;
               index = ipl.ipInList(ip, rList);
               if (index >= 0) {
                  r = rList.get(index);
                  registrant = r.name;   
                  // Update counter for this IP
                  r.counter = r.counter + 1;
                  rList.set(index, r);
               } else {
                  // WHOIS - Check registrant of this IP address
                  registrant = w.whoisIP( ip );
                  // If name includes a comma, exclude the comma
                  registrant = registrant.replace(",", "");
                  rList.add(new Registrant(ip, registrant, 1));
               }
           }
           
           inputLine = br.readLine(); // Read next line of data.
           lines++;
     
           // Track the event in Mixpanel (using the POST import) - event time is in the PAST
           mix.postVersionCheckToMixpanel(API_KEY, TOKEN, ip, registrant, "Version-Check", eventTime, build);
           
      } // while
    } catch (IOException e) {
         e.printStackTrace();
    } finally {   
       // Close the file once all data has been read.
       if (br != null) br.close();

       ipl.printList(rList, 5);                
       ipl.saveList(rList, IPfile);
    
       return lines;
    }
   }
}