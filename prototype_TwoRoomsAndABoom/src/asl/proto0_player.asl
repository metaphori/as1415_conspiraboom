// Agent sample_agent in project prototype_TwoRoomsAndABoom

/* Initial beliefs and rules */

my_room(R) :- positions(Places) & .my_name(Me) & my_room(Me, R, Places).
my_room(Me, R, [place(Me,R)|OtherPositions]).
my_room(Me, R, [_|OtherPositions]) :- my_room(Me, R, OtherPositions).


/* Initial goals */

!start.

/* Plans */

+!start : true <- 
	.my_name(Me); 
	.print("Hello world, my name is ", Me, " and I wanna play to 'Two Rooms and a Boom' :)"); 
	.send(org, tell, wanna_play(Me)).

+!organize_info <-
	?positions(Positions);
	?my_room(Room);
	.my_name(Me);
	for(.member(place(P,Room), Positions)){
		if(P \== Me){
			if(R=Room){
				+here(P);
			} else{
				+there(P);
			}
		}
	};
	.findall(Ps, here(Ps), With);
	-+all_in_this_room([Me|With]);
	-+all_in_this_room_except_me(With);
	.count(here(_), N);
	-+num_players_in_this_room(N+1);	
	-+num_players_in_this_room_without_me(N);
	.
		
+phase(leader_campaign) <-
	?my_role(Team,Kind);
	?my_room(Room);
	?round(R);
	!organize_info;
	!choose_strategy( [propose_myself_as_leader, wait], ChosenStrategy);
	!ChosenStrategy.
	
+!clean_beliefs <-
	-phase(_,_)[source(org)];
	-phase(_)[source(org)];
	-phase(_)[source(org)];
	-phase(_)[source(org)];
	-phase(_)[source(org)];
	-phase(_)[source(org)];
	
	-leader(_)[source(_)];
	?players(Ps);
	for(.member(P,Ps)){
		-here(_);
		-there(_);
	}
	.
	
+phase(leader_selection, K) <- // K is the attempt num
	.my_name(Me);
	.findall(Ps, here(Ps), With);
	.length(With, L);
	proto0.actions.random_from_to(0,L,Rand);
	.nth(Rand, With, MyLeader);
	.send(org, tell, vote(Me, MyLeader)).
	
+phase(interaction) <- 
	true
	.
	
+phase(hostages_exchanges) : .my_name(Me) & leader(Me) <-
	?phase(Phase);
	?my_room(Room);
	?all_in_this_room_except_me(Players);
	?num_players_in_this_room_without_me(NP);
	proto0.actions.random_from_to(0, NP, Random);
	.nth(Random, Players, H);
	.print("[",Phase,"] I randomly select an hostage between ", Players, "... ", Random, " ===> ", H);
	.send(org, tell, hostages(Room, [H]));
	!clean_beliefs.
	
+phase(hostages_exchanges) : .my_name(Me) & not leader(Me) <- 
	!clean_beliefs.
	
+!propose_myself_as_leader <-
	.my_name(Me);
	!propose_someone_as_leader(Me);
	.
	
+!propose_someone_as_leader(Who) <- 
	?phase(Phase);	
	.findall(P, here(P), HerePlayers);
	.print("[",Phase,"] I will propose myself as leader to ", HerePlayers);
	for(.member(Other, HerePlayers)){
		.send(Other, tell, propose_leader(Me))
	}
	.
+!wait <- 
	?phase(Phase);
	.print("[",Phase,"] I will simply wait :)").
	
+!choose_strategy(Ss, Chosen) <-
	.length(Ss, Num);
	proto0.actions.random_from_to(0,Num,Rand);
	.nth(Rand, Ss, Chosen);
	.
