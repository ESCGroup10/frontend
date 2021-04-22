package com.example.singhealthapp;

import com.example.singhealthapp.HelperClasses.ApiHelper;
import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Report;

import org.junit.Before;
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
public class PatchReportUnitTest {

    public static String token;
    public static int report_id;
    public Report report = new Report();
    public String company, location, institution, outlet_type, report_notes, report_date, resolution_date;
    public int auditor_id, tenant_id, expected_response_code;
    public float staff_hygiene_score, housekeeping_score, safety_score, healthier_choice_score, food_hygiene_score;
    public boolean status;
    public static DatabaseApiCaller apiCaller = new Retrofit.Builder()
            .baseUrl("https://esc10-303807.et.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DatabaseApiCaller.class);

    // classic constructor
    public PatchReportUnitTest(int expected_response_code, int auditor_id, int tenant_id, String company,
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
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        // map result to constructor parameters (each inner list corresponds to the inputs to the constructor)
        return Arrays.asList (new Object [][] {
                // base case (no change from original report)
                {200, 20, 1, "company", "location", "institution", "F&B", true, "report_notes", "2021-04-14T08:26:54.466668Z", "", 80f, 90f,
                        100f, 100f, 100f},
                // changing mutable fields - pass
                {200, 21, 1, "company", "location", "institution", "F&B", true, "sup", "2021-04-14T08:26:54.466668Z", "", 80f, 90f,
                        100f, 100f, 100f},
                {200, 20, 2, "companzdsfvy", "location", "institution", "F&B", true, "niga", "2021-04-14T08:26:54.466668Z", "", 80f, 90f,
                        100f, 100f, 100f},
                {200, 20, 1, "company", "locatdfgzdfion", "institution", "F&B", true, "report_notes", "2021-04-14T08:26:54.466668Z", "", 80f, 90f,
                        100f, 100f, 100f},
                {200, 20, 1, "company", "location", "insdrgrzdtitution", "F&B", true, "report_notes", "2021-04-14T08:26:54.466668Z", "", 80f, 90f,
                        100f, 100f, 100f},
                {200, 20, 1, "company", "location", "institution", "F&drfgsB", false, "report_notes", "2021-04-14T08:26:54.466668Z", "", 80f, 90f,
                        100f, 100f, 100f},
                {200, 20, 1, "company", "location", "institution", "F&B", true, "reposefrt_notes", "2021-04-14T08:26:54.466668Z", "", 80f, 90f,
                        100f, 100f, 100f},
                {200, 20, 1, "company", "location", "institution", "F&B", true, "report_notes", "2021-04-14T08:26:54.466668Z", "xrfhrdf", 80f, 90f,
                        100f, 100f, 100f},
                // changing date, wrong format - fail
                {400, 20, 1, "company", "location", "institution", "F&B", true, "report_notes", "2021-04-14T08:26dzrg:54.466668Z", "", 80f, 90f,
                        100f, 100f, 100f},
                // score > 2dp - fail
                {400, 20, 1, "company", "location", "institution", "F&B", true, "report_notes", "2021-04-14T08:26:54.466668Z", "", 80f, 90f,
                        100f, 100f, 90.333f},
                // score > 10 digits - fail
                {400, 20, 1, "company", "location", "institution", "F&B", true, "report_notes", "2021-04-14T08:26:54.466668Z", "", 80f, 90f,
                        100f, 100f, 90.333124356789f},
                // score using int instead of float - pass
                {200, 20, 1, "company", "location", "institution", "F&B", true, "report_notes", "2021-04-14T08:26:54.466668Z", "", 80f, 90f,
                        100f, 100f, 90},
                // negative score - pass (prevent in code)
                {200, 20, 1, "company", "location", "institution", "F&B", true, "report_notes", "2021-04-14T08:26:54.466668Z", "", 80f, 90f,
                        100f, 100f, -90f},
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
        Call<Report> testCall = apiCaller.postNewReport("Token "+token, 20, 1, "company",
                "location", "institution", "F&B", false, "report_notes",
                "2021-04-14T08:26:54.466668Z", "", 80f, 90f,
                100f, 100f, 100f);
            testCall.enqueue(new Callback<Report>() {
                @Override
                public void onResponse(Call<Report> call, Response<Report> response) {
                    synchronized (apiHelper.lock) {
                        report_id = response.body().getId();
                        apiHelper.lock.notifyAll();
                    }
                }

                @Override
                public void onFailure(Call<Report> call, Throwable t) {
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
    public void patchReportTest() throws IOException {
        // create report to patch with
        report.setAuditor_id(auditor_id);
        report.setTenant_id(tenant_id);
        report.setCompany(company);
        report.setInstitution(institution);
        report.setOutlet_type(outlet_type);
        report.setStatus(status);
        report.setReport_notes(report_notes);
        report.setReport_date(report_date);
        report.setResolution_date(resolution_date);
        report.setStaffhygiene_score(staff_hygiene_score);
        report.setHousekeeping_score(housekeeping_score);
        report.setSafety_score(safety_score);
        report.setHealthierchoice_score(healthier_choice_score);
        report.setFoodhygiene_score(food_hygiene_score);

        Call<Void> patchReport = apiCaller.patchReport("Token "+token, report_id, report);
        assertEquals(expected_response_code, patchReport.execute().code());
    }
}