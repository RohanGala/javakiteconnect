package src;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.neovisionaries.ws.client.WebSocketException;

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

public class SellFutureSellPEBuyCEImpl {

	
	public void executeBuySell11(final KiteConnect kiteConnect, final ArrayList<Long> tokens,
			final Map<String, OptionDetails> tokenAndName,final Examples examples,final String FutresBuyTradingSymbol,final String PEBuyTradingSymbol, String CESellTradingSymbol) throws IOException, WebSocketException, KiteException {
    	

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
        	 double currentPESellSquareOff = 0;
        	 double currentCEBuySqaureOff = 0;
        	 double actualPESell = 0;
        	 double actualCEBuy = 0;
        	 double actualFutureSell=0;
        	 double currentFutureSellSquareOff=0;
        	 Boolean updateEntryPrices = true;
        	 Boolean orderPlaced = false;

        	 Boolean squareOffDone =false;
        	 
        	@Override
            public void onTicks(ArrayList<Tick> ticks) {
                NumberFormat formatter = new DecimalFormat();
                System.out.println("ticks size "+ticks.size());
               
                
                double finalPrice = 0;
                double entryPrice = 0;
                if(ticks.size() > 0) {
                
                	
                	for(Tick tick :ticks) {
                		if(tick.getMarketDepth() == null) {
                			continue;
                		}
                		if(tokenAndName.get("SELLFUT").getInstrumentToken().equals(tick.getInstrumentToken())) {
                			
                			

                            //System.out.println("CE Buy First depth->"+tick.getMarketDepth().get("buy").get(0).getPrice()+"("+tick.getMarketDepth().get("buy").get(0).getQuantity()+")");

                            //System.out.println("CE Buy Second depth->"+tick.getMarketDepth().get("buy").get(1).getPrice()+"("+tick.getMarketDepth().get("buy").get(1).getQuantity()+")");

                            //System.out.println("CE Buy Third depth->"+tick.getMarketDepth().get("buy").get(2).getPrice()+"("+tick.getMarketDepth().get("buy").get(2).getQuantity()+")");
                            currentFutureSellSquareOff = tick.getMarketDepth().get("sell").get(1).getPrice();
                            System.out.println("currentFutureSellSquareOff->"+currentFutureSellSquareOff);

                            if(updateEntryPrices) {
                            	actualFutureSell = tick.getMarketDepth().get("buy").get(1).getPrice();
                            	System.out.println("actualFutureSell->"+actualFutureSell);
                            }
                		}else if(tokenAndName.get("SELLOPTION").getInstrumentToken().equals(tick.getInstrumentToken())) {
                			
                			

                            //System.out.println("CE Buy First depth->"+tick.getMarketDepth().get("buy").get(0).getPrice()+"("+tick.getMarketDepth().get("buy").get(0).getQuantity()+")");

                            //System.out.println("CE Buy Second depth->"+tick.getMarketDepth().get("buy").get(1).getPrice()+"("+tick.getMarketDepth().get("buy").get(1).getQuantity()+")");

                            //System.out.println("CE Buy Third depth->"+tick.getMarketDepth().get("buy").get(2).getPrice()+"("+tick.getMarketDepth().get("buy").get(2).getQuantity()+")");
                            currentPESellSquareOff = tick.getMarketDepth().get("sell").get(1).getPrice();
                            System.out.println("currentPESellSquareOff->"+currentPESellSquareOff);

                            if(updateEntryPrices) {
                            	actualPESell = tick.getMarketDepth().get("buy").get(1).getPrice();
                            	System.out.println("actualPESell->"+actualPESell);
                            }
                		}else if(tokenAndName.get("BUYOPTION").getInstrumentToken().equals(tick.getInstrumentToken())) {

                    		//System.out.println("CE Sell First depth->"+tick.getMarketDepth().get("sell").get(0).getPrice()+"("+tick.getMarketDepth().get("sell").get(0).getQuantity()+")");
                            
                            //System.out.println("CE Sell Second depth->"+tick.getMarketDepth().get("sell").get(1).getPrice()+"("+tick.getMarketDepth().get("sell").get(1).getQuantity()+")");
                            
                            //System.out.println("CE Sell Third depth->"+tick.getMarketDepth().get("sell").get(2).getPrice()+"("+tick.getMarketDepth().get("sell").get(2).getQuantity()+")");
                            currentCEBuySqaureOff = tick.getMarketDepth().get("buy").get(1).getPrice();
                            System.out.println("currentCEBuySqaureOff->"+currentCEBuySqaureOff);
                            if(updateEntryPrices) {
                            	actualCEBuy = tick.getMarketDepth().get("sell").get(1).getPrice();
                            	System.out.println("actualCEBuy->"+actualCEBuy);
                            }
                            

                		}
                	}
                	System.out.println("actualCESell->"+actualCEBuy);
                	System.out.println("actualPEBuy->"+actualPESell);
                	System.out.println("actualFutureSell->"+actualFutureSell);
                	
                	
                      
                    
                   

                	Map<String, Object[]> data = new TreeMap<String, Object[]>();
                    data.put("1", new Object[] {actualFutureSell,actualPESell , actualCEBuy});
                	//putDatainFile(data);
                	
                	List<Object> record = new ArrayList<>();
                	record.add(actualFutureSell);
                	record.add(actualPESell);
                	record.add(actualCEBuy);
                	
					writeinCSV(record );
                	
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
	
	
	public void squareOffOrder(final KiteConnect kiteConnect, final ArrayList<Long> tokens,
			final Map<String, OptionDetails> tokenAndName,final Examples examples,final double actualCEBuy,final double actualCESell ) throws KiteException {
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
        	 boolean orderExecuted = false;
        	@Override
            public void onTicks(ArrayList<Tick> ticks) {
                NumberFormat formatter = new DecimalFormat();
                System.out.println("ticks size "+ticks.size());
               
                
                double FinalPrice = 0;
                if(ticks.size() > 0) {
                
                	
                	for(Tick tick :ticks) {
                		if(tokenAndName.get("BUY").getInstrumentToken().equals(tick.getInstrumentToken())) {

                            //System.out.println("CE Buy First depth->"+tick.getMarketDepth().get("buy").get(0).getPrice()+"("+tick.getMarketDepth().get("buy").get(0).getQuantity()+")");

                            //System.out.println("CE Buy Second depth->"+tick.getMarketDepth().get("buy").get(1).getPrice()+"("+tick.getMarketDepth().get("buy").get(1).getQuantity()+")");

                            //System.out.println("CE Buy Third depth->"+tick.getMarketDepth().get("buy").get(2).getPrice()+"("+tick.getMarketDepth().get("buy").get(2).getQuantity()+")");
                            currentCEBuySquareOff = tick.getMarketDepth().get("buy").get(0).getPrice();
                            System.out.println("currentCEBuySquareOff->"+currentCEBuySquareOff);

                            
                		}else if(tokenAndName.get("SELL").getInstrumentToken().equals(tick.getInstrumentToken())) {

                    		//System.out.println("CE Sell First depth->"+tick.getMarketDepth().get("sell").get(0).getPrice()+"("+tick.getMarketDepth().get("sell").get(0).getQuantity()+")");
                            
                            //System.out.println("CE Sell Second depth->"+tick.getMarketDepth().get("sell").get(1).getPrice()+"("+tick.getMarketDepth().get("sell").get(1).getQuantity()+")");
                            
                            //System.out.println("CE Sell Third depth->"+tick.getMarketDepth().get("sell").get(2).getPrice()+"("+tick.getMarketDepth().get("sell").get(2).getQuantity()+")");
                            currentCESellSqaureOff = tick.getMarketDepth().get("sell").get(0).getPrice();
                            System.out.println("currentCESellSqaureOff->"+currentCESellSqaureOff);
                            
                		}
                	}
                	System.out.println("actualCESell->"+actualCESell);
                	System.out.println("actualCEBuy->"+actualCEBuy);
                	FinalPrice = (currentCEBuySquareOff - actualCEBuy) + (actualCESell - currentCESellSqaureOff)*1;
                	System.out.println("Final Price->"+FinalPrice);
                	if(FinalPrice > 5.1 && !orderExecuted) {
                		try {
                			System.out.println("Placing Order With CE buy Square off="+currentCEBuySquareOff);
                			System.out.println("Placing Order With CE sell Square off="+currentCESellSqaureOff);
							//examples.placeOrder(kiteConnect,tokenAndName.get("SELL").getTradingSymbol(),currentCESellSqaureOff,Constants.TRANSACTION_TYPE_BUY,150);
							//examples.placeOrder(kiteConnect,tokenAndName.get("BUY").getTradingSymbol(),currentCEBuySquareOff,Constants.TRANSACTION_TYPE_SELL,75);
							orderExecuted = true;
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
	
	public void writeinCSV(List<Object> record){


		String NEW_LINE_SEPARATOR = "\n";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		CSVPrinter csvFilePrinter = null;
		FileWriter fileWriter =null;

		try {
			fileWriter = new FileWriter("/Users/rohangala/Documents/AlgoTrading/javakiteconnect/sample/src/NIFTY.csv",true); 
			CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
			csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
			// HEADER
			csvFilePrinter.printRecord(record);
			System.out.println("NIFTY.csv written successfully on disk."+record);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				fileWriter.flush();
				fileWriter.close();
				csvFilePrinter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	private void putDatainFile(Map<String, Object[]> data) {
		//Blank workbook
		FileInputStream myxls;
	    XSSFWorkbook workbook = null;
		try {
			myxls = new FileInputStream("/Users/rohangala/Documents/AlgoTrading/javakiteconnect/sample/src/NIFTY.csv");
			workbook = new XSSFWorkbook(myxls);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
         
        //Create a blank sheet
	    XSSFSheet worksheet = workbook.getSheetAt(0);
		//Iterate over data and write to sheet
        Set<String> keyset = data.keySet();
        int rownum = 0;
        for (String key : keyset)
        {
        	rownum=worksheet.getLastRowNum();
            Row row = worksheet.createRow(++rownum);
            Object [] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr)
            {
               Cell cell = row.createCell(cellnum++);
               if(obj instanceof String)
                    cell.setCellValue((String)obj);
                else if(obj instanceof Integer)
                    cell.setCellValue((Integer)obj);
                else {
                	cell.setCellValue((Double)obj);
                }
            }
        }
        try
        {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File("/Users/rohangala/Documents/AlgoTrading/javakiteconnect/sample/src/NIFTY.csv"));
            workbook.write(out);
            out.close();
            System.out.println("NIFTY.csv written successfully on disk.");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
		
	}
}
