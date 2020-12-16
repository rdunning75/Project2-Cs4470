import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;


public class distance_vector_routing {
    public static Integer port;
    public static int time;
    public static Timer timer = new Timer();
    public static int numberofpacketsRecieved = 0;
    public static int NUMOFSERVER = 2;
    public static int SERVERID = 0;
    public static Topology server = new Topology(SERVERID, NUMOFSERVER);

    public static void main(String[] args) {
    // Initial command when executing class is 'server -t <filename> -i <timeinterval>'
        if (args.length == 5 && args[0].equals("server") && args[1].equals("-t") && args[3].equals("-i")) {
            String filename = args[2];
            String interval = args[4];
            initialize(filename, interval);

            functions.setServer(server);

            System.out.println("\n[!] Project 2: Distance Vector Routing Program initiated\n");
            server.print();

            // Check port specified in topology file
            Integer test_port = Integer.parseInt(server.getIpsAndPorts().get(0).port);

            if ((test_port >= 1024) && (test_port <= 65535)) {
                // Call function that enables user to select from available commands
                port = test_port;
                time = Integer.parseInt(args[4]);
                program(server, time);
            }
        } else {
            // Print the wrong command to user
            System.out.println("\n[!] " + functions.user_command(args) + " COMMAND ERROR : Incorect call to program. Please use 'server -t <topology-file-name> -i <routing-update-interval>'\n\n");

            System.exit(0);
        }
    }

    private static void initialize(String filename, String time) {
        ArrayList<String> filedata = new ArrayList<>();
        File toplogyfile = new File(filename);

        try {
            Scanner reader = new Scanner(toplogyfile);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                filedata.add(data);
            }
            reader.close();

            server.addToServerIDs(NUMOFSERVER);

            // Read topology file, adding pairs to Topology object
            for (int i = 2; i < 4; i++) {
                String[] topologyData = filedata.get(i).split(" ");
                server.addToIpPortMap(topologyData[1], topologyData[2], i - 1, true);

                // Update server id variable, depending on available IP
                if (functions.getMyIP().equals(topologyData[1])) {
                    SERVERID = i - 1;
                    server.setServerID(SERVERID);
                }
            }

            // Read topology file, adding costs to pair to Topology object
            for (int i = 4; i < 5; i++) {
                String[] costData = filedata.get(i).split(" ");
                server.addCosts(Integer.parseInt(costData[0]), Integer.parseInt(costData[1]), costData[2]);
            }

        // CHANGED
        } catch (Exception e) {
            System.out.println("\n[!] server COMMAND ERROR: Could not read topology file. Exiting program.");
            System.exit(0);
        }
    }

    public static void program(Topology server, int time) {
        String[] commands = {"help", "update", "step", "packets", "display", "disable", "crash"};
        final Topology[] finalServer = {server};

        try {
            // Start the thread where program waits for connection
            Thread listen = new Thread(new Runnable() {
                public void run() {
                    functions.listening(port);
                }
            });

            listen.start();

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        functions.step(finalServer[0]);
                    } catch (IOException e) {
                        System.out.println("\n[!] step COMMAND ERROR: Cannot send neighbor step update.");
                    }
                }
            }, time * 1000, time * 1000);
            server = finalServer[0];
        } catch (Exception ex) {
            System.out.println("I pity the fool!");
        }


        boolean status = true;

        while (status) {
            Scanner input = new Scanner(System.in);

            // Get user parameters and confirm it meets the expected command/parameter count
            String[] user_command = input.nextLine().split(" ");

            switch (user_command[0]) {
                case "help":
                    if (user_command.length == 1) {
                        functions.showHelp();
                    } else {
                        System.out.println("\n[!] " + functions.user_command(user_command) + "COMMAND ERROR: Wrong # of parameters. Try again.");
                    }

                    break;
                case "update":
                    if (user_command.length == 4) {
                        try {
                            server = functions.update(server.id, Integer.parseInt(user_command[1]), Integer.parseInt(user_command[2]), user_command[3], server);
                        } catch (Exception e) {
                            System.out.println("\n[!] update COMMAND ERROR: Unable to update cost.");
                        }
                    } else {
                        System.out.println("\n[!] " + functions.user_command(user_command) + "COMMAND ERROR: Wrong # of parameters. Try again.");
                    }
                    break;
                case "step":
                    if (user_command.length == 1) {
                        try {
                            functions.step(server);
                        } catch (Exception e) {
                            System.out.println("\n[!] update COMMAND ERROR: Unable to update cost.");
                        }
                    } else {
                        System.out.println("\n[!] " + functions.user_command(user_command) + "COMMAND ERROR: Wrong # of parameters. Try again.");
                    }
                    break;
                case "packets":
                    System.out.println("Number of packets recieved since last call: " + numberofpacketsRecieved);
                    numberofpacketsRecieved = 0;
                    break;
                case "display":
                    try {
                        functions.display(server.costs);
                    } catch (Exception e) {
                        System.out.println("\n[!] display COMMAND ERROR: Cannot view current routing table.");
                    }
                    break;
                case "disable":
                    try {
                         functions.disable(server, Integer.parseInt(user_command[1]));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "crash":
                    try {
                        functions.crash(server);
                    } catch (IOException e){
                        System.out.println("\n[!] crash COMMAND ERROR: system could not crash");
                        System.exit(0);
                    }
                    break;
                default:
                    System.out.println("[!] COMMAND ERROR: " + functions.user_command(user_command) + " command not in list. Try again.");
            }
        }
    }


}
