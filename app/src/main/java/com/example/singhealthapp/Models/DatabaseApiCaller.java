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
import retrofit2.http.Path;
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

    // login post request
    @FormUrlEncoded
    @POST("/login/")
    Call<Token> postLogin(
            @Field("username") String email,
            @Field("password") String password
    );

    // get a single user based on user email
    @GET("/api/singleUser/")
    Call<List<User>> getSingleUser(
            @Header("authorization") String token,
            @Query("email") String email
    );


    // post details of new user i.e. add a new tenant/auditor to the database
    @FormUrlEncoded
    @POST("/api/users/")
    Call<Void> postUser (
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

    @GET("/api/score/")
    Call<List<Report>> getScoresById (
        @Header("authorization") String token,
        @Query("tenant_id") int tenant_id
    );


    @FormUrlEncoded
    @POST("/api/case/")
    Call<Case> postCase (
            @Header("authorization") String token,
            @Field("report_id") int report_id,
            @Field("question") String question,
            @Field("is_resolved") boolean is_resolved,
            @Field("non_compliance_type") String non_compliance_type,
            @Field("unresolved_photo") String unresolved_photo,
            @Field("unresolved_comments") String unresolved_comments
    );

    @FormUrlEncoded
    @POST("/api/report/")
    Call<Report> postNewReport(
            @Header("authorization") String token,
            @Field("auditor_id") int auditor_id,
            @Field("tenant_id") int tenant_id,
            @Field("company") String company,
            @Field("location") String location,
            @Field("outlet_type") String outlet_type,
            @Field("status") boolean status,
            @Field("report_notes") String report_notes,
            @Field("resolution_date") String resolution_date,
            @Field("staff_hygiene_score") double staff_hygiene_score,
            @Field("housekeeping_score") double housekeeping_score,
            @Field("safety_score") double safety_score,
            @Field("healthierchoice_score") double healthierchoice_score,
            @Field("foodhygiene_score") double foodhygiene_score
    );

    @DELETE("/api/users/{id}/")
    Call<Void> deleteUser(
            @Header("authorization") String token,
            @Path("id") int id
    );
    @DELETE("/api/report/{id}/")
    Call<Void> deleteReport(@Header("authorization") String token,
                            @Path("id") int reportID);

    @DELETE("/api/case/{id}/")
    Call<Void> deleteCase(@Header("authorization") String token,
                            @Path("id") int caseID);
}
