package Building;
import java.util.Vector;

public class Node {
    public double [] coordinates;
    static int numNodes = 0;
    NodeType type;
    int nodeId;
    public int floor;
    double weight;
    boolean visited = false;

    //    Vector<Double> distanceToNodes = new Vector();
//    Vector<Node> connectedTo = new Vector();
    Vector<Person> assignedPersons = new Vector();
    Vector<Path> Paths = new Vector();

    Node(NodeType Type, double [] d,int Floor){
        floor=Floor;
        coordinates = new double[2];
        coordinates[0] = d[0];
        coordinates[1] = d[1];
        nodeId = numNodes++;
        type= Type;
        switch (type){
            case doubleDoor:{
                weight = 1;
                break;
            }
            case singleDoor:{
                weight = 1.1;
                break;
            }
            case stairs:{
                weight = 0.9;
                break;
            }
            case buildingExit:{
                weight = 1;
                break;
            }
            case goal:{
                weight = 1;
                break;
            }
        }
    }


    Node(NodeType Type){
        nodeId = numNodes++;
        type= Type;
        switch (type){
            case doubleDoor:{
                weight = 1;
                break;
            }
            case singleDoor:{
                weight = 2;
                break;
            }
            case stairs:{
                weight = 3;
                break;
            }
            case buildingExit:{
                weight = 1;
                break;
            }
            case goal:{
                weight = 0;
                break;
            }
        }
    }
    public Vector<Person> listPeople()//Done
    {
        return assignedPersons;
    }

    public int getNumPeople()//Done
    {
        return assignedPersons.size();
    }
    void connect(Node n,double distance){// Done
//        connectedTo.add(n);
//        n.connectedTo.add(this);
//        n.distanceToNodes.add(distance);
//        distanceToNodes.add(distance);

        n.Paths.add(new Path(n,this,distance));
        Paths.add(new Path(this,n,distance));

    }

    public void addPerson(Person p) {//Done
        assignedPersons.add(p);
    }
    public Person removePerson(Person p) { //Done
        Person temp= null;
        for (int i=0;i<assignedPersons.size();i++){
            if(assignedPersons.get(i) == p){
                temp= assignedPersons.remove(i);
                break;
            }
        }
        return temp;
    }
    public double distance(double x, double y)
    {
        double total = Math.sqrt(((x - coordinates[0])*(x - coordinates[0]))+((y - coordinates[1])*(y - coordinates[1])));
        return total;
    }
    public int getNodeId() {
        return nodeId;
    }

//    public double distanceToNode(Node n)
//    {
//        int index = connectedTo.indexOf(n);
//        return distanceToNodes.get(index);
//    }

    public Vector<Path> getPaths(){
        return Paths;
    }
    public NodeType getType() {
        return type;
    }

    public void disconnect(Node target){
        Vector<Path> temp = Paths;
        for (Path p:temp) {
            if(p.start == target || p.end == target){
                Paths.remove(p);
                return;
            }
        }
    }

}
