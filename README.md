# Sportradar

## TLDR
If you want to jump straight to the acceptance test which captures the scenario described in the test then you want the 
last test in the file ScoreBoardTest

## Approach

I have decided to take a more functional approach with this where I do not use mutation and instead return new objects 
for everything.
I believe this makes it easier to reason about what is happening because you start with one state at the beginning and 
have another state at the end and it will not be updated while you use it.

If this library needs to be accessed by many users at once on different threads then I would separate that concern away 
from this library and introduce another class that would hold a scoreboard and manage access to it by using a lock and 
locking on both read and write operations. This would guarantee correctness but would carry a performance penalty which
would need to be evaluated.

For the unit tests you will notice that I have kept all tests at the level of the scoreboard when some of the tests 
could be at the individual class level. For example I could easily test the ordering of games separately.
My reasons for doing this are that although there end up being quite a few tests in the test for the scoreboard class
it does not seem unmanageable and by targeting tests at the functional level it leaves us free to refactor the code 
underneath with ease without having to worry about tests for each class or moving them around etc. With the tests at the
scoreboard level giving us confidence we have not broken anything. 

I have found in the past that when you have tests for every method of every class those tests can be brittle and 
lead to refactoring becoming more difficult. This is not an argument for omitting tests more for seeing tests as part of 
an executable specification which where possible should match up closely with the requirements. In an ideal world one 
should be able to take the same tests and apply them to an entirely different implemenation of the same thing and they 
should still work. I acknowledge that this is not always possible in practice.

Of course there are scenarios where the number of possible cases means that you have to have tests at a different level
and in those circumstances that is obviously what I would do. I would use either mocking or stubbing depending on what 
sort of test I am writing. In a case like this I would use a test clock rather than mock it out but for external systems 
I would be more tempted to mock it as we might want to be very explicit about how we interact with it.

I would have external tests as well and do tests against external systems including databases but these would 
clearly not be in the unit test build. End to end tests can also be useful as can ui only tests. In the most recent 
project I worked on I separated out the api layer by making every endpoint use an Action class so you could have requests
from the browser go directly to the real api but have that backed by entirely programmable actions. That allowed me to 
have fast browser tests that were easy to setup and also checked that the url definitions matched between js and backend. 
I then had end to end tests which did not use the browser but fired of http requests directly to the real api with a 
real set of actions behind it.

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



