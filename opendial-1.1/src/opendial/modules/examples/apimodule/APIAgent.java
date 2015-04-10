package opendial.modules.examples.apimodule;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class APIAgent {

   public APIAgent() {

   }

   public static void main (String[] args) {
      // Tester
	  System.out.println(query("reviews", "professor_id", "1957", "latest"));
	 
   }

   
   public static String query(String endpoint, String searchBy, String searchTerm, String modifier) {
      String response = "";
      try {
          // Build up the URL.
          String url = buildURL(endpoint, searchBy, searchTerm);
          String jsonResponse = httpGet(url);
          response = parseResponse(jsonResponse, modifier);
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
         String firstRevew = reviews.getString(0);
         System.out.println(reviews.toString()); 
         response = firstReview; 
      }
      if (modifier.equals("numberReviews")) {
         JSONArray reviews = obj.getJSONArray("reviews");
         response = String.valueOf(review.length());
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
}
