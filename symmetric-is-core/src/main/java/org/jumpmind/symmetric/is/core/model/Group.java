package org.jumpmind.symmetric.is.core.model;

import java.util.List;

public class Group extends AbstractObject {

    private static final long serialVersionUID = 1L;

    String name;
    
    List<GroupPrivilege> groupPrivileges;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<GroupPrivilege> getGroupPrivileges() {
        return groupPrivileges;
    }

    public void setGroupPrivileges(List<GroupPrivilege> groupPrivileges) {
        this.groupPrivileges = groupPrivileges;
    }

}