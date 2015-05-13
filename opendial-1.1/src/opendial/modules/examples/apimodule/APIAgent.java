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

   /**
    * API Agent constructor
    */
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

   /**
    * Given a full professor name, returns the professor
    * ID for API queries.
    * @param profName the full name of the professor
    * @return the professor ID, or "0" if not found.
    */
   public String getProfID(String profName) {
      if (profIDs.containsKey(profName)) {
         return profIDs.get(profName);
      } else {
         return "0";
      }
   }

   /**
    * Build the URL query to access the correct CULPA API
    * endpoint.
    * @param endpoint The API endpoint
    * @param searchBy The type of identifying information to give to the API
    * @param searchTerm The identifying information itself
    * @param modifier Any modifier necessary
    * @return the full URL
    */
   public static String query(String endpoint, String searchBy, String searchTerm, String modifier) {
      String response = "";
      try {
         // Build up the URL.
         String url = buildURL(endpoint, searchBy, searchTerm);
         String jsonResponse = httpGet(url);
         // DialogueSystem.log.info("query=" + url);
         //DialogueSystem.log.info("response=" + jsonResponse);

         response = parseResponse(jsonResponse, modifier);
         // DialogueSystem.log.info("response=" + response);
      }
      catch (IOException ioe) {
         ioe.printStackTrace();
      }
      catch (JSONException jse) {
         jse.printStackTrace();
      }
      return response; 
   }

   /**
    * Build the URL query to access the correct CULPA API
    * endpoint.
    * @param endpoint
    * @param searchBy
    * @param searchTerm
    * @return the full URL
    */
   public static String buildURL(String endpoint, String searchBy, String searchTerm){
      String base = "http://api.culpa.info";
      String url = base + "/" + endpoint + "/" + searchBy + "/" + searchTerm;
      return url;
   }

   /**
    * Parses the JSON response according to the type of information
    * the user has requested (summary, full review, etc)
    * @param jsonSrc
    * @param modifier the type of information the user has requested
    * @return the full information to be spoken to the user.
    */
   public static String parseResponse(String jsonSrc,
         String modifier) throws JSONException {
      JSONObject obj = new JSONObject(jsonSrc);
      String response = "";

      if (modifier.equals("latestReview")) {
         JSONArray reviews = obj.getJSONArray("reviews");
         Integer i = reviews.length() - 1;
         JSONObject review = reviews.getJSONObject(i);
         String latest_review_txt = review.getString("review_text");

         response = latest_review_txt;
         String[] responses = response.split("\n");
         response = responses[0].trim();
      }
      else if (modifier.equals("numberReviews")) {
         JSONArray reviews = obj.getJSONArray("reviews");
         response = String.valueOf(reviews.length());
      }

      else if (modifier.equals("summary")) {
         //JSONArray reviews = obj.getJSONArray("reviews");

         response = "";
         JSONArray reviews = obj.getJSONArray("reviews");
         int maxReviews = 0;
         maxReviews = Math.min(reviews.length(), 5);

         for (int i=0; i<maxReviews; i++){
            JSONObject review = reviews.getJSONObject(i);
            String review_txt = review.getString("review_text");  
            String firstSentence = review_txt.split("\\.")[0];


            // Replace unpronounceable syllables
            firstSentence = firstSentence.replace("(", "");
            firstSentence = firstSentence.replace(")", "");
            firstSentence = firstSentence.replace("*", "");
            firstSentence = firstSentence.replace("\n", "");
            firstSentence = firstSentence.replace("\t", "");
            if (firstSentence.length() > 0) {
               response += firstSentence + ". ";
            }
         }
         response = response.trim();
      }

      else if (modifier.equals("keywords")) {
         response = "";

         try{
            String parsing_url = 
               "http://text-processing.com/api/tag/";

            JSONArray reviews = obj.getJSONArray("reviews");
            Integer count = 0;
            for (int i=0; i<reviews.length(); i++){
               JSONObject review = reviews.getJSONObject(i);
               String review_txt = review.getString("review_text"); 

               //call parsing API to get parse of review text

               // first, get first 2,000 characters
               String sub_txt = review_txt.substring(0, 
                     Math.min(review_txt.length(), 2000));

               String param = "text=" + URLEncoder.encode(sub_txt, 
                     "UTF-8");
               String parsing_jsonResponse = httpPost(parsing_url, 
                     param);

               JSONObject obj2 = new JSONObject(
                     parsing_jsonResponse);
               String text = obj2.getString("text");

               String[] parts = text.split("\\n");

               for (int j=0; j<parts.length; j++){
                  //early exit
                  if (count > 9) {
                     break;
                  }
                  if (parts[j].endsWith("/JJ")){
                     String adj = parts[j].split("\\/")[0].trim();
                     String next = parts[j+1].split("\\/")[0].trim();
                     // adj + noun
                     if (parts[j+1].endsWith("/NN")  && 
                           next.length() > 2 && 
                           Character.isLetter(next.charAt(2)) ){
                        String new_adj = adj + " " + next;
                        // limit to words with >2 letters
                        if (response.indexOf(new_adj) == -1 && 
                              adj.length() > 4 && next.length() > 4){

                           if (count < 9){	// stop when get to 10
                              response += new_adj + ", ";
                              count += 1;
                           }
                           //end with a period not a comma
                           else if (count == 9) {
                              response += new_adj + ".";
                              count += 1;
                           }
                           System.out.println(count.toString());
                        }
                     }
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
            String sentiment_url = 
               "http://text-processing.com/api/sentiment/";

            JSONArray reviews = obj.getJSONArray("reviews");

            for (int i=0; i<reviews.length(); i++){
               JSONObject review = reviews.getJSONObject(i);
               String review_txt = review.getString("review_text"); 
               //call sentiment API to get polarity of review text
               String param = "text=" + URLEncoder.encode(
                     review_txt, "UTF-8");
               String sentiment_jsonResponse = httpPost(
                     sentiment_url, param);

               JSONObject obj2 = new JSONObject(
                     sentiment_jsonResponse);
               String label = obj2.getString("label");

               //increment polarity counts
               if(label.equals("pos")){pos += 1;}
               else if(label.equals("neg")){neg += 1;}
               else if(label.equals("neutral")){neutral += 1;}

            }

            response = "There are " + pos + 
               " positive reviews and " + neg + 
               " negative reviews and " + neutral + 
               " neutral reviews.";
         }
         catch (IOException ioe) {
            ioe.printStackTrace();
         }

      }

      // If the professor has less than 10 possible sentences, the
      // string won't end in a period, so we add one here.
      response = response.trim();
      if (response.endsWith(",")) {
         response = response.substring(0, response.length() - 1);
         response = response + ".";
      }

      return response;
   }

   /**
    * Generic HTTP Get request
    * Source:http://rest.elkstein.org/2008/02/using-rest-in-java.html
    * @param urlStr the URL to get
    * @return the response
    */
   private static String httpGet(String urlStr) throws IOException {
      URL url = new URL(urlStr);
      HttpURLConnection connection = (HttpURLConnection) 
         url.openConnection();

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

   /**
    * Generic utility Post function
    * @param targetURL the URL to post to
    * @param urlParameters the parameters to include
    * @return the post response
    */
   public static String httpPost(String targetURL, 
         String urlParameters)
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
         BufferedReader rd = new BufferedReader(new 
               InputStreamReader(is));
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
