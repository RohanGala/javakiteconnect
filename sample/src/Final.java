package src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import com.neovisionaries.ws.client.WebSocketException;

import src.com.zerodhatech.kiteconnect.KiteConnect;
import src.com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import src.com.zerodhatech.models.OptionDetails;

/**
 * Change in the following classes:
 * KiteTicker->wsuri
 * KiteRequestHandler.createPostRequest  -> for authorization and cookie
 * 
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
            
    		//examples.placeOrder(kiteConnect,"NIFTY20DEC12000CE",210.0,Constants.TRANSACTION_TYPE_BUY);
           
    		//Placing orders
    		
    		
    		
            
    		
            Map<String,OptionDetails> tokenAndName = new HashMap<>();
            tokenAndName.put("BUY",new OptionDetails(Long.parseLong("10322946"), "NIFTY20N0512000PE")); //12300 CE buy
            tokenAndName.put("SELL",new OptionDetails( Long.parseLong("13733890"),"NIFTY20OCT12000PE")); //12200 CE sell
            
            
            ArrayList<Long> tokens = new ArrayList<>();
            tokens.add(tokenAndName.get("BUY").getInstrumentToken());
            tokens.add(tokenAndName.get("SELL").getInstrumentToken());
            
            
            //examples.testOrders(kiteConnect, tokens,tokenAndName,examples);
            //examples.tickerUsageInTheMoneySellOutOfMoneyBuy(kiteConnect, tokens,tokenAndName,examples,tokenAndName.get("BUY").getTradingSymbol(),tokenAndName.get("SELL").getTradingSymbol());
            
            //ce buy,ce sell
            examples.squareOffOrder(kiteConnect, tokens,tokenAndName,examples,318.9,255.15);
            
        } catch (KiteException e) {
            System.out.println(e.message+" "+e.code+" "+e.getClass().getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
        	  //examples.logout(kiteConnect);
        }
    }
}