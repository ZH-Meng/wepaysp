package com.zbsp.wepaysp.po.dic.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sequence")
public class Sequence
    implements java.io.Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7091180083793963649L;
    private String name;
    private Integer currentValue;
    private Integer increment;

    public Sequence() {
    }

    @Id
    @Column(name = "NAME", unique = true, nullable = false, length = 64)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "CURRENT_VALUE")
    public Integer getCurrentValue() {
        return this.currentValue;
    }

    public void setCurrentValue(Integer currentValue) {
        this.currentValue = currentValue;
    }

    @Column(name = "INCREMENT")
    public Integer getIncrement() {
        return this.increment;
    }

    public void setIncrement(Integer increment) {
        this.increment = increment;
    }

}
