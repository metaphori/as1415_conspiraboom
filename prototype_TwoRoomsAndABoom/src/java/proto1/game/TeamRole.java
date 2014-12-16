package proto1.game;

import java.util.ArrayList;
import java.util.List;

public class TeamRole extends GameConcept {
	
	protected Team team;
	protected Role role;
	
	public TeamRole(Team team, Role role){
		super(team.name + "_" + role.name);
		this.team = team;
		this.role = role;
	}
	
	public Role getRole(){ return role; }
	public Team getTeam(){ return team; }
	
}
