package src.sellCurrentBuyNextExpiryIntraday;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.neovisionaries.ws.client.WebSocketException;

import src.Examples;
import src.com.zerodhatech.kiteconnect.KiteConnect;
import src.com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import src.com.zerodhatech.models.OptionDetails;
import src.com.zerodhatech.models.Position;

/**
 * Change in the following classes: KiteTicker->wsuri
 * KiteRequestHandler.createPostRequest -> for authorization and cookie
 * 
 */
public class SellCurrentExpiryBuyNextExpiryIntradayHDFCBank {

	public static void main(String[] args) throws IOException, KiteException, WebSocketException {
		KiteConnect kiteConnect = new KiteConnect("");

		Examples examples = new Examples();
		SellCurrentExpiryBuyNextExpiryIntradayPrintIntoCSV sellCurrentExpiryBuyNextExpiryIntradayPrintIntoCSV = new SellCurrentExpiryBuyNextExpiryIntradayPrintIntoCSV();
		try {

			Map<String, OptionDetails> tokenAndName = new HashMap<>();
			tokenAndName.put("SPOT", new OptionDetails(Long.parseLong("341249"), "HDFCBANK")); 
			tokenAndName.put("BUY", new OptionDetails(Long.parseLong("16561410"), "HDFCBANK21APRFUT")); // 12200 CE buy
			tokenAndName.put("SELL", new OptionDetails(Long.parseLong("15721730"), "HDFCBANK21MAYFUT")); // 12400 CE
																											// sell

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