public class CostMap {
    int id;
    int neighborId;
    String cost;

    public CostMap(int id, int neighborId, String cost){
        this.id = id;
        this.neighborId = neighborId;
        this.cost = cost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNeighborId() {
        return neighborId;
    }

    public void setNeighborId(int otroid) {
        this.neighborId = otroid;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }


    public void print(){
        System.out.println(id+" "+neighborId+ " " + cost);
    }
}
