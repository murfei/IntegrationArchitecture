package org.example;

import java.util.HashMap;
import java.util.List;

public class SocialPerformanceRecord {
    private int year;
    private double totalBonus;
    private HashMap<String, HashMap<String, Integer>> performanceRecord;

    public SocialPerformanceRecord(int year, List<Integer> l) {
        this.year = year;
        String[] categories = {"Leadership Competence", "Openness to Employees", "Social Behaviour to Employees",
                "Attitude towards Clients", "Communication Skills", "Integrity to Company", "Average"};
        performanceRecord = new HashMap<>();
        int sumSup = 0;
        int sumPeer = 0;
        for (int i = 0; i < 12; i += 2) {
            HashMap tmp = new HashMap<String, Integer>();
            tmp.put("Supervisor", l.get(i));
            sumSup += l.get(i);
            tmp.put("Peer", l.get(i + 1));
            sumPeer += l.get(i + 1);
            tmp.put("Bonus", 0);
            performanceRecord.put(categories[i / 2], tmp);
        }
        HashMap tmp = new HashMap<String, Integer>();
        tmp.put("Supervisor", sumSup / (categories.length - 2));
        tmp.put("Peer", sumPeer / (categories.length - 2));
        performanceRecord.put(categories[6], tmp);
        totalBonus = 0;

    }

    public void setBonus(String category, Integer bonus) {
        totalBonus -= performanceRecord.get(category).get("Bonus");
        performanceRecord.get(category).put("Bonus", bonus);
        totalBonus += bonus;
    }

    public double getTotalBonus() {
        return totalBonus;
    }

    public HashMap<String, HashMap<String, Integer>> getPerformanceRecords() {
        return performanceRecord;
    }

    public int getYear() {
        return year;
    }
}
