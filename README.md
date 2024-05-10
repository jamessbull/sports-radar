# Sportradar

## Approach

I have decided to take an approach with this where I do not use mutation and instead return new objects for everything.
I believe this makes it easier to reason about what is happening because you start with one state at the beginning and 
have another state at the end and it will not be updated while you use it.

If this library needs to be accessed by many users at once on different threads then I would separate that concern away 
from this library and introduce another class that would hold a scoreboard and manage access to it by using a lock and 
locking on both read and write operations. This would guarantee correctness but would carry a performance penalty which
would need to be evaluated.


## Assumptions

### Home and away

Initially I thought that a team had a location enum which was home or away but that would allow you to pass in two home 
teams or two away teams which wouldn't make much sense. There has to be a sinlge home team and a single away team.

So I then thought about a home team class and an away team class but I wasn't happy
with that either because a football team is still a football team regardless of whether they are home or away so I ended 
up with a class for Home and a class for Away which each took a footballTeam as a constructor parameter.

I'm not sure that is the right thing but it does give me type safety, we are guaranteed to have a single home team and a 
single away team, and we have a single class for team.

### Team sources
I have decided that where the teams come from is not in scope for this. In real life I would check this assumption I 
assume that the scoreboard library would be used by another class that had access to a class that would supply it with 
the teams it needs,  perhaps a Supplier<List<Team>> or perhaps a repository that would load directly from a database.
I don't know how much detail we need on the teams or whether they could be updated during the match. For the purposes of 
these requirements though we only use the name of the team and that feels unlikely to change during a match.

### Identifying games and teams
I have assumed (and I think it is a safe assumption) that a single football team can only play one match at a time so
identifying games on the board by either of the teams seemed like a simple way to do things.

I have assumed that teams can be uniquely identified by their names. If this assumption turned out to be untrue you would
add an id which is a guid so you could distinguish teams with the same name.

### Updating scores
The requirements suggest that the scoreboard supports the update score. I would check how literally we should take this.
I did consider an approach where you do something like
....
~~~~
val game = scoreBoard.getGameFor(team)
val updatedGame = game.updateScore(Score(1,1))
val updatedBoard = scoreBoard.updateGame(updatedGame)
~~~~

This has the advantage that each update method only takes a single parameter but in the end I have gone with a method on
the scoreboard which uses one of the teams to identify the game (given a team can only play one game at once)

~~~~
val updatedboard = scoreBoard.updateScore(team, Score(10,0))
~~~~

I think that a single line of code to update the score is better in this instance. 

The requirements say that the update score method should receive a pair of scores I have not taken this to literally 
mean use a pair class. I think a domain object called Score that wraps two ints is good for now.

### Error handling 

I have also assumed that where operations do not appear to make a lot of sense I will throw exceptions. 
It may be acceptable to silently do nothing if you try to start two games for the same team at once but similarly it 
might be something you want to know about. I would discuss requirements for edge cases to figure out what was best. 
I think though that we should try for some sort of visibility in the event of unexpected operation of the system. At the 
very least that means logging something ideally we would have a support page in the app that would detail these
erroneus requests.



