package com.example.f1formula;

public class DriverStanding {
    private String firstName;
    private String surName;
    private int position;
    private int points;
    private int wins;
    private String nationality;
    private String dateOfBirth;
    private String constructorName;
    public DriverStanding() {
    }

    public DriverStanding(String firstName, String surName, int position, int points, int wins) {
        this.firstName = firstName;
        this.surName = surName;
        this.position = position;
        this.points = points;
        this.wins = wins;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurName() {
        return surName;
    }

    public int getPosition() {
        return position;
    }

    public int getPoints() {
        return points;
    }

    public int getWins() {
        return wins;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getConstructorName() {
        return constructorName;
    }

    public void setConstructorName(String constructorName) {
        this.constructorName = constructorName;
    }

    @Override
    public String toString() {
        return
                "    Firstname: " + firstName + "\n" +
                "    Surname: " + surName + "\n" +
                "    Position: " + position + "\n" +
                "    Points: " + points + "\n" +
                "    Wins: " + wins + "\n" ;
    }
}
