package ApiEndpoints;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.util.Vector;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.sql.*;

public class Database {
    public String fileName;
    File f;
    Lock lock;
    Connection con = null;
    Statement query = null;
    public Database()
    {
        fileName = "../../database.txt";
        f = new File(fileName);
        lock = new ReentrantLock();


        //NEW CODE
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:database.db");
            System.out.println("Connected to DB!!");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        createTable();
    }
    //DATABASE CODE @Kinson
    /**
     * function is currently static and only creates table users if no table users exists
     */
    public void createTable(){
        try{
            query = con.createStatement();
            query.execute("create table if not exists users(id integer primary key, name varchar(250));");
            query = null;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * function can be used to insert new users to the users table
     * @param name: is a string of user name
     */
    public void insert(String name){
        try{
            query = con.createStatement();
            query.execute("insert into users(name) values('"+name+"')");
            query = null;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * function works as a means to create sql queries to the database and will return a bool to determine if query was successful
     * @param sql: is an sql query
     * @return boolean: true - query is successful, false - query failed
     */
    public boolean execute(String sql){
        try{
            query = con.createStatement();
            query.execute(sql);
            query = null;
            return true;
        }catch(Exception e){
            printError(e,sql);
        }
        return  false;
    }

    /**
     * function can be used to make selections to the db
     * @param sql: is a sql selection query
     * @return ResultSet: which holds all the data returned from the select query
     */
    public ResultSet select(String sql){
        try{
            query = con.createStatement();
            ResultSet result = query.executeQuery(sql);
            query = null;
            return result;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    /**
     * function will close the connection to the db
     */
    public void close(){
        try{
            con.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * function prints errors from any query
     * @param e: is the error which was caught
     * @param sql: is the sql which caused the error
     */
    private void printError(Exception e, String sql){
        System.out.println("Error in " + sql + ": " + e.getMessage() );
    }

    //DATABSE CODE
    public String outputFile()
    {
        String line = null;
        String ret = "";
        try {
            FileReader fileReader =
                    new FileReader(f);

            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                ret += line += " - ";
                ret += bufferedReader.readLine();
                ret += "\n\r";
            }
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
        }
        return ret;
    }

    public void write(String name, String pass)
    {
        lock.lock();
        try {

            FileWriter fileWriter =
                    new FileWriter(f, true);
            BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);

            Scanner input = new Scanner(System.in);
            bufferedWriter.write(name+","+pass);
//            bufferedWriter.write(pass);
            bufferedWriter.newLine();
            bufferedWriter.close();
        }
        catch(IOException ex) {
            System.out.println(
                    "Error writing to file '"
                            + fileName + "'");
        }
        lock.unlock();
    }

    /*
    * If ONLY a name is provided it will check if that name exists in the database
    *
    * if a password is provided it will check if that password matches the one in the db
    * */
    public boolean search(String name,String pass)
    {
        boolean found = false;
        String line = null;
        String [] currentLine = new String[2];
        try {
            FileReader fileReader =
                    new FileReader(f);

            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null && !found) {
                currentLine = line.split(",");
//                System.out.println(currentLine[0]);
                if(currentLine[0].equals(name) )
                {
//                    System.out.println("found user");
                    found = true;
                    break;
                }

            }

            bufferedReader.close();
//            System.out.println("Found:"+found);
//            System.out.println("pass match:"+currentLine[1].equals(pass));
            if(pass.equals("") || found == false){//normal search
                return found;
            }else{  // Login Attempt
                if(currentLine[1].equals(pass))
                    return true; // Pass match
                else
                    return false; // Pass failed
            }

        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
        }
        return false;
    }

    public String remove(String name)
    {
        lock.lock();
        Vector<String> tempData = new Vector<String>();
        boolean found = false;
        String line = null;
        String ret = "";
        try {
            FileReader fileReader =
                    new FileReader(f);

            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {

                if(line.equals(name))
                {
                    found = true;
                    bufferedReader.readLine();
                }
                else
                {
                    tempData.add(line);
                }
            }
            if(found)
            {
                FileWriter fileWriter =
                        new FileWriter(f, false);
                fileWriter.close();
                for(int i = 0; i < (tempData.size()- 1) ; i += 2)
                {
                    write(tempData.get(i), tempData.get(i+1));
                }
                ret += "Found and removed ";
                ret += name += "\n\r";
                ret += "\n\t";
            }
            else
            {
                ret += "Name " + name + " not found" + "\n\r";
            }
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
        }
        lock.unlock();
        return ret;
    }
}
