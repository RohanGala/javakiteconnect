package src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.neovisionaries.ws.client.WebSocketException;

import src.com.zerodhatech.kiteconnect.KiteConnect;
import src.com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import src.com.zerodhatech.models.HistoricalData;
import src.com.zerodhatech.models.OptionDetails;
import src.com.zerodhatech.models.Position;

/**
 * Change in the following classes: KiteTicker->wsuri
 * KiteRequestHandler.createPostRequest -> for authorization and cookie
 * 
 */
public class TradeFuturesCurrentExpiryAndNextExpiryIntraday {

	public static void main(String[] args) throws IOException, KiteException, WebSocketException {
		KiteConnect kiteConnect = new KiteConnect("");

		Examples examples = new Examples();
		TradeFuturesCurrentExpiryAndNextExpiryIntradayImpl rsi = new TradeFuturesCurrentExpiryAndNextExpiryIntradayImpl();
		try {

			Map<String, OptionDetails> tokenAndName = new HashMap<>();
			tokenAndName.put("BUY", new OptionDetails(Long.parseLong("16492034"), "NIFTY21APRFUT")); // 12200 CE buy
			tokenAndName.put("SELL", new OptionDetails(Long.parseLong("12418050"), "NIFTY21JUNFUT")); // 12400 CE
																											// sell

			ArrayList<Long> tokens = new ArrayList<>();
			tokens.add(tokenAndName.get("BUY").getInstrumentToken());
			tokens.add(tokenAndName.get("SELL").getInstrumentToken());

			
            
			List<HistoricalData> output = rsi.getHistoricalData(kiteConnect, "2021-04-08 09:15:00", "2021-04-08 15:30:00", "16492034", "minute");
			//Get the Prices and Square off
			for (HistoricalData a : output) {

				if (a != null) {
						System.out.println(a.timeStamp);
				        System.out.println(a.volume);
				        System.out.println(a.close);
				        System.out.println(a.high);
				        System.out.println(a.open);
				        System.out.println(a.close);
				        System.out.println(a.close);


				        System.out.println(a.oi);
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
}