package gov.doe.jgi.pi.pps.datageneration.util

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.runtime.ActivityInstance
import org.grails.web.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by dscott on 7/7/2017.
 */
class ProcessExecutionUtil {

    static Logger logger = LoggerFactory.getLogger(ProcessExecutionUtil.class.name)

    static Map<String,Object> activityProperties(ActivityInstance activityInstance) {
        Map props = [:]
        if (activityInstance) {
            props['activityId'] = activityInstance.activityId
            props['activityName'] = activityInstance.activityName
            props['activityType'] = activityInstance.activityType
            props['childActivityInstances'] = activityInstance.childActivityInstances
            props['childTransitionInstances'] = activityInstance.childTransitionInstances
            props['executionIds'] = activityInstance.executionIds
            props['id'] = activityInstance.id
            props['parentActivityInstanceId'] = activityInstance.parentActivityInstanceId
            props['processDefinitionId'] = activityInstance.processDefinitionId
            props['processInstanceId'] = activityInstance.processInstanceId
        }
        props
    }

    static JSONObject delegateToJson(DelegateExecution delegate) {
        JSONObject json = new JSONObject()
        json['activity-instance-id'] = delegate.activityInstanceId
        json['current-activity-id'] = delegate.currentActivityId
        json['current-activity-name'] = delegate.currentActivityName
        json['current-transition-id'] = delegate.currentTransitionId
        json['parent-activity-instance-id'] = delegate.parentActivityInstanceId
        json['parent-id'] = delegate.parentId
        json['process-business-key'] = delegate.processBusinessKey
        json['process-definition-id'] = delegate.processDefinitionId
        json['process-instance-id'] = delegate.processInstanceId
        json['tenant-id'] = delegate.tenantId
        json['is-canceled'] = new Boolean(delegate.isCanceled())
        JSONObject variables = new JSONObject()
        json['variables'] = variables
        delegate.variables?.each { String key, Object val ->
            variables[key] = val
        }
        json
    }

    static void logDelegateProperties(DelegateExecution delegate, String prefix = 'delegate-property') {
        logger.info "${prefix} activityInstanceId: ${delegate.activityInstanceId}"
        logger.info "${prefix} currentActivityId: ${delegate.currentActivityId}"
        logger.info "${prefix} currentActivityName: ${delegate.currentActivityName}"
        logger.info "${prefix} currentTransitionId: ${delegate.currentTransitionId}"
        logger.info "${prefix} parentActivityInstanceId: ${delegate.parentActivityInstanceId}"
        logger.info "${prefix} parentId: ${delegate.parentId}"
        logger.info "${prefix} processBusinessKey: ${delegate.processBusinessKey}"
        logger.info "${prefix} processDefinitionId: ${delegate.processDefinitionId}"
        logger.info "${prefix} processInstanceId: ${delegate.processInstanceId}"
        logger.info "${prefix} tenantId: ${delegate.tenantId}"
        logger.info "${prefix} isCanceled: ${delegate.isCanceled()}"
        logger.info "${prefix} delegate: ${delegate}"
        logger.info "${prefix} processInstance: ${delegate.processInstance}"
        logger.info "${prefix} superExecution: ${delegate.superExecution}"

        delegate.variables?.each {String key, Object val ->
            logger.info "${prefix} variable ${key}: ${val}"
        }
    }

}
