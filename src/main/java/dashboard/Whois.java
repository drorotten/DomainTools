/*
 * Copyright (c) 2013 Dror. All rights reserved
 * <p/>
 * The software source code is proprietary and confidential information of Dror.
 * You may use the software source code solely under the terms and limitations of
 * the license agreement granted to you by Dror.
 */

package dashboard;

import org.codehaus.jackson.JsonNode;
import com.domaintoolsapi.exceptions.DomainToolsException;

  
public class  Whois {
     private DomainTools domainTools = null;
     
     public Whois( String apiuser, String apipass ) {
       this.domainTools = new DomainTools(apiuser, apipass);
     }
         		 
     public String whoisIP( String ip ) {
		   JsonNode jsonNode;
       String registrant = "";  
             
       try {
      
      			DTRequest dtRequest = domainTools.use("whois");
            dtRequest.on(ip).toJSON();            
      			jsonNode = dtRequest.getObject();
             
            registrant = jsonNode.get("response").get("registrant").getTextValue();
    
      			//System.out.println(">>>>>> " + ip + " - " + registrant );
		
       //} catch (DomainToolsException e) {
       } catch (Exception e) {
			      //e.printStackTrace();
            System.out.println("\n ===== DOMAIN TOOL EXCEPTION ====");
            System.out.println(    " =====       " + ip + " " + registrant + "    ====\n");
            registrant = "ERROR";
		   }
     
     return registrant;     
    }
}
