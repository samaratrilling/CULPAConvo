package opendial.modules.examples.apimodule;
import java.io.*;
import java.util.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import opendial.DialogueSystem;

public class APIAgent {
    Map<String, String> profIDs = new HashMap<String, String>();
    
   public APIAgent() throws IOException {
       String cwd = System.getProperty("user.dir");
       String[] dirs = cwd.split("opendial-1.1");
       String filename = dirs[0] + "data/CS_profID.txt";
       System.out.println(filename);
       try {
           BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
           while (br.ready()) {
               String str = br.readLine();
               String[] input = str.split(",");
               profIDs.put(input[0], input[1]);
           }
       } catch (FileNotFoundException fe) {
           System.out.println("Error: Couldn't find data file.");
       }

   }

   public static void main (String[] args) {
      // Tester
      // String id = getProfID("Michael Collins");
      // System.out.println(query("reviews", "professor_id", id, "latest"));
	 
   }

    public String getProfID(String profName) {
        if (profIDs.containsKey(profName)) {
            return profIDs.get(profName);
        } else {
            return "0";
        }
    }
   
   public static String query(String endpoint, String searchBy, String searchTerm, String modifier) {
      String response = "";
      try {
          // Build up the URL.
          String url = buildURL(endpoint, searchBy, searchTerm);
          String jsonResponse = httpGet(url);
	 // DialogueSystem.log.info("query=" + url);
	  //DialogueSystem.log.info("response=" + jsonResponse);

          response = parseResponse(jsonResponse, modifier);
          DialogueSystem.log.info("response=" + response);
      }
      catch (IOException ioe) {
         ioe.printStackTrace();
      }
      catch (JSONException jse) {
         jse.printStackTrace();
      }
      return response; 
   }
   
   public static String buildURL(String endpoint, String searchBy, 
         String searchTerm){
	   String base = "http://api.culpa.info";
	   String url = base + "/" + endpoint + "/" + searchBy + "/" + searchTerm;
	   return url;
   }
   
   public static String parseResponse(String jsonSrc, String modifier) throws JSONException {
      JSONObject obj = new JSONObject(jsonSrc);
      String response = "";

      if (modifier.equals("latestReview")) {
	 JSONArray reviews = obj.getJSONArray("reviews");
    	 Integer i = reviews.length() - 1;
    	 JSONObject review = reviews.getJSONObject(i);
    	 String latest_review_txt = review.getString("review_text");
    	 
    	 response = latest_review_txt;
      }
      else if (modifier.equals("numberReviews")) {
         JSONArray reviews = obj.getJSONArray("reviews");
         response = String.valueOf(reviews.length());
      }
      
      else if (modifier.equals("firstSentence")) {
     	 //JSONArray reviews = obj.getJSONArray("reviews");
     	 
     	 response = "";
     	 JSONArray reviews = obj.getJSONArray("reviews");
    	  
  	 for (int i=0; i<reviews.length(); i++){
  		    JSONObject review = reviews.getJSONObject(i);
  		    String review_txt = review.getString("review_text");  
     	    String firstSentence = review_txt.split("\\.")[0];
     	    response += firstSentence + ".  ";
  	 }
     	 
       }
       
      else if (modifier.equals("adjectives")) {
    	  response = "";
    	      	  
    	  try{
    	  String parsing_url = "http://text-processing.com/api/tag/";
    	  
    	  JSONArray reviews = obj.getJSONArray("reviews");
              	  
    	  for (int i=0; i<reviews.length(); i++){
    		  JSONObject review = reviews.getJSONObject(i);
    		  String review_txt = review.getString("review_text");   		  
        		  
    		  //call parsing API to get parse of review text
    		  String param = "text=" + URLEncoder.encode(review_txt, "UTF-8");
    		  String parsing_jsonResponse = httpPost(parsing_url, param);
    		  
    		  JSONObject obj2 = new JSONObject(parsing_jsonResponse);
    		  String text = obj2.getString("text");
    		  
       	      String[] parts = text.split("\\n");
       	      for (int j=0; j<parts.length; j++){
       	    	  if (parts[j].endsWith("/JJ")){
       	    		  //System.out.println(parts[j]);
       	    		  String adj = parts[j].split("\\/")[0];
       	    		  response += adj + ", ";
       	    		  
       	    	  }
       	      }
       	      

    	  }
    	  
          
    	  }
          catch (IOException ioe) {
              ioe.printStackTrace();
           }
          
      }
      
      
      else if (modifier.equals("sentiment")) {
    	  int pos = 0;
    	  int neg = 0;
    	  int neutral = 0;
    	      	  
    	  try{
    	  String sentiment_url = "http://text-processing.com/api/sentiment/";
    	  
    	  JSONArray reviews = obj.getJSONArray("reviews");
              	  
    	  for (int i=0; i<reviews.length(); i++){
    		  JSONObject review = reviews.getJSONObject(i);
    		  String review_txt = review.getString("review_text");   		  
    		  
    		  //call sentiment API to get polarity of review text
    		  String param = "text=" + URLEncoder.encode(review_txt, "UTF-8");
    		  String sentiment_jsonResponse = httpPost(sentiment_url, param);

    		  JSONObject obj2 = new JSONObject(sentiment_jsonResponse);
    		  String label = obj2.getString("label");
    		  
    		  //increment polarity counts
    		  if(label.equals("pos")){pos += 1;}
    		  else if(label.equals("neg")){neg += 1;}
    		  else if(label.equals("neutral")){neutral += 1;}

    	  }
          
          response = "There are " + pos + " positive reviews and " + neg + " negative reviews and " + neutral + " neutral reviews.";
    	  }
          catch (IOException ioe) {
              ioe.printStackTrace();
           }
          
      }
       

      return response;
   }
   
   /**
    * Source: http://rest.elkstein.org/2008/02/using-rest-in-java.html
    */
   private static String httpGet(String urlStr) throws IOException {
	   URL url = new URL(urlStr);
	   HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	   
	   if (connection.getResponseCode() != 200) {
		   throw new IOException(connection.getResponseMessage());
	   }
	   
	   // Buffer the result into a string
	   BufferedReader rd = new BufferedReader(
	       new InputStreamReader(connection.getInputStream()));
	   StringBuilder sb = new StringBuilder();
	   String line;
	   while ((line = rd.readLine()) != null) {
	     sb.append(line);
	   }
	   rd.close();

	   connection.disconnect();
	   return sb.toString();
   }
   
   public static String httpPost(String targetURL, String urlParameters)
{
  URL url;
  HttpURLConnection connection = null;  
  try {
    //Create connection
    url = new URL(targetURL);
    connection = (HttpURLConnection)url.openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", 
         "application/x-www-form-urlencoded");
			
    connection.setRequestProperty("Content-Length", "" + 
             Integer.toString(urlParameters.getBytes().length));
    connection.setRequestProperty("Content-Language", "en-US");  
			
    connection.setUseCaches (false);
    connection.setDoInput(true);
    connection.setDoOutput(true);

    //Send request
    DataOutputStream wr = new DataOutputStream (
                connection.getOutputStream ());
    wr.writeBytes (urlParameters);
    wr.flush ();
    wr.close ();

    //Get Response	
    InputStream is = connection.getInputStream();
    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
    String line;
    StringBuffer response = new StringBuffer(); 
    while((line = rd.readLine()) != null) {
      response.append(line);
      response.append('\r');
    }
    rd.close();
    return response.toString();

  } catch (Exception e) {

    e.printStackTrace();
    return null;

  } finally {

    if(connection != null) {
      connection.disconnect(); 
    }
  }
}

}
