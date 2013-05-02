/**
 * Main
 *
 * @author 
 *
 */
package dashboard;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.codehaus.jackson.JsonNode;

import com.domaintoolsapi.exceptions.DomainToolsException;


/**
 * Sample class to use DomainAPI java library.
 * 
 * @author Julien SOSIN
 */
public class Main {
/*
	public static void main(String[] args) {
		//args[0] your username, args[1] : your api_key
    String propertiesFileName = "";
    String apiuser = "";
    String apipass = "";
    String ip = "";
           
    if (args.length > 0)       
         ip = args[0];
    else { 
           System.err.println("Please specify IP address or Domain name.");
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
         
           //get the property values
           apiuser = prop.getProperty("DOMAINTOOLS_USER");
           apipass = prop.getProperty("DOMAINTOOLS_API_KEY");
    } catch (IOException ex) {
    		ex.printStackTrace();
        System.err.println("Can't find Propertie file - " + propertiesFileName);
        System.err.println("Second argument must be properties file name");
        System.exit(1);
    }
    
	  Whois wi = new Whois();
    String registrant = wi.whois(ip, apiuser, apipass);
  	System.out.println("\n\n>> IP: "+ ip + "   Registrant: " + registrant );
    
*/   	
/*		JsonNode jsonNode;
		String xml_response;
    DomainTools domainTools = new DomainTools(apiuser, apipass);
    try {
			DTRequest dtRequest = domainTools.use("whois");
			// Whois in XML on domaintools.com
      //System.out.println("\n>>>>>   Whois in XML on " + ip + "  ========\n");
			//dtRequest.on(ip).toXML();
			//xml_response = dtRequest.getXML();
			//System.out.println(xml_response);

      System.out.println("\n>>>>>   Whois in JSON on " + ip + "  ========\n");
      dtRequest.on(ip).toJSON();
			jsonNode = dtRequest.getObject();
      
      registrant = jsonNode.get("response").get("registrant").getValueAsText();
			System.out.println("\n\n>> Registrant : " + registrant );
      
			//System.out.println("\n\n" + jsonNode );
			//Iterator<JsonNode> it = jsonNode.get("response").get("registrant").getElements();
			//if (it.hasNext()){
			//	System.out.println("   " + it.next());
			//}
		
    } catch (DomainToolsException e) {
			e.printStackTrace();
		}
  
  
	}
 */
}