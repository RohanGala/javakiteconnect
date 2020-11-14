package src;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;

import com.neovisionaries.ws.client.WebSocketException;

import src.com.zerodhatech.kiteconnect.KiteConnect;
import src.com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import src.com.zerodhatech.kiteconnect.utils.Constants;
import src.com.zerodhatech.models.OptionDetails;
import src.com.zerodhatech.models.Order;
import src.com.zerodhatech.models.Tick;
import src.com.zerodhatech.ticker.KiteTicker;
import src.com.zerodhatech.ticker.OnConnect;
import src.com.zerodhatech.ticker.OnDisconnect;
import src.com.zerodhatech.ticker.OnError;
import src.com.zerodhatech.ticker.OnOrderUpdate;
import src.com.zerodhatech.ticker.OnTicks;

public class ReverseIronButterflyImpl {

	
	public void executeReverseIronButterfly121(final KiteConnect kiteConnect, final ArrayList<Long> tokens,
			final Map<String, OptionDetails> tokenAndName,final Examples examples,final String CEInSellTradingSymbol,final String CEBuyTradingSymbol, String CEOutSellTradingSymbol) throws IOException, WebSocketException, KiteException {
    	

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
        	 double currentCEInSellSquareOff = 0;
        	 double currentCEOutSellSquareOff = 0;
        	 double currentCEBuySqaureOff = 0;
        	 double actualCEInSell = 0;
        	 double actualCEOutSell = 0;
        	 double actualCEBuy = 0;
        	 double currentCEBuy=0;
        	 Boolean updateEntryPrices = true;
        	 Boolean orderPlaced = false;
        	 Boolean squareOffDone =false;
        	 
        	@Override
            public void onTicks(ArrayList<Tick> ticks) {
                NumberFormat formatter = new DecimalFormat();
                System.out.println("ticks size "+ticks.size());
               
                
                double FinalPrice = 0;
                double entryPrice = 0;
                if(ticks.size() > 0) {
                
                	
                	for(Tick tick :ticks) {
                		if(tokenAndName.get("SELLIN").getInstrumentToken().equals(tick.getInstrumentToken())) {
                			
                			

                            //System.out.println("CE Buy First depth->"+tick.getMarketDepth().get("buy").get(0).getPrice()+"("+tick.getMarketDepth().get("buy").get(0).getQuantity()+")");

                            //System.out.println("CE Buy Second depth->"+tick.getMarketDepth().get("buy").get(1).getPrice()+"("+tick.getMarketDepth().get("buy").get(1).getQuantity()+")");

                            //System.out.println("CE Buy Third depth->"+tick.getMarketDepth().get("buy").get(2).getPrice()+"("+tick.getMarketDepth().get("buy").get(2).getQuantity()+")");
                            currentCEInSellSquareOff = tick.getMarketDepth().get("sell").get(1).getPrice();
                            System.out.println("currentCEInSellSquareOff->"+currentCEInSellSquareOff);

                            if(updateEntryPrices) {
                            	actualCEInSell = tick.getMarketDepth().get("buy").get(1).getPrice();
                            	System.out.println("actualCEInSell->"+actualCEInSell);
                            }
                		}else if(tokenAndName.get("BUY").getInstrumentToken().equals(tick.getInstrumentToken())) {

                    		//System.out.println("CE Sell First depth->"+tick.getMarketDepth().get("sell").get(0).getPrice()+"("+tick.getMarketDepth().get("sell").get(0).getQuantity()+")");
                            
                            //System.out.println("CE Sell Second depth->"+tick.getMarketDepth().get("sell").get(1).getPrice()+"("+tick.getMarketDepth().get("sell").get(1).getQuantity()+")");
                            
                            //System.out.println("CE Sell Third depth->"+tick.getMarketDepth().get("sell").get(2).getPrice()+"("+tick.getMarketDepth().get("sell").get(2).getQuantity()+")");
                            currentCEBuySqaureOff = tick.getMarketDepth().get("buy").get(1).getPrice();
                            System.out.println("currentCEBuySqaureOff->"+currentCEBuySqaureOff);
                            if(updateEntryPrices) {
                            	actualCEBuy = tick.getMarketDepth().get("sell").get(1).getPrice();
                            	System.out.println("actualCEBuy->"+actualCEBuy);
                            }
                            

                		}else if(tokenAndName.get("SELLOUT").getInstrumentToken().equals(tick.getInstrumentToken())) {
                			
                			

                            //System.out.println("CE Buy First depth->"+tick.getMarketDepth().get("buy").get(0).getPrice()+"("+tick.getMarketDepth().get("buy").get(0).getQuantity()+")");

                            //System.out.println("CE Buy Second depth->"+tick.getMarketDepth().get("buy").get(1).getPrice()+"("+tick.getMarketDepth().get("buy").get(1).getQuantity()+")");

                            //System.out.println("CE Buy Third depth->"+tick.getMarketDepth().get("buy").get(2).getPrice()+"("+tick.getMarketDepth().get("buy").get(2).getQuantity()+")");
                			currentCEOutSellSquareOff = tick.getMarketDepth().get("sell").get(1).getPrice();
                            System.out.println("currentCEOutSellSquareOff->"+currentCEOutSellSquareOff);

                            if(updateEntryPrices) {
                            	actualCEOutSell = tick.getMarketDepth().get("buy").get(1).getPrice();
                            	System.out.println("actualCEOutBuy->"+actualCEOutSell);
                            }
                		}
                		
                	}
                	System.out.println("actualCEInSell->"+actualCEInSell);
                	System.out.println("actualCEBuy->"+actualCEBuy);
                	System.out.println("actualCEOutSell->"+actualCEOutSell);
                	
                	FinalPrice = (actualCEInSell - currentCEInSellSquareOff) + (currentCEBuySqaureOff - actualCEBuy)*2 + (actualCEOutSell - currentCEOutSellSquareOff);
                	
                	entryPrice = actualCEInSell + actualCEOutSell- actualCEBuy*2;
                	System.out.println("Entry Price->"+entryPrice);
                	System.out.println("Final Price->"+FinalPrice);
                	if (entryPrice > 12 && !orderPlaced && !squareOffDone) {
                		try {
                			System.out.println("Placing Order With CE In Sell="+actualCEInSell);
                			System.out.println("Placing Order With CE Buy="+actualCEBuy);
                			System.out.println("Placing Order With CE Out Sell="+actualCEOutSell);
                			//examples.placeOrder(kiteConnect,CEbuyTradingSymbol,actualCEBuy,Constants.TRANSACTION_TYPE_BUY,75);
							//examples.placeOrder(kiteConnect,CESellTradingSymbol,actualCESell,Constants.TRANSACTION_TYPE_SELL,75);
                			//examples.placeOrder(kiteConnect,CESellTradingSymbol,actualCESell,Constants.TRANSACTION_TYPE_SELL,75);
                			orderPlaced = true;
                			updateEntryPrices = false;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                	else if(!squareOffDone && orderPlaced && FinalPrice > 5) {

                		try {
                			System.out.println("Placing Order With CE In Sell Square off="+currentCEInSellSquareOff);
                			System.out.println("Placing Order With CE Buy Square off="+currentCEBuySqaureOff);
                			System.out.println("Placing Order With CE Out Sell Square off="+currentCEOutSellSquareOff);
                			

							//examples.placeOrder(kiteConnect,CESellTradingSymbol,currentCESellSqaureOff,Constants.TRANSACTION_TYPE_BUY,quantitysold);
							//examples.placeOrder(kiteConnect,CEbuyTradingSymbol,currentCEBuySquareOff,Constants.TRANSACTION_TYPE_SELL,75);
                			updateEntryPrices = true;
                			squareOffDone = true;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	
                		
                		
                	}
                	
                    /*System.out.println("last price "+ticks.get(1).getLastTradedPrice());
                    System.out.println("open interest "+formatter.format(ticks.get(1).getOi()));
                    System.out.println("day high OI "+formatter.format(ticks.get(1).getOpenInterestDayHigh()));
                    System.out.println("day low OI "+formatter.format(ticks.get(1).getOpenInterestDayLow()));
                    System.out.println("change "+formatter.format(ticks.get(1).getChange()));
                    System.out.println("tick timestamp "+ticks.get(1).getTickTimestamp());
                    System.out.println("tick timestamp date "+ticks.get(1).getTickTimestamp());
                    System.out.println("last traded time "+ticks.get(0).getLastTradedTime());
                    */
                   
                    
                }
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

        // Unsubscribe for a token.
       // tickerProvider.unsubscribe(tokens);

        // After using com.zerodhatech.com.zerodhatech.ticker, close websocket connection.
        //tickerProvider.disconnect();
    
    }
}
