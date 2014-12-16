ConspiraBoom
====================

![](https://s3.amazonaws.com/ksr/assets/001/246/367/227afc2451d4c065a1be69d50dee1a0b_large.png?1382926817)

* Image taken from https://www.kickstarter.com/projects/gerdling/two-rooms-and-a-boom

## Introduction

### What: The Game

*The short story.* _Two Rooms and a Boom_ is a competitive, team-based, social deduction party game
 set in two rooms where hostages are exchanged at each round. The (initially mutually extraneous) players in the rooms interact 
 to ultimately allow the Bomber to assassinate the President or the latter to remain in life.

*The long story.*

_Two Rooms and a Boom_ is a competitive, social deduction party game. There are two teams: 
 the Red Team and the Blue Team. Players are secretely given a card that certify their team and role: 
 this is the sole information a player knows at the beginning of the game\footnote{In addition to the rules of the game, of course.}.
There are two special roles: the \emph{President} (in the Blue Team), and the \emph{Bomber} (in the Red Team). 
Players are equally distributed between two rooms (i.e., separate playing areas). 
Then, the game consists of many timed rounds. In each round, players interact with each other by exchanging information.
At the end of each round, some players (i.e., the hostages) will be swapped into opposing rooms, 
 as decided by the room's leader that had been elected for that round. 
If the Red Team's Bomber is in the same room as the President at the end of the game, 
 then the Red Team wins; otherwise the Blue Team wins. Lying encouraged.

\subsection{Why is this game funny?}

\textbf{\emph{Two Rooms and a Boom} entertains its players by making them interact in a \emph{socially dynamic context}
 where \emph{competition}, \emph{cooperation}, and \emph{unpredictability} play a key role}.
 People stay puzzled: ``is he a team-mate or an enemy?'', ``what's her strategy?'', ``what's the deep reason for his actions?'', 
 ``what's happening in the other room?'', ``what happens if I do this?'' 

Interaction has \emph{no rules}; the only regulations that apply are those of your national legal system: 
 players can lie, break agreements, remain silent, mix truth and falsehood, steal one's card, and so on.

 Moreover, \emph{the fear of being discovered, the cleverness of the competition, the unexpected and the hazard stimulate emotions 
 such as excitement, fear, pleasure and reward}.
 
Other characteristics that might contribute to the comical dimension follows:

\begin{itemize}
 \item The game is \emph{short} and doesn't require complex setups
 \item Each execution is \emph{potentially different} from the others
 \item The game is also very \emph{extensible/customizable} to support a variety of new situations
  or to adapt it to generate more enjoyable scenarios
\end{itemize}

\subsection{Why is this game relevant within the Autonomous Systems course?}

Basically, as many other games, \textbf{\emph{Two Rooms and a Boom} builds on the \emph{autonomy} of its actors (players) to be 
  interesting and enjoyable}. In fact, a good player should be capable of \emph{self-government} and self-protection 
  against the \emph{influence} of other players (which might try to manipulate it).
 Note that one player's autonomy does not just need to be preserved against opponents, but also against team-mates 
  which might suggest uneffective strategies or communicate biased information.

Good players should be \emph{intelligent} and, in particular, 
 capable of complex \emph{epistemic reasoning}, based on all the knowledge they hold about the game.
  Note that the game would be quite boring if a player made deterministic decisions exclusively based on certain knowledge.
  So, the game is interesting because the players can take \emph{unpredictable} decisions and because they can elaborate 
   effective strategies based on \emph{probabilistic, multi-level reasoning}.
   
Also, it's important for a player to be able to \emph{predict} (with a certain \emph{confidence}) the behavior of the other players.
 In doing so, one could reason by asking himself: ``what are their \emph{intentions}? what do they \emph{believe} about me?'' 
 (cf. intentional stance).

Another reason for which players should have strong \emph{executive} autonomy\footnote{Note that nothing prevents 
 the players to exhibit \emph{motivational} autonomy as commonly the actual (meta-)goal -- in most of the games -- is to simply enjoy.}
 and intelligence lies in the fact that each player is \emph{responsible} in respect to all the team-mates. 
 That is, any ``stupid'' action might offend the entire team.

The competition is ruled so as to foster two \emph{conflicting (sub-)goals}. 
 As information potentially represent a competitive advantage, on one side, the less information is given to opponents, the lower
  will be their ability to take profitable decisions. On the other side, the more information is spread across the team,
  the better chance to perform well.
% In the continuum of the dichotomy between conservative and hazardous behaviors, it's usually 

When a player discovers one or more of its team-mates, things get interesting as they can exchange information and
 \emph{cooperatively} build strategies. In particular, a \emph{team-level strategy} could be defined (e.g., 
 ``if you'll find yourself in this situation, do X and Y, and I'll do W and Z'').
 
\emph{Imagination} is also important to \emph{predict} future situations (e.g., ``what is happening in the other room?'' or 
 ``what will happen in the next round?''). Humans in the real game may also have \emph{intuitions} (``\emph{sixth sense}'')
  or capture \emph{unexplicit} signs (e.g., based on personal acquaintance with the other players).

In a nutshell, \textbf{this game exhibits a complexity and characteristics that make it amenable for analysis/modelling/engineering 
 within the conceptual framework developed in the context of the Autonomous Systems course}.
 
 
\newpage
 
\section{Analysis}


\subsection{Elements}

\begin{itemize}
 \item 2 Teams: blues and reds
 \item N Players per team
 \item 2 Rooms
 \item Team roles
    \begin{itemize}
      \item President (only 1 in blue team)
      \item Bomber (only 1 in red team)
      \item Normal player (N-1 per team) 
    \end{itemize}
 \item Roles within a room
    \begin{itemize}
      \item Normal actor (N-1 per room)
      \item Room leader (1 per room)
    \end{itemize}
 \item Actions (see Section \ref{sec:actions})
 \item Information units
    \begin{itemize}
      \item Team/role of a player
      \item Strategy (i.e., mapping from contexts to actions?) of a player
      \item Statements by other players
      \item Observed actions in the past
      \item ...Opinions? Confidence about one's future actions? Confidence about future states-of-affairs?...
    \end{itemize}
 \item $R$ rounds
 \item Phases
    \begin{enumerate}[ref={Phase \arabic*}]
      \item Setup: assignment of a pair (team, role) for each member; equally-sized random distribution of players into the two rooms
      \item \label{rstart} Round
    \begin{enumerate}[label*=\arabic*]
        \item Room leader selection
        \item Interaction
        \item Exchange of hostages (as decided by the room leaders)
    \end{enumerate}
      \item End of game or back to \ref*{rstart} if $num\_rounds < R$
    \end{enumerate}
 \item Rules
    \begin{itemize}
      \item Room leader selection by voting (if parity, a leader is randomly chosen)
      \item The players cannot interact during leader selection
      \item Reds win if the Bomber is in the same room as the President; otherwise, blues win.
      \item A round terminates once a \emph{round termination condition} takes place (e.g., time-based or per num-of-interactions)
      \item (No rules in interaction)
    \end{itemize}
\end{itemize}


\subsection{Actions}
\label{sec:actions}

An \emph{action} is any interaction, performed by an a player, which potentially has an effect on the world, that is, on the other players
 or on the game.

There are two kinds of actions:

\begin{enumerate}
 \item Communication actions
 \item Game-related actions
\end{enumerate}

While communication actions can be performed in any game phase, other actions are specific to a given phase. 
 So, doing actions at the wrong time will simply make them fail.
 
Phase or context-specific actions include:

\begin{itemize}
 \item Room leader selection
 \begin{itemize}
  \item Vote
 \end{itemize}

 \item Interaction
 \begin{itemize}
  \item Co-reveal 
  \item Colour-reveal
 \end{itemize}
 
 \item Hostages exchange
 \begin{itemize}
  \item (Only for room leaders) Selection of hostages
 \end{itemize}
\end{itemize}


\subsubsection{Communication actions}

The game is very free for what concerns the interactions that can take place.

However, typical communications include:

\begin{itemize}
 \item General
 \begin{itemize}
  \item Ask X for some information
  \item Tell some information to X
 \end{itemize}

 \item Interaction
 \begin{itemize}
  \item Ask X to co-/colour-reveal 
  \item Accept/reject to co-/colour-reveal
 \end{itemize}
\end{itemize}


\subsection{Reasoning}

Typically, complex practical and epistemic reasoning is carried out by players.

The reasoning process has the following characteristics:
\begin{itemize}
 \item It may have ``genetically'' specific features -- i.e., each player may reason in a different manner or have different \emph{attitudes}
 \item It is probabilistic/stochastic
\end{itemize}


The following are examples of reasoning.

\subsubsection{Epistemic reasoning}

\begin{tabular}{ | l | l | }
  \hline
  \textbf{Given} & \textbf{Inferred knowledge} \\
  \hline
  (i) The number of players & The probability of player P to have role R \\ 
  (ii) The distribution of roles in a team & \\ 
  (iii) The roles of a subset of players & \\
  \hline
  (i) Player X asks me to co-reveal & \texttt{X = president/bomber} is quite probable. \\
  %\footnote{NOTE: a smart player may ask to co-reveal but then not do it.} \\
  (ii) I don't know his/her role & \\
  \hline
  (i) A team-mate tells me some information & That information is true. \\
  \hline
  ... & ... \\
  \hline
\end{tabular}

\subsubsection{Practical reasoning}

\begin{tabular}{ | l | l | }
  \hline
  \textbf{Given} & \textbf{Inferred action} \\
  \hline
  (i) Me and my team-mates have decided that X has to be voted  & Vote X \\ 
  \hline
  (i) I am the room leader and a red player & Send the bomber to the other room based on \\
  (ii) It is the last round & the probability of the president to be sent here \\
  (iii) The bomber is in this room &  \\
  (iv) The president is in the other room & \\
  \hline
  ... & ... \\
  \hline
\end{tabular}
 
 
\section{Technical requirements}

The analysis pointed out many features of the game. 

When the game is implemented as a software system, it should satisfy the following technical requirements:


%In order to reduce the \emph{abstraction gap}, we draw the following technical requirements:

\begin{itemize}
% \item \textbf{Agent-oriented paradigm}: a sound conceptual framework to understand/design/build agents 
%  that exhibit characteristics such as autonomy, proactivity, situatedness
 \item \textbf{Ontological reasoning}: so that agents can infer implicit knowledge from explicitly represented facts
 \item \textbf{Probabilistic reasoning}: so that agents can deal with uncertainty
%\item (Approximate/fuzzy reasoning?)
\end{itemize}

In addition to such aspects, we should be able to express the following features:

\begin{itemize}
 \item \textbf{Attitudes} for agents
 \begin{itemize}
  \item E.g., the \emph{``character''} (inclination) of a player may be considered as associated with a particular \emph{``approach''} 
  to the game (which ultimately defines a coherent position wrt strategies). 
  However, players may occasionally take \emph{unexpected} courses of action.
 \end{itemize}
 \item \textbf{Cooperative strategies}
 \item \textbf{Stochastic behavior}
 \item (\textbf{Falsehood} and \textbf{trust}?)
\end{itemize}


\section{Feasibility analysis}

A question that must be addressed is: \textbf{can we build, with \emph{reasonable} effort, a computer program that is able to play 
 this game \emph{effectively}?}
 
Of course, the answer depends on the meaning of ``reasonable'' and ``effectively''.
 
At a first sight, \emph{Two Rooms and a Boom} is a game that just \emph{seems} unplayable by computers\footnote{``You insist that there is something that a machine can't do. If you will tell me precisely what it is that a machine cannot do, then I can always make a machine which will do just that.'' (Von Neumann)}. 

Human players try to do their best to conceal information, misguide supposed enemies,
  perturb other players, deduce facts from any manifestation or sign (be it verbal \emph{or not}),
  or elaborate strategies that could not be predicted by other players. 
 Very complex reasoning takes place, clearly multi-level (it's common to reason about the way other players reason) and 
  based on a lot of context information.

 In general, multiple levels of feasibility can be considered:
 
\begin{enumerate}
 \item \emph{Theoretical feasibility of reasoning}: relating to decidability issues in ontological/probabilistic reasoning
 \item \emph{Practical feasibility of reasoning}: relating to complexity and time constraints 
 \item \emph{``Turing-test'' feasibility}: can we build agents that are able to play this game just like humans do?
 \item \emph{Project feasibility}: can we build a system of acceptable quality within reasonable time/effort constraints?
\end{enumerate}

The ``plain vanilla'' game seems to be too unconstrained (free of rules). 
 As a consequence, the game designers have the burden to codify anything the players can talk about.
 
So, we should think about the possibility of shaping/restricting the game so as to find a \emph{trade-off}
 between complexity and playability.
 
 \definecolor{darkgreen}{rgb}{0.0, 0.5, 0.0}
\definecolor{white}{rgb}{1.0, 1.0, 1.0}
\lstset{backgroundcolor=\color{white}, language=Java, keywordstyle=\color{blue}, numbers=left, frame=single,
  tabsize=4, basicstyle=\footnotesize\ttfamily,
           keywordstyle=\color{blue}\ttfamily,
           stringstyle=\color{red}\ttfamily,
           commentstyle=\color{darkgreen}\ttfamily,
          breaklines=true }
 
\section{Prototyping}

\subsection{Game modelling}

The object-oriented paradigm effectively supports the modelling of static game elements and game-related information.

The following UML diagram concisely shows these entities by a \emph{structural} point of view. $\\$

\includegraphics[scale=0.5]{img/AS-Project-Game-StructureUML.png}

\subsection{Mapping from game to concepts}

\textbf{Players} -- First of all, they are \textbf{autonomous} entities.
 Secondly, they play the game, they do something, they \emph{act}. Thus, players can be naturally mapped to \textbf{agents}.
 They are autonomous and, as a consequence, their \emph{moving force} is internal; i.e., they are \textbf{proactive}.
 
\begin{lstlisting}
/* Initial goals */

!start.

/* Plans */

+!start <- wanna_play. 
\end{lstlisting}

 
$\\$
\textbf{Actions} -- The actions are \emph{situated} in the sense of Suchman \cite{situated_actions}. 
In other words, the \emph{context} in which they are carried out is prominent.
A player could (as it is autonomous, ``free'') advance a vote for its candidate for room leadership during the phase of hostages swapping, 
 but it would not have any effect as the action would be performed at the wrong time.
 
\begin{lstlisting}
    @Override
    public boolean executeAction(String agName, Structure action) {
      boolean result = false;

      List<Term> terms = action.getTerms();
      String functor = action.getFunctor();
      Player p = game.getPlayerFromName(agName);
      
      if(functor.equals(Actions.WANNA_PLAY) && game.IsAt(GamePhase.Init) ){
        result = game.AddPlayer(agName);
      } else if(functor.equals(Actions.VOTE_LEADER) && game.IsAt(GamePhase.LeaderSelection)){
        String candidateLeader = ((Atom)terms.get(0)).toString();
        result = game.Vote(agName, candidateLeader);
      } else if(functor.equals(Actions.SELECT_HOSTAGE) && game.IsAt(GamePhase.HostageSelection)){
        String hostage = ((Atom)terms.get(0)).toString();
        result = game.SelectHostage(p, hostage);
      } 
      
      /* ... */
    }
\end{lstlisting}
 
$\\$
\textbf{Two rooms} -- The players are \textbf{situated} in a room, and can be \textbf{moved} from a room to another.
 The rooms constitutes the game \textbf{environment} and effectively define a notion of \textbf{locality}, thus
  ensuring that only players of the same room can interact.
 
$\\$
\textbf{Game, game rules, game phases} -- The game phases set the \textbf{context} for the behavior of the agents. 

\begin{lstlisting}
+phase(leader_selection) <-
  !decide_who_to_vote(Who);
  vote(Who);
  .

+phase(interaction) <-
  /* ... */
  .
  
+phase(hostages_exchange) : .my_name(Me) & room_leader(Me) <-
  !choose_hostage(H);
  select_hostage(H);
  . 
\end{lstlisting}  

The players \textbf{coordinate} and act on the basis of the game rules.
 The environment, by representing the game in itself, can support the coordination of players in a \textbf{stigmergic} fashion.

 Each agent keeps a \textbf{representation} of the state of the game and its \emph{history} as well. 

% Passaggio da implementazione dove
%   Room leader effettuano azione: selezione ostaggio XXX
%   E l'ambiente SPOSTA gli agenti
% 
% A un'implementazione dove
%   Room leader effettuano azione: selezione ostaggio XXX
%   L'ambiente comunica agli ostaggi di muoversi nella stanza opposta
%   Quando ciò avviene (azione di movimento degli ostaggi), il gioco può proseguire
 
$\\$
\textbf{Hostages swapping} -- The fact that players can only be moved (and cannot freely move) 
 \ul{should not be interpreted as a lack of autonomy} ;
  instead, it should be thought as a \emph{deliberate} reduction of self-determination resulting from the will 
  of playing the game according to its rules.
 Then, player agents can be aware of that (e.g., by explicitly having the goal of playing the game) or not (e.g., by having the goal 
  of following the rules of the game structurally embedded).
 
$\\$
\textbf{\emph{Two Rooms and a Boom} as a \emph{social} game} -- The players in the game can be collectively mapped to 
 the notion of \textbf{society}. As in human societies, the agents have different \textbf{roles} within a given 
 social context (here, the game). As in human societies, different players may have conflicting \textbf{goals}.
 As in human societies, players with the same roles could \textbf{collaborate} to reach goals that could be hard 
 to be achieved with isolated effort.
 
 
\subsection{Implementation in Jason}

Jason is a Java-based, multi-agent system development platform.

It has been chosen as the platform for the development of the prototype of the game because it 
 provides many first-class abstractions that directly map to the elements of 
 our conceptual game model.
 
\subsubsection{The system} -- The system (environment and agents) is declaratively described as follows:

\begin{lstlisting}
 MAS prototype_TwoRoomsAndABoom {
 
  infrastructure: Centralised

  environment: proto1.env.Env(6, gui)

  agents:
    roby  proto1_dumb_player;
    marco   proto1_player;
    ste   proto1_player;
    fede  proto1_player;
    fanny   proto1_player;
    vale  proto1_player;

  aslSourcePath:
    "src/asl";
}
\end{lstlisting}

\subsubsection{Architecture: components, organization, responsibilities} -- 

The game environment is organized using the MVC architectural pattern.

\includegraphics[scale=0.5]{img/AS-Project-Game-Architecture.png}
 
 
\subsection{Screenshot} 
 
 \includegraphics[scale=0.4]{img/AS-twoRooms-screenshot.png}
