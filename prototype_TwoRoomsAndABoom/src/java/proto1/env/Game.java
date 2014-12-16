package proto1.env;

import jason.environment.grid.Location;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.logging.Logger;

import proto1.game.Player;
import proto1.game.Roles;
import proto1.game.Room;
import proto1.game.Rooms;
import proto1.game.Team;
import proto1.game.TeamRole;
import proto1.game.Teams;

public class Game extends Observable {			
	
	protected int num_players = 0;
	
	protected Logger logger = Logger.getLogger("Game");
	
	protected List<Player> players = new ArrayList<Player>();	
	
	public static final String ROOM1 = "room1";
	public static final String ROOM2 = "room2";	
	
	public static enum GamePhase {
		Init,
		SetupRound,
		LeaderSelection,
		Interaction,
		HostageSelection,
		End
	};
	protected GamePhase state = GamePhase.Init;	
	
	protected int round = 0;
	protected static final int MAX_ROUNDS = 3;
	
	public Game(int num_players){
		this.num_players = num_players;
	}
	
	Hashtable<Player, Boolean> votersRoom1 = new Hashtable<Player, Boolean>();
	Hashtable<Player, Integer> candidatesRoom1 = new Hashtable<Player, Integer>();
	Hashtable<Player, Boolean> votersRoom2 = new Hashtable<Player, Boolean>();
	Hashtable<Player, Integer> candidatesRoom2 = new Hashtable<Player, Integer>();	
	
	Player hostageFromRoom1 = null;
	Player hostageFromRoom2 = null;
	
	public synchronized boolean SelectHostage(Player chooser, String hostageName){
		if(!chooser.getRoom().getLeader().equals(chooser)) { 
			logger.info("ERROR ==> NOT LEADER: The room of " + chooser + " is " + chooser.getRoom() + " which has leader " +
					chooser.getRoom().getLeader());
			return false; 
		}
		
		Player hostage = getPlayerFromName(hostageName);
		Room room = chooser.getRoom();
		if(room.equals(Rooms.ROOM1)){
			logger.info("Set hostage from " + Rooms.ROOM1);
			hostageFromRoom1 = hostage;
		} else if(room.equals(Rooms.ROOM2)){
			logger.info("Set hostage from " + Rooms.ROOM2);
			hostageFromRoom2 = hostage;
		}
		
		if(hostageFromRoom1!=null && hostageFromRoom2!=null){
			logger.info("Swapping hostages...");
			return SwapHostages(hostageFromRoom1, hostageFromRoom2);
		}
		
		return true;
	}
	
	public synchronized boolean SwapHostages(Player h1, Player h2){
		h1.setRoom(Rooms.ROOM2);
		h2.setRoom(Rooms.ROOM1);
		//Rooms.ROOM1.setPhase(RoundPhase.HostageExchanged);
		//Rooms.ROOM2.setPhase(RoundPhase.HostageExchanged);
		
		this.setChanged();
		notifyObservers(new HOSTAGES_EXCHANGE(h1,h2));
		
		this.setChanged();
		notifyObservers(new GAME_PHASE_COMPLETED(GamePhase.HostageSelection));
		
		return true;
	}
	
	public synchronized boolean Vote(String agentFrom, String agentTo){
		Player from = null;
		Player to = null;
		for(Player p : players){
			if(p.getName().equals(agentFrom))
				from = p;
			if(p.getName().equals(agentTo))
				to = p;
		}
		if(from==null || to==null || !from.getRoom().equals(to.getRoom())) return false;
		
		Room room = from.getRoom();
		
		Hashtable<Player,Boolean> voters = null;
		Hashtable<Player, Integer> candidates = null;
		if(room.equals(Rooms.ROOM1)){
			voters = votersRoom1;
			candidates = candidatesRoom1;
		} else{
			voters = votersRoom2;
			candidates = candidatesRoom2;
		}	
		
		if(!voters.containsKey(from)){
			voters.put(from, true);
			Integer last = candidates.get(to);
			candidates.put(to, last==null? 0 : last+1);
			logger.info(room + " vote " + voters.size() +  "/" + (players.size()/2));
			if(voters.size() == players.size()/2){
				logger.info("Scrutinizing...");
				Player leader = Scrutinize(candidates);
				logger.info("Electing leader in " + room + " => " + leader.getName());
				ElectLeader(room, leader);
			}
		} else{
			return false; // cannot vote twice
		}
		
		return true;
	}
	
	public synchronized Player Scrutinize(Hashtable<Player,Integer> votes){
		Player leader = null;
		int maxvotes = -1;
		
		for(Player candidate : votes.keySet()){
			int nv = votes.get(candidate);
			if(nv>maxvotes){
				leader = candidate;
				maxvotes = nv;
			}
		}
		return leader;
	}
	
	public synchronized void ElectLeader(Room room, Player newLeader){
		room.setLeader(newLeader);
		this.setChanged();
		notifyObservers(new NEW_LEADER());
		
		if(Rooms.ROOM1.getLeader()!=null && Rooms.ROOM2.getLeader()!=null){
			// Both leaders have been set
			this.setChanged();
			notifyObservers(new GAME_PHASE_COMPLETED(GamePhase.LeaderSelection));
		}
	}
	
	public synchronized void EndInteraction(){
		this.state = GamePhase.HostageSelection;
		
		this.setChanged();
		this.notifyObservers(new GAME_PHASE_COMPLETED(GamePhase.Interaction));
	}
	
	
	/******************************************/
	/** Phase: INIT **/
	/******************************************/
	
	public synchronized boolean AddPlayer(String agentName){
		Player p = new Player(agentName);
		players.add(p);
		int current_num_players = players.size();
		if(current_num_players == num_players){
			logger.info("We finally have " + current_num_players + " players: the game can start!");
			
			StartGame(); // assign roles, rooms, and give the start!
		}
		return true;
	}
	
	protected synchronized void StartGame(){
		logger.info("Initializing the game.");
		AssignRoles();
		logger.info("Roles assigned.");
		AssignRooms();
		logger.info("Rooms assigned.");
		logger.info("Ready to start.");
		
		this.setChanged();
		this.notifyObservers(new GAME_PHASE_COMPLETED(GamePhase.Init));
	}
	
	protected synchronized void AssignRoles(){
		List<TeamRole> rolesToAssign = GenerateRoles(num_players);
		Hashtable<Player, TeamRole> roleAssignments = utils.Utils.RandomMatch(players, rolesToAssign);
		for(Player p : roleAssignments.keySet()){
			p.setRole(roleAssignments.get(p));
		}
	}
	
	protected synchronized void AssignRooms(){
		List<Room> roomsToAssign = GenerateRooms(num_players);
		Hashtable<Player, Room> roomAssignments = utils.Utils.RandomMatch(players, roomsToAssign);
		for(Player p : roomAssignments.keySet()){
			Room room = roomAssignments.get(p);
			p.setRoom(room);
		}
		this.setChanged();
		notifyObservers(new ROOM_PLACEMENT()); // notify env model so that positions of players can be updated
	}
	
	protected synchronized void ProceedWithInteraction(){
		this.state = GamePhase.Interaction;
	}
	
	protected synchronized void SetupNewRound(){
		this.round++;
		
		if(this.round<=MAX_ROUNDS){
			votersRoom1.clear();
			votersRoom2.clear();
			candidatesRoom1.clear();
			candidatesRoom2.clear();
			Rooms.ROOM1.setLeader(null);
			Rooms.ROOM2.setLeader(null);
			hostageFromRoom1 = null;
			hostageFromRoom2 = null;
			
			state = GamePhase.LeaderSelection;
			
			this.setChanged();
			this.notifyObservers(new GAME_PHASE_COMPLETED(GamePhase.SetupRound));		
		} else{
			this.setChanged();
			this.notifyObservers(new GAME_PHASE_COMPLETED(GamePhase.End));			
		}
	}
	
	public static List<TeamRole> GenerateRoles(int num_players){
		List<TeamRole> result = new ArrayList<TeamRole>();
		result.add(new TeamRole(Teams.BLUES, Roles.PRESIDENT));
		result.add(new TeamRole(Teams.REDS, Roles.BOMBER));
		
		for(int i=2; i<num_players; i+=2){
			result.add(new TeamRole(Teams.BLUES, Roles.NORMAL));
			result.add(new TeamRole(Teams.REDS, Roles.NORMAL));
		}
		
		return result;
	}
	
	public static List<Room> GenerateRooms(int num_players){
		List<Room> result = new ArrayList<Room>();
		
		for(int i=0; i<num_players; i+=2){
			result.add(Rooms.ROOM1);
			result.add(Rooms.ROOM2);
		}
		
		return result;
	}
	
	public synchronized List<Player> getPlayersInRoom(Room room){
		List<Player> result = new ArrayList<Player>();
		for(Player p : players){
			if(p.getRoom().equals(room))
				result.add(p);
		}
		return result;
	}	
	
	public synchronized Player getPlayerFromName(String agName){
		for(Player p : players){
			if(p.getName().equals(agName))
				return p;
		}
		return null;
	}
	
	public synchronized Team getWinner() {
		TeamRole presidentRole = new TeamRole(Teams.BLUES, Roles.PRESIDENT);
		TeamRole bomberRole = new TeamRole(Teams.REDS, Roles.BOMBER);
		Player president = null, bomber=null;
		logger.info("President role = " + presidentRole);
		logger.info("Bomber role = " + bomberRole);
		for(Player p : players){
			if(p.getTeamRole().equals(presidentRole)){
				president = p;
				logger.info("Got president => " + p);
			}
			if(p.getTeamRole().equals(bomberRole)){
				bomber = p;
				logger.info("Got bomber => " + p);
			}
		}
		logger.info("President is in room " + president.getRoom());
		logger.info("Bomber is in room " + bomber.getRoom());
		return president.getRoom().equals(bomber.getRoom()) ? Teams.REDS : Teams.BLUES;
	}	
	
	public synchronized boolean IsAt(GamePhase phase){
		return phase == this.state;
	}
	
	/* Notification events for observers */
	public class NotifyEvents {	}
	public class ROOM_PLACEMENT extends NotifyEvents{
		public ROOM_PLACEMENT(){}
	}
	public class NEW_LEADER extends NotifyEvents{
		public NEW_LEADER(){}
	}
	public class HOSTAGES_EXCHANGE extends NotifyEvents{
		Player h1,h2;
		public HOSTAGES_EXCHANGE(Player h1, Player h2){ this.h1 = h1; this.h2=h2;}
	}	
	public class GAME_PHASE_COMPLETED extends NotifyEvents {
		public GamePhase phase;
		
		public GAME_PHASE_COMPLETED(GamePhase phase_completed){
			this.phase = phase_completed;
		}
	}	
}