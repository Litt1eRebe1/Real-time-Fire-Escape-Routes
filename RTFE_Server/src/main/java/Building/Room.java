package Building;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Vector;

public class Room {

    /**Attributes*/
    RoomType roomType;
    public Vector<Door> doors = new Vector<Door>();
    private Vector<Person> peopleInRoom = new Vector<>();
    private Vector<Sensor> sensors = new Vector<>();
    private Vector<Room> Rooms = new Vector<Room>();
    protected Vector<Corner> Corners = new Vector<>();
    public Vector<Fire> fires = new Vector<Fire>();
    protected Vector<Vector<Corner>> Walls=new Vector<>(); // Adjacency List Represntation
    Vector<Node> nodesInRooms = new Vector<Node>();
    static boolean verbose = false;

    // Constructor
    public Room(RoomType type) {
        roomType = type;
        Walls.add(new Vector<>());
    }
    /**Self Explanatory*/
    public Vector<Room> getRooms() {
        return Rooms;
    }
    /**Self Explanatory*/
    public Room getRooms(int i ) {
        return Rooms.get(i);
    }
    /**Self Explanatory*/
    public void addRoom(Room room) {
        Rooms.add(room);
    }
    public boolean addDoor(Door d){
        int numDoorsPlaced = 0;
        for (int i = 0; i < Rooms.size(); i++) {
            if(Rooms.get(i).addDoor(d))
                numDoorsPlaced++;
        }
        for (int i = 0; i < Walls.size(); i++) {
            if(onWall(i,d.getCenter())){
                if(roomType == RoomType.stairs)
                    d.changeType(NodeType.stairs);
                doors.add(d);
                return true;
            }
        }
        if(numDoorsPlaced>=1)
            return true;
        return false;
    }

    private boolean onWall(int i, double[] pos) {
        /**
         * @Description: Identify whether or not the pos from the param overlaps with the wall
         *
         * Take note this works but doesnt 'snap' to the diagonal walls so your guessing should be on point
         * consult this link below for some useful math stuff en live triangle demo 0.o (such wow)
         * http://blackpawn.com/texts/pointinpoly
         **/
        //Very specific reason for this no touchy!
        Corner A = Corners.get(i);

        Corner B ;
        if(i==0)
            B = Walls.get(i).get(0);
        else
            B = Walls.get(i).get(1);

        //Line is Vertical
        if(A.x == B.x && A.x == pos[0])
            return (pos[1] < A.z && pos[1] > B.z) || (pos[1] > A.z && pos[1] < B.z);

        //Line is Horizontal
        if(A.z == B.z && A.z == pos[1])
            return (pos[0] < A.x && pos[0] > B.x) || (pos[0] > A.x && pos[0] < B.x);
        
        //Line is diagonal
        return ((A.x <pos[0] && pos[0] <B.x) && (A.z <pos[1] && pos[1] <B.z)) || ((A.x >pos[0] && pos[0] >B.x) && (A.z >pos[1] && pos[1] >B.z));

    }
    private static boolean onSegment(Corner p, Corner q, Corner r)
    {
        if (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x)
                && q.z <= Math.max(p.z, r.z) && q.z >= Math.min(p.z, r.z))
            return true;
        return false;
    }

    private static int orientation(Corner p, Corner q, Corner r)
    {
        double val = (q.z - p.z) * (r.x - q.x) - (q.x - p.x) * (r.z - q.z);

        if (val == 0)
            return 0;
        return (val > 0) ? 1 : 2;
    }

    private static boolean doIntersect(Corner p1, Corner q1, Corner p2, Corner q2)
    {

        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        if (o1 != o2 && o3 != o4)
            return true;

        if (o1 == 0 && onSegment(p1, p2, q1))
            return true;

        if (o2 == 0 && onSegment(p1, q2, q1))
            return true;

        if (o3 == 0 && onSegment(p2, p1, q2))
            return true;

        if (o4 == 0 && onSegment(p2, q1, q2))
            return true;

        return false;
    }

    private static boolean isInside(Corner polygon[], int n, Corner p)
    {
        int INF = 10000;
        if (n < 3)
            return false;

        Corner extreme = new Corner(INF, p.z);

        int count = 0, i = 0;
        do
        {
            int next = (i + 1) % n;
            if (doIntersect(polygon[i], polygon[next], p, extreme))
            {
                if (orientation(polygon[i], p, polygon[next]) == 0)
                    return onSegment(polygon[i], p, polygon[next]);

                count++;
            }
            i = next;
        } while (i != 0);

        return (count & 1) == 1 ? true : false;
    }
    public void removePeople(){
        for (int i = 0; i < Rooms.size(); i++) {
            Rooms.get(i).removePeople();
        }

        while(peopleInRoom.size() != 0){
            peopleInRoom.remove(0);
        }
    }

    private double distance(double[] doorCoordinates, double[] doorCoordinates2)
    {
        double total = Math.sqrt(((doorCoordinates[0] - doorCoordinates2[0])*(doorCoordinates[0] - doorCoordinates2[0]))+((doorCoordinates[1] - doorCoordinates2[1])*(doorCoordinates[1] - doorCoordinates2[1])));
        return total;
    }

    public boolean connectDoors()
    {
        boolean connect = true;
        for (int i = 0; i < getRooms().size(); i++) {
            if(getRooms(i).connectDoors())
            {
                return true;
            }
        }
        for(int j = 0; j < doors.size()-1; j++)
        {
            for(int k = j + 1; k < doors.size(); k++)
            {
//                for(int i = 0; i < Walls.size(); i++)
//                {
//                    if(onWall(i, doors.get(j).getCenter()) && onWall(i, doors.get(k).getCenter()))
//                    {
//                        // DO NOT CONNECT
//                        connect = false;
//                    }
//                }
                if(connect)
                {
                    doors.get(j).node.connect(doors.get(k).node, distance(doors.get(j).getCenter(), doors.get(k).getCenter()));
                    System.out.println("Connecting doors: " + doors.get(j).node.nodeId + " <-> " + doors.get(k).node.nodeId + "  // distance: " + distance(doors.get(j).getCenter(), doors.get(k).getCenter()));
                }
                connect = true;
            }

        }
        return true;
    }

    public Vector<Node> getAllDoors(){
        Vector<Node> currentDoors = new Vector<Node>();

        for (int i = 0; i < doors.size(); i++) {
            currentDoors.add(doors.get(i).node);
        }
        for (int i = 0; i < Rooms.size(); i++) {
            currentDoors.addAll(Rooms.get(i).getAllDoors());
        }
        return currentDoors;
    }
    public Vector<Sensor> getAllSensors(){
        Vector<Sensor> sensors = new Vector<>();

        for (int i = 0; i < Rooms.size(); i++) {
            sensors.addAll(Rooms.get(i).getAllSensors());
        }
        sensors.addAll(this.sensors);
        return sensors;
    }
    /**
     * Removes a person from a Room, This function searches recursively through all the rooms nested inside it to remove the person.
     * @param personToRemove: An object of the person that needs to be removed, typically obtained through a prior search through all the people.
     * @return true - Returns true if the person is found and removed.
     *         false - Returns false if the person is not found within the room.
     * */
    public boolean removePerson(Person personToRemove) {
        for (int i = 0; i < getRooms().size(); i++) {
            if (getRooms(i).removePerson(personToRemove))
                return true;
        }
        for (Person p:peopleInRoom) {
            if(p == personToRemove){
                peopleInRoom.remove(p);
                return true;
            }
        }
        return false;
    }
    public boolean addPerson(Person p){
        for (int i = 0; i <getRooms().size() ; i++) {
            if(getRooms(i).addPerson(p))
                return true;
        }
        Corner [] poly = new Corner[Corners.size()];
        for (int i = 0; i < poly.length; i++) {
            poly[i] = Corners.get(i);
        }
        Corner Point = new Corner(p.position);
        if(isInside(poly,poly.length,Point)){
            peopleInRoom.add(p);
            return true;
        }
        return false;
    }
    public boolean addSensor(Sensor s){
        for (int i = 0; i <getRooms().size() ; i++) {
            if(getRooms(i).addSensor(s))
                return true;
        }
        Corner [] poly = new Corner[Corners.size()];
        for (int i = 0; i < poly.length; i++) {
            poly[i] = Corners.get(i);
        }
        Corner Point = new Corner(s.getLocation());
        if(isInside(poly,poly.length,Point)){
            sensors.add(s);
            for (Door d:doors) {
                s.connect(d.node, distance(s.coordinates, d.node.coordinates));
                if(verbose){
                    System.out.println("Connecting sensors to pathing: "+s.nodeId+"("+s.deviceID+")<->"+d.node.nodeId+"//distance: "+distance(s.coordinates, d.node.coordinates) );
                }
            }

            return true;
        }
        return false;
    }
    public boolean addFire(Fire f){
        for (int i = 0; i <getRooms().size() ; i++) {
            if(getRooms(i).addFire(f))
                return true;
        }
        Corner [] poly = new Corner[Corners.size()];
        for (int i = 0; i < poly.length; i++) {
            poly[i] = Corners.get(i);
        }
        Corner Point = new Corner(f.coords);
        if(isInside(poly,poly.length,Point)){
            fires.add(f);
            if(verbose){
                System.out.println("Fire added in Room Type: "+this.roomType);
            }
            return true;
        }
        return false;
    }
    public JSONArray getFires(JSONArray fires,int floor){
        for (int i = 0; i <getRooms().size() ; i++) {
            getRooms().get(i).getFires(fires,floor);
        }
        if (this.fires.size()!=0){
            JSONObject fireObject = new JSONObject();
            fireObject.put("floor",floor);
            JSONArray corners = new JSONArray();
            for (int i = 0; i < Corners.size(); i++) {
                corners.put(new double[]{Corners.get(i).x,Corners.get(i).z});
            }
            fireObject.put("corners",corners);
            fires.put(fireObject);
        }
        return fires;
    }

    public int destroyRoutes(){
        int numPathsAffected = 0;
        for (Room r:Rooms) {
            numPathsAffected += r.destroyRoutes();
        }
        Vector<Node> doors = getAllDoors();
        for (Node d :doors) {
            for (Fire fire:fires) {
                Vector<Path> tempPaths = new Vector<Path>();
                tempPaths.addAll(d.Paths);
                for (Path p:tempPaths) {
                    if(fire.intersect(p.start,p.end)){
                        System.out.println("Disconnecting Nodes: "+p.start.nodeId+" - "+p.end.nodeId);
                        Node start = p.start;
                        Node end = p.end;
                        numPathsAffected++;
                        start.disconnect(end);
                        end.disconnect(start);
                    }
                }
            }
        }
        return numPathsAffected;
    }

    public int getNumPeople(){
        int total = 0;
        for (int i = 0; i < getRooms().size(); i++) {
            total += getRooms(i).getNumPeople();
        }
        total += peopleInRoom.size();
        return total;
    }

    public Vector<Person> getPeople(){
        Vector <Person> ListOfPeople = new Vector<Person>();
        for (int i = 0; i < getRooms().size(); i++) {
            ListOfPeople.addAll(getRooms(i).getPeople());
        }
//        System.out.println("People In Room "+roomType.toString() +" is "+peopleInRoom.size());
        ListOfPeople.addAll(peopleInRoom);
        return ListOfPeople;
    }
    /**
     * @Description: Returns a string specifying the type of room and if it is valid
     * */
    public String isValidRoom(){
        if(!isCyclic())
            return roomType.name()+ " (room) has an error";
        return roomType.name()+ " (room) is complete";
    }

    /**
     * @Description: Adds a corner to the room, Take Note it means nothing on its own
     * */
    protected void addCorner(Corner c){
        Corners.add(c);
        Walls.add(new Vector<>());
    }

    protected Corner getCorner(int i){
        return Corners.get(i);
    }

    public boolean crossWall(int i, double [] coords)
    {
        boolean inRange = false;
        Vector<Corner> wall = Walls.get(i);
        double [] point1 = wall.get(0).getCoords();
        double [] point2 = wall.get(1).getCoords();
        double largeX = Math.max(point1[0], point2[0]);
        double largeZ = Math.max(point1[1], point2[1]);
        double smallX = Math.min(point1[0], point2[0]);
        double smallZ = Math.min(point1[1], point2[1]);

        if(coords[0] < largeX && coords[0] > smallX)
        {
            inRange = true;
        }
        if(coords[1] < largeZ && coords[1] > smallZ)
        {
            inRange = true;
        }
        return inRange;
    }
    /**
     * Connects 2 corners and adds them into the wall array
     * You can call this function with corners not previously added since it will add it automatically
     * */
    void buildWall(Corner v, Corner w) {
        int i= Corners.indexOf(v);      //  This part prevents
        if(i== -1)                      //  errors by making sure
            addCorner(v);               //  the corner exists in
        int j = Corners.indexOf(w);     //  the room
        if(j== -1)                      //
            addCorner(w);               //

        Walls.get(Corners.indexOf(v)).add(w);
        Walls.get(Corners.indexOf(w)).add(v);
    }
    /**
     * Just a simple way of building Walls
     * */
    public void buildWall(double[] corner1, double[] corner2) {

        Corner a = null;
        Corner b = null;
        for (int i = 0; i < Corners.size(); i++) {
            double [] current =Corners.get(i).getCoords();
            if(current[0] == corner1[0] && current[1] == corner1[1])
                a= Corners.get(i);
            if(current[0] == corner2[0] && current[1] == corner2[1])
                b= Corners.get(i);
        }
        if(a==null)
            a = new Corner(corner1);
        if(b==null)
            b = new Corner(corner2);
        buildWall(a,b);
    }

    /**
     * @brief : See the function "isCyclic()" for more details
     * */
    private Boolean isCyclicUtil(Corner v, Boolean visited[], Corner parent) {
        int _v = Corners.indexOf(v);
        visited[_v] = true;
        Corner i;
        Iterator<Corner> it = Walls.get(_v).iterator();
        while (it.hasNext())
        {
            i = it.next();
            if (!visited[Corners.indexOf(i)]) {
                if (isCyclicUtil(i, visited, v))
                    return true;
            }
            else if (i != parent)
                return true;
        }
        return false;
    }
    /**
     * @return :
     *  true - Returns true if it contains a cycle
     *  false - Returns false if it still needs walls
     * */
    private Boolean isCyclic() {
        if(Walls.lastElement().size() == 0)
            Walls.remove(Walls.size()-1);
        Boolean visited[] = new Boolean[Corners.size()];
        for (int i = 0; i < Corners.size(); i++)
            visited[i] = false;
        for (int u = 0; u < Corners.size(); u++)
            if (!visited[u])
                if (isCyclicUtil(Corners.elementAt(u), visited, null))
                    return true;
        return false;
    }
/*
    public void assignPeople(Vector<Routes> routes){
        for (int i = 0; i < Rooms.size(); i++) {
            Rooms.get(i).assignPeople(routes);
        }

        for (int i = 0; i < peopleInRoom.size(); i++) {
            if(verbose){
                System.out.println("============== Assigning Next Person ==============");
            }
            double Heuristic =  Double.POSITIVE_INFINITY;
            Person p = peopleInRoom.get(i);
            int BestRoute = -1;
            int BestDoor = -1;
            Door BestD = null;
            Routes BestR = null;
            Vector<Node> Bestpath= new Vector<Node>();
//            System.out.println("New Person:");
            for (int j = 0; j < routes.size(); j++) {
                for (int k = 0; k < doors.size(); k++) {
                    Door d = doors.get(k);
                    try {
//                        double distance = routes.get(j).calculateHeuristic(d.node, p);
//                        System.out.println("Heuristic val: "+distance +" for Person: "+p.personID+" on Route "+routes.get(j).RouteName);
                        routes.get(j).resetVisited();
                        Vector<Node> path = routes.get(j).ShortestPathToGoal(d.node, routes.get(j).getGoal());
                        if(verbose)
                            Routes.printPath(path,p);
                        double currentHeuristic = Routes.pathHeuristic(path,p);
                        if (currentHeuristic < Heuristic) {
                            BestRoute = j;
                            BestDoor = k;
                            Heuristic = currentHeuristic;
                            Bestpath = path;
//                            Routes.printPath(Bestpath);
                        }
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                }
            }

            if(BestRoute == -1)
                continue;

            p.setAssignedRoute(routes.get(BestRoute));
            routes.get(BestRoute).addPerson(p);

            if(verbose){
                System.out.println("Person "+p.name+" is assigned to Route "+p.AssignedRoute.RouteName +" Heuristic safety value is: "+Heuristic+ " using door " +doors.get(BestDoor).id);
                Routes.printPath(Bestpath,p);
                System.out.println();
            }
        }
    }
/**/
    public Vector<Person> getPeopleData(Vector<Routes> routes){
        Vector<Person> peopleData = new Vector<>();
        for (int i = 0; i < Rooms.size(); i++) {
            peopleData.addAll(Rooms.get(i).getPeopleData( routes));
        }
        for (Person p:peopleInRoom) {
            p.distanceToExit = 0;
            peopleData.add(p);
            for (Door d:doors) {
                p.availableDoors.add(d);
                for (Routes r:routes) {
                    r.resetVisited();
                    Vector<Node> path =  r.ShortestPathToGoal(d.node,r.getGoal());
                    if(Path.hasGoal(path)) {
//                        Routes.printPath(path,p);
                        double tempD = Routes.pathHeuristic(path, p);
                        if (tempD < p.distanceToExit) {
                            p.distanceToExit = tempD;
                        }
                    }
                }
            }
        }
        return peopleData;
    }

    protected static class Corner{
        double x;
        double z;

        Corner(double[] coords){
            x = coords[0];
            z = coords[1];
        }
        Corner(double _x,double _z){
            x = _x;
            z = _z;
        }
        public double[] getCoords(){
            double [] temp = {x,z};
            return temp;
        }

    }
}
