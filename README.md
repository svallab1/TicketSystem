# TicketSystem
Instructions:
==============
	Compile and run unit tests:
	---------------------------
	mvn clean install
	
	Command line execution:
	-----------------------
	Once compiled, navigate to target folder.
	Run: "java -jar $jarname"
	Follow the instructions on screen to play with the system.

	Dependencies:
	-------------
	JUnit
	Log4J

Assumptions:
-----------
1. Asummed there will only be one instance of TicketService.

2. Best Seats: Best seat criteria: Longest subsequence in minimum venue level. That is, in a given venue level, maximum possible sets of continous seats(sets with decreasing order of length. Here sets do not refer to Collections Set).

3.Ticket cancellation: A booked ticket cannot be cancelled.

PS: I implemented the system at the simplest and hence did not address persistance(Database/Disk Storage). Also did not use any frameworks(Spring/Struts) due to the simplicity of the project.
