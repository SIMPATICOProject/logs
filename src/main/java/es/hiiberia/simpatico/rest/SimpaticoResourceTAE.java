package es.hiiberia.simpatico.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;

import es.hiiberia.simpatico.utils.SimpaticoProperties;
import es.hiiberia.simpatico.utils.Utils;

@Path("/tae")
public class SimpaticoResourceTAE {

	private static String ES_INDEX = SimpaticoProperties.elasticSearchSharedIndex;
	private static String ES_TYPE =  "TAE";
	private static String ES_FIELD_SEARCH = SimpaticoProperties.elasticSearchFieldSearch;
	private static String FILE_LOG = SimpaticoProperties.simpaticoLog_Logs;
	private static String THIS_RESOURCE = "TAE";
	
	
	private static String USER_ID = "userID";
	private static String E_SERVICE_ID = "e-serviceID";
	private static String TIMESTAMP = "timestamp";
	private static String PARAGRAPH_ID = "paragraphID";
	private static String PHRASE_ID = "phraseID";
	private static String WORD_ID = "wordID";
	private static String SELECTED_TEXT = "selected_text";
	
	private static String EVENT = "event";
	private static String EVENT_PARAGRAPH_SIMPLIFICATION = "paragraph_simplification";
	private static String EVENT_PHRASE_SIMPLIFICATION = "phrase_simplification";
	private static String EVENT_WORD_SIMPLIFICATION = "word_simplification";
	private static String EVENT_FREETEXT_SIMPLIFICATION = "free_text_simplification";
	private static String EVENT_TAE = "tae_simplification_start";
	
	private static int numLinesPrintStackInternalError = 1;
	
	@GET
    @Path("/servicesimplification")
    @Produces(MediaType.APPLICATION_JSON)
    public Response service_simplification(@Context HttpServletRequest request, @Context UriInfo uriInfo) {
		// Need {userID: <string>, e-serviceID: <string>}
		
		return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverOkCode, "Implementing...");

    }
	
	@GET
    @Path("/find/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response find_force(@Context HttpServletRequest request, @Context UriInfo uriInfo) {

		try {
	    	Map<String, List<String>> queryParams = uriInfo.getQueryParameters();
	    	
	    	return SimpaticoResourceUtils.findRequest(request, queryParams, ES_INDEX, ES_TYPE, ES_FIELD_SEARCH, FILE_LOG, THIS_RESOURCE);
		} catch (Exception e) {
			SimpaticoResourceUtils.logException(e, FILE_LOG, THIS_RESOURCE);
    		return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
    	}
    }
	
    @GET
    @Path("/find/paragraph")
    @Produces(MediaType.APPLICATION_JSON)
    public Response find_paragraph(@Context HttpServletRequest request, @Context UriInfo uriInfo) {
    	
    	try {
    		// Copy map (it is unmodificable)
	    	Map<String, List<String>> queryParamsUnmodificable = uriInfo.getQueryParameters();
	    	Map<String, List<String>> queryParams = new MultivaluedHashMap<>();
	    	
	    	if (queryParamsUnmodificable != null)
	    		queryParams.putAll(queryParamsUnmodificable);
	    	
	    	List<String> words = queryParams.get(SimpaticoResourceUtils.wordsParam);
			if (words == null) {
				words = new ArrayList<>();
				words.add(EVENT_PARAGRAPH_SIMPLIFICATION);
				queryParams.put(SimpaticoResourceUtils.wordsParam, words);
			} else {
				words.add(EVENT_PARAGRAPH_SIMPLIFICATION);
			}
			
	    	return SimpaticoResourceUtils.findRequest(request, queryParams, ES_INDEX, ES_TYPE, ES_FIELD_SEARCH, FILE_LOG, THIS_RESOURCE);
    	} catch (Exception e) {
			SimpaticoResourceUtils.logException(e, FILE_LOG, THIS_RESOURCE);
    		return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
    	}
    }
    
    @GET
    @Path("/find/phrase")
    @Produces(MediaType.APPLICATION_JSON)
    public Response find_phrase(@Context HttpServletRequest request, @Context UriInfo uriInfo) {

    	try {
    		// Copy map (it is unmodificable)
	    	Map<String, List<String>> queryParamsUnmodificable = uriInfo.getQueryParameters();
	    	Map<String, List<String>> queryParams = new MultivaluedHashMap<>();
	    	
	    	if (queryParamsUnmodificable != null)
	    		queryParams.putAll(queryParamsUnmodificable);
	    	List<String> words = queryParams.get(SimpaticoResourceUtils.wordsParam);
			if (words == null) {
				words = new ArrayList<>();
				words.add(EVENT_PHRASE_SIMPLIFICATION);
				queryParams.put(SimpaticoResourceUtils.wordsParam, words);
			} else {
				words.add(EVENT_PHRASE_SIMPLIFICATION);
			}
	    	
	    	return SimpaticoResourceUtils.findRequest(request, queryParams, ES_INDEX, ES_TYPE, ES_FIELD_SEARCH, FILE_LOG, THIS_RESOURCE);
    	} catch (Exception e) {
			SimpaticoResourceUtils.logException(e, FILE_LOG, THIS_RESOURCE);
    		return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
    	}
    }
    
    @GET
    @Path("/find/word")
    @Produces(MediaType.APPLICATION_JSON)
    public Response find_word(@Context HttpServletRequest request, @Context UriInfo uriInfo) {

    	try {
    		// Copy map (it is unmodificable)
	    	Map<String, List<String>> queryParamsUnmodificable = uriInfo.getQueryParameters();
	    	Map<String, List<String>> queryParams = new MultivaluedHashMap<>();
	    	
	    	if (queryParamsUnmodificable != null)
	    		queryParams.putAll(queryParamsUnmodificable);
	    	List<String> words = queryParams.get(SimpaticoResourceUtils.wordsParam);
			if (words == null) {
				words = new ArrayList<>();
				words.add(EVENT_WORD_SIMPLIFICATION);
				queryParams.put(SimpaticoResourceUtils.wordsParam, words);
			} else {
				words.add(EVENT_WORD_SIMPLIFICATION);
			}
	    	
	    	return SimpaticoResourceUtils.findRequest(request, queryParams, ES_INDEX, ES_TYPE, ES_FIELD_SEARCH, FILE_LOG, THIS_RESOURCE);
    	} catch (Exception e) {
			SimpaticoResourceUtils.logException(e, FILE_LOG, THIS_RESOURCE);
    		return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
    	}
    }
    
    @GET
    @Path("/find/freetext")
    @Produces(MediaType.APPLICATION_JSON)
    public Response find_freetext(@Context HttpServletRequest request, @Context UriInfo uriInfo) {

    	try {
    		// Copy map (it is unmodificable) 
	    	Map<String, List<String>> queryParamsUnmodificable = uriInfo.getQueryParameters();
	    	Map<String, List<String>> queryParams = new MultivaluedHashMap<>();
	    	
	    	if (queryParamsUnmodificable != null)
	    		queryParams.putAll(queryParamsUnmodificable);
	    	List<String> words = queryParams.get(SimpaticoResourceUtils.wordsParam);
			if (words == null) {
				words = new ArrayList<>();
				words.add(EVENT_FREETEXT_SIMPLIFICATION);
				queryParams.put(SimpaticoResourceUtils.wordsParam, words);
			} else {
				words.add(EVENT_FREETEXT_SIMPLIFICATION);
			}
	    	
	    	return SimpaticoResourceUtils.findRequest(request, queryParams, ES_INDEX, ES_TYPE, ES_FIELD_SEARCH, FILE_LOG, THIS_RESOURCE);
    	} catch (Exception e) {
			SimpaticoResourceUtils.logException(e, FILE_LOG, THIS_RESOURCE);
    		return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
    	}
    }
        
    @POST
    @Path("/insert/paragraph")
    @Produces(MediaType.APPLICATION_JSON)
    public Response insert_paragraph(@Context HttpServletRequest request, String postData) {
    	/* JSON: {userID: <string>, e-serviceID: <string>, paragraphID: <string>}   	event: paragraph_simplification
				 {userID: <string>, e-serviceID: <string>, phraseID: <string>}			event: phrase_simplification
				 {userID: <string>, e-serviceID: <string>, wordID: <string>}			event: word_simplification
				 {userID: <string>, e-serviceID: <string>, selected_text: <string>}		event: free_text_simplification
		*/
    	
    	boolean badRequest = true;
    	
    	try {
	    	// Check parameters and generate event attribute
	    	JSONObject jsonObject = Utils.createJSONObjectIfValid(postData);
	    	if (jsonObject != null) {
	    		if (jsonObject.has(USER_ID) && jsonObject.has(E_SERVICE_ID) && jsonObject.has(PARAGRAPH_ID)) {
	    			badRequest = false;
    				jsonObject.put(EVENT, EVENT_PARAGRAPH_SIMPLIFICATION);
	    		}
	    	}
	    	
	    	if (!badRequest) {    		
	    		return SimpaticoResourceUtils.insertRequest(request, jsonObject.toString(), ES_INDEX, ES_TYPE, ES_FIELD_SEARCH, FILE_LOG, THIS_RESOURCE);
	    	}
	    	
	    	Logger.getLogger(FILE_LOG).warn("[BAD REQUEST] Insert document. IP Remote: " + request.getRemoteAddr() + ". IP Header Real: " + SimpaticoResourceUtils.getRealIPHeader(request) + ". POST data: " + postData);
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, SimpaticoResourceUtils.badPOSTRequestResponse);
			
    	} catch (Exception e) {
			SimpaticoResourceUtils.logException(e, FILE_LOG, THIS_RESOURCE);
    		return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
    	}
    }
    
    @POST
    @Path("/insert/phrase")
    @Produces(MediaType.APPLICATION_JSON)
    public Response insert_phrase(@Context HttpServletRequest request, String postData) {
    	/* JSON: {userID: <string>, e-serviceID: <string>, paragraphID: <string>}   	event: paragraph_simplification
				 {userID: <string>, e-serviceID: <string>, phraseID: <string>}			event: phrase_simplification
				 {userID: <string>, e-serviceID: <string>, wordID: <string>}			event: word_simplification
				 {userID: <string>, e-serviceID: <string>, selected_text: <string>}		event: free_text_simplification
		*/
    	
    	boolean badRequest = true;
    	
    	try {
	    	// Check parameters and generate event attribute
	    	JSONObject jsonObject = Utils.createJSONObjectIfValid(postData);
	    	if (jsonObject != null) {
	    		if (jsonObject.has(USER_ID) && jsonObject.has(E_SERVICE_ID) && jsonObject.has(PHRASE_ID)) {
	    			badRequest = false;
    				jsonObject.put(EVENT, EVENT_PHRASE_SIMPLIFICATION);
	    		}
	    	}
	    	
	    	if (!badRequest) {    		
	    		return SimpaticoResourceUtils.insertRequest(request, jsonObject.toString(), ES_INDEX, ES_TYPE, ES_FIELD_SEARCH, FILE_LOG, THIS_RESOURCE);
	    	}
	    	
	    	Logger.getLogger(FILE_LOG).warn("[BAD REQUEST] Insert document. IP Remote: " + request.getRemoteAddr() + ". IP Header Real: " + SimpaticoResourceUtils.getRealIPHeader(request) + ". POST data: " + postData);
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, SimpaticoResourceUtils.badPOSTRequestResponse);
			
    	} catch (Exception e) {
			SimpaticoResourceUtils.logException(e, FILE_LOG, THIS_RESOURCE);
    		return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
    	}
    }
    
    @POST
    @Path("/insert/word")
    @Produces(MediaType.APPLICATION_JSON)
    public Response insert_word(@Context HttpServletRequest request, String postData) {
    	/* JSON: {userID: <string>, e-serviceID: <string>, paragraphID: <string>}   	event: paragraph_simplification
				 {userID: <string>, e-serviceID: <string>, phraseID: <string>}			event: phrase_simplification
				 {userID: <string>, e-serviceID: <string>, wordID: <string>}			event: word_simplification
				 {userID: <string>, e-serviceID: <string>, selected_text: <string>}		event: free_text_simplification
		*/
    	
    	boolean badRequest = true;
    	
    	try {
	    	// Check parameters and generate event attribute
	    	JSONObject jsonObject = Utils.createJSONObjectIfValid(postData);
	    	if (jsonObject != null) {
	    		if (jsonObject.has(USER_ID) && jsonObject.has(E_SERVICE_ID) && jsonObject.has(WORD_ID)) {
	    			badRequest = false;
    				jsonObject.put(EVENT, EVENT_WORD_SIMPLIFICATION);
	    		}
	    	}
	    	
	    	if (!badRequest) {    		
	    		return SimpaticoResourceUtils.insertRequest(request, jsonObject.toString(), ES_INDEX, ES_TYPE, ES_FIELD_SEARCH, FILE_LOG, THIS_RESOURCE);
	    	}
	    	
	    	Logger.getLogger(FILE_LOG).warn("[BAD REQUEST] Insert document. IP Remote: " + request.getRemoteAddr() + ". IP Header Real: " + SimpaticoResourceUtils.getRealIPHeader(request) + ". POST data: " + postData);
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, SimpaticoResourceUtils.badPOSTRequestResponse);
			
    	} catch (Exception e) {
			SimpaticoResourceUtils.logException(e, FILE_LOG, THIS_RESOURCE);
    		return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
    	}
    }
    
    @POST
    @Path("/insert/freetext")
    @Produces(MediaType.APPLICATION_JSON)
    public Response insert_freetext(@Context HttpServletRequest request, String postData) {
    	/* JSON: {userID: <string>, e-serviceID: <string>, paragraphID: <string>}   	event: paragraph_simplification
				 {userID: <string>, e-serviceID: <string>, phraseID: <string>}			event: phrase_simplification
				 {userID: <string>, e-serviceID: <string>, wordID: <string>}			event: word_simplification
				 {userID: <string>, e-serviceID: <string>, selected_text: <string>}		event: free_text_simplification
		*/
    	
    	boolean badRequest = true;
    	
    	try {
	    	// Check parameters and generate event attribute
	    	JSONObject jsonObject = Utils.createJSONObjectIfValid(postData);
	    	if (jsonObject != null) {
	    		if (jsonObject.has(USER_ID) && jsonObject.has(E_SERVICE_ID) && jsonObject.has(SELECTED_TEXT)) {
	    			badRequest = false;
    				jsonObject.put(EVENT, EVENT_FREETEXT_SIMPLIFICATION);
	    		}
	    	}
	    	
	    	if (!badRequest) {    		
	    		return SimpaticoResourceUtils.insertRequest(request, jsonObject.toString(), ES_INDEX, ES_TYPE, ES_FIELD_SEARCH, FILE_LOG, THIS_RESOURCE);
	    	}
	    	
	    	Logger.getLogger(FILE_LOG).warn("[BAD REQUEST] Insert document. IP Remote: " + request.getRemoteAddr() + ". IP Header Real: " + SimpaticoResourceUtils.getRealIPHeader(request) + ". POST data: " + postData);
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, SimpaticoResourceUtils.badPOSTRequestResponse);
			
    	} catch (Exception e) {
			SimpaticoResourceUtils.logException(e, FILE_LOG, THIS_RESOURCE);
    		return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
    	}
    }
    
    /**
     * Force insert a json document. The postData must be a valid json (we store the full json)
     * @param request
     * @param postData
     * @return
     */
    @POST
    @Path("/insert/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response insert_force(@Context HttpServletRequest request, String postData) {
    		boolean badRequest = true;
	    	try {
	    		// Check parameters and generate event attribute
		    	JSONObject jsonObject = Utils.createJSONObjectIfValid(postData);
		    	if (jsonObject != null) {
		    		if (jsonObject.has(USER_ID) && jsonObject.has(E_SERVICE_ID) && jsonObject.has(TIMESTAMP)) {
	    				badRequest = false;
	    				jsonObject.put(EVENT, EVENT_TAE);
		    		}
		    	}
		    	
		    	if (!badRequest) {    		
		    		return SimpaticoResourceUtils.insertRequest(request, jsonObject.toString(), ES_INDEX, ES_TYPE, ES_FIELD_SEARCH, FILE_LOG, THIS_RESOURCE);
		    	}
		    	
		    	Logger.getLogger(FILE_LOG).warn("[BAD REQUEST] Insert document. IP Remote: " + request.getRemoteAddr() + ". IP Header Real: " + SimpaticoResourceUtils.getRealIPHeader(request) + ". POST data: " + postData);
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, SimpaticoResourceUtils.badPOSTRequestResponse);
				
	    	} catch (Exception e) {
			SimpaticoResourceUtils.logException(e, FILE_LOG, THIS_RESOURCE);
	    		return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
	    	}
    }    
   
    @PUT
    @Path("/update/paragraph")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update_paragraph(@Context HttpServletRequest request, String postData) {
    	/* JSON: {userID: <string>, e-serviceID: <string>, paragraphID: <string>}   	event: paragraph_simplification
				 {userID: <string>, e-serviceID: <string>, phraseID: <string>}			event: phrase_simplification
				 {userID: <string>, e-serviceID: <string>, wordID: <string>}			event: word_simplification
				 {userID: <string>, e-serviceID: <string>, selected_text: <string>}		event: free_text_simplification
		*/
    	
    	boolean badRequest = true;
    	
    	try {
	    	// Check parameters and generate event attribute
	    	JSONObject jsonObject = Utils.createJSONObjectIfValid(postData);
	    	if (jsonObject != null) {
	    		if (jsonObject.has(USER_ID) && jsonObject.has(E_SERVICE_ID) && jsonObject.has(PARAGRAPH_ID)) {
	    			badRequest = false;
    				jsonObject.put(EVENT, EVENT_PARAGRAPH_SIMPLIFICATION);
	    		}
	    	}
	    	
	    	if (!badRequest) {    		
	    		return SimpaticoResourceUtils.updateRequest(request, jsonObject.toString(), ES_INDEX, ES_TYPE, ES_FIELD_SEARCH, FILE_LOG, THIS_RESOURCE);
	    	}
	    	
	    	Logger.getLogger(FILE_LOG).warn("[BAD REQUEST] Insert document. IP Remote: " + request.getRemoteAddr() + ". IP Header Real: " + SimpaticoResourceUtils.getRealIPHeader(request) + ". POST data: " + postData);
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, SimpaticoResourceUtils.badPOSTRequestResponse);
			
    	} catch (Exception e) {
			SimpaticoResourceUtils.logException(e, FILE_LOG, THIS_RESOURCE);
    		return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
    	}
    }
    
    @PUT
    @Path("/update/phrase")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update_phrase(@Context HttpServletRequest request, String postData) {
    	/* JSON: {userID: <string>, e-serviceID: <string>, paragraphID: <string>}   	event: paragraph_simplification
				 {userID: <string>, e-serviceID: <string>, phraseID: <string>}			event: phrase_simplification
				 {userID: <string>, e-serviceID: <string>, wordID: <string>}			event: word_simplification
				 {userID: <string>, e-serviceID: <string>, selected_text: <string>}		event: free_text_simplification
		*/
    	
    	boolean badRequest = true;
    	
    	try {
	    	// Check parameters and generate event attribute
	    	JSONObject jsonObject = Utils.createJSONObjectIfValid(postData);
	    	if (jsonObject != null) {
	    		if (jsonObject.has(USER_ID) && jsonObject.has(E_SERVICE_ID) && jsonObject.has(PHRASE_ID)) {
	    			badRequest = false;
    				jsonObject.put(EVENT, EVENT_PHRASE_SIMPLIFICATION);
	    		}
	    	}
	    	
	    	if (!badRequest) {    		
	    		return SimpaticoResourceUtils.updateRequest(request, jsonObject.toString(), ES_INDEX, ES_TYPE, ES_FIELD_SEARCH, FILE_LOG, THIS_RESOURCE);
	    	}
	    	
	    	Logger.getLogger(FILE_LOG).warn("[BAD REQUEST] Insert document. IP Remote: " + request.getRemoteAddr() + ". IP Header Real: " + SimpaticoResourceUtils.getRealIPHeader(request) + ". POST data: " + postData);
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, SimpaticoResourceUtils.badPOSTRequestResponse);
			
    	} catch (Exception e) {
			SimpaticoResourceUtils.logException(e, FILE_LOG, THIS_RESOURCE);
    		return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
    	}
    }
    
    @PUT
    @Path("/update/word")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update_word(@Context HttpServletRequest request, String postData) {
    	/* JSON: {userID: <string>, e-serviceID: <string>, paragraphID: <string>}   	event: paragraph_simplification
				 {userID: <string>, e-serviceID: <string>, phraseID: <string>}			event: phrase_simplification
				 {userID: <string>, e-serviceID: <string>, wordID: <string>}			event: word_simplification
				 {userID: <string>, e-serviceID: <string>, selected_text: <string>}		event: free_text_simplification
		*/
    	
    	boolean badRequest = true;
    	
    	try {
	    	// Check parameters and generate event attribute
	    	JSONObject jsonObject = Utils.createJSONObjectIfValid(postData);
	    	if (jsonObject != null) {
	    		if (jsonObject.has(USER_ID) && jsonObject.has(E_SERVICE_ID) && jsonObject.has(WORD_ID)) {
	    			badRequest = false;
    				jsonObject.put(EVENT, EVENT_WORD_SIMPLIFICATION);
	    		}
	    	}
	    	
	    	if (!badRequest) {    		
	    		return SimpaticoResourceUtils.updateRequest(request, jsonObject.toString(), ES_INDEX, ES_TYPE, ES_FIELD_SEARCH, FILE_LOG, THIS_RESOURCE);
	    	}
	    	
	    	Logger.getLogger(FILE_LOG).warn("[BAD REQUEST] Insert document. IP Remote: " + request.getRemoteAddr() + ". IP Header Real: " + SimpaticoResourceUtils.getRealIPHeader(request) + ". POST data: " + postData);
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, SimpaticoResourceUtils.badPOSTRequestResponse);
			
    	} catch (Exception e) {
			SimpaticoResourceUtils.logException(e, FILE_LOG, THIS_RESOURCE);
    		return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
    	}
    }
    
    @PUT
    @Path("/update/freetext")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update_freetext(@Context HttpServletRequest request, String postData) {
    	/* JSON: {userID: <string>, e-serviceID: <string>, paragraphID: <string>}   	event: paragraph_simplification
				 {userID: <string>, e-serviceID: <string>, phraseID: <string>}			event: phrase_simplification
				 {userID: <string>, e-serviceID: <string>, wordID: <string>}			event: word_simplification
				 {userID: <string>, e-serviceID: <string>, selected_text: <string>}		event: free_text_simplification
		*/
    	
    	boolean badRequest = true;
    	
    	try {
	    	// Check parameters and generate event attribute
	    	JSONObject jsonObject = Utils.createJSONObjectIfValid(postData);
	    	if (jsonObject != null) {
	    		if (jsonObject.has(USER_ID) && jsonObject.has(E_SERVICE_ID) && jsonObject.has(SELECTED_TEXT)) {
	    			badRequest = false;
    				jsonObject.put(EVENT, EVENT_FREETEXT_SIMPLIFICATION);
	    		}
	    	}
	    	
	    	if (!badRequest) {    		
	    		return SimpaticoResourceUtils.updateRequest(request, jsonObject.toString(), ES_INDEX, ES_TYPE, ES_FIELD_SEARCH, FILE_LOG, THIS_RESOURCE);
	    	}
	    	
	    	Logger.getLogger(FILE_LOG).warn("[BAD REQUEST] Insert document. IP Remote: " + request.getRemoteAddr() + ". IP Header Real: " + SimpaticoResourceUtils.getRealIPHeader(request) + ". POST data: " + postData);
			return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverBadRequestCode, SimpaticoResourceUtils.badPOSTRequestResponse);
			
    	} catch (Exception e) {
			SimpaticoResourceUtils.logException(e, FILE_LOG, THIS_RESOURCE);
    		return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
    	}
    }
    
    /**
     * Force update a json document. The postData must be a valid json and format: {"id": "<id to update>", "content": "<json>"}. 
     * If json has the same keys that the old document, the document updates.
     * If the document does not exists, it is created.
     * @param request
     * @param postData
     * @return
     */
    @PUT
    @Path("/update/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update_force(@Context HttpServletRequest request, String postData) {
    	
    	// No Check params
    	try {

			return SimpaticoResourceUtils.updateRequest(request, postData, ES_INDEX, ES_TYPE, ES_FIELD_SEARCH, FILE_LOG, THIS_RESOURCE);
    	} catch (Exception e) {
			SimpaticoResourceUtils.logException(e, FILE_LOG, THIS_RESOURCE);
    		return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
    	}
    }
    
    /**
     * Remove a json document. The postData must be a valid json and format: {"id": "<id to eliminate>"}
     * @param request
     * @param postData
     * @return
     */
    @DELETE
    @Path("/remove")
    @Produces(MediaType.APPLICATION_JSON)
    public Response remove(@Context HttpServletRequest request, String postData) {
    	try {
			return SimpaticoResourceUtils.removeRequest(request, postData, ES_INDEX, ES_TYPE, ES_FIELD_SEARCH, FILE_LOG, THIS_RESOURCE);
    	} catch (Exception e) {
			SimpaticoResourceUtils.logException(e, FILE_LOG, THIS_RESOURCE);
    		return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverInternalServerErrorCode, SimpaticoResourceUtils.internalErrorResponse + ": " + SimpaticoResourceUtils.getInternalErrorMessageWithStackTrace(e, numLinesPrintStackInternalError));
    	}
    }
       
    
    /** Test Method **/
    @GET
	@Path("/test/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response testGet(@Context HttpServletRequest request) {
    	Logger.getLogger(FILE_LOG).warn("[TEST] IP Remote: " + request.getRemoteAddr() + ". IP Header Real: " + SimpaticoResourceUtils.getRealIPHeader(request));
    	return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverOkCode, "Welcome to SIMPATICO " + THIS_RESOURCE + " API! Method: GET");
	}
    
    @POST
	@Path("/test/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response testPost(@Context HttpServletRequest request) {
    	Logger.getLogger(FILE_LOG).warn("[TEST] IP Remote: " + request.getRemoteAddr() + ". IP Header Real: " + SimpaticoResourceUtils.getRealIPHeader(request));
    	return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverOkCode, "Welcome to SIMPATICO " + THIS_RESOURCE + " API! Method: POST");
	}
    
    @PUT
	@Path("/test/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response testPut(@Context HttpServletRequest request) {
    	Logger.getLogger(FILE_LOG).warn("[TEST] IP Remote: " + request.getRemoteAddr() + ". IP Header Real: " + SimpaticoResourceUtils.getRealIPHeader(request));
    	return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverOkCode, "Welcome to SIMPATICO " + THIS_RESOURCE + " API! Method: PUT");
	}
    
    @DELETE
   	@Path("/test/")
   	@Produces(MediaType.APPLICATION_JSON)
   	public Response testDelete(@Context HttpServletRequest request) {
    	Logger.getLogger(FILE_LOG).warn("[TEST] IP Remote: " + request.getRemoteAddr() + ". IP Header Real: " + SimpaticoResourceUtils.getRealIPHeader(request));
       	return SimpaticoResourceUtils.createMessageResponse(SimpaticoResourceUtils.serverOkCode, "Welcome to SIMPATICO " + THIS_RESOURCE + " API! Method: DELETE");
   	}
}
