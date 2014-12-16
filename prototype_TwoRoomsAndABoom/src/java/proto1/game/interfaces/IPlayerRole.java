package proto1.game.interfaces;

import proto1.game.impl.Team;
import proto1.game.impl.TeamRole;

public interface IPlayerRole {

	TeamRole getTeamRole();
	Team getTeam();
	
}
