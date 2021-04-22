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

@RunWith(Parameterized.class)
public class PatchReportApiUnitTest {
    public static String token;
    public Report report;
    public int auditor_id, expected_response_code;

    // classic constructor
    public PatchReportApiUnitTest(int expected_response_code, int auditor_id, Report report) {
        this.auditor_id = auditor_id;
        this.report = report;
        this.expected_response_code = expected_response_code;
        token = "fake token";
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        // map result to constructor parameters (each inner list corresponds to the inputs to the constructor)
        return Arrays.asList (new Object [][] {
                {200, 20, new Report()}
        });
    }

    @Test
    public void patchReportTest() throws IOException, InterruptedException {
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

        Call<Void> patchReport = mockApiCaller.patchReport("Token "+token, auditor_id, report);

        int actual_response_code = patchReport.execute().code();
        System.out.println("Actual response code: "+actual_response_code);
        assertEquals(expected_response_code, actual_response_code);
        RecordedRequest request = server.takeRequest();
        System.out.println("Actual header response: "+request.getHeader("authorization"));
        assertEquals("Token fake token", request.getHeader("authorization"));
        System.out.println("Request line: "+request.getRequestLine());
        assertEquals("PATCH /api/report/20/ HTTP/1.1", request.getRequestLine());
    }

}