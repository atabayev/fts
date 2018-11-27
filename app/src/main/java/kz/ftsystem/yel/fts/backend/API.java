package kz.ftsystem.yel.fts.backend;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface API {


    @FormUrlEncoded
    @POST("registration/reg/")
    Call<ServerResponse> registration(
            @Field("name") String name,
            @Field("surname") String surname,
            @Field("email") String email,
            @Field("phone") String phone);


    @FormUrlEncoded
    @POST("registration/authentication/")
    Call<ServerResponse> authentication(
            @Field("phone") String phone);


    @FormUrlEncoded
    @POST("registration/isordered/")
    Call<ServerResponse> isOrdered(
            @Field("cid") String phone);

    @FormUrlEncoded
    @POST("registration/get_iao/")
    Call<OrderResponse> getInfoAboutMyOrder(
            @Field("cid") String myId,
            @Field("token") String myToken,
            @Field("oid") String orderId);


    @FormUrlEncoded
    @POST("registration/make_ao/")
    Call<ServerResponse> makeAnOrder(
            @Field("cid") String myId,
            @Field("token") String myToken,
            @Field("oid") String orderId);


    @FormUrlEncoded
    @POST("registration/cancel_ao/")
    Call<ServerResponse> cancelAnOrder(
            @Field("cid") String myId,
            @Field("token") String myToken,
            @Field("oid") String orderId);


    @FormUrlEncoded
    @POST("registration/dyftfo/")
    Call<ServerResponse> findingTranslator(
            @Field("cid") String myId,
            @Field("token") String myToken,
            @Field("oid") String orderId);



    @FormUrlEncoded
    @POST("registration/dytmo/")
    Call<ServerResponse> translatingOrder(
            @Field("cid") String myId,
            @Field("token") String myToken,
            @Field("oid") String orderId);


    @Multipart
    @POST("orders/new/")
    Call<ServerResponse> newOrder(
            @Part("cid") RequestBody myid,
            @Part("language") RequestBody language,
            @Part("pages") RequestBody pages,
            @Part("urgency") RequestBody urgency,
            @Part("token") RequestBody token,
            @Part List<MultipartBody.Part> files);


    @FormUrlEncoded
    @POST("registration/finish_o/")
    Call<ServerResponse> finishOrder(
            @Field("cid") String myId,
            @Field("token") String myToken,
            @Field("oid") String orderId);

}
