package ApiEndpoints;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

/**
 * WebAPI class is used by the '/database' endpoint in the HTTPServer
 * and handles all requests related to the database and basic administration
 * */
public class WebAPI extends API {

    private static boolean verbose = true; //USED for debugging purposes
    private static Database USERDB = new Database();

    /**
     * This function will be used to process the request handed over to the API
     * @param request: Contains the JSON data that was sent to the server
     * @return returns a JSON object with the appropriate response messages for the initial request
     * */
    public static JSONObject handleRequest(JSONObject request)throws Exception {
        //String reqType = (String)req.get("type");
        if(verbose)
            System.out.println("WEBAPI -> "+request.toString());
        JSONObject response;
        switch ((String)request.get("type")){
            case "remove":
            {
                response = remove((String)request.get("email"));
                return response;
            }
            case"login":
            {
                response = login((String)request.get("email"), (String)request.get("pass"));
                return response;
            }
            case "getUsersInBuilding":
            {
                response = getUsersInBuilding((int)request.get("building_id"));
                return response;
            }
            case"update":
            {
                String typeOfUpdate = (String)request.get("typeOfUpdate");
                if(typeOfUpdate.compareTo("userType") == 0)
                {
                    response = updateType((String)request.get("email"), (String)request.get("value"));
                    return response;
                }
                if(typeOfUpdate.compareTo("name") == 0)
                {
                    response = updateName((String)request.get("email"), (String)request.get("value"));
                    return response;
                }
                if(typeOfUpdate.compareTo("deviceID") == 0)
                {
                    response = updateDeviceID((String)request.get("email"), (String)request.get("value"));
                    return response;
                }
                if(typeOfUpdate.compareTo("password") == 0)
                {
                    response = updatePassword((String)request.get("email"), (String)request.get("value"));
                    return response;
                }
                if(typeOfUpdate.compareTo("email") == 0)
                {
                    response = updateEmail((String)request.get("email"), (String)request.get("value"));
                    return response;
                }
                else
                {
                    response = new JSONObject();
                    response.put("status", false);
                    response.put("msg","Error in update");
                    return response;
                }
            }
            case "getUsers":
            {
                response = getUsers();
                return response;
            }
            case "register":
            {
                response =  register((String)request.get("name"), (String)request.get("email"),(String)request.get("pass"),(String)request.get("userType"));
                return response;
            }
            case "getBuildings":
            {
                response = listDir();
                return response;
            }
            case "uploadBuilding":
            {
                response =  uploadBuilding((String)request.get("name"), (String)request.get("file").toString());
                return response;
            }
        }

        throw new Exception("Unsupported Request");
    }
    private static  JSONObject getUsersInBuilding(int building_id)
    {
        JSONObject Response = new JSONObject();
        Response.put("status", true);
        Response.put("msg","Users in building returned");
        Response.put("data", USERDB.getUsersInBuilding(building_id));
        return Response;
    }
    private static JSONObject getUsers()
    {
        JSONObject Response = new JSONObject();
        Response.put("status", true);
        Response.put("msg","Users returned");
        Response.put("data", USERDB.getUsers());
        return Response;
    }
    private static JSONObject updateEmail(String email, String newEmail){
        JSONObject Response = new JSONObject();
        try{
            boolean exist = USERDB.updateName(email, newEmail);
            if(exist){
                Response.put("status", true);
                Response.put("msg","User email successfully updated");
            }else{
//                USERDB.insert(name, password);
                Response.put("status", false);
                Response.put("msg","Could not update user email");

            }
        }catch (Exception e){
            if(verbose)
                System.out.println("CRITICAL - REGISTER FAILED");
        }

        return Response;
    }
    private static JSONObject updatePassword(String email, String password){
        JSONObject Response = new JSONObject();
        try{
            boolean exist = USERDB.updateName(email, password);
            if(exist){
                Response.put("status", true);
                Response.put("msg","password successfully updated");
            }else{
//                USERDB.insert(name, password);
                Response.put("status", false);
                Response.put("msg","Could not update user password");

            }
        }catch (Exception e){
            if(verbose)
                System.out.println("CRITICAL - REGISTER FAILED");
        }

        return Response;
    }
    private static JSONObject updateDeviceID(String email, String deviceID){
        JSONObject Response = new JSONObject();
        try{
            boolean exist = USERDB.updateDeviceID(email, deviceID);
            if(exist){
                Response.put("status", true);
                Response.put("msg","deviceID successfully updated");
            }else{
//                USERDB.insert(name, password);
                Response.put("status", false);
                Response.put("msg","Could not update deviceID");

            }
        }catch (Exception e){
            if(verbose)
                System.out.println("CRITICAL - REGISTER FAILED");
        }

        return Response;
    }
    private static JSONObject updateName(String email, String name){
        JSONObject Response = new JSONObject();
        try{
            boolean exist = USERDB.updateName(email, name);
            if(exist){
                Response.put("status", true);
                Response.put("msg","User name successfully updated");
            }else{
//                USERDB.insert(name, password);
                Response.put("status", false);
                Response.put("msg","Could not update user name");

            }
        }catch (Exception e){
            if(verbose)
                System.out.println("CRITICAL - REGISTER FAILED");
        }

        return Response;
    }
    private static JSONObject updateType(String email, String type){
        JSONObject Response = new JSONObject();
        try{
            boolean exist = USERDB.updateType(email, type);
            if(exist){
                Response.put("status", true);
                Response.put("msg","User type successfully updated");
            }else{

                Response.put("status", false);
                Response.put("msg","Could not update user type");

            }
        }catch (Exception e){
            if(verbose)
                System.out.println("CRITICAL - REGISTER FAILED");
        }

        return Response;
    }
    
    /**
     * This function is used to upload buildings to the server's file system
     * @param name: The name of the building being uploaded
     * @param file: A JSON file that contains all the building data
     * @return returns success or fail depending on outcome
     * */
    private static  JSONObject uploadBuilding(String name, String file)
    {
        File dir = new File("./html"+ "/" + "Buildings/"  +name);
        if (!dir.exists()) {
            if (dir.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }
        String absoluteFilePath =  "./html"+ "/" + "Buildings/"  +name +"/" + name + ".json";
        File f = new File(absoluteFilePath);
        FileOutputStream fop = null;

        try
        {
            if(f.createNewFile()){
                System.out.println(absoluteFilePath+" File Created");
            }else System.out.println("File "+absoluteFilePath+" already exists");
        }
        catch (Exception e)
        {
            System.out.println("Error  " + absoluteFilePath);
        }
        try
        {
            fop = new FileOutputStream(f);
            byte[] contentInBytes = file.getBytes();
            fop.write(contentInBytes);
            fop.flush();
            fop.close();
        }
        catch (Exception e)
        {

        }

        JSONObject Response = new JSONObject();
        Response.put("status", true);
        Response.put("msg","Building successfully uploaded");

        return Response;
    }
    /**
     * This function is used to add a user to the database
     * @param name: The name of the user to be registered
     * @param email: The email of the user to be registered
     * @param password: The password of the user to be registered
     * @param type: The role that will be assigned to the user
     * @return returns success or fail depending on outcome
     * */
    private static JSONObject register(String name, String email, String password, String type){
        JSONObject Response = new JSONObject();
        try{
            boolean exist =  USERDB.insert(name, email,password,type);
            if(exist){
                Response.put("status", false);
                Response.put("msg","User already Exists");
            }else{

                Response.put("status", true);
                Response.put("msg","User Successfully created");

            }
        }catch (Exception e){
            if(verbose)
                System.out.println("CRITICAL - REGISTER FAILED");
        }

        return Response;
    }

    /**
     * This function is used to remove a user from the system
     * @param email: The email of the user to be removed
     * @return returns success or fail depending on outcome
     * */
    private static JSONObject remove(String email) {
        JSONObject Response = new JSONObject();
        boolean exist = USERDB.delete(email);
        if (exist) {
            Response.put("status", true);
            Response.put("msg", "User successfully removed");
        } else {
            Response.put("status", false);
            Response.put("msg", "User does not exist");


        }
        return Response;
    }
    /**
     * This function is used to log a user into the system
     * @param email: The email of the user to be logged in
     * @param password: The password of the user to be logged in
     * @return returns success or fail depending on outcome
     * */
    private static JSONObject login(String email, String password){

        JSONObject Response = new JSONObject();
        try{
            boolean status= USERDB.search(email, password);
            Response.put("status", status);
            if(status)
                Response.put("msg","Login success");
            else
                Response.put("msg","Invalid user/pass");
        }catch(Exception e){
            if(verbose)
                System.out.println("CRITICAL - LOGIN FAILED");
        }
        return Response;
    }

    /**
     * This function is used to show all the buildings stored in the server's file system
     * @return returns a JSON object containing the names of all the buildings
     * */
    private static JSONObject listDir()throws Exception{
        File folder = new File("Buildings/");
        JSONArray buildings = new JSONArray();
        JSONObject response = new JSONObject();
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if(file.isDirectory())
                buildings.put(file.getName());;
        }
        response.put("status",true);
        response.put("msg",buildings);
        return response;
    }
}
