import java.util.ArrayList;

public class Topology {
    int id;
    int num_servers;
    int num_neighbors;
    ArrayList<Integer> serverIDs;
    ArrayList<CostMap> costs;
    ArrayList<IpPortMap> ipsAndPorts;

    public Topology(int serverId, int num_servers) {
        this.num_servers = num_servers;
        this.id = serverId;
        this.serverIDs = new ArrayList<>();
        this.costs = new ArrayList<>();
        this.ipsAndPorts = new ArrayList<>();
    }

    public ArrayList<IpPortMap> getIpsAndPorts() {
        return ipsAndPorts;
    }

    public void setIpsAndPorts(ArrayList<IpPortMap> ipsAndPorts) {
        this.ipsAndPorts = ipsAndPorts;
    }

    public void setThisServerID(int id) {
        this.id = id;
    }

    public void setServerID(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }


    public ArrayList<Integer> getserverIDs() {
        return serverIDs;
    }

    public void setserverIDs(ArrayList<Integer> serverIDs) {
        this.serverIDs = serverIDs;
    }

    public ArrayList<CostMap> getCosts() {
        return costs;
    }

    public void setCosts(ArrayList<CostMap> costs) {
        this.costs = costs;
    }

    public void addToServerIDs(int numOfServers) {
        int i = 0;
        num_neighbors = numOfServers - 1;
        while (i < numOfServers) {
            serverIDs.add(i + 1);
            i++;
        }
    }

    public void addCosts(int serverId1, int serverId2, String cost) {
         costs.add(new CostMap(serverId1, serverId2, cost));
    }


    public void addToIpPortMap(String ip, String port, int serverId, boolean connected) {
        ipsAndPorts.add(new IpPortMap(ip, port, serverId, connected));
    }

    public CostMap getCostMapByServerID2(int serverID2){
        for(CostMap c : costs){
            if(serverID2 == c.neighborId){
                return c;
            }
        }
        return new CostMap(0,0,"null");
    }

    public void setCostMapByServerID2(int serverID2, String newCost){
        for(CostMap c : costs){
            c.setCost(newCost);
        }
    }

    public void print(){
        System.out.println("1) num_servers   :"+ num_servers);
        System.out.println("2) num_neighbors :"+ num_neighbors);
        int j =3;
        for(IpPortMap server : ipsAndPorts){
            System.out.print(j+") ");
            server.print();
            j++;
        }
        for(int i =0; i < costs.size();i++,j++ ){
            System.out.print(j+ ") ");
            costs.get(i).print();
        }
    }
}