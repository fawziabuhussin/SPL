package bgu.spl.net.impl.stomp;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.lang.String;
import java.time.LocalDateTime;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.srv.Connections;

public class StompMessagingProtocol implements MessagingProtocol<String> {
    private ConnectionsImpl<String> connections;
    private int clientId;
    private volatile boolean connectionStatus = false;
    private volatile boolean shouldTerminate = false;
    String currentUsername;

    @Override
    // server receives frame.
    public String process(String msg) {
        /*
         * msg can be:
         * login, join, exit, report, summarize, logout.
         */
        String[] str = msg.split("\n");
        String commandIn = str[0];
        // check str[0], and process the msg.
        String returnerValue = "";
        if (validation(str)) {
            switch (commandIn) {
                case "CONNECT":
                    returnerValue = connect(str);
                    break;
                case "DISCONNECT":
                    returnerValue = disconnect(str);
                    break;
                case "SUBSCRIBE":
                    returnerValue = sub(str);
                    break;
                case "UNSUBSCRIBE":
                    returnerValue = unsub(str);
                    break;
                case "SEND":
                    returnerValue = send(str, msg);
                    break;
                // default:
                // returnerValue = error("Wrong Input", "-1");
                // connections.disconnect(clientId, currentUsername);
            }
        } else {
            returnerValue = error("Wrong Input", "-1");
            connections.disconnect(clientId, currentUsername);
        }   
        String stayed = connectionStatus == true? "connected":"dis-connected";
        System.out.println("[" + LocalDateTime.now() + "]: " + "responding to user: "
         + currentUsername + ", with id: " + clientId +
         ", responding with: " + returnerValue.split("\n")[0] +  ", connection status: " + stayed );

        return returnerValue;
    }

    private boolean validation(String[] str) {
        boolean valdiation = false;
        String commandIn = str[0];
        switch (commandIn) {
            case "CONNECT":
                if (str.length >= 5) {
                    if (str[1].equals("accept-version:1.2")) {
                        if (str[2].equals("host:stomp.cs.bgu.ac.il")) {
                            if (str[3].split(":")[0].equals("login")) {
                                if (str[4].split(":")[0].equals("passcode")) {
                                    valdiation = true;
                                }

                            }
                        }
                    }

                }

            case "DISCONNECT":
                if (str.length >= 2) {
                    if (str[1].split(":")[0].equals("receipt")) {
                        valdiation = true;
                    }

                }

            case "SUBSCRIBE":
                if (str.length >= 4) {
                    if (str[1].split(":")[0].equals("destination")) {
                        if (str[2].split(":")[0].equals("id")) {
                            if (str[3].split(":")[0].equals("receipt")) {
                                valdiation = true;
                            }
                        }
                    }
                }

            case "UNSUBSCRIBE":
                if (str.length >= 3) {
                    if (str[1].split(":")[0].equals("id")) {
                        if (str[2].split(":")[0].equals("receipt")) {
                            valdiation = true;
                        }

                    }
                }

            case "SEND":
                if (str.length >= 2) {
                    if (str[1].split(":")[0].equals("destination")) {
                        valdiation = true;
                    }
                }
        }
        return valdiation;
    }

    /*
     * this method will call connections.connect(connnectionId)
     * this function add the client connectionHandler to connections.
     */
    @Override
    public void start(int connectionId, Connections<String> connectionsP) {
        clientId = connectionId;
        connections = (ConnectionsImpl<String>) connectionsP; // to be checked.
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    private String error(String msg, String receipt) {
        HashMap<String, String> headers = new HashMap<String, String>();
        if (receipt != "-1") // frame sent does have receipt
            headers.put("receipt-id", receipt);
        headers.put("message", msg);
        return new StompFrame("ERROR", headers, "").toString();
    }

    private String send(String[] str, String msg) {
        String topic = str[1].split(":/")[1];
        // get subId of username
        boolean subed;
        if (connections.topics.get(topic) != null)
            subed = (connections.topics.get(topic)).contains(clientId);
        else
            subed = false;
        // create msg for each Id.
        if (subed) {
            ConcurrentLinkedQueue<Integer> clientsOfChannel = connections.topics.get(topic);
            for (Integer clt : clientsOfChannel) { // run over all the strings.
                if (clt != clientId) {
                    String msgForClient = createMsgId(clt, msg, topic);
                    // for each client in the topic send the msg.
                    connections.send(clt.intValue(), msgForClient);
                }
            }
        }

        else {
            connections.disconnect(clientId, currentUsername);
            return (new StompFrame("Client is not subscribed to this game", connections.messageId.getAndIncrement())
                    .toString());
        }
        // connections.send(topic, msg);
        return createMsgId(clientId, msg, topic);
    }

    private String createMsgId(int clientId2, String msg, String topic) {
        // get the subId
        String subId = connections.subscribitionsIdsClient.get(clientId2).get(topic);
        int messageId = connections.messageId.getAndIncrement();
        String body = msg.split("\n\n")[1];
        String res = (new StompFrame()).message(Integer.parseInt(subId), messageId, topic, body);
        return res;
    }

    /*
     * Unsubscribe to channel.
     * Given the id of the subs, find the sub and disconnect from it. (connections
     * will work.)
     */
    private String unsub(String[] str) {
        String subscritionid = str[1].split(":")[1];
        String receipt = str[2].split(":")[1];
        boolean unsub = connections.unsubscribe(clientId, subscritionid);

        // make frame.
        if (unsub) {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("receipt-id", receipt);
            return new StompFrame("RECEIPT", headers, "").toString();
        } else {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("receipt-id", receipt);
            headers.put("message", "There is no such a game");
            connections.disconnect(clientId, currentUsername);
            return new StompFrame("ERROR", headers, "").toString();
        }
    }

    /*
     * subscribe to channel.
     */
    private String sub(String[] str) {
        String gameName = str[1].split(":/")[1];
        String subscribtionid = str[2].split(":")[1];
        String receiptId = str[3].split(":")[1];
        // String receipt = str[2].split(":")[1];
        connections.subscribe(clientId, gameName, subscribtionid, currentUsername); // we should add username- search in
                                                                                    // hash maps according to clID

        // make frame.
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("receipt-id", receiptId);
        return new StompFrame("RECEIPT", headers, "").toString();
    }

    private String disconnect(String[] str) {
        connections.disconnect(clientId, currentUsername);
        connections.username_active.remove(currentUsername);
        connections.username_active.put(currentUsername, false);
        connectionStatus = false;
        return (new StompFrame("RECEIPT", str[1].split(":")[1], "").toString()); // str[1].split(":") = receipt id.
    }

    private String connect(String[] str) {

        String res;
        String username = str[3].split(":")[1]; // get username
        String password = str[4].split(":")[1]; // get pw
        currentUsername = username;
        boolean activeUser = false;
        // user exists, but there is connection.
        if (connections.username_active.containsKey(username) && connections.username_active.get(username) == true)
            activeUser = true;
        // user exists.
        if (activeUser) {
            res = error("User is already logged in", "-1");
        } else if (connections.username_password.containsKey(username)) {
            if (password.equals(connections.username_password.get(username))) {
                connectionStatus = true;
                currentUsername = username;
                connections.username_active.remove(username);
                connections.username_active.put(username, true);
                res = StompFrame.connect();
            }
            // wrong password
            else {
                res = error("Wrong Password", "-1");
                connections.disconnect(clientId, currentUsername);
            }
        }
        // user does not exist, and there is no connection.
        else {
            connections.username_password.put(username, password);
            connectionStatus = true;
            currentUsername = username;
            connections.username_active.put(username, true);
            res = StompFrame.connect();
        }

        return res;
    }

}
