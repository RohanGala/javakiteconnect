package src.com.zerodhatech.ticker;

import src.com.zerodhatech.models.Order;

/**
 * Created by sujith on 12/26/17.
 */
public interface OnOrderUpdate {
    void onOrderUpdate(Order order);
}
