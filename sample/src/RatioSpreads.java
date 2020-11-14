package src;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.neovisionaries.ws.client.WebSocketException;

import src.com.zerodhatech.kiteconnect.KiteConnect;
import src.com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import src.com.zerodhatech.models.OptionDetails;
import src.com.zerodhatech.models.Position;

/**
 * Change in the following classes: KiteTicker->wsuri
 * KiteRequestHandler.createPostRequest -> for authorization and cookie
 * 
 */
public class RatioSpreads {

	public static void main(String[] args) throws IOException, KiteException, WebSocketException {
		KiteConnect kiteConnect = new KiteConnect("");

		Examples examples = new Examples();
		RatioSpreadImpl rsi = new RatioSpreadImpl();
		try {

			Map<String, OptionDetails> tokenAndName = new HashMap<>();
			tokenAndName.put("BUY", new OptionDetails(Long.parseLong("12207362"), "NIFTY20NOV10700PE")); // 12200 CE buy
			tokenAndName.put("SELL", new OptionDetails(Long.parseLong("12205314"), "NIFTY20NOV10500PE")); // 12400 CE
																											// sell

			ArrayList<Long> tokens = new ArrayList<>();
			tokens.add(tokenAndName.get("BUY").getInstrumentToken());
			tokens.add(tokenAndName.get("SELL").getInstrumentToken());

			// first execute 1:2
			// rsi.executeRatioSpreads11(kiteConnect,
			// tokens,tokenAndName,examples,tokenAndName.get("BUY").getTradingSymbol(),tokenAndName.get("SELL").getTradingSymbol());

			// examples.testOrders(kiteConnect, tokens,tokenAndName,examples);
			// examples.tickerUsageRatioSpreads(kiteConnect,
			// tokens,tokenAndName,examples,tokenAndName.get("BUY").getTradingSymbol(),tokenAndName.get("SELL").getTradingSymbol());



			//Get the Prices and Square off
			Map<String, List<Position>> positionMap = examples.getPositions(kiteConnect);
			double buyPrice = 0;
			double sellPrice = 0;
			for (Map.Entry<String, List<Position>> entry : positionMap.entrySet()) {

				if (entry.getKey().equals("net")) {
					List<Position> positions = entry.getValue();
					for (Position position : positions) {
						if (position.tradingSymbol.equals(tokenAndName.get("BUY").getTradingSymbol())) {
							buyPrice = position.averagePrice;
							System.out.println("buyPrice->" + buyPrice);
						} else if (position.tradingSymbol.equals(tokenAndName.get("SELL").getTradingSymbol())) {
							sellPrice = position.averagePrice;
							System.out.println("sellPrice->" + sellPrice);
						}
					}
				}
			}

			examples.squareOffOrderRatioSpreads(kiteConnect, tokens, tokenAndName, examples, buyPrice, sellPrice);

		} catch (KiteException e) {
			System.out.println(e.message + " " + e.code + " " + e.getClass().getName());
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			// examples.logout(kiteConnect);
		}
	}
}