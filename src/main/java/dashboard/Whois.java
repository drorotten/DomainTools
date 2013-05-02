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
    
     public String whois( String ip, String apiuser, String apipass ) {
   
   		 
		   JsonNode jsonNode;
       String registrant = "";  
       
       DomainTools domainTools = new DomainTools(apiuser, apipass);
       
       try {
      
      			DTRequest dtRequest = domainTools.use("whois");
            dtRequest.on(ip).toJSON();            
      			jsonNode = dtRequest.getObject();
             
            registrant = jsonNode.get("response").get("registrant").getTextValue();
    
      			//System.out.println(">>>>>> " + ip + " - " + registrant );
		
       } catch (DomainToolsException e) {
			      e.printStackTrace();
		   }
     return registrant;     
    }
}
