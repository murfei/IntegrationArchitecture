package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SocialPerformanceRecord {
    private HashMap<String, HashMap<String, Integer>> performanceRecords;
    private double totalBonus;
    private int year;
    private static List<Integer> years;

    public SocialPerformanceRecord(int year, List<Integer> l){
        if(years == null){
            years = new ArrayList<Integer>();
        }
        if(!years.contains(year)) {
            years.add(year);
            this.year = year;
            String[] categories = {"Leadership Competence", "Openness to Employees", "Social Behaviour to Employees",
                    "Attitude towards Clients", "Communication Skills", "Integrity to Company", "Average"};
            performanceRecords = new HashMap<>();
            int sumSup = 0;
            int sumPeer = 0;
            for (int i = 0; i < 12; i += 2) {
                HashMap tmp = new HashMap<String, Integer>();
                tmp.put("Supervisor", l.get(i));
                sumSup += l.get(i);
                tmp.put("Peer", l.get(i + 1));
                sumPeer += l.get(i + 1);
                tmp.put("Bonus", 0);
                performanceRecords.put(categories[i / 2], tmp);
            }
            HashMap tmp = new HashMap<String, Integer>();
            tmp.put("Supervisor", sumSup / (categories.length - 2));
            tmp.put("Peer", sumPeer / (categories.length - 2));
            performanceRecords.put(categories[6], tmp);
            totalBonus = 0;
        } else{
            //TODO- Fehlermeldung wenn Jahr bereits existiert
        }
    }

    public void setBonus(String category, Integer bonus){
        totalBonus -= performanceRecords.get(category).get("Bonus");
        performanceRecords.get(category).put("Bonus", bonus);
        totalBonus += bonus;
    }

    public double getTotalBonus() {
        return totalBonus;
    }

    public HashMap<String, HashMap<String, Integer>> getPerformanceRecords() {
        return performanceRecords;
    }

    public int getYear() {
        return year;
    }
}
