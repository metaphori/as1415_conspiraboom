// Agent sample_agent in project prototype_TwoRoomsAndABoom

/* Initial beliefs and rules */

room_mates(Rms) :- my_room(MyRoom) & .findall(P, room(P, MyRoom), Rms).

/* Initial goals */

!start.

/* Plans */

+!start <- wanna_play.
	
+phase(leader_selection) <-
	!delegate_to_human.
	
+turn(Who) : .my_name(Me) & Who==Me <- 
	!delegate_to_human.

+phase(hostages_exchange) : .my_name(Me) & room_leader(Me) <-
	!delegate_to_human.	

+!delegate_to_human <-
	?phase(Phase);
	.print("I'll delegate my actions during ", Phase, " to a human.");
	delegate_to_human.