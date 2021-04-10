package com.example.singhealthapp;

import com.example.singhealthapp.Models.Case;
import com.example.singhealthapp.Models.DatabaseApiCaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import okhttp3.ResponseBody;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Header;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class DatabaseApiPostCaseTest {
    public String token, question, non_compliance_type, unresolved_photo, unresolved_comments;
    public int report_id, expected_response_code;
    public boolean is_resolved;

    // classic constructor
    public DatabaseApiPostCaseTest(int expected_response_code, int report_id, String question, boolean is_resolved,
                                   String non_compliance_type, String unresolved_photo, String unresolved_comments) {
        this.report_id = report_id;
        this.question = question;
        this.is_resolved = is_resolved;
        this.non_compliance_type = non_compliance_type;
        this.unresolved_photo = unresolved_photo;
        this.unresolved_comments = unresolved_comments;
        this.expected_response_code = expected_response_code;
        this.token = "fakeToken";
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        // map result to constructor parameters (each inner list corresponds to the inputs to the constructor)
        return Arrays.asList (new Object [][] {
                // correct base version
                {200, 123, "question", false, "Professional & Staff Hygiene", "50_1", "This table is dirty."},
                // invalid date formats
                {405, 123, "question", false, "Professional & Staff Hygiene", "50_1", "This table is dirty."},
                // invalid non-compliance type
                {405, 123, "question", false, "Professionalll & Staff Hygiene", "50_1", "This table is dirty."}
        });
    }

    @Test
    public void mockPostCaseTest() throws IOException, InterruptedException { // test if interface works

        MockWebServer server = new MockWebServer();

        String mockTokenJson = "{\"id\":123,\"report_id\":50,\"question\":\"question\",\"is_resolved\":false," +
                "\"non_compliance_type\":Professional & Staff Hygiene\"\",\"unresolved_photo\":\"50_1\"," +
                "\"unresolved_comments\":\"This table is dirty.\",\"unresolved_date\":\"2021-03-23T12:19:19.038028Z\"," +
                "\"resolved_photo\":\"\",\"resolved_comments\":\"\",\"resolved_date\":null}";
        server.enqueue(new MockResponse()
                .addHeader("authorization", "Token")
                .setBody(mockTokenJson)
                .setResponseCode(expected_response_code));

        DatabaseApiCaller mockApiCaller = new Retrofit.Builder()
                .baseUrl(server.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);

        Call<Case> testCall = mockApiCaller.postCase("Token "+token, report_id,  2, "question", 0, "non_compliance type",
                unresolved_photo, unresolved_comments, "");

        int actual_response_code = testCall.execute().code();
        System.out.println("Actual response code: "+actual_response_code);
        assertEquals(expected_response_code, actual_response_code);
        RecordedRequest request = server.takeRequest();
        System.out.println("Actual header response: "+request.getHeader("authorization"));
        assertEquals("Token fakeToken", request.getHeader("authorization"));
        System.out.println("Request line: "+request.getRequestLine());
        assertEquals("POST /api/case/ HTTP/1.1", request.getRequestLine());

        // Shut down the server. Instances cannot be reused.
        server.shutdown();
    }
}