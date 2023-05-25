package com.example.zouye_4.entity;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * (GameInfo)实体类
 *
 * @author makejava
 * @since 2022-06-04 17:36:30
 */
@Data
@EqualsAndHashCode
@ToString(callSuper = true)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("game_info")
public class GameInfo implements Serializable {
    private static final long serialVersionUID = 441013258132338155L;
                                                                                                                

    @TableField(value = "gid")
    @TableId(type=IdType.AUTO)
    private Integer gid;
                                                                                                                                                                                                    
                                    
    @TableField(value = "first_id")
    private Integer firstId;
                                                                                                                                                                                                    
                                    
    @TableField(value = "second_id")
    private Integer secondId;
                                                                                                                                                                                                    
                                    
    @TableField(value = "time")
    private Date time;
                                                                                                                                                                                                    
                                    
    @TableField(value = "winner")
    private String winner;
                                                                                                                                                                                                    
                                    
    @TableField(value = "step")
    private String step;
                                                                                    
}

