package com.alkemy.ong.models.response;

import com.alkemy.ong.models.entity.SlideEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrganizationResponseInfo {

    private String name;
    private String image;
    private String address;
    private Integer phone;
    private List<SlideEntity> slides;

}
