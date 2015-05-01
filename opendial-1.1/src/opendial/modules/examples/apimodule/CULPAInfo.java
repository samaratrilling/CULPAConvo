package opendial.modules.examples.apimodule;
import java.util.Collection;
import java.util.*;
import java.io.*;
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
   public CULPAInfo(DialogueSystem system) throws IOException {
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
          //DialogueSystem.log.info("Prof name is Michael Collins");
          
          //Action 1 Validate(professor) is check if prof name exists, if yes, Ground([rpfesspr, else Reject(professor)
          //Action 2 ground(reviewoptions, reviewoptions) then return SpeakReviews(Review)
          
          if (action.equals("Validate(Professor)")) {
              String profname = state.queryProb("Professor").toDiscrete().getBest().toString();
              String profID = agent.getProfID(profname);
              if (profID.equals("0")) {
                  String newAction = "Reject(Professor)";
                  system.addContent(new Assignment("a_m", newAction));
              } else {
                  String newAction = "Ground(Professor)";
                  system.addContent(new Assignment("a_m", newAction));
              }
          }
          if (action.equals("Ground(ReviewOptions")) {
              String profname = state.queryProb("Professor").toDiscrete().getBest().toString();
              String profID = agent.getProfID(profname);
              String revoption = state.queryProb("ReviewOptions").toDiscrete().getBest().toString();
              String review = agent.query("reviews", "professor_id", profID, revoption);
              String newAction = "SpeakReviews(" + review + ")";
              system.addContent(new Assignment("a_m", newAction));
          }

         // end example
         
         /*if (action.equals("FindProfessor")) {
            String profName = state.queryProb("Professor").toDiscrete().getBest().toString();
            // query the API for that professor.
            // for now just does a dummy return.
            String classes = "NLP " + 
                     "Spoken Dialogue Systems " +
                     "Spoken Language Processing";
            String newAction="DisplayClasses(" + classes + ")";
            system.addContent(new Assignment("a_m", newAction));
         }
         else if (action.equals("FindClass")) {
            String className = state.queryProb("a_m").toDiscrete().getBest().toString();
            String classInfo = "9:10am, taught by a professor";
            String newAction="DisplayClass(" + className + " " + classInfo + ")";
            system.addContent(new Assignment("a_m", newAction));
         }
	*/
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

