/*
 * Copyright (c) 2013 Dror. All rights reserved
 * <p/>
 * The software source code is proprietary and confidential information of Dror.
 * You may use the software source code solely under the terms and limitations of
 * the license agreement granted to you by Dror.
 */

package dashboard;

import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Reads Logs 
 *    - CDNs from Amazon CloudFront backet 
 *    - Version Check
 *    - ...
 * and Import log events to Mixpanel
 *
 */

public class ImportMain
{
   static String MIXPANEL_TOKEN = "";
   static String MIXPANEL_API_KEY = "";
   static String bucketName = "";
   static String AWS_USER = "";
   static String AWS_PASS = "";
   static String DELETE_PROCESSED_LOGS = "";
   static String DT_API_USER = "";
   static String DT_API_PASS = "";
   static String versionCheckURL = "";
   static String versionCheckPass = "";
   static String VC_IP_FILE = "";
   static String CDN_IP_FILE = "";
   static String ERR_IP_FILE = "";
   static String FROM_TIME = "5/22/13 00:00 AM";

   public static void main(String[] args)
 
    {
      int n = 0;
      String propertiesFileName = "";
      AmazonLogs al = new AmazonLogs();
      VersionCheck vc = new VersionCheck();
      String whatToImport  = "CDN";
      
      // First argument - number of events to import
      if (args.length > 0) {
        try {
           n = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
           System.err.println("First argument must be an integer");
           System.exit(1);
        }
      } else { 
           System.err.println("Please specify:");
           System.err.println("       - Max number of events to import.");
           System.err.println("       - Properties file name.");
           System.err.println("       - What to import: VC / CDN.");
           System.exit(1);
      }

      // Second argument - properties file name
      if (args.length > 1) 
        propertiesFileName = args[1];
      else 
        propertiesFileName = "gigaDashboard.properties";
      
      // Read Properties file
      Properties prop = new Properties(); 
      try {
           //load a properties file
    		   prop.load(new FileInputStream(propertiesFileName));
         
           // Another option - load default properties from the Jar
           //prop.load(ImportCDN.class.getResourceAsStream("/gigaDashboard.properties"));
 
           //get the property values
           MIXPANEL_TOKEN = prop.getProperty("MIXPANEL_TOKEN");
           MIXPANEL_API_KEY = prop.getProperty("MIXPANEL_API_KEY");

           bucketName = prop.getProperty("S3_BUCKET_NAME");
           AWS_USER = prop.getProperty("AWS_USER");
           AWS_PASS = prop.getProperty("AWS_PASS");
           DELETE_PROCESSED_LOGS = prop.getProperty("DELETE_PROCESSED_LOGS");

           DT_API_USER = prop.getProperty("DOMAINTOOLS_USER");
           DT_API_PASS = prop.getProperty("DOMAINTOOLS_API_KEY");

           versionCheckURL  = prop.getProperty("VERSION_CHECK_LOG");
           versionCheckPass = prop.getProperty("VERSION_CHECK_PASS");
           FROM_TIME = prop.getProperty("FROM_TIME");
           VC_IP_FILE = prop.getProperty("VC_IP_FILE");
           CDN_IP_FILE = prop.getProperty("CDN_IP_FILE");
           
           CDN_IP_FILE = prop.getProperty("CDN_IP_FILE");
           ERR_IP_FILE = prop.getProperty("ERR_IP_FILE");
                              
       		System.out.println("DELETE_PROCESSED_LOGS = " + DELETE_PROCESSED_LOGS);
    		   System.out.println("S3_BUCKET_NAME = " + prop.getProperty("S3_BUCKET_NAME"));
           System.out.println( "===================");
 
    	} catch (IOException ex) {
    		//ex.printStackTrace();
        System.err.println("Can't find Propertie file - " + propertiesFileName);
        System.err.println("Second argument must be properties file name");
        System.exit(1);
      }

      // READ LOG FILES (Amazon CDNS, Version Check CSV, ...)
      // AND IMPORT EVENTS TO MIXPANEL
      try {
         // Third argument - WHAT TO DO
         if (args.length > 2) 
            whatToImport = args[2];

            System.out.println("\n>>> Starting to import " + n + "  " + whatToImport + " events ............. \n");

            if ( whatToImport.equals("VC") )
               vc.importVersionCheckLog( n, DT_API_USER, DT_API_PASS, VC_IP_FILE, ERR_IP_FILE,
                                         versionCheckURL, versionCheckPass, 
                                         MIXPANEL_API_KEY, MIXPANEL_TOKEN,
                                         FROM_TIME);

            if (whatToImport.equals("CDN") )
               al.readAmazonLogs( n, AWS_USER, AWS_PASS, CDN_IP_FILE, ERR_IP_FILE, 
                                  bucketName, DELETE_PROCESSED_LOGS, 
                                  MIXPANEL_API_KEY, MIXPANEL_TOKEN, DT_API_USER, DT_API_PASS);

      } catch (Exception e) {
         e.printStackTrace();
      }   
    }    
}