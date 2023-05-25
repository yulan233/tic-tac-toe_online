package com.example.zouye_4.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * (User)实体类
 *
 * @author makejava
 * @since 2022-05-26 21:57:57
 */
@Data
@EqualsAndHashCode
@ToString(callSuper = true)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class User implements Serializable {
    private static final long serialVersionUID = -41748599053501702L;
                                                                                                                
                                    
    @TableField(value = "id")
    @TableId(type=IdType.AUTO)
    private Integer id;
                                                                                                                                                                                                    
                                    
    @TableField(value = "username")
    private String username;
                                                                                                                                                                                                    
                                    
    @TableField(value = "password")
    private String password;
                                                                                                                                                                                                    
                                    
    @TableField(value = "total_games")
    private Integer totalGames;
                                                                                                                                                                                                    
                                    
    @TableField(value = "win_games")
    private Integer winGames;
                                                                                                                                                                                                    
                                    
    @TableField(value = "win_rate")
    private float winRate;
}

