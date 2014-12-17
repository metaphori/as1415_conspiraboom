package proto1.game.config;

import java.util.*;

import proto1.game.impl.Room;

public class Rooms {

	public static final Room ROOM1 = new Room("west_room");
	public static final Room ROOM2 = new Room("east_room");
	
	
	public static List<Room> asList(){
		List<Room> lst = new ArrayList<Room>();
		lst.add(ROOM1);
		lst.add(ROOM2);
		return lst;
	}
}
