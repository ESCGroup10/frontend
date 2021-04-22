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
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class PatchCaseUnitTest {

    public static String token;
    public static int case_id;
    public static Case thisCase = new Case();
    public String question, non_compliance_type, unresolved_photo, unresolved_comments, unresolved_date, rejected_comments, resolved_photo,
            resolved_date, resolved_comments;
    public int report_id, tenant_id, expected_response_code;
    public boolean is_resolved;
    public static DatabaseApiCaller apiCaller = new Retrofit.Builder()
            .baseUrl("https://esc10-303807.et.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DatabaseApiCaller.class);

    // classic constructor
    public PatchCaseUnitTest(int expected_response_code, int report_id, int tenant_id, String question, boolean is_resolved,
                             String non_compliance_type, String unresolved_photo, String unresolved_comments,
                             String unresolved_date, String resolved_photo, String resolved_date,
                             String resolved_comments, String rejected_comments) {
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
        this.resolved_photo = resolved_photo;
        this.resolved_date = resolved_date;
        this.resolved_comments = resolved_comments;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        // map result to constructor parameters (each inner list corresponds to the inputs to the constructor)
        return Arrays.asList (new Object [][] {
                // base case (no change from original report)
                {200, 5, 3, "Label caloric count of healthier options.", false, "Food Hygiene", "5_1", "comments", "2021-04-12T18:42:36.204000Z",
                        "", null, "", ""},
                // change fields - pass
                {200, 51, 3, "Label caloric count of healthier options.", false, "Food Hygiene", "5_1", "comments", "2021-04-12T18:42:36.204000Z",
                        "", null, "", ""},
                {200, 5, 13, "Label caloric count of healthier options.", false, "Food Hygiene", "5_1", "comments", "2021-04-12T18:42:36.204000Z",
                        "", null, "", ""},
                {200, 5, 3, "Label caloric count of headfsvsdlthier options.", false, "Food Hygiene", "5_1", "comments", "2021-04-12T18:42:36.204000Z",
                        "", null, "", ""},
                {200, 5, 3, "Label caloric count of healthier options.", true, "Food Hygiene", "5_1", "comments", "2021-04-12T18:42:36.204000Z",
                        "", null, "", ""},
                {200, 5, 3, "Label caloric count of healthier options.", false, "Food Hygiene", "5_dfvsdz1", "comments", "2021-04-12T18:42:36.204000Z",
                        "", null, "", ""},
                {200, 5, 3, "Label caloric count of healthier options.", false, "Food Hygiene", "5_1", "commedfvsdfnts", "2021-04-12T18:42:36.204000Z",
                        "", null, "", ""},
                {200, 5, 3, "Label caloric count of healthier options.", false, "Food Hygiene", "5_1", "comments", "2021-04-12T18:42:36.204000Z",
                        "afvadfc as", null, "", ""},
                {200, 5, 3, "Label caloric count of healthier options.", false, "Food Hygiene", "5_1", "comments", "2021-04-12T18:42:36.204000Z",
                        "", null, "avfsadevde", ""},
                {200, 5, 3, "Label caloric count of healthier options.", false, "Food Hygiene", "5_1", "comments", "2021-04-12T18:42:36.204000Z",
                        "", null, "", "asvaervaed"},
                // change unresolved date to invalid date format - fail
                {400, 5, 3, "Label caloric count of healthier options.", false, "Food Hygiene", "5_1", "comments", "2021-04-12T18:42dsfvzsadcfv:36.204000Z",
                        "", null, "", ""},
                // invalid non compliance type - fail
                {400, 5, 3, "Label caloric count of healthier options.", false, "Food arvsfdvHygiene", "5_1", "comments", "2021-04-12T18:42:36.204000Z",
                        "", null, "", ""},
                // set resolved date wrong format - fail
                {400, 5, 3, "Label caloric count of healthier options.", false, "Food Hygiene", "5_1", "comments", "2021-04-12T18:42:36.204000Z",
                        "", "sebgabgfaev", "", ""},
                // set resolved date correct format - pass
                {200, 5, 3, "Label caloric count of healthier options.", false, "Food Hygiene", "5_1", "comments", "2021-04-12T18:42:36.204000Z",
                        "", "2021-04-16T18:42:36.204000Z", "", ""}
        });
    }
    @BeforeClass
    public static void getToken() throws InterruptedException, IOException {
        ApiHelper apiHelper = new ApiHelper();
        apiHelper.getToken(apiCaller, "auditor@test.com", "1234");
        sleep(1000);
        synchronized (apiHelper.lock) {
            while (ApiHelper.token == null) {
                apiHelper.lock.wait();
            }
        }
        token = ApiHelper.token;

        // make report
        Call<Case> testCall = apiCaller.postCase("Token " + token, 5, 3,
                "Label caloric count of healthier options.", 0, "Food Hygiene",
                "5_1", "comments", "2021-04-12T18:42:36.204000Z", "");
        testCall.enqueue(new Callback<Case>() {
            @Override
            public void onResponse(Call<Case> call, Response<Case> response) {
                synchronized (apiHelper.lock) {
                    case_id = response.body().getId();
                    apiHelper.lock.notifyAll();
                }
            }

            @Override
            public void onFailure(Call<Case> call, Throwable t) {
                synchronized (apiHelper.lock) {
                    apiHelper.lock.notifyAll();
                }
            }
        });
        sleep(1000);
        synchronized (apiHelper.lock) {
            while (ApiHelper.token == null) {
                apiHelper.lock.wait();
            }
        }
    }

    @Test
    public void patchCaseTest() throws IOException {
        // create report to patch with
        thisCase.setReport_id(report_id);
        thisCase.setTenant_id(tenant_id);
        thisCase.setQuestion(question);
        thisCase.setIs_resolved(is_resolved);
        thisCase.setNon_compliance_type(non_compliance_type);
        thisCase.setUnresolved_photo(unresolved_photo);
        thisCase.setUnresolved_comments(unresolved_comments);
        thisCase.setUnresolved_date(unresolved_date);
        thisCase.setResolved_photo(resolved_photo);
        thisCase.setResolved_date(resolved_date);
        thisCase.setResolved_comments(resolved_comments);
        thisCase.setRejected_comments(rejected_comments);

        Call<Void> patchCase = apiCaller.patchCase("Token "+token, case_id, thisCase);
        assertEquals(expected_response_code, patchCase.execute().code());
    }
}