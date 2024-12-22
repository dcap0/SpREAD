package org.ddmac.spread;

import jakarta.persistence.*;

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
}
