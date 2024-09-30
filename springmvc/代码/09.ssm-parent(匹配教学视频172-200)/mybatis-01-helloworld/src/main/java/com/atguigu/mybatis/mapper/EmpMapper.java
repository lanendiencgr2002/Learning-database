package com.atguigu.mybatis.mapper;

import com.atguigu.mybatis.bean.Emp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper  //告诉Spring，这是MyBatis操作数据库用的接口; Mapper接口
public interface EmpMapper {
    Emp getEmpById02(Integer id,String tableName);

    //按照id查询
    Emp getEmpById(Integer id);

    //查询所有员工 1. 不用管list，配置返回类型为类
    List<Emp> getAll();

    //添加员工 1. 这里加了返回id功能
    void addEmp(Emp emp);

    //更新员工 1. 传对象的话，配置文件中参数用对象里边的属性（会自动做映射） 2. 还演示了表字段名和属性名不一致的情况
    void updateEmp(Emp emp);

    //删除员工
    void deleteEmpById(Integer id);
}
