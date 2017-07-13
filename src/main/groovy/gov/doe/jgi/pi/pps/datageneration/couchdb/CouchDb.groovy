package gov.doe.jgi.pi.pps.datageneration.couchdb

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.Method
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.json.simple.JSONValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
//import groovyx.net.http.*

/**
 * Created by dscott on 7/7/2017.
 */
class CouchDb {

    static Logger logger = LoggerFactory.getLogger(CouchDb.class.name)

    final String url
    final String databaseName

    CouchDb(String url, String databaseName) {
        this.url = url.endsWith('/') ? url : url + '/'
        this.databaseName = databaseName
    }

    String getPath() {
        "${url}${databaseName}/"
    }

    JSONObject insert(String id, jsonObject) {
        logger.trace "${path}${id} inserting ${jsonObject}"
        HTTPBuilder http = new HTTPBuilder("${path}${id}")
        JSONObject jsonResponse = null
        http.request( Method.PUT, ContentType.JSON ) { req ->
            body = jsonObject?.toString() ?: '{}'
            response.success = { HttpResponseDecorator resp, Map respJson ->
                jsonResponse = new JSONObject(respJson)
            }
        }
        return jsonResponse
    }

    JSONArray queryViewByKey(designDocument, view, key) {
        logger.info "querying view for key \"${key}\""
        HTTPBuilder http = new HTTPBuilder("${path}_design/${designDocument}/_view/${view}")
        JSONArray results = new JSONArray()
        http.request( Method.GET, ContentType.JSON ) { req ->
            uri.query = [ key : JSONValue.toJSONString(key) ]
            response.success = { HttpResponseDecorator resp, Map respJson ->
                JSONObject respObj = new JSONObject(respJson)
                respObj['rows']?.each { Map map ->
                    results << new JSONObject(map)
                }
            }
        }
        results
    }
    
}
