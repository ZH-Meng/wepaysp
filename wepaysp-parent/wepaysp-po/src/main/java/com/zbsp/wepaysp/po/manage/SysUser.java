package com.zbsp.wepaysp.po.manage;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.zbsp.wepaysp.po.dic.SysCity;
import com.zbsp.wepaysp.po.dic.SysProvince;

@Entity
@Table(name = "sys_user_t")
public class SysUser implements Serializable {

    private static final long serialVersionUID = -6759340112504418331L;
    private String iwoid;
    private String userId;
    private String loginPwd;
    private String userName;
    private Integer gender;
    private Integer age;
    private String department;
    private String position;
    private String lineTel;
    private String email;
    private Integer buildType;
    private Date lastLoginTime;
    private String lastLoginIp;
    private Integer state;
    private Integer dataPermisionType;
    private SysProvince dataPermisionProvince;
    private SysCity dataPermisionCity;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    private String remark;

    public static enum Gender {
        /** 性别:男 */        male(0),
        /** 性别:女 */        female(1);

        private int value;

        public int getValue() {
            return value;
        }

        private Gender(int value) {
            this.value = value;
        }
    }
    
    public static enum BuildType {
        /** 创建类型:内置 */        internal(0),
        /** 创建类型:应用 */        create(1);

        private int value;

        public int getValue() {
            return value;
        }

        private BuildType(int value) {
            this.value = value;
        }
    }
    
    public static enum State {
        /** 状态:正常 */        normal(0),
        /** 状态:冻结 */        frozen(1),
        /** 状态:注销 */        canceled(2);

        private int value;

        public int getValue() {
            return value;
        }

        private State(int value) {
            this.value = value;
        }
    }
    
    public static enum DataPermisionType {
        /** 数据权限范围:无 */        none(0),
        /** 数据权限范围:全国 */     country(1),
        /** 数据权限范围:省 */        province(2),
        /** 数据权限范围:地市 */     city(3);

        private int value;

        public int getValue() {
            return value;
        }

        private DataPermisionType(int value) {
            this.value = value;
        }
    }
    
    public SysUser() {
    }

    public SysUser(String iwoid) {
        this.iwoid = iwoid;
    }
    
    @Id
    @Column(name = "iwoid", unique = true, nullable = false, length = 32)
    public String getIwoid() {
        return this.iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    @Column(name = "user_id", nullable = false, length = 30)
    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "login_pwd", nullable = false, length = 200)
    public String getLoginPwd() {
        return this.loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    @Column(name = "user_name", nullable = false, length = 50)
    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "gender")
    public Integer getGender() {
        return this.gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    @Column(name = "age")
    public Integer getAge() {
        return this.age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Column(name = "department", length = 50)
    public String getDepartment() {
        return this.department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Column(name = "position", length = 50)
    public String getPosition() {
        return this.position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Column(name = "line_tel", length = 50)
    public String getLineTel() {
        return this.lineTel;
    }

    public void setLineTel(String lineTel) {
        this.lineTel = lineTel;
    }

    @Column(name = "email", length = 100)
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "build_type", nullable = false)
    public Integer getBuildType() {
        return this.buildType;
    }

    public void setBuildType(Integer buildType) {
        this.buildType = buildType;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_login_time", length = 29)
    public Date getLastLoginTime() {
        return this.lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
    
    @Column(name="last_login_ip", length=20)
    public String getLastLoginIp() {
        return this.lastLoginIp;
    }
    
    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }
    
    @Column(name = "state", nullable = false)
    public Integer getState() {
        return this.state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
    
    @Column(name = "data_permision_type", nullable = false)
    public Integer getDataPermisionType() {
        return this.dataPermisionType;
    }
    
    public void setDataPermisionType(Integer dataPermisionType) {
        this.dataPermisionType = dataPermisionType;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sys_province_oid")
    public SysProvince getDataPermisionProvince() {
        return dataPermisionProvince;
    }
    
    public void setDataPermisionProvince(SysProvince dataPermisionProvince) {
        this.dataPermisionProvince = dataPermisionProvince;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sys_city_oid")
    public SysCity getDataPermisionCity() {
        return dataPermisionCity;
    }

    public void setDataPermisionCity(SysCity dataPermisionCity) {
        this.dataPermisionCity = dataPermisionCity;
    }

    @Column(name = "creator", nullable = false, length = 32)
    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", nullable = false, length = 29)
    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "modifier", length = 32)
    public String getModifier() {
        return this.modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_time", length = 29)
    public Date getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Column(name = "remark", length = 256)
    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[用户标识=");
        builder.append(userId);
        builder.append(", 用户姓名=");
        builder.append(userName);
        
        builder.append(", 性别=");
        
        if (gender != null) {
            if (gender == Gender.male.getValue()) {
                builder.append("男");
            } else if (gender == Gender.female.getValue()) {
                builder.append("女");
            }
        }
        
        builder.append(", 年龄=");
        if(age!=null && age!=0){
        	builder.append(age);
        }
        builder.append(", 部门=");
        builder.append(department);
        builder.append(", 职务=");
        builder.append(position);
        builder.append(", 联系电话=");
        builder.append(lineTel);
        builder.append(", 邮箱=");
        builder.append(email);
        
        builder.append(", 状态=");
        
        if (state != null) {
            if (state == State.normal.getValue()) {
                builder.append("正常");
            } else if (state == State.frozen.getValue()) {
                builder.append("冻结");
            } else if (state == State.canceled.getValue()) {
                builder.append("注销");
            }
        }
        
        builder.append(", 数据权限范围=");
        
        if (dataPermisionType != null) {
            if (dataPermisionType == DataPermisionType.none.getValue()) {
                builder.append("无");
            } else if (dataPermisionType == DataPermisionType.country.getValue()) {
                builder.append("全国");
            } else if (dataPermisionType == DataPermisionType.province.getValue()) {
                builder.append("省市");
            } else if (dataPermisionType == DataPermisionType.city.getValue()) {
                builder.append("地市");
            }
        }
        
        builder.append(", 备注=");
        if (remark != null) {
        	builder.append(remark);
        }
        builder.append("]");
        return builder.toString();
    }
}
