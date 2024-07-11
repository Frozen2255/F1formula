package com.example.f1formula;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Date;

public class RaceSchedule
{
    private int round;
    private String raceName;
    private String date;
    private String time;
    private String circuit;

    public RaceSchedule()
    {

    }
    public boolean isFutureRace()
    {
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime raceDateTime = LocalDateTime.parse(getDate() + "T" + getTime().substring(0,getTime().length() - 1));
        if (current.isBefore(raceDateTime))
        {
            return true;
        }
        return false;
    }
    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getRaceName() {
        return raceName;
    }

    public void setRaceName(String raceName) {
        this.raceName = raceName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCircuit() {
        return circuit;
    }

    public void setCircuit(String circuit) {
        this.circuit = circuit;
    }

    @Override
    public String toString() {
        return "RaceSchedule{" +
                "round=" + round +
                ", raceName='" + raceName + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", circuit='" + circuit + '\'' +
                '}';
    }
}
