package application.model;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.*;

import java.io.FileReader;

import java.io.FileWriter;

import java.io.ObjectInputStream;

import java.io.ObjectOutputStream;

import application.controller.Main;
import com.thoughtworks.xstream.XStream;

import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.Mapper;

import static java.lang.Integer.parseInt;

public class SystemModel {
    boolean DEBUG = true;

    public Map<String, Member> memberMap; //Mapping username to Member Data Object

    public Map<String, Group> groupMap; // Mapping group names to Group Data Object

    public Map<String, Set<Email>> groupEmailMap;  //Mapping GroupNames to a Set of emails

    public HashSet<Email> Emails; //A set of all emails sent

    public SystemModel(){
        memberMap = new HashMap<>();
        groupMap = new HashMap<>();
        groupEmailMap = new HashMap<>();
        Emails = new HashSet<>();
    }

    public boolean registerMember(String emailAddress, String username, String password, boolean isAdmin, String initialGroup) throws Exception {
        if(DEBUG) System.out.println("Registering Member: " + username);

        //Load members stored in file
        loadMembers();

        //make sure username is not already registered
        if (memberMap.containsKey(username)) {
            if (DEBUG) System.out.println("Username already exists.");
            System.out.println("Registration failed.");
            return false;
        }

        HashSet groupSets =new HashSet<String>();
        groupSets.add(initialGroup);
        Member member = new Member(emailAddress, username, password, isAdmin, groupSets);
        memberMap.put(username, member);

        //creates group if not already exists
        if (!groupMap.containsKey(initialGroup)) {
            HashSet members =new HashSet<Member>();
            members.add(member);
            addGroup(initialGroup, members);
        }

        else if (groupMap.containsKey(initialGroup)) {
            try {
                Set<Member> members = groupMap.get(initialGroup).getMembers();
                members.add(member);
                groupMap.get(initialGroup).setMembers(members);
            }
            catch (NullPointerException e) {
                Set<Member> members = new HashSet<>();
                members.add(member);
                groupMap.get(initialGroup).setMembers(members);
            }
        }
        if(DEBUG)    System.out.println("Member registered successfully.");

        //Save
        saveMembers();
        saveGroups();
        saveGroupMaps();

        //Load
        loadMembers();
        loadGroups();
        loadGroupMaps();

        return true;
    }

    public boolean logInUserMember(String username, String password) {

        //Load members stored in file
        try {
            loadMembers();
        } catch (Exception e) {
            System.out.println("Error loading members.");
            return false;
        }

        if (memberMap.containsKey(username) && memberMap.get(username).getPassword().equals(password)
                && (!memberMap.get(username).isAdmin())) {
            Main.setUser(memberMap.get(username));
            return true;
        }
        else {
            return false;
        }
    }

    public boolean logInAdminMember(String username, String password) {

        //Load members stored in file
        try {
            loadMembers();
        } catch (Exception e) {
            System.out.println("Error loading members.");
            return false;
        }

        if (memberMap.containsKey(username) && memberMap.get(username).getPassword().equals(password)
                && memberMap.get(username).isAdmin()) {
            Main.setUser(memberMap.get(username));
            return true;
        }
        else {
            return false;
        }
    }

    public boolean addGroup(String name, HashSet<Member> members) throws Exception {
        //Load groups stored in file
        loadGroups();
        loadGroupMaps();

        //make sure username is not already registered
        if (groupMap.containsKey(name)) {
            if (DEBUG) System.out.println("Group already exists.");
            return false;
        }

        Group group = new Group(name, members);
        groupMap.put(name, group);
        if(DEBUG)    System.out.println("Group registered successfully.");

        //Save groups to group file
        saveGroups();
        saveGroupMaps();

        loadGroups();
        loadGroupMaps();

        return true;
    }

    public boolean updateGroup(String currentName, String newName) throws Exception {
        //Load groups stored in file
        loadGroups();
        loadGroupMaps();

        groupMap.get(currentName).setName(newName);
        Group group = groupMap.get(currentName);

        groupMap.remove(currentName);
        groupMap.put(newName, group);

        Set<Email> values = groupEmailMap.get(currentName);

        groupEmailMap.remove(currentName);

        groupEmailMap.put(newName, values);

        for (Member member : memberMap.values()) {
            HashSet<String> groups = member.getGroups();
            HashSet<String> updatedGroups = new HashSet<>();
            for (String memberGroup: groups) {
                if (memberGroup.equals(currentName)) {
                        updatedGroups.add(newName);
                }
                else {
                    updatedGroups.add(memberGroup);
                }
            }
            member.setGroups(updatedGroups);
        }

        //Save
        saveGroups();
        saveGroupMaps();

        saveMembers();
        saveEmails();

        System.out.println("Group changes saved.");

        //Load
        loadGroupMaps();
        loadGroups();

        loadMembers();
        loadEmails();

        return true;
    }

    public boolean removeGroup(String group) throws Exception {
        //Load
        loadGroups();
        loadGroupMaps();

        loadEmails();

        groupMap.remove(group);

        groupEmailMap.remove(group);

        for (Member member : memberMap.values()) {
            HashSet<String> groups = member.getGroups();
            String toRemove = "";
            for (String memberGroup: groups) {
                if (memberGroup.equals(group)) {
                    memberGroup = toRemove;
                }
                else {
                }
            }
            groups.remove(toRemove);
        }

        Set<Email> toRemove = new HashSet<>();
        for (Email email : Emails) {
            if (!groupEmailMap.values().contains(email)) {
                toRemove.add(email);
            }
        }
        Emails.remove(toRemove);

        //Save
        saveGroups();
        saveGroupMaps();

        saveMembers();
        saveEmails();

        //Load
        loadGroupMaps();
        loadGroups();

        loadMembers();
        loadEmails();

        System.out.println("Group removed.");

        return true;
    }

    public boolean sendEmail(String username, String title, int priority, String messageBody, String group){
        //Load emails stored in file
        try {
            loadEmails();
        } catch (Exception e) {
            System.out.println("Error loading emails.");
            return false;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY, HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String dateTimeSent = now.format(formatter);

        Email email = new Email(username, title, dateTimeSent, messageBody, priority);
        Emails.add(email);

        try {
            Set<Email> groupEmails = groupEmailMap.get(group);
            groupEmails.add(email);
            groupEmailMap.put(group, groupEmails);
        }
        catch (NullPointerException e) {
            Set<Email> groupEmails = new HashSet<>();
            groupEmails.add(email);
            groupEmailMap.put(group, groupEmails);
        }

        memberMap.get(username).setLastPostedDate(dateTimeSent);


        //Save emails to email file
        try {
            saveEmails();
            saveGroups();
            saveGroupMaps();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            loadEmails();
            loadGroups();
            loadGroupMaps();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean removeUserFromList(Member member, String group) throws Exception {
        //Load
        loadGroups();
        loadGroupMaps();
        loadMembers();

        HashSet<Group> groups= member.getGroups();
        groups.remove(group);
        String memberName = member.getUsername();
        memberMap.remove(member);
        memberMap.put(memberName, member);

        //Save
        saveMembers();
        saveGroups();
        saveGroupMaps();
        return true;
    }

    public boolean removeUser(String user) throws Exception {
        //Load
        loadGroups();
        loadGroupMaps();

        loadEmails();

        memberMap.remove(user);

        //Save
        saveGroups();
        saveGroupMaps();

        saveMembers();
        saveEmails();

        //Load
        loadGroupMaps();
        loadGroups();

        loadMembers();
        loadEmails();

        System.out.println("Group removed.");

        return true;
    }

    /**

     * save() - This method saves the contents of the Members memberMap and bargains Map to

     * an XML file called members.xml

     * */

    public void saveMembers() throws Exception {
        XStream xstream = new XStream(new DomDriver());

        ObjectOutputStream out = xstream.createObjectOutputStream(new FileWriter("members.xml"));

        out.writeObject(memberMap);

        out.close();
    }

    /**
     * load() - This method reads the contents of the XML file called
     * members.xml stored in the project directory.
     * */

    public void loadMembers() throws Exception {

        XStream xstream = new XStream(new DomDriver());
        try {
            ObjectInputStream is = xstream.createObjectInputStream(new FileReader("members.xml"));
            memberMap = (Map<String, Member>) is.readObject();
            is.close();
        } catch (FileNotFoundException e) {
            memberMap = new HashMap<>();
        } catch (Exception e) {
            System.out.println("Error Loading Members");
        }
    }

    /**

     * save() - This method saves the contents of the Groups groupMap and bargains Map to

     * an XML file called groups.xml

     * */

    public void saveGroups() throws Exception {
        XStream xstream = new XStream(new DomDriver());

        ObjectOutputStream out = xstream.createObjectOutputStream(new FileWriter("groups.xml"));

        out.writeObject(groupMap);

        out.close();
    }

    public void saveGroupMaps() throws Exception {
        XStream xstream = new XStream(new DomDriver());

        ObjectOutputStream out = xstream.createObjectOutputStream(new FileWriter("groupMaps.xml"));

        out.writeObject(groupEmailMap);

        out.close();
    }

    /**
     * load() - This method reads the contents of the XML file called
     * groups.xml stored in the project directory.
     * */

    public void loadGroups() throws Exception {

        XStream xstream = new XStream(new DomDriver());
        try {
            ObjectInputStream is = xstream.createObjectInputStream(new FileReader("groups.xml"));
            groupMap = (Map<String, Group>) is.readObject();
            is.close();
        } catch (FileNotFoundException e) {
            groupMap = new HashMap<>();
        } catch (Exception e) {
            System.out.println("Error Loading Groups");
        }
    }

    public void loadGroupMaps() throws Exception {

        XStream xstream = new XStream(new DomDriver());
        try {
            ObjectInputStream is = xstream.createObjectInputStream(new FileReader("groupMaps.xml"));
            groupEmailMap = (Map<String, Set<Email>>) is.readObject();
            is.close();
        } catch (FileNotFoundException e) {
            groupEmailMap = new HashMap<>();
        } catch (Exception e) {
            System.out.println("Error Loading Groups");
        }
    }

    /**

     * save() - This method saves the contents of the Emails set and bargains Set to

     * an XML file called emails.xml

     * */

    public void saveEmails() throws Exception {
        XStream xstream = new XStream(new DomDriver());

        ObjectOutputStream out = xstream.createObjectOutputStream(new FileWriter("emails.xml"));

        out.writeObject(Emails);

        out.close();
    }

    /**
     * load() - This method reads the contents of the XML file called
     * emails.xml stored in the project directory.
     * */

    public void loadEmails() throws Exception {

        XStream xstream = new XStream(new DomDriver());
        try {
            ObjectInputStream is = xstream.createObjectInputStream(new FileReader("emails.xml"));
            Emails = (HashSet<Email>) is.readObject();
            is.close();
        } catch (FileNotFoundException e) {
            Emails = new HashSet<>();
        } catch (Exception e) {
            System.out.println("Error Loading Emails");
        }
    }
}
