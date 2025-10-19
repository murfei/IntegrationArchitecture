package org.example;

import java.util.ArrayList;
import java.util.List;

    public class SalesMan {
    private String firstName;
    private String lastName;
    private String nationality;
    private String address;
    private int employee_id;
    private String gender;
    private String dateOfBirth;
    private List<SocialPerformanceRecord> performanceRecords;

        public SalesMan(String firstName, String lastName, int employee_id) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.employee_id = employee_id;
            this.performanceRecords = new ArrayList<>();
        }

        public SalesMan(String firstName, String lastName, String nationality, String address, int employee_id, String gender, String dateOfBirth) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.nationality = nationality;
            this.address = address;
            this.employee_id = employee_id;
            this.gender = gender;
            this.dateOfBirth = dateOfBirth;
            this.performanceRecords = new ArrayList<>();
        }

        public void addPerformanceRecord(SocialPerformanceRecord record) {
            performanceRecords.add(record);
        }

        public List<SocialPerformanceRecord> getPerformanceRecords() {
            return performanceRecords;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getEmployee_id() {
            return employee_id;
        }

        public void setEmployee_id(int employee_id) {
            this.employee_id = employee_id;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }
    }
