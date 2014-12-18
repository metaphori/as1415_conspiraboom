package proto1.env;

import jason.environment.grid.Location;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.logging.Logger;

import proto1.game.config.Rooms;
import proto1.game.config.TeamRoles;
import proto1.game.config.Teams;
import proto1.game.impl.Player;
import proto1.game.impl.PlayerRole;
import proto1.game.impl.Room;
import proto1.game.impl.Team;
import proto1.game.impl.Turn;
import proto1.game.interfaces.IPlayer;
import proto1.game.interfaces.IPlayerRole;
import proto1.game.interfaces.IRoom;
import proto1.game.interfaces.ITurn;
import utils.Tuple;

public class Game extends Observable {			
	
	protected int num_players = 0;
	protected List<IPlayer> players = new ArrayList<IPlayer>();

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
	
	Hashtable<IPlayer, Boolean> votersRoom1 = new Hashtable<IPlayer, Boolean>();
	Hashtable<IPlayer, Integer> candidatesRoom1 = new Hashtable<IPlayer, Integer>();
	Hashtable<IPlayer, Boolean> votersRoom2 = new Hashtable<IPlayer, Boolean>();
	Hashtable<IPlayer, Integer> candidatesRoom2 = new Hashtable<IPlayer, Integer>();	
	
	IPlayer hostageFromRoom1 = null;
	IPlayer hostageFromRoom2 = null;
	
	protected ITurn<IPlayer> turn = null;
	
	
	protected static final int MAX_ROUNDS = 3;
	protected Logger logger = Logger.getLogger("Game");
	
	public Game(int num_players){
		this.num_players = num_players;
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
		List<IPlayerRole> rolesToAssign = GenerateRoles(num_players);
		Hashtable<IPlayer, IPlayerRole> roleAssignments = utils.Utils.RandomMatch(players, rolesToAssign);
		for(IPlayer p : roleAssignments.keySet()){
			p.setRole(roleAssignments.get(p));
		}
	}
	
	protected synchronized void AssignRooms(){
		List<IRoom> roomsToAssign = GenerateRooms(num_players);
		Hashtable<IPlayer, IRoom> roomAssignments = utils.Utils.RandomMatch(players, roomsToAssign);
		for(IPlayer p : roomAssignments.keySet()){
			IRoom room = roomAssignments.get(p);
			p.setRoom(room);
		}
		this.setChanged();
		notifyObservers(new ROOM_PLACEMENT()); // notify env model so that positions of players can be updated
	}
	
	
	/******************************************/
	/** Phase: ROUND SETUP **/
	/******************************************/	
	
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
		
	
	/******************************************/
	/** Phase: LEADER SELECTION **/
	/******************************************/	
	
	public synchronized boolean Vote(String agentFrom, String agentTo){
		IPlayer from = null;
		IPlayer to = null;
		for(IPlayer p : players){
			if(p.getName().equals(agentFrom))
				from = p;
			if(p.getName().equals(agentTo))
				to = p;
		}
		if(from==null || to==null || !from.getRoom().equals(to.getRoom())) return false;
		
		IRoom room = from.getRoom();
		
		Hashtable<IPlayer,Boolean> voters = null;
		Hashtable<IPlayer, Integer> candidates = null;
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
				IPlayer leader = Scrutinize(candidates);
				logger.info("Electing leader in " + room + " => " + leader.getName());
				ElectLeader(room, leader);
			}
		} else{
			return false; // cannot vote twice
		}
		
		return true;
	}
	
	public synchronized IPlayer Scrutinize(Hashtable<IPlayer,Integer> votes){
		IPlayer leader = null;
		int maxvotes = -1;
		
		for(IPlayer candidate : votes.keySet()){
			int nv = votes.get(candidate);
			if(nv>maxvotes){
				leader = candidate;
				maxvotes = nv;
			}
		}
		return leader;
	}
	
	public synchronized void ElectLeader(IRoom room, IPlayer newLeader){
		room.setLeader(newLeader);
		this.setChanged();
		notifyObservers(new NEW_LEADER());
		
		if(Rooms.ROOM1.getLeader()!=null && Rooms.ROOM2.getLeader()!=null){
			// Both leaders have been set
			this.setChanged();
			notifyObservers(new GAME_PHASE_COMPLETED(GamePhase.LeaderSelection));
		}
	}
	
	
	/******************************************/
	/** Phase: INTERACTION **/
	/******************************************/	
	
	protected synchronized void ProceedWithInteraction(){
		this.state = GamePhase.Interaction;
		
		// Initialize the turn
		turn = new Turn<IPlayer>(this.players, 1);
		advanceTurn();
	}
	protected synchronized ITurn<IPlayer> getTurn(){
		return turn;
	}
	public synchronized IPlayer currentTurn(){
		return turn.currentTurn();
	}
	
	public synchronized boolean advanceTurn(){			
		boolean hasnext = turn.hasNext();
		if(!turn.hasNext()){
			logger.info("The turn cannot advance => start HOSTAGES SEL");
			this.StartSelectionOfHostages();
		} else if(hasnext){
			IPlayer player = turn.next();
			logger.info("Advancing the turn, which now is to " + player);
			this.setChanged();
			this.notifyObservers(new NEXT_TURN(player));
		} else{
			logger.severe("I should not have received such a command");
		}
		
		return true;
	}

	
	
	/******************************************/
	/** Phase: EXCHANGE OF HOSTAGES **/
	/******************************************/	
	
	public synchronized boolean SelectHostage(IPlayer chooser, String hostageName){
		if(!chooser.getRoom().getLeader().equals(chooser)) { 
			logger.severe("ERROR ==> NOT LEADER: The room of " + chooser + " is " + chooser.getRoom() + " which has leader " +
					chooser.getRoom().getLeader());
			return false; 
		}
		
		IPlayer hostage = getPlayerFromName(hostageName);
		IRoom room = chooser.getRoom();
		if(room.equals(Rooms.ROOM1)){
			logger.info(chooser + " set hostage from " + Rooms.ROOM1);
			hostageFromRoom1 = hostage;
		} else if(room.equals(Rooms.ROOM2)){
			logger.info(chooser + " set hostage from " + Rooms.ROOM2);
			hostageFromRoom2 = hostage;
		}
		
		if(hostageFromRoom1!=null && hostageFromRoom2!=null){
			logger.info("Swapping hostages...");
			return SwapHostages(hostageFromRoom1, hostageFromRoom2);
		}
		
		return true;
	}
	
	public synchronized boolean SwapHostages(IPlayer h1, IPlayer h2){
		h1.setRoom(Rooms.ROOM2);
		h2.setRoom(Rooms.ROOM1);
		
		this.setChanged();
		notifyObservers(new HOSTAGES_EXCHANGE(h1,h2));
		
		this.setChanged();
		notifyObservers(new GAME_PHASE_COMPLETED(GamePhase.HostageSelection));
		
		return true;
	}
	
	public synchronized void StartSelectionOfHostages(){
		this.state = GamePhase.HostageSelection;
		
		this.setChanged();
		this.notifyObservers(new GAME_PHASE_COMPLETED(GamePhase.Interaction));
	}	
	

	/******************************************/
	/** Phase: END **/
	/******************************************/	
	
	public synchronized Team getWinner() {
		IPlayerRole presidentRole = new PlayerRole(Teams.BLUES, TeamRoles.PRESIDENT);
		IPlayerRole bomberRole = new PlayerRole(Teams.REDS, TeamRoles.BOMBER);
		IPlayer president = null, bomber=null;
		logger.info("President role = " + presidentRole);
		logger.info("Bomber role = " + bomberRole);
		for(IPlayer p : players){
			if(p.getRole().equals(presidentRole)){
				president = p;
			}
			if(p.getRole().equals(bomberRole)){
				bomber = p;
			}
		}
		logger.info("President is in room " + president.getRoom());
		logger.info("Bomber is in room " + bomber.getRoom());
		return president.getRoom().equals(bomber.getRoom()) ? Teams.REDS : Teams.BLUES;
	}		
	
	
	/******************************************/
	/** Utilities **/
	/******************************************/	
	
	public static List<IPlayerRole> GenerateRoles(int num_players){
		List<IPlayerRole> result = new ArrayList<IPlayerRole>();
		result.add(new PlayerRole(Teams.BLUES, TeamRoles.PRESIDENT));
		result.add(new PlayerRole(Teams.REDS, TeamRoles.BOMBER));
		
		for(int i=2; i<num_players; i+=2){
			result.add(new PlayerRole(Teams.BLUES, TeamRoles.NORMAL));
			result.add(new PlayerRole(Teams.REDS, TeamRoles.NORMAL));
		}
		
		return result;
	}
	
	public static List<IRoom> GenerateRooms(int num_players){
		List<IRoom> result = new ArrayList<IRoom>();
		
		for(int i=0; i<num_players; i+=2){
			result.add(Rooms.ROOM1);
			result.add(Rooms.ROOM2);
		}
		
		return result;
	}
	
	public synchronized List<IPlayer> getPlayersInRoom(IRoom room){
		List<IPlayer> result = new ArrayList<IPlayer>();
		for(IPlayer p : players){
			if(p.getRoom().equals(room))
				result.add(p);
		}
		return result;
	}	
	
	public synchronized IPlayer getPlayerFromName(String agName){
		for(IPlayer p : players){
			if(p.getName().equals(agName))
				return p;
		}
		return null;
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
		IPlayer h1,h2;
		public HOSTAGES_EXCHANGE(IPlayer h1, IPlayer h2){ this.h1 = h1; this.h2=h2;}
	}	
	public class GAME_PHASE_COMPLETED extends NotifyEvents {
		public GamePhase phase;
		
		public GAME_PHASE_COMPLETED(GamePhase phase_completed){
			this.phase = phase_completed;
		}
	}	
	public class NEXT_TURN extends NotifyEvents {
		public IPlayer player;
		
		public NEXT_TURN(IPlayer player){
			this.player = player;
		}
	}	
}
