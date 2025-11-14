package Ziaat.E_library.Utils;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author lgrcis-development team
 * @date Apr 02, 2019
 * @version 1.0.0
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {

    private Boolean status;

    private Integer code;

    private T data;

    private String description;

}
