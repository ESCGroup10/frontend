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
public class DatabaseApiPostCaseTest {
    public String token, question, non_compliance_type, unresolved_photo, unresolved_comments, unresolved_date, resolved_photo,
            resolved_comments, resolved_date;
    public int report_id, response_code;
    public boolean expected, is_resolved;
    public DatabaseApiCaller apiCaller = new Retrofit.Builder()
            .baseUrl("https://esc10-303807.et.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DatabaseApiCaller.class);

    // classic constructor
    public DatabaseApiPostCaseTest(int response_code, String token, int report_id, String question, boolean is_resolved,
                                   String non_compliance_type, String unresolved_photo, String unresolved_comments,
                                   String unresolved_date, String resolved_photo, String resolved_comments,
                                   String resolved_date, boolean expected) {
        this.token = token;
        this.report_id = report_id;
        this.question = question;
        this.is_resolved = is_resolved;
        this.non_compliance_type = non_compliance_type;
        this.unresolved_photo = unresolved_photo;
        this.unresolved_comments = unresolved_comments;
        this.unresolved_date = unresolved_date;
        this.resolved_photo = resolved_photo;
        this.resolved_comments = resolved_comments;
        this.resolved_date = resolved_date;
        this.response_code = response_code;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        // map result to constructor parameters (each inner list corresponds to the inputs to the constructor)
        return Arrays.asList (new Object [][] {
                // TODO: should token have letters etc?
                {200, "Token 1234", 123, "question", false, "Professionalism", "photo", "comments", "12/04/2021", "photo", "comments", "12/05/2021", true},
        });
    }

    @Test
    public void postCaseTest() throws IOException {
        Call<ResponseBody> call = apiCaller.postCase(token, report_id,  question, is_resolved, non_compliance_type,
                unresolved_photo, unresolved_comments, unresolved_date, resolved_photo, resolved_comments, resolved_date);
        if (expected) {
            assertEquals(response_code, call.execute().code());
        } else {
            assertNotEquals(response_code, call.execute().code());
        }
    }
}