package com.example.singhealthapp;

import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Report;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class DatabaseApiPostNewReportTest {
    public String token, company, location, outlet_type, report_notes, resolution_date;
    public int auditor_id, tenant_id, expected_response_code;
    public double staff_hygiene_score, housekeeping_score, safety_score, healthierchoice_score, foodhygiene_score;
    public boolean status;

    // classic constructor
    public DatabaseApiPostNewReportTest(int expected_response_code, int auditor_id, int tenant_id, String company,
                                        String location, String outlet_type, boolean status, String report_notes, String resolution_date, double staff_hygiene_score,
                                        double housekeeping_score, double safety_score, double healthierchoice_score,
                                        double foodhygiene_score) {
        this.auditor_id = auditor_id;
        this.tenant_id = tenant_id;
        this.company = company;
        this.location = location;
        this.outlet_type = outlet_type;
        this.status = status;
        this.report_notes = report_notes;
        this.resolution_date = resolution_date;
        this.staff_hygiene_score = staff_hygiene_score;
        this.housekeeping_score = housekeeping_score;
        this.safety_score = safety_score;
        this.healthierchoice_score = healthierchoice_score;
        this.foodhygiene_score = foodhygiene_score;
        this.expected_response_code = expected_response_code;
        this.token = "fakeToken";
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        // map result to constructor parameters (each inner list corresponds to the inputs to the constructor)
        return Arrays.asList (new Object [][] {
                // base correct case
                {200, 123, 234, "company", "location", "F&B", false, "notes", "12/04/2021", 0.5, 3.45, 34, 0, 1.2},
                // more than 2dp - fail
                {405, 123, 234, "company", "location", "F&B", false, "notes", "12/04/2021", 0.5, 3.45, 34, 0, 1.222},
                // more than 10 digits - fail
                {405, 123, 234, "company", "location", "F&B", false, "notes", "12/04/2021", 0.5, 3.45, 34, 0, 10000000.111},
                // negative score - pass (frontend code should prevent this)
                {405, 123, 234, "company", "location", "F&B", false, "notes", "12/04/2021", 0.5, 3.45, 34, 0, -1.2},
                // invalid date format -fail
                {405, 123, 234, "company", "location", "F&B", false, "notes", "123", 0.5, 3.45, 34, 0, 1.2},
                // invalid date (future) -fail
                {405, 123, 234, "company", "location", "F&B", false, "notes", "12/04/2051", 0.5, 3.45, 34, 0, 1.2},
                // invalid date format (swap month and day) -fail
                {405, 123, 234, "company", "location", "F&B", false, "notes", "04/12/2021", 0.5, 3.45, 34, 0, 1.2},
        });
    }

    @Test
    public void postNewReportTest() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        String mockTokenJson = "{\"id\":127,\"auditor_id\":123,\"tenant_id\":234,\"company\":\"fakeCompany\"," +
                "\"location\":\"fakeLocation\",\"outlet_type\":\"F&B\",\"status\":false,\"report_notes\":\"fakeNotes\"," +
                "\"report_date\":\"2021-04-03T13:48:42.339440Z\",\"resolution_date\":\"\",\"staffhygiene_score\":\"1.00\"," +
                "\"housekeeping_score\":\"2.00\",\"safety_score\":\"3.00\",\"healthierchoice_score\":\"4.50\"," +
                "\"foodhygiene_score\":\"6.78\"}";
        server.enqueue(new MockResponse()
                .addHeader("authorization", "Token")
                .setBody(mockTokenJson)
                .setResponseCode(expected_response_code));
        DatabaseApiCaller mockApiCaller = new Retrofit.Builder()
                .baseUrl(server.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);

        Call<Report> testCall = mockApiCaller.postNewReport("Token "+token, auditor_id, tenant_id, company,
                location, outlet_type, status, report_notes, resolution_date, staff_hygiene_score, housekeeping_score,
                safety_score, healthierchoice_score, foodhygiene_score);

        int actual_response_code = testCall.execute().code();
        System.out.println("Actual response code: "+actual_response_code);
        assertEquals(expected_response_code, actual_response_code);
        RecordedRequest request = server.takeRequest();
        System.out.println("Actual header response: "+request.getHeader("authorization"));
        assertEquals("Token fakeToken", request.getHeader("authorization"));
        System.out.println("Request line: "+request.getRequestLine());
        assertEquals("POST /api/report/ HTTP/1.1", request.getRequestLine());
    }

}