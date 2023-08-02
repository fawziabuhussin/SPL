package bgu.spl.net.impl.echo;

// import bgu.spl.net.impl.stomp.ConnectionsImpl;
// import bgu.spl.net.srv.Server;

public class EchoServer {

    public static void main(String[] args) {
        // ConnectionsImpl<String> connections = new ConnectionsImpl<>(); // one shared object

        // you can use any server... 
        // Server.threadPerClient(
        //         7777, //port
        //         () -> new EchoProtocol(), //protocol factory
        //         LineMessageEncoderDecoder::new //message encoder decoder factory
        // , connections).serve();

        // Server.reactor(
        //         Runtime.getRuntime().availableProcessors(),
        //         7777, //port
        //         () -> new EchoProtocol<>(), //protocol factory
        //         LineMessageEncoderDecoder::new //message encoder decoder factory
        // ).serve();
    }
}
