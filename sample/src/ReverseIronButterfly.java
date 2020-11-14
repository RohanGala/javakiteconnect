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
public class ReverseIronButterfly {

    public static void main(String[] args) throws IOException, KiteException, WebSocketException{
    	 KiteConnect kiteConnect = new KiteConnect("");


         Examples examples = new Examples();
         ReverseIronButterflyImpl rib =new ReverseIronButterflyImpl();
    	try {
    		
    		
    		
    		
            
    		
            Map<String,OptionDetails> tokenAndName = new HashMap<>();
            tokenAndName.put("SELLIN",new OptionDetails(Long.parseLong("12224514"), "NIFTY20NOV12200CE")); //12200 CE sell
            tokenAndName.put("BUY",new OptionDetails( Long.parseLong("12225538"),"NIFTY20NOV12300CE")); //12300 CE buy
            tokenAndName.put("SELLOUT",new OptionDetails(Long.parseLong("12226562"), "NIFTY20NOV12400CE")); //12200 CE sell
            
            
            ArrayList<Long> tokens = new ArrayList<>();
            tokens.add(tokenAndName.get("SELLIN").getInstrumentToken());
            tokens.add(tokenAndName.get("BUY").getInstrumentToken());
            tokens.add(tokenAndName.get("SELLOUT").getInstrumentToken());
            
            // execute 1:2:1
            rib.executeReverseIronButterfly121(kiteConnect, tokens,tokenAndName,examples,tokenAndName.get("SELLIN").getTradingSymbol(),tokenAndName.get("BUY").getTradingSymbol(),tokenAndName.get("SELLOUT").getTradingSymbol());
            
            
            //examples.testOrders(kiteConnect, tokens,tokenAndName,examples);
            //examples.tickerUsageRatioSpreads(kiteConnect, tokens,tokenAndName,examples,tokenAndName.get("BUY").getTradingSymbol(),tokenAndName.get("SELL").getTradingSymbol());
            
            //to get all the trades executed
            /*List<Trade> trades = examples.getTrades(kiteConnect);
            for (int i=0; i < trades.size(); i++) {
                System.out.println(trades.get(i).tradingSymbol+" "+trades.size());
            }
            */
            
          //ce buy,ce sell
            
            //examples.squareOffOrderRatioSpreads(kiteConnect, tokens,tokenAndName,examples,103.2,60.55);
            
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