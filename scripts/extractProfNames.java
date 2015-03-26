import java.io.*;
public class extractProfNames {
public static void main (String[] args) {

   try {
      BufferedReader br = new BufferedReader(new FileReader("professors.txt"));
      PrintWriter pw = new PrintWriter(new File("profNames.txt"));
      String line = br.readLine();
      while (line != null) {
         String name;
        if (line.contains("first_name")) {
           String[] tokens = line.split("\\W");
             for (String token : tokens) {
               if (token.length() > 0 && !token.equals("first_name")) {
                    pw.print(token + " ");
               }
            }
        }
        if (line.contains("last_name")) {
           String[] tokens = line.split("\\W");
             for (String token : tokens) {
                if (token.length() > 0 && !token.equals("last_name")) {
                    pw.print(token + "\n");
                }
             }
        }
                    
        line = br.readLine();
      }
      br.close();
      pw.close();
   }
   catch (IOException e) {
      e.printStackTrace();
   }
   
}
}
