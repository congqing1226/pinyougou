package tbSpecification;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * @author congzi
 * @Description: 规格组合实体
 * @create 2018-05-05
 * @Version 1.0
 */
public class Specification  implements Serializable{

    /**
     * 规格实体
     */
    private  TbSpecification Specification ;

    /**
     * 规格选项 列表
     */

    private List<TbSpecificationOption> specificationOptionList;

    public TbSpecification getSpecification() {
        return Specification;
    }

    public void setSpecification(TbSpecification specification) {
        Specification = specification;
    }

    public List<TbSpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}
