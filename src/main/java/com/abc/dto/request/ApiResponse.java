package com.abc.dto.request;



import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)//khi huuyeen object sang
public class ApiResponse<T> {//class chua field de chuan hoa api
    //T la do kieu tra ve co the thay doi tuy theo ntung api
   // @Builder.Default
    @Builder.Default
    private int code = 1000;

    private String message;
    private T result;


}