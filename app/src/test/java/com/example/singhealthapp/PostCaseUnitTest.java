package com.example.singhealthapp;

import com.example.singhealthapp.HelperClasses.ApiHelper;
import com.example.singhealthapp.Models.Case;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Report;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class PostCaseUnitTest {

    public static String token;
    public String question, non_compliance_type, unresolved_photo, unresolved_comments, unresolved_date, rejected_comments;
    public int report_id, tenant_id, expected_response_code, is_resolved;
    public static DatabaseApiCaller apiCaller = new Retrofit.Builder()
            .baseUrl("https://esc10-303807.et.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DatabaseApiCaller.class);

    // classic constructor
    public PostCaseUnitTest(int expected_response_code, int report_id, int tenant_id, String question, int is_resolved,
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
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        // map result to constructor parameters (each inner list corresponds to the inputs to the constructor)
        return Arrays.asList (new Object [][] {
                {201, 5, 3, "Label caloric count of healthier options.", 0, "Food Hygiene", "5_1", "comments", "2021-04-12T18:42:36.204000Z", ""},
                // invalid non-compliance types
                {400, 5, 3, "Label caloric count of healthier options.", 0, "Food Hygienee", "5_1", "comments", "2021-04-12T18:42:36.204000Z", ""},
                {400, 5, 3, "Label caloric count of healthier options.", 0, "Food  Hygiene", "5_1", "comments", "2021-04-12T18:42:36.204000Z", ""}
        });
    }
    @BeforeClass
    public static void getToken() throws InterruptedException {
        ApiHelper apiHelper = new ApiHelper();
        apiHelper.getToken(apiCaller, "auditor@test.com", "1234");
        Thread.sleep(1000);
        synchronized (apiHelper.lock) {
            while (ApiHelper.token == null) {
                apiHelper.lock.wait();
            }
        }
        token = ApiHelper.token;
    }

    @Test
    public void postReportTest() throws IOException {
        Call<Case> testCall = apiCaller.postCase("Token "+token, report_id,  tenant_id, question, 0, non_compliance_type,
                unresolved_photo, unresolved_comments, unresolved_date, rejected_comments);
        assertEquals(expected_response_code, testCall.execute().code());
    }
}