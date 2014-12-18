// Agent sample_agent in project prototype_TwoRoomsAndABoom

/* Initial beliefs and rules */

players_here(Rms) :- my_room(MyRoom) & .findall(P, room(P, MyRoom), Rms).
others_here(Others) :- players_here(Ph) & .my_name(Me) & iactions.except(Ph, Me, Others).

num_mates_here(N) :- mates_here(L) & count(L, N).

mates_here(MatesHere) :- 
	players_here(Ps) & 
	.findall(P, team_mate(P), Mates) & 
	intersect(Ps, Mates, MatesHere).

/* Utils */

intersect([], L, _).
intersect(L, [], _).
intersect([A|L1], [A|L2], I) :- Res=[A|I] & intersect(L1,L2,Res).
intersect([A|L1], [B|L2], I) :- intersect(L1, L2, I).

except([H|L], H, R) :- except(L, H, R).

count([], 0).
count([H|[]], 1).
count([H|T], N) :- count(T, K) & N=K+1. 

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
		?players_here(Rms);
		.length(Rms, N);
		proto0.actions.random_from_to(0,N,Rand);
		.nth(Rand, Rms, Who);
		.print("I'll vote ", Who, " by a brilliant random reasoning.");
	};	
	.
	*/

@self_vote[prob(0.7)]	
+!decide_who_to_vote(Who) <- .my_name(Who).

/* Hazard: when I don't  */
@vote_randomly[prob(0.3)]
+!decide_who_to_vote(Who) : (num_mates_here(NM) & NM=0) <-
	?players_here(Rms);
	.length(Rms, N);
	proto0.actions.random_from_to(0,N,Rand);
	.nth(Rand, Rms, Who);
	//.print("I'll vote ", Who, " by a brilliant random reasoning.")
	.

+!decide_who_to_vote(Who) : mates_here(Mates) & count(Mates,N) & N>0 <-
	.print("In this room I am with ", N, " teammates: ", Mates);
	.nth(0, Mates, Who).

	
+phase(interaction) <-
	?round(R);
	?others_here(Others);
	.print("I am in this room with...... ", Others);
	//.print("Interaction phase (round",R,")");
	.
	
+turn(Who) : .my_name(Me) & Who==Me <-
	.print("Ehy, but it is my turn!!! :) :) :) ");
	?others_here(Ps);
	.length(Ps, N);
	proto0.actions.random_from_to(0,N,Rand);
	.nth(Rand,Ps,Dest);
	co_reveal(Dest);
	ok_i_am_done;
	.
	
+turn(Who) : .my_name(Me) & Who\==Me <- //.print("It's not my turn'")
	true.
	
+request(RID, What, From) <-  
	.print("Received a request (", What, ") from ", From);
	!decide_about_request(What,From,Decision); 
	Decision.
	
@req_risky[prob(0.1)]
+!decide_about_request(co_reveal, X, ok) : my_role(_,president) | my_role(_,bomber).
@req_consderv[prob(0.9)]
+!decide_about_request(co_reveal, X, no) : my_role(_,president) | my_role(_,bomber).

@req_notrisky[prob(0.7)]
+!decide_about_request(co_reveal, X, ok) : my_role(Team,Role) & Role\==president & Role\==bomber.
@req_notrisky_cons[prob(0.3)]
+!decide_about_request(co_reveal, X, no) : my_role(Team,Role) & Role\==president & Role\==bomber.

+phase(hostages_exchange) : .my_name(Me) & room_leader(Me) <-
	!choose_hostage(H);
	select_hostage(H);
	.	
	
+!choose_hostage(Hostage) <-
	?players_here(Rms);
	.length(Rms, N);
	proto0.actions.random_from_to(0,N,Rand);
	.nth(Rand, Rms, Hostage);
	//.print("I've chosen my hostage =>", Hostage );
	.