package src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import com.neovisionaries.ws.client.WebSocketException;

import src.com.zerodhatech.kiteconnect.KiteConnect;
import src.com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;

/**
 * Created by sujith on 7/10/16.
 * This class has example of how to initialize kiteSdk and make rest api calls to place order, get orders, modify order, cancel order,
 * get positions, get holdings, convert positions, get instruments, logout user, get historical data dump, get trades
 */
public class Final {

    public static void main(String[] args) throws IOException, KiteException, WebSocketException{
    	 KiteConnect kiteConnect = new KiteConnect("");


         Examples examples = new Examples();
    	try {
    		 /*
            ArrayList<Long> tokens = new ArrayList<>();
            tokens.add(Long.parseLong("12234754"));
            tokens.add(Long.parseLong("12232706"));
            Map<Long,String> tokenAndName = new HashMap<>();
            tokenAndName.put(Long.parseLong("12234754"),"2"); //13000 CE 
            tokenAndName.put(Long.parseLong("12232706"),"1"); //12800 CE
            double CEBuy = 30.9;
            double CESell = 19.65;
            examples.tickerUsageRatioSpreads(kiteConnect, tokens,tokenAndName,CEBuy,CESell);*/
            
           
            ArrayList<Long> tokens = new ArrayList<>();
            tokens.add(Long.parseLong("12221954"));
            tokens.add(Long.parseLong("9001218"));
            Map<Long,String> tokenAndName = new HashMap<>();
            tokenAndName.put(Long.parseLong("9001218"),"BUY"); //12300 CE buy
            tokenAndName.put(Long.parseLong("12221954"),"SELL"); //12200 CE sell
            double CEBuy = 272;
            double CESell = 177;
            
            
            examples.tickerUsageInTheMoneySellOutOfMoneyBuy(kiteConnect, tokens,tokenAndName,CEBuy,CESell);
        } catch (KiteException e) {
            System.out.println(e.message+" "+e.code+" "+e.getClass().getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
        	  examples.logout(kiteConnect);
        }
    }
}