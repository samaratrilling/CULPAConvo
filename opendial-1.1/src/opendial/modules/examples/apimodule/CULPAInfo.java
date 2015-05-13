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
import opendial.DialogueSystem;

public class CULPAInfo implements Module {
   DialogueSystem system;
   APIAgent agent;
   boolean paused = true;

   /**
    * Constructor for module
    * @param system the dialogue system to which the module should 
    * be attached
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
    * Checks whether the updated variables contain the system action 
    * and if yes, what the system action value is.
    * @param state the current dialogue state
    * @param updatedVars the updates variables in the state
    */
   @Override
   public void trigger(DialogueState state,
         Collection<String> updatedVars) {
      //Logging
      DialogueSystem.log.info("Current step is: " + 
         state.queryProb("current_step").toDiscrete().getBest().toString());
      DialogueSystem.log.info("a_u: " + 
         state.queryProb("a_u").toDiscrete().getBest().toString());
      DialogueSystem.log.info("a_m: " + 
         state.queryProb("a_m").toDiscrete().getBest().toString());

      if (updatedVars.contains("a_m") && 
            state.hasChanceNode("a_m")) {
         String action =
            state.queryProb("a_m").toDiscrete().getBest().toString();
         DialogueSystem.log.info("in trigger, action = " + action);
         if (action.equals("Validate(Professor)")) {
            String profname = 
               state.queryProb("Professor").toDiscrete().getBest().toString();
            String profID = agent.getProfID(profname);
            if (profID.equals("0")) {
               String newAction = "Reject(Professor)";
               system.addContent(new Assignment("a_m", newAction));
               DialogueSystem.log.info("a_m=" + newAction);
            } else {
               String newAction = "Ground(Professor,"+profname+")";
               system.addContent(new Assignment("a_m", newAction));
               DialogueSystem.log.info("a_m=" + newAction);
            }
         }
         if (action.equals("Ground(ReviewOptions)")) {
            String profname = 
               state.queryProb("Professor").toDiscrete().getBest().toString();
            String profID = agent.getProfID(profname);
            String revoption = 
               state.queryProb("ReviewOptions").toDiscrete().getBest().toString();
            DialogueSystem.log.info("ReviewOptions: " + revoption);

            String review = agent.query("reviews", 
                  "professor_id", profID, revoption);
            DialogueSystem.log.info("Review: " + review);
            String newAction = "SpeakReview(" + review + ")";
            system.addContent(new Assignment("a_m", newAction));
            DialogueSystem.log.info("a_m" + newAction);
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

