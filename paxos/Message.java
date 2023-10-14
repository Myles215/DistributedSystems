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
        Commit
    }

    public int sender;
    public int receiver;
    
    public MessageType type;
    public String value;

    public Message(String m)
    {
        if (m == null)
        {
            type = MessageType.Fail;
            sender = -1;
            receiver = -1;
            value = "";
        }
        else
        {
            //Message format is:
            //-s sender; -r receiver; -v value; -t type;
            String sub = m.substring(m.indexOf("-s"));
            sender = Integer.parseInt(sub.substring(3, sub.indexOf(";")));

            sub = m.substring(m.indexOf("-r"));
            receiver = Integer.parseInt(sub.substring(3, sub.indexOf(";")));

            sub = m.substring(m.indexOf("-v"));
            value = sub.substring(3, sub.indexOf(";"));

            sub = m.substring(m.indexOf("-t"));
            String t = sub.substring(3, sub.indexOf(";"));

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
                default:
                    type = MessageType.Fail;
            }
        }
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
            default:
                return "Fail";
        }
    }

    public String toString()
    {
        return "-s " + Integer.toString(sender) + "; -r " + Integer.toString(receiver) + "; -v " + value + "; -t " + TypeToString() + ";";
    }
}
