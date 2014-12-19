// Agent sample_agent in project prototype_TwoRoomsAndABoom

/* Initial beliefs and rules */

players_here(Rms) :- my_room(MyRoom) & .findall(P, room(P, MyRoom), Rms).
others_here(Others) :- players_here(Ph) & .my_name(Me) & iactions.except(Ph, Me, Others).

num_mates_here(N) :- mates_here(L) & count(L, N).

mates_here(MatesHere) :- 
	players_here(Ps) & 
	.findall(P, team_mate(P), Mates) & 
	iactions.intersect(Ps, Mates, MatesHere).

/* Utils */


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

@self_vote[prob(0.7)]	
+!decide_who_to_vote(Who) <- .my_name(Who).

/* Hazard: when I don't  */
@vote_randomly[prob(0.3)]
+!decide_who_to_vote(Who) : (num_mates_here(NM) & NM=0) <-
	?players_here(Rms);
	.length(Rms, N);
	proto0.actions.random_from_to(0,N,Rand);
	.nth(Rand, Rms, Who);
	.print("I have no teammates here, I think I'll vote randomly'");
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
	
@req_risky[prob(0.3)]
+!decide_about_request(co_reveal, X, ok) : my_role(_,president) | my_role(_,bomber).
@req_consderv[prob(0.7)]
+!decide_about_request(co_reveal, X, no) : my_role(_,president) | my_role(_,bomber).

@req_notrisky[prob(0.5)]
+!decide_about_request(co_reveal, X, ok) : my_role(Team,Role) & Role\==president & Role\==bomber.
@req_notrisky_cons[prob(0.5)]
+!decide_about_request(co_reveal, X, no) : my_role(Team,Role) & Role\==president & Role\==bomber.

+phase(hostages_exchange) : .my_name(Me) & room_leader(Me) <-
	!choose_hostage(H);
	select_hostage(H);
	.	
	
+!choose_hostage(Hostage) : round(3) <-
	?my_role(Team,Role);
	?my_room(Room);
	.my_name(Me);
	!room_of(reds,bomber,BomberRoom);
	!room_of(blues,president,PresidentRoom);
	// If I am a blue leader and bomber and president are in the same room
	// We'll simply win by moving one of them 
	if(Team==blues & BomberRoom==PresidentRoom){
		.print("(-___-) Bomber and president in same room. I'll save my president!!");
		!get_player_here_of_role(reds, bomber, B);
		if(B\==dont_know & room(B,Room)){
			Hostage = B;
		} else{
			Hostage = Me;
		}
	} else{
		// If I am red and bomber/president are in different rooms,
		// I might try by moving them to the other room
		if(Team==reds & BomberRoom\==PresidentRoom){
			.print("(--__--) Bomber and president in opposite rooms. I'll make them match!!!'");
			!get_player_here_of_role(reds, bomber, B);
			!get_player_here_of_role(blues, president, P);
			if(B\==dont_know & room(B,Room)){
				Hostage=B;
			} else {
				if(P\==dont_know & room(P,Room)){
					Hostage=P;
				} else{
					Hostage=Me;
				}
			}
		} else{
			Hostage = Me
		}
	}.
	
+!get_player_here_of_role(Team, Role, Who) <-
	?players_here(Ps);
	.print("Players here are ", Ps);
	.findall(P, role(P, Team, Role), PlayersOfRole);
	iactions.intersect(PlayersOfRole, Ps, Candidates);
	.print("Candidates at role ", Role," are ", Candidates);
	?count(Candidates,K);
	if(K>0){
		.nth(0, Candidates, Who);
	} else{
		Who = dont_know;
	}.
+!room_of(Team,Role,Room) <-
	.findall(P, role(P, Team, Role), PlayersOfRole);
	?count(PlayersOfRole,K);
	if(K>0){
		.nth(0, PlayersOfRole, X);
		room(X, Room);
	} else{
		Room = dont_know;
	}.


+!choose_hostage(Hostage) <-
	?players_here(Rms);
	.length(Rms, N);
	proto0.actions.random_from_to(0,N,Rand);
	.nth(Rand, Rms, Hostage);
	//.print("I've chosen my hostage =>", Hostage );
	.
	
+role(Player, Team, Role)[source(percept)] <-
	?my_role(MyTeam, _);
	if(Team == MyTeam){
		// take mental note: he is a team-mate :)
		+team_mate(Player);
		// let's share information!
		.findall(role(Pbb, Tbb, Rbb), role(Pbb, Tbb, Rbb), Roless);
		for(.member(R, Roless)){
			.send(Player, tell, R);
		};
	}.

+role(Player, Team, Role)[source(OtherPlayer)] : team_mate(OtherPlayer) <-
	.print("Thank you ", OtherPlayer, " for having shared your knowledge with me.");
	?my_role(MyTeam, _);
	+role(Player, Team, Role);
	if(Team == MyTeam){
		// take mental note: he is a team-mate :)
		+team_mate(Player);
	}.
	
// I believe what my team mates tell me they believe
+A[source(TeamMate)] : team_mate(TeamMate) <- +A.
	
