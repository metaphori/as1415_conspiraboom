package proto1.game.impl;

import java.util.ArrayList;
import java.util.List;

import proto1.game.interfaces.IPlayerRole;

public class PlayerRole extends GameConcept implements IPlayerRole {
	
	protected Team team;
	protected TeamRole role;
	
	public PlayerRole(Team team, TeamRole role){
		super(team.name + "_" + role.name);
		this.team = team;
		this.role = role;
	}
	
	public TeamRole getTeamRole(){ return role; }
	public Team getTeam(){ return team; }
	
}
