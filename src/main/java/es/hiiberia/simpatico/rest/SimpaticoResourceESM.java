package es.hiiberia.simpatico.rest;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import es.hiiberia.simpatico.utils.ElasticSearchConnector;
import es.hiiberia.simpatico.utils.SimpaticoProperties;

/**
 * 
 * @author hi
 * Every endpoint returns a percentage to directly use it in eSM.
 * Parameter "eserviceId" is mandatory.
 * Parameters "init" and "end" are optional.
 * No authentication needed to access this paths.
 */
@Path("/esm")
public class SimpaticoResourceESM {
	
	private static int numLinesPrintStackInternalError = 5;
	private static int[] ownIDs = {10, 11};
	
	@GET
	@Path("/totalrequests")
	public Response getTotalRequests(@QueryParam("eserviceId") String eservice, @QueryParam("init") String init, @QueryParam("end") String end) {
		// Search all form_start events from init to end. If no dates present, search all
		if (eservice != null) {
			BoolQueryBuilder query = new BoolQueryBuilder();
			query.must(QueryBuilders.matchQuery("e-serviceID", eservice));
			query.must(QueryBuilders.matchQuery("event", "form_start"));
			query.must(QueryBuilders.rangeQuery("created").gte(init).lte(end));
			// Filter out our IDS
			query.mustNot(QueryBuilders.matchQuery("userID", ownIDs[0]));
			query.mustNot(QueryBuilders.matchQuery("userID", ownIDs[1]));
			
			try {
				SearchResponse responseES = ElasticSearchConnector.getInstance().searchES(SimpaticoProperties.elasticSearchSharedIndex, null, query, SimpaticoProperties.elasticSearchCreatedFieldName, SortOrder.ASC, 1);
				long hits = responseES.getHits().getTotalHits();
				return Response.status(SimpaticoResourceUtils.serverOkCode).entity(hits).build();
			} catch (Exception e) {
				Logger.getRootLogger().error(e.getMessage());
				return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
			}
		} else {
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, "eserviceId parameter is mandatory");
		}
	}
	
	@GET
	@Path("/finishedrequests")
	public Response getTotalFinishedRequests(@QueryParam("eserviceId") String eservice, @QueryParam("init") String init, @QueryParam("end") String end) {
		// Search all form_end events from init to end. If no dates present, search all
		if (eservice != null) {
			BoolQueryBuilder query = new BoolQueryBuilder();
			query.must(QueryBuilders.matchQuery("e-serviceID", eservice));
			query.must(QueryBuilders.matchQuery("event", "form_end"));
			query.must(QueryBuilders.rangeQuery("created").gte(init).lte(end));
			// Filter out our IDS
			query.mustNot(QueryBuilders.matchQuery("userID", ownIDs[0]));
			query.mustNot(QueryBuilders.matchQuery("userID", ownIDs[1]));
			
			try {
				SearchResponse responseES = ElasticSearchConnector.getInstance().searchES(SimpaticoProperties.elasticSearchSharedIndex, null, query, SimpaticoProperties.elasticSearchCreatedFieldName, SortOrder.ASC, 1);
				long hits = responseES.getHits().getTotalHits();
				return Response.status(SimpaticoResourceUtils.serverOkCode).entity(hits).build();
			} catch (Exception e) {
				Logger.getRootLogger().error(e.getMessage());
				return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
			}
		} else {
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, "eserviceId parameter is mandatory");
		}
	}
	
	@GET
	@Path("/averagetime")
	public Response getAverageTime(@QueryParam("eserviceId") String eservice, @QueryParam("init") String init, @QueryParam("end") String end) {
		// Search all session_end events. Get sum of fields averageTime and divide it between the number of total hits
		if (eservice != null) {
			BoolQueryBuilder query = new BoolQueryBuilder();
			query.must(QueryBuilders.matchQuery("e-serviceID", eservice));
			query.must(QueryBuilders.matchQuery("event", "session_end"));
			query.must(QueryBuilders.rangeQuery("created").gte(init).lte(end));
			// Filter out our IDS
			query.mustNot(QueryBuilders.matchQuery("userID", ownIDs[0]));
			query.mustNot(QueryBuilders.matchQuery("userID", ownIDs[1]));
			
			try {
				SearchResponse responseES = ElasticSearchConnector.getInstance().searchES(SimpaticoProperties.elasticSearchSharedIndex, null, query, SimpaticoProperties.elasticSearchCreatedFieldName, SortOrder.ASC, 0);
				long hits = responseES.getHits().getTotalHits();
				int sum = 0;
				for (SearchHit hit: responseES.getHits().getHits()) {
					int value = (int) hit.getSource().get("averageTime");
					sum += value;
				}
				
				long sumMin = TimeUnit.MILLISECONDS.toMinutes(sum); // Total time in minutes
				long averageMin = sumMin / hits;
				return Response.status(SimpaticoResourceUtils.serverOkCode).entity(averageMin).build();
			} catch (Exception e) {
				Logger.getRootLogger().error(e.getMessage());
				return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
			}
		} else {
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, "eserviceId parameter is mandatory");
		}
	}
	
	@GET
	@Path("/satisfaction")
	public Response getFacesSatisfaction(@QueryParam("eserviceId") String eservice, @QueryParam("init") String init, @QueryParam("end") String end) {
		// Search all session_feedback events from init to end. Return an array of 3 positions: happy, normal, sad with their respectives total values
		if (eservice != null) {
			BoolQueryBuilder query = new BoolQueryBuilder();
			query.must(QueryBuilders.matchQuery("e-serviceID", eservice));
			query.must(QueryBuilders.matchQuery("event", "session_feedback"));
			query.must(QueryBuilders.rangeQuery("created").gte(init).lte(end));
			// Filter out our IDS
			query.mustNot(QueryBuilders.matchQuery("userID", ownIDs[0]));
			query.mustNot(QueryBuilders.matchQuery("userID", ownIDs[1]));
			
			try {
				SearchResponse responseES = ElasticSearchConnector.getInstance().searchES(SimpaticoProperties.elasticSearchHIIndex, null, query, SimpaticoProperties.elasticSearchCreatedFieldName, SortOrder.ASC, 0);
				JSONArray totalsAr = new JSONArray(); // happy, normal, sad
				totalsAr.put(0).put(0).put(0); // initialize array
				for (SearchHit hit: responseES.getHits().getHits()) {
					String value = (String) hit.getSource().get("faces_session_feedback");
					if (value != null) {
						// Cases
						if (value.equals("happy")) {
							totalsAr.put(0, totalsAr.getInt(0)+1);
						} else if (value.equals("normal")) {
							totalsAr.put(1, totalsAr.getInt(1)+1);
						} else if (value.equals("sad")) {
							totalsAr.put(2, totalsAr.getInt(2)+1);
						}
					}
				}
				return Response.status(SimpaticoResourceUtils.serverOkCode).entity(totalsAr.toString()).build();
			} catch (Exception e) {
				Logger.getRootLogger().error(e.getMessage());
				return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
			}
		} else {
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, "eserviceId parameter is mandatory");
		}
	}
	
	@GET
	@Path("/ctzuse")
	public Response getUseOfCtz(@QueryParam("eserviceId") String eservice, @QueryParam("init") String init, @QueryParam("end") String end) {
		if (eservice != null) {
			BoolQueryBuilder query = new BoolQueryBuilder();
			query.must(QueryBuilders.matchQuery("e-serviceID", eservice));
			query.must(QueryBuilders.matchQuery("event", "simplification_start"));
			query.must(QueryBuilders.matchQuery("component", "ctz"));
			query.must(QueryBuilders.rangeQuery("created").gte(init).lte(end));
			// Filter out our IDS
			query.mustNot(QueryBuilders.matchQuery("userID", ownIDs[0]));
			query.mustNot(QueryBuilders.matchQuery("userID", ownIDs[1]));
			
			try {
				// First get total sessions
				float totalSessions = (float) getTotalSessions(eservice, init, end);
				
				SearchResponse responseES = ElasticSearchConnector.getInstance().searchES(SimpaticoProperties.elasticSearchHIIndex, null, query, SimpaticoProperties.elasticSearchCreatedFieldName, SortOrder.ASC, 1);
				float hits = (float) responseES.getHits().getTotalHits();
				float percentage = (hits/totalSessions)*100; // calculate percentage
				return Response.status(SimpaticoResourceUtils.serverOkCode).entity((int) percentage).build(); // Remove decimals from float percentage
			} catch (Exception e) {
				Logger.getRootLogger().error(e.getMessage());
				return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
			}
		} else {
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, "eserviceId parameter is mandatory");
		}
	}
	
	@GET
	@Path("/ctzuseful")
	public Response getHowUsefulCtzIs(@QueryParam("eserviceId") String eservice, @QueryParam("init") String init, @QueryParam("end") String end) {
		if (eservice != null) {
			BoolQueryBuilder query = new BoolQueryBuilder();
			query.must(QueryBuilders.matchQuery("e-serviceID", eservice));
			query.must(QueryBuilders.matchQuery("event", "session_feedback"));
			query.must(QueryBuilders.rangeQuery("created").gte(init).lte(end));
			query.must(QueryBuilders.matchQuery("radios.component", "global")); // To differentiate from previous session_feedback events. New structure
			// Should have at least one field with the component "ctz"
			query.should(QueryBuilders.matchQuery("ranges.component", "ctz"));
			query.should(QueryBuilders.matchQuery("texts.component", "ctz"));
			query.should(QueryBuilders.matchQuery("radios.component", "ctz"));
			query.minimumNumberShouldMatch(1);
			
			try {
				SearchResponse responseES = ElasticSearchConnector.getInstance().searchES(SimpaticoProperties.elasticSearchHIIndex, null, query, SimpaticoProperties.elasticSearchCreatedFieldName, SortOrder.ASC, 0);
				// Get the ranges values
				int totalResponses = 0; // Total responses with at least one questions related to ctz
				int totalValues = 0;
				ArrayList<?> list;
				for (SearchHit hit: responseES.getHits().getHits()) {
					list = (ArrayList<?>) hit.getSource().get("ranges");
					JSONArray ranges = new JSONArray(list);
					for (int i=0; i<ranges.length(); i++) {
						JSONObject range = ranges.getJSONObject(i);
						if (range.getString("component").equalsIgnoreCase("ctz")) {
							int value = Integer.valueOf(range.getString("value"));
							totalValues += value;
							totalResponses++;
						}
					}
				}
				// Calculate percentage of usefulness
				float percentage = 0;
				if (totalResponses != 0 && totalValues != 0) {
					percentage = ((float) totalValues/totalResponses)*10;
				}
				return Response.status(SimpaticoResourceUtils.serverOkCode).entity((int) percentage).build(); // Remove decimals from float percentage
			} catch (Exception e) {
				Logger.getRootLogger().error(e.getMessage());
				return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
			}
		} else {
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, "eserviceId parameter is mandatory");
		}
	}
	
	@GET
	@Path("/taeuse")
	public Response getUseOfTAE(@QueryParam("eserviceId") String eservice, @QueryParam("init") String init, @QueryParam("end") String end) {
		if (eservice != null) {
			BoolQueryBuilder query = new BoolQueryBuilder();
			query.must(QueryBuilders.matchQuery("e-serviceID", eservice));
			query.must(QueryBuilders.matchQuery("event", "tae_simplification_start"));
			query.must(QueryBuilders.rangeQuery("created").gte(init).lte(end));
			// Filter out our IDS
			query.mustNot(QueryBuilders.matchQuery("userID", ownIDs[0]));
			query.mustNot(QueryBuilders.matchQuery("userID", ownIDs[1]));
			
			try {
				// First get total sessions
				float totalSessions = (float) getTotalSessions(eservice, init, end);
				
				SearchResponse responseES = ElasticSearchConnector.getInstance().searchES(SimpaticoProperties.elasticSearchSharedIndex, null, query, SimpaticoProperties.elasticSearchCreatedFieldName, SortOrder.ASC, 1);
				float hits = (float) responseES.getHits().getTotalHits();
				float percentage = (hits/totalSessions)*100; // calculate percentage
				percentage = Math.round(percentage);
				return Response.status(SimpaticoResourceUtils.serverOkCode).entity((int) percentage).build(); // Remove decimals from float percentage
			} catch (Exception e) {
				Logger.getRootLogger().error(e.getMessage());
				return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
			}
		} else {
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, "eserviceId parameter is mandatory");
		}
	}
	
	@GET
	@Path("/taeuseful")
	public Response getHowUsefulTaeIs(@QueryParam("eserviceId") String eservice, @QueryParam("init") String init, @QueryParam("end") String end) {
		if (eservice != null) {
			BoolQueryBuilder query = new BoolQueryBuilder();
			query.must(QueryBuilders.matchQuery("e-serviceID", eservice));
			query.must(QueryBuilders.matchQuery("event", "session_feedback"));
			query.must(QueryBuilders.rangeQuery("created").gte(init).lte(end));
			query.must(QueryBuilders.matchQuery("radios.component", "global")); // To differentiate from previous session_feedback events. New structure
			// Should have at least one field with the component "tae"
			query.should(QueryBuilders.matchQuery("ranges.component", "tae"));
			query.should(QueryBuilders.matchQuery("texts.component", "tae"));
			query.should(QueryBuilders.matchQuery("radios.component", "tae"));
			query.minimumNumberShouldMatch(1);
			
			try {
				SearchResponse responseES = ElasticSearchConnector.getInstance().searchES(SimpaticoProperties.elasticSearchHIIndex, null, query, SimpaticoProperties.elasticSearchCreatedFieldName, SortOrder.ASC, 0);
				// Get the ranges values
				int totalResponses = 0; // Total responses with at least one questions related to ctz
				int totalValues = 0;
				ArrayList<?> list;
				for (SearchHit hit: responseES.getHits().getHits()) {
					list = (ArrayList<?>) hit.getSource().get("ranges");
					JSONArray ranges = new JSONArray(list);
					for (int i=0; i<ranges.length(); i++) {
						JSONObject range = ranges.getJSONObject(i);
						if (range.getString("component").equalsIgnoreCase("tae")) {
							int value = Integer.valueOf(range.getString("value"));
							totalValues += value;
							totalResponses++;
						}
					}
				}
				// Calculate percentage of usefulness
				float percentage = 0;
				if (totalResponses != 0 && totalValues != 0) {
					percentage = ((float) totalValues/totalResponses)*10;
				}
				return Response.status(SimpaticoResourceUtils.serverOkCode).entity((int) percentage).build(); // Remove decimals from float percentage
			} catch (Exception e) {
				Logger.getRootLogger().error(e.getMessage());
				return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
			}
		} else {
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, "eserviceId parameter is mandatory");
		}
	}
	
	@GET
	@Path("/cdvuse")
	public Response getUseOfCDV(@QueryParam("eserviceId") String eservice, @QueryParam("init") String init, @QueryParam("end") String end) {
		if (eservice != null) {
			BoolQueryBuilder query = new BoolQueryBuilder();
			query.must(QueryBuilders.matchQuery("e-serviceID", eservice));
			query.must(QueryBuilders.matchQuery("action", "usedata")); 
			query.must(QueryBuilders.matchQuery("component", "cdv"));
			query.must(QueryBuilders.rangeQuery("created").gte(init).lte(end));
			// Filter out our IDS
			query.mustNot(QueryBuilders.matchQuery("userID", ownIDs[0]));
			query.mustNot(QueryBuilders.matchQuery("userID", ownIDs[1]));
			
			try {
				// First get total sessions
				float totalSessions = (float) getTotalSessions(eservice, init, end);
				
				SearchResponse responseES = ElasticSearchConnector.getInstance().searchES(SimpaticoProperties.elasticSearchHIIndex, null, query, SimpaticoProperties.elasticSearchCreatedFieldName, SortOrder.ASC, 1);
				float hits = (float) responseES.getHits().getTotalHits();
				float percentage = (hits/totalSessions)*100; // calculate percentage
				percentage = Math.round(percentage);
				return Response.status(SimpaticoResourceUtils.serverOkCode).entity((int) percentage).build(); // Remove decimals from float percentage
			} catch (Exception e) {
				Logger.getRootLogger().error(e.getMessage());
				return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
			}
		} else {
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, "eserviceId parameter is mandatory");
		}
	}
	
	@GET
	@Path("/cdvuseful")
	public Response getHowUsefulCdvIs(@QueryParam("eserviceId") String eservice, @QueryParam("init") String init, @QueryParam("end") String end) {
		if (eservice != null) {
			BoolQueryBuilder query = new BoolQueryBuilder();
			query.must(QueryBuilders.matchQuery("e-serviceID", eservice));
			query.must(QueryBuilders.matchQuery("event", "session_feedback"));
			query.must(QueryBuilders.rangeQuery("created").gte(init).lte(end));
			query.must(QueryBuilders.matchQuery("radios.component", "global")); // To differentiate from previous session_feedback events. New structure
			// Should have at least one field with the component "cdv"
			query.should(QueryBuilders.matchQuery("ranges.component", "cdv"));
			query.should(QueryBuilders.matchQuery("texts.component", "cdv"));
			query.should(QueryBuilders.matchQuery("radios.component", "cdv"));
			query.minimumNumberShouldMatch(1);
			
			try {
				SearchResponse responseES = ElasticSearchConnector.getInstance().searchES(SimpaticoProperties.elasticSearchHIIndex, null, query, SimpaticoProperties.elasticSearchCreatedFieldName, SortOrder.ASC, 0);
				// Get the ranges values
				int totalResponses = 0; // Total responses with at least one questions related to ctz
				int totalValues = 0;
				ArrayList<?> list;
				for (SearchHit hit: responseES.getHits().getHits()) {
					list = (ArrayList<?>) hit.getSource().get("ranges");
					JSONArray ranges = new JSONArray(list);
					for (int i=0; i<ranges.length(); i++) {
						JSONObject range = ranges.getJSONObject(i);
						if (range.getString("component").equalsIgnoreCase("cdv")) {
							int value = Integer.valueOf(range.getString("value"));
							totalValues += value;
							totalResponses++;
						}
					}
				}
				// Calculate percentage of usefulness
				float percentage = 0;
				if (totalResponses != 0 && totalValues != 0) {
					percentage = ((float) totalValues/totalResponses)*10;
				}
				return Response.status(SimpaticoResourceUtils.serverOkCode).entity((int) percentage).build(); // Remove decimals from float percentage
			} catch (Exception e) {
				Logger.getRootLogger().error(e.getMessage());
				return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
			}
		} else {
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, "eserviceId parameter is mandatory");
		}
	}
	
	@GET
	@Path("/waeuse")
	public Response getUseOfWAE(@QueryParam("eserviceId") String eservice, @QueryParam("init") String init, @QueryParam("end") String end) {
		if (eservice != null) {
			BoolQueryBuilder query = new BoolQueryBuilder();
			query.must(QueryBuilders.matchQuery("e-serviceID", eservice));
			query.must(QueryBuilders.matchQuery("event", "workflow_adaptation_request"));
			query.must(QueryBuilders.rangeQuery("created").gte(init).lte(end));
			// Filter out our IDS
			query.mustNot(QueryBuilders.matchQuery("userID", ownIDs[0]));
			query.mustNot(QueryBuilders.matchQuery("userID", ownIDs[1]));
			
			try {
				// First get total sessions
				float totalSessions = (float) getTotalSessions(eservice, init, end);
				
				SearchResponse responseES = ElasticSearchConnector.getInstance().searchES(SimpaticoProperties.elasticSearchSharedIndex, null, query, SimpaticoProperties.elasticSearchCreatedFieldName, SortOrder.ASC, 1);
				float hits = (float) responseES.getHits().getTotalHits();
				float percentage = (hits/totalSessions)*100; // calculate percentage
				percentage = Math.round(percentage);
				return Response.status(SimpaticoResourceUtils.serverOkCode).entity((int) percentage).build(); // Remove decimals from float percentage
			} catch (Exception e) {
				Logger.getRootLogger().error(e.getMessage());
				return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
			}
		} else {
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, "eserviceId parameter is mandatory");
		}
	}
	
	@GET
	@Path("/waeuseful")
	public Response getHowUsefulWaeIs(@QueryParam("eserviceId") String eservice, @QueryParam("init") String init, @QueryParam("end") String end) {
		if (eservice != null) {
			BoolQueryBuilder query = new BoolQueryBuilder();
			query.must(QueryBuilders.matchQuery("e-serviceID", eservice));
			query.must(QueryBuilders.matchQuery("event", "session_feedback"));
			query.must(QueryBuilders.rangeQuery("created").gte(init).lte(end));
			query.must(QueryBuilders.matchQuery("radios.component", "global")); // To differentiate from previous session_feedback events. New structure
			// Should have at least one field with the component "wae"
			query.should(QueryBuilders.matchQuery("ranges.component", "wae"));
			query.should(QueryBuilders.matchQuery("texts.component", "wae"));
			query.should(QueryBuilders.matchQuery("radios.component", "wae"));
			query.minimumNumberShouldMatch(1);
			
			try {
				SearchResponse responseES = ElasticSearchConnector.getInstance().searchES(SimpaticoProperties.elasticSearchHIIndex, null, query, SimpaticoProperties.elasticSearchCreatedFieldName, SortOrder.ASC, 0);
				// Get the ranges values
				int totalResponses = 0; // Total responses with at least one questions related to ctz
				int totalValues = 0;
				ArrayList<?> list;
				for (SearchHit hit: responseES.getHits().getHits()) {
					list = (ArrayList<?>) hit.getSource().get("ranges");
					JSONArray ranges = new JSONArray(list);
					for (int i=0; i<ranges.length(); i++) {
						JSONObject range = ranges.getJSONObject(i);
						if (range.getString("component").equalsIgnoreCase("wae")) {
							int value = Integer.valueOf(range.getString("value"));
							totalValues += value;
							totalResponses++;
						}
					}
				}
				// Calculate percentage of usefulness
				float percentage = 0;
				if (totalResponses != 0 && totalValues != 0) {
					percentage = ((float) totalValues/totalResponses)*10;
				}
				return Response.status(SimpaticoResourceUtils.serverOkCode).entity((int) percentage).build(); // Remove decimals from float percentage
			} catch (Exception e) {
				Logger.getRootLogger().error(e.getMessage());
				return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
			}
		} else {
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, "eserviceId parameter is mandatory");
		}
	}
	
	@GET
	@Path("/words")
	public Response getWordsInComments(@QueryParam("eserviceId") String eservice, @QueryParam("init") String init, @QueryParam("end") String end) {
		if (eservice != null) {
			BoolQueryBuilder query = new BoolQueryBuilder();
			query.must(QueryBuilders.matchQuery("e-serviceID", eservice));
			query.must(QueryBuilders.matchQuery("event", "session_feedback"));
			query.must(QueryBuilders.rangeQuery("created").gte(init).lte(end));
			query.must(QueryBuilders.matchQuery("texts.component", "global")); // To differentiate from previous session_feedback events. New structure
			
			try {
				SearchResponse responseES = ElasticSearchConnector.getInstance().searchES(SimpaticoProperties.elasticSearchHIIndex, null, query, SimpaticoProperties.elasticSearchCreatedFieldName, SortOrder.ASC, 0);
				// Get the texts string
				String allComments = "";
				ArrayList<?> list;
				for (SearchHit hit: responseES.getHits().getHits()) {
					list = (ArrayList<?>) hit.getSource().get("texts");
					JSONArray texts = new JSONArray(list);
					for (int i=0; i<texts.length(); i++) {
						JSONObject text = texts.getJSONObject(i);
						if (!text.getString("value").isEmpty()) {
							String value = text.getString("value");
							allComments += value + ". ";
						}
					}
				}
				return Response.status(SimpaticoResourceUtils.serverOkCode).entity(allComments).build();
			} catch (Exception e) {
				Logger.getRootLogger().error(e.getMessage());
				return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
			}
		} else {
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, "eserviceId parameter is mandatory");
		}
	}
	
	private long getTotalSessions(String eservice, String init, String end) throws Exception {
		BoolQueryBuilder query = new BoolQueryBuilder();
		query.must(QueryBuilders.matchQuery("e-serviceID", eservice));
		query.must(QueryBuilders.matchQuery("event", "session_start"));
		query.must(QueryBuilders.rangeQuery("created").gte(init).lte(end));
		// Filter out our IDS
		query.mustNot(QueryBuilders.matchQuery("userID", ownIDs[0]));
		query.mustNot(QueryBuilders.matchQuery("userID", ownIDs[1]));
		
		SearchResponse responseES = ElasticSearchConnector.getInstance().searchES(SimpaticoProperties.elasticSearchSharedIndex, null, query, SimpaticoProperties.elasticSearchCreatedFieldName, SortOrder.ASC, 0);
		long hits = responseES.getHits().getTotalHits();
		
		return hits;
	}
}
