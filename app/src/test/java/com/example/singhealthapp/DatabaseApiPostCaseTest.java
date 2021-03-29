package com.example.singhealthapp;

import com.example.singhealthapp.Models.DatabaseApiCaller;
import com.example.singhealthapp.Models.Token;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class DatabaseApiPostCaseTest {
    public String token, question, non_compliance_type, unresolved_photo, unresolved_comments, resolved_photo,
            resolved_comments, resolved_date;
    public int report_id, response_code;
    public boolean expected, is_resolved;
//    public DatabaseApiCaller apiCaller = new Retrofit.Builder()
//            .baseUrl("https://esc10-303807.et.r.appspot.com/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(DatabaseApiCaller.class);

    // classic constructor
    public DatabaseApiPostCaseTest(int response_code, int report_id, String question, boolean is_resolved,
                                   String non_compliance_type, String unresolved_photo, String unresolved_comments, boolean expected) {
        this.report_id = report_id;
        this.question = question;
        this.is_resolved = is_resolved;
        this.non_compliance_type = non_compliance_type;
        this.unresolved_photo = unresolved_photo;
        this.unresolved_comments = unresolved_comments;
        this.response_code = response_code;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        // map result to constructor parameters (each inner list corresponds to the inputs to the constructor)
        return Arrays.asList (new Object [][] {
                // correct base version
                {200, 123, "question", false, "Professionalism", "photo", "comments", true},
                // invalid date formats
                {405, 123, "question", false, "Professionalism", "photo", "comments", true},
                // invalid non-compliance type
                {405, 123, "question", false, "Professionalismm", "photo", "comments", true}
        });
    }

    @Test
    public void mockPostCaseTest() throws IOException { // test if interface works

        MockWebServer server = new MockWebServer();

        String mockTokenJson = "{\"id\":123,\"report_id\":50,\"question\":\"Status of main table\",\"is_resolved\":false," +
                "\"non_compliance_type\":Professional & Staff Hygiene\"\",\"unresolved_photo\":\"50_1\"," +
                "\"unresolved_comments\":\"This table is dirty.\",\"unresolved_date\":\"2021-03-23T12:19:19.038028Z\"," +
                "\"resolved_photo\":\"\",\"resolved_comments\":\"\",\"resolved_date\":null}";
        server.enqueue(new MockResponse().setBody(mockTokenJson));

        DatabaseApiCaller mockApiCaller = new Retrofit.Builder()
                .baseUrl(server.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DatabaseApiCaller.class);

        Call<ResponseBody> testCall = mockApiCaller.postCase("Token "+token, report_id,  question, is_resolved, non_compliance_type,
                unresolved_photo, unresolved_comments);

        int response_code = testCall.execute().code();
        assertEquals(200, response_code);

        // Shut down the server. Instances cannot be reused.
        server.shutdown();
    }

    String print;

//    @Test
//    public void postCaseTest() throws InterruptedException {
//        Call<Token> authCall = apiCaller.postLogin("auditor@test.com", "1234");
//        authCall.enqueue(new Callback<Token>() {
//            @Override
//            public void onResponse(Call<Token> call, Response<Token> response) {
//                if (response.code() == 200) { // response code is valid
//                    token = response.body().getToken();
//                    print="1";
//                    Call<ResponseBody> testCall = apiCaller.postCase("Token "+token, report_id,  question, is_resolved, non_compliance_type,
//                            unresolved_photo, unresolved_comments);
//                    print="2";
//
//                    if (expected) {
//                        try {
//                            print="3";
//                            assertEquals(response_code, testCall.execute().code());
//                        } catch (IOException e) {
//                            print="unable to execute";
//                        }
//                    } else {
//                        try {
////                            print="12";
////                            int actual = testCall.execute().code();
////                            Thread.sleep(1000);
////                            print="22";
////                            print=""+response_code+""+actual;
//                            assertNotEquals(response_code, testCall.execute().code());
//                        } catch (IOException e) {
//                            print="unable to execute";
//                        }
//                    }
//                } else {
//                    fail();
//                    print="error getting token: "+response.code();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Token> call, Throwable t) {
//                print="6";
//                System.out.println(t);
//            }
//        });
//        Thread.sleep(1000);
//        System.out.println(print);
//    }
}