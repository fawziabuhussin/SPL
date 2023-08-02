package bgu.spl.net.impl.stomp;

import bgu.spl.net.srv.Server;

public class StompServer {
    static volatile Integer messageId = 0; // AtomicInteger.

    public static void main(String[] args) {
        
        ConnectionsImpl<String> connections = new ConnectionsImpl<>(); // one shared object
        Integer port = new Integer(args[0]);
        String serverType = args[1];

        if (serverType.equals("tpc")) {
            Server.threadPerClient(
                    port, // port
                    () -> new StompMessagingProtocol(), // protocol factory
                    () -> new StompMessageEncoderDecoder<>() // message encoder decoder factory
                    , connections).serve();
        } else {
            Server.reactor(
                    Runtime.getRuntime().availableProcessors(),
                    port, // port
                    () -> new StompMessagingProtocol(), // protocol factory
                    StompMessageEncoderDecoder::new, // message encoder decoder factory
                    connections)
                    .serve();
        }
    }
}