package com.beard.terminals.pipeline.mapper;

import com.beard.terminals.pipeline.domain.PipelineProcess;
import com.beard.terminals.pipeline.domain.PipelineProcessExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PipelineProcessMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pipeline_process
     *
     * @mbggenerated
     */
    int countByExample(PipelineProcessExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pipeline_process
     *
     * @mbggenerated
     */
    int deleteByExample(PipelineProcessExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pipeline_process
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pipeline_process
     *
     * @mbggenerated
     */
    int insert(PipelineProcess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pipeline_process
     *
     * @mbggenerated
     */
    int insertSelective(PipelineProcess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pipeline_process
     *
     * @mbggenerated
     */
    List<PipelineProcess> selectByExampleWithBLOBs(PipelineProcessExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pipeline_process
     *
     * @mbggenerated
     */
    List<PipelineProcess> selectByExample(PipelineProcessExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pipeline_process
     *
     * @mbggenerated
     */
    PipelineProcess selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pipeline_process
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") PipelineProcess record, @Param("example") PipelineProcessExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pipeline_process
     *
     * @mbggenerated
     */
    int updateByExampleWithBLOBs(@Param("record") PipelineProcess record, @Param("example") PipelineProcessExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pipeline_process
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") PipelineProcess record, @Param("example") PipelineProcessExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pipeline_process
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(PipelineProcess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pipeline_process
     *
     * @mbggenerated
     */
    int updateByPrimaryKeyWithBLOBs(PipelineProcess record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pipeline_process
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(PipelineProcess record);
}