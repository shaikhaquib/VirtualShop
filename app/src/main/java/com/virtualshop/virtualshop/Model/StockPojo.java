package com.virtualshop.virtualshop.Model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class StockPojo{

	@SerializedName("result")
	private List<ResultItem> result;

	@SerializedName("status")
	private boolean status;

	public void setResult(List<ResultItem> result){
		this.result = result;
	}

	public List<ResultItem> getResult(){
		return result;
	}

	public void setStatus(boolean status){
		this.status = status;
	}

	public boolean isStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"StockPojo{" + 
			"result = '" + result + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}