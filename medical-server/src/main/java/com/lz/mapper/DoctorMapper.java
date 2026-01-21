package com.lz.mapper;

import com.github.pagehelper.Page;
import com.lz.dto.DoctorPageQueryDTO;
import com.lz.entity.Admin;
import com.lz.entity.Doctor;
import com.lz.vo.DoctorVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DoctorMapper {

    @Select("select * from doctor where username = #{username}")
    Doctor getByUsername(String username);

    @Select("select * from doctor where id = #{doctorId}")
    Doctor list(Long doctorId);

    List<DoctorVO> pageQuery(DoctorPageQueryDTO doctorPageQueryDTO);

    /**
     * 重置所有医生的每日审核额度
     * @param count 重置后的数值 (比如 3)
     */
    void resetAllMaxDailyAudit(@Param("count") Integer count);

    @Select("select * from doctor where dept_id = #{deptId}")
    List<Doctor> getByDeptId(Long deptId);
}
