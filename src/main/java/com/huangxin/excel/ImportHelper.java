package com.huangxin.excel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.huangxin.excel.handle.importHandle.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.unit.DataSize;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * ImportEventListener
 *
 * @author 黄鑫
 */
@Getter
@Slf4j
public class ImportHelper<T>
        extends AnalysisEventListener<T>
        implements ImportStream<T> {
    // 业务名称.默认值：默认导入业务
    private String bizName;
    // 导入校验对应的对象类型
    private Class<T> cls;
    // 表头行数（包含非表头的提示行）
    private Integer headNum;
    // 数据行数（不包括表头）
    private Integer rowNum;
    // 临时文件
    private File tempFile;


    // 处理的数据条数（不包括表头）
    private int total = 0;
    // 处理的表头条数
    private int headCount = 0;
    // 非表头的提示行数
    private int noHeadNum = 1;
    // 错误信息MAP集合
    protected final HashMap<Integer, List<String>> errorMap = new HashMap<>(1024);
    private Integer startRowNum;
    // 默认业务名称
    private static final String defaultBizName = "默认导入业务";
    // 表头集合
    private final List<List<String>> headList = new ArrayList<>();
    // 读取的数据集合
    protected final List<T> tempList = new ArrayList<>(1000);
    // 数据处理器集合
    private final List<DataHandle> dataHandles = new ArrayList<>();
    // 表头处理器集合
    private final List<HeadHandle> headHandles = new ArrayList<>();
    // 用户流式处理器集合
    private final List<StreamHandle<T>> streamHandles = new ArrayList<>();
    //
    private final List<Class<?>> groups = new ArrayList<>();
    // 分批处理的最大批次，默认最大1000
    private int batchCount = 1000;
    // 分批处理函数
    private Consumer<List<T>> consumer = list -> {};



    /**
     * 下载文件
     *
     * @param bizName 业务名称
     * @param fileUrl 文件地址
     * @param limit   限制实体
     */
    public static File downloadFile(String bizName, String fileUrl, Limit limit) {
        log.info("==> {}导入验证 - 开始, param={}", bizName, fileUrl);
        File tempFile;
        try {
            String fileExt = FileNameUtil.extName(fileUrl);
            tempFile = File.createTempFile(IdUtil.fastSimpleUUID(), fileExt);
            tempFile.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException("创建临时文件失败");
        }
        long fileSize = HttpUtil.downloadFile(fileUrl, tempFile);
        log.info("{}导入验证 fileUrl:{}, fileSize:{}", bizName, fileUrl, fileSize);
        if (fileSize > DataSize.ofMegabytes(limit.getFileSize()).toBytes()) {
            throw new RuntimeException(String.format("文件不能大于%sM", limit.getFileSize()));
        }
        return tempFile;
    }

    /**
     * 通过文件地址创建ImportHelper对象
     *
     * @param fileUrl 文件地址
     * @param cls     导入的数据类型
     * @param limit   限制实体
     * @param bizName 业务名称
     */
    public static <T> ImportHelper<T> ofUrl(String fileUrl, Class<T> cls, Limit limit, String bizName) {
        File tempFile = downloadFile(bizName, fileUrl, limit);
        return new ImportHelper<>(tempFile, cls, limit, bizName);
    }

    /**
     * 通过文件创建ImportHelper对象
     *
     * @param file    文件地址
     * @param cls     导入的数据类型
     * @param limit   限制实体
     * @param bizName 业务名称
     */
    public static <T> ImportHelper<T> ofFile(File file, Class<T> cls, Limit limit, String bizName) {
        return new ImportHelper<>(file, cls, limit, bizName);
    }

    private ImportHelper() {
    }

    private ImportHelper(File file, Class<T> cls, Limit limit, String bizName) {
        this.checkParam(file, cls, limit);
        this.tempFile = file;
        this.bizName = bizName;
        this.cls = cls;
        this.headNum = limit.getHeadNum();
        this.rowNum = limit.getRowNum();
        this.initHandles();
    }

    private void checkParam(File file, Class<T> cls, Limit limit) {
        if (ObjectUtil.isEmpty(file)
                || ObjectUtil.isEmpty(cls)
                || ObjectUtil.isEmpty(limit.getHeadNum())
                || ObjectUtil.isEmpty(limit.getRowNum())) {
            throw new RuntimeException("参数有误，请检查");
        }
    }


    /**
     * 加载默认的处理器
     */
    private void initHandles() {
        this.addDataHandle(new DefaultDataHandle());
    }

    /**
     * 添加一行校验表头
     *
     * @param head 一行表头
     */
    public ImportHelper<T> addHead(List<String> head) {
        this.headList.add(head);
        return this;
    }

    /**
     * 添加多行行校验表头
     *
     * @param headList 多行表头
     */
    public ImportHelper<T> addHeadList(List<List<String>> headList) {
        this.headList.addAll(headList);
        return this;
    }

    /**
     * 添加自定义数据处理器
     *
     * @param handles 自定义数据处理器
     */
    public ImportHelper<T> addDataHandle(DataHandle... handles) {
        this.dataHandles.addAll(Arrays.asList(handles));
        return this;
    }

    /**
     * 添加自定义表头处理器
     *
     * @param handles 自定义表头处理器
     */
    public ImportHelper<T> addHeadHandle(HeadHandle... handles) {
        this.headHandles.addAll(Arrays.asList(handles));
        return this;
    }

    public ImportHelper<T> addDefaultHeadHandle() {
        return this.addHeadHandle(new DefaultHeadHandle());
    }

    public ImportHelper<T> addGroups(Class<?>... groups) {
        this.groups.addAll(Arrays.asList(groups));
        return this;
    }

    public ImportHelper<T> notHeadNum(Integer noHeadNum) {
        this.noHeadNum = noHeadNum;
        return this;
    }

    public ExcelReaderBuilder read() {
        return EasyExcel.read(this.tempFile, this.getCls(), this);
    }


    /**
     * 监听异常
     *
     * @param exception 异常
     * @param context   上下文
     * @throws Exception 异常
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        log.error("==> {}导入excel解析失败了，已解析了 {} 行----", bizName, total);
        super.onException(exception, context);
    }

    /**
     * 调用执行解析表头
     *
     * @param headMap 头图
     * @param context 上下文
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        Integer rowIndex = context.readSheetHolder().getRowIndex();
        if (this.noHeadNum > rowIndex) {
            return;
        }
        Integer num = this.headNum - this.noHeadNum;
        List<String> head = null;
        if (ObjectUtil.isNotEmpty(this.headList)) {
            head = this.headList.get(headCount);
        }
        Field[] fields = this.cls.getDeclaredFields();
        for (HeadHandle handle : headHandles) {
            handle.headProcess(fields, head, num, headMap, context);
        }
        headCount++;
    }

    /**
     * 调用行解析
     *
     * @param t               t
     * @param analysisContext 分析上下文
     */
    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        int currentRowNum = analysisContext.readSheetHolder().getRowIndex() + 1;
        if (Objects.isNull(startRowNum)) {
            startRowNum = currentRowNum;
        }
        ++total;
        if (total > this.rowNum) {
            throw new RuntimeException(String.format("文件超过%s行，请分批导入", this.rowNum));
        }
        // 流式处理
        for (StreamHandle<T> handle : streamHandles) {
            if (handle.exec(t)) {
                return;
            }
        }

        // 达到batchCount了，防止数据几万条数据在内存，容易OOM
        if (tempList.size() >= this.batchCount) {
            this.consumer.accept(tempList);
            this.tempList.clear();
        }
        List<String> list = new ArrayList<>();
        for (DataHandle handle : dataHandles) {
            if (!handle.rowProcess(t, currentRowNum, list, groups.toArray(new Class[0]))) {
                return;
            }
        }
        list.addAll(verifyRowData(t));
        if (CollectionUtils.isNotEmpty(list)) {
            errorMap.put(currentRowNum, list);
        }
        this.tempList.add(t);
    }


    /**
     * 验证行数据
     *
     * @param t t
     */
    private List<String> verifyRowData(T t) {
        List<String> msgList = new ArrayList<>();
        Field[] fields = t.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            //属性值
            Object fieldValue = null;
            try {
                fieldValue = field.get(t);
                if (fieldValue instanceof String) {
                    fieldValue = String.valueOf(fieldValue).trim();
                    field.set(t, fieldValue);
                }
            } catch (IllegalAccessException e) {
                log.error("==> 数据解析异常", e);
                throw new RuntimeException("数据解析异常");
            }
            boolean isExcelProperty = field.isAnnotationPresent(ExcelProperty.class);
            if (!isExcelProperty) {
                continue;
            }
            for (DataHandle handle : dataHandles) {
                handle.fieldProcess(t, field, fieldValue, msgList);
            }
        }
        return msgList;
    }

    /**
     * 所有解析成功完成后执行方法
     *
     * @param analysisContext 分析上下文
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        this.consumer.accept(tempList);
        log.info("==> {}导入excel解析完成了，总行数 {} ----", bizName, total);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        delTempFile();
    }

    public void delTempFile() {
        FileUtil.del(this.tempFile);
    }

    @Override
    public ImportHelper<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        streamHandles.add(t -> !predicate.test(t));
        return this;
    }

    @Override
    public ImportHelper<T> peek(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        streamHandles.add(t -> {
            action.accept(t);
            return false;
        });
        return this;
    }

    @Override
    public ImportHelper<T> limit(long maxSize) {
        LongAdder adder = new LongAdder();
        streamHandles.add(t -> {
            if (adder.sum() < maxSize) {
                adder.add(1L);
                return false;
            }
            return true;
        });
        return this;
    }

    @Override
    public ImportHelper<T> skip(long n) {
        LongAdder adder = new LongAdder();
        streamHandles.add(t -> {
            if (adder.sum() >= n) {
                return false;
            }
            adder.add(1L);
            return true;
        });
        return this;
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        streamHandles.add(t -> {
            action.accept(t);
            return false;
        });
    }

    @Override
    public List<T> toList(Integer sheetNo, Integer headNum) {
        sheetNo = ObjectUtil.isNotEmpty(sheetNo) ? sheetNo : 0;
        headNum = ObjectUtil.isNotEmpty(headNum) ? headNum : this.headNum;
        this.read().sheet(sheetNo).headRowNumber(headNum).doRead();
        return new ArrayList<>(this.tempList);
    }

    public List<T> toList(Integer headNum) {
        return toList(null, headNum);
    }

    public List<T> toList() {
        return this.toList(null, null);
    }

    public void page(Consumer<List<T>> consumer) {
        this.page(null, null, null, consumer);
    }

    public void page(Integer batchCount, Consumer<List<T>> consumer) {
        this.page(null, null, batchCount, consumer);
    }

    public void page(Integer headNum, Integer batchCount, Consumer<List<T>> consumer) {
        this.page(null, headNum, batchCount, consumer);
    }

    /**
     * 分批读取, 实际上调用了toList()
     * @param sheetNo  sheet页索引 默认为0
     * @param headNum  表头行数（包含非表头的提示行）
     * @param batchCount 分批处理的最大批次，默认最大1000
     * @param consumer 分批处理函数
     */
    public void page(Integer sheetNo, Integer headNum, Integer batchCount, Consumer<List<T>> consumer) {
        this.batchCount = ObjectUtil.isNotEmpty(batchCount) ? batchCount : this.batchCount;
        this.consumer = ObjectUtil.isNotEmpty(consumer) ? consumer : this.consumer;
        this.toList(sheetNo, headNum);
    }

}
