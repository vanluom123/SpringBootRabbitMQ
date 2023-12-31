package com.javainuse.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = Employee.class)
public class Employee {

  private String empName;
  private String empId;
  private int salary;

  @Override
  public String toString() {
    return "Employee [empName=" + empName + ", empId=" + empId + ", salary=" + salary + "]";
  }
}
