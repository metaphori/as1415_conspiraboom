// Agent organizer in project prototype_TwoRoomsAndABoom

/* Initial beliefs and rules */

/* GAME RULES and INFO */
rooms(room1, room2).
round(0).
max_rounds(3).
min_players(6).
stop_condition(round(R)) :- R==3.
positions(Positions) :-	.findall(place(P,R), place(P,R), Positions).

players_in_room(Room, Players) :- .findall(P, place(P, Room), Players).
other_room(Room, Other) :- rooms(Room, Other).
other_room(Room, Other) :- rooms(Other, Room).

/* Utilities */
append(L, Item, 1, [Item|L]).
append(L, Item, K, [Item|R]) :- R = [Item|L] & append(R, Item, K-1, [Item|R]) .
waiting(registration, 10).
waiting(leader_campaign, 10).
waiting(interaction,1000).
/* Initial goals */

!init.

/* Plans */

+!init <-
	.print("Let's wait for players");
	?waiting(registration,Msecs);
	!wait_some_time(Msecs,prepare_game).

+!wait_some_time(Ms,Todo) <-
	.wait(Ms);
	!Todo.

+wanna_play(P) <- 
	//.print("Added ", P, " to players.");
	+player(P).

+!prepare_game <-
	.print("Game can start. Let's see who wants to play :)))");
	.findall(P,player(P),Players);
	+players(Players);
	L = .length(Players);
	?min_players(Min);
	if(L>=Min){
		+num_players(L);	
		!determine_roles(L, Roles);
		!assign_roles(Roles);
		!assign_players_to_rooms;
		!start_game;
	} else{
		.print("Just ", L, " players want to play but we need at least ", Min, " people :( ");
	}.

+!determine_roles(L, AllRoles) : min_players(Min) & L >= Min & ((L mod 2) == 0) <-
	//.print("There are ", L, " players.");
	?append([role(blue,president)], role(blue,normal), (L/2-1), Blues);
	?append([role(red,bomber)|Blues], role(red,normal), (L/2-1), AllRoles); 
	//.print("Roles to assign: ");
	//for(.member(M,AllRoles)){.print("- ", M); }
	.
	
+!assign_roles(AllRoles) <-
	?num_players(N);
	?players(Players);
	.print("Assigning roles");
	.shuffle(AllRoles, Roles);
	for(.range(I,1,N)){
		K = I-1;
		.nth(K, Players, Player);
		.nth(K, Roles, RoleToAssign);
		+role(Player, RoleToAssign);
		//.send(Player, tell, startup(RoleToAssign, Players, Room));
	}.

+!assign_players_to_rooms <-
	?players(Players);
	?num_players(N);
	?rooms(Room1, Room2);
	for(.range(I,0,N-1)){
		.nth(I, Players, Player); // todo: refactor
		if(I mod 2 ==0){
			+place(Player,Room1);
		} else{
			+place(Player,Room2);
		}
	}.
	
+!start_game <-
	?players(Players);
	?positions(Positions);
	?round(Round);
	?max_rounds(MRounds);
	for(.member(P, Players)){
		?place(P, RoomToAssign);
		?role(P, RoleToAssign);
		RoleToAssign = role(Team,Kind);
		.send(P, tell, my_role(Team, Kind));
		.send(P, tell, players(Players));
		.send(P, tell, rooms(Room1,Room2));
		.send(P, tell, max_rounds(MRounds));
	}
	!new_round;
	.
	
+!new_round: round(R) & not stop_condition(round(R)) <-
	?rooms(Room1,Room2);
	NewRound = R+1;
	-+round(NewRound);	
	!clean_beliefs;
	.print("***************** Starting a new round: #", NewRound, " *******************");
	!switch_phase(Room1, setup);
	!switch_phase(Room2, setup);
	!!next_step(Room1);
	!!next_step(Room2)	
	.
	
+!new_round: round(R) & stop_condition(round(R)) <-
	!end_of_game
	.
	
+!end_of_game <-
	?role(President, role(blue,president));
	?role(Bomber, role(red,bomber));
	?place(President, RoomOfPresident);
	?place(Bomber, RoomOfBomber);
	if(RoomOfPresident==RoomOfBomber){
		.print("***********************************************************");
		.print("***********************************************************");
		.print("******************* THE REDS HAVE WON **********************");
		.print("***********************************************************");
		.print("***********************************************************");
	} else{
		.print("***********************************************************");
		.print("***********************************************************");
		.print("******************* THE BLUES HAVE WON ********************");
		.print("***********************************************************");
		.print("***********************************************************");		
	}
	.
	

+!clean_beliefs <-
	?players(Players);
	for(.member(P, Players)){
		-vote(_, _)[source(_)];
		-vote(_, _, _);
	}
	-leader(_,_);
	-leader(_,_);
	-hostages(_,_)[source(_)];
	-hostages(_,_)[source(_)];
	-accepted_hostages(_,_);
	-accepted_hostages(_,_);
	.

+!next_step(Room) : phase(Room, setup) <-
	?positions(Positions);
	?round(Round);
	!tell_all(Room, round(Round));
	!tell_all(Room, positions(Positions));
	!switch_phase(Room, leader_campaign);
	!tell_all(Room, phase(leader_campaign));
	?waiting(leader_campaign,Msecs);
	!!wait_some_time(Msecs,next_step(Room));
	.	
	
+!next_step(Room) : phase(Room, leader_campaign) <-
	!switch_phase(Room, leader_selection);
	!tell_all(Room, phase(leader_selection, 1));
	.

+!next_step(Room) : phase(Room, interaction) <-
	!switch_phase(Room, hostages_exchanges);
	!tell_all(Room, phase(hostages_exchanges));
	.

+!next_step(Room) : phase(Room, hostages_exchanges) <-
	!switch_phase(Room, setup);
	!tell_all(Room, phase(setup));
	.
	
@hst_is[atomic]
+hostages(Room, Hostages1)[source(Player)] : leader(Room, Player) <-
	+accepted_hostages(Room, Hostages1);
	?other_room(Room,OtherRoom);
	.count(accepted_hostages(OtherRoom, _), N);
	if(N==0){
		.print("Leader of ", Room, "(",Player,") has given hostages: ", Hostages1)
	} else{
		?leader(OtherRoom, OtherLeader);
		?hostages(OtherRoom, Hostages2)[source(OtherLeader)];
		.print("Also the leader of ", Room, "(",Player,") has given hostages: ", Hostages1);
		!exchange(Room, Hostages1, OtherRoom, Hostages2);
		!new_round;	
	}. 
	
+!exchange(R1,Hostages1,R2,Hostages2) <-
	?players_in_room(R1, Players1);
	.difference(Players1, Hostages1, Ps1);
	.union(Ps1, Hostages2, NewPlayers1);
	?players_in_room(R2, Players2);
	.difference(Players2, Hostages2, Ps2);
	.union(Ps2, Hostages1, NewPlayers2);
	for(.member(P1,NewPlayers1)){
		-place(P1,_);
		+place(P1,R1);
	}
	for(.member(P2,NewPlayers2)){
		-place(P2,_);
		+place(P2,R2);
	}
	.print("After the swap in ", R1, ": ", NewPlayers1);
	.print("After the swap in ", R2, ": ", NewPlayers2);
	.
	
+!tell_all(What) <-
	?players(Players);
	for(.member(P,Players)){
		.send(P, tell, What);
	}.
+!tell_all(Room, What) <-
	?players_in_room(Room, PlayersInRoom);
	//.print("Telling ", What, " to all players in room ", Room, " i.e. ", PlayersInRoom);
	for(.member(P,PlayersInRoom)){
		.send(P, tell, What);
	}.	

@is[atomic] // todo: limit atomic scope of plan
+vote(Player, Candidate) : place(Player, RoomOfVote) & phase(RoomOfVote, leader_selection)
	<-
	// 1. Assign vote
	+vote(in(RoomOfVote), from(Player), to(Candidate));

	// 2. Understand if all the players in a room have voted a leader
	!count_votes_in_room(RoomOfVote, NVotes);
	?num_players(NP);	
	//.print(Player, " has voted ", Candidate, " (#", NVotes, "/", (NP/2), " in ", RoomOfVote,")");
	if(NVotes = (NP/2)){
		//.print("All players have voted a leader in room ", RoomOfVote);

		.findall(vote(in(RoomOfVote),From,To), vote(in(RoomOfVote),From,To), Votes);
		// 3. Understand if there is a winner
		// 3b. (optional) retry election if parity
		proto0.actions.scrutinize(Votes, random_if_parity, Leader, WithNumVotes);
		.print("In room ", RoomOfVote, " the player ", Leader, " has been elected as leader with ", WithNumVotes, " votes");
		
		// 4. Communicate to players **in that room** the leader and proceed to next phase
		+leader(RoomOfVote, Leader);
		!switch_phase(RoomOfVote, interaction);
		!tell_all(RoomOfVote, leader(Leader));
		!tell_all(RoomOfVote, phase(interaction));
		?waiting(interaction,Msecs);
		!!wait_some_time(Msecs, next_step(RoomOfVote));
	}.
	
+!count_votes_in_room(R, N) <-
	.count(vote(in(R),_,_), N).

+!switch_phase(Room, NewPhase) <-
	//!shout(["SWITCH TO ", NewPhase, " IN ROOM ", Room]);
	.print("Switch to ",NewPhase, " in room ", Room);
	-phase(Room, _);
	+phase(Room, NewPhase).

+!shout(In) <-
		proto0.actions.stringify(In, Str);
		.print("\n##############################\n", Str, "\n##############################");
		.

