package src;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.json.JSONObject;

import com.neovisionaries.ws.client.WebSocketException;

import src.com.zerodhatech.kiteconnect.KiteConnect;
import src.com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import src.com.zerodhatech.kiteconnect.utils.Constants;
import src.com.zerodhatech.models.GTT;
import src.com.zerodhatech.models.GTTParams;
import src.com.zerodhatech.models.HistoricalData;
import src.com.zerodhatech.models.Holding;
import src.com.zerodhatech.models.Instrument;
import src.com.zerodhatech.models.MFHolding;
import src.com.zerodhatech.models.MFInstrument;
import src.com.zerodhatech.models.MFOrder;
import src.com.zerodhatech.models.MFSIP;
import src.com.zerodhatech.models.Margin;
import src.com.zerodhatech.models.MarginCalculationData;
import src.com.zerodhatech.models.MarginCalculationParams;
import src.com.zerodhatech.models.OptionDetails;
import src.com.zerodhatech.models.Order;
import src.com.zerodhatech.models.OrderParams;
import src.com.zerodhatech.models.Position;
import src.com.zerodhatech.models.Profile;
import src.com.zerodhatech.models.Quote;
import src.com.zerodhatech.models.Tick;
import src.com.zerodhatech.models.Trade;
import src.com.zerodhatech.models.TriggerRange;
import src.com.zerodhatech.ticker.KiteTicker;
import src.com.zerodhatech.ticker.OnConnect;
import src.com.zerodhatech.ticker.OnDisconnect;
import src.com.zerodhatech.ticker.OnError;
import src.com.zerodhatech.ticker.OnOrderUpdate;
import src.com.zerodhatech.ticker.OnTicks;

/**
 * Created by sujith on 15/10/16.
 */
public class Examples {


    public void getProfile(KiteConnect kiteConnect) throws IOException, KiteException {
        Profile profile = kiteConnect.getProfile();
        System.out.println(profile.userName);
    }

    /**Gets Margin.*/
    public void getMargins(KiteConnect kiteConnect) throws KiteException, IOException {
        // Get margins returns margin model, you can pass equity or commodity as arguments to get margins of respective segments.
        //Margins margins = kiteConnect.getMargins("equity");
        Margin margins = kiteConnect.getMargins("equity");
        System.out.println(margins.available.cash);
        System.out.println(margins.utilised.debits);
        System.out.println(margins.utilised.m2mUnrealised);
    }

    public void getMarginCalculation(KiteConnect kiteConnect) throws IOException, KiteException {
        MarginCalculationParams param = new MarginCalculationParams();
        param.exchange = "NSE";
        param.tradingSymbol = "INFY";
        param.orderType = "MARKET";
        param.quantity = 1;
        param.product = "MIS";
        param.variety = "regular";
        List<MarginCalculationParams> params = new ArrayList<>();
        params.add(param);
        List<MarginCalculationData> data = kiteConnect.getMarginCalculation(params);
        System.out.println(data.get(0).total);
    }

    /**Place order.*/
    public void placeOrder(KiteConnect kiteConnect,String tradingSymbol,double price,String buysell,Integer quantity) throws KiteException, IOException {
        /** Place order method requires a orderParams argument which contains,
         * tradingsymbol, exchange, transaction_type, order_type, quantity, product, price, trigger_price, disclosed_quantity, validity
         * squareoff_value, stoploss_value, trailing_stoploss
         * and variety (value can be regular, bo, co, amo)
         * place order will return order model which will have only orderId in the order model
         *
         * Following is an example param for LIMIT order,
         * if a call fails then KiteException will have error message in it
         * Success of this call implies only order has been placed successfully, not order execution. */

        OrderParams orderParams = new OrderParams();
        orderParams.quantity = quantity;
        orderParams.orderType = Constants.ORDER_TYPE_LIMIT;
        orderParams.tradingsymbol = tradingSymbol;
        orderParams.product = Constants.PRODUCT_NRML;
        orderParams.exchange = Constants.EXCHANGE_NFO;
        orderParams.transactionType = buysell;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.price = price;
        orderParams.triggerPrice = 0.0;
        orderParams.tag = "NiftyDec"; //tag is optional and it cannot be more than 8 characters and only alphanumeric is allowed

        Order order = kiteConnect.placeOrder(orderParams, Constants.VARIETY_REGULAR);
        System.out.println(order.orderId);
    }

    /** Place bracket order.*/
    public void placeBracketOrder(KiteConnect kiteConnect) throws KiteException, IOException {
        /** Bracket order:- following is example param for bracket order*
         * trailing_stoploss and stoploss_value are points and not tick or price
         */
        OrderParams orderParams = new OrderParams();
        orderParams.quantity = 1;
        orderParams.orderType = Constants.ORDER_TYPE_LIMIT;
        orderParams.price = 30.5;
        orderParams.transactionType = Constants.TRANSACTION_TYPE_BUY;
        orderParams.tradingsymbol = "SOUTHBANK";
        orderParams.trailingStoploss = 1.0;
        orderParams.stoploss = 2.0;
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.squareoff = 3.0;
        orderParams.product = Constants.PRODUCT_MIS;
         Order order10 = kiteConnect.placeOrder(orderParams, Constants.VARIETY_BO);
         System.out.println(order10.orderId);
    }

    /** Place cover order.*/
    public void placeCoverOrder(KiteConnect kiteConnect) throws KiteException, IOException {
        /** Cover Order:- following is an example param for the cover order
         * key: quantity value: 1
         * key: price value: 0
         * key: transaction_type value: BUY
         * key: tradingsymbol value: HINDALCO
         * key: exchange value: NSE
         * key: validity value: DAY
         * key: trigger_price value: 157
         * key: order_type value: MARKET
         * key: variety value: co
         * key: product value: MIS
         */
        OrderParams orderParams = new OrderParams();
        orderParams.price = 0.0;
        orderParams.quantity = 1;
        orderParams.transactionType = Constants.TRANSACTION_TYPE_BUY;
        orderParams.orderType = Constants.ORDER_TYPE_MARKET;
        orderParams.tradingsymbol = "SOUTHBANK";
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.triggerPrice = 30.5;
        orderParams.product = Constants.PRODUCT_MIS;

        Order order11 = kiteConnect.placeOrder(orderParams, Constants.VARIETY_CO);
        System.out.println(order11.orderId);
    }

    /** Get trigger range.*/
    public void getTriggerRange(KiteConnect kiteConnect) throws KiteException, IOException {
        // You need to send transaction_type, exchange and tradingsymbol to get trigger range.
        String[] instruments = {"BSE:INFY", "NSE:APOLLOTYRE", "NSE:SBIN"};
        Map<String, TriggerRange> triggerRangeMap = kiteConnect.getTriggerRange(instruments, Constants.TRANSACTION_TYPE_BUY);
        System.out.println(triggerRangeMap.get("NSE:SBIN").lower);
        System.out.println(triggerRangeMap.get("NSE:APOLLOTYRE").upper);
        System.out.println(triggerRangeMap.get("BSE:INFY").percentage);
    }

    /** Get orderbook.*/
    public void getOrders(KiteConnect kiteConnect) throws KiteException, IOException {
        // Get orders returns order model which will have list of orders inside, which can be accessed as follows,
        List<Order> orders = kiteConnect.getOrders();
        for(int i = 0; i< orders.size(); i++){
            System.out.println(orders.get(i).tradingSymbol+" "+orders.get(i).orderId+" "+orders.get(i).parentOrderId+
                " "+orders.get(i).orderType+" "+orders.get(i).averagePrice+" "+orders.get(i).exchangeTimestamp+" "+orders.get(i).exchangeUpdateTimestamp+" "+orders.get(i).guid);
        }
        System.out.println("list of orders size is "+orders.size());
    }

    /** Get order details*/
    public List<Order> getOrder(KiteConnect kiteConnect,String orderId) throws KiteException, IOException {
        List<Order> orders = kiteConnect.getOrderHistory(orderId);
        for(int i = 0; i< orders.size(); i++){
            System.out.println(orders.get(i).orderId+" "+orders.get(i).status);
        }
        System.out.println("list size is "+orders.size());
        return orders;
    }

    /** Get tradebook*/
    public  List<Trade>  getTrades(KiteConnect kiteConnect) throws KiteException, IOException {
        // Returns tradebook.
        List<Trade> trades = kiteConnect.getTrades();
        for (int i=0; i < trades.size(); i++) {
            System.out.println(trades.get(i).tradingSymbol+" "+trades.size());
        }
        System.out.println(trades.size());
        return trades;
    }

    /** Get trades for an order.*/
    public void getTradesWithOrderId(KiteConnect kiteConnect) throws KiteException, IOException {
        // Returns trades for the given order.
        List<Trade> trades = kiteConnect.getOrderTrades("180111000561605");
        System.out.println(trades.size());
    }

    /** Modify order.*/
    public void modifyOrder(KiteConnect kiteConnect) throws KiteException, IOException {
        // Order modify request will return order model which will contain only order_id.
        OrderParams orderParams =  new OrderParams();
        orderParams.quantity = 1;
        orderParams.orderType = Constants.ORDER_TYPE_LIMIT;
        orderParams.tradingsymbol = "ASHOKLEY";
        orderParams.product = Constants.PRODUCT_CNC;
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.transactionType = Constants.TRANSACTION_TYPE_BUY;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.price = 122.25;

        Order order21 = kiteConnect.modifyOrder("180116000984900", orderParams, Constants.VARIETY_REGULAR);
        System.out.println(order21.orderId);
    }

    /** Modify first leg bracket order.*/
    public void modifyFirstLegBo(KiteConnect kiteConnect) throws KiteException, IOException {
        OrderParams orderParams = new OrderParams();
        orderParams.quantity = 1;
        orderParams.price = 31.0;
        orderParams.transactionType = Constants.TRANSACTION_TYPE_BUY;
        orderParams.tradingsymbol = "SOUTHBANK";
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.product = Constants.PRODUCT_MIS;
        orderParams.tag = "myTag";
        orderParams.triggerPrice = 0.0;

        Order order = kiteConnect.modifyOrder("180116000798058", orderParams, Constants.VARIETY_BO);
        System.out.println(order.orderId);
    }

    public void modifySecondLegBoSLM(KiteConnect kiteConnect) throws KiteException, IOException {

        OrderParams orderParams = new OrderParams();
        orderParams.parentOrderId = "180116000798058";
        orderParams.tradingsymbol = "SOUTHBANK";
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.product = Constants.PRODUCT_MIS;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.triggerPrice = 30.5;
        orderParams.price = 0.0;
        orderParams.orderType = Constants.ORDER_TYPE_SLM;
        orderParams.transactionType = Constants.TRANSACTION_TYPE_SELL;

        Order order = kiteConnect.modifyOrder("180116000812154", orderParams, Constants.VARIETY_BO);
        System.out.println(order.orderId);
    }

    public void modifySecondLegBoLIMIT(KiteConnect kiteConnect) throws KiteException, IOException {
        OrderParams orderParams =  new OrderParams();
        orderParams.parentOrderId = "180116000798058";
        orderParams.tradingsymbol = "SOUTHBANK";
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.quantity =  1;
        orderParams.product = Constants.PRODUCT_MIS;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.price = 35.3;
        orderParams.orderType = Constants.ORDER_TYPE_LIMIT;
        orderParams.transactionType = Constants.TRANSACTION_TYPE_SELL;

        Order order = kiteConnect.modifyOrder("180116000812153", orderParams, Constants.VARIETY_BO);
        System.out.println(order.orderId);
    }

    /** Cancel an order*/
    public void cancelOrder(KiteConnect kiteConnect) throws KiteException, IOException {
        // Order modify request will return order model which will contain only order_id.
        // Cancel order will return order model which will only have orderId.
        Order order2 = kiteConnect.cancelOrder("180116000727266", Constants.VARIETY_REGULAR);
        System.out.println(order2.orderId);
    }

    public void exitBracketOrder(KiteConnect kiteConnect) throws KiteException, IOException {
        Order order = kiteConnect.cancelOrder("180116000812153","180116000798058", Constants.VARIETY_BO);
        System.out.println(order.orderId);
    }

    /**Get all gtts. */
    public void getGTTs(KiteConnect kiteConnect) throws KiteException, IOException {
        List<GTT> gtts = kiteConnect.getGTTs();
        System.out.println(gtts.get(0).createdAt);
        System.out.println(gtts.get(0).condition.exchange);
        System.out.println(gtts.get(0).orders.get(0).price);
    }

    /** Get a particular GTT. */
    public void getGTT(KiteConnect kiteConnect) throws IOException, KiteException {
        GTT gtt = kiteConnect.getGTT(177574);
        System.out.println(gtt.condition.tradingSymbol);
    }

    /** Place a GTT (Good till trigger)*/
    public void placeGTT(KiteConnect kiteConnect) throws IOException, KiteException {
        GTTParams gttParams = new GTTParams();
        gttParams.triggerType = Constants.OCO;
        gttParams.exchange = "NSE";
        gttParams.tradingsymbol = "SBIN";
        gttParams.lastPrice = 302.95;

        List<Double> triggerPrices = new ArrayList<>();
        triggerPrices.add(290d);
        triggerPrices.add(320d);
        gttParams.triggerPrices = triggerPrices;

        /** Only sell is allowed for OCO or two-leg orders.
         * Single leg orders can be buy or sell order.
         * Passing a last price is mandatory.
         * A stop-loss order must have trigger and price below last price and target order must have trigger and price above last price.
         * Only limit order type  and CNC product type is allowed for now.
         * */

        /** Stop-loss or lower trigger. */
        GTTParams.GTTOrderParams order1Params = gttParams. new GTTOrderParams();
        order1Params.orderType = Constants.ORDER_TYPE_LIMIT;
        order1Params.price = 290;
        order1Params.product = Constants.PRODUCT_CNC;
        order1Params.transactionType = Constants.TRANSACTION_TYPE_SELL;
        order1Params.quantity = 0;

        GTTParams.GTTOrderParams order2Params = gttParams. new GTTOrderParams();
        order2Params.orderType = Constants.ORDER_TYPE_LIMIT;
        order2Params.price = 320;
        order2Params.product = Constants.PRODUCT_CNC;
        order2Params.transactionType = Constants.TRANSACTION_TYPE_SELL;
        order2Params.quantity = 1;

        /** Target or upper trigger. */
        List<GTTParams.GTTOrderParams> ordersList = new ArrayList();
        ordersList.add(order1Params);
        ordersList.add(order2Params);
        gttParams.orders = ordersList;

        GTT gtt = kiteConnect.placeGTT(gttParams);
        System.out.println(gtt.id);
    }

    /** Modify a GTT (Good till trigger)*/
    public void modifyGTT(KiteConnect kiteConnect) throws IOException, KiteException {
        GTTParams gttParams = new GTTParams();
        gttParams.triggerType = Constants.OCO;
        gttParams.exchange = "NSE";
        gttParams.tradingsymbol = "SBIN";
        gttParams.lastPrice = 302.95;

        List<Double> triggerPrices = new ArrayList<>();
        triggerPrices.add(290d);
        triggerPrices.add(320d);
        gttParams.triggerPrices = triggerPrices;

        GTTParams.GTTOrderParams order1Params = gttParams. new GTTOrderParams();
        order1Params.orderType = Constants.ORDER_TYPE_LIMIT;
        order1Params.price = 290;
        order1Params.product = Constants.PRODUCT_CNC;
        order1Params.transactionType = Constants.TRANSACTION_TYPE_SELL;
        order1Params.quantity = 1;

        GTTParams.GTTOrderParams order2Params = gttParams. new GTTOrderParams();
        order2Params.orderType = Constants.ORDER_TYPE_LIMIT;
        order2Params.price = 320;
        order2Params.product = Constants.PRODUCT_CNC;
        order2Params.transactionType = Constants.TRANSACTION_TYPE_SELL;
        order2Params.quantity = 1;

        List<GTTParams.GTTOrderParams> ordersList = new ArrayList();
        ordersList.add(order1Params);
        ordersList.add(order2Params);
        gttParams.orders = ordersList;

        GTT gtt = kiteConnect.modifyGTT(176036, gttParams);
        System.out.println(gtt.id);
    }

    /** Cancel a GTT.*/
    public void cancelGTT(KiteConnect kiteConnect) throws IOException, KiteException {
        GTT gtt = kiteConnect.cancelGTT(175859);
        System.out.println(gtt.id);
    }

    /** Get all positions.*/
    public Map<String, List<Position>> getPositions(KiteConnect kiteConnect) throws KiteException, IOException {
        // Get positions returns position model which contains list of positions.
        Map<String, List<Position>> position = kiteConnect.getPositions();
		/*
		 * System.out.println(position.get("net").size());
		 * System.out.println(position.get("day").size());
		 * System.out.println(position.get("net").get(0).averagePrice);
		 */
        return position;
    }

    /** Get holdings.*/
    public void getHoldings(KiteConnect kiteConnect) throws KiteException, IOException {
        // Get holdings returns holdings model which contains list of holdings.
        List<Holding> holdings = kiteConnect.getHoldings();
        System.out.println(holdings.size());
    }

    /** Converts position*/
    public void converPosition(KiteConnect kiteConnect) throws KiteException, IOException {
        //Modify product can be used to change MIS to NRML(CNC) or NRML(CNC) to MIS.
        JSONObject jsonObject6 = kiteConnect.convertPosition("ASHOKLEY", Constants.EXCHANGE_NSE, Constants.TRANSACTION_TYPE_BUY, Constants.POSITION_DAY, Constants.PRODUCT_MIS, Constants.PRODUCT_CNC, 1);
        System.out.println(jsonObject6);
    }

    /** Get all instruments that can be traded using kite connect.*/
    public void getAllInstruments(KiteConnect kiteConnect) throws KiteException, IOException {
        // Get all instruments list. This call is very expensive as it involves downloading of large data dump.
        // Hence, it is recommended that this call be made once and the results stored locally once every morning before market opening.
        List<Instrument> instruments = kiteConnect.getInstruments();
        System.out.println(instruments.size());
    }

    /** Get instruments for the desired exchange.*/
    public void getInstrumentsForExchange(KiteConnect kiteConnect) throws KiteException, IOException {
        // Get instruments for an exchange.
        List<Instrument> nseInstruments = kiteConnect.getInstruments("CDS");
        System.out.println(nseInstruments.size());
    }

    /** Get quote for a scrip.*/
    public void getQuote(KiteConnect kiteConnect) throws KiteException, IOException {
        // Get quotes returns quote for desired tradingsymbol.
        String[] instruments = {"256265","BSE:INFY", "NSE:APOLLOTYRE", "NSE:NIFTY 50", "24507906"};
        Map<String, Quote> quotes = kiteConnect.getQuote(instruments);
        System.out.println(quotes.get("NSE:APOLLOTYRE").instrumentToken+"");
        System.out.println(quotes.get("NSE:APOLLOTYRE").oi +"");
        System.out.println(quotes.get("NSE:APOLLOTYRE").depth.buy.get(4).getPrice());
        System.out.println(quotes.get("NSE:APOLLOTYRE").timestamp);
        System.out.println(quotes.get("NSE:APOLLOTYRE").lowerCircuitLimit+"");
        System.out.println(quotes.get("NSE:APOLLOTYRE").upperCircuitLimit+"");
        System.out.println(quotes.get("24507906").oiDayHigh);
        System.out.println(quotes.get("24507906").oiDayLow);
    }

    /* Get ohlc and lastprice for multiple instruments at once.
     * Users can either pass exchange with tradingsymbol or instrument token only. For example {NSE:NIFTY 50, BSE:SENSEX} or {256265, 265}*/
    public void getOHLC(KiteConnect kiteConnect) throws KiteException, IOException {
        String[] instruments = {"256265","BSE:INFY", "NSE:INFY", "NSE:NIFTY 50"};
        System.out.println(kiteConnect.getOHLC(instruments).get("256265").lastPrice);
        System.out.println(kiteConnect.getOHLC(instruments).get("NSE:NIFTY 50").ohlc.open);
    }

    /** Get last price for multiple instruments at once.
     * USers can either pass exchange with tradingsymbol or instrument token only. For example {NSE:NIFTY 50, BSE:SENSEX} or {256265, 265}*/
    public void getLTP(KiteConnect kiteConnect) throws KiteException, IOException {
        String[] instruments = {"256265","BSE:INFY", "NSE:INFY", "NSE:NIFTY 50"};
        System.out.println(kiteConnect.getLTP(instruments).get("256265").lastPrice);
    }

    /** Get historical data for an instrument.*/
    public void getHistoricalData(KiteConnect kiteConnect) throws KiteException, IOException {
        /** Get historical data dump, requires from and to date, intrument token, interval, continuous (for expired F&O contracts), oi (open interest)
         * returns historical data object which will have list of historical data inside the object.*/
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date from =  new Date();
        Date to = new Date();
        try {
            from = formatter.parse("2019-09-20 09:15:00");
            to = formatter.parse("2019-09-20 15:30:00");
        }catch (ParseException e) {
            e.printStackTrace();
        }
        HistoricalData historicalData = kiteConnect.getHistoricalData(from, to, "54872327", "15minute", false, true);
        System.out.println(historicalData.dataArrayList.size());
        System.out.println(historicalData.dataArrayList.get(0).volume);
        System.out.println(historicalData.dataArrayList.get(historicalData.dataArrayList.size() - 1).volume);
        System.out.println(historicalData.dataArrayList.get(0).oi);
    }

    /** Logout user.*/
    public void logout(KiteConnect kiteConnect) throws KiteException, IOException {
        /** Logout user and kill session. */
        JSONObject jsonObject10 = kiteConnect.logout();
        System.out.println(jsonObject10);
    }

    /** Retrieve mf instrument dump */
    public void getMFInstruments(KiteConnect kiteConnect) throws KiteException, IOException {
        List<MFInstrument> mfList = kiteConnect.getMFInstruments();
        System.out.println("size of mf instrument list: "+mfList.size());
    }

    /* Get all mutualfunds holdings */
    public void getMFHoldings(KiteConnect kiteConnect) throws KiteException, IOException {
        List<MFHolding> MFHoldings = kiteConnect.getMFHoldings();
        System.out.println("mf holdings "+ MFHoldings.size());
    }

    /* Place a mutualfunds order */
    public void placeMFOrder(KiteConnect kiteConnect) throws KiteException, IOException {
        System.out.println("place order: "+ kiteConnect.placeMFOrder("INF174K01LS2", Constants.TRANSACTION_TYPE_BUY, 5000, 0, "myTag").orderId);
    }

    /* cancel mutualfunds order */
    public void cancelMFOrder(KiteConnect kiteConnect) throws KiteException, IOException {
        kiteConnect.cancelMFOrder("668604240868430");
        System.out.println("cancel order successful");
    }

    /* retrieve all mutualfunds orders */
    public void getMFOrders(KiteConnect kiteConnect) throws KiteException, IOException {
        List<MFOrder> MFOrders = kiteConnect.getMFOrders();
        System.out.println("mf orders: "+ MFOrders.size());
    }

    /* retrieve individual mutualfunds order */
    public void getMFOrder(KiteConnect kiteConnect) throws KiteException, IOException {
        System.out.println("mf order: "+ kiteConnect.getMFOrder("106580291331583").tradingsymbol);
    }

    /* place mutualfunds sip */
    public void placeMFSIP(KiteConnect kiteConnect) throws KiteException, IOException {
        System.out.println("mf place sip: "+ kiteConnect.placeMFSIP("INF174K01LS2", "monthly", 1, -1, 5000, 1000).sipId);
    }

    /* modify a mutual fund sip */
    public void modifyMFSIP(KiteConnect kiteConnect) throws KiteException, IOException {
        kiteConnect.modifyMFSIP("weekly", 1, 5, 1000, "active", "504341441825418");
    }

    /* cancel a mutualfunds sip */
    public void cancelMFSIP(KiteConnect kiteConnect) throws KiteException, IOException {
        kiteConnect.cancelMFSIP("504341441825418");
        System.out.println("cancel sip successful");
    }

    /* retrieve all mutualfunds sip */
    public void getMFSIPS(KiteConnect kiteConnect) throws KiteException, IOException {
        List<MFSIP> sips = kiteConnect.getMFSIPs();
        System.out.println("mf sips: "+ sips.size());
    }

    /* retrieve individual mutualfunds sip */
    public void getMFSIP(KiteConnect kiteConnect) throws KiteException, IOException {
        System.out.println("mf sip: "+ kiteConnect.getMFSIP("291156521960679").instalments);
    }

    /** Demonstrates com.zerodhatech.ticker connection, subcribing for instruments, unsubscribing for instruments, set mode of tick data, com.zerodhatech.ticker disconnection
     * @param tokenAndName 
     * @param CESell 
     * @param CEBuy */
    public void tickerUsageRatioSpreads(final KiteConnect kiteConnect, final ArrayList<Long> tokens,
			final Map<String, OptionDetails> tokenAndName,final Examples examples,final String CEbuyTradingSymbol,final String CESellTradingSymbol) throws IOException, WebSocketException, KiteException {
    	

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
        	 Boolean updateEntryPrices = true;
        	 Boolean orderPlaced = false;
        	@Override
            public void onTicks(ArrayList<Tick> ticks) {
                NumberFormat formatter = new DecimalFormat();
                System.out.println("ticks size "+ticks.size());
               
                
                double FinalPrice = 0;
                double entryPrice = 0;
                if(ticks.size() > 0) {
                
                	
                	for(Tick tick :ticks) {
                		if(tokenAndName.get("BUY").getInstrumentToken().equals(tick.getInstrumentToken())) {
                			
                			

                            //System.out.println("CE Buy First depth->"+tick.getMarketDepth().get("buy").get(0).getPrice()+"("+tick.getMarketDepth().get("buy").get(0).getQuantity()+")");

                            //System.out.println("CE Buy Second depth->"+tick.getMarketDepth().get("buy").get(1).getPrice()+"("+tick.getMarketDepth().get("buy").get(1).getQuantity()+")");

                            //System.out.println("CE Buy Third depth->"+tick.getMarketDepth().get("buy").get(2).getPrice()+"("+tick.getMarketDepth().get("buy").get(2).getQuantity()+")");
                            currentCEBuySquareOff = tick.getMarketDepth().get("buy").get(2).getPrice();
                            System.out.println("currentCEBuySquareOff->"+currentCEBuySquareOff);

                            if(updateEntryPrices) {
                            	actualCEBuy = tick.getMarketDepth().get("sell").get(1).getPrice();
                            	System.out.println("actualCEBuy->"+actualCEBuy);
                            }
                		}else if(tokenAndName.get("SELL").getInstrumentToken().equals(tick.getInstrumentToken())) {

                    		//System.out.println("CE Sell First depth->"+tick.getMarketDepth().get("sell").get(0).getPrice()+"("+tick.getMarketDepth().get("sell").get(0).getQuantity()+")");
                            
                            //System.out.println("CE Sell Second depth->"+tick.getMarketDepth().get("sell").get(1).getPrice()+"("+tick.getMarketDepth().get("sell").get(1).getQuantity()+")");
                            
                            //System.out.println("CE Sell Third depth->"+tick.getMarketDepth().get("sell").get(2).getPrice()+"("+tick.getMarketDepth().get("sell").get(2).getQuantity()+")");
                            currentCESellSqaureOff = tick.getMarketDepth().get("sell").get(2).getPrice();
                            System.out.println("currentCESellSqaureOff->"+currentCESellSqaureOff);
                            if(updateEntryPrices) {
                            	actualCESell = tick.getMarketDepth().get("buy").get(1).getPrice();
                            	System.out.println("actualCESell->"+actualCESell);
                            }
                		}
                	}
                	System.out.println("actualCESell->"+actualCESell);
                	System.out.println("actualCEBuy->"+actualCEBuy);
                	FinalPrice = (currentCEBuySquareOff - actualCEBuy)*2 + (actualCESell - currentCESellSqaureOff)*4;
                	entryPrice = actualCEBuy - actualCESell*2;
                	System.out.println("Entry Price->"+entryPrice);
                	System.out.println("Final Price->"+FinalPrice);
                	if(FinalPrice > 3 && orderPlaced) {
                		try {
                			System.out.println("Placing Order With CE buy Square off="+currentCEBuySquareOff);
                			System.out.println("Placing Order With CE sell Square off="+currentCESellSqaureOff);
							examples.placeOrder(kiteConnect,CESellTradingSymbol,currentCESellSqaureOff,Constants.TRANSACTION_TYPE_BUY,150);
							examples.placeOrder(kiteConnect,CEbuyTradingSymbol,currentCEBuySquareOff,Constants.TRANSACTION_TYPE_SELL,75);
                			orderPlaced = false;
                			updateEntryPrices = true;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}else if (entryPrice < -20 && !orderPlaced) {
                		try {
                			System.out.println("Placing Order With CE buy="+actualCEBuy);
                			System.out.println("Placing Order With CE sell="+actualCESell);
                			examples.placeOrder(kiteConnect,CEbuyTradingSymbol,actualCEBuy,Constants.TRANSACTION_TYPE_BUY,75);
							examples.placeOrder(kiteConnect,CESellTradingSymbol,actualCESell,Constants.TRANSACTION_TYPE_SELL,150);
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

	public void tickerUsageInTheMoneySellOutOfMoneyBuy(final KiteConnect kiteConnect, final ArrayList<Long> tokens,
			final Map<String, OptionDetails> tokenAndName,final Examples examples,final String CEbuyTradingSymbol,final String CESellTradingSymbol) throws KiteException {
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
        	 Boolean updateEntryPrices = true;
        	 Boolean orderPlaced = false;
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
                            currentCEBuySquareOff = tick.getMarketDepth().get("buy").get(2).getPrice();
                            System.out.println("currentCEBuySquareOff->"+currentCEBuySquareOff);

                            if(updateEntryPrices) {
                            	actualCEBuy = tick.getMarketDepth().get("sell").get(1).getPrice();
                            	System.out.println("actualCEBuy->"+actualCEBuy);
                            }
                		}else if(tokenAndName.get("SELL").getInstrumentToken().equals(tick.getInstrumentToken())) {

                    		//System.out.println("CE Sell First depth->"+tick.getMarketDepth().get("sell").get(0).getPrice()+"("+tick.getMarketDepth().get("sell").get(0).getQuantity()+")");
                            
                            //System.out.println("CE Sell Second depth->"+tick.getMarketDepth().get("sell").get(1).getPrice()+"("+tick.getMarketDepth().get("sell").get(1).getQuantity()+")");
                            
                            //System.out.println("CE Sell Third depth->"+tick.getMarketDepth().get("sell").get(2).getPrice()+"("+tick.getMarketDepth().get("sell").get(2).getQuantity()+")");
                            currentCESellSqaureOff = tick.getMarketDepth().get("sell").get(2).getPrice();
                            System.out.println("currentCESellSqaureOff->"+currentCESellSqaureOff);
                            if(updateEntryPrices) {
                            	actualCESell = tick.getMarketDepth().get("buy").get(1).getPrice();
                            	System.out.println("actualCESell->"+actualCESell);
                            }
                		}
                	}
                	System.out.println("actualCESell->"+actualCESell);
                	System.out.println("actualCESell->"+actualCESell);
                	FinalPrice = (currentCEBuySquareOff - actualCEBuy) + (actualCESell - currentCESellSqaureOff);
                	System.out.println("Final Price->"+FinalPrice);
                	if(FinalPrice > 3 && orderPlaced) {
                		try {
                			System.out.println("Placing Order With CE buy Square off="+currentCEBuySquareOff);
                			System.out.println("Placing Order With CE sell Square off="+currentCESellSqaureOff);
							examples.placeOrder(kiteConnect,CESellTradingSymbol,currentCESellSqaureOff,Constants.TRANSACTION_TYPE_BUY,75);
							examples.placeOrder(kiteConnect,CEbuyTradingSymbol,currentCEBuySquareOff,Constants.TRANSACTION_TYPE_SELL,75);
                			orderPlaced = false;
                			updateEntryPrices = true;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}else if (!orderPlaced) {
                		try {
                			System.out.println("Placing Order With CE buy="+actualCEBuy);
                			System.out.println("Placing Order With CE sell="+actualCESell);
                			examples.placeOrder(kiteConnect,CEbuyTradingSymbol,actualCEBuy,Constants.TRANSACTION_TYPE_BUY,75);
							examples.placeOrder(kiteConnect,CESellTradingSymbol,actualCESell,Constants.TRANSACTION_TYPE_SELL,75);
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
                	FinalPrice = (currentCEBuySquareOff - actualCEBuy) + (actualCESell - currentCESellSqaureOff);
                	System.out.println("Final Price->"+FinalPrice);
                	if(FinalPrice > 3 && !orderExecuted) {
                		try {
                			System.out.println("Placing Order With CE buy Square off="+currentCEBuySquareOff);
                			System.out.println("Placing Order With CE sell Square off="+currentCESellSqaureOff);
							examples.placeOrder(kiteConnect,tokenAndName.get("SELL").getTradingSymbol(),currentCESellSqaureOff,Constants.TRANSACTION_TYPE_BUY,75);
							examples.placeOrder(kiteConnect,tokenAndName.get("BUY").getTradingSymbol(),currentCEBuySquareOff,Constants.TRANSACTION_TYPE_SELL,75);
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
	
	
	public void squareOffOrderRatioSpreads(final KiteConnect kiteConnect, final ArrayList<Long> tokens,
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
                	FinalPrice = (currentCEBuySquareOff - actualCEBuy) + (actualCESell - currentCESellSqaureOff)*2;
                	System.out.println("Final Price->"+FinalPrice);
                	if(FinalPrice > 5.1 && !orderExecuted) {
                		try {
                			System.out.println("Placing Order With CE buy Square off="+currentCEBuySquareOff);
                			System.out.println("Placing Order With CE sell Square off="+currentCESellSqaureOff);
							examples.placeOrder(kiteConnect,tokenAndName.get("SELL").getTradingSymbol(),currentCESellSqaureOff,Constants.TRANSACTION_TYPE_BUY,150);
							examples.placeOrder(kiteConnect,tokenAndName.get("BUY").getTradingSymbol(),currentCEBuySquareOff,Constants.TRANSACTION_TYPE_SELL,75);
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
	
	public void testOrders(final KiteConnect kiteConnect, final ArrayList<Long> tokens,
			final Map<String, OptionDetails> tokenAndName,final Examples examples) throws KiteException {
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
        	
        	 Boolean orderPlaced = false;
        	double actualCEBuy=0.0;
        	@Override
            public void onTicks(ArrayList<Tick> ticks) {
                System.out.println("ticks size "+ticks.size());
               
                
                if(ticks.size() > 0) {
                
                	
                	for(Tick tick :ticks) {
                		if(tokenAndName.get("BUY").getInstrumentToken().equals(tick.getInstrumentToken())) {

                            
                            	actualCEBuy = tick.getMarketDepth().get("sell").get(1).getPrice();
                            	actualCEBuy = actualCEBuy-40.0;
                            	System.out.println("actualCEBuy->"+actualCEBuy);
                            
                		}
                	}
                	
                		try {
                			System.out.println("Placing Order With CE buy="+actualCEBuy);
                			if(!orderPlaced) {
                				examples.placeOrder(kiteConnect,tokenAndName.get("BUY").getTradingSymbol(),actualCEBuy,Constants.TRANSACTION_TYPE_BUY,75);
                				orderPlaced = true;;
                			}
							
                			
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	
                	
                  
                    
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
        //tickerProvider.setMode(tokens, KiteTicker.modeLTP);

        // Unsubscribe for a token.
       // tickerProvider.unsubscribe(tokens);

        // After using com.zerodhatech.com.zerodhatech.ticker, close websocket connection.
        //tickerProvider.disconnect();
    }
	
	/** Get historical data for an instrument.*/
    public List<HistoricalData> getHistoricalData(KiteConnect kiteConnect,String fromDateInString, String  toDateInString, String token, String interval) throws KiteException, IOException {
        /** Get historical data dump, requires from and to date, intrument token, interval, continuous (for expired F&O contracts), oi (open interest)
         * returns historical data object which will have list of historical data inside the object.*/
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date from =  new Date();
        Date to = new Date();
        try {
            from = formatter.parse(fromDateInString);
            to = formatter.parse(toDateInString);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        HistoricalData historicalData = kiteConnect.getHistoricalData(from, to, token, interval, false, true);
       
        return historicalData.dataArrayList;
    }
    
    public void writeinCSV(List<Object> record, String fileName){


		String NEW_LINE_SEPARATOR = "\n";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		CSVPrinter csvFilePrinter = null;
		FileWriter fileWriter =null;

		try {
			fileWriter = new FileWriter("/Users/rohangala/Documents/AlgoTrading/javakiteconnect/sample/src/"+fileName+".csv",true); 
			CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
			csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
			// HEADER
			csvFilePrinter.printRecord(record);
			System.out.println(fileName+".csv written successfully on disk."+record);
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
}
