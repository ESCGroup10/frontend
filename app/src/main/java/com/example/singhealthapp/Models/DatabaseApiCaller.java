package com.example.singhealthapp.Models;

import com.example.singhealthapp.Views.Auditor.SearchTenant.SearchMain;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DatabaseApiCaller {

    // TEST EXAMPLE: get a list of all users (auditors + tenants)
    @GET("/api/users/")
    Call<List<User>> getUsers(
            @Header("authorization") String token
    );

    // TEST EXAMPLE: post details of new user
    @FormUrlEncoded
    @POST("/api/users/")
    Call<User> postNewUser(
            @Header("authorization") String token,
            @Field("email") String email,
            @Field("password") String password,
            @Field("name") String name,
            @Field("company") String company,
            @Field("location") String location,
            @Field("institution") String institution,
            @Field("type") String type
    );

    // get a single user based on user email
    @GET("/api/singleUser/")
    Call<List<User>> getSingleUser(
            @Header("authorization") String token,
            @Query("email") String email
    );

    // login post request
    @FormUrlEncoded
    @POST("/login/")
    Call<Token> postLogin(
            @Field("username") String email,
            @Field("password") String password
    );

    // post details of new user i.e. add a new tenant/auditor to the database
    @FormUrlEncoded
    @POST("/api/users/")
    Call<User> postUser (
            @Header("authorization") String token,
            @FieldMap Map<String, String> fields
    );

    @GET("/api/previewReport/")
    Call<List<ReportPreview>> getReportPreview (
            @Header("authorization") String token
    );

    @GET("/api/report/")
    Call<List<Report>> getReport (
            @Header("authorization") String token
    );

    @GET("/api/tenants/")
    Call<List<Tenant>> getTenant(
            @Header("authorization") String token
    );
    @GET("/api/tenants/")
    Call<List<SearchMain>> getSearchMain(
            @Header("authorization") String token
    );

    @GET("/api/filterCases/")
    Call<List<Case>> getCasesById (
            @Header("authorization") String token,
            @Query("report_id") int report_id,
            @Query("is_resolved") int is_resolved
    );

    // temp fake request
    @GET("/api/filterCases2/")
    Call<List<Case>> getCaseByCaseId (
            @Header("authorization") String token,
            @Query("case_id") int case_id
    );

    @DELETE("/api/filterCases2/")
    Call<ResponseBody> deleteCaseByCaseId (
            @Header("authorization") String token,
            @Query("case_id") int case_id
    );

//    @FormUrlEncoded
//    @POST("/api/report/")
//    Call<ResponseBody> postNewReport(
//            @Header("authorization") String token,
////            @Field("email") String email,
////            @Field("password") String password,
////            @Field("name") String name,
////            @Field("company") String company,
////            @Field("location") String location,
////            @Field("institution") String institution,
////            @Field("type") String type
//    );

    @FormUrlEncoded
    @POST("/api/case/")
    Call<ResponseBody> postNewCase(
            @Header("authorization") String token,
            @Field("id") int case_id,
            @Field("report_id") int report_id,
            @Field("question") String question,
            @Field("unresolved_photo") String unresolved_photo,
            @Field("unresolved_comments") String unresolved_comments,
            @Field("resolved_photo") String resolved_photo,
            @Field("resolved_comments") String resolved_comments
    );
}
