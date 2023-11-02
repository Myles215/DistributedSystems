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
this case, they send a propose with this value. If they get a quorum of 
