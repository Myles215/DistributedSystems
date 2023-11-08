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

## Testing
I have a testing harness in the test folder. There are proposer, acceptor and server tests. These check that each specific function of the
server and paxos clients works as expected. I use a mock client and mock server to have control over the flow of data. I also use clients
and a server on a thread to be able to run multiple things at once. These tests can be run with ./gradlew test. 

## Scenarios
To fill in assignment requirements I've made a few 'scenarios' files in the test directory. These can be run with:
./gradlew test --tests 'testName'

Each scenario is implemented using gradle testing. They just start a server on a thread and a number
of clients on threads and run each of these. Each test has some ending conditions (found in a while loop at the end) but will time out
after 60 seconds if it hasn't reached a conclusion. Printouts will occur during each scenario and these can be followed in the terminal. Each message is
in the format:
M 'ID' Handling 'message type' from M 'sender ID'
and potentially any other necessary details. When M1, M2 or M3s value activates they will also send a message

To Run all tests use: ./gradlew test
all printouts will be available

I will go through one by one what each means

# OneClientProposes
As the name suggests, one client proposes a value and all others immediately reply. Just the simple case "paxos works when one client proposes"

- run with: ./gradlew test --tests OneClientProposes

# M4to9HaveRoles
This test keeps one client proposing but adds roles to clients 4 to 9. As they 'try to vote fairly' they have an 80% chance of replying but will sometimes
fall asleep. As there is an 80% chance each client replies this test could potentially run for a lot more iterations than the first immediaate reply test

- run with: ./gradlew test --tests M4to9HaveRoles

# M2HasRole
M2s role makes it visit the cafe sometimes, where it has good internet. If it's at the cafe, there is a 33% chance it will leave and go back home,
where it's internet is bad. If it's at home it will not reply to messages. Although, when at home, there is a 66% chance it will head back to the cafe where
it can start immediately replying again.

What this means for Paxos is that if M2 is proposing, it will only handle promise and accept messages 66% of the time. Other times, it will omit these messages.

- run with: ./gradlew test --tests M2HasRole

# M3HasRole
M3 occassionally goes camping. M3 loves camping so will head out and not reply to messages 60% of the time. As camping is a long task, there is a 40%
at each message that M3 has returned and can respond. 

When it is an acceptor, 50% of the time, M3 will leave to go camping and reply extremely late to messages. When it does this, it will print a messae
announcing that it is leaving to camp.

For paxos, this means it will be very very slow when M3 is proposing. When M3 is an acceptor paxos can still trod along but only very slowly

- run with: ./gradlew test --tests M3HasRole

# JustM1M2M3
Just running paxos with M1 as aproposer and M2 and M3 with their acceptor roles making them reply very slowly (or not at all).

M2 has two actions, one is missing a message. On this activating, they will print "M2 missed message". The other action is driving
home from the cafe, on which they will say "M2 finished their coffee at the cafe". 

M3 has one action, they will sometimes go camping for a whole round, on this they will print "M3 is going camping, see you next round"

In this test, the proposer M1 must also handle late replies, as M3 will reply to current messages in a future round (when they go camping)

- run with: ./gradlew test --tests JustM1M2M3.java

# M1ProposesAllRoles
All roles switched on, M1 proposing

- run with: ./gradlew test --tests M1ProposesAllRoles

# M1andM2Propose
The test to check if 2 proposers can work in conjunction... They can! The test reaches a result with the first proposer to reach quorum winning

- run with: ./gradlew test --tests M1andM2Propose

# M1M2andM3Propose
The test to see if all 3 can propose all at once!
When they all propose at once, due to M2 and M3s roles they often lose, as they are away and do not reply

- run with: ./gradlew test --tests M1M2andM3Propose

# M2ProposesThenLeaves
The test to cover the scneario where M2 proposes then goes offline. As specified a few times: There is no paxos implementation in which an acceptor
flips between acceptor and proposer. Even if nothing happens, even if there is an accepted value, it cannot become a proposer. Why is this though? Well,
it is the same reason why if there is not a quorum of clients active (e.g. 2/5) we have some mass server or node failure. If there are not enough nodes to
complete a normal protocol, is it safe to have a work aorund? Probably not. So, in this scenario, M2 proposes and goes to sleep, then, M1 wakes up and
tries to propose, thus pushing M2s proposal through and we can see in printouts that the committed value is M2IsPresident

- run with: ./gradlew test --tests M2ProposesThenLeaves
