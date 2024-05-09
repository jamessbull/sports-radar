# sports-radar
interview coding excercise

I had to spend a while thinking about the home and away part of things.

At first I thought that maybe a team had a property that was a location enum which was home or away but that didn't feel 
quite right because if a game just took two teams then you could pass in two home teams or two away teams which wouldn't
make much sense. There has to be a sinlge home team and a single away team and then you would have to write extra code to
enforce that.

So I then thought about a home team class and an away team class but I wasn't happy
with that either because a football team is still a football team regardless of whether they are home or away so I ended 
up with a class for Home and a class for Away which each took a footballTeam as a constructor parameter.

I'm not sure that is the right thing but it does give me type safety and we have a single class for team

# Assumptions
I have assumed (and I think it is a safe assumption) that a single football team can only play one match at a time so 
identifying games by the home team seemed like a simple way to do things.

I have also assumed that where operations do not appear to make a lot of sense I will throw exceptions. 
It may be acceptable to silently do nothing if you try to start two games at once but similarly it might be something 
you want to know about. I would discuss requirements for edge cases to figure out what was best. I think though that we 
should try for some sort of visibility in the event of unexpected operation of the system.