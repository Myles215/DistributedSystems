Assignment 3: Paxos implementation

## Asummptions
First, I will start with some assumptions my paxos implementation makes
    - Proposers drive paxos : without a proposer, paxos will not run. There is NO paxos implementation that allows an
    acceptor to flip to be a proposer. So, without a proposer, paxos will not run. If a proposer proposes then goes to sleep
    another proposer will have to wake up to push the algorithm forward. Having an acceptor flip to be a proposer
    could lead to undefined behaviour or non-deterministic outcomes in paxos.

    - A proposer will not read Prepare / Propose messages. I do not handle the case in which a proposer proposes
    a value to another proposer

## Proposers
A proposer is the driver of Paxos. On starting, they prepare their selected value. There are two next paths, if they get
a quorum, or not. If they get a quorum of promises, they check if any of these promises have a previously accepted value. In 
this case, they send a propose with this value. If they get a quorum of replies accepting their proposal they send a commit message to 
all other clients and close. All clients will commit a value on hearing this commit message

## Acceptors
Acceptors reply to proposer messages but will only reply to the most up to date messages they receive. To prepares, they reply a 
promise with the latest value they've accepted (empty if they haven't accepted a value). To proposes, they reply an accepted

## Scearios
To fill in assignment requirements I've made a few 'scenarios' files in the test directory. These can be run with:
javac ./test/scenarios/'filename'.java
java test.scenarios.filename

I will go through one by one what each means

# OneClientProposes
As the name suggests, one client proposes a value and all others immediately reply. Just the simple case "paxos works when one client proposes"
- compile with: javac ./test/scenarios/OneClientProposes.java
- run with: java test.scenarios.OneClientProposes

# M4to9HaveRoles
This test keeps one client proposing but adds roles to clients 4 to 9. As they 'try to vote fairly' they have an 80% chance of replying but will sometimes
fall asleep. As there is an 80% chance each client replies this test could potentially run for a lot more iterations than the first immediaate reply test
- compile with: javac ./test/scenarios/M4to9HaveRoles.java
- run with: java test.scenarios.M4to9HaveRoles

# M2HasRole
M2s role makes it visit the cafe sometimes, where it has good internet. If it's at the cafe, there is a 33% chance it will leave and go back home,
where it's internet is bad. If it's at home it will not reply to messages. Although, when at home, there is a 66% chance it will head back to the cafe where
it can start immediately replying again.

What this means for Paxos is that if M2 is proposing, it will only handle promise and accept messages 66% of the time. Other times, it will omit these messages.
- compile with: javac ./test/scenarios/M2HasRole.java
- run with: java test.scenarios.M2HasRole.java

# M3HasRole
M3 occassionally goes camping. M3 loves camping so will head out and not reply to messages 60% of the time. As camping is a long task, there is a 40%
at each message that M3 has returned and can respond. 

When it is an acceptor, 50% of the time, M3 will leave to go camping and reply extremely late to messages. When it does this, it will print a messae
announcing that it is leaving to camp.

For paxos, this means it will be very very slow when M3 is proposing. When M3 is an acceptor paxos can still trod along but only very slowly
- compile with: javac ./test/scenarios/M3HasRole.java
- run with: java test.scenarios.M3HasRole.java

# JustM1M2M3
Just running paxos with M1 as aproposer and M2 and M3 with their acceptor roles making them reply very slowly (or not at all).

M2 has two actions, one is missing a message. On this activating, they will print "M2 missed message". The other action is driving
home from the cafe, on which they will say "M2 finished their coffee at the cafe". 

M3 has one action, they will sometimes go camping for a whole round, on this they will print "M3 is going camping, see you next round"

In this test, the proposer M1 must also handle late replies, as M3 will reply to current messages in a future round (when they go camping)

- compile with: javac ./test/scenarios/JustM1M2M3.java
- run with: java test.scenarios.JustM1M2M3.java

# M1ProposesAllRoles
All roles switched on, M1 proposing

- compile with: javac ./test/scenarios/M1ProposesAllRoles.java
- run with: java test.scenarios.M1ProposesAllRoles.java

# M1andM2Propose
The test to check if 2 proposers can work in conjunction... They can! The test reaches a result with the first proposer to reach quorum winning