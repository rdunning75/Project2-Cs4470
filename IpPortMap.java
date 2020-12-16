public class IpPortMap {
    String ip;
    String port;
    int serverId;
    int connectTryCount;
    boolean connected;

    public IpPortMap(String ip, String port, int serverId, boolean connected) {
        this.ip = ip;
        this.port = port;
        this.serverId = serverId;
        this.connected =connected;
        connectTryCount = 0;
    }

    public int getConnectTryCount() {
        return connectTryCount;
    }

    public void setConnectTryCount(int connectTryCount) {
        this.connectTryCount = connectTryCount;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public void print(){
        System.out.println(serverId + " " +ip+ " "+port);
    }
}