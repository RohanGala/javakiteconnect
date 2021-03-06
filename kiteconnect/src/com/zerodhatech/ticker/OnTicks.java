package src.com.zerodhatech.ticker;

import java.util.ArrayList;

import src.com.zerodhatech.models.Tick;

/**
 * Callback to listen to com.zerodhatech.ticker websocket on tick arrival event.
 */

/** OnTicks interface is called once ticks arrive.*/
public interface OnTicks {
    void onTicks(ArrayList<Tick> ticks);
}
