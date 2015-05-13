CULPAConvo
Project for Spoken Dialogue Systems providing a voice interface to the CULPA API.
Anna Prokofieva, Sarah Ita Levitan, Samara Trilling

IMPORTANT FILES:
domains/examples/professors.xml
domains/examples/professors_dm.xml
domains/examples/professors_nlu.xml
domains/examples/professors_nlg.xml
src/opendial/modules/examples/apimodule/CULPAInfo.java
src/opendial/modules/examples/apimodule/APIAgent.java

professors.xml is the file that includes the DM, NLU and NLG
(contained in professors_dm.xml, professors_nlu.xml, and
professors_nlg.xml respectively). The bulk of the decision
logic is in the DM. The NLU contains the first round
of professor verification (whether the first and last
names spoken are valid Columbia professors' names), as
well as the decision logic for what to do when the system
has not properly understood the user.
The NLG, depending upon the Dialogue State, fills speech
templates with the appropriate information to convey to the
user.

CULPAInfo is the first point of contact between the XML 
dialogue manager and external APIs. It is the file that
makes direct changes to Dialogue State variables that rely on
information from extermal APIs.
Specifically, its two main functions are to facilitate
validation of professor names (to ensure that the spoken
name is actually that of a CS Columbia professor) and
to convey the information (review text, summary, sentiment
analysis, or keywords) back to the NLG for synthesis.

APIAgent is the home of the functions and actual API
calls used by CULPAInfo to complete the above tasks. It 
makes API calls, examines, analyzes and formats the JSON
responses, and sends them back to CULPAInfo.
This is where the summaries are generated and the keywords
are chosen.
