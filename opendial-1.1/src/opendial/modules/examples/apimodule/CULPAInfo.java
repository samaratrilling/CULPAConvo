package src.opendial.modules.examples.apimodule;
import java.util.Collection;
import opendial.DialogueSystem;
import opendial.arch.DialException;
import opendial.arch.Logger;
import opendial.datastructs.Assignment;
import opendial.modules.Module;
import opendial.state.DialogueState;

public class CULPAInfo implements Module {
   DialogueSystem system;
   APIAgent agent;
   boolean paused = true;

   /**
    * Constructor for module
    * @param system the dialogue system to which the module should be attached
    */
   public CULPAInfo(DialogueSystem system) {
      this.system = system;
      this.agent = new APIAgent();
   }

   /**
    * Starts the module.
    */
   @Override
   public void start() throws DialException {
      paused = false;
   }

   /**
    * Checks whether the updated variables contain the system action and
    * if yes, what the system action value is.
    *
    * @param state the current dialogue state
    * @param updatedVars the updates variables in the state
    */
   @Override
   public void trigger(DialogueState state, Collection<String> updatedVars) {
      if (updatedVars.contains("a_m") && state.hasChanceNode("a_m")) {
         String action = state.queryProb("a_m").toDiscrete().getBest().toString();

         /*// Example
         String latestReview = agent.query("Reviews", "professsor_id", "10729", "latestReview");
         String newAction = "ReadReview(" + latestReview + ")";
         system.addContent(new Assignment("a_m", newAction));

         // end example*/
         
         if (action.equals("ProfReview")) {
            String profName = state.queryProb("prof").toDiscrete().getBest().toString();
             String revoptions = "we found x positive and y negative reviews.";

            String newAction="ReviewOptions(" + revoptions + ")";
            system.addContent(new Assignment("a_m", newAction));
         }
         else if (action.equals("CourseReview")) {
             String courseName = state.queryProb("course").toDiscrete().getBest().toString();
            String revoptions = "we found x positive and y negative reviews.";

            String newAction="ReviewOptions(" + revoptions + ")";
            system.addContent(new Assignment("a_m", newAction));
         }
         else if (action.equals("ProfRecentReview")) {
            String profName = state.queryProb("prof").toDiscrete().getBest().toString();
            String recentReview = "most recent review";
            String newAction="RecentReview(" + recentReview + ")";
            system.addContent(new Assignment("a_m", newAction));
         }
         else if (action.equals("ProfSummaryReview")) {
            String profName = state.queryProb("prof").toDiscrete().getBest().toString();
            String summaryReview = "sentiment summary";
            String newAction="SummaryReview(" + summaryReview + ")";
            system.addContent(new Assignment("a_m", newAction));
         }
         else if (action.equals("CourseRecentReview")) {
            String courseName = state.queryProb("course").toDiscrete().getBest().toString();
            String recentReview = "most recent review for course";
            String newAction="RecentReview(" + recentReview + ")";
            system.addContent(new Assignment("a_m", newAction));
         }
         else if (action.equals("CourseSummaryReview")) {
            String courseName = state.queryProb("course").toDiscrete().getBest().toString();
            String summaryReview = "sentiment summary";
            String newAction="SummaryReview(" + summaryReview + ")";
            system.addContent(new Assignment("a_m", newAction));
         }
      }
   }

   /**
    * Pauses the module.
    *
    * @param toPause whether to pause the module or not
    */
   @Override
   public void pause(boolean toPause) {
      paused = toPause;
   }

   /**
    * Returns whether module is currently running or not.
    *
    * @return whether the module is running or not.
    */
   @Override
   public boolean isRunning() {
      return !paused;
   }
}
