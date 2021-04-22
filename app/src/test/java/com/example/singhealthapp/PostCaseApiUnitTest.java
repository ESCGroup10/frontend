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
public class PostCaseApiUnitTest {
    public String token, question, non_compliance_type, unresolved_photo, unresolved_comments, unresolved_date, rejected_comments;
    public int report_id, tenant_id, expected_response_code, is_resolved;

    // classic constructor
    public PostCaseApiUnitTest(int expected_response_code, int report_id, int tenant_id, String question, int is_resolved,
                               String non_compliance_type, String unresolved_photo, String unresolved_comments,
                               String rejected_comments, String unresolved_date) {
        this.report_id = report_id;
        this.tenant_id = tenant_id;
        this.question = question;
        this.is_resolved = is_resolved;
        this.non_compliance_type = non_compliance_type;
        this.unresolved_photo = unresolved_photo;
        this.unresolved_comments = unresolved_comments;
        this.expected_response_code = expected_response_code;
        this.rejected_comments = rejected_comments;
        this.unresolved_date = unresolved_date;
        this.token = "fakeToken";
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        // map result to constructor parameters (each inner list corresponds to the inputs to the constructor)
        return Arrays.asList (new Object [][] {
                // correct base version = [expected response code, report ID, question, isResolved, non-compliance type, unresolved photo name,
                // unresolved comments, unresolved date, rejected comments]
                {200, 5, 3, "Label caloric count of healthier options.", 0, "Food Hygiene", "5_1", "comments", "2021-04-12T18:42:36.204000Z", ""},
        });
    }

    @Test
    public void mockPostCaseTest() throws IOException, InterruptedException { // test if interface works

        MockWebServer server = new MockWebServer();

        String mockTokenJson = "{\"id\":2,\"report_id\":5,\"tenant_id\":3,\"question\":\"Label caloric count of healthier options.\"," +
                "\"is_resolved\":false,\"non_compliance_type\":\"Food Hygiene\",\"unresolved_photo\":\"5_1\",\"unresolved_comments\":\"HOW\"," +
                "\"unresolved_date\":\"2021-04-12T18:42:36.204000Z\",\"resolved_photo\":\"\",\"resolved_comments\":\"\",\"resolved_date\":null," +
                "\"rejected_comments\":\"\"}";


        server.enqueue(new MockResponse()
                .addHeader("authorization", "Token")
                .setBody(mockTokenJson)
                .setResponseCode(expected_response_code));

        DatabaseApiCaller mockApiCaller = new Retrofit.Builder()
                .baseUrl(server.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);

        Call<Case> testCall = mockApiCaller.postCase("Token "+token, report_id,  tenant_id, question, 0, non_compliance_type,
                unresolved_photo, unresolved_comments, unresolved_date, rejected_comments);

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