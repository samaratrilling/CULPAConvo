package opendial.modules.examples;
import java.util.Collection;
import opendial.DialogueSystem;
import opendial.arch.DialException;
import opendial.arch.Logger;
import opendial.datastructs.Assignment;
import opendial.modules.Module;
import opendial.state.DialogueState;

public class CULPAInfo implements Module {
   DialogueSystem system;
   boolean paused = true;

   /**
    * Constructor for module
    * @param system the dialogue system to which the module should be attached
    */
   public CULPAInfo(DialogueSystem system) {
      this.system = system;
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

         if (action.equals("FindProfessor")) {
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
