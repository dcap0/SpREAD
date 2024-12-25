package org.ddmac.spreadtest;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.util.Objects;

@Entity
@Table(name="test")
public class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String testString;
    int testInt;
    boolean testBool;


    public TestEntity(){}
    public TestEntity(
            Long id,
            String testString,
            int testInt,
            boolean testBool
    ){
        this.id = id;
        this.testString = testString;
        this.testInt = testInt;
        this.testBool = testBool;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTestString() {
        return testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }

    public int getTestInt() {
        return testInt;
    }

    public void setTestInt(int testInt) {
        this.testInt = testInt;
    }

    public boolean isTestBool() {
        return testBool;
    }

    public void setTestBool(boolean testBool) {
        this.testBool = testBool;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestEntity that = (TestEntity) o;
        return testInt == that.testInt && testBool == that.testBool && Objects.equals(id, that.id) && Objects.equals(testString, that.testString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, testString, testInt, testBool);
    }
}
