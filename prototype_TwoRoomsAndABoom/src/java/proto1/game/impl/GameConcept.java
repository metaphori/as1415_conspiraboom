package proto1.game.impl;

import proto1.game.interfaces.IGameConcept;

public class GameConcept implements IGameConcept {
	
	protected String name;
	
	public GameConcept(String name){
		this.name = name;
	}

	public String getName(){ return this.name; }
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof GameConcept)) return false;
		
		GameConcept other = (GameConcept)obj;
		return other.getName().equals(this.getName());
	}
	
	@Override
	public String toString(){
		return this.getClass().getSimpleName() + "#" + getName();
	}
	
}
