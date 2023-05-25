package com.example.zouye_4.entity;

import java.util.Date;
import java.io.Serializable;
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
 * @since 2022-05-26 22:06:34
 */
@Data
@EqualsAndHashCode
@ToString(callSuper = true)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("game_info_two")
public class GameInfoTwo implements Serializable {
    private static final long serialVersionUID = -23628571047114555L;
                                                                                                                
                                    
    @TableField(value = "gid")
    @TableId
    private Integer gid;
                                                                                                                                                                                                    
                                    
    @TableField(value = "first_id")
    private Integer firstId;

    @TableField(value = "first_name")
    private String firstName;

                                    
    @TableField(value = "second_id")
    private Integer secondId;

    @TableField(value = "second_name")
    private String secondName;

    @TableField(value = "winner")
    private String winner;
                                    
    @TableField(value = "time")
    private String time;
                                                                                                                                                                                                    
                                    
    @TableField(value = "step")
    private String step;
                                                                                    
}

