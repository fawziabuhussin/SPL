package bgu.spl.net.impl.stomp;

import java.util.HashMap;
import java.util.Map;

public class StompFrame {

    private final String command;
    private final Map<String, String> headers;
    private final String body;

    public StompFrame(String command, String hds, String body) {
        this.command = command;
        this.headers = new HashMap<String, String>();
        headers.put("receipt-id", hds);
        this.body = body;
    }

    public StompFrame(String command, Map<String, String> headers, String body) {
        this.command = command;
        this.headers = headers;
        this.body = body;
    }

    public StompFrame(String msgError, int msgId) {
        this.command = "ERROR";
        this.headers = new HashMap<String, String>();
        headers.put("receipt-id", msgId + "");
        headers.put("message", msgError);
        this.body = "";
    }

    public StompFrame() {
        this.command = "";
        this.headers = new HashMap<String, String>();
        this.body = "";
    }

    public String getCommand() {
        return command;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String toString() {
        String res = command + "\n";
        for (String key : headers.keySet()) {
            res += key + ":" + headers.get(key) + "\n";
        }
        if (body != null)
            res += "\n" + body; 
        else
            res += "\n";

        return res;
    }

    public static String connect() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("version", "1.2");
        return (new StompFrame("CONNECTED", headers, "")).toString();
    }

    public String message(int subId, int messageId, String destination, String msg) {
        String res = "MESSAGE\n" +
                    "subscription:" + subId + "\n" + 
                    "message-id:" + messageId + "\n" +
                    "destination:" + destination + "\n\n" +
                    msg;
        return res;
    }

    // public static void main(String[] args) {
    //     HashMap<String, String> headers = new HashMap<String, String>();
    //     headers.put("accept-version", "1.2");
    //     headers.put("host", "stomp.cs.bgu.ac.il");
    //     headers.put("login", "fawzi");
    //     headers.put("passcode", "films");
    //     String stri = new StompFrame("CONNECT", headers, "").toString();
    //     String[] str = stri.split("\n");
    //     String username = str[3].split(":")[1]; // get username
    //     String password = str[4].split(":")[1]; // get pw

    // }

}
