package com.example.singhealthapp.Views;

import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Token;
import com.example.singhealthapp.Models.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RunWith(Parameterized.class)
public class ApiPostCaseTest2 {
    public String token, question, non_compliance_type, unresolved_photo, unresolved_comments, resolved_photo,
            resolved_comments, resolved_date;
    public int report_id, response_code;
    public boolean expected, is_resolved;

    public ApiPostCaseTest2(int response_code, int report_id, String question, boolean is_resolved,
                                   String non_compliance_type, String unresolved_photo, String unresolved_comments,
                                   String resolved_photo, String resolved_comments,
                                   String resolved_date, boolean expected) {
        this.report_id = report_id;
        this.question = question;
        this.is_resolved = is_resolved;
        this.non_compliance_type = non_compliance_type;
        this.unresolved_photo = unresolved_photo;
        this.unresolved_comments = unresolved_comments;
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
                // correct base version
                {200, 123, "question", false, "Professionalism", "photo", "comments", "photo", "comments", "yesterday", true},
                // invalid date formats
                {405, 123, "question", false, "Professionalism", "photo", "comments", "photo", "comments", "1-2-3", true},
                // invalid non-compliance type
                {405, 123, "question", false, "Professionalismm", "photo", "comments", "photo", "comments", "yesterday", true}
        });
    }

    @Test
    public void mockApiPostCaseTest() {
        MockWebServer server = new MockWebServer();

        String mockTokenJson = "{\"token\": \"127bc352b84eead35cc28340349a8dda9\"}";
        server.enqueue(new MockResponse().setBody(mockTokenJson));

        DatabaseApiCaller mockApiCaller = new Retrofit.Builder()
                .baseUrl(server.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);

        Call<ResponseBody> caseCall = mockApiCaller.postCase(mockTokenJson, report_id,  question, is_resolved, non_compliance_type,
                unresolved_photo, unresolved_comments, resolved_photo, resolved_comments, resolved_date);
    }

}
