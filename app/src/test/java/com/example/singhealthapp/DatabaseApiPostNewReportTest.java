package com.example.singhealthapp;

import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Report;
import com.example.singhealthapp.Models.Token;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class DatabaseApiPostNewReportTest {
    public String token, company, location, outlet_type, report_notes, resolution_date;
    public int auditor_id, tenant_id, response_code;
    public double staff_hygiene_score, housekeeping_score, safety_score, healthierchoice_score, foodhygiene_score;
    public boolean expected, status;
    public DatabaseApiCaller apiCaller = new Retrofit.Builder()
            .baseUrl("https://esc10-303807.et.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DatabaseApiCaller.class);

    // classic constructor
    public DatabaseApiPostNewReportTest(int response_code, int auditor_id, int tenant_id, String company,
                                        String location, String outlet_type, boolean status, String report_notes, String resolution_date, double staff_hygiene_score,
                                        double housekeeping_score, double safety_score, double healthierchoice_score,
                                        double foodhygiene_score, boolean expected) {
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
        this.response_code = response_code;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        // map result to constructor parameters (each inner list corresponds to the inputs to the constructor)
        return Arrays.asList (new Object [][] {
                // base correct case
                {200, 123, 234, "company", "location", "F&B", false, "notes", "12/04/2021", 0.5, 3.45, 34, 0, 1.2, true},
                // more than 2dp - fail
                {200, 123, 234, "company", "location", "F&B", false, "notes", "12/04/2021", 0.5, 3.45, 34, 0, 1.222, false},
                // more than 10 digits - fail
                {200, 123, 234, "company", "location", "F&B", false, "notes", "12/04/2021", 0.5, 3.45, 34, 0, 10000000.111, false},
                // negative score - pass (frontend code should prevent this)
                {200, 123, 234, "company", "location", "F&B", false, "notes", "12/04/2021", 0.5, 3.45, 34, 0, -1.2, true},
                // invalid date format -fail
                {200, 123, 234, "company", "location", "F&B", false, "notes", "123", 0.5, 3.45, 34, 0, 1.2, false},
                // invalid date (future) -fail
                {200, 123, 234, "company", "location", "F&B", false, "notes", "12/04/2051", 0.5, 3.45, 34, 0, 1.2, false},
                // invalid date format (swap month and day) -fail
                {200, 123, 234, "company", "location", "F&B", false, "notes", "04/12/2021", 0.5, 3.45, 34, 0, 1.2, false},
        });
    }

    @Test
    public void postNewReportTest() throws IOException {
        Call<Token> authCall = apiCaller.postLogin("auditor@test.com", "1234");
        authCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.code() == 200) { // response code is valid
                    token = response.body().getToken();
                    Call<Report> testCall = apiCaller.postNewReport("Token "+token, auditor_id, tenant_id, company, location, outlet_type, status, report_notes,
                            resolution_date, staff_hygiene_score, housekeeping_score, safety_score, healthierchoice_score, foodhygiene_score);
                    if (expected) {
                        try {
                            assertEquals(response_code, call.execute().code());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            assertNotEquals(response_code, call.execute().code());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    fail();
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                System.out.println(t);
            }
        });
    }
}