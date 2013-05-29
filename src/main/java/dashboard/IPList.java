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
import java.io.IOException;
import java.io.*;

public class IPList {
   
    // ==== CHECK IF IP ALREADY IN LIST ====
    public int ipInList(String ip, ArrayList<Registrant> rList) {
       int size = rList.size();
       int i;
       
       for (i=0;i<size;i++) 
           if (rList.get(i).ip.equals(ip))  
                 return i;      // ip ALREADY EXIST IN LIST
           
        return -1;
    }
    
    // ==== PRINT LIST ====
    public void  printList( ArrayList<Registrant>  rList, int saf) {
       int size = rList.size();
       int c = 0;
       
       System.out.println("\n>>> ----------- Printing List of IPs with counter above " + saf);
       int i;
       
       for (i=0;i<size;i++) {
          c = rList.get(i).counter;
          if  (c >= saf)
           System.out.println(">>> " + (i+1) + " - " + rList.get(i).ip + " - " + rList.get(i).name + "  >> " + c);
       }
       System.out.println("\n------------------------- List size = " + size + "\n");
       
    }
    
    // ====  SAVE LIST TO FILE ======
    public void saveList(ArrayList<Registrant> rList, String fName) {
      try {  
        FileWriter fos = new FileWriter( fName );
        PrintWriter out = new PrintWriter(fos);

        // Write column-headers line
        out.write("IP Address" + "," + "Registrant" + "," + "CDNs");
        out.write("\n");

        for (int i = 0; i < rList.size(); i++) {

            out.write(rList.get(i).ip+","+rList.get(i).name+ "," + rList.get(i).counter);
            out.write("\n");
        }
        out.close();

      } catch (Exception e) {
        System.out.println("\n\n CAN'T WRITE TO FILE - " + fName + "\n");
      }
   }
    
    // ====  Load LIST FROM FILE ======
    public void loadList( ArrayList<Registrant> rList, 
                          String fName ) throws IOException, ClassNotFoundException {
      
       BufferedReader CSVFile = null;
       String dataRow = null;

       String ip = "";
       String name = "";
       int counter = 0;
       int x=0;
       
       try {
           CSVFile = new BufferedReader(new FileReader(fName));
       } catch (FileNotFoundException e) {
          //e.printStackTrace();
           System.out.println("\n\n CAN'T OPEN FILE - " + fName + " Creating a new IP LIST file\n");
           return;
       }

       try {
           // Read colum-headers line     
           dataRow = CSVFile.readLine();
           // Read first line
           if (dataRow != null) 
              dataRow = CSVFile.readLine();
       } catch (IOException e) {
           System.out.println("\n\n CAN'T READ LINES FROM FILE - " + fName + " \n");
           return;
       }

       while (dataRow != null) {
          String[] dataArray = dataRow.split(",");
          x = 0;
          for (String ttt : dataArray) x++;
          if (x == 2) {
             ip = dataArray[0];
             name = dataArray[1];
             counter = 1;
             rList.add(new Registrant(ip, name, counter));
          }
          if (x == 3) {
             ip = dataArray[0];
             name = dataArray[1];
             counter = Integer.parseInt(dataArray[2]);
             rList.add(new Registrant(ip, name, counter));
         
          }
          
          try {
              dataRow = CSVFile.readLine();
          } catch (IOException e) {
             System.out.println("\n NO MORE LINES TO READ IN FILE - " + fName + " \n");
          }
      } // while

      try {
          CSVFile.close();
      } catch (IOException e) {
          //e.printStackTrace();
          System.out.println("\n ERROR CLOSING FILE - " + fName + " \n");
      }
        
       //System.out.println("\n SUCCESSFULY READ LIST FROM FILE - " + fName + "\n");        
    }
}
