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
As the name suggests, one client proposes
