package gov.doe.jgi.pi.pps.datageneration.service

import gov.doe.jgi.pi.pps.datageneration.couchdb.CouchDb
import gov.doe.jgi.pi.pps.datageneration.util.ConfigurationUtil
import gov.doe.jgi.pi.pps.datageneration.util.OnDemandCache
import gov.doe.jgi.pi.pps.datageneration.util.ProcessExecutionUtil
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by dscott on 7/6/2017.
 */
class ProcessExecutionService implements JavaDelegate {

    final static String OUTPUT_IDS = 'output-ids'
    final static String INPUT_IDS = 'input-ids'

    Long lastTime
    Short timeSuffix = 0

    synchronized String getTimeStamp() {
        Long time = System.currentTimeMillis()
        if (time == lastTime) {
            timeSuffix++
        } else {
            timeSuffix = 0
        }
        lastTime = time
        "${time}:${timeSuffix}"
    }

    Logger logger = LoggerFactory.getLogger(ProcessExecutionService.class.name)

    static final  String PROCESS_SET = 'process-set'

    final OnDemandCache<CouchDb> cachedCouchDb = new OnDemandCache<>()

    CouchDb getCouchDb() {
        cachedCouchDb.fetch {
            String env = System.getProperty('camunda.env') ?: System.getenv('CAMUNDA_ENV') ?: 'development'
            ConfigObject configObject = ConfigurationUtil.loadConfig(env)
            String couchdbUrl = configObject.couchdbUrl
            String couchdbDatabaseName = configObject.couchdbDatabaseName
            new CouchDb(couchdbUrl,couchdbDatabaseName)
        }
    }

    List<String> processOutputIds(DelegateExecution delegate) {
        [UUID.randomUUID().toString()]
    }

    JSONObject lastTask(String processInstanceId) {
        JSONArray instanceTasks = couchDb.queryViewByKey('process','instance-tasks',processInstanceId)
        JSONObject task = new JSONObject()
        if (instanceTasks) {
            instanceTasks.sort { a, b ->
                List<String> aComps = a.'value'.'millisecond-stamp'.toString().split(':').toList()
                List<String> bComps = b.'value'.'millisecond-stamp'.toString().split(':').toList()
                Long aMillis = aComps[0].toLong()
                Long bMillis = bComps[0].toLong()
                int result = aMillis <=> bMillis
                if (!result) {
                    result = aComps[1].toShort() <=> bComps[1].toShort()
                }
                result
            }
            task.putAll((Map) instanceTasks.last().'value')
        }
        return task
    }

    void executeProcess(DelegateExecution delegate) {
        String processName = delegate.currentActivityName
        logger.warn "detected known process [${processName}]"
        String processSet = delegate.getVariable(PROCESS_SET)
        assert processSet
        logger.info "executing process [${processName} for process-set [${processSet}]]"
        JSONObject lastTask = lastTask(delegate.processInstanceId)
        logger.info "last task:\n${lastTask.toString(2)}"
        def inputTaskId = lastTask.'_id'
        def inputIds = lastTask[OUTPUT_IDS]

        List<String> outputIds = processOutputIds(delegate)

        JSONObject json = new JSONObject()
        json.putAll(ProcessExecutionUtil.delegateToJson(delegate))
        json.put(PROCESS_SET, delegate.getVariable(PROCESS_SET))

        JSONArray inputIdJson = new JSONArray()
        if (inputIds) {
            inputIdJson.addAll(inputIds)
        }
        JSONArray outputIdJson = new JSONArray(outputIds)
        json.put(OUTPUT_IDS,outputIdJson)
        json.put(INPUT_IDS,inputIdJson)
        json.put('input-task', inputTaskId)
        json.put('millisecond-stamp',timeStamp)

        CouchDb couchDb = getCouchDb()
        String id = delegate.activityInstanceId.replaceAll('_','-')
        logger.info "[${processName}] logging JSON to [${couchDb.path}${id}]"
        couchDb.insert(id, json)
    }

    void execute(DelegateExecution delegate) throws Exception {
        logger.info "############ process execution begin ############"
        String processSet = delegate.getVariable(PROCESS_SET)
        if (!processSet) {
            processSet = delegate.businessKey ?: UUID.randomUUID().toString()
            delegate.setVariable(PROCESS_SET, processSet)
        }
        ProcessExecutionUtil.logDelegateProperties(delegate, 'delegate-before-execution')
        executeProcess(delegate)
        logger.info "############ process execution end ##############\n";
    }

}
