package com.example.singhealthapp;

import com.example.singhealthapp.Models.DatabaseApiCaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(Parameterized.class)
public class DatabaseApiPostNewReportTest {
    public String token, company, location, outlet_type, report_notes, report_date, resolution_date;
    public int auditor_id, tenant_id, response_code;
    public float staff_hygiene_score, housekeeping_score, safety_score, healthierchoice_score, foodhygiene_score;
    public boolean expected, status;
    public DatabaseApiCaller apiCaller = new Retrofit.Builder()
            .baseUrl("https://esc10-303807.et.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DatabaseApiCaller.class);

    // classic constructor
    public DatabaseApiPostNewReportTest(int response_code, String token, int auditor_id, int tenant_id, String company,
                                        String location, String outlet_type, boolean status, String report_notes,
                                        String report_date, String resolution_date, float staff_hygiene_score,
                                        float housekeeping_score, float safety_score, float healthierchoice_score,
                                        float foodhygiene_score, boolean expected) {
        this.token = token;
        this.auditor_id = auditor_id;
        this.tenant_id = tenant_id;
        this.company = company;
        this.location = location;
        this.outlet_type = outlet_type;
        this.status = status;
        this.report_notes = report_notes;
        this.report_date = report_date;
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
                // TODO: should token have letters etc?
                {200, "Token 1234", 123, 234, "company", "location", "F&B", false, "notes", "12/04/2021", null, 0.5, 3.45, 34, 0, 1.2, true},
                {200, "Token 1234", 123, 234, "company", "location", "F&B", false, "notes", "12/04/2021", null, 0.5, 3.45, 34, 0, 1.222, false},
                {200, "Token 1234", 123, 234, "company", "location", "F&B", false, "notes", "12/04/2021", null, 0.5, 3.45, 34, 0, -1.2, true},
                {200, "Token 1234", 123, 234, "company", "location", "F&B", false, "notes", 123, null, 0.5, 3.45, 34, 0, 1.2, false},
                {200, "Token 1234", 123, 234, "company", "location", "F&B", false, 123, "12/04/2021", null, 0.5, 3.45, 34, 0, 1.2, false},
                {200, "Token 1234", 123, 234, "company", "location", "F&B", "false", "notes", "12/04/2021", null, 0.5, 3.45, 34, 0, 1.2, false},
                {200, "1234", 123, 234, "company", "location", "F&B", false, "notes", "12/04/2021", null, 0.5, 3.45, 34, 0, 1.2, false},
                {200, "Token1234", 123, 234, "company", "location", "F&B", false, "notes", "12/04/2021", null, 0.5, 3.45, 34, 0, 1.2, false},
                {200, 1234, 123, 234, "company", "location", "F&B", false, "notes", "12/04/2021", null, 0.5, 3.45, 34, 0, 1.2, false},
                {200, "Token 1234", 123, 234, "company", "location", "F&B", true, "notes", "12/04/2021", null, 0.5, 3.45, 34, 0, 1.2, true},
                {200, "Token 1234", 123, 234, "company", "location", "F&B", false, "notes", "12/04/2021", "", 0.5, 3.45, 34, 0, 1.2, true},
                {200, "Token 1234", 123, 234, "company", "location", "F&B", false, "notes", "12/04/2021", null, null, 3.45, 34, 0, 1.2, false}
        });
    }

    @Test
    public void postNewReportTest() throws IOException {
        Call<ResponseBody> call = apiCaller.postNewReport(token, auditor_id, tenant_id, company, location, outlet_type, status, report_notes,
                report_date, resolution_date, staff_hygiene_score, housekeeping_score, safety_score, healthierchoice_score, foodhygiene_score);
        if (expected) {
            assertEquals(response_code, call.execute().code());
        } else {
            assertNotEquals(response_code, call.execute().code());
        }
    }
}
