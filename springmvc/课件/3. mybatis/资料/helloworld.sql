CREATE DATABASE `mybatis-example`;

USE `mybatis-example`;

CREATE TABLE `t_emp`(
  id INT AUTO_INCREMENT,
  emp_name CHAR(100),
  age INT,
  emp_salary DOUBLE(10,5),
  PRIMARY KEY(id)
);

INSERT INTO `t_emp`(emp_name,age,emp_salary) VALUES("tom",18,200.33);
INSERT INTO `t_emp`(emp_name,age,emp_salary) VALUES("jerry",19,666.66);
INSERT INTO `t_emp`(emp_name,age,emp_salary) VALUES("andy",20,777.77);