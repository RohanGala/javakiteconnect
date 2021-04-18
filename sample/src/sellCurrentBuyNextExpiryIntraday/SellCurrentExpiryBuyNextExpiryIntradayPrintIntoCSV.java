package src.sellCurrentBuyNextExpiryIntraday;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.neovisionaries.ws.client.WebSocketException;

import src.Examples;
import src.com.zerodhatech.kiteconnect.KiteConnect;
import src.com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import src.com.zerodhatech.models.OptionDetails;
import src.com.zerodhatech.models.Order;
import src.com.zerodhatech.models.Tick;
import src.com.zerodhatech.ticker.KiteTicker;
import src.com.zerodhatech.ticker.OnConnect;
import src.com.zerodhatech.ticker.OnDisconnect;
import src.com.zerodhatech.ticker.OnError;
import src.com.zerodhatech.ticker.OnOrderUpdate;
import src.com.zerodhatech.ticker.OnTicks;

public class SellCurrentExpiryBuyNextExpiryIntradayPrintIntoCSV {
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");

	
	public void printDataIntoCSV(final KiteConnect kiteConnect,
			final Map<String, OptionDetails> tokenAndName,final Examples examples) throws IOException, WebSocketException, KiteException {

		final ArrayList<Long> tokens = new ArrayList<>();
		tokens.add(tokenAndName.get("BUY").getInstrumentToken());
		tokens.add(tokenAndName.get("SELL").getInstrumentToken());
		tokens.add(tokenAndName.get("SPOT").getInstrumentToken());
		
		final String fileName = tokenAndName.get("SPOT").getTradingSymbol()+sdf.format(new Date());


        /** To get live price use websocket connection.
         * It is recommended to use only one websocket connection at any point of time and make sure you stop connection, once user goes out of app.
         * custom url points to new endpoint which can be used till complete Kite Connect 3 migration is done. */
        final KiteTicker tickerProvider = new KiteTicker(null,null);

        tickerProvider.setOnConnectedListener(new OnConnect() {
            @Override
            public void onConnected() {
                /** Subscribe ticks for token.
                 * By default, all tokens are subscribed for modeQuote.
                 * */
                tickerProvider.subscribe(tokens);
                tickerProvider.setMode(tokens, KiteTicker.modeFull);
            }
        });

        tickerProvider.setOnDisconnectedListener(new OnDisconnect() {
            @Override
            public void onDisconnected() {
                // your code goes here
            }
        });

        /** Set listener to get order updates.*/
        tickerProvider.setOnOrderUpdateListener(new OnOrderUpdate() {
            @Override
            public void onOrderUpdate(Order order) {
                System.out.println("order update "+order.orderId);
            }
        });

        /** Set error listener to listen to errors.*/
        tickerProvider.setOnErrorListener(new OnError() {
            @Override
            public void onError(Exception exception) {
                //handle here.
            }

            @Override
            public void onError(KiteException kiteException) {
                //handle here.
            }

            @Override
            public void onError(String error) {
                System.out.println(error);
            }
        });

        tickerProvider.setOnTickerArrivalListener(new OnTicks() {
        	 double currentCEBuySquareOff = 0;
        	 double currentCESellSqaureOff = 0;
        	 double actualCEBuy = 0;
        	 double actualCESell = 0;
        	 double actualSpotBuy = 0;
        	 double actualSpotSell = 0;
        	 Date tickTimestamp=null;
        	 
        	@Override
            public void onTicks(ArrayList<Tick> ticks) {
                
                	for(Tick tick :ticks) {
                		if(tokenAndName.get("BUY").getInstrumentToken().equals(tick.getInstrumentToken())) {
                			currentCEBuySquareOff = tick.getMarketDepth().get("buy").get(1).getPrice();
                			actualCEBuy = tick.getMarketDepth().get("sell").get(1).getPrice();
                            
                		}else if(tokenAndName.get("SELL").getInstrumentToken().equals(tick.getInstrumentToken())) {
                    		currentCESellSqaureOff = tick.getMarketDepth().get("sell").get(1).getPrice();
                    		actualCESell = tick.getMarketDepth().get("buy").get(1).getPrice();

                		}else if(tokenAndName.get("SPOT").getInstrumentToken().equals(tick.getInstrumentToken())) {
                			actualSpotSell = tick.getMarketDepth().get("buy").get(1).getPrice();
                    		actualSpotBuy = tick.getMarketDepth().get("sell").get(1).getPrice();

                		}
                		tickTimestamp = tick.getTickTimestamp();
                	}
                	
                	List<Object> record = new ArrayList<>();
                	record.add(tickTimestamp);
                	record.add(actualSpotBuy);
                	record.add(actualSpotSell);
                	
                	record.add(actualCEBuy);
                	record.add(currentCEBuySquareOff);
                	record.add(actualCESell);
                	record.add(currentCESellSqaureOff);
                	
					examples.writeinCSV(record ,fileName);
                   
                    
                
            }
        });
        // Make sure this is called before calling connect.
        tickerProvider.setTryReconnection(true);
        //maximum retries and should be greater than 0
        tickerProvider.setMaximumRetries(10);
        //set maximum retry interval in seconds
        tickerProvider.setMaximumRetryInterval(30);

        /** connects to com.zerodhatech.com.zerodhatech.ticker server for getting live quotes*/
        tickerProvider.connect();

        /** You can check, if websocket connection is open or not using the following method.*/
        boolean isConnected = tickerProvider.isConnectionOpen();
        System.out.println(isConnected);

        /** set mode is used to set mode in which you need tick for list of tokens.
         * Ticker allows three modes, modeFull, modeQuote, modeLTP.
         * For getting only last traded price, use modeLTP
         * For getting last traded price, last traded quantity, average price, volume traded today, total sell quantity and total buy quantity, open, high, low, close, change, use modeQuote
         * For getting all data with depth, use modeFull*/
        tickerProvider.setMode(tokens, KiteTicker.modeLTP);

        
    
    }
	
	
	public void printDataIntoCSVForIndex(final KiteConnect kiteConnect,
			final Map<String, OptionDetails> tokenAndName,final Examples examples) throws IOException, WebSocketException, KiteException {

		final ArrayList<Long> tokens = new ArrayList<>();
		tokens.add(tokenAndName.get("BUY").getInstrumentToken());
		tokens.add(tokenAndName.get("SELL").getInstrumentToken());
		
		final String fileName = tokenAndName.get("BUY").getTradingSymbol()+sdf.format(new Date());


        /** To get live price use websocket connection.
         * It is recommended to use only one websocket connection at any point of time and make sure you stop connection, once user goes out of app.
         * custom url points to new endpoint which can be used till complete Kite Connect 3 migration is done. */
        final KiteTicker tickerProvider = new KiteTicker(null,null);

        tickerProvider.setOnConnectedListener(new OnConnect() {
            @Override
            public void onConnected() {
                /** Subscribe ticks for token.
                 * By default, all tokens are subscribed for modeQuote.
                 * */
                tickerProvider.subscribe(tokens);
                tickerProvider.setMode(tokens, KiteTicker.modeFull);
            }
        });

        tickerProvider.setOnDisconnectedListener(new OnDisconnect() {
            @Override
            public void onDisconnected() {
                // your code goes here
            }
        });

        /** Set listener to get order updates.*/
        tickerProvider.setOnOrderUpdateListener(new OnOrderUpdate() {
            @Override
            public void onOrderUpdate(Order order) {
                System.out.println("order update "+order.orderId);
            }
        });

        /** Set error listener to listen to errors.*/
        tickerProvider.setOnErrorListener(new OnError() {
            @Override
            public void onError(Exception exception) {
                //handle here.
            }

            @Override
            public void onError(KiteException kiteException) {
                //handle here.
            }

            @Override
            public void onError(String error) {
                System.out.println(error);
            }
        });

        tickerProvider.setOnTickerArrivalListener(new OnTicks() {
        	 double currentCEBuySquareOff = 0;
        	 double currentCESellSqaureOff = 0;
        	 double actualCEBuy = 0;
        	 double actualCESell = 0;
        	 Date tickTimestamp=null;
        	 
        	@Override
            public void onTicks(ArrayList<Tick> ticks) {
                
                	for(Tick tick :ticks) {
                		if(tokenAndName.get("BUY").getInstrumentToken().equals(tick.getInstrumentToken())) {
                			currentCEBuySquareOff = tick.getMarketDepth().get("buy").get(1).getPrice();
                			actualCEBuy = tick.getMarketDepth().get("sell").get(1).getPrice();
                            
                		}else if(tokenAndName.get("SELL").getInstrumentToken().equals(tick.getInstrumentToken())) {
                    		currentCESellSqaureOff = tick.getMarketDepth().get("sell").get(1).getPrice();
                    		actualCESell = tick.getMarketDepth().get("buy").get(1).getPrice();

                		}               		tickTimestamp = tick.getTickTimestamp();
                	}
                	
                	List<Object> record = new ArrayList<>();
                	record.add(tickTimestamp);
                
                	record.add(actualCEBuy);
                	record.add(currentCEBuySquareOff);
                	record.add(actualCESell);
                	record.add(currentCESellSqaureOff);
                	
					examples.writeinCSV(record ,fileName);
                   
                    
                
            }
        });
        // Make sure this is called before calling connect.
        tickerProvider.setTryReconnection(true);
        //maximum retries and should be greater than 0
        tickerProvider.setMaximumRetries(10);
        //set maximum retry interval in seconds
        tickerProvider.setMaximumRetryInterval(30);

        /** connects to com.zerodhatech.com.zerodhatech.ticker server for getting live quotes*/
        tickerProvider.connect();

        /** You can check, if websocket connection is open or not using the following method.*/
        boolean isConnected = tickerProvider.isConnectionOpen();
        System.out.println(isConnected);

        /** set mode is used to set mode in which you need tick for list of tokens.
         * Ticker allows three modes, modeFull, modeQuote, modeLTP.
         * For getting only last traded price, use modeLTP
         * For getting last traded price, last traded quantity, average price, volume traded today, total sell quantity and total buy quantity, open, high, low, close, change, use modeQuote
         * For getting all data with depth, use modeFull*/
        tickerProvider.setMode(tokens, KiteTicker.modeLTP);

        
    
    }
	
	
	
}
