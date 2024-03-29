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
public class PostReportApiUnitTest {
    public String token, company, location, institution, outlet_type, report_notes, report_date, resolution_date;
    public int auditor_id, tenant_id, expected_response_code;
    public float staff_hygiene_score, housekeeping_score, safety_score, healthier_choice_score, food_hygiene_score;
    public boolean status;

    // classic constructor
    public PostReportApiUnitTest(int expected_response_code, int auditor_id, int tenant_id, String company,
                                 String institution, String location, String outlet_type, boolean status,
                                 String report_notes, String report_date, String resolution_date, float staff_hygiene_score,
                                 float housekeeping_score, float safety_score, float healthierchoice_score,
                                 float foodhygiene_score) {
        this.auditor_id = auditor_id;
        this.tenant_id = tenant_id;
        this.company = company;
        this.location = location;
        this.institution = institution;
        this.outlet_type = outlet_type;
        this.status = status;
        this.report_notes = report_notes;
        this.report_date = report_date;
        this.resolution_date = resolution_date;
        this.staff_hygiene_score = staff_hygiene_score;
        this.housekeeping_score = housekeeping_score;
        this.safety_score = safety_score;
        this.healthier_choice_score = healthierchoice_score;
        this.food_hygiene_score = foodhygiene_score;
        this.expected_response_code = expected_response_code;
        this.token = "fakeToken";
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        // map result to constructor parameters (each inner list corresponds to the inputs to the constructor)
        return Arrays.asList (new Object [][] {
                // [expected response code, auditor ID, tenant ID, company, location, institution, outlet type,
                // resolved status, overall report notes, resolution date, staff_hygiene_score, housekeeping_score, safety_score,
                // healthier_choice_score, food_hygiene_score]

                // base correct case
                {200, 20, 22, "Fred's Bookstore", "SGH", "Blk 6 Lvl 1", "Non F&B", false, "notes", "2021-04-14T08:45:18.386910Z", "",
                        0.2f, 0.37f, 0.4f, -1.00f, -1.00f},
        });
    }

    @Test
    public void postReportTest() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        String mockTokenJson = "{\n" +
                "        \"id\": 1430,\n" +
                "        \"auditor_id\": 20,\n" +
                "        \"tenant_id\": 22,\n" +
                "        \"company\": \"Fred's Bookstore\",\n" +
                "        \"institution\": \"SGH\",\n" +
                "        \"location\": \"Blk 6 Lvl 1\",\n" +
                "        \"outlet_type\": \"Non F&B\",\n" +
                "        \"status\": false,\n" +
                "        \"report_notes\": \"\",\n" +
                "        \"report_date\": \"2021-04-14T08:45:18.386910Z\",\n" +
                "        \"resolution_date\": \"\",\n" +
                "        \"staffhygiene_score\": \"20\",\n" +
                "        \"housekeeping_score\": \"37\",\n" +
                "        \"safety_score\": \"40\",\n" +
                "        \"healthierchoice_score\": \"-1.00\",\n" +
                "        \"foodhygiene_score\": \"-1.00\"\n" +
                "    }";
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
                location, institution, outlet_type, status, report_notes, report_date, resolution_date, staff_hygiene_score, housekeeping_score,
                safety_score, healthier_choice_score, food_hygiene_score);

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