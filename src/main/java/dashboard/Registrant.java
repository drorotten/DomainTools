package dashboard;


public class Registrant
{
   String ip = "";
   String name = "";
   int counter = 0;
        
   public  Registrant(){
      this.ip = "";
      this.name = "";
      this.counter = 0;
   }
   public  Registrant(String ip, String name){
      this.ip = ip;
      this.name = name;
      this.counter = 0;
   }

   public  Registrant(String ip, String name, int counter){
      this.ip = ip;
      this.name = name;
      this.counter = counter;
   }
    
   public boolean equal(Registrant r2) {
     if (this.ip == r2.ip & this.name == r2.name)
         return true;
     else 
         return false;
   }
      
}