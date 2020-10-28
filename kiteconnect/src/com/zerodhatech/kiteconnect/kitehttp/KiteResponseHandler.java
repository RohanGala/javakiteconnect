package src.com.zerodhatech.kiteconnect.kitehttp;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;
import src.com.zerodhatech.kiteconnect.KiteConnect;
import src.com.zerodhatech.kiteconnect.kitehttp.exceptions.DataException;
import src.com.zerodhatech.kiteconnect.kitehttp.exceptions.GeneralException;
import src.com.zerodhatech.kiteconnect.kitehttp.exceptions.InputException;
import src.com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import src.com.zerodhatech.kiteconnect.kitehttp.exceptions.NetworkException;
import src.com.zerodhatech.kiteconnect.kitehttp.exceptions.OrderException;
import src.com.zerodhatech.kiteconnect.kitehttp.exceptions.PermissionException;
import src.com.zerodhatech.kiteconnect.kitehttp.exceptions.TokenException;

/**
 * Response handler for handling all the responses.
 */
public class KiteResponseHandler {

    public JSONObject handle(Response response, String body) throws IOException, KiteException, JSONException {
        if (response.header("Content-Type").contains("json")) {
            JSONObject jsonObject = new JSONObject(body);
            if(jsonObject.has("error_type")) {
                throw dealWithException(jsonObject, response.code());
            }
            return jsonObject;
        } else {
            throw new DataException("Unexpected content type received from server: "+ response.header("Content-Type")+" "+response.body().string(), 502);
        }
    }

    public String handle(Response response, String body, String type) throws IOException, KiteException, JSONException {
        if (response.header("Content-Type").contains("csv")) {
            return body;
        } else if(response.header("Content-Type").contains("json")){
            throw dealWithException(new JSONObject(response.body().string()), response.code());
        } else {
            throw new DataException("Unexpected content type received from server: "+ response.header("Content-Type")+" "+response.body().string(), 502);
        }
    }


    private KiteException dealWithException(JSONObject jsonObject, int code) throws JSONException {
        String exception = jsonObject.getString("error_type");

        switch (exception){
            // if there is a token exception, generate a signal to logout the user.
            case "TokenException":
                if(KiteConnect.sessionExpiryHook != null) {
                    KiteConnect.sessionExpiryHook.sessionExpired();
                }
                return  new TokenException(jsonObject.getString("message"), code);

            case "DataException": return new DataException(jsonObject.getString("message"), code);

            case "GeneralException": return new GeneralException(jsonObject.getString("message"), code);

            case "InputException": 
            	System.out.println(jsonObject.getString("message")+code);
            	return new InputException(jsonObject.getString("message"), code);

            case "OrderException": return new OrderException(jsonObject.getString("message"), code);

            case "NetworkException": return new NetworkException(jsonObject.getString("message"), code);

            case "PermissionException": return new PermissionException(jsonObject.getString("message"), code);

            default: return new KiteException(jsonObject.getString("message"), code);
        }
    }

}
