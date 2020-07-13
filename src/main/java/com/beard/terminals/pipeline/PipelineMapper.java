package com.beard.terminals.pipeline;

import com.beard.terminals.pipeline.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * /**
 * Created by angry_beard on 2020/7/13.
 */
@Component
public class PipelineMapper implements ApplicationContextAware, InitializingBean {

    protected static final int STARTING_PROCESS_ORDER = 0;

    private static final String UPDATED_AT_FIELD_NAME = "updatedAt";

    private static final String ID_FIELD_NAME = "id";

    private static final String CLASS_KEY = "class";

    private static final String VALUE_KEY = "value";

    private static final String SELECT_BY_EXAMPLE_WITH_BLOBS_METHOD_NAME = "selectByExampleWithBLOBs";

    private static final String UPDATE_BY_EXAMPLE_SELECTIVE_METHOD_NAME = "updateByExampleSelective";

    private static final String ORDER_FIELD_NAME = "order";

    private static final String SERIAL_FIELD_NAME = "serial";

    private static final String PARAM_FIELD_NAME = "param";

    private static final String EXEC_STATUS_FIELD_NAME = "execStatus";

    private static final boolean FORCE_ACCESS = true;

    private static final String NAME_FIELD_NAME = "name";

    private static final String REQUEST_PARAM_FIELD_NAME = "requestParam";

    private static final String TASK_SERIAL_FIELD_NAME = "taskSerial";

    private static final String AND_EXEC_STATUS_IN_METHOD_NAME = "andExecStatusIn";

    private static final String AND_SERIAL_EQUAL_TO_METHOD_NAME = "andSerialEqualTo";

    private static final String AND_TASK_SERIAL_EQUAL_TO_METHOD_NAME = "andTaskSerialEqualTo";

    private static final String CREATE_CRITERIA_METHOD_NAME = "createCriteria";

    private static final String UPDATE_BY_PRIMARY_KEY_SELECTIVE_METHOD_NAME = "updateByPrimaryKeySelective";

    private static final String INSERT_SELECTIVE_METHOD_NAME = "insertSelective";

    private static final List<String> PAUSED_EXEC_STATUSES = Lists.newArrayList(PipelineExecStatus.INIT.toString(), PipelineExecStatus.PAUSED.toString());

    public static final String SELECT_BY_EXAMPLE_METHOD_NAME = "selectByExample";

    private final String taskMapperClassName;

    private final String processMapperClassName;

    private final DataSourceTransactionManager txManager;

    private ApplicationContext applicationContext;

    private Class<?> taskDomainClass;

    private Class<?> taskExampleClass;

    private Class<?> processDomainClass;

    private Class<?> processExampleClass;

    private Object taskMapper;

    private Object processMapper;

    /**
     * 初始化 PipelineMapper，两个参数用于定位要使用的数据库表
     * 对应的 domain 类，对应的 Mapper 类，以及对应的 Example 类
     * 要求 mapper 包和 domain 包要放同一个目录，并且三个类的前缀要相同
     * <p>
     * 样例
     * Domain 类名必须为 xxx.domain.Admin
     * Example 类名必须为 xxx.domain.AdminExample
     * Mapper 类名必须为 xxx.mapper.AdminMapper
     *
     * @param txManager 事务管理器
     */
    public PipelineMapper(DataSourceTransactionManager txManager, MapperNameConfig mapperNameConfig) {
        this.taskMapperClassName = mapperNameConfig.getTaskMapperClassName();
        this.processMapperClassName = mapperNameConfig.getProcessMapperClassName();
        this.txManager = txManager;
    }

    private static void setProcessName(Object processDomain, String startingProcessName) throws IllegalAccessException {
        FieldUtils.writeDeclaredField(processDomain, NAME_FIELD_NAME, startingProcessName, FORCE_ACCESS);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String taskDomainClassName = getDomainClassName(taskMapperClassName);
        taskDomainClass = Class.forName(taskDomainClassName);

        String taskExampleClassName = getExampleClassName(taskDomainClassName);
        taskExampleClass = Class.forName(taskExampleClassName);

        String processDomainClassName = getDomainClassName(processMapperClassName);
        processDomainClass = Class.forName(processDomainClassName);

        String processExampleClassName = getExampleClassName(processDomainClassName);
        processExampleClass = Class.forName(processExampleClassName);

        this.taskMapper = applicationContext.getBean(getBeanName(taskMapperClassName));

        this.processMapper = applicationContext.getBean(getBeanName(processMapperClassName));
    }

    private String getBeanName(String mapperClassName) {
        return StringUtils.uncapitalize(mapperClassName.substring(taskMapperClassName.lastIndexOf(".") + 1));
    }

    private String getExampleClassName(String domainClassName) {
        return domainClassName + "Example";
    }

    private String getDomainClassName(String mapperClassName) {
        int indexOfLastDot = mapperClassName.lastIndexOf(".");
        int lastIndexOfMapper = mapperClassName.lastIndexOf("Mapper");
        String domainName = mapperClassName.substring(indexOfLastDot, lastIndexOfMapper);

        int indexOfMapperPackage = mapperClassName.lastIndexOf("mapper");
        return mapperClassName.substring(0, indexOfMapperPackage) + "domain" + domainName;
    }

    public Integer insertTaskAndStartingProcess(String serial, String name, PipelineProcess startingProcess, Object requestParam) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus transaction = txManager.getTransaction(definition);
        try {
            insertPipelineTask(serial, requestParam, name);
            Integer processId = insertStartingProcess(serial, startingProcess, STARTING_PROCESS_ORDER);
            txManager.commit(transaction);
            return processId;
        } catch (Exception e) {
            txManager.rollback(transaction);
            throw new RuntimeException(e);
        }
    }

    private Integer insertStartingProcess(String taskSerial, PipelineProcess process, int order) throws ReflectiveOperationException {
        Object domain = processDomainClass.newInstance();
        setProcessTaskSerial(domain, taskSerial);
        setProcessExecStatus(domain, PipelineExecStatus.INIT);
        setProcessName(domain, process.getName());
        setProcessOrder(domain, order);
        setProcessClassName(domain, getFormalClassName(process));

        invokeMapperInsertSelective(processMapper, domain);
        return (Integer) MethodUtils.invokeMethod(domain, "getId");
    }

    private Integer insertNextProcess(String taskSerial, PipelineProcess process, int order, Object param) throws ReflectiveOperationException {
        Object domain = processDomainClass.newInstance();
        setProcessTaskSerial(domain, taskSerial);
        setProcessExecStatus(domain, PipelineExecStatus.INIT);
        setProcessName(domain, process.getName());
        setProcessOrder(domain, order);
        setProcessClassName(domain, getFormalClassName(process));


        if (param != null) {
            setProcessParam(domain, param);
        }

        invokeMapperInsertSelective(processMapper, domain);
        return (Integer) MethodUtils.invokeMethod(domain, "getId");
    }


    /* 移除 Spring CGLib 生成的类名后缀 */
    private String getFormalClassName(PipelineProcess process) {
        String canonicalName = process.getClass().getCanonicalName();
        String[] split = StringUtils.split(canonicalName, "$");
        return split[0];
    }

    private void setProcessClassName(Object processDomain, String classCanonicalName) throws IllegalAccessException {
        FieldUtils.writeDeclaredField(processDomain, "className", classCanonicalName, FORCE_ACCESS);
    }

    private void setProcessOrder(Object processDomain, int order) throws IllegalAccessException {
        FieldUtils.writeDeclaredField(processDomain, ORDER_FIELD_NAME, order, FORCE_ACCESS);
    }

    private void setProcessExecStatus(Object processDomain, PipelineExecStatus execStatus) throws IllegalAccessException {
        FieldUtils.writeDeclaredField(processDomain, EXEC_STATUS_FIELD_NAME, execStatus.toString(), FORCE_ACCESS);
    }

    private void setProcessTaskSerial(Object processDomain, String serial) throws IllegalAccessException {
        FieldUtils.writeDeclaredField(processDomain, TASK_SERIAL_FIELD_NAME, serial, FORCE_ACCESS);
    }

    @SuppressWarnings("unused")
    public void updateTaskRequestParam(String serial, Object requestParam) {
        try {
            Object taskDomain = taskDomainClass.newInstance();
            setTaskRequestParam(taskDomain, requestParam);

            Object taskExample = taskExampleClass.newInstance();
            Object criteria = createCriteria(taskExample);
            MethodUtils.invokeMethod(criteria, AND_SERIAL_EQUAL_TO_METHOD_NAME, serial);

            updateTaskByExampleSelective(taskDomain, taskExample);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertPipelineTask(String serial, Object requestParam, String name) throws ReflectiveOperationException {
        Object taskDomain = taskDomainClass.newInstance();

        setTaskSerial(taskDomain, serial);
        setTaskExecStatus(taskDomain, PipelineExecStatus.INIT);
        setTaskName(taskDomain, name);
        setTaskRequestParam(taskDomain, requestParam);

        invokeMapperInsertSelective(taskMapper, taskDomain);
    }

    private void invokeMapperInsertSelective(Object mapper, Object domain) throws ReflectiveOperationException {
        MethodUtils.invokeMethod(mapper, INSERT_SELECTIVE_METHOD_NAME, domain);
    }

    private void setTaskRequestParam(Object task, Object requestParam) throws IllegalAccessException {
        Map<String, Object> paramObject = new HashMap<>();
        paramObject.put(CLASS_KEY, requestParam.getClass().getName());
        paramObject.put(VALUE_KEY, JsonUtils.toJSONString(requestParam));
        FieldUtils.writeDeclaredField(task, REQUEST_PARAM_FIELD_NAME, JsonUtils.toJSONString(paramObject), FORCE_ACCESS);
    }

    private void setTaskName(Object task, String name) throws IllegalAccessException {
        FieldUtils.writeDeclaredField(task, NAME_FIELD_NAME, name, FORCE_ACCESS);
    }

    private void setTaskExecStatus(Object task, PipelineExecStatus execStatus) throws IllegalAccessException {
        FieldUtils.writeDeclaredField(task, EXEC_STATUS_FIELD_NAME, execStatus.toString(), FORCE_ACCESS);
    }

    private void setTaskSerial(Object task, String serial) throws IllegalAccessException {
        FieldUtils.writeDeclaredField(task, SERIAL_FIELD_NAME, serial, FORCE_ACCESS);
    }

    /**
     * 从数据库中拿出 name, headProcess，requestParam 等数据，然后再重建 pipeline 任务
     * 先找到 Task，然后再找到被 PAUSED 的 process，从 Spring 容器中找 process 实例
     */
    public PipelineTask restoreTask(String serial) {
        try {
            Object taskDomain = selectPausedTaskBySerial(serial);
            String taskName = getName(taskDomain);
            Object requestParam = getTaskRequestParam(taskDomain);

            Object processDomain = selectPausedProcessBySerial(serial);
            Integer processId = (Integer) FieldUtils.readDeclaredField(processDomain, ID_FIELD_NAME, FORCE_ACCESS);
            Integer processOrder = (Integer) FieldUtils.readDeclaredField(processDomain, ORDER_FIELD_NAME, FORCE_ACCESS);
            PipelineProcess startingProcess = getStartingProcessFromApplicationContext(processDomain);
            Object resultParam = getResultParam(processDomain);

            return PipelineTask.builder()
                    .execStatus(PipelineExecStatus.PAUSED)
                    .pipelineMapper(this)
                    .name(taskName)
                    .serial(serial)
                    .requestParam(requestParam)
                    .currentProcessId(processId)
                    .currentProcessOrder(processOrder)
                    .startingProcess(startingProcess)
                    .resultParam(resultParam)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object getResultParam(Object processDomain) throws IllegalAccessException, ClassNotFoundException {
        Object resultParam = null;
        Field paramField = FieldUtils.getDeclaredField(processDomainClass, PARAM_FIELD_NAME, FORCE_ACCESS);
        if (paramField != null) {
            String paramJson = (String) FieldUtils.readDeclaredField(processDomain, PARAM_FIELD_NAME, FORCE_ACCESS);
            if (StringUtils.isNotBlank(paramJson)) {
                resultParam = deserializeFromJsonWithTypeInfo(paramJson);
            }
        }
        return resultParam;
    }

    private PipelineProcess getStartingProcessFromApplicationContext(Object processDomain) throws IllegalAccessException, ClassNotFoundException {
        String processClassName = (String) FieldUtils.readDeclaredField(processDomain, "className", FORCE_ACCESS);
        Class<?> processClass = Class.forName(processClassName);

        PipelineProcess startingProcess = (PipelineProcess) applicationContext.getBean(processClass);

        Preconditions.checkNotNull(startingProcess, "恢复 startingProcess: %s 错误, ", processClassName);
        return startingProcess;
    }

    public <T> T getTaskRequestParam(Object taskDomain, Class<T> klass) {
        try {
            Object taskRequestParam = getTaskRequestParam(taskDomain);
            return klass.cast(taskRequestParam);
        } catch (IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getTaskRequestParam(Object taskDomain) throws IllegalAccessException, ClassNotFoundException {
        String paramObject = (String) FieldUtils.readDeclaredField(taskDomain, "requestParam", FORCE_ACCESS);
        return deserializeFromJsonWithTypeInfo(paramObject);
    }

    private Object deserializeFromJsonWithTypeInfo(String paramObject) throws ClassNotFoundException {
        Map map = JsonUtils.parseObj(paramObject, Map.class);
        String paramClassName = String.valueOf(map.get(CLASS_KEY));
        String paramValue = String.valueOf(map.get(VALUE_KEY));
        Class<?> paramClass = Class.forName(paramClassName);
        return JsonUtils.parseObj(paramValue, paramClass);
    }

    private Object selectPausedProcessBySerial(String serial) throws ReflectiveOperationException {
        Object processExample = processExampleClass.newInstance();
        Object criteria = createCriteria(processExample);
        MethodUtils.invokeMethod(criteria, AND_TASK_SERIAL_EQUAL_TO_METHOD_NAME, serial);
        MethodUtils.invokeMethod(criteria, AND_EXEC_STATUS_IN_METHOD_NAME, new Object[]{PAUSED_EXEC_STATUSES}, new Class[]{List.class});

        Method selectBlobsMethod = MethodUtils.getAccessibleMethod(processMapper.getClass(), SELECT_BY_EXAMPLE_WITH_BLOBS_METHOD_NAME, processExampleClass);
        List list;
        if (selectBlobsMethod != null) {
            list = (List) MethodUtils.invokeMethod(processMapper, SELECT_BY_EXAMPLE_WITH_BLOBS_METHOD_NAME, processExample);
        } else {
            list = (List) MethodUtils.invokeMethod(processMapper, SELECT_BY_EXAMPLE_METHOD_NAME, processExample);
        }
        Preconditions.checkArgument(list.size() == 1, "流水线没有对应的 process, serial=[%s]", serial);
        return list.get(0);
    }

    private Object selectPausedTaskBySerial(String serial) throws ReflectiveOperationException {
        Object taskExample = getTaskExample(serial);

        Object taskDomain = ((List) MethodUtils.invokeMethod(taskMapper, SELECT_BY_EXAMPLE_WITH_BLOBS_METHOD_NAME, taskExample)).get(0);

        Preconditions.checkNotNull(taskDomain);

        return taskDomain;
    }

    protected Object selectPausedTaskOrNull(String serial) {
        try {
            Object taskExample = getTaskExample(serial);
            List tasks = (List) MethodUtils.invokeMethod(taskMapper, SELECT_BY_EXAMPLE_WITH_BLOBS_METHOD_NAME, taskExample);
            if (CollectionUtils.isEmpty(tasks)) {
                return null;
            } else {
                return tasks.get(0);
            }

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getTaskExample(String serial) throws ReflectiveOperationException {
        Object taskExample = taskExampleClass.newInstance();
        Object criteria = createCriteria(taskExample);
        MethodUtils.invokeMethod(criteria, AND_SERIAL_EQUAL_TO_METHOD_NAME, serial);
        MethodUtils.invokeMethod(criteria, AND_EXEC_STATUS_IN_METHOD_NAME, new Object[]{PAUSED_EXEC_STATUSES}, new Class[]{List.class});
        return taskExample;
    }

    private String getName(Object domain) throws IllegalAccessException {
        return (String) FieldUtils.readDeclaredField(domain, NAME_FIELD_NAME, FORCE_ACCESS);
    }

    protected void storePausedPipeline(PipelineTask pipelineTask) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus transaction = txManager.getTransaction(definition);
        try {
            updateTaskExecStatus(pipelineTask, PipelineExecStatus.PAUSED);
            updateProcessExecStatus(pipelineTask.getCurrentProcessId(), PipelineExecStatus.PAUSED);
            txManager.commit(transaction);
        } catch (Exception e) {
            txManager.rollback(transaction);
            throw new RuntimeException(e);
        }
    }

    private String toJsonWitheTypeInfo(Object processParam) {
        Map<String, Object> paramObject = new HashMap<>();
        paramObject.put(CLASS_KEY, processParam.getClass().getName());
        paramObject.put(VALUE_KEY, JsonUtils.toJSONString(processParam));
        return JsonUtils.toJSONString(paramObject);
    }

    private Integer updateTaskExecStatus(PipelineTask pipelineTask, PipelineExecStatus execStatus) {
        try {
            Object taskDomain = taskDomainClass.newInstance();
            setTaskExecStatus(taskDomain, execStatus);
            setDomainUpdatedAt(taskDomain, new Date());

            Object taskExample = taskExampleClass.newInstance();
            Object criteria = createCriteria(taskExample);
            MethodUtils.invokeMethod(criteria, AND_SERIAL_EQUAL_TO_METHOD_NAME, pipelineTask.getSerial());

            return updateTaskByExampleSelective(taskDomain, taskExample);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setDomainUpdatedAt(Object domain, Date date) throws IllegalAccessException {
        FieldUtils.writeDeclaredField(domain, UPDATED_AT_FIELD_NAME, date, FORCE_ACCESS);
    }

    public boolean updateTaskRunning(PipelineTask pipelineTask) {
        try {
            Object taskDomain = taskDomainClass.newInstance();
            setTaskExecStatus(taskDomain, PipelineExecStatus.RUNNING);
            setDomainUpdatedAt(taskDomain, new Date());

            Object taskExample = taskExampleClass.newInstance();
            Object criteria = createCriteria(taskExample);
            MethodUtils.invokeMethod(criteria, AND_SERIAL_EQUAL_TO_METHOD_NAME, pipelineTask.getSerial());
            MethodUtils.invokeMethod(criteria, AND_EXEC_STATUS_IN_METHOD_NAME, new Object[]{PAUSED_EXEC_STATUSES}, new Class[]{List.class});

            Integer updateCount = updateTaskByExampleSelective(taskDomain, taskExample);
            return updateCount == 1;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object createCriteria(Object example) throws ReflectiveOperationException {
        return MethodUtils.invokeMethod(example, CREATE_CRITERIA_METHOD_NAME);
    }

    private Integer updateTaskByExampleSelective(Object taskDomain, Object taskExample) throws ReflectiveOperationException {
        return (Integer) MethodUtils.invokeMethod(taskMapper, UPDATE_BY_EXAMPLE_SELECTIVE_METHOD_NAME, taskDomain, taskExample);
    }

    private void updateProcessExecStatus(Integer id, PipelineExecStatus execStatus) throws ReflectiveOperationException {
        Object processDomain = processDomainClass.newInstance();
        setProcessExecStatus(processDomain, execStatus);
        setProcessId(processDomain, id);
        setDomainUpdatedAt(processDomain, new Date());

        updateProcessByPrimaryKeySelective(processDomain);
    }

    private void setProcessParam(Object processDomain, Object processParam) throws IllegalAccessException {
        FieldUtils.writeDeclaredField(processDomain, PARAM_FIELD_NAME, toJsonWitheTypeInfo(processParam), FORCE_ACCESS);
    }

    private void setProcessId(Object processDomain, Integer id) throws IllegalAccessException {
        FieldUtils.writeDeclaredField(processDomain, ID_FIELD_NAME, id, FORCE_ACCESS);
    }

    public void updateProcessRunning(Integer currentProcessId) throws ReflectiveOperationException {
        updateProcessExecStatus(currentProcessId, PipelineExecStatus.RUNNING);
    }

    private void updateProcessByPrimaryKeySelective(Object processDomain) throws ReflectiveOperationException {
        MethodUtils.invokeMethod(processMapper, UPDATE_BY_PRIMARY_KEY_SELECTIVE_METHOD_NAME, processDomain);
    }

    public Integer updateProcessDoneAndInitNextProcess(String taskSerial, Integer currentProcessId, ProcessResult processResult, Integer nextProcessOrder) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus transaction = txManager.getTransaction(definition);
        try {
            updateProcessExecStatus(currentProcessId, PipelineExecStatus.DONE);
            Integer nextProcessId = insertNextProcess(taskSerial, processResult.getNextProcess(), nextProcessOrder, processResult.getResultParam());
            txManager.commit(transaction);
            return nextProcessId;
        } catch (Exception e) {
            txManager.rollback(transaction);
            throw new RuntimeException(e);
        }
    }

    public void updateProcessDone(Integer processId) {
        try {
            updateProcessExecStatus(processId, PipelineExecStatus.DONE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateTaskDone(PipelineTask pipelineTask) {
        updateTaskExecStatus(pipelineTask, PipelineExecStatus.DONE);

    }

}
