package application.model;

import java.util.*;

public class Group {

    private String name;
    private Set<Member> members = new HashSet<>();

    public Group(String name, HashSet<Member> members) {
        this.name = name;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Member> getMembers() {
        return members;
    }

    public void setMembers(Set<Member> members) {
        this.members = members;
    }
}
