// Agent sample_agent in project prototype_TwoRoomsAndABoom

/* Initial beliefs and rules */

room_mates(Rms) :- my_room(MyRoom) & .findall(P, room(P, MyRoom), Rms).

/* Initial goals */

!start.

/* Plans */

+!start <- wanna_play.
	
+phase(leader_selection) <-
	!decide_who_to_vote(Who);
	vote(Who);
	.

/*
+!decide_who_to_vote(Who) <-
	proto0.actions.random_from_to(0,2,A);
	if(A==0){
		// myself
		.my_name(Who); 
		.print("I'll vote myself");
	} 
	if(A==1){
		// random
		?room_mates(Rms);
		.length(Rms, N);
		proto0.actions.random_from_to(0,N,Rand);
		.nth(Rand, Rms, Who);
		.print("I'll vote ", Who, " by a brilliant random reasoning.");
	};	
	.
	*/

@self_vote[prob(0.9)]	
+!decide_who_to_vote(Who) <- .my_name(Who).

@vote_randomly[prob(0.1)] 
+!decide_who_to_vote(Who) <-
	?room_mates(Rms);
	.length(Rms, N);
	proto0.actions.random_from_to(0,N,Rand);
	.nth(Rand, Rms, Who);
	.print("I'll vote ", Who, " by a brilliant random reasoning.")
	.	

	
+phase(interaction) <-
	?round(R);
	.print("Interaction phase (round",R,")");
	.
	
+turn(Who) : .my_name(Me) & Who==Me <-
	.print("Ehy, but it is my turn!!! :) :) :) ");
	ok_i_am_done
	.
	
+turn(Who) : .my_name(Me) & Who\==Me <- .print("It's not my turn'").
	
+phase(hostages_exchange) : .my_name(Me) & room_leader(Me) <-
	!choose_hostage(H);
	select_hostage(H);
	.	
	
+!choose_hostage(Hostage) <-
	?room_mates(Rms);
	.length(Rms, N);
	proto0.actions.random_from_to(0,N,Rand);
	.nth(Rand, Rms, Hostage);
	.print("I've chosen my hostage =>", Hostage );
	.