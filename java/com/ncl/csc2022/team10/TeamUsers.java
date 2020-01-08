/**
 * Class Description    : A class which hold team members and a leader of its team
 * Contributors         : Eko Manggaprouw
 */
package com.ncl.csc2022.team10;

import java.util.List;

public class TeamUsers {
    public Team team;
    public List<Employee> employeeList;
    public Employee teamLeader;

    /**
     * Constructor for teamUsers
     * @param team Team object
     * @param employeeList List with employees
     * @param teamLeader Leader of the team(employee object)
     */
    public TeamUsers(Team team, List<Employee> employeeList, Employee teamLeader) {
        this.team = team;
        this.employeeList = employeeList;
        this.teamLeader = teamLeader;
    }
}
