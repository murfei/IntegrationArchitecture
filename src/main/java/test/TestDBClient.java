package test;
import org.example.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.*;
import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.*;


import java.util.Arrays;
import java.util.List;

public class TestDBClient {

    private static DBClient dbClient;
    private static MongoDatabase db;
    private static MongoCollection<Document> collection;
    private static SalesMan testSalesman;
    private static SalesMan testSalesman2;

    @BeforeAll
    static void setupAll() {
        dbClient = new DBClient();
        db = dbClient.db;
        collection = db.getCollection("data");

        testSalesman = new SalesMan("John", "Doe", "German", "Street 1", 9999, "Male", "1980-01-01");
        List<Integer> perfScores = Arrays.asList(5,4,4,3,3,5,5,4,4,5,5,5);
        SocialPerformanceRecord record = new SocialPerformanceRecord(2025, perfScores);
        testSalesman.addPerformanceRecord(record);

        testSalesman2 = new SalesMan("Max", "Mustermann", "German", "Street 2", 1, "Male", "1980-01-01");
        testSalesman2.addPerformanceRecord(record);
    }

    @AfterEach
    void cleanup() {
        collection.deleteOne(eq("employee_id", 9999));
        collection.deleteOne(eq("employee_id", 1));
    }

    @Test
    @Order(1)
    public void testCreateSalesMan() {
        dbClient.createSalesMan(testSalesman);
        Document found = collection.find(eq("employee_id", 9999)).first();

        assertNotNull(found);
        assertEquals("John", found.getString("firstName"));
        assertEquals("Doe", found.getString("lastName"));
        assertEquals(9999, found.getInteger("employee_id"));
    }

    @Test
    @Order(2)
    public void testReadSalesMan() {
        dbClient.createSalesMan(testSalesman);
        SalesMan result = dbClient.readSalesMan(9999);
        //positive test
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals(9999, result.getEmployee_id());
        //negative test
        assertNull(dbClient.readSalesMan(0));
    }

    @Test
    @Order(3)
    public void testAddSocialPerformanceRecord() {
        //positive test
        dbClient.createSalesMan(testSalesman);

        List<Integer> scores = Arrays.asList(3,3,4,4,5,5,2,2,3,3,4,4);
        SocialPerformanceRecord newRecord = new SocialPerformanceRecord(2024, scores);
        dbClient.addSocialPerformanceRecord(newRecord, testSalesman);

        Document updated = collection.find(eq("employee_id", 9999)).first();
        List<Document> perfRecords = (List<Document>) updated.get("performanceRecords");

        assertEquals(2, perfRecords.size());
        assertTrue(perfRecords.stream().anyMatch(r -> r.getInteger("year") == 2024));
        //negative test
        //trying to add a not existing record --> number of records should stay the same
        dbClient.addSocialPerformanceRecord(null, testSalesman);
        updated = collection.find(eq("employee_id", 9999)).first();
        perfRecords = (List<Document>) updated.get("performanceRecords");
        assertEquals(2, perfRecords.size());
        //trying to add a already existing year
        try  {
            dbClient.addSocialPerformanceRecord(newRecord, testSalesman);
            assert false;
        } catch (IllegalArgumentException e) {
            assert true;
        }
    }

    @Test
    @Order(4)
    void testDeleteSocialPerformanceRecord() {
        dbClient.createSalesMan(testSalesman);
        List<Integer> scores = Arrays.asList(3,3,4,4,5,5,2,2,3,3,4,4);
        SocialPerformanceRecord record2019 = new SocialPerformanceRecord(2019, scores);
        dbClient.addSocialPerformanceRecord(record2019, testSalesman);
        //negative test --> trying to delete a record which not exists
        Document updated = collection.find(eq("employee_id", 9999)).first();
        int recordsbefore = ((List<Document>) updated.get("performanceRecords")).size();
        dbClient.deleteSocialPerformanceRecord(9999, 2000);
        updated = collection.find(eq("employee_id", 9999)).first();
        List<Document> perfRecords = (List<Document>) updated.get("performanceRecords");
        assertEquals(recordsbefore, perfRecords.size());
        //positive test
        dbClient.deleteSocialPerformanceRecord(9999, 2019);
        updated = collection.find(eq("employee_id", 9999)).first();
        perfRecords = (List<Document>) updated.get("performanceRecords");
        assertTrue(perfRecords.stream().noneMatch(r -> r.getInteger("year") == 2019));
    }

    @Test
    @Order(5)
    void testDeleteSalesMan() {
        dbClient.createSalesMan(testSalesman);
        List<SalesMan> salesMen = dbClient.readAllSalesMen();
        int lengthbefore = salesMen.size();
        //neagtive test --> trying to delete a not existing employeeID
        dbClient.deleteSalesMan(1);
        salesMen = dbClient.readAllSalesMen();
        assertEquals(lengthbefore, salesMen.size());
        //positive test
        dbClient.deleteSalesMan(9999);
        Document deleted = collection.find(eq("employee_id", 9999)).first();
        assertNull(deleted);
    }

    @Test
    @Order(6)
    void testReadAllSalesMen() {
        List<SalesMan> allPrev = dbClient.readAllSalesMen();
        int prevSize = allPrev.size();
        dbClient.createSalesMan(testSalesman);
        dbClient.createSalesMan(testSalesman2);
        List<SalesMan> all = dbClient.readAllSalesMen();
        assertEquals(all.size(), prevSize + 2);
        assertTrue(all.stream().anyMatch(s -> s.getEmployee_id() == 9999));
        assertTrue(all.stream().anyMatch(s -> s.getEmployee_id() == 1));
    }

}
