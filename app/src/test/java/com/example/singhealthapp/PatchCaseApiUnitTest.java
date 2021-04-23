package com.example.singhealthapp;

import com.example.singhealthapp.Models.Case;
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
public class PatchCaseApiUnitTest {
    public static String token;
    public Case thisCase;
    public int case_id, expected_response_code;

    // classic constructor
    public PatchCaseApiUnitTest(int expected_response_code, int case_id, Case thisCase) {
        this.case_id = case_id;
        this.thisCase = thisCase;
        this.expected_response_code = expected_response_code;
        token = "fake token";
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        // map result to constructor parameters (each inner list corresponds to the inputs to the constructor)
        return Arrays.asList (new Object [][] {
                {200, 17, new Case()}
        });
    }

    @Test
    public void patchCaseTest() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        String mockTokenJson = "{\"id\":17,\"report_id\":19,\"tenant_id\":71,\"question\":" +
                "\"Shop is open and ready to service patients/visitors according to operating hours." +
                "\",\"is_resolved\":false,\"non_compliance_type\":\"Professional & Staff Hygiene" +
                "\",\"unresolved_photo\":\"19_1\",\"unresolved_comments\":\"\",\"unresolved_date" +
                "\":\"2021-04-22T03:56:09.809000Z\",\"resolved_photo\":\"\",\"resolved_comments\":\"\"," +
                "\"resolved_date\":null,\"rejected_comments\":\"\"}";
        server.enqueue(new MockResponse()
                .addHeader("authorization", "Token")
                .setBody(mockTokenJson)
                .setResponseCode(expected_response_code));
        DatabaseApiCaller mockApiCaller = new Retrofit.Builder()
                .baseUrl(server.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);

        Call<Void> patchCase = mockApiCaller.patchCase("Token "+token, case_id, thisCase);

        int actual_response_code = patchCase.execute().code();
        System.out.println("Actual response code: "+actual_response_code);
        assertEquals(expected_response_code, actual_response_code);
        RecordedRequest request = server.takeRequest();
        System.out.println("Actual header response: "+request.getHeader("authorization"));
        assertEquals("Token fake token", request.getHeader("authorization"));
        System.out.println("Request line: "+request.getRequestLine());
        assertEquals("PATCH /api/case/17/ HTTP/1.1", request.getRequestLine());
    }

}