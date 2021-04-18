package src.historicalTesting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.neovisionaries.ws.client.WebSocketException;

import src.Examples;
import src.com.zerodhatech.kiteconnect.KiteConnect;
import src.com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import src.com.zerodhatech.models.HistoricalData;
import src.com.zerodhatech.models.OptionDetails;

/**
 * Change in the following classes: KiteTicker->wsuri
 * KiteRequestHandler.createPostRequest -> for authorization and cookie
 * 
 */
public class SellCurrentExpiryBuyNextExpiryIntradayITC {

	public static void main(String[] args) throws IOException, KiteException, WebSocketException {
		KiteConnect kiteConnect = new KiteConnect("");

		Examples examples = new Examples();
		SellCurrentExpiryBuyNextExpiryIntradayNiftyImpl rsi = new SellCurrentExpiryBuyNextExpiryIntradayNiftyImpl();
		try {

			Map<String, OptionDetails> tokenAndName = new HashMap<>();
			tokenAndName.put("SPOT", new OptionDetails(Long.parseLong("424961"), "ITC")); 

			tokenAndName.put("BUY", new OptionDetails(Long.parseLong("16568066"), "ITC21APRFUT")); 
			tokenAndName.put("SELL", new OptionDetails(Long.parseLong("15728130"), "ITC21MAYFUT")); 

			ArrayList<Long> tokens = new ArrayList<>();
			tokens.add(tokenAndName.get("BUY").getInstrumentToken());
			tokens.add(tokenAndName.get("SELL").getInstrumentToken());
			tokens.add(tokenAndName.get("SPOT").getInstrumentToken());


			List<HistoricalData> spotList = examples.getHistoricalData(kiteConnect, "2021-02-08 09:15:00", "2021-04-08 15:30:00", "424961", "minute");  //SPOT
			List<HistoricalData> currentExpiryList = examples.getHistoricalData(kiteConnect, "2021-02-08 09:15:00", "2021-04-08 15:30:00", "16568066", "minute");  //CURRENT EXPIRY
			List<HistoricalData> sellExpiryList = examples.getHistoricalData(kiteConnect, "2021-02-08 09:15:00", "2021-04-08 15:30:00", "15728130", "minute");  //NEXT EXPIRY

			Map<String,HistoricalData> spotMapList=new LinkedHashMap<>();
			Map<String,HistoricalData> currentMapExpiryList=new LinkedHashMap<>();
			Map<String,HistoricalData> sellMapExpiryList=new LinkedHashMap<>();
			
			
			//Get the Prices and Square off
			for (HistoricalData a : spotList) {
				if (a != null) 
					spotMapList.put(a.timeStamp, a);
			}
			
			for (HistoricalData a : currentExpiryList) {
				if (a != null) 
					currentMapExpiryList.put(a.timeStamp, a);
			}
			
			for (HistoricalData a : sellExpiryList) {
				if (a != null) 
					sellMapExpiryList.put(a.timeStamp, a);
			}
			
			
			for (Map.Entry<String,HistoricalData> entry : spotMapList.entrySet()) {
	            System.out.println("Key = " + entry.getKey());
	            printHighLowOpenClose("SPOT",entry.getValue());
	            if(currentMapExpiryList.containsKey(entry.getKey())) {
	            	printHighLowOpenClose("CURRENT",currentMapExpiryList.get(entry.getKey()));
	            }
	            if(sellMapExpiryList.containsKey(entry.getKey())) {
	            	printHighLowOpenClose("NEXT",sellMapExpiryList.get(entry.getKey()));
	            }
	            
			}
			
	        
	        
		} catch (KiteException e) {
			System.out.println(e.message + " " + e.code + " " + e.getClass().getName());
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			// examples.logout(kiteConnect);
		}
	}

	private static void printHighLowOpenClose(String string, HistoricalData historicalData) {
		System.out.println(string+" :timeStamp:"+historicalData.timeStamp);
		System.out.println(string+" :volume:"+historicalData.volume);
		System.out.println(string+" :close:"+historicalData.close);
		System.out.println(string+" :high:"+historicalData.high);
		System.out.println(string+" :low:"+historicalData.low);
		System.out.println(string+" :open:"+historicalData.open);
		System.out.println(string+" :close:"+historicalData.close);
		System.out.println(string+" :oi:"+historicalData.oi);
	}
}