package paxos;
public class PaxosClient {
    
    public static void main(String[] args)
    {

        Boolean isProposer = false;
        String value = "";

        if (args.length > 0)
        {
            isProposer = true;
            value = args[0];
        }
        //check if we start with a proposed value
        //if yes, this client is a proposer

        //if proposer, prepare our value

        //if we get a quorum of promises for our value, send proposal for our value

        //if we don't get quorum, update ID and go back to start

        //send accept message and wait for commit then finalise

    }

}
