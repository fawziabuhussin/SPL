package bgu.spl.net.impl.stomp;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;

public class ConnectionsImpl<T> implements Connections<T> {

    /*
     * Queue of clients connectionHandlers', maps each id of user to its
     * connectionHandler.
     */
    AtomicInteger messageId = new AtomicInteger(0);
    public final ConcurrentHashMap<Integer, ConnectionHandler<T>> clientsCH = new ConcurrentHashMap<>();
    /* Hash maps that hashs each <ClientId:username> */
    public final ConcurrentHashMap<Integer, String> userSubUd = new ConcurrentHashMap<>();
    /* Hash maps that hashs each <topic:ClientId> */
    public final ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>> topics = new ConcurrentHashMap<>();
    /* Hash maps that hashs each <clientId:<topic,subId>> */
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> subscribitionsIdsClient = new ConcurrentHashMap<>();
    /* Hash maps that hashs each <username:password> */
    public final ConcurrentHashMap<String, String> username_password = new ConcurrentHashMap<>();
    /* Hash maps that hashs each <username:password> */
    public final ConcurrentHashMap<String, Boolean> username_active = new ConcurrentHashMap<>();

    @Override
    /*
     * this method will remove the client connectionHandler from clientsCH with
     * connectionId.
     */
    public void disconnect(int connectionId, String username) {
        // unsub from all the topics, then disconnect from the server.
        ConcurrentHashMap<String, String> clientMappedIdToTopic = subscribitionsIdsClient.get(connectionId); // clientId:
        if (clientMappedIdToTopic != null) { // HashMap<Topic,SubId>
            for (String currentTopic : clientMappedIdToTopic.keySet()) { // run over all the strings.
                String subId = clientMappedIdToTopic.get(currentTopic); // get the subId.
                unsubscribe(connectionId, subId);
            }
        }
        if(username!= null && username_active!= null)
            {
                username_active.remove(username);
                username_active.put(username, false);
            }
        subscribitionsIdsClient.remove(connectionId); // remove the client's HashMap of <Topic,SubId>
        clientsCH.remove(connectionId); // remove the CH from the the HashMap of <ClientId, ConnecttionHandler>
        userSubUd.remove(connectionId);
    }

    /* uses this inside the server, to intiallize the id and the cH. */
    @Override
    public void connect(int id, ConnectionHandler<T> cH) {
        if (cH != null) { // if cH is not null, and is a new client.
            clientsCH.put(id, cH);
            subscribitionsIdsClient.put(id, new ConcurrentHashMap<>());
        }
    }

    @Override
    /*
     * get client connectionHandler and send it a message, client's id
     * (connectionId).
     */
    public boolean send(int connectionId, T msg) { // sends message T to user with connectionsId.
        clientsCH.get(connectionId).send(msg);
        return false;
    }

    public void subscribe(int connectionId, String topic, String subId, String username) { // sends message T to user
                                                                                           // with connectionsId.
        if (!userSubUd.containsKey(connectionId)) {
            userSubUd.put(connectionId, username);
        }
        if (!topics.containsKey(topic)) {
            topics.put(topic, new ConcurrentLinkedQueue<Integer>()); // if the topic does not exist, add it.
            // subsrcitionsIds.put(topic, new ConcurrentHashMap<>());
        }
        topics.get(topic).add(connectionId); // add topic: LL->connectionId.
        subscribitionsIdsClient.get(connectionId).put(topic, subId); // add to the clientId, <topic,subId>

    }

    public boolean unsubscribe(int connectionId, String subId) { // sends message T to user with connectionsId.
        boolean hasTopic = false;
        String topic_ForSubId = "";
        ConcurrentHashMap<String, String> clientMappedIdToTopic = subscribitionsIdsClient.get(connectionId);
        if (!subscribitionsIdsClient.containsKey(connectionId)) {
            return false; // client does not exist.
        }
        if (clientMappedIdToTopic == null)
            return false;
        else {
            for (String topic : clientMappedIdToTopic.keySet()) { // run over all the strings.
                if (subId.equals(clientMappedIdToTopic.get(topic))) {
                    hasTopic = true;
                    topic_ForSubId = topic;
                }
            }
        }
        if (hasTopic == false) {
            return false;
        }
            // Id : <Topic,SubId>
        if (topics.get(topic_ForSubId) != null) {
                topics.get(topic_ForSubId).remove(connectionId); // remove from the topics, connectionId
        }
        if (clientMappedIdToTopic != null) {
                clientMappedIdToTopic.remove(topic_ForSubId); // remove from the clientId: <topic,subId>
        }
        return true;
    }

    /*
     * get all the ids that subscribed of topic "channel" and use send(connectionId,
     * T msg);
     */
    @Override
    public void send(String channel, T msg) { // sends message T to client that subscribed to channel.

    }

}
