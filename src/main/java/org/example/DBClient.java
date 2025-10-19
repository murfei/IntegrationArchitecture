package org.example;

import com.mongodb.client.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;


public class DBClient implements org.hbrs.ia.code.ManagePersonal {
    String uri = "mongodb+srv://murfei_db_user:k3U2RJi3lUNf93Uo@smarthooverltd.tw0ettl.mongodb.net/";
    MongoClient mongoClient = MongoClients.create(uri);
    MongoDatabase db = mongoClient.getDatabase("SmartHooverDB");
    MongoCollection<Document> collection = db.getCollection("data");

    @Override
    public void createSalesMan(SalesMan record) {
        List<SocialPerformanceRecord> tmp = record.getPerformanceRecords();
        List<Document> docs = new ArrayList<>();
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

    @Override
    public void addSocialPerformanceRecord(SocialPerformanceRecord record, SalesMan salesMan) {
        Document convRecord = convertPerformanceRecord(record);
        Document doc = collection.find(new Document("employee_id", salesMan.getEmployee_id())).first();
        collection.updateOne(doc,  new Document("$push", new Document("performanceRecords", convRecord)));
    }

    @Override
    public SalesMan readSalesMan(int sid) {
        Document doc = collection.find(new Document("employee_id", sid)).first();
        return getSalesManFromDoc(doc);
    }

    @Override
    public List<SalesMan> readAllSalesMen() {
        List<SalesMan> ret = new  ArrayList<>();
        for(Document doc : collection.find()) {
            ret.add(getSalesManFromDoc(doc));
        }
        return ret;
    }

    @Override
    public List<SocialPerformanceRecord> readSocialPerformanceRecord(SalesMan salesMan) {
            return salesMan.getPerformanceRecords();
    }

    @Override
    public void deleteSalesMan(int sid) {
        Document doc = collection.find(new Document("employee_id", sid)).first();
        collection.deleteOne(doc);
    }

    @Override
    public void deleteSocialPerformanceRecord(int sid, int year) {
        Document doc = collection.find(new Document("employee_id", sid)).first();
        collection.updateOne(doc, new Document("$pull", new Document("performanceRecords", new Document("year", year)))
        );
    }

    private Document convertPerformanceRecord(SocialPerformanceRecord records) {
        Document recordDoc = new Document();
        recordDoc.put("year", records.getYear());
        records.getPerformanceRecords().forEach((category, scores) -> {
            Document scoresDoc = new Document(scores);
            recordDoc.put(category, scoresDoc);
        });
        recordDoc.put("totalBonus", records.getTotalBonus());
        return recordDoc;
    }

    private SalesMan getSalesManFromDoc(Document doc) {
        List<Document> records = doc.get("performanceRecords", List.class);
        SalesMan salesMan = new SalesMan(doc.getString("firstName"), doc.getString("lastName"),
                doc.getString("nationality"),doc.getString("address"), doc.getInteger("employee_id"), doc.getString("gender"),
                doc.getString("dateOfBirth"));
        for (Document r : records) {
            List<Integer> scores = new java.util.ArrayList<>();
            String[] categories = {"Leadership Competence", "Openness to Employees","Social Behaviour to Employees",
                    "Attitude towards Clients", "Communication Skills", "Integrity to Company"};
            for (String cat : categories) {
                Document scoreDoc = (Document) r.get(cat);
                scores.add(scoreDoc.getInteger("Supervisor"));
                scores.add(scoreDoc.getInteger("Peer"));
            }
            int year = r.getInteger("year");
            SocialPerformanceRecord record = new SocialPerformanceRecord(year, scores);
            // Bonus-Werte setzen
            for (String cat : categories) {
                Document scoreDoc = (Document) r.get(cat);
                record.setBonus(cat, scoreDoc.getInteger("Bonus"));
            }
            salesMan.addPerformanceRecord(record);
        }
        return salesMan;
    }
}
