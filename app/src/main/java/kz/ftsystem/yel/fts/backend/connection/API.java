package kz.ftsystem.yel.fts.backend.connection;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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
            @Part("comment") RequestBody pages,
            @Part("urgency") RequestBody urgency,
            @Part("token") RequestBody token,
            @Part List<MultipartBody.Part> files);


    @FormUrlEncoded
    @POST("registration/finish_o/")
    Call<ServerResponse> finishOrder(
            @Field("cid") String myId,
            @Field("token") String myToken,
            @Field("oid") String orderId,
            @Field("rating") float rating);


    @FormUrlEncoded
    @POST("registration/set_fcm_token/")
    Call<ServerResponse> sendFcmToken(
            @Field("cid") String myId,
            @Field("token") String myToken,
            @Field("fcm_token") String fcmToken);


    @FormUrlEncoded
    @POST("payment/paying/")
    Call<PayResponse> paying(
            @Field("amount") String amount,
            @Field("currency") String currency,
            @Field("ipAddress") String ipAddress,
            @Field("name") String name,
            @Field("cardCryptogramPacket") String cardCryptogramPacket,
            @Field("accountId") String accountId,
            @Field("token") String token,
            @Field("invoiceId") String invoiceId,
            @Field("email") String email);

    @FormUrlEncoded
    @POST("registration/pay_phys/")
    Call<ServerResponse> payPhysycal(
            @Field("cid") String myId,
            @Field("token") String myToken,
            @Field("oid") String orderId);

    @FormUrlEncoded
    @POST("registration/pay_by_order/")
    Call<ServerResponse> payByOrder(
            @Field("cid") String myId,
            @Field("token") String myToken,
            @Field("oid") String orderId);
}
