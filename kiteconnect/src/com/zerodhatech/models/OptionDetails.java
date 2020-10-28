package src.com.zerodhatech.models;

public class OptionDetails {

	
	private String buyOrSell;
	
	private String tradingSymbol;
	
	private Long instrumentToken;
	
	

	

	

	public Long getInstrumentToken() {
		return instrumentToken;
	}

	public void setInstrumentToken(Long instrumentToken) {
		this.instrumentToken = instrumentToken;
	}

	public String getBuyOrSell() {
		return buyOrSell;
	}

	public void setBuyOrSell(String buyOrSell) {
		this.buyOrSell = buyOrSell;
	}

	public String getTradingSymbol() {
		return tradingSymbol;
	}

	public void setTradingSymbol(String tradingSymbol) {
		this.tradingSymbol = tradingSymbol;
	}
	
	public OptionDetails(Long instrumentToken,String tradingSymbol) {
		this.instrumentToken=instrumentToken;
		this.tradingSymbol=tradingSymbol;
	}
}
