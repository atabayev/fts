package kz.ftsystem.yel.fts.backend.connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ftsystem.yel.fts.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kz.ftsystem.yel.fts.Interfaces.MyCallback;
import kz.ftsystem.yel.fts.backend.database.DB;
import kz.ftsystem.yel.fts.backend.MyConstants;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Backend {

    private Context context;

    private MyCallback myCallback;

    public Backend(Context context_, MyCallback myCallback_) {
        context = context_;
        myCallback = myCallback_;
    }

    public void authentication(String phoneNm) {
        Call<ServerResponse> call = getApi().authentication(phoneNm);
        if (isNetworkOnline()) {
            try {
                call.enqueue(new Callback<ServerResponse>() {

                    @Override
                    public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
//                            myCallback.fromBackend(response.body().getResponse(), response.body().getId());
                            HashMap<String, String> data = new HashMap<>();
                            data.put("response", response.body().getResponse());
                            data.put("id", response.body().getId());
                            data.put("token", response.body().getToken());
                            data.put("status", response.body().getStatus());
                            myCallback.fromBackend(data);
                        } else {
                            Log.d(MyConstants.TAG, "failure response is: " + response.raw().toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                        HashMap<String, String> data = new HashMap<>();
                        data.put("response", "error");
                        myCallback.fromBackend(data);
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.inet_off), Toast.LENGTH_LONG).show();
        }
    }


    public void registration(String name, String surname, String email, String phone) {
        Call<ServerResponse> call = getApi().registration(name, surname, email, phone);
        if (isNetworkOnline()) {
            try {
                call.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            HashMap<String, String> data = new HashMap<>();
                            data.put("response", response.body().getResponse());
                            data.put("id", response.body().getId());
                            data.put("token", response.body().getToken());
                            myCallback.fromBackend(data);
                        } else {
                            Log.d(MyConstants.TAG, "failure response is: " + response.raw().toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                        HashMap<String, String> data = new HashMap<>();
                        data.put("response", "error");
                        myCallback.fromBackend(data);
                        Log.d(MyConstants.TAG, "Error: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.inet_off), Toast.LENGTH_LONG).show();
        }
    }


    public void getIsOrdered(String myId) {
        Call<ServerResponse> call = getApi().isOrdered(myId);
        if (isNetworkOnline()) {
            try {
                call.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            HashMap<String, String> data = new HashMap<>();
                            data.put("response", response.body().getResponse());
                            data.put("id", response.body().getId());
                            myCallback.fromBackend(data);
                        } else {
                            Log.d(MyConstants.TAG, "failure response is: " + response.raw().toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                        Log.d(MyConstants.TAG, "Error: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.inet_off), Toast.LENGTH_LONG).show();
        }
    }


    public void getInfoAboutMyOrder(String myId, String myToken, String orderId) {
        Call<OrderResponse> call = getApi().getInfoAboutMyOrder(myId, myToken, orderId);
        if (isNetworkOnline()) {
            try {
                call.enqueue(new Callback<OrderResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<OrderResponse> call, @NonNull Response<OrderResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            HashMap<String, String> data = new HashMap<>();
                            data.put("response", response.body().getResponse());
                            data.put("orderId", response.body().getOrderId());
                            data.put("language", response.body().getLanguage());
                            data.put("pagesCount", response.body().getPagesCount());
                            data.put("price", response.body().getPrice());
                            data.put("dateEnd", response.body().getDateEnd());
                            data.put("urgency", response.body().getUrgency());
                            myCallback.fromBackend(data);
                        } else {
                            Log.d(MyConstants.TAG, "failure response is: " + response.raw().toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<OrderResponse> call, @NonNull Throwable t) {
                        Log.d(MyConstants.TAG, "Error: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.inet_off), Toast.LENGTH_LONG).show();
        }
    }

    public void makeAnOrder(String myId, String myToken, String orderId) {
        Call<ServerResponse> call = getApi().makeAnOrder(myId, myToken, orderId);
        if (isNetworkOnline()) {
            try {
                call.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            HashMap<String, String> data = new HashMap<>();
                            data.put("response", response.body().getResponse());
                            myCallback.fromBackend(data);
                        } else {
                            Log.d(MyConstants.TAG, "failure response is: " + response.raw().toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                        Log.d(MyConstants.TAG, "Error: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.inet_off), Toast.LENGTH_LONG).show();
        }
    }


    public void cancelOrder(String myId, String myToken, String orderId) {
        Call<ServerResponse> call = getApi().cancelAnOrder(myId, myToken, orderId);
        if (isNetworkOnline()) {
            try {
                call.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            HashMap<String, String> data = new HashMap<>();
                            data.put("response", response.body().getResponse());
                            myCallback.fromBackend(data);
                        } else {
                            Log.d(MyConstants.TAG, "failure response is: " + response.raw().toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                        Log.d(MyConstants.TAG, "Error: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.inet_off), Toast.LENGTH_LONG).show();
        }
    }


    public void doYouFindTranslator(String myId, String myToken, String orderId) {
        Call<ServerResponse> call = getApi().findingTranslator(myId, myToken, orderId);
        if (isNetworkOnline()) {
            try {
                call.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            HashMap<String, String> data = new HashMap<>();
                            data.put("response", response.body().getResponse());
                            myCallback.fromBackend(data);
                        } else {
                            Log.d(MyConstants.TAG, "failure response is: " + response.raw().toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                        Log.d(MyConstants.TAG, "Error: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.inet_off), Toast.LENGTH_LONG).show();
        }
    }


    public void doYouTranslateOrder(String myId, String myToken, String orderId) {
        Call<ServerResponse> call = getApi().translatingOrder(myId, myToken, orderId);
        if (isNetworkOnline()) {
            try {
                call.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            HashMap<String, String> data = new HashMap<>();
                            data.put("response", response.body().getResponse());
                            myCallback.fromBackend(data);
                        } else {
                            Log.d(MyConstants.TAG, "failure response is: " + response.raw().toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                        Log.d(MyConstants.TAG, "Error: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.inet_off), Toast.LENGTH_LONG).show();
        }
    }


    public void finishTheOrder(String myId, String myToken, String orderId, float rating) {
        Call<ServerResponse> call = getApi().finishOrder(myId, myToken, orderId, rating);
        if (isNetworkOnline()) {
            try {
                call.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            HashMap<String, String> data = new HashMap<>();
                            data.put("response", response.body().getResponse());
                            myCallback.fromBackend(data);
                        } else {
                            Log.d(MyConstants.TAG, "failure response is: " + response.raw().toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                        Log.d(MyConstants.TAG, "Error: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.inet_off), Toast.LENGTH_LONG).show();
        }
    }


    public void sendNewFcmToken(String myId, String myToken, String fmcToken) {
        Call<ServerResponse> call = getApi().sendFcmToken(myId, myToken, fmcToken);
        if (isNetworkOnline()) {
            try {
                call.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            HashMap<String, String> data = new HashMap<>();
                            data.put("response", response.body().getResponse());
                            myCallback.fromBackend(data);
                        } else {
                            Log.d(MyConstants.TAG, "failure response is: " + response.raw().toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                        HashMap<String, String> data = new HashMap<>();
                        data.put("response", "failure");
                        myCallback.fromBackend(data);
                        Log.d(MyConstants.TAG, "Error: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.inet_off), Toast.LENGTH_LONG).show();
        }
    }


    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);
    }

//    @NonNull
//    public static MultipartBody.Part prepareFilePart(Context context, String partName, File file) {
//        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
//        RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
//        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
//    }


    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, String filePath) {
        File file = FileUtils.getFile(String.valueOf(filePath));
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(mimeType),
                        file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    public void sendingData(ArrayList<String> imgPaths, String lang, String pageCount) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(MyConstants.BASE_URL) // Адрес сервера
                .addConverterFactory(GsonConverterFactory.create(gson)) // говорим ретрофиту что для сериализации необходимо использовать GSON
                .build();
        API api = retrofit.create(API.class);
        List<MultipartBody.Part> files = new ArrayList<>();
        for (int i = 0; i < imgPaths.size(); i++) {
            MultipartBody.Part body = prepareFilePart("files", imgPaths.get(i));
            files.add(body);
        }
        DB preferences = new DB(context);
        preferences.open();
        RequestBody myId = createPartFromString(preferences.getVariable(MyConstants.MY_ID));
        RequestBody language = createPartFromString(lang);
        RequestBody page_count = createPartFromString(pageCount);
        RequestBody urgency = createPartFromString("3");
        RequestBody token = createPartFromString(preferences.getVariable(MyConstants.MY_TOKEN));
        preferences.close();
        Call<ServerResponse> call = api.newOrder(
                myId,
                language,
                page_count,
                urgency,
                token,
                files);
        try {
            call.enqueue(new Callback<ServerResponse>() {

                @Override
                public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        HashMap<String, String> data = new HashMap<>();
                        data.put("response", response.body().getResponse());
                        data.put("id", response.body().getId());
                        myCallback.fromBackend(data);
                    } else {
                        Log.d(MyConstants.TAG, "failure response is: " + response.raw().toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                    Log.d(MyConstants.TAG, "Failure: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void getPK(String cid, String token) {
        Call<PKResponse> call = getApi().getPK(cid, token);
        if (isNetworkOnline()) {
            call.enqueue(new Callback<PKResponse>() {
                @Override
                public void onResponse(Call<PKResponse> call, Response<PKResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        HashMap<String, String> data = new HashMap<>();
                        data.put("response", response.body().getResponse());
                        myCallback.fromBackend(data);
                    } else {
                        Log.d(MyConstants.TAG, "failure response is: " + response.raw().toString());
                    }
                }

                @Override
                public void onFailure(Call<PKResponse> call, Throwable t) {

                }
            });
        }
    }


    public void sendCryptogram(String cid, String token, String oid, String amount, String ipAddr,
                               String cardCryptogramPacket) {
        Call<ServerResponse> call = getApi().sendPayment(cid, token, oid, amount, ipAddr, cardCryptogramPacket);
        if (isNetworkOnline()) {
            try {
                call.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            HashMap<String, String> data = new HashMap<>();
                            data.put("response", response.body().getResponse());
                            data.put("id", response.body().getId());
                            myCallback.fromBackend(data);

                        } else {
                            Log.d(MyConstants.TAG, "failure response is: " + response.raw().toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                        Log.d(MyConstants.TAG, "Failure: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
//                TODO: Обработка ошибки
                Log.d(MyConstants.TAG, "Error: " + e.getMessage());
            }
        }
    }


    private API getApi() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(API.class);
    }

    public boolean isNetworkOnline() {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;
    }

}

