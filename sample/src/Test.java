package src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import com.neovisionaries.ws.client.WebSocketException;

import src.com.zerodhatech.kiteconnect.KiteConnect;
import src.com.zerodhatech.kiteconnect.kitehttp.SessionExpiryHook;
import src.com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import src.com.zerodhatech.kiteconnect.utils.Constants;
import src.com.zerodhatech.models.OptionDetails;
import src.com.zerodhatech.models.User;

/**
 * Created by sujith on 7/10/16.
 * This class has example of how to initialize kiteSdk and make rest api calls to place order, get orders, modify order, cancel order,
 * get positions, get holdings, convert positions, get instruments, logout user, get historical data dump, get trades
 */
public class Test {

    public static void main(String[] args){
        try {
            // First you should get request_token, public_token using kitconnect login and then use request_token, public_token, api_secret to make any kiteConnect api call.
            // Initialize KiteSdk with your apiKey.
            KiteConnect kiteConnect = new KiteConnect("xxxxyyyyzzzz");

            //If you wish to enable debug logs send true in the constructor, this will log request and response.
            //KiteConnect kiteConnect = new KiteConnect("xxxxyyyyzzzz", true);

            // If you wish to set proxy then pass proxy as a second parameter in the constructor with api_key. syntax:- new KiteConnect("xxxxxxyyyyyzzz", proxy).
            //KiteConnect kiteConnect = new KiteConnect("xxxxyyyyzzzz", userProxy, false);

            // Set userId
            kiteConnect.setUserId("xxxxx");

            // Get login url
            String url = kiteConnect.getLoginURL();

            // Set session expiry callback.
            kiteConnect.setSessionExpiryHook(new SessionExpiryHook() {
                @Override
                public void sessionExpired() {
                    System.out.println("session expired");
                }
            });

            /* The request token can to be obtained after completion of login process. Check out https://kite.trade/docs/connect/v3/user/#login-flow for more information.
               A request token is valid for only a couple of minutes and can be used only once. An access token is valid for one whole day. Don't call this method for every app run.
               Once an access token is received it should be stored in preferences or database for further usage.
            */
            User user =  kiteConnect.generateSession("xxxxxtttyyy", "xxxxxxxyyyyy");
            kiteConnect.setAccessToken(user.accessToken);
            kiteConnect.setPublicToken(user.publicToken);

            Examples examples = new Examples();

            examples.getProfile(kiteConnect);

            examples.getMargins(kiteConnect);

            examples.getMarginCalculation(kiteConnect);

            examples.placeOrder(kiteConnect,"NIFTY20DEC12000CE",210.0,Constants.TRANSACTION_TYPE_BUY,75);

            examples.modifyOrder(kiteConnect);

            examples.cancelOrder(kiteConnect);

            examples.placeBracketOrder(kiteConnect);

            examples.modifyFirstLegBo(kiteConnect);

            examples.modifySecondLegBoSLM(kiteConnect);

            examples.modifySecondLegBoLIMIT(kiteConnect);

            examples.exitBracketOrder(kiteConnect);

            examples.getTriggerRange(kiteConnect);

            examples.placeCoverOrder(kiteConnect);

            examples.converPosition(kiteConnect);

            examples.getHistoricalData(kiteConnect);

            examples.getOrders(kiteConnect);

            examples.getOrder(kiteConnect,"201028004069211");

            examples.getTrades(kiteConnect);

            examples.getTradesWithOrderId(kiteConnect);

            examples.getPositions(kiteConnect);

            examples.getHoldings(kiteConnect);

            examples.getAllInstruments(kiteConnect);

            examples.getInstrumentsForExchange(kiteConnect);

            examples.getQuote(kiteConnect);

            examples.getOHLC(kiteConnect);

            examples.getLTP(kiteConnect);

            examples.getGTTs(kiteConnect);

            examples.getGTT(kiteConnect);

            examples.placeGTT(kiteConnect);

            examples.modifyGTT(kiteConnect);

            examples.cancelGTT(kiteConnect);

            examples.getMFInstruments(kiteConnect);

            examples.placeMFOrder(kiteConnect);

            examples.cancelMFOrder(kiteConnect);

            examples.getMFOrders(kiteConnect);

            examples.getMFOrder(kiteConnect);

            examples.placeMFSIP(kiteConnect);

            examples.modifyMFSIP(kiteConnect);

            examples.cancelMFSIP(kiteConnect);

            examples.getMFSIPS(kiteConnect);

            examples.getMFSIP(kiteConnect);

            examples.getMFHoldings(kiteConnect);

            examples.logout(kiteConnect);

            Map<String,OptionDetails> tokenAndName = new HashMap<>();
            tokenAndName.put("BUY",new OptionDetails(Long.parseLong("10322946"), "NIFTY20N0512000PE")); //12300 CE buy
            tokenAndName.put("SELL",new OptionDetails( Long.parseLong("13733890"),"NIFTY20OCT12000PE")); //12200 CE sell
            
            
            ArrayList<Long> tokens = new ArrayList<>();
            tokens.add(tokenAndName.get("BUY").getInstrumentToken());
            tokens.add(tokenAndName.get("SELL").getInstrumentToken());
            double CEBuy = 32.9;
            double CESell = 21.4;
            examples.tickerUsageRatioSpreads(kiteConnect, tokens,tokenAndName,examples,tokenAndName.get("BUY").getTradingSymbol(),tokenAndName.get("SELL").getTradingSymbol());
        } catch (KiteException e) {
            System.out.println(e.message+" "+e.code+" "+e.getClass().getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }
}