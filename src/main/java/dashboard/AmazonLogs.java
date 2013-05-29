/*
 * Copyright (c) 2013 Dror. All rights reserved
 * <p/>
 * The software source code is proprietary and confidential information of Dror.
 * You may use the software source code solely under the terms and limitations of
 * the license agreement granted to you by Dror.
 */

package dashboard;

import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.*;

import java.util.zip.GZIPInputStream;

// Amazon
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

public class AmazonLogs
{

   // ======================================  
   // Read Amazon CloudFront Logs (Zip files) 
   // 
   // ======================================
   public int readAmazonLogs(int n, 
                             String AWS_USER, String AWS_PASS, String IPfile, String ERRfile,  
                             String bucketName, String DELETE_PROCESSED_LOGS,
                             String API_KEY, String TOKEN,
                             String apiuser, String apipass) throws Exception {
                             
        if (n < 1) return 0;  
        int eventsNumber = 0;
        String line = null;
        int begin = 0;
        int zips = 0;
        int deletedZips = 0;
        int mixpanelStatus = 0;
        String registrant = "";
        String ip = "";
        String prevIP = "";    
        Mixpanel mix = new Mixpanel();
        Whois w = new Whois(apiuser, apipass);
        int index = -1;
        Registrant r;
        ArrayList<Registrant> rList = new ArrayList<Registrant>();
        ArrayList<Registrant> eList = new ArrayList<Registrant>();
        IPList ipl = new IPList();
        IPList errl = new IPList();
        
        // Log files Bucket
        AWSCredentials credentials = new BasicAWSCredentials(AWS_USER,AWS_PASS);
        AmazonS3Client s3Client = new AmazonS3Client(credentials);
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName);
              
        BufferedReader br = null;
        
        ObjectListing objectListing = s3Client.listObjects(listObjectsRequest);
        ObjectListing nextObjectListing = objectListing;
        zips = 0;
        Boolean more = true;
        if (objectListing == null) 
            more = false;
        else {
            ipl.loadList(  rList, IPfile );
            ipl.printList(rList, 30);
        }
                    
        while (more) {
        // Reads 1000 files
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
          // Handle  ZIP files        
          
          try { // Open and send to mixpanel events of one ZIP file  
            String key = objectSummary.getKey();
         
            S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, key));
            // Extract ZIP and read Object to reader
            br = new BufferedReader(new InputStreamReader(new GZIPInputStream(object.getObjectContent())));
            zips++;      
            
            // Read the lines from the unzipped file, break it and send to Mixpanel
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) continue;
                if (line.trim().equals("")) continue;
                String[] values = line.split("\\s");  
                
                String eventTime = values[0] + " " + values[1];
                ip = values[4];
                
                if (ip != prevIP) {
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
                       // if there was an error, try again
                       if (registrant.equals("ERROR"))
                            registrant = w.whoisIP( ip );
                            
                       // if there was a second error, add it to errors list
                       if (registrant.equals("ERROR")) {
                          eList.add(new Registrant(ip, registrant, 1));
                       } else  {
                          // If name includes a comma, exclude the comma
                          registrant = registrant.replace(",", "");
                          rList.add(new Registrant(ip, registrant, 1));
                       }
                   }
                }
                
                String method = values[5];
                String fileName = values[7];
                String statusCode = values[8];
                String userAgent = values[10];
                String fName = fileName;
                
                if (fileName.contains("gigaspaces-")) {
                   begin = fileName.lastIndexOf("gigaspaces-") + 11;
                   fName = fileName.substring(begin, fileName.length()); 
                }

                eventsNumber++; 
                System.out.println(eventsNumber + ": " + eventTime + " " + ip + " " + registrant );

                // ====================================================
                // Track the event in Mixpanel (using the POST import)
                // ====================================================
                mixpanelStatus = mix.postCDNEventToMixpanel(API_KEY, TOKEN, ip, "Cloudfront CDN", eventTime, method,  fileName, fName, userAgent, statusCode, registrant);
      
            } // while on ZIP file lines
     
            if (mixpanelStatus == 1 & DELETE_PROCESSED_LOGS.equals("YES")) { 
                  // Delete the CDN log ZIP file
                  s3Client.deleteObject(bucketName, key);
                  System.out.println("========= Deleted Zip " + zips + " ===== List Size " + rList.size() + " =========="); 
                  deletedZips++;
            }
         } catch (IOException e) {
			
               e.printStackTrace();
               return eventsNumber;
		     } finally {
				     if (br != null) {
                br.close();
             }
   
             if (eventsNumber >= n) { 
                System.out.println("\n>>> " + eventsNumber + " events in " + zips + " Zip files. Deleted " + deletedZips + " Zip files.\n");
                
                ipl.printList(rList, 100);                
                ipl.saveList(rList, IPfile);
                
                if (!eList.isEmpty()) {
                   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                   String fName = ERRfile + sdf.format( new Date() ) + ".txt";

                   System.out.println("\n>>> " + eList.size() + " DomainTools errors:"); 
                   errl.saveList(eList, fName );
                } else 
                   System.out.println("\n>>> No DomainTools errors"); 
                
                return eventsNumber;
            }
         }
                
        } // for (continue to next ZIP file
        
        // If there are more ZIP files, read next batch of 1000
        if (objectListing.isTruncated()) {
            nextObjectListing = s3Client.listNextBatchOfObjects(objectListing);
            objectListing = nextObjectListing;
        } else 
            more = false; // no more files
        
       } // while next objectListing
        
       System.out.println("\n>>> " + eventsNumber + " events in " + zips + " Zip files. Deleted " + deletedZips + " Zip files.\n");
       ipl.printList(rList, 50);

       ipl.saveList(rList, IPfile);
                
       if (!eList.isEmpty()) {
                   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                   String fName = ERRfile + sdf.format( new Date() ) + ".txt";

                   System.out.println("\n>>> " + eList.size() + " DomainTools errors:"); 
                   errl.saveList(eList, fName );
       } else 
                   System.out.println("\n>>> No DomainTools errors"); 
       
       return eventsNumber;
    }     
}