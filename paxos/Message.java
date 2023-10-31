package paxos;

public class Message 
{
    public enum MessageType
    {
        Fail,
        Prepare,
        Promise,
        Propose,
        Accept,
        Commit,
        NULL,
        NC
    }

    public int sender;
    public int receiver;

    public int timeID;
    
    public MessageType type;
    public String value;

    public String previousProposal = null;
    public int previousProposalTime;

    public Message(String m)
    {
        if (m == null)
        {
            type = MessageType.NULL;
            sender = -1;
            receiver = -1;
            value = "";
            timeID = -1;
        }
        else
        {
            //Message format is:
            //-s sender; -r receiver; -v value; -t type; -i timeID;
            int jump = 3;

            String sub = m.substring(m.indexOf("-s"));
            sender = Integer.parseInt(sub.substring(jump, sub.indexOf(";")));

            sub = m.substring(m.indexOf("-r"));
            receiver = Integer.parseInt(sub.substring(jump, sub.indexOf(";")));

            sub = m.substring(m.indexOf("-v"));
            value = sub.substring(jump, sub.indexOf(";"));

            sub = m.substring(m.indexOf("-t"));
            String t = sub.substring(jump, sub.indexOf(";"));

            sub = m.substring(m.indexOf("-i"));
            timeID = Integer.parseInt(sub.substring(jump, sub.indexOf(";")));

            switch(t)
            {
                case "Prepare":
                    type = MessageType.Prepare;
                    break;
                case "Promise":
                    type = MessageType.Promise;
                    break;
                case "Propose":
                    type = MessageType.Propose;
                    break;
                case "Accept":
                    type = MessageType.Accept;
                    break;
                case "Commit":
                    type = MessageType.Commit;
                    break;
                case "NC":
                    type = MessageType.NC;
                    break;
                default:
                    type = MessageType.Fail;
            }
        }
    }

    public Message(int r, int s, String val, int t, MessageType ty)
    {
        receiver = r;
        sender = s;
        value = val;
        timeID = t;
        type = ty;
    }

    //Exclusvie to proposals
    public void PreviousProposal(String val, int time)
    {
        previousProposal = val;
        previousProposalTime = time;
    }

    public String TypeToString()
    {
        switch(type)
        {
            case Prepare:
                return "Prepare";
            case Promise:
                return "Promise";
            case Propose:
                return "Propose";
            case Accept:
                return "Accept";
            case Commit:
                return "Commit";
            case NC:
                return "NC";
            default:
                return "Fail";
        }
    }

    public String toString()
    {
        return "-s " + Integer.toString(sender) + "; -r " + Integer.toString(receiver) + "; -v " + value + "; -t " + TypeToString() + "; -i " + Integer.toString(timeID) + ";";
    }
}
