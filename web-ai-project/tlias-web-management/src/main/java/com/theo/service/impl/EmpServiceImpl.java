package com.theo.service.impl;

import com.github.pagehelper.Page;
import com.theo.mapper.EmpExprMapper;
import com.theo.mapper.EmpMapper;
import com.theo.pojo.*;
import com.theo.service.EmpService;
import com.theo.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmpServiceImpl implements EmpService {
    @Autowired
    private EmpMapper empMapper;

    @Autowired
    private EmpExprMapper empExprMapper;

    @Override
    public PageResult<Emp> page(EmpQueryParam empQueryParam) {
        //1.设置分页参数
        PageHelper.startPage(empQueryParam.getPage(), empQueryParam.getPageSize());

        //2.执行查询
        List<Emp> empList=empMapper.list(empQueryParam);
        Page<Emp> p = (Page<Emp>)empList;

        //3.封装并返回结果
        PageResult<Emp> pageResult = new PageResult<>(p.getTotal(), p.getResult());
        return pageResult;
    }

    @Transactional
    @Override
    public void add(Emp emp) {
        /**
         * 添加员工基本信息
         */
        emp.setCreateTime(LocalDateTime.now());
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.insert(emp);

        /**
         *添加员工经历信息
         */
        List<EmpExpr> exprList = emp.getExprList();
        if(!CollectionUtils.isEmpty(exprList)){
            //遍历集合, 为empId赋值
            exprList.forEach(empExpr -> {
                empExpr.setEmpId(emp.getId());
            });
            empExprMapper.insertBatch(exprList);
        }

    }

    @Transactional(rollbackFor =  {Exception.class})
    @Override
    public void delete(List<Integer> ids) {
        //1.删除员工基本信息
        empMapper.deleteBatch(ids);

        //2.删除员工经历信息
        empExprMapper.deleteByEmpIds(ids);
    }

    @Override
    public Emp getById(Integer id) {
        Emp emp1 = empMapper.getById(id);
        List<EmpExpr> exprList = empExprMapper.getByEmpId(id);
        emp1.setExprList(exprList);;

        return emp1;
    }

    /**
     * 修改员工信息
     * @param emp
     */
    @Transactional(rollbackFor =  {Exception.class})
    @Override
    public void update(Emp emp) {
        emp.setUpdateTime(LocalDateTime.now());
        //1.修改员工基本信息
        empMapper.update(emp);

        //2.修改员工经历信息
        //2.1 删除原有的经历信息
        List<Integer> id = List.of(emp.getId());
        empExprMapper.deleteByEmpIds(id);

        //2.2 添加新的经历信息
        List<EmpExpr> exprList = emp.getExprList();
        if(!CollectionUtils.isEmpty(exprList)) {
            //遍历集合, 为empId赋值
            exprList.forEach(empExpr -> {
                empExpr.setEmpId(emp.getId());
            });
            empExprMapper.insertBatch(exprList);
        }
    }

    /**
     * 获取所有员工信息
     */
    @Override
    public List<Emp> listAll() {
        List<Emp> empList = empMapper.listAll();
        return empList;
    }

    @Override
    public LoginInfo login(Emp emp) {
        LoginInfo login = empMapper.login(emp);
        if(login!=null){
            //生成token
            Map<String,Object> map = new HashMap<>();
            map.put("id", login.getId());
            map.put("username", login.getId());
            String s = JwtUtils.generateToken(map);
            login.setToken(s);
            return login;
        }
        return null;
    }
}
