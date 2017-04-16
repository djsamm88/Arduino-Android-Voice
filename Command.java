package com.medantechno.arduinobluetooth;

/**
 * Created by user on 4/14/2017.
 */

public class Command {

    //private variables
    int _id;
    String vName;
    String codenya;

    // Empty constructor
    public Command(){

    }
    // constructor
    public Command(int id, String name, String codenya){
        this._id = id;
        this.vName = name;
        this.codenya = codenya;
    }

    // constructor
    public Command(String name, String codenya){
        this.vName = name;
        this.codenya = codenya;
    }
    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting name
    public String getName(){
        return this.vName;
    }

    // setting name
    public void setName(String name){
        this.vName = name;
    }

    // getting phone number
    public String getCodenya(){
        return this.codenya;
    }

    // setting phone number
    public void setCodenya(String a){
        this.codenya = a;
    }

}
