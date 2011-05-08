package tz.mldm.workshop.measures;

import com.google.code.bing.search.client.BingSearchClient;
import com.google.code.bing.search.client.BingSearchServiceClientFactory;
import com.google.code.bing.search.client.BingSearchClient.SearchRequestBuilder;
import com.google.code.bing.search.schema.AdultOption;
import com.google.code.bing.search.schema.SearchOption;
import com.google.code.bing.search.schema.SearchResponse;
import com.google.code.bing.search.schema.SourceType;
import com.google.code.bing.search.schema.web.WebResult;
import com.google.code.bing.search.schema.web.WebSearchOption;

public class BingSearch {
	
	private final static BingSearchServiceClientFactory factory = BingSearchServiceClientFactory.newInstance();	
	private final static BingSearchClient client = factory.createBingSearchClient();
	private final static String AppId = "20E94F658CD0BE4CA6C43ACFD0A704400C55801F";
	
	public static SearchResponse search(String query) {
		SearchRequestBuilder builder = client.newSearchRequestBuilder();
		builder.withAppId(AppId);
		builder.withQuery(query);
		builder.withSourceType(SourceType.WEB);
		builder.withVersion("2.0");
		builder.withMarket("en-us");
		builder.withAdultOption(AdultOption.MODERATE);
		builder.withSearchOption(SearchOption.ENABLE_HIGHLIGHTING);
		
		builder.withWebRequestCount(50L);
		builder.withWebRequestOffset(0L);
		builder.withWebRequestSearchOption(WebSearchOption.DISABLE_HOST_COLLAPSING);
		builder.withWebRequestSearchOption(WebSearchOption.DISABLE_QUERY_ALTERATIONS);

		SearchResponse response = client.search(builder.getResult());
		
		return response;
	}
	
	
}
