package com.yupi.springbootinit.model.dto.picture;

import com.yupi.springbootinit.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 获取图片接口
 * @author lzy
 */
@Data
public class pictureRequest extends PageRequest implements Serializable {
    String searchText;
}
