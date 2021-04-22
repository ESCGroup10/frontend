package com.example.singhealthapp;

import com.example.singhealthapp.HelperClasses.ApiHelper;
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
public class PostReportUnitTest {

    public static String token;
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
    public PostReportUnitTest(int expected_response_code, int auditor_id, int tenant_id, String company,
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
                // Non F&B
                {201, 20, 22, "Fred's Bookstore", "SGH", "Blk 6 Lvl 1", "Non F&B", false, "notes", "2021-04-14T08:45:18.386910Z", "",
                        90f, 98f, 100f, -1.00f, -1.00f},
                // more than 2dp - fail
                {400, 20, 22, "Fred's Bookstore", "SGH", "Blk 6 Lvl 1", "Non F&B", false, "notes", "2021-04-14T08:45:18.386910Z", "",
                        90f, 98f, 90.987f, -1.00f, -1.00f},
                // more than 10 digits - fail
                {400, 20, 22, "Fred's Bookstore", "SGH", "Blk 6 Lvl 1", "Non F&B", false, "notes", "2021-04-14T08:45:18.386910Z", "",
                        90f, 98f, 1001234321234f, -1.00f, -1.00f},
                // negative score - fail (frontend code should prevent this)
                {201, 20, 22, "Fred's Bookstore", "SGH", "Blk 6 Lvl 1", "Non F&B", false, "notes", "2021-04-14T08:45:18.386910Z", "",
                        90f, 98f, -100f, -1.00f, -1.00f},
                // invalid date format -fail
                {400, 20, 22, "Fred's Bookstore", "SGH", "Blk 6 Lvl 1", "Non F&B", false, "notes", "2021-04-14T08:45:61.386910Z", "",
                        90f, 98f, 100f, -1.00f, -1.00f},
                {400, 20, 22, "Fred's Bookstore", "SGH", "Blk 6 Lvl 1", "Non F&B", false, "notes", "2021-04-14T08:61:18.386910Z", "",
                        90f, 98f, 100f, -1.00f, -1.00f},
                {400, 20, 22, "Fred's Bookstore", "SGH", "Blk 6 Lvl 1", "Non F&B", false, "notes", "2021-04-14T25:45:18.386910Z", "",
                        90f, 98f, 100f, -1.00f, -1.00f},
                {400, 20, 22, "Fred's Bookstore", "SGH", "Blk 6 Lvl 1", "Non F&B", false, "notes", "2021-04-40T08:45:18.386910Z", "",
                        90f, 98f, 100f, -1.00f, -1.00f},
                {400, 20, 22, "Fred's Bookstore", "SGH", "Blk 6 Lvl 1", "Non F&B", false, "notes", "2021-13-14T08:45:18.386910Z", "",
                        90f, 98f, 100f, -1.00f, -1.00f},
                {400, 20, 22, "Fred's Bookstore", "SGH", "Blk 6 Lvl 1", "Non F&B", false, "notes", "2021_04-14T08:45:18.386910Z", "",
                        90f, 98f, 100f, -1.00f, -1.00f},
                // F&B
                // base correct case
                {201, 20, 28, "Joey Bakery", "SGH", "Blk 4 Lvl 1", "F&B", false, "notes", "2021-04-14T08:26:54.466668Z", "", 100f, 100f, 90f, 97f, 33f},
                // more than 2dp - fail
                {400, 20, 28, "Joey Bakery", "SGH", "Blk 4 Lvl 1", "F&B", false, "notes", "2021-04-14T08:26:54.466668Z", "", 100f, 100f, 90f, 97f, 33.333f},
                // more than 10 digits - fail
                {400, 20, 28, "Joey Bakery", "SGH", "Blk 4 Lvl 1", "F&B", false, "notes", "2021-04-14T08:26:54.466668Z", "", 100f, 100f, 90f, 97f, 331234567891f},
                // negative score - pass (frontend code should prevent this)
                {201, 20, 28, "Joey Bakery", "SGH", "Blk 4 Lvl 1", "F&B", false, "notes", "2021-04-14T08:26:54.466668Z", "", 100f, 100f, 90f, 97f, -33f},
                // invalid date format -fail
                {400, 20, 28, "Joey Bakery", "SGH", "Blk 4 Lvl 1", "F&B", false, "notes", "2021-04-14T08:26:99.466668Z", "", 100f, 100f, 90f, 97f, 33f},
                {400, 20, 28, "Joey Bakery", "SGH", "Blk 4 Lvl 1", "F&B", false, "notes", "2021-04-14T08:61:54.466668Z", "", 100f, 100f, 90f, 97f, 33f},
                {400, 20, 28, "Joey Bakery", "SGH", "Blk 4 Lvl 1", "F&B", false, "notes", "2021-04-14T25:26:54.466668Z", "", 100f, 100f, 90f, 97f, 33f},
                {400, 20, 28, "Joey Bakery", "SGH", "Blk 4 Lvl 1", "F&B", false, "notes", "2021-04-40T08:26:54.466668Z", "", 100f, 100f, 90f, 97f, 33f},
                {400, 20, 28, "Joey Bakery", "SGH", "Blk 4 Lvl 1", "F&B", false, "notes", "2021-13-14T08:26:54.466668Z", "", 100f, 100f, 90f, 97f, 33f},
                {400, 20, 28, "Joey Bakery", "SGH", "Blk 4 Lvl 1", "F&B", false, "notes", "2021_04-14T08:26:54.466668Z", "", 100f, 100f, 90f, 97f, 33f}
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
        Call<Report> testCall = apiCaller.postNewReport("Token "+token, auditor_id, tenant_id, company,
                location, institution, outlet_type, status, report_notes, report_date, resolution_date, staff_hygiene_score, housekeeping_score,
                safety_score, healthier_choice_score, food_hygiene_score);
        assertEquals(expected_response_code, testCall.execute().code());
    }
}