package com.try3x.uttam.Retrofit;

import com.try3x.uttam.Models.PayMethodInfo;
import com.try3x.uttam.Models.Paytm.Checksum;
import com.try3x.uttam.Models.Response.BajiInfoResponse;
import com.try3x.uttam.Models.Response.BuyCoinTransResponse;
import com.try3x.uttam.Models.Response.CoinHistoryResponse;
import com.try3x.uttam.Models.Response.CoinPackageResponse;
import com.try3x.uttam.Models.Response.MyCommissionsResponse;
import com.try3x.uttam.Models.Response.PaymentInfoResponse;
import com.try3x.uttam.Models.Response.PaymentMethodResponse;
import com.try3x.uttam.Models.Response.PayoutHistoryResponse;
import com.try3x.uttam.Models.Response.SinglePayMethodResponse;
import com.try3x.uttam.Models.Response.UserPayMethodListResponse;
import com.try3x.uttam.Models.Response.addUserResponse;
import com.try3x.uttam.Models.Response.BajiServerBody;
import com.try3x.uttam.Models.MyBajiList;
import com.try3x.uttam.Models.Response.MyCoinResponse;
import com.try3x.uttam.Models.PackageList;
import com.try3x.uttam.Models.Response.ReferUserResponse;
import com.try3x.uttam.Models.Response.ServerResponse;
import com.try3x.uttam.Models.UserLogin;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IRetrofitApiCall {

    @FormUrlEncoded
    @POST("account.isUserExits.php")
    Call<UserLogin> isUserExits(
            @Field("sha1") String sha1,
            @Field("email") String email,
            @Field("u_id") String uid
    );

    @FormUrlEncoded
    @POST("account.addUser.php")
    Call<addUserResponse> addUser(
            @Field("sha1") String sha1,
            @Field("email") String email,
            @Field("u_id") String uid,
            @Field("name") String name,
            @Field("photo_url") String photo_url,
            @Field("phone") String phone,
            @Field("gender") int gender,
            @Field("pay_method") int pay_method,
            @Field("pay_id") String pay_id,
            @Field("my_refer_link") String my_refer_link,
            @Field("refer_by_uid") String refer_by_uid,
            @Field("isReferBy") boolean isReferBy,
            @Field("refer_by_email") String refer_by_email
    );

    @GET("helper.getPackages.php")
    Call<PackageList> getPackages();


    @POST("baji.placeBaji.php")
    Call<ServerResponse> placeBaji(
           @Body BajiServerBody bajiServerBody
     );

    @FormUrlEncoded
    @POST("baji.getMyBajiList.php")
    Call<MyBajiList> getMyBajiList(
        @Field("sha1") String sha1,
        @Field("email") String email,
        @Field("u_id") String uId,
        @Field("isPaging") boolean isPaging,
        @Field("currPage") int currPage,
        @Field("itemPage") int itemPage

    );

    @FormUrlEncoded
    @POST("coin.bajiClaim.php")
    Call<ServerResponse> claimBaji(
            @Field("sha1") String sha1,
            @Field("email") String email,
            @Field("u_id") String uId,
            @Field("token") String token,
            @Field("baji_id") int id


    );

    @FormUrlEncoded
    @POST("coin.getCoin.php")
    Call<MyCoinResponse> getCoin(
            @Field("sha1") String sha1,
            @Field("email") String email,
            @Field("u_id") String uId,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("coin.getCommission.php")
    Call<MyCoinResponse> getCommission(
            @Field("sha1") String sha1,
            @Field("email") String email,
            @Field("u_id") String uId,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("coin.getWithdrawable.php")
    Call<MyCoinResponse> getWithdrawable(
            @Field("sha1") String sha1,
            @Field("email") String email,
            @Field("u_id") String uId,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("coin.getCoinHistory.php")
    Call<CoinHistoryResponse> getCoinHistory(
            @Field("sha1") String sha1,
            @Field("email") String email,
            @Field("u_id") String uId,
            @Field("token") String token,
            @Field("isPaging") boolean isPaging,
            @Field("currPage") int currPage,
            @Field("itemPage") int itemPage
    );


    @FormUrlEncoded
    @POST("coin.getWithrawbleHistory.php")
    Call<CoinHistoryResponse> getWithrawbleHistory(
            @Field("sha1") String sha1,
            @Field("email") String email,
            @Field("u_id") String uId,
            @Field("token") String token,
            @Field("isPaging") boolean isPaging,
            @Field("currPage") int currPage,
            @Field("itemPage") int itemPage
    );


    @FormUrlEncoded
    @POST("coin.getCommissions.php")
    Call<MyCommissionsResponse> getCommissions(
            @Field("sha1") String sha1,
            @Field("email") String email,
            @Field("u_id") String uId,
            @Field("token") String token,
            @Field("isPaging") boolean isPaging,
            @Field("currPage") int currPage,
            @Field("itemPage") int itemPage
    );
    @FormUrlEncoded
    @POST("account.getReferUser.php")
    Call<ReferUserResponse> getReferUser(
            @Field("sha1") String sha1,
            @Field("u_id") String uId
    );

    @FormUrlEncoded
    @POST("baji.getMyBajiInfo.php")
    Call<BajiInfoResponse> getMyBajiInfo(
            @Field("sha1") String sha1,
            @Field("email") String email,
            @Field("u_id") String uId,
            @Field("token") String token
    );


    @FormUrlEncoded
    @POST("coin.getCoinPack.php")
    Call<CoinPackageResponse> getCoinPack(
            @Field("sha1") String sha1
    );

    @FormUrlEncoded
    @POST("money.addBuyCoinTransaction.php")
    Call<BuyCoinTransResponse> getBuyCoinTransRef(
            @Field("sha1") String sha1,
            @Field("email") String email,
            @Field("u_id") String uId,
            @Field("token") String token,
            @Field("amount") float amount,
            @Field("coin_pack_id") int coin_pack_id,
            @Field("coin") int coin
    );

    @FormUrlEncoded
    @POST("setting.getPaymentInfo.php")
    Call<PaymentInfoResponse> getPaymentInfo(
            @Field("sha1") String sha1,
            @Field("email") String email,
            @Field("u_id") String uId,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("money.addBuyCoin.php")
    Call<ServerResponse> addBuyCoin(
            @Field("sha1") String sha1,
            @Field("email") String email,
            @Field("u_id") String uId,
            @Field("token") String token,
            @Field("ref") String ref,
            @Field("price") float amount
    );

    @FormUrlEncoded
    @POST("paytm_checksum/generateChecksum.php")
    Call<Checksum> getChecksum(
            @Field("MID") String mId,
            @Field("ORDER_ID") String orderId
    );

    @POST("payment.getPaymentMethod.php")
    Call<PaymentMethodResponse> getPaymentMethod();

    @FormUrlEncoded
    @POST("payment.getMyPayMethodById.php")
    Call<SinglePayMethodResponse> getMyPayMethodById(
            @Field("sha1") String sha1,
            @Field("email") String email,
            @Field("u_id") String uId,
            @Field("token") String token,
            @Field("method_id") int method_id

    );

    @FormUrlEncoded
    @POST("payment.addPayMethod.php")
    Call<ServerResponse> addPayMethod(
            @Field("sha1") String sha1,
            @Field("email") String email,
            @Field("u_id") String uId,
            @Field("token") String token,
            @Field("pay_method_id") int pay_method_id,
            @Field("pay_number") String pay_number

    );

    @FormUrlEncoded
    @POST("payment.getPayMethodList.php")
    Call<UserPayMethodListResponse> getPayMethodList(
            @Field("sha1") String sha1,
            @Field("email") String email,
            @Field("u_id") String uId,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("payment.paymentRequest.php")
    Call<ServerResponse> paymentRequest(
            @Field("sha1") String sha1,
            @Field("email") String email,
            @Field("u_id") String uId,
            @Field("token") String token,
            @Field("method_id") int method_id,
            @Field("coin") int coin,
            @Field("rupee") int rupee
    );

    @FormUrlEncoded
    @POST("payment.getPayoutList.php")
    Call<PayoutHistoryResponse> getPayoutList(
            @Field("sha1") String sha1,
            @Field("email") String email,
            @Field("u_id") String uId,
            @Field("token") String token,
            @Field("isPaging") boolean isPaging,
            @Field("currPage") int currPage,
            @Field("itemPage") int itemPage
    );
}
