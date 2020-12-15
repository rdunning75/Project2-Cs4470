import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class functions {
    public static ArrayList<Peer> peerList = new ArrayList<>();

    // Incoming connection
    public static void listening(Integer port) {
        System.out.println("listening function ");
        try {
            // Create socket instance, await new connection
            ServerSocket ss_socket = new ServerSocket(port);
            Socket s_socket = ss_socket.accept();

            Peer peer = new Peer(s_socket, s_socket.getPort());

            // Notify user new connection establish
            System.out.println("You have a new connection from " + s_socket.getInetAddress().getHostAddress() + "\n");
            peerList.add(peer);

            // Print new messages, continue checking status of remote peer
//            peer.printMessage();
            String message = peer.readMessage();
            String[] messageTokens = message.split(" ");
            String function = messageTokens[0];
            System.out.println("Mesasge sent by server is : "+ message);
            System.out.println("Function called by message : "+ function );
            if(function.equals("update")){
            } else if(function.equals("step")){
                ArrayList<CostMap> costs = Project2.server.getCosts();
                for(CostMap cost: costs){
                    if(cost.getNeighborId() == Integer.parseInt(messageTokens[2])){
                        cost.setCost(messageTokens[1]);
                    }
                }
                Project2.server.setCosts(costs);

            } else if(function.equals("disable")) {


            } else {
                peer.printMessage();
            }

            Project2.numberofpacketsRecieved++;

            // Reopen socket for new connection
            s_socket.close();
            ss_socket.close();
            terminate(peer.getId());
            listening(port);
        } catch (IOException e) {

        }
    }

    // FUNCTION # 1: help
    public static void showHelp() {
        try (BufferedReader br = new BufferedReader(new FileReader("help.txt"))) {
            String helpline;
            while ((helpline = br.readLine()) != null) {
                System.out.println(helpline);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // FUNCTION # 2: myip
    public static String getMyIP() throws UnknownHostException {
        InetAddress ip;
        ip = InetAddress.getLocalHost();

        return ip.getHostAddress();
    }

    // FUNCTION # 3: myport implemented in main chat class

    // FUNCTION # 4: connect <destination> <port no>
    public static Topology connect(String destinationIP, Integer portNumber, Topology server, int i) {
        // TODO: Check if IP address is valid
        ArrayList<IpPortMap> ipmap = server.getIpsAndPorts();
        System.out.println("connection function started");
        try {

            Socket socket = new Socket(destinationIP, portNumber);
            Peer peer = new Peer(socket, portNumber);
            System.out.println("You have connected to " + destinationIP + "\n");
            peerList.add(peer);
            ipmap.get(i).setConnected(true);

        } catch (Exception e) {
            // if ip is not connected to ...
            if (!ipmap.get(i).isConnected()) {
                // increasea connection try counter by one
                System.out.println("ERROR: Could not connect to " + ipmap.get(i).getIp());
                ipmap.get(i).setConnectTryCount(ipmap.get(i).getConnectTryCount() + 1);
                // if connection try count is = 3, set it to zero and set is connection to false
                if (ipmap.get(i).getConnectTryCount() >= 3) {
                    System.out.println("Server id tp change : " + server.id + " & " + ipmap.get(i).getServerId());
                    functions.update(server.id, server.id, ipmap.get(i).getServerId(), "infinte", server);
                    ipmap.get(i).setConnectTryCount(0);
                    ipmap.get(i).setConnected(false);
                }
            }
        }
        System.out.println("connection function ended");
        return server;
    }

    // FUNCTION # 5: list
    public static void listPeers() {
        System.out.println("id: IPaddress\t\tPortNo.");

        for (Peer peer : peerList) {
            System.out.println(peer.toString());
        }

        System.out.println("");
    }

    // FUNCTION # 6: terminate <connection id.>
    public static void terminate(int connectionID) {
        for (int i = 0; i < peerList.size(); i++) {
            if (peerList.get(i).getId() == connectionID) {
                peerList.get(i).terminate();
                peerList.remove(i);
                System.out.println("Terminated connection ID " + connectionID + "\n");
                return;
            }
        }
    }

    // FUNCTION # 7: send <connection id.> <message>
    public static void send(int connectionID, String message) {
        boolean in_list = false;

        for (Peer peer : peerList) {
            if (peer.getId() == connectionID) {
                in_list = true;
                // Write message to output stream
                peer.sendMessage(message);


                // Confirmation to sender
                System.out.println("Message sent to " + peer.getId() + "\n");
            }
        }

        if (!in_list) {
            System.out.println("ERROR: Connection ID " + connectionID + " not in list. \n");
        }
    }

    // FUNCTION # 8: exit
    public static void exit() {
        for (Peer peer : peerList) {
            peer.terminate();
        }


        System.out.println("Closing all connections. Terminating processes...\n");
    }

    // Fucntion # 2.1 update
    // TODO: Send signal to update other neighbors topology maps
    //  curently only operates on topology map

    public static Topology update(int currentServerId, int serverID1, int serverID2, String newCost, Topology server) {
        if (currentServerId == serverID1) {
            CostMap currentCost = server.getCostMapByServerID2(serverID2);
            if (!currentCost.getCost().equals("null")) {
                server.setCostMapByServerID2(serverID2, newCost);
//                for(Peer peer : peerList){
//                    for(int i =0; i <server.getIpsAndPorts().size(); i++) {
//                        if (peer.getIp().equals(server.getIpsAndPorts().get(i).ip)) {
//                                    String message = "update " + serverID1 + " " + serverID2 + " " +newCost;
//                                    peer.sendMessage(message);
//                        }
//                    }
//                }
            }
        } else {
            System.out.println("You may only update your connection costs with you onw neighbors");
        }
        return server;
    }

    public static void step(Topology server) throws IOException {
        System.out.println("Step started");
        ArrayList<IpPortMap> neighborIpAndPorts = server.getIpsAndPorts();


        for (Peer peer : peerList) {

            String peerListInetIp = peer.socket.getInetAddress().toString();

            for (int j = 0; j < neighborIpAndPorts.size(); j++) {

                IpPortMap neighbor = neighborIpAndPorts.get(j);

                if (peerListInetIp.equals(neighbor.getIp())) {
                    int neighborId = neighbor.getServerId();
                    CostMap currentCostMap = server.getCostMapByServerID2(neighborId);
                    String message = "step";
                    message = message + " " + currentCostMap.cost;
                    message = message +" " + Integer.toString(currentCostMap.getId());
                    try{
                        System.out.println("Messaage to be sent: "+message);
                        peer.sendMessage(message);
                        System.out.println("Cost sent to Server" + peer.getId() + "\n");
                    } catch (Exception e){
                        System.out.println("Cost not sent to Server" +peer.getId() +"\n");
                        e.printStackTrace();
                    }
                }
            }
        }

        System.out.println("Step ended");

    }

    //Function # display
    //TODO: Display the current routing table. And the table should be displayed in a sorted order from small ID to big.
    public static void display(ArrayList<CostMap> costMapList) {
        int j = 0;
        for (int i = 1; i < costMapList.size(); i++) {
            CostMap temp = costMapList.get(i);
            j = i;
            while (j > 0 && temp.id < costMapList.get(j - 1).id) {
                costMapList.set(j, costMapList.get(j - 1));
                j--;
            }
            costMapList.set(j, temp);
        }

        for(int i = 0; i < costMapList.size(); i++){
            costMapList.get(i).print();
        }
    }

    //Function # disable
    //TODO: Disable the link to a given server. Doing this closes the connection to a given server with server-ID.
    public static Topology disable(Topology server, int serverID){
        System.out.println(server.id);
        System.out.println(serverID);
        System.out.println();
        System.out.println(serverID != server.id );
        System.out.println(!server.getCostMapByServerID2(serverID).cost.equals("infinite"));
        if(serverID != server.id || !server.getCostMapByServerID2(serverID).cost.equals("infinite") ) {
            for (int i = 0; i < server.costs.size(); i++) {
                CostMap currentCostMap = server.costs.get(i);
                if (serverID == currentCostMap.neighborId) {
                    server.costs.get(i).setCost("infinite");

                    //TODO : Handle disabling connection here...

                    System.out.println("Disabled connection with server " + server.ipsAndPorts.get(i).serverId + "(" + server.ipsAndPorts.get(i).ip + ")");
                }
            }
        } else {
            System.out.println("You can only disable connection with your neighbor!!");
        }

        return server;
    }
}