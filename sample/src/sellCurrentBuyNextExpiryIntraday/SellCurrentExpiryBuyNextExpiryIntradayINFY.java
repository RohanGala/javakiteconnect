package src.sellCurrentBuyNextExpiryIntraday;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import com.neovisionaries.ws.client.WebSocketException;

import src.Examples;
import src.com.zerodhatech.kiteconnect.KiteConnect;
import src.com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import src.com.zerodhatech.models.OptionDetails;

/**
 * Change in the following classes: KiteTicker->wsuri
 * KiteRequestHandler.createPostRequest -> for authorization and cookie
 * 
 */
public class SellCurrentExpiryBuyNextExpiryIntradayINFY {

	public static void main(String[] args) throws IOException, KiteException, WebSocketException {
		KiteConnect kiteConnect = new KiteConnect("");

		Examples examples = new Examples();
		SellCurrentExpiryBuyNextExpiryIntradayPrintIntoCSV sellCurrentExpiryBuyNextExpiryIntradayPrintIntoCSV = new SellCurrentExpiryBuyNextExpiryIntradayPrintIntoCSV();
		try {

			Map<String, OptionDetails> tokenAndName = new HashMap<>();
			tokenAndName.put("SPOT", new OptionDetails(Long.parseLong("408065"), "INFY")); 
			tokenAndName.put("BUY", new OptionDetails(Long.parseLong("16567554"), "INFY21APRFUT"));
			tokenAndName.put("SELL", new OptionDetails(Long.parseLong("15726850"), "INFY21MAYFUT")); 

			sellCurrentExpiryBuyNextExpiryIntradayPrintIntoCSV.printDataIntoCSV(kiteConnect,tokenAndName,examples);


		} catch (KiteException e) {
			System.out.println(e.message + " " + e.code + " " + e.getClass().getName());
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			// examples.logout(kiteConnect);
		}
	}
}