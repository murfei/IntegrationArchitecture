package org.example;

import com.mongodb.client.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;


public class DBClient implements ManagePersonal {
    //set up DB connection
    String uri = "mongodb+srv://murfei_db_user:k3U2RJi3lUNf93Uo@smarthooverltd.tw0ettl.mongodb.net/";
    MongoClient mongoClient = MongoClients.create(uri);
    public MongoDatabase db = mongoClient.getDatabase("SmartHooverDB");
    MongoCollection<Document> collection = db.getCollection("data");

    /**
     * Adds a given SalesMan object to the DB. Therefore, the method maps the object to a new Document
     * and inserts it into the DB.
     * @param record SalesMan object to be persisted in the DB
     */
    @Override
    public void createSalesMan(SalesMan record) {
        if(record == null) return;
        List<SocialPerformanceRecord> tmp = record.getPerformanceRecords();
        List<Document> docs = new ArrayList<>();
        //converts the SocialPerformanceRecords to Documents and adds them to a List which is later inserted
        //in the SalesMan document
        for (SocialPerformanceRecord sr : tmp) {
            docs.add(convertPerformanceRecord(sr));
        }
        Document document = new Document("firstName", record.getFirstName())
                .append("lastName", record.getLastName())
                .append("nationality", record.getNationality())
                .append("address", record.getAddress())
                .append("employee_id", record.getEmployee_id())
                .append("gender", record.getGender())
                .append("dateOfBirth", record.getDateOfBirth())
                .append("performanceRecords", docs);
        collection.insertOne(document);
    }

    /**
     * Adds a given SocialPerformanceRecord to a Salesman and saves it to the DB
     * @param record SocialPerformanceRecord to be added
     * @param salesMan corresponding Salesman
     * @throws IllegalArgumentException if a record for the year already exists
     */
    @Override
    public void addSocialPerformanceRecord(SocialPerformanceRecord record, SalesMan salesMan) throws IllegalArgumentException {
        if(record == null || salesMan == null) return;
        // Check if a record for this year already exists
        for (SocialPerformanceRecord r : salesMan.getPerformanceRecords()) {
            if (r.getYear() == record.getYear()) {
                throw new IllegalArgumentException(
                        "Performance record for year " + record.getYear() + " already exists for this employee."
                );
            }
        }
        //converts the SocialPerformanceRecord to a document
        Document convRecord = convertPerformanceRecord(record);
        //find the document of the corresponding SalesMan in the DB
        Document doc = collection.find(new Document("employee_id", salesMan.getEmployee_id())).first();
        //adds the SocialPerformanceRecord document to the List of records of the corresponding SalesMan
        collection.updateOne(doc, new Document("$push", new Document("performanceRecords", convRecord)));
        salesMan.addPerformanceRecord(record);
    }

    /**
     * Searches the Salesman of the given employeeID in the DB and gives back a corresponding SalesMan object.
     * Returns
     * @param sid employeeID
     * @return null if no such SalesMan exists, otherwise the SalesMan with the given employeeID
     */
    @Override
    public SalesMan readSalesMan(int sid) {
        Document doc = collection.find(new Document("employee_id", sid)).first();
        if (doc == null) return null;
        return getSalesManFromDoc(doc);
    }

    /**
     * Returns all persisted Salesman
     * @return List of all SalesMan persisted in the DB
     */
    @Override
    public List<SalesMan> readAllSalesMen() {
        List<SalesMan> ret = new ArrayList<>();
        for (Document doc : collection.find()) {
            ret.add(getSalesManFromDoc(doc));
        }
        return ret;
    }

    /**
     * Gives all the SocialPerformanceRecords of a given SalesMan
     * @param salesMan SalesMan who's SocialPerformanceRecords are asked for
     * @return List of SocialPerformanceRecords
     */
    @Override
    public List<SocialPerformanceRecord> readSocialPerformanceRecord(SalesMan salesMan) {
        if (salesMan == null) return null;
        return salesMan.getPerformanceRecords();
    }

    /**
     * Deletes a given SalesMan
     * @param sid employeeID of the SalesMan that is to be deleted
     */
    @Override
    public void deleteSalesMan(int sid) {
        Document doc = collection.find(new Document("employee_id", sid)).first();
        if (doc == null) return;
        collection.deleteOne(doc);
    }

    /**
     * Deletes a SocialPerformanceRecord from a given SalesMan of a given year
     * @param sid employeeID of the SalesMan
     * @param year year of the SocialPerformanceRecord that is to be deleted
     */
    @Override
    public void deleteSocialPerformanceRecord(int sid, int year) {
        //find Salesman
        Document doc = collection.find(new Document("employee_id", sid)).first();
        if (doc == null) return;
        //delete corresponding SocialPerformanceRecord
        collection.updateOne(doc, new Document("$pull", new Document("performanceRecords", new Document("year", year)))
        );
    }

    /**
     * Converts a SocialPerformanceRecord to a document
     * @param record SocialPerformanceRecord to be converted
     * @return Document of the SocialPerformanceRecord
     */
    private Document convertPerformanceRecord(SocialPerformanceRecord record) {
        Document recordDoc = new Document();
        recordDoc.put("year", record.getYear());
        //adds each category to the return document
        record.getPerformanceRecords().forEach((category, scores) -> {
            Document scoresDoc = new Document(scores);
            recordDoc.put(category, scoresDoc);
        });
        recordDoc.put("totalBonus", record.getTotalBonus());
        return recordDoc;
    }

    /**
     * Converts a Document to a SalesMan.
     * @param doc Document to be convertet
     * @return SalesMan object corresponding to the given document
     */
    private SalesMan getSalesManFromDoc(Document doc) {
        List<Document> records = doc.get("performanceRecords", List.class);
        SalesMan salesMan = new SalesMan(doc.getString("firstName"), doc.getString("lastName"),
                doc.getString("nationality"), doc.getString("address"), doc.getInteger("employee_id"), doc.getString("gender"),
                doc.getString("dateOfBirth"));
        //read all the socialPerformanceRecords from the salesman and add them to the salesman object
        for (Document r : records) {
            List<Integer> scores = new java.util.ArrayList<>();
            String[] categories = {"Leadership Competence", "Openness to Employees", "Social Behaviour to Employees",
                    "Attitude towards Clients", "Communication Skills", "Integrity to Company"};
            //set the Supervisor and Peer scores for each category
            for (String cat : categories) {
                Document scoreDoc = (Document) r.get(cat);
                scores.add(scoreDoc.getInteger("Supervisor"));
                scores.add(scoreDoc.getInteger("Peer"));
            }
            int year = r.getInteger("year");
            SocialPerformanceRecord record = new SocialPerformanceRecord(year, scores);
            // set Bonus values for each category
            for (String cat : categories) {
                Document scoreDoc = (Document) r.get(cat);
                record.setBonus(cat, scoreDoc.getInteger("Bonus"));
            }
            salesMan.addPerformanceRecord(record);
        }
        return salesMan;
    }
}
